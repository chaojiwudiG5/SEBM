package group5.sebm.notifiation;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * Service测试基类
 * 用于解决 MyBatis-Plus ServiceImpl 测试时 baseMapper 注入问题
 */
public abstract class BaseServiceTest {

    /**
     * 设置 Service 的 baseMapper
     * 
     * @param service Service 实例
     * @param baseMapper Mapper 实例
     */
    protected void setBaseMapper(Object service, Object baseMapper) {
        ReflectionTestUtils.setField(service, "baseMapper", baseMapper);
    }
    
    /**
     * 设置 Service 的任意字段
     * 
     * @param target 目标对象
     * @param name 字段名
     * @param value 字段值
     */
    protected void setField(Object target, String name, Object value) {
        ReflectionTestUtils.setField(target, name, value);
    }
}

