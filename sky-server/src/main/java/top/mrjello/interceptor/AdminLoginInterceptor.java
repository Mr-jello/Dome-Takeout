package top.mrjello.interceptor;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.mrjello.utils.JwtUtil;

/**
 * @author jason@mrjello.top
 * @date 2023/8/3 16:10
 */
@Slf4j
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("{} is requesting", request.getRequestURI());

        //1.从请求头中获取token
        String token = request.getHeader("token");

        //2.判断token是否为空,如果为空，返回错误码401
        if (!StringUtils.hasLength(token)) {
            log.info("token is null, error code 401");
            response.setStatus(401);
            return false;
        }
        //3.判断token是否有效，如果无效，返回错误码401

        try{
            JwtUtil.checkToken(token);
        } catch (Exception e) {
            log.info("token is invalid, error code 401");
            response.setStatus(401);
            return false;
        }
        //4.如果token有效，放行
        return true;
    }
}
