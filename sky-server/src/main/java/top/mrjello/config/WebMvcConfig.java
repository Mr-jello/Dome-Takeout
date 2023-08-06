package top.mrjello.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.mrjello.interceptor.AdminLoginInterceptor;
import top.mrjello.json.JacksonObjectMapper;

/**
 * @author jason@mrjello.top
 * @date 2023/8/3 16:29
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    /**
     * 添加拦截器
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminLoginInterceptor).addPathPatterns("/admin/**")
                                                        .excludePathPatterns("/admin/employee/login",
                                                                "/swagger-resources/**",
                                                                "/v2/api-docs/**",
                                                                "/swagger-ui.html",
                                                                "/doc.html",
                                                                "/webjars/**",
                                                                "/swagger-resources/configuration/ui",
                                                                "/swagger-resources/configuration/security");

    }


    /**
     * 添加静态资源映射
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问的问题
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 解决升级swagger3.0.3后，swagger无法访问的问题
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 添加自定义的消息转换器
     * @param converters converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, jackson2HttpMessageConverter);
        }


}
