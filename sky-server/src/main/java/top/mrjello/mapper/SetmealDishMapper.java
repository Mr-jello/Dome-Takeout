package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.entity.SetmealDish;
import top.mrjello.vo.DishItemVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 19:09
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品的id查询套餐的id
     * @param ids 菜品id
     * @return Long
     */
    List<Long> querySetmealIdsByDishIds(List<Long> ids);

    /**
     * 添加套餐菜品信息
     * @param setmealDishes 套餐菜品信息
     */
    void addSetmealDishes(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐的id查询套餐菜品信息
     * @param id 套餐id
     * @return List<SetmealDish> 套餐菜品信息
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> querySetmealDishesBySetmealId(Long id);

    /**
     * 根据套餐的id删除套餐菜品信息
     * @param ids 套餐id列表
     */
    void deleteSetmealDishByIds(List<Long> ids);

    /**
     * 根据套餐id查询套餐菜品状态信息
     * @param id 套餐id
     * @return Long
     */
    @Select("select count(*) from setmeal_dish sd left join dish d on sd.dish_id = d.id where sd.setmeal_id = #{id} and d.status = 0")
    Long countStatusByIds(Long id);

    /**
     * 根据套餐id查询套餐菜品信息
     * @param id 套餐id
     * @return List<DishItemVO>
     */
    @Select("select d.name, d.image, sd.copies, d.description from setmeal_dish sd left join dish d on sd.dish_id = d.id where sd.setmeal_id = #{id}")
    List<DishItemVO> queryUserSetmealDishBySetmealId(Long id);
}
