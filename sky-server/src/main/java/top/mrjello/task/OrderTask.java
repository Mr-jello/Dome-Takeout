package top.mrjello.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.mrjello.entity.Orders;
import top.mrjello.mapper.OrdersMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/12 17:42
 */
@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 定时关闭未支付订单
     * 每隔30秒执行一次
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void cancelOrderTask() {
        //1.查询所有超时订单（待支付，15分钟之前下单）的数据
        LocalDateTime fifteenMinutesBefore = LocalDateTime.now().minusMinutes(15);
        List<Orders> cancelLists = ordersMapper.queryOrdersByStatusAndOrderTime(Orders.ORDER_PENDING_PAYMENT, fifteenMinutesBefore);

        //2.修改订单状态为取消
        if (!cancelLists.isEmpty()) {
            cancelLists.forEach(orders -> {
                log.info("取消超时的订单 {}" , orders.getId());
                //修改订单状态为取消
                orders.setStatus(Orders.ORDER_CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("超时未支付，系统自动取消");
                ordersMapper.update(orders);
            });
        }
    }

    /**
     * 每天凌晨1点执行完成的任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateCompleteOrderTask() {
        //1.查询所有已完成订单（派送中，2小时之前下单）的数据
        LocalDateTime twoHoursBefore = LocalDateTime.now().minusHours(2);
        List<Orders> completeLists = ordersMapper.queryOrdersByStatusAndOrderTime(Orders.ORDER_DELIVERY_IN_PROGRESS, twoHoursBefore);
        //修改订单状态为完成
        if (!completeLists.isEmpty()) {
            completeLists.forEach(orders -> {
                log.info("打样后完成所有订单 {}" , orders.getId());
                //修改订单状态为完成
                orders.setStatus(Orders.ORDER_COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                ordersMapper.update(orders);
            });
        }
    }


}
