package group5.sebm.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.junit.jupiter.api.Assertions.*;

class RedisConfigTest {

    @Test
    void testCacheManager() {
        // 模拟 RedisConnectionFactory
        RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
        RedisConfig config = new RedisConfig();

        RedisCacheManager cacheManager = config.cacheManager(factory);

        assertNotNull(cacheManager, "RedisCacheManager should not be null");

        // 不能访问 getCacheWriter()，验证方法：通过公共 API 判断缓存管理器可用
        assertDoesNotThrow(() -> cacheManager.getCacheNames(),
            "RedisCacheManager should allow getCacheNames without exception");
    }

    @Test
    void testRedisTemplate() {
        RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
        RedisConfig config = new RedisConfig();

        RedisTemplate<String, Object> template = config.redisTemplate(factory);

        assertNotNull(template, "RedisTemplate should not be null");
        assertEquals(factory, template.getConnectionFactory(), "RedisConnectionFactory should match");

        // 验证 key / hashKey 序列化器
        assertTrue(template.getKeySerializer() instanceof StringRedisSerializer, "Key serializer should be StringRedisSerializer");
        assertTrue(template.getHashKeySerializer() instanceof StringRedisSerializer, "HashKey serializer should be StringRedisSerializer");

        // 验证 value / hashValue 序列化器
        assertTrue(template.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer, "Value serializer should be GenericJackson2JsonRedisSerializer");
        assertTrue(template.getHashValueSerializer() instanceof GenericJackson2JsonRedisSerializer, "HashValue serializer should be GenericJackson2JsonRedisSerializer");
    }
}
