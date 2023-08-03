package top.mrjello.exception;
/**
 * 业务异常
 * @author jason@mrjello.top
 * @date 2023/8/2 21:23
 */
public class BusinessException extends BaseException{

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
