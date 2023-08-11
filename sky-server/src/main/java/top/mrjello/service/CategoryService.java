package top.mrjello.service;

import top.mrjello.dto.CategoryDTO;
import top.mrjello.dto.CategoryPageQueryDTO;
import top.mrjello.entity.Category;
import top.mrjello.result.PageResult;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/5 23:48
 */
public interface CategoryService {
    /**
     * 根据分类名称分页查询分类
     * @param categoryPageQueryDTO 分类分页查询DTO
     * @return PageResult 分页结果
     */
    PageResult queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增菜品分类和套餐分类
     * @param categoryDTO 分类DTO
     *
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 更新分类id删除分类
     * @param id 分类id
     *
     */
    void deleteCategory(Long id);

    /**
     * 启用或者禁用分类
     * @param id 分类id
     * @param status 分类状态
     */
    void enableOrDisableCategory(Long id, Integer status);

    /**
     * 根据分类id查询分类
     * @param id 分类id
     * @return CategoryDTO
     */
    CategoryDTO queryCategoryById(Long id);

    /**
     * 更新分类
     * @param categoryDTO 分类DTO
     */
    void updateCategory(CategoryDTO categoryDTO);


    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return List<CategoryDTO>
     */
    List<Category> queryCategoryByType(Integer type);
}
