package top.mrjello.service;

import top.mrjello.dto.UserLoginDTO;
import top.mrjello.entity.User;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 15:26
 */
public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO 用户登录信息
     * @return 用户信息
     */
    User userLogin(UserLoginDTO userLoginDTO);
}
