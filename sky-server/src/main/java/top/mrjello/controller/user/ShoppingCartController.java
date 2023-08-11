package top.mrjello.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.context.BaseContext;
import top.mrjello.dto.ShoppingCartDTO;
import top.mrjello.entity.ShoppingCart;
import top.mrjello.result.Result;
import top.mrjello.service.ShoppingCartService;

import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/10 17:43
 */
@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车DTO
     * @return Result
     */
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车数据：{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return Result
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> queryShoppingCart() {
        log.info("{}查询购物车数据" , BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.queryShoppingCart();
        return Result.success(shoppingCartList);
    }

    /**
     * 清空购物车
     * @return Result
     */
    @DeleteMapping("/clean")
    public Result deleteAllShoppingCart() {
        log.info("清空购物车数据" );
        shoppingCartService.deleteAllShoppingCart();
        return Result.success();
    }

    /**
     * 删除购物车中的某一项
     * @param shoppingCartDTO 购物车DTO
     * @return Result
     */
    @PostMapping("sub")
    public Result deleteShoppingCartById(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车数据：{}", shoppingCartDTO);
        shoppingCartService.deleteShoppingCartById(shoppingCartDTO);
        return Result.success();
    }
}
