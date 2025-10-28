package group5.sebm.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MybatisPlusConfigTest {

    @Test
    void testMybatisPlusInterceptor() {
        MybatisPlusConfig config = new MybatisPlusConfig();

        MybatisPlusInterceptor interceptor = config.mybatisPlusInterceptor();
        assertNotNull(interceptor, "Interceptor should not be null");

        // 验证 interceptor 内部包含 PaginationInnerInterceptor
        boolean hasPagination = interceptor.getInterceptors().stream()
                .anyMatch(inner -> inner instanceof PaginationInnerInterceptor);

        assertTrue(hasPagination, "Interceptor should contain PaginationInnerInterceptor");

        // 验证 PaginationInnerInterceptor 的 DbType 是 MYSQL
        PaginationInnerInterceptor pagination = interceptor.getInterceptors().stream()
                .filter(inner -> inner instanceof PaginationInnerInterceptor)
                .map(inner -> (PaginationInnerInterceptor) inner)
                .findFirst()
                .orElse(null);

        assertNotNull(pagination, "PaginationInnerInterceptor should not be null");
        assertEquals(DbType.MYSQL, pagination.getDbType(), "DbType should be MYSQL");
    }
}
