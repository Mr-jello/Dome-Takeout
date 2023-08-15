package top.mrjello.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mrjello.constant.MessageConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.dto.OrdersPageQueryDTO;
import top.mrjello.dto.OrdersPaymentDTO;
import top.mrjello.dto.OrdersSubmitDTO;
import top.mrjello.entity.*;
import top.mrjello.exception.BusinessException;
import top.mrjello.mapper.*;
import top.mrjello.result.PageResult;
import top.mrjello.service.OrdersManagementService;
import top.mrjello.service.OrdersService;
import top.mrjello.utils.BeanHelper;
import top.mrjello.utils.WeChatPayUtil;
import top.mrjello.vo.OrderPaymentVO;
import top.mrjello.vo.OrderSubmitVO;
import top.mrjello.vo.OrderVO;
import top.mrjello.websocket.WebSocketServer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author jason@mrjello.top
 * @date 2023/8/11 18:04
 */
@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailsMapper orderDetailsMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private OrdersManagementService ordersManagementService;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO 订单提交DTO
     * @return Result<OrderSubmitVO>
     */
    @Transactional
    @Override
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //前置1：查询当前用户地址信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new BusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //前置2：查询当前用户购物车信息
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryShoppingCartIfExist(shoppingCart);
        if (shoppingCarts.isEmpty()) {
            throw new BusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //1. 保存订单信息
        Orders orders = BeanHelper.copyProperties(ordersSubmitDTO, Orders.class);
        assert orders != null;
        orders.setNumber(BaseContext.getCurrentId() + String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.ORDER_PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.PAY_UN_PAID);
        //设置收货人信息
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        ordersMapper.addNewOrder(orders);
        //2. 保存订单详情信息 详细信息来源于购物车
        List<OrderDetail> orderDetailList = shoppingCarts.stream().map(item -> {
            //赋值订单id
            OrderDetail orderDetail = BeanHelper.copyProperties(item, OrderDetail.class);
            //赋值订单详情id
            assert orderDetail != null;
            orderDetail.setOrderId(orders.getId());
            return orderDetail;
        }).toList();

        orderDetailsMapper.addNewOrderDetails(orderDetailList);

        //3.清空当前用户购物车数据
        if (Objects.equals(orders.getPayStatus(), Orders.PAY_PAID)) {
            //如果是在线支付，清空购物车
            shoppingCartMapper.deleteAllShoppingCart(shoppingCart);
        }

        //4.封装返回数据
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())   //下单时间
                .orderAmount(orders.getAmount())   //订单金额
                .orderNumber(orders.getNumber())   //订单编号
                .build();
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO 订单支付DTO
     * @return Result<OrderPaymentVO>
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getUserById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal("0.01"), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        //模拟微信支付接口返回数据,空json
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new BusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 商户订单号
     */
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.ORDER_TO_BE_CONFIRMED)
                .payStatus(Orders.PAY_PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
        // 发送消息给商家
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", ordersDB.getNumber());
        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }

    /**
     * 用户催单
     * @param id 订单id
     */
    @Override
    public void reminder(Long id) {
        // 根据订单id查询订单
        Orders orders = ordersMapper.queryOrdersById(id);
        // 发送消息给商家
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", orders.getId());
        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }

    /**
     * 查询当前用户历史订单
     * @param page 页码
     * @param pageSize 页大小
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return Result<PageResult>
     */
    @Override
    public PageResult queryUserHistoryOrders(int page, int pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = OrdersPageQueryDTO.builder()
                .userId(BaseContext.getCurrentId())
                .status(status)
                .build();
        List<Orders> ordersList = ordersMapper.queryOrdersByCondition(ordersPageQueryDTO);
        //3.将Orders转化为OrderVO
        List<OrderVO> orderVOList = BeanHelper.copyListProperties(ordersList, OrderVO.class);
        //4.补充订单菜品信息.部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO后，需要补充订单菜品信息
        StringBuilder orderDishesBuilder = new StringBuilder();
        orderVOList.forEach(orderVO -> {
            List<OrderDetail> orderDetailList = orderDetailsMapper.queryOrderDetailsByOrderId(orderVO.getId());
            orderDishesBuilder.setLength(0); // 清空StringBuilder
            //5.设置orderVO中String orderDishes
            orderDetailList.forEach(orderDetail -> {
                orderDishesBuilder.append('<').append(orderDetail.getName()).append('*').append(orderDetail.getNumber()).append('>').append(';');
            });
            // 5. 设置orderVO中String orderDishes
            orderVO.setOrderDishes(orderDishesBuilder.toString());
            //6.设置orderVO中List<OrderDetail> orderDetailList
            orderVO.setOrderDetailList(orderDetailList);
        });
        //7.将orderVOList转化为PageResult
        PageResult pageResult = new PageResult();
        pageResult.setTotal(((Page<?>) ordersList).getTotal());
        pageResult.setRecords(orderVOList);
        return pageResult;
    }

    /**
     * 根据id查询当前用户订单详情
     * @param id 订单id
     * @return OrderVO
     */
    @Override
    public OrderVO getUserOrderDetail(Long id) {
        return ordersManagementService.details(id);
    }

    /**
     * 用户取消订单
     * @param id 订单id
     */
    @Override
    public void userCancelOrder(Long id) {
        // 根据id查询订单
        Orders orders = ordersMapper.queryOrdersById(id);
        // 判断订单状态是否为待付款
        if (orders == null || orders.getStatus() > Orders.ORDER_TO_BE_CONFIRMED) {
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND + "或" + MessageConstant.ORDER_CONFIRMED_NOT_CANCEL);
        }
        // 订单处于待接单状态下取消，需要进行退款
//        if (orders.getStatus().equals(Orders.ORDER_TO_BE_CONFIRMED)) {
//            //调用微信支付退款接口
//            weChatPayUtil.refund(
//                    orders.getNumber(), //商户订单号
//                    orders.getNumber(), //商户退款单号
//                    new BigDecimal("0.01"),//退款金额，单位 元
//                    new BigDecimal("0.01"));//原订单金额
//
//            //支付状态修改为 退款
//            orders.setPayStatus(Orders.PAY_REFUND);
//        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.ORDER_CANCELLED);
        orders.setPayStatus(Orders.PAY_REFUND);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }

    /**
     * 用户再来一单
     * @param id 订单id
     */
    @Override
    public void repetition(Long id) {
        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailsMapper.queryOrderDetailsByOrderId(id);
        // 将订单详情转化为购物车
        List<ShoppingCart> shoppingCartList = BeanHelper.copyListProperties(orderDetailList, ShoppingCart.class);
        // 将shoppingCartList的userId设置为当前用户id,时间设置为当前时间
        shoppingCartList.forEach(shoppingCart -> {
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
        });
        // 将购物车信息插入到购物车表中
        shoppingCartMapper.addShoppingCartBatch(shoppingCartList);
    }


}
