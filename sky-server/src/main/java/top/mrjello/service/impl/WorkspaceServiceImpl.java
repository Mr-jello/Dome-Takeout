package top.mrjello.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrjello.constant.StatusConstant;
import top.mrjello.dto.OrderReportDTO;
import top.mrjello.dto.TurnoverReportDTO;
import top.mrjello.dto.UserReportDTO;
import top.mrjello.entity.Orders;
import top.mrjello.mapper.DishMapper;
import top.mrjello.mapper.OrdersMapper;
import top.mrjello.mapper.SetmealMapper;
import top.mrjello.mapper.UserMapper;
import top.mrjello.service.WorkspaceService;
import top.mrjello.vo.BusinessDataVO;
import top.mrjello.vo.DishOverViewVO;
import top.mrjello.vo.OrderOverViewVO;
import top.mrjello.vo.SetmealOverViewVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {


    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 统计今日营业数据
     * 营业额：当日已完成订单的总金额
     * 有效订单：当日已完成订单的数量
     * 订单完成率：有效订单数 / 总订单数
     * 平均客单价：营业额 / 有效订单数
     * 新增用户：当日新增用户的数量
     * @param begin 开始时间
     * @param end 结束时间
     * @return BusinessDataVO
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        //获取今日总订单数,todayOrdersAllSingleList正常情况下只有一条数据，即当日总订单数
        List<OrderReportDTO> todayAllOrdersSingleList = ordersMapper.queryOrdersStatistics(begin, end, null);
        //获取今日新增用户数,todayNewUsersSingleList正常情况下只有一条数据，即当日新增用户数
        List<UserReportDTO> todayNewUsersSingleList = userMapper.queryUserStatistics(begin, end);
        //获取今日有效订单数,todayOrdersSingleList正常情况下只有一条数据，即当日有效订单数
        List<OrderReportDTO> todayOrdersSingleList = ordersMapper.queryOrdersStatistics(begin, end, Orders.ORDER_COMPLETED);
        //如果当日没有订单，则返回0
        if (todayAllOrdersSingleList.isEmpty()) {
            return BusinessDataVO.builder()
                    .turnover(BigDecimal.ZERO)
                    .validOrderCount(0)
                    .orderCompletionRate(0.0)
                    .unitPrice((double) 0)
                    .newUsers(todayNewUsersSingleList.isEmpty() ? 0 : todayNewUsersSingleList.get(0).getUserCount())
                    .build();
            //如果当日有订单，则进行统计
        } else {
            //如果当日没有有效订单，则返回0
            if (todayOrdersSingleList.isEmpty()) {
                return BusinessDataVO.builder()
                        .turnover(BigDecimal.ZERO)
                        .validOrderCount(0)
                        .orderCompletionRate(0.0)
                        .unitPrice((double) 0)
                        .newUsers(todayNewUsersSingleList.isEmpty() ? 0 : todayNewUsersSingleList.get(0).getUserCount())
                        .build();
            }
            //获取今日营业额,todayTurnoverSingleList正常情况下只有一条数据，即当日营业额
            List<TurnoverReportDTO> todayTurnoverSingleList = ordersMapper.queryTurnoverStatistics(begin, end, Orders.ORDER_COMPLETED);

            //计算订单完成率
            Double orderCompletionRate = (double) todayOrdersSingleList.get(0).getOrderCount() / todayAllOrdersSingleList.get(0).getOrderCount();
            //计算平均客单价
            Double unitPrice = todayTurnoverSingleList.get(0).getOrderMoney().doubleValue() / todayOrdersSingleList.get(0).getOrderCount();

            //有有效订单，既有营业额。有有效订单，既有平均客单价。不用判断是否为空
            return BusinessDataVO.builder()
                    .turnover(todayTurnoverSingleList.get(0).getOrderMoney())
                    .validOrderCount(todayOrdersSingleList.get(0).getOrderCount())
                    .orderCompletionRate(orderCompletionRate)
                    .unitPrice(unitPrice)
                    .newUsers(todayNewUsersSingleList.isEmpty() ? 0 : todayNewUsersSingleList.get(0).getUserCount())
                    .build();
        }
    }


    /**
     * 统计今日订单管理数据
     * private Integer waitingOrders;
     * private Integer deliveredOrders;
     * private Integer completedOrders;
     * private Integer cancelledOrders;
     * private Integer allOrders;
     * @return OrderOverViewVO
     */
    public OrderOverViewVO getOrderOverView() {
        //获得当天的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        //获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        //待接单数量
        List<OrderReportDTO> todayWaitingOrders = ordersMapper.queryOrdersStatistics(begin, end, Orders.ORDER_TO_BE_CONFIRMED);
        //待配送数量
        List<OrderReportDTO> todayDeliveredOrders = ordersMapper.queryOrdersStatistics(begin, end, Orders.ORDER_CONFIRMED);
        //已完成数量
        List<OrderReportDTO> todayCompletedOrders = ordersMapper.queryOrdersStatistics(begin, end, Orders.ORDER_COMPLETED);
        //已取消数量
        List<OrderReportDTO> todayCancelledOrders = ordersMapper.queryOrdersStatistics(begin, end, Orders.ORDER_CANCELLED);
        //总订单数量
        List<OrderReportDTO> todayAllOrders = ordersMapper.queryOrdersStatistics(begin, end, null);

        return OrderOverViewVO.builder()
                .waitingOrders(todayWaitingOrders.isEmpty() ? 0 : todayWaitingOrders.get(0).getOrderCount())
                .deliveredOrders(todayDeliveredOrders.isEmpty() ? 0 : todayDeliveredOrders.get(0).getOrderCount())
                .completedOrders(todayCompletedOrders.isEmpty() ? 0 : todayCompletedOrders.get(0).getOrderCount())
                .cancelledOrders(todayCancelledOrders.isEmpty() ? 0 : todayCancelledOrders.get(0).getOrderCount())
                .allOrders(todayAllOrders.isEmpty() ? 0 : todayAllOrders.get(0).getOrderCount())
                .build();
    }


    /**
     * private Integer sold;
     * private Integer discontinued;
     * 查询菜品总览
     * @return DishOverViewVO
     */
    public DishOverViewVO getDishOverView() {
        //查询起售中菜品的数量
        Integer enableDishes = dishMapper.countDishByStatus(StatusConstant.ENABLE);
        //查询已下架数量
        Integer disableDishes = dishMapper.countDishByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .sold(enableDishes)
                .discontinued(disableDishes)
                .build();
    }

    /**
     * 查询套餐总览
     * @return SetmealOverViewVO
     */
    public SetmealOverViewVO getSetmealOverView() {
        //查询起售中套餐的数量
        Integer enableSetmeals = setmealMapper.countSetmealByStatus(StatusConstant.ENABLE);
        //查询已下架数量
        Integer disableSetmeals = setmealMapper.countSetmealByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(enableSetmeals)
                .discontinued(disableSetmeals)
                .build();
    }
}
