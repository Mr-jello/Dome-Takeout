package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.entity.DishFlavor;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 21:15
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量新增菜品口味
     * @param flavors 口味列表
     */
    void addDishFlavorBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除菜品口味
     * @param ids 菜品id列表
     */
    void deleteDishFlavorsByDishIds(List<Long> ids);

    /**
     * 根据菜品id查询菜品口味
     * @param id 菜品id
     * @return List<DishFlavor> 菜品口味列表
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> queryDishFlavorsByDishId(Long id);
}
