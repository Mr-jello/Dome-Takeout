package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 19:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTop10ReportDTO implements Serializable {

    // 菜品名称列表
    private String nameList;

    // 菜品销量列表
    private String numberList;
}
