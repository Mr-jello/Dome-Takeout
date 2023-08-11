package top.mrjello.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.annotation.AutoFill;
import top.mrjello.entity.Category;
import top.mrjello.enumeration.OperationType;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 0:02
 */
@Mapper
public interface CategoryMapper {


    /**
     * 根据分类名称分页查询分类
     * @param name 分类名称
     * @param type 分类类型
     * @return List<Category>
     */
    List<Category> queryCategoryByPage(String name, Integer type);

    /**
     * 新增菜品分类和套餐分类
     * @param category 分类
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
             "values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addCategory(Category category);


    /**
     * 根据分类id删除分类
     * @param id 分类id
     */
    @Delete("delete from category where id = #{id}")
    void deleteCategory(Long id);

    /**
     * 根据分类id查询分类
     * @param id 分类id
     * @return Category
     */
    @Select("select * from category where id = #{id}")
    Category queryCategoryById(Long id);

    /**
     * 更新分类信息和启用或者禁用分类
     * @param category 分类
     */
    @AutoFill(OperationType.UPDATE)
    void updateCategory(Category category);

    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return List<Category>
     */
    List<Category> queryCategoryByType(Integer type);
}
