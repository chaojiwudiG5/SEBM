package group5.sebm.exception;

/**
 * 全局统一返回码枚举
 */
public enum ErrorCode {
    INVALID_PARAM(100, "参数错误"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未授权或登录过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源未找到"),
    SYSTEM_ERROR(500, "系统错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
