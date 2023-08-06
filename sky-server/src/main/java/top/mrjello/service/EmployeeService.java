package top.mrjello.service;

import top.mrjello.dto.EmployeeDTO;
import top.mrjello.dto.EmployeeEditPasswordDTO;
import top.mrjello.dto.EmployeeLoginDTO;
import top.mrjello.dto.EmployeePageQueryDTO;
import top.mrjello.entity.Employee;
import top.mrjello.result.PageResult;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 20:25
 */

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 员工登录DTO
     * @return Employee
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    /**
     * 添加员工
     * @param employeeDTO 员工DTO
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO 员工分页查询DTO
     * @return PageResult<Employee>
     */
    PageResult queryEmployeeByPage(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用或禁用员工
     * @param id 员工id
     * @param status 员工状态
     */
    void enableOrDisableEmployee(Long id, Integer status);

    /**
     * 根据员工id查询员工
     * @param id 员工id
     * @return EmployeeDTO
     */
    EmployeeDTO queryEmployeeById(Long id);

    /**
     * 更新员工
     * @param employeeDTO 员工DTO
     */
    void updateEmployee(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param employeeEditPasswordDTO 员工修改密码DTO
     */
    void editPassword(EmployeeEditPasswordDTO employeeEditPasswordDTO);

}
