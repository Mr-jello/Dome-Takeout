package top.mrjello.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrjello.constant.MessageConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.dto.ShoppingCartDTO;
import top.mrjello.entity.Dish;
import top.mrjello.entity.Setmeal;
import top.mrjello.entity.ShoppingCart;
import top.mrjello.mapper.DishMapper;
import top.mrjello.mapper.SetmealMapper;
import top.mrjello.mapper.ShoppingCartMapper;
import top.mrjello.service.ShoppingCartService;
import top.mrjello.utils.BeanHelper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/10 17:48
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车DTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //1. 查询判断购物车中是否已经存在该商品及其规格
        ShoppingCart shoppingCart = BeanHelper.copyProperties(shoppingCartDTO, ShoppingCart.class);
        assert shoppingCart != null;
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryShoppingCartIfExist(shoppingCart);

        //2. 如果存在，更新购物车中该商品的数量+1
        if (!shoppingCartList.isEmpty()) {
            ShoppingCart cart = shoppingCartList.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //3. 如果不存在，添加该菜品到购物车中
            Long dishId = shoppingCart.getDishId();
            //3.1 如果是菜品，添加购物车
            if (dishId != null) {
                Dish dish = dishMapper.queryDishById(dishId);
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());

            } else {
                //3.2 如果是套餐，添加购物车
                Setmeal setmeal = setmealMapper.querySetmealById(shoppingCart.getSetmealId());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);

            //3. 如果不存在，添加该商品到购物车中
            shoppingCartMapper.addShoppingCart(shoppingCart);
        }

    }

    /**
     * 查看购物车
     * @return Result
     */
    @Override
    public List<ShoppingCart> queryShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return shoppingCartMapper.queryShoppingCartIfExist(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void deleteAllShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        shoppingCartMapper.deleteAllShoppingCart(shoppingCart);
    }

    @Override
    public void deleteShoppingCartById(ShoppingCartDTO shoppingCartDTO) {
        //1. 查询判断购物车中是否已经存在该商品及其规格
        ShoppingCart shoppingCart = BeanHelper.copyProperties(shoppingCartDTO, ShoppingCart.class);
        assert shoppingCart != null;
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryShoppingCartIfExist(shoppingCart);

        //2. 如果数量 > 1，更新购物车中该商品的数量-1
        if (!shoppingCartList.isEmpty()) {
            ShoppingCart cart = shoppingCartList.get(0);
            if (cart.getNumber() > 1) {
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(cart);
            } else if (cart.getNumber() == 1){
                //3. 如果数量 = 1，删除该商品
                shoppingCartMapper.deleteShoppingCartById(cart);
            } else {
                //4. 如果数量 < 1，抛出异常
                throw new RuntimeException(MessageConstant.SHOPPING_CART_ITEM_NULL);
            }
        }
    }


}
