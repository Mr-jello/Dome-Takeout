package top.mrjello.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import top.mrjello.entity.User;

/**
 * @author jason@mrjello.top
 * @date 2023/8/9 15:27
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid 微信用户唯一标识
     * @return 用户信息
     */
    @Select("select * from user where openid = #{openid}")
    User queryUserByOpenid(String openid);

    /**
     * 新增用户
     * @param user 用户信息
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time) values " +
            "(#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    void addNewUser(User user);
}
