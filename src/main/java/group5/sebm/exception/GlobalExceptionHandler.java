package group5.sebm.exception;

import group5.sebm.common.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: " + e.getCode(), e.getMessage());
        return Result.failure(e.getCode(),e.getMessage());
    }

    // DTO 校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.failure(ErrorCode.INVALID_PARAM.getCode(), errorMsg);
    }

    // RequestParam / PathVariable 校验异常
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolation(ConstraintViolationException e) {
        String errorMsg = e.getConstraintViolations().iterator().next().getMessage();
        return Result.failure(ErrorCode.INVALID_PARAM.getCode(), errorMsg);
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.failure(ErrorCode.SYSTEM_ERROR.getCode(), "系统异常: " + e.getMessage());
    }
}
