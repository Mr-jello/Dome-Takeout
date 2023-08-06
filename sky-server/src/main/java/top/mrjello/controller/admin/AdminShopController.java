package top.mrjello.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.mrjello.result.Result;

/**
 * @author jason@mrjello.top
 * @date 2023/8/5 22:10
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class AdminShopController {

    public static final String SHOP_STATUS_KEY = "Shop_Status";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 设置营业状态
     * @param status 0-休息中 1-营业中
     * @return Result
     */
    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("Shop Status: {}", status == 0 ? "休息中" : "营业中");
        redisTemplate.opsForValue().set(SHOP_STATUS_KEY, status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return Result
     */
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        assert status != null;
        log.info("Shop Status: {}", status == 0 ? "休息中" : "营业中");
        return Result.success(status);
    }

}
