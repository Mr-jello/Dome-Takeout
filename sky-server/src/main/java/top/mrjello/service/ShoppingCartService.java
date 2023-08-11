package top.mrjello.service;

import top.mrjello.dto.ShoppingCartDTO;
import top.mrjello.entity.ShoppingCart;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/10 17:48
 */
public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车DTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return Result
     */
    List<ShoppingCart> queryShoppingCart();

    /**
     * 清空购物车
     */
    void deleteAllShoppingCart();

    /**
     * 删除购物车中的某一项
     * @param shoppingCartDTO 购物车DTO
     */
    void deleteShoppingCartById(ShoppingCartDTO shoppingCartDTO);
}
