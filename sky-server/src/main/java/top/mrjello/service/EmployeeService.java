package top.mrjello.service;

import top.mrjello.dto.EmployeeLoginDTO;
import top.mrjello.entity.Employee;

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
}
