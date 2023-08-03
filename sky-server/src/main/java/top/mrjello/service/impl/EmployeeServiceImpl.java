package top.mrjello.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrjello.constant.MessageConstant;
import top.mrjello.constant.StatusConstant;
import top.mrjello.dto.EmployeeLoginDTO;
import top.mrjello.entity.Employee;
import top.mrjello.exception.DataException;
import top.mrjello.mapper.EmployeeMapper;
import top.mrjello.service.EmployeeService;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 20:24
 */
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String password = employeeLoginDTO.getPassword();

        //1.调用mapper查询数据库
        Employee employee = employeeMapper.queryByUserName(employeeLoginDTO.getUsername());

        //2.判断是否查询到数据，如果查询到数据，返回Employee对象，否则返回错误信息
        if (employee == null) {
            log.info("No employee found by username");
            throw new DataException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //3.校验密码是否正确，如果正确，返回Employee对象，否则返回错误信息
        if (!password.equals(employee.getPassword())) {
            log.info("Password is incorrect");
            throw new DataException(MessageConstant.PASSWORD_ERROR);
        }

        //4.校验员工是否被禁用，如果被禁用，返回错误信息，否则返回Employee对象
        if (employee.getStatus() == StatusConstant.DISABLE) {
            log.info("This employee {} is disabled ", employeeLoginDTO.getUsername());
            throw new DataException(MessageConstant.ACCOUNT_LOCKED);
        }


        return employee;
    }
}
