package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 15:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoverReportDTO implements Serializable {

    //日期
    private String orderDate;
    //营业额
    private BigDecimal orderMoney;
}
