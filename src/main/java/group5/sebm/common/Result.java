package group5.sebm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回结果
 * @param <T> 返回数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;     // 状态码：0=成功，1=失败
    private String msg;   // 提示信息
    private T data;       // 返回数据

    // 静态方法方便快速返回
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(0, msg, data);
    }

    public static <T> Result<T> failure(String msg) {
        return new Result<>(1, msg ,null);
    }

    public static <T> Result<T> failure(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}