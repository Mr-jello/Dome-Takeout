package top.mrjello.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.dto.DishDTO;
import top.mrjello.dto.DishPageQueryDTO;
import top.mrjello.entity.Dish;
import top.mrjello.result.PageResult;
import top.mrjello.result.Result;
import top.mrjello.service.DishService;
import top.mrjello.vo.DishVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 19:06
 */
@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;


    /**
     * 根据名称分页查询菜品列表
     * @param dishPageQueryDTO 分页查询条件
     * @return Result<PageResult> 分页查询结果
     */
    @GetMapping("/page")
    public Result<PageResult> queryDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        log.info("Dish Page Query: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.queryDishByPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据分类id查询菜品列表
     * @param categoryId 分类id
     * @return Result<List<Dish>> 菜品列表
     */
    @GetMapping("/list")
    public Result queryDishByCategoryId(Long categoryId) {
        log.info("Dish List Query By categoryId: {}", categoryId);
        List<Dish> dishList = dishService.queryDishByCategoryId(categoryId);
        return Result.success(dishList);
    }

    /**
     * 新增菜品: 操作两个表，一个菜品表，一个菜品口味表
     * @param dishDTO 菜品信息
     * @return Result
     */
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishDTO) {
        //1.保存菜品基本信息
        log.info("Add dish: {}", dishDTO);
        dishService.addDish(dishDTO);
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     * @param id 菜品id
     */
    @GetMapping("/{id}")
    public Result<DishVO> queryDishById(@PathVariable Long id) {
        log.info("Query dish by id: {}", id);
        DishVO dishVO = dishService.queryDishById(id);
        return Result.success(dishVO);
    }


    /**
     * 更新菜品信息
     * @param dishDTO 菜品信息
     */
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("Update dish: {}", dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }


    /**
     * 根据id删除菜品
     * 多个单个删除共用一个接口
     */
    @DeleteMapping
    public Result deleteDishes(@RequestParam List<Long> ids) {
        log.info("Delete dishes: {}", ids);
        dishService.deleteDishes(ids);
        return Result.success();
    }

    /**
     * 更新菜品的起售，停售
     * @param id 菜品id
     * @param status 菜品状态
     * @return Result 结果
     */
    @PostMapping("/status/{status}")
    public Result enableOrDisableDish(Long id, @PathVariable Integer status) {
        log.info("{} No.{} dish: ", status == 0 ? "Disable" : "Enable", id);
        dishService.enableOrDisableDish(id, status);
        return Result.success();
    }

}
