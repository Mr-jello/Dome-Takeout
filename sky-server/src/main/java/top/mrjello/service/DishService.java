package top.mrjello.service;

import top.mrjello.dto.DishDTO;
import top.mrjello.dto.DishPageQueryDTO;
import top.mrjello.entity.Dish;
import top.mrjello.result.PageResult;
import top.mrjello.vo.DishVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 19:08
 */
public interface DishService {

    /**
     * 分页查询菜品列表
     * @param dishPageQueryDTO 分页查询条件
     * @return PageResult 分页查询结果
     */
    PageResult queryDishByPage(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据分类id查询菜品列表
     * @param categoryId 分类id
     * @return List<Dish> 菜品列表
     */
    List<Dish> queryDishByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dishDTO 菜品信息
     *
     */
    void addDish(DishDTO dishDTO);

    /**
     * 根据id删除菜品
     * @param ids 菜品id
     */
    void deleteDishes(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return DishVO 菜品信息
     */
    DishVO queryDishById(Long id);

    /**
     * 更新菜品
     * @param dishDTO 菜品信息
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 更新菜品之起售或者停售
     * @param id 菜品id
     * @param status 菜品状态
     */
    void enableOrDisableDish(Long id, Integer status);
}
