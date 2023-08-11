package top.mrjello.controller.user;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrjello.constant.StatusConstant;
import top.mrjello.entity.Setmeal;
import top.mrjello.result.Result;
import top.mrjello.service.SetmealService;
import top.mrjello.vo.DishItemVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 20:22
 */
@Slf4j
@RestController
@RequestMapping("/user/setmeal")
public class UserSetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("list")
    public Result<List<Setmeal>> ListSetmealDishByCategoryId(Long categoryId) {
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();

        List<Setmeal> setmealList = setmealService.queryUserSetmeal(setmeal);
        return Result.success(setmealList);
    }


    /**
     * 根据套餐id查询包含的菜品
     * @param id 套餐id
     * @return Result
     */
    @GetMapping("dish/{id}")
    public Result<List<DishItemVO>> queryUserSetmealDishBySetmealId(@PathVariable Long id) {
        List<DishItemVO> dishItemVOList = setmealService.queryUserSetmealDishBySetmealId(id);
        return Result.success(dishItemVOList);
    }

}
