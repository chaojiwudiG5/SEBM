package group5.sebm.notifiation.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * WebSocket握手拦截器测试
 */
@ExtendWith(MockitoExtension.class)
class LoggingHandshakeInterceptorTest {

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServletServerHttpRequest servletRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    private LoggingHandshakeInterceptor interceptor;
    private Map<String, Object> attributes;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        interceptor = new LoggingHandshakeInterceptor();
        attributes = new HashMap<>();
        headers = new HttpHeaders();
    }

    @Test
    void testBeforeHandshake_WithUserId() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws?userId=123");
        when(request.getURI()).thenReturn(uri);
        when(request.getHeaders()).thenReturn(headers);
        headers.add("User-Agent", "Mozilla/5.0");
        headers.setOrigin("http://localhost:8080");

        // When
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("123", attributes.get("userId"));
        assertEquals("http://localhost:8080", attributes.get("origin"));
        assertEquals("Mozilla/5.0", attributes.get("userAgent"));
        assertNotNull(attributes.get("requestUri"));
    }

    @Test
    void testBeforeHandshake_WithoutUserId() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws");
        when(request.getURI()).thenReturn(uri);
        when(request.getHeaders()).thenReturn(headers);

        // When
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertNull(attributes.get("userId"));
    }

    @Test
    void testBeforeHandshake_WithClientIp() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws?userId=123");
        when(servletRequest.getURI()).thenReturn(uri);
        when(servletRequest.getHeaders()).thenReturn(headers);
        when(servletRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1");

        // When
        boolean result = interceptor.beforeHandshake(servletRequest, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("192.168.1.1", attributes.get("clientIp"));
    }

    @Test
    void testBeforeHandshake_WithMultipleForwardedIps() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws?userId=123");
        when(servletRequest.getURI()).thenReturn(uri);
        when(servletRequest.getHeaders()).thenReturn(headers);
        when(servletRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");

        // When
        boolean result = interceptor.beforeHandshake(servletRequest, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("192.168.1.1", attributes.get("clientIp"));
    }

    @Test
    void testBeforeHandshake_WithXRealIp() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws");
        when(servletRequest.getURI()).thenReturn(uri);
        when(servletRequest.getHeaders()).thenReturn(headers);
        when(servletRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn("10.0.0.1");

        // When
        boolean result = interceptor.beforeHandshake(servletRequest, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("10.0.0.1", attributes.get("clientIp"));
    }

    @Test
    void testBeforeHandshake_WithRemoteAddr() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws");
        when(servletRequest.getURI()).thenReturn(uri);
        when(servletRequest.getHeaders()).thenReturn(headers);
        when(servletRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        // When
        boolean result = interceptor.beforeHandshake(servletRequest, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("127.0.0.1", attributes.get("clientIp"));
    }

    @Test
    void testBeforeHandshake_WithException() throws Exception {
        // Given
        when(request.getURI()).thenThrow(new RuntimeException("Test exception"));

        // When
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertTrue(result); // Should still return true despite exception
    }

    @Test
    void testAfterHandshake_Success() {
        // Given
        URI uri = URI.create("ws://localhost/ws");
        when(request.getURI()).thenReturn(uri);

        // When
        interceptor.afterHandshake(request, response, wsHandler, null);

        // Then
        // Should not throw exception
        verify(request).getURI();
    }

    @Test
    void testAfterHandshake_WithException() {
        // Given
        URI uri = URI.create("ws://localhost/ws");
        when(request.getURI()).thenReturn(uri);
        Exception exception = new RuntimeException("Handshake failed");

        // When
        interceptor.afterHandshake(request, response, wsHandler, exception);

        // Then
        // Should not throw exception
        verify(request).getURI();
    }

    @Test
    void testBeforeHandshake_WithMultipleQueryParams() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws?userId=123&token=abc&role=admin");
        when(request.getURI()).thenReturn(uri);
        when(request.getHeaders()).thenReturn(headers);

        // When
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("123", attributes.get("userId"));
    }

    @Test
    void testBeforeHandshake_WithUnknownHeader() throws Exception {
        // Given
        URI uri = new URI("ws://localhost/ws");
        when(servletRequest.getURI()).thenReturn(uri);
        when(servletRequest.getHeaders()).thenReturn(headers);
        when(servletRequest.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        // When
        boolean result = interceptor.beforeHandshake(servletRequest, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals("127.0.0.1", attributes.get("clientIp"));
    }
}

