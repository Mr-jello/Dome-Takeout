package top.mrjello.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mrjello.constant.MessageConstant;
import top.mrjello.constant.StatusConstant;
import top.mrjello.dto.SetmealDTO;
import top.mrjello.dto.SetmealPageQueryDTO;
import top.mrjello.entity.Setmeal;
import top.mrjello.entity.SetmealDish;
import top.mrjello.exception.BusinessException;
import top.mrjello.mapper.SetmealDishMapper;
import top.mrjello.mapper.SetmealMapper;
import top.mrjello.result.PageResult;
import top.mrjello.service.SetmealService;
import top.mrjello.utils.BeanHelper;
import top.mrjello.vo.DishItemVO;
import top.mrjello.vo.SetmealVO;

import java.util.Collections;
import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/8 15:45
 */
@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService{

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 根据名称分页查询套餐列表
     * @param setmealPageQueryDTO 分页查询条件
     * @return PageResult 分页查询结果
     */
    @Override
    public PageResult querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //2.查询套餐列表
        List<SetmealVO> setmealList = setmealMapper.querySetmealByPage(setmealPageQueryDTO.getName(), setmealPageQueryDTO.getCategoryId(), setmealPageQueryDTO.getStatus());
        //3.封装分页结果
        Page<SetmealVO> setmealPage = (Page<SetmealVO>) setmealList;
        return new PageResult(setmealPage.getTotal(), setmealPage.getResult());
    }

    /**
     * 新增套餐: 操作两个表，一个套餐表，一个套餐菜品表
     * @param setmealDTO 套餐信息
     */
    @CacheEvict(cacheNames = "setmeal:cache", key = "#a0.categoryId")
    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        //1.补全套餐信息的实体类
        Setmeal setmeal = BeanHelper.copyProperties(setmealDTO, Setmeal.class);
        assert setmeal != null;
        setmeal.setStatus(StatusConstant.DISABLE);
        //1.1.新增套餐
        setmealMapper.addSetmeal(setmeal);

        //2.新增套餐菜品表
        List<SetmealDish> setmealDishes = getSetmealDishes(setmealDTO, setmeal);
        setmealDishMapper.addSetmealDishes(setmealDishes);

    }

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return SetmealVO 套餐信息
     */
    @Override
    public SetmealVO querySetmealById(Long id) {
        //1.查询套餐基本信息
        Setmeal setmeal = setmealMapper.querySetmealById(id);

        //2.查询套餐菜品信息
        List<SetmealDish> setmealDishesList = setmealDishMapper.querySetmealDishesBySetmealId(id);

        //3.封装套餐VO
        SetmealVO setmealVO = BeanHelper.copyProperties(setmeal, SetmealVO.class);
        assert setmealVO != null;
        //将套餐菜品信息封装到套餐VO中
        setmealVO.setSetmealDishes(setmealDishesList);

        return setmealVO;
    }

    /**
     * 更新套餐
     * @param setmealDTO 套餐信息
     */
    @CacheEvict(cacheNames = "setmeal:cache", allEntries = true)
    @Transactional
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //1.更新套餐基本信息
        Setmeal setmeal = BeanHelper.copyProperties(setmealDTO, Setmeal.class);
        assert setmeal != null;
        setmealMapper.updateSetmeal(setmeal);

        //2.更新套餐菜品信息(先删除，再新增)
        setmealDishMapper.deleteSetmealDishByIds(Collections.singletonList(setmeal.getId()));

        List<SetmealDish> setmealDishes = getSetmealDishes(setmealDTO, setmeal);
        setmealDishMapper.addSetmealDishes(setmealDishes);
    }

    /**
     * 根据id删除套餐
     * @param ids 套餐id
     */
    @CacheEvict(cacheNames = "setmeal:cache", allEntries = true)
    @Transactional
    @Override
    public void deleteSetmealById(List<Long> ids) {

        //2.判断套餐的状态，如果是启用状态，则不能删除
        Long enableCount = setmealMapper.countStatusByIds(ids);
        if (enableCount > 0) {
            throw new BusinessException(MessageConstant.SETMEAL_ON_SALE);
        }

        //3.删除套餐
        setmealMapper.deleteSetmealByIds(ids);

        //4.删除套餐菜品
        setmealDishMapper.deleteSetmealDishByIds(ids);

    }

    /**
     * 启用或禁用套餐
     * @param id 套餐id
     * @param status 套餐状态
     */
    @CacheEvict(cacheNames = "setmeal:cache", allEntries = true)
    @Override
    public void enableOrDisableSetmeal(Long id, Integer status) {
        //1.判断当前setmeal_id下的所有菜品是否都是启用状态
        Long enableCount = setmealDishMapper.countStatusByIds(id);
        if (enableCount > 0) {
            throw new BusinessException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }

        Setmeal setmeal = Setmeal.builder()
                            .id(id)
                            .status(status)
                            .build();
        setmealMapper.updateSetmeal(setmeal);
    }

    /**
     * 查询用户套餐
     * @param setmeal 套餐信息
     * @return List<Setmeal> 套餐列表
     */
    @Cacheable(cacheNames = "setmeal:cache", key = "#a0.categoryId")
    @Override
    public List<Setmeal> queryUserSetmeal(Setmeal setmeal) {

        return setmealMapper.queryUserSetmeal(setmeal);
    }

    /**
     * 查询用户套餐菜品
     * @param id 套餐id
     * @return List<DishItemVO> 套餐菜品列表
     */
    @Cacheable(cacheNames = "setmeal:cache", key = "#a0")
    @Override
    public List<DishItemVO> queryUserSetmealDishBySetmealId(Long id) {

        return setmealDishMapper.queryUserSetmealDishBySetmealId(id);
    }

    /**
     * 获取套餐菜品信息
     * @param setmealDTO 套餐信息
     * @param setmeal 套餐实体类
     * @return List<SetmealDish> 套餐菜品信息
     */
    private static List<SetmealDish> getSetmealDishes(SetmealDTO setmealDTO, Setmeal setmeal) {
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(mealDish -> {
            mealDish.setSetmealId(setmeal.getId());
            mealDish.setDishId(mealDish.getDishId());
            mealDish.setName(mealDish.getName());
            mealDish.setPrice(mealDish.getPrice());
            mealDish.setCopies(mealDish.getCopies());
        });
        return setmealDishes;
    }


}
