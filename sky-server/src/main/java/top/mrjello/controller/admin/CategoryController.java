package top.mrjello.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.dto.CategoryDTO;
import top.mrjello.dto.CategoryPageQueryDTO;
import top.mrjello.entity.Category;
import top.mrjello.result.PageResult;
import top.mrjello.result.Result;
import top.mrjello.service.CategoryService;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/5 23:27
 */

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据分类名称分页查询分类
     * @param categoryPageQueryDTO 分类分页查询DTO
     * @return Result 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult> queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("Query category by page: {}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.queryCategoryByPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return Result 分页结果
     */
    @GetMapping("/list")
    public Result queryCategoryByType(Integer type) {
        log.info("Query category by type: {}", type);
        List<Category> categoryList = categoryService.queryCategoryByType(type);
        return Result.success(categoryList);
    }



    /**
     * 新增菜品分类和套餐分类
     * @param categoryDTO 分类DTO
     * @return Result
     */
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("Add category: {}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 更新分类id删除分类
     * @param id 分类id
     * @return Result
     */
    @DeleteMapping
    public Result deleteCategory(Long id) {
        log.info("Delete category: {}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 启用或者禁用分类
     * @param id 分类id
     * @param status 分类状态
     * @return Result
     */
    @PostMapping("/status/{status}")
    public Result enableOrDisableCategory(Long id, @PathVariable Integer status) {
        log.info("{} No.{} category: ", status == 0 ? "Disable" : "Enable", id);
        categoryService.enableOrDisableCategory(id, status);
        return Result.success();
    }

    /**
     * 根据id查询分类
     * @param id 分类id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<CategoryDTO> queryCategoryById(@PathVariable Long id) {
        log.info("Query category by id: {}", id);
        CategoryDTO categoryDTO = categoryService.queryCategoryById(id);
        return Result.success(categoryDTO);
    }

    /**
     * 更新分类信息
     * @param categoryDTO 分类信息
     * @return Result
     */
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("Update category: {}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }
}
