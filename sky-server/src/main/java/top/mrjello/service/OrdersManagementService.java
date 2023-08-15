package top.mrjello.service;

import top.mrjello.dto.OrdersCancelDTO;
import top.mrjello.dto.OrdersConfirmDTO;
import top.mrjello.dto.OrdersPageQueryDTO;
import top.mrjello.dto.OrdersRejectionDTO;
import top.mrjello.result.PageResult;
import top.mrjello.vo.OrderStatisticsVO;
import top.mrjello.vo.OrderVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/12 18:36
 */
public interface OrdersManagementService {

    /**
     * 获取各个订单数量的统计信息
     * @return OrderStatisticsVO
     */
    OrderStatisticsVO getOrderStatistics();

    /**
     * 条件分页查询订单
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 接单
     * @param ordersConfirmDTO 订单id
     */
    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO 订单id
     */
    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO 订单id
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id 订单id
     */
    void deliveryOrder(Long id);

    /**
     * 完成订单
     * @param id 订单id
     */
    void completeOrder(Long id);

    /**
     * 查看订单详情
     * @param id 订单id
     * @return OrderVO
     */
    OrderVO details(Long id);
}
