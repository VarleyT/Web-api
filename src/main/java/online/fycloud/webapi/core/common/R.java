package online.fycloud.webapi.core.common;

import lombok.Data;

/**
 * 通用返回结果类
 *
 * @param <T>
 * @author VarleyT
 */
@Data
public class R<T> {
    /**
     * 响应代码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;
    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.code = 200;
        r.msg = "success";
        r.data = null;
        return r;
    }

    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.code = 200;
        r.msg = "success";
        r.data = object;
        return r;
    }

    public static <T> R<T> error(String errorMessage) {
        R<T> r = new R<>();
        r.code = 500;
        r.msg = errorMessage;
        r.data = null;
        return r;
    }

    public static <T> R<T> error(int code, String errorMessage) {
        R<T> r = new R<>();
        r.code = code;
        r.msg = errorMessage;
        r.data = null;
        return r;
    }

    public static <T> R<T> error(int code, String errorMessage, T data) {
        R<T> r = new R<>();
        r.code = code;
        r.msg = errorMessage;
        r.data = data;
        return r;
    }
}
