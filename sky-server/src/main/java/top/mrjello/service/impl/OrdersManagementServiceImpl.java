package top.mrjello.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrjello.constant.MessageConstant;
import top.mrjello.dto.OrdersCancelDTO;
import top.mrjello.dto.OrdersConfirmDTO;
import top.mrjello.dto.OrdersPageQueryDTO;
import top.mrjello.dto.OrdersRejectionDTO;
import top.mrjello.entity.OrderDetail;
import top.mrjello.entity.Orders;
import top.mrjello.exception.BusinessException;
import top.mrjello.mapper.OrderDetailsMapper;
import top.mrjello.mapper.OrdersMapper;
import top.mrjello.result.PageResult;
import top.mrjello.service.OrdersManagementService;
import top.mrjello.utils.BeanHelper;
import top.mrjello.utils.WeChatPayUtil;
import top.mrjello.vo.OrderStatisticsVO;
import top.mrjello.vo.OrderVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/12 18:36
 */
@Slf4j
@Service
public class OrdersManagementServiceImpl implements OrdersManagementService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailsMapper orderDetailsMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 获取各个订单数量的统计信息
     * @return OrderStatisticsVO
     */
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        Integer toBeConfirmedOrders = ordersMapper.CountOrdersByStatus(Orders.ORDER_TO_BE_CONFIRMED);
        Integer confirmed = ordersMapper.CountOrdersByStatus(Orders.ORDER_CONFIRMED);
        Integer deliveryInProgress = ordersMapper.CountOrdersByStatus(Orders.ORDER_DELIVERY_IN_PROGRESS);

        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmedOrders)
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .build();
    }

    /**
     * 条件分页查询订单
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //1.分页设置分页参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //2.查询数据库
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
     * 接单
     * @param ordersConfirmDTO 订单id
     */
    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.ORDER_CONFIRMED)
                .build();
        ordersMapper.update(orders);

    }

    /**
     * 拒单
     * @param ordersRejectionDTO 订单id
     */
    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.queryOrdersById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.ORDER_TO_BE_CONFIRMED)) {
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

//        //支付状态
//        Integer payStatus = ordersDB.getPayStatus();
//        if (Objects.equals(payStatus, Orders.PAY_PAID)) {
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal("0.01"),
//                    new BigDecimal("0.01"));
//            log.info("申请退款：{}", refund);
                //支付状态修改为 退款
//            orders.setPayStatus(Orders.PAY_REFUND);
//        }

        Orders orders = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.ORDER_CANCELLED)
                .payStatus(Orders.PAY_REFUND)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 订单id
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.queryOrdersById(ordersCancelDTO.getId());

//        //支付状态
//        if (Objects.equals(ordersDB.getPayStatus(), Orders.PAY_PAID)) {
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal("0.01"),
//                    new BigDecimal("0.01"));
//            log.info("申请退款：{}", refund);
                //支付状态修改为 退款
//            orders.setPayStatus(Orders.PAY_REFUND);
//        }
        // 取消订单
        Orders orders = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.ORDER_CANCELLED)
                .payStatus(Orders.PAY_REFUND)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 配送订单
     * @param id 订单id
     */
    @Override
    public void deliveryOrder(Long id) {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.queryOrdersById(id);

        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.ORDER_CONFIRMED)) {
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.ORDER_DELIVERY_IN_PROGRESS)
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id 订单id
     */
    @Override
    public void completeOrder(Long id) {
        // 根据id查询订单
        Orders ordersDB = ordersMapper.queryOrdersById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.ORDER_DELIVERY_IN_PROGRESS)) {
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.ORDER_COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        ordersMapper.update(orders);
    }

    /**
     * 根据订单id查询订单
     * @param id 订单id
     * @return 订单
     */
    @Override
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = ordersMapper.queryOrdersById(id);

        // 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailsMapper.queryOrderDetailsByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = BeanHelper.copyProperties(orders, OrderVO.class);
        assert orderVO != null;
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }


}
