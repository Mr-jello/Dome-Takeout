package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.entity.OrderDetail;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/11 18:14
 */
@Mapper
public interface OrderDetailsMapper {

    /**
     * 批量新增订单详情
     * @param orderDetailList 订单详情实体类
     */
    void addNewOrderDetails(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单详情
     * @param id 订单id
     * @return 订单详情集合
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> queryOrderDetailsByOrderId(Long id);
}
