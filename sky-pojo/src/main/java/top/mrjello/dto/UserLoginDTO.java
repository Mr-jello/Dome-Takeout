package top.mrjello.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 * @author Jason
 */
@Data
public class UserLoginDTO implements Serializable {

    private String code;

}
