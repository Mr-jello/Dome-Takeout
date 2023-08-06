package top.mrjello.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrjello.constant.StatusConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.dto.CategoryDTO;
import top.mrjello.dto.CategoryPageQueryDTO;
import top.mrjello.entity.Category;
import top.mrjello.mapper.CategoryMapper;
import top.mrjello.result.PageResult;
import top.mrjello.service.CategoryService;
import top.mrjello.utils.BeanHelper;

/**
 * @author jason@mrjello.top
 * @date 2023/8/5 23:52
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据分类名称分页查询分类的service实现类
     * @param categoryPageQueryDTO 分类分页查询DTO
     * @return PageResult 分页结果
     */
    @Override
    public PageResult queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //2.查询分类
        List<Category> categoryList = categoryMapper.queryCategoryByPage(categoryPageQueryDTO.getName(), categoryPageQueryDTO.getType());
        //3.封装分页结果
        Page<Category> categoryPage = (Page<Category>) categoryList;
        return new PageResult(categoryPage.getTotal(), categoryPage.getResult());
    }

    /**
     * 新增菜品分类和套餐分类的service实现类
     * @param categoryDTO 分类DTO
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        //1.不全实体属性
        Category category = BeanHelper.copyProperties(categoryDTO, Category.class);
        assert category != null;
        category.setStatus(StatusConstant.DISABLE);
        //设置时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        //设置创建人和修改人
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        //2.新增分类
        categoryMapper.addCategory(category);
    }

    /**
     * 删除分类的service实现类
     * @param id 分类id
     */
    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteCategory(id);
    }

    /**
     * 启用或者禁用分类的service实现类
     * @param id 分类id
     * @param status 分类状态
     */
    @Override
    public void enableOrDisableCategory(Long id, Integer status) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        //2.调用mapper修改分类状态,后续也可以修改其他属性
        categoryMapper.updateCategory(category);
    }

    /**
     * 根据分类id查询分类的service实现类
     * @param id 分类id
     * @return
     */
    @Override
    public CategoryDTO queryCategoryById(Long id) {
        Category category = categoryMapper.queryCategoryById(id);
        assert category != null;
        return BeanHelper.copyProperties(category, CategoryDTO.class);
    }

    /**
     * 更新分类的service实现类
     * @param categoryDTO 分类DTO
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        //1.不全实体属性
        Category category = BeanHelper.copyProperties(categoryDTO, Category.class);
        assert category != null;
        //设置时间
        category.setUpdateTime(LocalDateTime.now());
        //设置修改人
        category.setUpdateUser(BaseContext.getCurrentId());
        //2.调用mapper修改分类
        categoryMapper.updateCategory(category);
    }
}
