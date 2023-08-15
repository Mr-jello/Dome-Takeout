package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import top.mrjello.dto.OrderReportDTO;
import top.mrjello.dto.OrdersPageQueryDTO;
import top.mrjello.dto.TurnoverReportDTO;
import top.mrjello.entity.Orders;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/11 18:05
 */
@Mapper
public interface OrdersMapper {

    /**
     * 新增订单
     * @param orders 订单实体
     */
    void addNewOrder(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders 订单实体
     */
    void update(Orders orders);

    /**
     * 根据订单状态和下单时间查询订单
     * @param status 订单状态
     * @param fifteenMinutesBefore 15分钟前的下单时间
     * @return 订单集合
     */
    @Select("select * from orders where status = #{status} and orders.order_time < #{fifteenMinutesBefore}")
    List<Orders> queryOrdersByStatusAndOrderTime(Integer status, LocalDateTime fifteenMinutesBefore);

    /**
     * 根据订单状态查询订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer CountOrdersByStatus(Integer status);

    /**
     * 根据订单id查询订单
     * @param id 订单id
     * @return 订单实体
     */
    @Select("select * from orders where id = #{id}")
    Orders queryOrdersById(Long id);

    /**
     * 条件分页查询订单
     * @param ordersPageQueryDTO 条件分页查询DTO
     * @return 订单集合
     */
    List<Orders> queryOrdersByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单状态和指定时间段查询订单营业额
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param status 订单状态
     * @return 订单集合
     */
    List<TurnoverReportDTO> queryTurnoverStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer status);

    /**
     * 根据订单状态和指定时间段查询订单数量
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param status 订单状态
     * @return 订单集合
     */
    List<OrderReportDTO> queryOrdersStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer status);

}
