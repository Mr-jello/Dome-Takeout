package top.mrjello.service;


import top.mrjello.entity.Dish;
import top.mrjello.vo.DishVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 18:30
 */
public interface UserDishService {

    /**
     * 根据菜品分类id查询菜品及其口味
     * @param dish 菜品分类id
     * @return 菜品及其口味
     */
    List<DishVO> listDishWithFlavorByCategoryId(Dish dish);

}
