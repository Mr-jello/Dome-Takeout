package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 18:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportDTO implements Serializable {
    //日期
    private String orderDate;
    //订单数
    private Integer orderCount;
}
