package top.mrjello.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.annotation.AutoFill;
import top.mrjello.entity.Employee;
import top.mrjello.enumeration.OperationType;

import java.util.List;

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
    @Select("select id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, create_time from employee where username = #{username} order by update_time desc")
    Employee queryByUserName(String username);

    /**
     * 新增员工
     * @param employee 员工
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addEmployee(Employee employee);

    /**
     * 动态条件分页查询员工
     * @param name 员工姓名
     * @return List<Employee>
     */
    List<Employee> queryEmployeeByPage(String name);

    /**
     * 启用或禁用员工
     * 后续也可以用来更新员工其他信息
     * 使用动态sql
     */
    @AutoFill(OperationType.UPDATE)
    void updateEmployee(Employee employee);

    /**
     * 根据员工id查询员工
     * @param id 员工id
     * @return Employee
     */
    @Select("select * from employee where id = #{id}")
    Employee queryEmployeeById(Long id);

}
