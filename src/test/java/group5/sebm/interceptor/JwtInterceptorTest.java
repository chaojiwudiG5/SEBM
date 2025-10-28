package group5.sebm.interceptor;

import group5.sebm.interceptors.JwtInterceptor;
import group5.sebm.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtInterceptorTest {

    private final JwtInterceptor interceptor = new JwtInterceptor();

    @Test
    void testPreHandle_optionsMethod_shouldReturnTrue() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("OPTIONS");

        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
        verifyNoInteractions(response);
    }

    @Test
    void testPreHandle_missingAuthorization_shouldReturn401() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(writer.toString().contains("Missing or invalid Authorization header"));
    }

    @Test
    void testPreHandle_invalidToken_shouldReturn401() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        try (MockedStatic<JwtUtils> mocked = mockStatic(JwtUtils.class)) {
            mocked.when(() -> JwtUtils.getUserIdFromToken("invalid-token"))
                    .thenThrow(new RuntimeException("Invalid token"));

            boolean result = interceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            assertTrue(writer.toString().contains("Invalid or expired token"));
        }
    }

    @Test
    void testPreHandle_validToken_shouldReturnTrue() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        try (MockedStatic<JwtUtils> mocked = mockStatic(JwtUtils.class)) {
            mocked.when(() -> JwtUtils.getUserIdFromToken("valid-token")).thenReturn(123L);

            boolean result = interceptor.preHandle(request, response, new Object());

            assertTrue(result);
            verify(request).setAttribute("userId", 123L);
        }
    }
}
