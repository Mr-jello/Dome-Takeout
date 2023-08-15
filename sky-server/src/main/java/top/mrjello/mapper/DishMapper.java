package top.mrjello.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import top.mrjello.annotation.AutoFill;
import top.mrjello.entity.Dish;
import top.mrjello.enumeration.OperationType;
import top.mrjello.vo.DishVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 19:09
 */
@Mapper
public interface DishMapper {

    /**
     * 动态分页查询菜品列表
     * @param name 菜品名称
     * @return List<Dish> 菜品列表
     */
    List<DishVO> queryDishByPage(String name, Integer categoryId, Integer status);

    /**
     * 根据分类id查询菜品列表
     * @param categoryId 分类id
     * @return List<Dish> 菜品列表
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> queryDishByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * 主键返回给dishflavor表的dish_id
     * @param dish 菜品
     */
    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
        "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addDish(Dish dish);

    /**
     * 根据菜品id删除菜品
     * @param ids 菜品id
     * @return Long
     */
    Long countStatusByIds(List<Long> ids);

    /**
     * 批量根据菜品id删除菜品
     * @param ids 菜品id
     */
    void deleteDishesByIds(List<Long> ids);

    /**
     * 根据菜品id查询菜品
     * @param id 菜品id
     * @return Dish
     */
    @Select("select * from dish where id = #{id}")
    Dish queryDishById(Long id);

    /**
     * 更新菜品基本信息
     * @param dish 菜品
     */
    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    /**
     * 根据菜品状态统计菜品数量
     * @param status 菜品状态
     * @return Integer
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer countDishByStatus(Integer status);
}
