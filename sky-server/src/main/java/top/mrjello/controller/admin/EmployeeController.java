package top.mrjello.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrjello.constant.JwtClaimsConstant;
import top.mrjello.dto.EmployeeLoginDTO;
import top.mrjello.entity.Employee;

import top.mrjello.result.Result;
import top.mrjello.service.EmployeeService;
import top.mrjello.utils.JwtUtil;
import top.mrjello.vo.EmployeeLoginVO;

import java.util.HashMap;
import java.util.Map;

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
}
