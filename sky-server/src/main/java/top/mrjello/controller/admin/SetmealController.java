package top.mrjello.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.dto.SetmealDTO;
import top.mrjello.dto.SetmealPageQueryDTO;
import top.mrjello.result.PageResult;
import top.mrjello.result.Result;
import top.mrjello.service.SetmealService;
import top.mrjello.vo.SetmealVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/8 15:42
 */
@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据名称分页查询套餐列表
     * @param setmealPageQueryDTO 分页查询条件
     * @return Result<PageResult> 分页查询结果
     */
    @GetMapping("/page")
    public Result<PageResult> querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("Setmeal Page Query: {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.querySetmealByPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增套餐: 操作两个表，一个套餐表，一个套餐菜品表
     * @param setmealDTO 套餐信息
     * @return Result
     */
    @PostMapping
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("Add setmeal: {}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result querySetmealById(@PathVariable Long id) {
        log.info("Query setmeal by id: {}", id);
        SetmealVO setmealVO = setmealService.querySetmealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 更新套餐信息
     * @param setmealDTO 套餐信息
     * @return Result
     */
    @PutMapping
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("Update setmeal: {}", setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id删除套餐
     * @param ids 套餐id列表
     * @return Result
     */
    @DeleteMapping
    public Result deleteSetmealById(@RequestParam List<Long> ids) {
        log.info("Delete setmeal by id: {}", ids);
        setmealService.deleteSetmealById(ids);
        return Result.success();
    }

    /**
     * 更新套餐起售状态
     * @param id 套餐id
     * @param status 套餐状态
     * @return Result
     */
    @PostMapping("/status/{status}")
    public Result enableOrDisableSetmeal(Long id, @PathVariable Integer status) {
        log.info("{} No.{} setmeal: ", status == 0 ? "Disable" : "Enable", id);
        setmealService.enableOrDisableSetmeal(id, status);
        return Result.success();
    }




}
