package top.mrjello.controller.admin;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.dto.OrdersCancelDTO;
import top.mrjello.dto.OrdersConfirmDTO;
import top.mrjello.dto.OrdersPageQueryDTO;
import top.mrjello.dto.OrdersRejectionDTO;
import top.mrjello.result.PageResult;
import top.mrjello.result.Result;
import top.mrjello.service.OrdersManagementService;
import top.mrjello.vo.OrderStatisticsVO;
import top.mrjello.vo.OrderVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/12 18:32
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class OrdersManagementController {
    @Autowired
    private OrdersManagementService ordersManagementService;

    /**
     * 获取各个订单数量的统计信息
     * @return Result<OrderStatisticsVO>
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getOrderStatistics() {
        log.info("获取各个订单数量的统计信息");
        OrderStatisticsVO orderStatisticsVO = ordersManagementService.getOrderStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 条件分页查询订单
     * @param ordersPageQueryDTO 分页查询条件
     * @return Result<PageResult>
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("条件分页查询订单");
        PageResult pageResult = ordersManagementService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 接单
     * @param ordersConfirmDTO 订单id
     */
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单{}", ordersConfirmDTO);
        ordersManagementService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 订单id
     */
    @PutMapping("/rejection")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单{}", ordersRejectionDTO);
        ordersManagementService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 订单id
     */
    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单{}", ordersCancelDTO);
        ordersManagementService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id 订单id
     */
    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable Long id) {
        log.info("派送订单{}", id);
        ordersManagementService.deliveryOrder(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id 订单id
     */
    @PutMapping("complete/{id}")
    public Result completeOrder(@PathVariable Long id) {
        log.info("完成订单{}", id);
        ordersManagementService.completeOrder(id);
        return Result.success();
    }

    /**
     * 查看订单详情
     * @param id 订单id
     * @return Result<OrderVO>
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询订单详情{}", id);
        OrderVO orderVO = ordersManagementService.details(id);
        return Result.success(orderVO);
    }
}
