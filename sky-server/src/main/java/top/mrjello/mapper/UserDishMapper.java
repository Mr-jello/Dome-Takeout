package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import top.mrjello.entity.Dish;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 18:30
 */
@Mapper
public interface UserDishMapper {

    /**
     * 根据菜品分类id查询菜品及其口味
     * @param dish 菜品
     * @return 菜品及其口味
     */
    @Options(useGeneratedKeys = true,keyProperty = "id")
    List<Dish> listDishWithFlavorByCategoryId(Dish dish);

}
