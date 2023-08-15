package top.mrjello.vo;


import lombok.*;
import top.mrjello.entity.OrderDetail;
import top.mrjello.entity.Orders;

import java.io.Serializable;
import java.util.List;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单菜品信息
    private String orderDishes;

    //订单详情
    private List<OrderDetail> orderDetailList;

}
