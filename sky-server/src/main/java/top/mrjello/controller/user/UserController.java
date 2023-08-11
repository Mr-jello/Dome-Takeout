package top.mrjello.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrjello.constant.JwtClaimsConstant;
import top.mrjello.dto.UserLoginDTO;
import top.mrjello.entity.User;
import top.mrjello.properties.JwtProperties;
import top.mrjello.result.Result;
import top.mrjello.service.UserService;
import top.mrjello.utils.JwtUtil;
import top.mrjello.vo.UserLoginVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 15:22
 */
@Slf4j
@RestController
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/login")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("userLoginDTO: {}", userLoginDTO);
        //1.调用service层，进行登录
        User user = userService.userLogin(userLoginDTO);
        //2.生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.generateUserToken(jwtProperties.getUserSecretKey(), claims);

        //3.封装结果
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        //4.返回结果
        return Result.success(userLoginVO);
    }

}
