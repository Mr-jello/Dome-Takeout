package top.mrjello.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author jason@mrjello.top
 * @date 2023/8/3 18:05
 */
@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 1.创建RedisTemplate对象
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 2.设置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        // 3.设置key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 4.设置value的序列化器
        template.setHashKeySerializer(new StringRedisSerializer());
        // 5.返回RedisTemplate对象
        return template;
    }
}
