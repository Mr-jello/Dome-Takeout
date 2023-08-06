package top.mrjello.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.mrjello.constant.MessageConstant;
import top.mrjello.constant.PasswordConstant;
import top.mrjello.constant.StatusConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.dto.EmployeeDTO;
import top.mrjello.dto.EmployeeEditPasswordDTO;
import top.mrjello.dto.EmployeeLoginDTO;
import top.mrjello.dto.EmployeePageQueryDTO;
import top.mrjello.entity.Employee;
import top.mrjello.exception.BusinessException;
import top.mrjello.exception.DataException;
import top.mrjello.mapper.EmployeeMapper;
import top.mrjello.result.PageResult;
import top.mrjello.service.EmployeeService;
import top.mrjello.utils.BeanHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 20:24
 */
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    // 密码错误的key
    private static final String LOGIN_ERROR_KEY = "login:error:";
    // 账号锁定的key
    private static final String LOGIN_LOCK_KEY = "login:lock:";


    // 注入mapper
    @Autowired
    private EmployeeMapper employeeMapper;

    // 注入redisTemplate
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 员工登录service实现类
     * @param employeeLoginDTO 员工登录DTO
     * @return Employee
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 检验账号是否被锁定  账号5分钟内连续输错密码5次，账号锁定一小时
        validAccountLock(username);

        //1.调用mapper查询数据库
        Employee employee = employeeMapper.queryByUserName(employeeLoginDTO.getUsername());

        //2.判断是否查询到数据，如果查询到数据，返回Employee对象，否则返回错误信息
        if (employee == null) {
            log.info("No employee found by username");
            throw new DataException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //3.校验密码是否正确，如果正确，返回Employee对象，否则返回错误信息
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            log.info("Password is incorrect");

            // 1.密码错误次数+1，记录员工错误密码标识，并在redis中设置有效期为5分钟
            //  每次类似在redis中写入：setex login_用户名_随机字符串 300 -
            redisTemplate.opsForValue().set(getKey(username), "-", 5, TimeUnit.MINUTES);

            // 2.获取该员工的密码错误标识，若标识>=5，锁定账号，设置redis有效期为1小时
            Set<Object> keys = redisTemplate.keys(LOGIN_ERROR_KEY + username + ":*");
            if(keys != null && keys.size() >= 5) {
                log.info("This employee {} is locked for 1 hours", employeeLoginDTO.getUsername());
                // 3.设置redis中的login_lock标识，有效期为1小时
                redisTemplate.opsForValue().set(LOGIN_LOCK_KEY + username, "-", 1, TimeUnit.HOURS);
                // 4.抛出异常
                throw new BusinessException(MessageConstant.OVERTRY_LOGIN_LOCKED);
            }

            throw new DataException(MessageConstant.PASSWORD_ERROR);
        }
        //4.校验员工是否被禁用，如果被禁用，返回错误信息，否则返回Employee对象
        if (employee.getStatus() == StatusConstant.DISABLE) { //禁用
            log.info("This employee {} is disabled ", employeeLoginDTO.getUsername());
            throw new DataException(MessageConstant.ACCOUNT_LOCKED);
        }
        return employee;
    }
    // 检验账号是否被锁定
    private void validAccountLock(String username) {
        Object flag = redisTemplate.opsForValue().get(LOGIN_LOCK_KEY + username);
        if(ObjectUtils.isNotEmpty(flag)) { // 有标识，说明账号被锁定
            log.info("This employee {} is locked for 1 hours", username);
            throw new BusinessException(MessageConstant.OVERTRY_LOGIN_LOCKED);
        }
    }
    // 生成redis中的key
    private static String getKey(String username) {
        return LOGIN_ERROR_KEY + username + ":" + RandomStringUtils.randomAlphabetic(5);
    }



    /**
     * 添加员工service实现类
     * @param employeeDTO 员工DTO
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        //1. 补全实体属性(createTime, updateTime....)
        Employee employee = BeanHelper.copyProperties(employeeDTO, Employee.class);
        assert employee != null;
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setStatus(StatusConstant.ENABLE);
        // 设置时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 设置创建人和修改人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //2. 调用mapper添加员工
        employeeMapper.addEmployee(employee);

    }

    /**
     * 分页查询员工的service实现类
     * @param employeePageQueryDTO 员工分页查询DTO
     * @return PageResult
     */
    @Override
    public PageResult queryEmployeeByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        // 1.设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //2.查询数据库
        List<Employee> employeeList = employeeMapper.queryEmployeeByPage(employeePageQueryDTO.getName());
        //3.封装结果集
        //Page<Employee> employeePage = (Page<Employee>) employeeList;
        Page<Employee> employeePage = (Page<Employee>) employeeList;

        return new PageResult(employeePage.getTotal(), employeePage.getResult());
    }

    /**
     * 更新员工状态的service实现类
     * @param id 员工id
     * @param status 员工状态
     */
    @Override
    public void enableOrDisableEmployee(Long id, Integer status) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        //2.调用mapper修改员工状态,后续也可以修改其他属性
        employeeMapper.updateEmployee(employee);
    }

    /**
     * 根据员工id查询员工信息的service实现类
     * @param id 员工id
     * @return EmployeeDTO
     */
    @Override
    public EmployeeDTO queryEmployeeById(Long id) {
        log.info("queryEmployeeById id:{}", id);
        //1.调用mapper查询员工信息
        Employee employee = employeeMapper.queryEmployeeById(id);
        //2.判断是否查询到数据，如果查询到数据，返回EmployeeDTO对象，否则返回错误信息
        assert employee != null;
        //3.返回EmployeeDTO对象
        return BeanHelper.copyProperties(employee, EmployeeDTO.class);
    }

    /**
     * 更新员工信息的service实现类
     * @param employeeDTO 员工DTO
     */
    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        //1.补全实体属性
        Employee employee = BeanHelper.copyProperties(employeeDTO, Employee.class, "password", "status", "createTime", "createUser");
        assert employee != null;
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        //2.调用mapper修改员工信息
        employeeMapper.updateEmployee(employee);
    }

    /**
     * 修改员工密码的service实现类
     * @param employeeEditPasswordDTO 员工修改密码DTO
     */
    @Override
    public void editPassword(EmployeeEditPasswordDTO employeeEditPasswordDTO) {
        //1.校验原密码是否正确，如果不正确，返回错误信息
        employeeEditPasswordDTO.setEmpId(BaseContext.getCurrentId());
        //1.1根据员工id查询员工信息
        Employee employee = employeeMapper.queryEmployeeById(employeeEditPasswordDTO.getEmpId());
        assert employee != null;
        // 判断原密码是否正确
        if(!employee.getPassword().equals(DigestUtils.md5DigestAsHex(employeeEditPasswordDTO.getOldPassword().getBytes()))) {
            throw new BusinessException(MessageConstant.PASSWORD_ERROR);
        }
//        //判断新密码和确认密码是否一致
//        if(!employeeEditPasswordDTO.getNewPassword().equals(employeeEditPasswordDTO.getConfirmPassword())) {
//            throw new BusinessException(MessageConstant.PASSWORD_NOT_SAME);
//        }
        //2.补全实体属性
        Employee editedEmployee = Employee.builder()
                .id(employeeEditPasswordDTO.getEmpId())
                .password(DigestUtils.md5DigestAsHex(employeeEditPasswordDTO.getNewPassword().getBytes()))
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        //3.调用mapper修改员工密码
        employeeMapper.updateEmployee(editedEmployee);
    }

}
