package group5.sebm.config;

import group5.sebm.notifiation.config.ListTypeHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.*;

class TypeHandlerConfigTest {

    private SqlSessionFactory sqlSessionFactory;
    private Configuration configuration;
    private TypeHandlerRegistry typeHandlerRegistry;
    private TypeHandlerConfig typeHandlerConfig;

    @BeforeEach
    void setUp() throws Exception {
        sqlSessionFactory = mock(SqlSessionFactory.class);
        configuration = mock(Configuration.class);
        typeHandlerRegistry = mock(TypeHandlerRegistry.class);

        when(sqlSessionFactory.getConfiguration()).thenReturn(configuration);
        when(configuration.getTypeHandlerRegistry()).thenReturn(typeHandlerRegistry);

        typeHandlerConfig = new TypeHandlerConfig();

        // 使用反射注入 private 字段
        Field field = TypeHandlerConfig.class.getDeclaredField("sqlSessionFactory");
        field.setAccessible(true);
        field.set(typeHandlerConfig, sqlSessionFactory);
    }

    @Test
    void testRun_registersListTypeHandler() throws Exception {
        typeHandlerConfig.run();

        // 验证 register 方法被调用
        verify(typeHandlerRegistry, times(1))
            .register(eq(List.class), any(ListTypeHandler.class));
    }
}
