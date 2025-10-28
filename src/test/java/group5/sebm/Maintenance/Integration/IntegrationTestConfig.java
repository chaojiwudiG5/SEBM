package group5.sebm.Maintenance.Integration;

import group5.sebm.aop.AuthInterceptor;
import group5.sebm.interceptors.JwtInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 集成测试配置类
 * 用于配置测试环境中的特殊Bean
 */
@TestConfiguration
public class IntegrationTestConfig {

    /**
     * 提供一个Mock的邮件发送器，避免测试时真正发送邮件
     */
    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    /**
     * 提供一个不进行JWT验证的拦截器，用于测试环境
     * 直接从请求的 userId 属性获取用户ID（由测试代码设置）
     */
    @Bean
    @Primary
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // 在测试环境中，不进行JWT验证
                // userId 由测试代码通过 requestAttr 设置
                return true;
            }
        };
    }

    /**
     * 提供一个Mock的AOP拦截器，用于测试环境
     * 直接放行所有带@AuthCheck注解的方法
     */
    @Bean
    @Primary
    public AuthInterceptor authInterceptor() throws Throwable {
        AuthInterceptor mockInterceptor = Mockito.mock(AuthInterceptor.class);
        // 配置Mock：当调用doInterceptor时，直接执行原方法（放行）
        when(mockInterceptor.doInterceptor(any(ProceedingJoinPoint.class), any()))
            .thenAnswer(invocation -> {
                ProceedingJoinPoint joinPoint = invocation.getArgument(0);
                return joinPoint.proceed();
            });
        return mockInterceptor;
    }
}

