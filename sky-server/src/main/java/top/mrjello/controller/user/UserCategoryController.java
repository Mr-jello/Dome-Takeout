package top.mrjello.controller.user;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrjello.entity.Category;
import top.mrjello.result.Result;
import top.mrjello.service.CategoryService;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 17:17
 */
@Slf4j
@RestController
@RequestMapping("/user/category")
public class UserCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 用户获取菜品分类列表
     * @param type 分类类型
     * @return Result
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList(Integer type) {
        List<Category> categoryList = categoryService.queryCategoryByType(type);
        return Result.success(categoryList);
    }

}
