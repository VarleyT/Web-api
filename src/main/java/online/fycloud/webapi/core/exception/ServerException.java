package online.fycloud.webapi.core.exception;

import lombok.Getter;

/**
 * @author VarleyT
 * @date 2023/1/9
 */
@Getter
public class ServerException extends Exception {

    /**
     * 错误码
     */
    private ErrorCodes errorCode;
    /**
     * 错误描述
     */
    private String errorMsg;

    public ServerException() {
        this.errorCode = ErrorCodes.COMMON;
        this.errorMsg = "请求发生错误";
    }

    public ServerException(String errorMsg) {
        this.errorCode = ErrorCodes.COMMON;
        this.errorMsg = errorMsg;
    }

    public ServerException(ErrorCodes errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
