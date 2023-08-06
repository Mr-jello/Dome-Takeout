package top.mrjello.controller.admin;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.mrjello.constant.JwtClaimsConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.dto.EmployeeDTO;
import top.mrjello.dto.EmployeeEditPasswordDTO;
import top.mrjello.dto.EmployeeLoginDTO;
import top.mrjello.dto.EmployeePageQueryDTO;
import top.mrjello.entity.Employee;
import top.mrjello.result.PageResult;
import top.mrjello.result.Result;
import top.mrjello.service.EmployeeService;
import top.mrjello.utils.JwtUtil;
import top.mrjello.vo.EmployeeLoginVO;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 19:58
 */
@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * <p>
     * DTO: Data Transfer Object 数据传输对象
     * 用于封装业务层传输的数据，封装员工登录的用户名和密码
     * <p>
     * VO: View Object 返回给前端的数据对象
     * 用于封装业务层返回给前端的数据，封装员工登录成功后返回给前端的数据
     * <p>
     * Entity: 实体对象
     *
     * @return Result<EmployeeLoginVO>
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("Employee Login: {}", employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);

        //封装结果并返回，生成token
        Map<String, Object> claims = new HashMap<>();
        // 使用员工id作为jwt的唯一标识
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.generateJwtToken(claims);

        //封装结果并返回
        //EmployeeLoginVO employeeLoginVO = new EmployeeLoginVO(employee.getId(), employee.getUsername(), employee.getName(), token);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();


        return Result.success(employeeLoginVO);
    }

    /**
     * 添加员工
     * @return Result
     */
    @PostMapping
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("Add a new employee: {}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }


    /**
     * 分页查询员工
     * @return Result
     */
    @GetMapping("/page")
    public Result<PageResult> queryEmployeeByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("Query employee by page: {}", employeePageQueryDTO);
        PageResult pageResult = employeeService.queryEmployeeByPage(employeePageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 启用或者禁用员工
     * @param id 员工id
     * status 员工状态
     * @return Result
     */
//    @PutMapping("/status/{id}/{status}")
//    public Result enableOrDisableEmployee(@PathVariable Long id, @PathVariable Integer status) {
//        log.info("Enable or disable employee: id: {}, status: {}", id, status);
//        employeeService.enableOrDisableEmployee(id, status);
//        return Result.success();
//    }
    @PostMapping("/status/{status}")
    public Result enableOrDisableEmployee(Long id, @PathVariable Integer status) {
        log.info("Enable or disable employee: id: {}, status: {}", id, status);
        employeeService.enableOrDisableEmployee(id, status);
        return Result.success();
    }

    /**
     * 根据id查询员工
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<EmployeeDTO> queryEmployeeById(@PathVariable Long id) {
        log.info("Query employee by id: {}", id);
        EmployeeDTO employeeDTO = employeeService.queryEmployeeById(id);
        return Result.success(employeeDTO);
    }

    /**
     * 更新员工信息
     * @param employeeDTO 员工信息
     * @return Result
     */
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("Update employee: {}", employeeDTO);
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 修改员工密码
     */
    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody EmployeeEditPasswordDTO employeeEditPasswordDTO) {
        log.info("Edit employee password: {}", employeeEditPasswordDTO);
        employeeService.editPassword(employeeEditPasswordDTO);
        return Result.success();
    }

    /**
     * 员工登出
     */
    @PostMapping("/logout")
    public Result logout(HttpSession session) {
        log.info("NO.{} Employee logout", BaseContext.getCurrentId());
        session.invalidate();
        return Result.success();
    }

}
