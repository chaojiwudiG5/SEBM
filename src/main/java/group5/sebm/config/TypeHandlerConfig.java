package group5.sebm.config;

import group5.sebm.notifiation.config.ListTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类型处理器配置类
 * 使用 CommandLineRunner 在应用启动完成后注册类型处理器
 */
@Component
public class TypeHandlerConfig implements CommandLineRunner {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void run(String... args) throws Exception {
        // 在应用启动完成后注册 ListTypeHandler
        sqlSessionFactory.getConfiguration().getTypeHandlerRegistry()
                .register(List.class, new ListTypeHandler());
    }
}




