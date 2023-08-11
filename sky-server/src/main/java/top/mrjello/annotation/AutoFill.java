package top.mrjello.annotation;

import top.mrjello.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 16:51
 * @description 自动填充注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoFill {

    /**
     * 标识是用于新增还是修改
     */
    OperationType value();
}
