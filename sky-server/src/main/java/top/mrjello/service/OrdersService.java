package top.mrjello.service;

import top.mrjello.dto.OrdersPaymentDTO;
import top.mrjello.dto.OrdersSubmitDTO;
import top.mrjello.result.PageResult;
import top.mrjello.vo.OrderPaymentVO;
import top.mrjello.vo.OrderSubmitVO;
import top.mrjello.vo.OrderVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/11 18:04
 */
public interface OrdersService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO 订单提交DTO
     * @return Result<OrderSubmitVO>
     */
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO 订单支付DTO
     * @return Result<OrderPaymentVO>
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 商户订单号
     */
    void paySuccess(String outTradeNo);

    /**
     * 用户催单
     * @param id 订单id
     */
    void reminder(Long id);

    /**
     * 查询用户历史订单
     * @param page 页码
     * @param pageSize 页大小
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return Result<PageResult>
     */
    PageResult queryUserHistoryOrders(int page, int pageSize, Integer status);

    /**
     * 查询用户订单详情
     * @param id 订单id
     * @return Result<OrderVO>
     */
    OrderVO getUserOrderDetail(Long id);

    /**
     * 用户取消订单
     * @param id 订单id
     */
    void userCancelOrder(Long id);

    /**
     * 用户再来一单
     * @param id 订单id
     */
    void repetition(Long id);
}
