package top.mrjello.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jason@mrjello.top
 * @date 2023/8/4 19:17
 */
@Slf4j
public class BeanHelper {

    /**
     * 将源对象的属性值拷贝到目标对象中
     * @param source 源对象
     * @param target 目标对象
     */
    public static <T> T copyProperties(Object source, Class<T> target) {
        try {
            if (source == null) {
                return null;
            }
            T t = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("BeanHelper copyProperties error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 将源对象的属性值拷贝到目标对象中
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreProperties 忽略的属性
     */
    public static <T> T copyProperties(Object source, Class<T> target, String... ignoreProperties) {
        try {
            if (source == null) {
                return null;
            }
            T t = target.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, t, ignoreProperties);
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("BeanHelper copyProperties error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
