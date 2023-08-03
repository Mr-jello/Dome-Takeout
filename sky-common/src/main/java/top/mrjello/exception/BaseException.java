package top.mrjello.exception;
/**
 * 基础异常类
 * @author jason@mrjello.top
 * @date 2023/8/2 21:09
 */
public class BaseException extends RuntimeException{
    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
