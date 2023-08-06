package top.mrjello.context;

/**
 * @author Jason
 */
public class BaseContext {

    // ThreadLocal是一个线程内部的存储类，可以在指定线程内存储数据，数据存储以后，只有指定线程可以得到存储数据
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    //线程存储当前登录用户的id
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    //获取当前登录用户的id
    public static Long getCurrentId() {
        return threadLocal.get();
    }
    //移除当前登录用户的id
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
