package group5.sebm.CommonTest;

import group5.sebm.common.BaseResponse;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseResponseTest {

    @Test
    void testConstructorWithCodeDataMessage() {
        BaseResponse<String> response = new BaseResponse<>(200, "OK", "Success");
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getData());
        assertEquals("Success", response.getMessage());
    }

    @Test
    void testConstructorWithCodeAndData() {
        BaseResponse<Integer> response = new BaseResponse<>(201, 123);
        assertEquals(201, response.getCode());
        assertEquals(123, response.getData());
        assertEquals("", response.getMessage());
    }

    @Test
    void testConstructorWithErrorCode() {
        BaseResponse<String> response = new BaseResponse<>(ErrorCode.PARAMS_ERROR);
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), response.getCode());
        assertNull(response.getData());
        assertEquals(ErrorCode.PARAMS_ERROR.getMessage(), response.getMessage());
    }

    @Test
    void testConstructorWithErrorCodeSuccessCase() {
        BaseResponse<String> response = new BaseResponse<>(ErrorCode.SUCCESS);
        assertEquals(0, response.getCode());
        assertEquals("ok", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testSettersAndGetters() {
        BaseResponse<String> response = new BaseResponse<>(0, null, null);
        response.setCode(500);
        response.setData("Error");
        response.setMessage("Internal Server Error");

        assertEquals(500, response.getCode());
        assertEquals("Error", response.getData());
        assertEquals("Internal Server Error", response.getMessage());
    }

    @Test
    void testEqualsHashCodeAndToString() {
        BaseResponse<String> r1 = new BaseResponse<>(200, "OK", "Success");
        BaseResponse<String> r2 = new BaseResponse<>(200, "OK", "Success");

        // @Data 自动生成 equals/hashCode
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("OK"));
        assertTrue(r1.toString().contains("Success"));
    }
}
