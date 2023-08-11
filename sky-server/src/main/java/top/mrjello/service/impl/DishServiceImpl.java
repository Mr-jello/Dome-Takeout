package top.mrjello.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mrjello.constant.MessageConstant;
import top.mrjello.constant.StatusConstant;
import top.mrjello.dto.DishDTO;
import top.mrjello.dto.DishPageQueryDTO;
import top.mrjello.entity.Dish;
import top.mrjello.entity.DishFlavor;
import top.mrjello.exception.BusinessException;
import top.mrjello.mapper.DishFlavorMapper;
import top.mrjello.mapper.DishMapper;
import top.mrjello.mapper.SetmealDishMapper;
import top.mrjello.mapper.SetmealMapper;
import top.mrjello.result.PageResult;
import top.mrjello.service.DishService;
import top.mrjello.utils.BeanHelper;
import top.mrjello.vo.DishVO;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 19:08
 */
@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 分页查询菜品列表的service实现
     * @param dishPageQueryDTO 分页查询条件
     * @return PageResult 分页查询结果
     */
    @Override
    public PageResult queryDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //2.查询菜品列表
        List<DishVO> dishList = dishMapper.queryDishByPage(dishPageQueryDTO.getName(), dishPageQueryDTO.getCategoryId(), dishPageQueryDTO.getStatus());
        //3.封装分页结果
        Page<DishVO> dishPage = (Page<DishVO>) dishList;

        return new PageResult(dishPage.getTotal(), dishPage.getResult());
    }

    /**
     * 根据分类id查询菜品列表的service实现
     * @param categoryId 分类id
     * @return List<Dish> 菜品列表
     */
    @Override
    public List<Dish> queryDishByCategoryId(Long categoryId) {
        return dishMapper.queryDishByCategoryId(categoryId);
    }

    /**
     * 新增菜品的service实现
     * 多次调用mapper的新增方法，需要使用事务
     * @param dishDTO 菜品DTO
     */
    @Transactional
    @Override
    public void addDish(DishDTO dishDTO) {
        //1.补全菜品基本信息的实体类
        Dish dish = BeanHelper.copyProperties(dishDTO, Dish.class);
        assert dish != null;
        dish.setStatus(StatusConstant.DISABLE);
        dishMapper.addDish(dish);

        //2.新增菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(dishflavor -> {
            dishflavor.setDishId(dish.getId());
        });
        dishFlavorMapper.addDishFlavorBatch(flavors);

        //3.删除redis中的菜品列表缓存
        cleanCache(dishDTO.getCategoryId().toString());
    }

    /**
     * 根据菜品id删除菜品的service实现
     * 多次调用mapper的删除方法，需要使用事务
     * @param ids 菜品id
     */
    @Transactional
    @Override
    public void deleteDishes(List<Long> ids) {
        //1. 判断菜品的状态，如果是起售状态，则不能删除
        Long enableCount = dishMapper.countStatusByIds(ids);
        if (enableCount > 0) {
            throw new BusinessException(MessageConstant.DISH_ON_SALE);
        }
        //2. 菜品是否关联套餐，如果关联套餐，则不能删除
        List<Long> setmealIds = setmealDishMapper.querySetmealIdsByDishIds(ids);
        if (!setmealIds.isEmpty()) {
            throw new BusinessException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3. 删除菜品
        dishMapper.deleteDishesByIds(ids);
        //4. 删除菜品口味
        dishFlavorMapper.deleteDishFlavorsByDishIds(ids);

        //3.删除redis中的菜品列表缓存
        cleanCache("*");

    }

    /**
     * 根据id查询菜品的service实现
     * @param id 菜品id
     */
    @Override
    public DishVO queryDishById(Long id) {
        //1. 查询菜品基本信息
        Dish dish = dishMapper.queryDishById(id);

        //2. 查询菜品口味 --List<DishFlavor>
        List<DishFlavor> dishFlavorList = dishFlavorMapper.queryDishFlavorsByDishId(id);

        //3. 封装菜品VO
        DishVO dishVO = BeanHelper.copyProperties(dish, DishVO.class);
        assert dishVO != null;
        dishVO.setFlavors(dishFlavorList);

        return dishVO;
    }

    /**
     * 修改菜品的service实现
     * @param dishDTO 菜品DTO
     */
    @Transactional
    @Override
    public void updateDish(DishDTO dishDTO) {
    //1. 修改菜品基本信息
        Dish dish = BeanHelper.copyProperties(dishDTO, Dish.class);
        assert dish != null;
        dishMapper.updateDish(dish);

        //2. 修改菜品口味(先删除，再新增)
        dishFlavorMapper.deleteDishFlavorsByDishIds(Collections.singletonList(dish.getId()));

        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dish.getId());
        });
        dishFlavorMapper.addDishFlavorBatch(flavors);

        //3.删除redis中的菜品列表缓存
        cleanCache("*");

    }

    /**
     * 起售或停售菜品的service实现
     * @param id 菜品id
     * @param status 菜品状态
     */
    @Override
    public void enableOrDisableDish(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateDish(dish);

        //停售菜品，需要停售与其关联的套餐
        if (status == StatusConstant.DISABLE) {
            List<Long> setmealIds = setmealDishMapper.querySetmealIdsByDishIds(Collections.singletonList(id));
            if (!setmealIds.isEmpty()) {
                setmealMapper.updateSetmealsStatus(setmealIds, StatusConstant.DISABLE);
            }
        }

        //3.删除redis中的菜品列表缓存
        cleanCache("*");

    }

    /**
     * 删除redis中的菜品列表缓存
     */
    private void cleanCache(String suffix) {
        log.info("删除redis中的指定缓存的key {}", "dish:cache:" + suffix);
        Set<Object> keys = redisTemplate.keys("dish:cache:" + suffix);
        assert keys != null;
        redisTemplate.delete(keys);
    }


}
