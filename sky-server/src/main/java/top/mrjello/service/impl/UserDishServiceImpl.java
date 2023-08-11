package top.mrjello.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.mrjello.entity.Dish;
import top.mrjello.entity.DishFlavor;
import top.mrjello.mapper.DishFlavorMapper;
import top.mrjello.mapper.UserDishMapper;
import top.mrjello.service.UserDishService;
import top.mrjello.vo.DishVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 18:30
 */
@Slf4j
@Service
public class UserDishServiceImpl implements UserDishService{

    @Autowired
    private UserDishMapper userDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;



    /**
         * 根据菜品分类id查询菜品及其口味
     * @param dish 菜品分类id
     * @return 菜品及其口味
     */
    @Override
    public List<DishVO> listDishWithFlavorByCategoryId(Dish dish) {
        String redisDishKey = "dish:cache:" + dish.getCategoryId();

        //1.先查redis缓存，如果有直接返回
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(redisDishKey);
        if (!CollectionUtils.isEmpty(dishVOList)) {
            log.info("从redis缓存中查询到了菜品数据");
            return dishVOList;
        }

        //2.缓存没有，查询数据库
        List<Dish> dishList = userDishMapper.listDishWithFlavorByCategoryId(dish);
        dishVOList = new ArrayList<>();
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.queryDishFlavorsByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        //3.将查询结果存入redis缓存
        redisTemplate.opsForValue().set(redisDishKey, dishVOList);
        log.info("将菜品数据存入redis缓存");
        return dishVOList;
    }


}
