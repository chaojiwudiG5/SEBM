package group5.sebm.utils;

import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTP 请求工具类
 */
public class RequestUtils {
    
    /**
     * 用户ID在request中的属性名
     */
    private static final String USER_ID_ATTRIBUTE = "userId";
    
    /**
     * 从 HttpServletRequest 中获取当前用户ID
     * 
     * 使用场景：
     * 1. JWT拦截器会解析token并将userId放入request.attribute中
     * 2. Controller层通过此方法获取当前登录用户ID
     * 
     * @param request HTTP请求对象
     * @return 当前登录用户ID
     * @throws RuntimeException 当用户未登录时抛出异常
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("HttpServletRequest 对象不能为空");
        }
        
        Long userId = (Long) request.getAttribute(USER_ID_ATTRIBUTE);
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        
        return userId;
    }
    
    /**
     * 安全地从 HttpServletRequest 中获取用户ID
     * 
     * @param request HTTP请求对象
     * @return 用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserIdSafely(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        return (Long) request.getAttribute(USER_ID_ATTRIBUTE);
    }
    
    /**
     * 检查当前用户是否已登录
     * 
     * @param request HTTP请求对象
     * @return true-已登录，false-未登录
     */
    public static boolean isUserLoggedIn(HttpServletRequest request) {
        return getCurrentUserIdSafely(request) != null;
    }
    
    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        // 检查代理和负载均衡器设置的头信息
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 可能有多个IP，取第一个
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 获取用户浏览器信息
     * 
     * @param request HTTP请求对象
     * @return User-Agent字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }
    
    /**
     * 私有构造方法，防止实例化
     */
    private RequestUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
