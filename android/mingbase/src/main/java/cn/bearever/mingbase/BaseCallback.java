package cn.bearever.mingbase;

/**
 * 通用异步返回接口
 *
 * @author luoming
 * @date 2020/4/12
 */
public abstract class BaseCallback<T> {
    /**
     * 成功
     *
     * @param data
     */
    public abstract void suc(T data);

    /**
     * 失败
     *
     * @param msg  失败说明
     * @param code 失败码
     */
    public void fail(String msg, int code) {

    }
}
