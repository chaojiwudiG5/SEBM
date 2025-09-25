package group5.sebm.interceptors;

import group5.sebm.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 * 1. 检查请求头 Authorization 是否存在并合法
 * 2. 解析 token 获取用户 ID
 * 3. 放入 request attribute 供 controller 使用
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 1. 获取请求头 token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        try {
            // 2. 解析 token
            Long userId = JwtUtils.getUserIdFromToken(token.substring(7)); // 去掉 "Bearer "
            // 3. 放入 request，controller 可以通过 request.getAttribute("userId") 获取
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return false;
        }
    }
}
