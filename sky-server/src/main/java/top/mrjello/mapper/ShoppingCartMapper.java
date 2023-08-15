package top.mrjello.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import top.mrjello.entity.ShoppingCart;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/10 17:49
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询判断购物车中是否已经存在该商品及其规格
     * @param shoppingCart 购物车实体类
     * @return List<ShoppingCart>
     */
    List<ShoppingCart> queryShoppingCartIfExist(ShoppingCart shoppingCart);

    /**
     * 更新购物车中该商品的数量+1
     * @param cart 购物车实体类
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);

    /**
     * 添加购物车
     * @param shoppingCart 购物车实体类
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void addShoppingCart(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteAllShoppingCart(ShoppingCart shoppingCart);

    /**
     * 删除购物车中的某一项
     * @param cart 购物车实体类
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteShoppingCartById(ShoppingCart cart);

    /**
     * 批量添加购物车
     * @param shoppingCartList 购物车实体类集合
     */
    void addShoppingCartBatch(List<ShoppingCart> shoppingCartList);
}
