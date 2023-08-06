package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jason@mrjello.top
 * @date 2023/8/5 23:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPageQueryDTO {

    //分类名称
    private String name;

    //类型 1 菜品分类 2 套餐分类
    private Integer type;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;


}
