package group5.sebm.notifiation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NotificationRateLimiter单元测试
 */
@ExtendWith(MockitoExtension.class)
class NotificationRateLimiterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private NotificationRateLimiter rateLimiter;

    /**
     * 设置通用的Redis Mock
     */
    private void setupRedisMock() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void testAllowNotification_Success() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(5L); // 当前5个请求，小于10的限制
        when(zSetOperations.add(anyString(), any(), anyDouble()))
                .thenReturn(true);
        when(redisTemplate.expire(anyString(), any(Duration.class)))
                .thenReturn(true);

        // Act
        boolean result = rateLimiter.allowNotification(userId);

        // Assert
        assertTrue(result);
        verify(zSetOperations, times(3)).removeRangeByScore(anyString(), anyDouble(), anyDouble());
        verify(zSetOperations, times(3)).zCard(anyString());
        verify(zSetOperations, times(3)).add(anyString(), any(), anyDouble());
    }

    @Test
    void testAllowNotification_MinuteRateLimitExceeded() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(10L); // 已经达到每分钟10个的限制

        // Act
        boolean result = rateLimiter.allowNotification(userId);

        // Assert
        assertFalse(result);
        verify(zSetOperations, times(1)).removeRangeByScore(anyString(), anyDouble(), anyDouble());
        verify(zSetOperations, times(1)).zCard(anyString());
        verify(zSetOperations, never()).add(anyString(), any(), anyDouble());
    }

    @Test
    void testAllowNotification_HourRateLimitExceeded() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        // 分钟级别通过（5个请求），小时级别超限（100个请求）
        when(zSetOperations.zCard(anyString()))
                .thenReturn(5L)    // 分钟级别
                .thenReturn(100L); // 小时级别已满

        // Act
        boolean result = rateLimiter.allowNotification(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testAllowNotification_DayRateLimitExceeded() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        // 分钟和小时级别通过，天级别超限
        when(zSetOperations.zCard(anyString()))
                .thenReturn(5L)    // 分钟级别
                .thenReturn(50L)   // 小时级别
                .thenReturn(500L); // 天级别已满

        // Act
        boolean result = rateLimiter.allowNotification(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testAllowNotification_NullUserId() {
        // Act
        boolean result = rateLimiter.allowNotification(null);

        // Assert
        assertFalse(result);
        verify(redisTemplate, never()).opsForZSet();
    }

    @Test
    void testAllowNotification_NullCountFromRedis() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(null); // Redis返回null
        when(zSetOperations.add(anyString(), any(), anyDouble()))
                .thenReturn(true);
        when(redisTemplate.expire(anyString(), any(Duration.class)))
                .thenReturn(true);

        // Act
        boolean result = rateLimiter.allowNotification(userId);

        // Assert
        assertTrue(result);
        verify(zSetOperations, times(3)).add(anyString(), any(), anyDouble());
    }

    @Test
    void testAllowNotification_RedisException() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("Redis连接失败"));

        // Act
        boolean result = rateLimiter.allowNotification(userId);

        // Assert
        // 异常情况下应该允许通过，避免影响业务
        assertTrue(result);
    }

    @Test
    void testGetRemainingQuotaPerMinute_Success() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(7L); // 已使用7个

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerMinute(userId);

        // Assert
        assertEquals(3L, remaining); // 10 - 7 = 3
    }

    @Test
    void testGetRemainingQuotaPerMinute_FullQuota() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(0L); // 未使用

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerMinute(userId);

        // Assert
        assertEquals(10L, remaining); // 全部配额可用
    }

    @Test
    void testGetRemainingQuotaPerMinute_NoQuota() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(10L); // 已用完

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerMinute(userId);

        // Assert
        assertEquals(0L, remaining); // 无配额
    }

    @Test
    void testGetRemainingQuotaPerMinute_Exceeded() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(15L); // 超过限制

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerMinute(userId);

        // Assert
        assertEquals(0L, remaining); // 返回0，不返回负数
    }

    @Test
    void testGetRemainingQuotaPerMinute_Exception() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("Redis异常"));

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerMinute(userId);

        // Assert
        assertEquals(10L, remaining); // 异常时返回最大配额
    }

    @Test
    void testGetRemainingQuotaPerHour_Success() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(80L); // 已使用80个

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerHour(userId);

        // Assert
        assertEquals(20L, remaining); // 100 - 80 = 20
    }

    @Test
    void testGetRemainingQuotaPerDay_Success() {
        // Arrange
        Long userId = 100L;
        setupRedisMock();
        when(zSetOperations.removeRangeByScore(anyString(), anyDouble(), anyDouble()))
                .thenReturn(0L);
        when(zSetOperations.zCard(anyString()))
                .thenReturn(450L); // 已使用450个

        // Act
        long remaining = rateLimiter.getRemainingQuotaPerDay(userId);

        // Assert
        assertEquals(50L, remaining); // 500 - 450 = 50
    }

    @Test
    void testClearRateLimit_Success() {
        // Arrange
        Long userId = 100L;
        Set<String> keys = new HashSet<>();
        keys.add("rate_limit:notification:100:60");
        keys.add("rate_limit:notification:100:3600");
        keys.add("rate_limit:notification:100:86400");
        
        when(redisTemplate.keys(anyString())).thenReturn(keys);
        when(redisTemplate.delete(keys)).thenReturn(3L);

        // Act
        rateLimiter.clearRateLimit(userId);

        // Assert
        verify(redisTemplate, times(1)).keys("rate_limit:notification:100:*");
        verify(redisTemplate, times(1)).delete(keys);
    }

    @Test
    void testClearRateLimit_Exception() {
        // Arrange
        Long userId = 100L;
        when(redisTemplate.keys(anyString()))
                .thenThrow(new RuntimeException("Redis异常"));

        // Act & Assert - 不应该抛出异常
        assertDoesNotThrow(() -> rateLimiter.clearRateLimit(userId));
    }
}
