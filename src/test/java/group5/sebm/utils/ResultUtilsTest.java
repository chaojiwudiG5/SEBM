package group5.sebm.utils;

import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultUtilsTest {

    @Test
    void testSuccess() {
        BaseResponse<String> response = ResultUtils.success("Hello");

        assertEquals(0, response.getCode());
        assertEquals("Hello", response.getData());
        assertEquals("ok", response.getMessage());
    }

    @Test
    void testErrorWithErrorCode() {
        BaseResponse<?> response = ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), response.getCode());
        assertNull(response.getData());
        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getMessage(), response.getMessage());
    }

    @Test
    void testErrorWithCodeAndMessage() {
        BaseResponse<?> response = ResultUtils.error(1234, "Custom Error");

        assertEquals(1234, response.getCode());
        assertNull(response.getData());
        assertEquals("Custom Error", response.getMessage());
    }

    @Test
    void testErrorWithErrorCodeAndCustomMessage() {
        BaseResponse<?> response = ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Access Denied");

        assertEquals(ErrorCode.NO_AUTH_ERROR.getCode(), response.getCode());
        assertNull(response.getData());
        assertEquals("Access Denied", response.getMessage());
    }
}
