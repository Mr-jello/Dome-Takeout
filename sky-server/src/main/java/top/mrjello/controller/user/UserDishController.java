package top.mrjello.controller.user;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrjello.constant.StatusConstant;
import top.mrjello.entity.Dish;
import top.mrjello.result.Result;
import top.mrjello.service.UserDishService;
import top.mrjello.vo.DishVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 18:14
 */
@Slf4j
@RestController
@RequestMapping("/user/dish")
public class UserDishController {

    @Autowired
    private UserDishService userDishService;

    /**
     * 根据菜品分类id查询菜品及其口味
     * @param categoryId 菜品分类id
     * @return 菜品及其口味
     */
    @GetMapping("list")
    public Result<List<DishVO>> listDishWithFlavorByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
         List<DishVO> list = userDishService.listDishWithFlavorByCategoryId(dish);
        return Result.success(list);
    }

}
