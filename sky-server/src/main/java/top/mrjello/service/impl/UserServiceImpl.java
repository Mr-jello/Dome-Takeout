package top.mrjello.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.mrjello.constant.MessageConstant;
import top.mrjello.dto.UserLoginDTO;
import top.mrjello.entity.User;
import top.mrjello.exception.BusinessException;
import top.mrjello.mapper.UserMapper;
import top.mrjello.properties.WeChatProperties;
import top.mrjello.service.UserService;
import top.mrjello.utils.HttpClientUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 15:26
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String USER_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String GRANT_TYPE = "authorization_code";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param userLoginDTO 用户登录信息
     * @return 用户信息
     */
    @Override
    public User userLogin(UserLoginDTO userLoginDTO) {
        //1.调用微信接口，登录凭证校验：appID + appSecret + code
        // params: appid, secret, js_code, grant_type
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("appid", weChatProperties.getAppid());
        paraMap.put("secret", weChatProperties.getSecret());
        paraMap.put("js_code", userLoginDTO.getCode());
        paraMap.put("grant_type", GRANT_TYPE);
        //调用微信接口
        String result = HttpClientUtil.doGet(USER_LOGIN_URL, paraMap);

        log.info("User login result: {}", result);
        if (!StringUtils.hasLength(result)) {
            throw new BusinessException(MessageConstant.LOGIN_FAILED);
        }
        //解析返回结果
        JSONObject jsonResult = JSON.parseObject(result);
        //微信用户唯一标识
        String openid = jsonResult.getString("openid");
        if (!StringUtils.hasLength(openid)) {
            throw new BusinessException(MessageConstant.LOGIN_FAILED);
        }

        //2.首次登录，自动注册用户
        User user = userMapper.queryUserByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.addNewUser(user);
        }
        //3.返回session_key + openid等
        return user;
    }
}
