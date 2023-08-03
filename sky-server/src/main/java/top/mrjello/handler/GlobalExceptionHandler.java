package top.mrjello.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import org.springframework.messaging.handler.MessageCondition;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.mrjello.constant.MessageConstant;
import top.mrjello.exception.BaseException;
import top.mrjello.result.Result;

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
