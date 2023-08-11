package top.mrjello.service;

import top.mrjello.dto.SetmealDTO;
import top.mrjello.dto.SetmealPageQueryDTO;
import top.mrjello.entity.Setmeal;
import top.mrjello.result.PageResult;
import top.mrjello.vo.DishItemVO;
import top.mrjello.vo.SetmealVO;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/8 15:44
 */
public interface SetmealService {

    /**
     * 根据名称分页查询套餐列表
     * @param setmealPageQueryDTO 分页查询条件
     * @return PageResult 分页查询结果
     */
    PageResult querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐: 操作两个表，一个套餐表，一个套餐菜品表
     * @param setmealDTO 套餐信息
     */
    void addSetmeal(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return SetmealVO 套餐信息
     */
    SetmealVO querySetmealById(Long id);

    /**
     * 更新套餐信息
     * @param setmealDTO 套餐信息
     */
    void updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 根据id删除套餐
     * @param ids 套餐id
     */
    void deleteSetmealById(List<Long> ids);

    /**
     * 启用或禁用套餐
     * @param id 套餐id
     * @param status 套餐状态
     */
    void enableOrDisableSetmeal(Long id, Integer status);

    /**
     * 查询用户端套餐列表
     * @param setmeal 套餐信息
     * @return List<Setmeal> 套餐列表
     */
    List<Setmeal> queryUserSetmeal(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品
     * @param id 套餐id
     * @return List<DishItemVO> 菜品列表
     */
    List<DishItemVO> queryUserSetmealDishBySetmealId(Long id);
}
