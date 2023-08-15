package top.mrjello.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import top.mrjello.dto.UserReportDTO;
import top.mrjello.entity.User;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 根据用户id查询用户
     * @param userId 用户id
     * @return 用户信息
     */
    @Select("select * from user where id = #{userId}")
    User getUserById(Long userId);

    /**
     * 查询指定日期范围内的用户统计信息
     * @param startDateTime 开始日期
     * @param endDateTime 结束日期
     * @return List<UserReportDTO>
     */
    List<UserReportDTO> queryUserStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 统计指定日期之前的用户总量
     * @param startDateTime 开始日期
     * @return Integer
     */
    @Select("select count(*) from user where create_time < #{startDateTime}")
    Integer queryTotalUserStatistics(LocalDateTime startDateTime);

}
