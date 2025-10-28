package group5.sebm.config;

import group5.sebm.interceptors.JwtInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class WebConfigTest {

    private WebConfig webConfig;
    private JwtInterceptor jwtInterceptor;
    private InterceptorRegistry registry;
    private InterceptorRegistration registration;

    @BeforeEach
    void setUp() throws Exception {
        webConfig = new WebConfig();
        jwtInterceptor = mock(JwtInterceptor.class);
        registry = mock(InterceptorRegistry.class);
        registration = mock(InterceptorRegistration.class);

        // 使用反射注入 private 字段 jwtInterceptor
        Field field = WebConfig.class.getDeclaredField("jwtInterceptor");
        field.setAccessible(true);
        field.set(webConfig, jwtInterceptor);

        // mock 调用链
        when(registry.addInterceptor(jwtInterceptor)).thenReturn(registration);
        when(registration.addPathPatterns("/**")).thenReturn(registration);
        when(registration.excludePathPatterns(
            "/user/login",
            "/user/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/doc.html",
            "/webjars/**"
        )).thenReturn(registration);
    }

    @Test
    void testAddInterceptors_registersJwtInterceptor() {
        webConfig.addInterceptors(registry);

        // 验证调用顺序
        InOrder inOrder = inOrder(registry, registration);
        inOrder.verify(registry).addInterceptor(jwtInterceptor);
        inOrder.verify(registration).addPathPatterns("/**");
        inOrder.verify(registration).excludePathPatterns(
            "/user/login",
            "/user/register",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/doc.html",
            "/webjars/**"
        );

        verifyNoMoreInteractions(registry, registration);
    }
}
