package group5.sebm.config;

import group5.sebm.interceptors.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 JWT 拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private JwtInterceptor jwtInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtInterceptor)
        .addPathPatterns("/**")                   // 拦截所有请求
        .excludePathPatterns
            ("/user/login",
                "/user/register",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/doc.html",
                "/webjars/**"); // 排除登录和注册
  }
}
