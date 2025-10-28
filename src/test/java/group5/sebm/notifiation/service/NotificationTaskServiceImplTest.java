package group5.sebm.notifiation.service;

import group5.sebm.notifiation.dao.NotificationTaskMapper;
import group5.sebm.notifiation.entity.NotificationTaskPo;
import group5.sebm.notifiation.service.impl.NotificationTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * NotificationTaskService单元测试
 */
@ExtendWith(MockitoExtension.class)
class NotificationTaskServiceImplTest {

    @Mock
    private NotificationTaskMapper notificationTaskMapper;

    private NotificationTaskServiceImpl notificationTaskService;

    @BeforeEach
    void setUp() {
        // 手动创建service实例并注入依赖
        notificationTaskService = Mockito.spy(new NotificationTaskServiceImpl());
        // 使用 ReflectionTestUtils 设置 baseMapper
        ReflectionTestUtils.setField(notificationTaskService, "baseMapper", notificationTaskMapper);
    }

    @Test
    void testCreateTask_Success() {
        // Arrange
        String title = "测试通知任务";
        String content = "这是测试内容";
        Integer notificationRole = 1;

        // Mock save方法返回成功
        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            // 模拟数据库自动生成的ID
            task.setId(100L);
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNotNull(taskId);
        assertEquals(100L, taskId);
        
        verify(notificationTaskService, times(1)).save(argThat(task -> {
            assertEquals(title, task.getTitle());
            assertEquals(content, task.getContent());
            assertEquals(notificationRole, task.getNotificationRole());
            assertNotNull(task.getCreateTime());
            assertNotNull(task.getUpdateTime());
            return true;
        }));
    }

    @Test
    void testCreateTask_NullContent() {
        // Arrange
        String title = "测试通知任务";
        String content = null; // content为null
        Integer notificationRole = 1;

        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            task.setId(200L);
            // 验证content被设置为空字符串
            assertEquals("", task.getContent());
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNotNull(taskId);
        assertEquals(200L, taskId);
        verify(notificationTaskService, times(1)).save(any(NotificationTaskPo.class));
    }

    @Test
    void testCreateTask_SaveFailed() {
        // Arrange
        String title = "测试通知任务";
        String content = "测试内容";
        Integer notificationRole = 1;

        // Mock save方法返回失败
        doReturn(false).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNull(taskId);
        verify(notificationTaskService, times(1)).save(any(NotificationTaskPo.class));
    }

    @Test
    void testCreateTask_Exception() {
        // Arrange
        String title = "测试通知任务";
        String content = "测试内容";
        Integer notificationRole = 1;

        // Mock save方法抛出异常
        doThrow(new RuntimeException("数据库异常"))
                .when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNull(taskId); // 异常时应该返回null
        verify(notificationTaskService, times(1)).save(any(NotificationTaskPo.class));
    }

    @Test
    void testCreateTask_EmptyTitle() {
        // Arrange
        String title = "";
        String content = "测试内容";
        Integer notificationRole = 1;

        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            task.setId(300L);
            assertEquals("", task.getTitle());
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNotNull(taskId);
        assertEquals(300L, taskId);
    }

    @Test
    void testCreateTask_EmptyContent() {
        // Arrange
        String title = "测试通知任务";
        String content = "";
        Integer notificationRole = 1;

        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            task.setId(400L);
            assertEquals("", task.getContent());
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNotNull(taskId);
        assertEquals(400L, taskId);
    }

    @Test
    void testCreateTask_DifferentRoles() {
        // Arrange
        String title = "测试通知任务";
        String content = "测试内容";

        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            task.setId(500L);
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act - 测试不同角色
        Long taskId1 = notificationTaskService.createTask(title, content, 1); // 管理员
        Long taskId2 = notificationTaskService.createTask(title, content, 2); // 借用者
        Long taskId3 = notificationTaskService.createTask(title, content, 3); // 维修员

        // Assert
        assertNotNull(taskId1);
        assertNotNull(taskId2);
        assertNotNull(taskId3);
        verify(notificationTaskService, times(3)).save(any(NotificationTaskPo.class));
    }

    @Test
    void testCreateTask_NullRole() {
        // Arrange
        String title = "测试通知任务";
        String content = "测试内容";
        Integer notificationRole = null;

        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            task.setId(800L);
            assertNull(task.getNotificationRole());
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNotNull(taskId);
        assertEquals(800L, taskId);
    }

    @Test
    void testCreateTask_VerifyTimestamps() {
        // Arrange
        String title = "测试通知任务";
        String content = "测试内容";
        Integer notificationRole = 1;

        doAnswer(invocation -> {
            NotificationTaskPo task = invocation.getArgument(0);
            task.setId(900L);
            // 验证时间戳不为空
            assertNotNull(task.getCreateTime());
            assertNotNull(task.getUpdateTime());
            return true;
        }).when(notificationTaskService).save(any(NotificationTaskPo.class));

        // Act
        Long taskId = notificationTaskService.createTask(title, content, notificationRole);

        // Assert
        assertNotNull(taskId);
        assertEquals(900L, taskId);
    }
}
