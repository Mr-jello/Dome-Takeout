package top.mrjello.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import top.mrjello.annotation.AutoFill;
import top.mrjello.entity.Setmeal;
import top.mrjello.enumeration.OperationType;
import top.mrjello.vo.SetmealVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/8 15:46
 */
@Mapper
public interface SetmealMapper {

    /**
     * 动态分页查询根据名称分页查询套餐列表
     * @param name 套餐名称
     * @return List<SetmealVO> 套餐列表
     */
    List<SetmealVO> querySetmealByPage(String name, Integer categoryId, Integer status);

    /**
     * 新增套餐
     * @param setmeal 套餐
     */
    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into setmeal (category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) " +
    "values (#{categoryId}, #{name}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addSetmeal(Setmeal setmeal);

    /**
     * 根据套餐id查询套餐
     * @param id 套餐id
     * @return Setmeal 套餐信息
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal querySetmealById(Long id);

    /**
     * 更新套餐信息
     * @param setmeal 套餐
     */
    @AutoFill(OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    /**
     * 根据套餐id查询套餐状态
     * @param ids 套餐id
     */
    Long countStatusByIds(List<Long> ids);

    /**
     * 批量根据套餐id删除套餐
     * @param ids 套餐id
     */
    void deleteSetmealByIds(List<Long> ids);

    /**
     * 根据分类id查询套餐列表
     * @param id 分类id
     * @return List<Setmeal> 套餐列表
     */
    @Select("select * from setmeal where category_id = #{id}")
    List<Setmeal> querySetmealByCategoryId(Long id);

    /**
     * 查询用户端套餐列表
     * @param setmeal 套餐信息
     * @return List<Setmeal> 套餐列表
     */
    List<Setmeal> queryUserSetmeal(Setmeal setmeal);

    /**
     * 更新套餐状态
     * @param ids 套餐id
     * @param disable 套餐状态
     */
    void updateSetmealsStatus(List<Long> ids, Integer disable);

    /**
     * 根据套餐状态统计套餐数量
     * @param status 套餐状态
     * @return Integer
     */
    @Select("select count(id) from setmeal where status = #{status}")
    Integer countSetmealByStatus(Integer status);
}
