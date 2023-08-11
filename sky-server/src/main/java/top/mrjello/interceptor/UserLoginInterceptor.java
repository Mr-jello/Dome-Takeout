package top.mrjello.interceptor;



import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.mrjello.constant.JwtClaimsConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.properties.JwtProperties;
import top.mrjello.utils.JwtUtil;

/**
 * @author jason@mrjello.top
 * @date 2023/8/3 16:10
 */
@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户端登录拦截器
     * 在请求处理之前进行调用（Controller方法调用之前）
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("{} is requesting", request.getRequestURI());

        //1.从请求头中获取token
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2.判断token是否为空,如果为空，返回错误码401
        if (!StringUtils.hasLength(token)) {
            log.info("token is null, error code 401");
            response.setStatus(401);
            return false;
        }
        //3.判断token是否有效，如果无效，返回错误码401

        try{
            JwtUtil.checkUserToken(jwtProperties.getUserSecretKey(), token);
            Claims claims = JwtUtil.parseUserToken(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            // 将用户id放入threadLocal中
            BaseContext.setCurrentId(userId);
        } catch (Exception e) {
            log.info("token is invalid, error code 401");
            response.setStatus(401);
            return false;
        }
        //4.如果token有效，放行
        return true;
    }


    /**
     * 在请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @param modelAndView 视图模型
     * @throws Exception 异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        BaseContext.removeCurrentId();
    }
}
