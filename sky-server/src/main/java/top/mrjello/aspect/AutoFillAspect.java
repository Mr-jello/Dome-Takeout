package top.mrjello.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import top.mrjello.annotation.AutoFill;
import top.mrjello.constant.AutoFillConstant;
import top.mrjello.context.BaseContext;
import top.mrjello.enumeration.OperationType;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 17:01
 */
@Slf4j
@Aspect // 标识为切面类
@Component // 注入到spring容器
public class AutoFillAspect {

        /**
         * 自动填充属性（更新创建时间，用户）切面
         * @param joinPoint 切点
         * @param autoFill 注解
         * 当基于注解的切面时，切点表达式中的注解必须是全限定名
         */
        @Before("execution(* top.mrjello.mapper.*.*(..)) && @annotation(autoFill)")
        public void autoFillProperties(JoinPoint joinPoint, AutoFill autoFill) throws Exception {
            //1.获取原始方法的运行参数（对象）
            Object[] args = joinPoint.getArgs();
            if (ObjectUtils.isEmpty(args)) {
                return;
            }
            Object arg = args[0];   //获取第一个参数

            log.info("Before autoFillProperties {}",arg);

            //2.反射获取对象对应的方法(4个属性的set方法)
            Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            //3.获取方法上的注解上的值
            OperationType operationType = autoFill.value();

            //4.根据注解上的值，判断是新增还是修改
            if (operationType.equals(OperationType.INSERT)) {
                setCreateTime.invoke(arg, LocalDateTime.now());
                setCreateUser.invoke(arg, BaseContext.getCurrentId());
            }
            setUpdateTime.invoke(arg, LocalDateTime.now());
            setUpdateUser.invoke(arg, BaseContext.getCurrentId());

            log.info("After autoFillProperties {}" , arg);
        }


    //        @Before("execution(* top.mrjello.mapper.*.*(..)) && @annotation(top.mrjello.annotation.AutoFill)")
//        public void autoFillProperties(JoinPoint joinPoint) throws Exception {
//            //1.获取原始方法的运行参数（对象）
//            Object[] args = joinPoint.getArgs();
//            if (ObjectUtils.isEmpty(args)) {
//                return;
//            }
//            Object arg = args[0];   //获取第一个参数
//
//            log.info("Before autoFillProperties {}",arg);
//
//            //2.反射获取对象对应的方法(4个属性的set方法)
//            Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
//            Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
//            Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
//            Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//
//            //3.获取方法上的注解上的值
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
//            OperationType operationType = autoFill.value();
//
//            //4.根据注解上的值，判断是新增还是修改
//            if (operationType.equals(OperationType.INSERT)) {
//                setCreateTime.invoke(arg, LocalDateTime.now());
//                setCreateUser.invoke(arg, BaseContext.getCurrentId());
//            }
//            setUpdateTime.invoke(arg, LocalDateTime.now());
//            setUpdateUser.invoke(arg, BaseContext.getCurrentId());
//
//            log.info("After autoFillProperties {}" , arg);
//        }

}
