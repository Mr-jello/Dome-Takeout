package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.entity.Employee;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 20:41
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username 用户名
     * @return Employee
     */
    @Select("select id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, create_time from employee where username = #{username}")
    public Employee queryByUserName(String username);

}
