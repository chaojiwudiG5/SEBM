package group5.sebm.utils;

import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestUtilsTest {

    @Test
    void testGetCurrentUserId_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(123L);

        Long userId = RequestUtils.getCurrentUserId(request);
        assertEquals(123L, userId);
    }

    @Test
    void testGetCurrentUserId_notLoggedIn_shouldThrow() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> RequestUtils.getCurrentUserId(request));
        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testGetCurrentUserIdSafely_loggedIn() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(456L);

        Long userId = RequestUtils.getCurrentUserIdSafely(request);
        assertEquals(456L, userId);
    }

    @Test
    void testGetCurrentUserIdSafely_notLoggedIn() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(null);

        Long userId = RequestUtils.getCurrentUserIdSafely(request);
        assertNull(userId);
    }

    @Test
    void testIsUserLoggedIn_true() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        assertTrue(RequestUtils.isUserLoggedIn(request));
    }

    @Test
    void testIsUserLoggedIn_false() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(null);

        assertFalse(RequestUtils.isUserLoggedIn(request));
    }

    @Test
    void testGetClientIpAddress_withXForwardedFor() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.100, 10.0.0.1");

        String ip = RequestUtils.getClientIpAddress(request);
        assertEquals("192.168.1.100", ip);
    }

    @Test
    void testGetClientIpAddress_withRemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(anyString())).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = RequestUtils.getClientIpAddress(request);
        assertEquals("127.0.0.1", ip);
    }

    @Test
    void testGetUserAgent_present() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        String ua = RequestUtils.getUserAgent(request);
        assertEquals("Mozilla/5.0", ua);
    }

    @Test
    void testGetUserAgent_missing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn(null);

        String ua = RequestUtils.getUserAgent(request);
        assertEquals("unknown", ua);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = RequestUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            constructor.newInstance();
            fail("Expected UnsupportedOperationException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            // 反射封装的异常，真正的异常在 getCause()
            Throwable cause = e.getCause();
            assertTrue(cause instanceof UnsupportedOperationException);
            assertEquals("This is a utility class and cannot be instantiated", cause.getMessage());
        }
    }

}
