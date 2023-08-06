package top.mrjello.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.handler.MessageCondition;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.mrjello.constant.MessageConstant;
import top.mrjello.exception.BaseException;
import top.mrjello.result.Result;

import java.security.Key;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 21:00
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param e BaseException
     * @return Result
     */
    @ExceptionHandler
    public Result baseExceptionHandler (BaseException e){
        e.printStackTrace();
        log.error("error msg：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 捕获数据库唯一键异常
     * @param e DuplicateKeyException
     * @return Result
     */
    @ExceptionHandler
    public Result baseExceptionHandler (DuplicateKeyException e){
        e.printStackTrace();
        log.error("error msg：{}", e.getMessage());
        String errorMessage = MessageConstant.UNKNOWN_ERROR;
        String message = e.getCause().getMessage();
        if (StringUtils.hasLength(message)) {
            String[] splitMessage = message.split(" ");
            errorMessage = splitMessage[2] + "已存在";
        }
        return Result.error(errorMessage);
    }



    /**
     * 捕获其他异常
     * @param e Exception
     * @return Result
     */
    @ExceptionHandler
    public Result exceptionHandler(Exception e){
        e.printStackTrace();
        log.error("error msg：{}", e.getMessage());
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
