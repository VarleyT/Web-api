package online.fycloud.webapi.core.annotation;

import java.lang.annotation.*;

/**
 * 接口次数限制
 * @author VarleyT
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequest {
    /**
     * 限制时间 单位：毫秒
     * @return
     */
    long time() default 6000;
    /**
     * 允许请求的次数
     * @return
     */
    int count() default 3;
}