package group5.sebm.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    void testAddCorsMappings() {
        CorsConfig corsConfig = new CorsConfig();

        // 模拟 CorsRegistry
        TestCorsRegistry registry = new TestCorsRegistry();
        corsConfig.addCorsMappings(registry);

        // 验证 addMapping 是否被调用
        assertTrue(registry.mappingsAdded.contains("/**"));

        // 验证允许的 origins
        assertArrayEquals(
            new String[]{"http://localhost:5173", "https://sebm-fe-production.up.railway.app","https://sebm-fe-admin-production.up.railway.app","https://sebm-production.up.railway.app"},
            registry.allowedOrigins
        );

        // 验证允许的方法
        assertArrayEquals(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"}, registry.allowedMethods);

        // 验证允许的 headers
        assertEquals("*", registry.allowedHeaders);
        assertEquals("*", registry.exposedHeaders);

        // 验证 allowCredentials
        assertTrue(registry.allowCredentials);
    }

    // 简单模拟 CorsRegistry 用于测试
    static class TestCorsRegistry extends CorsRegistry {
        boolean allowCredentials = false;
        String[] allowedOrigins;
        String[] allowedMethods;
        String allowedHeaders;
        String exposedHeaders;
        java.util.List<String> mappingsAdded = new java.util.ArrayList<>();

        @Override
        public CorsRegistration addMapping(String pathPattern) {
            mappingsAdded.add(pathPattern);
            return new CorsRegistration(pathPattern) {
                @Override
                public CorsRegistration allowCredentials(boolean allowCredentials) {
                    TestCorsRegistry.this.allowCredentials = allowCredentials;
                    return this;
                }

                @Override
                public CorsRegistration allowedOrigins(String... origins) {
                    TestCorsRegistry.this.allowedOrigins = origins;
                    return this;
                }

                @Override
                public CorsRegistration allowedMethods(String... methods) {
                    TestCorsRegistry.this.allowedMethods = methods;
                    return this;
                }

                @Override
                public CorsRegistration allowedHeaders(String... headers) {
                    TestCorsRegistry.this.allowedHeaders = headers[0];
                    return this;
                }

                @Override
                public CorsRegistration exposedHeaders(String... headers) {
                    TestCorsRegistry.this.exposedHeaders = headers[0];
                    return this;
                }
            };
        }
    }
}
