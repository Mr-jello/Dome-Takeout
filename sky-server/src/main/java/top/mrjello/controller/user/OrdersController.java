package top.mrjello.controller.user;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.dto.OrdersPaymentDTO;
import top.mrjello.dto.OrdersSubmitDTO;
import top.mrjello.result.PageResult;
import top.mrjello.result.Result;
import top.mrjello.service.OrdersService;
import top.mrjello.vo.OrderPaymentVO;
import top.mrjello.vo.OrderSubmitVO;
import top.mrjello.vo.OrderVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/11 18:03
 */
@Slf4j
@RestController
@RequestMapping("/user/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO 订单提交DTO
     * @return Result<OrderSubmitVO>
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> orderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单");
        OrderSubmitVO orderSubmitVO = ordersService.orderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO 订单支付DTO
     * @return Result<OrderPaymentVO>
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        //模拟支付成功
        ordersService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("支付成功");
        return Result.success(orderPaymentVO);
    }

    /**
     * 用户催单
     * @param id 订单id
     * @return Result
     */
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id) {
        log.info("用户催单：{}", id);
        ordersService.reminder(id);
        return Result.success();
    }

    /**
     * 查询当前用户历史订单
     * @param page 页码
     * @param pageSize 页大小
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return Result<PageResult>
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> queryUserHistoryOrders(int page, int pageSize, Integer status) {
        log.info("查询当前用户历史订单");
        PageResult pageResult = ordersService.queryUserHistoryOrders(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询订单详情
     * @param id 订单id
     * @return Result<OrderVO>
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getUserOrderDetail(@PathVariable Long id) {
        log.info("查询订单详情{}", id);
        OrderVO orderVO = ordersService.getUserOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     * @param id 订单id
     * @return Result
     */
    @PutMapping("/cancel/{id}")
    public Result userCancelOrder(@PathVariable("id") Long id) {
        log.info("用户取消订单{}", id);
        ordersService.userCancelOrder(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id 订单id
     * @return Result
     */
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id) {
        log.info("再来一单{}", id);
        ordersService.repetition(id);
        return Result.success();
    }
}
