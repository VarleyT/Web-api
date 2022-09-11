package online.fycloud.webapi.core.exception;

import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.core.common.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;

/**
 * @author VarleyT
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 实体绑定异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus
    public R<String> validException(BindException e) {
        String msg = e.getAllErrors().get(0).getDefaultMessage();
        return R.error(msg);
    }

    /**
     * 缺少请求参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus
    public R<String> missingParamException(MissingServletRequestParameterException e) {
        return R.error("缺少请求参数！");
    }

    /**
     * 约束异常
     *
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus
    public R<String> constraintViolationException(ConstraintViolationException e) {
        return R.error("参数值错误！");
    }

    /**
     * 不支持的请求类型异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus
    public R<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return R.error(500, "请使用正确的请求类型：" + Arrays.toString(e.getSupportedMethods()));
    }

    /**
     * 页面未找到
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public R<String> notFoundException(NoHandlerFoundException e) {
        return R.error(404, "页面不存在！");
    }

}
