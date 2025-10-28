package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.exception.BusinessException;
import group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.dao.NotificationRecordMapper;
import group5.sebm.notifiation.dao.NotificationTaskMapper;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.entity.NotificationTaskPo;
import group5.sebm.notifiation.service.impl.NotificationRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NotificationRecordService单元测试
 */
@ExtendWith(MockitoExtension.class)
class NotificationRecordServiceImplTest {

    @Mock
    private NotificationRecordMapper notificationRecordMapper;

    @Mock
    private NotificationTaskMapper notificationTaskMapper;

    private NotificationRecordServiceImpl notificationRecordService;

    private NotificationRecordPo mockRecordPo;
    private NotificationTaskPo mockTaskPo;

    @BeforeEach
    void setUp() {
        // 使用 Spy 来 partial mock notificationRecordService
        notificationRecordService = Mockito.spy(new NotificationRecordServiceImpl(notificationRecordMapper, notificationTaskMapper));
        // 使用 ReflectionTestUtils 设置 baseMapper（继承自父类）
        ReflectionTestUtils.setField(notificationRecordService, "baseMapper", notificationRecordMapper);
        
        // 初始化Mock数据
        mockRecordPo = NotificationRecordPo.builder()
                .id(1L)
                .notificationTaskId(100L)
                .userId(200L)
                .notificationMethod(3) // 站内信
                .status(1) // 发送成功
                .readStatus(0) // 未读
                .isDelete(0)
                .sendTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        mockTaskPo = new NotificationTaskPo();
        mockTaskPo.setId(100L);
        mockTaskPo.setTitle("测试通知");
        mockTaskPo.setContent("测试内容");
        mockTaskPo.setNotificationRole(1);
        mockTaskPo.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testCreateRecord_Success() {
        // Arrange
        when(notificationRecordMapper.insert(any(NotificationRecordPo.class)))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.createRecord(100L, 200L, 3, 0);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).insert(any(NotificationRecordPo.class));
    }

    @Test
    void testCreateRecord_Failure() {
        // Arrange
        when(notificationRecordMapper.insert(any(NotificationRecordPo.class)))
                .thenReturn(0);

        // Act
        boolean result = notificationRecordService.createRecord(100L, 200L, 3, 0);

        // Assert
        assertFalse(result);
        verify(notificationRecordMapper, times(1)).insert(any(NotificationRecordPo.class));
    }

    @Test
    void testCreateRecord_Exception() {
        // Arrange
        when(notificationRecordMapper.insert(any(NotificationRecordPo.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        boolean result = notificationRecordService.createRecord(100L, 200L, 3, 0);

        // Assert
        assertFalse(result);
        verify(notificationRecordMapper, times(1)).insert(any(NotificationRecordPo.class));
    }

    @Test
    void testUpdateRecordStatus_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.updateRecordStatus(100L, 200L, 3, 1, null);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testUpdateRecordStatus_WithErrorMsg() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.updateRecordStatus(100L, 200L, 3, 2, "发送失败");

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testGetRecordsByTaskId_Success() {
        // Arrange
        List<NotificationRecordPo> mockRecords = Arrays.asList(mockRecordPo);
        when(notificationRecordMapper.selectList(any()))
                .thenReturn(mockRecords);

        // Act
        List<NotificationRecordPo> result = notificationRecordService.getRecordsByTaskId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRecordMapper, times(1)).selectList(any());
    }

    @Test
    void testGetRecordsByTaskId_Exception() {
        // Arrange
        when(notificationRecordMapper.selectList(any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        List<NotificationRecordPo> result = notificationRecordService.getRecordsByTaskId(100L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationRecordMapper, times(1)).selectList(any());
    }

    @Test
    void testGetRecord_Success() {
        // Arrange
        when(notificationRecordMapper.selectOne(any(), anyBoolean()))
                .thenReturn(mockRecordPo);

        // Act
        NotificationRecordPo result = notificationRecordService.getRecord(100L, 200L, 3);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationRecordMapper, times(1)).selectOne(any(), anyBoolean());
    }

    @Test
    void testGetRecord_NotFound() {
        // Arrange
        when(notificationRecordMapper.selectOne(any(), anyBoolean()))
                .thenReturn(null);

        // Act
        NotificationRecordPo result = notificationRecordService.getRecord(100L, 200L, 3);

        // Assert
        assertNull(result);
        verify(notificationRecordMapper, times(1)).selectOne(any(), anyBoolean());
    }

    @Test
    void testBatchCreateRecords_Success() {
        // Arrange
        List<Long> userIds = Arrays.asList(200L, 201L, 202L);
        List<Integer> methods = Arrays.asList(1, 3); // 邮件和站内信
        // Mock saveBatch 方法返回 true
        doReturn(true).when(notificationRecordService).saveBatch(anyList());

        // Act
        boolean result = notificationRecordService.batchCreateRecords(100L, userIds, methods, 0);

        // Assert
        assertTrue(result);
        // 验证 saveBatch 被调用了一次，参数是包含6条记录的列表
        verify(notificationRecordService, times(1)).saveBatch(argThat(list -> 
            list != null && list.size() == 6
        ));
    }

    @Test
    void testBatchCreateRecords_EmptyUserIds() {
        // Act
        boolean result = notificationRecordService.batchCreateRecords(100L, Arrays.asList(), Arrays.asList(1, 3), 0);

        // Assert
        assertFalse(result);
        verify(notificationRecordService, never()).saveBatch(anyList());
    }

    @Test
    void testBatchCreateRecords_NullUserIds() {
        // Act
        boolean result = notificationRecordService.batchCreateRecords(100L, null, Arrays.asList(1, 3), 0);

        // Assert
        assertFalse(result);
        verify(notificationRecordService, never()).saveBatch(anyList());
    }

    @Test
    void testMarkAsRead_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.markAsRead(1L);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testMarkAsRead_NotFound() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(0);

        // Act
        boolean result = notificationRecordService.markAsRead(1L);

        // Assert
        assertFalse(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testMarkAllAsRead_NormalUser_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.markAllAsRead(200L, 2); // 普通用户角色

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
        verify(notificationRecordMapper, never()).markAllAsReadByRole(anyInt());
    }

    @Test
    void testMarkAllAsRead_Admin_Success() {
        // Arrange
        when(notificationRecordMapper.markAllAsReadByRole(0))
                .thenReturn(5);

        // Act
        boolean result = notificationRecordService.markAllAsRead(200L, 1); // 管理员角色

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).markAllAsReadByRole(0);
    }

    @Test
    void testMarkAllAsRead_NullUserId() {
        // Act
        boolean result = notificationRecordService.markAllAsRead(null, 1);

        // Assert
        assertFalse(result);
        verify(notificationRecordMapper, never()).update(any(), any());
        verify(notificationRecordMapper, never()).markAllAsReadByRole(anyInt());
    }

    @Test
    void testDeleteRecord_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.deleteRecord(1L);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testBatchDeleteRecords_Success() {
        // Arrange
        List<Long> recordIds = Arrays.asList(1L, 2L, 3L);
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.batchDeleteRecords(recordIds);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testBatchDeleteRecords_EmptyList() {
        // Act
        boolean result = notificationRecordService.batchDeleteRecords(Arrays.asList());

        // Assert
        assertFalse(result);
        verify(notificationRecordMapper, never()).update(any(), any());
    }

    @Test
    void testClearUserReadRecords_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.clearUserReadRecords(200L);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testClearUserReadRecords_NullUserId() {
        // Act
        boolean result = notificationRecordService.clearUserReadRecords(null);

        // Assert
        assertFalse(result);
        verify(notificationRecordMapper, never()).update(any(), any());
    }

    @Test
    void testCountUnreadByUserId_Success() {
        // Arrange
        when(notificationRecordMapper.selectCount(any()))
                .thenReturn(5L);

        // Act
        long result = notificationRecordService.countUnreadByUserId(200L);

        // Assert
        assertEquals(5L, result);
        verify(notificationRecordMapper, times(1)).selectCount(any());
    }

    @Test
    void testCountUnreadByUserId_NoUnread() {
        // Arrange
        when(notificationRecordMapper.selectCount(any()))
                .thenReturn(0L);

        // Act
        long result = notificationRecordService.countUnreadByUserId(200L);

        // Assert
        assertEquals(0L, result);
        verify(notificationRecordMapper, times(1)).selectCount(any());
    }

    @Test
    void testCountUnreadByUserId_Exception() {
        // Arrange
        when(notificationRecordMapper.selectCount(any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        long result = notificationRecordService.countUnreadByUserId(200L);

        // Assert
        assertEquals(0L, result);
        verify(notificationRecordMapper, times(1)).selectCount(any());
    }

    @Test
    void testQueryNotificationRecords_NormalUser_Success() {
        // Arrange
        NotificationRecordQueryDto queryDto = new NotificationRecordQueryDto();
        queryDto.setUserId(200L);
        queryDto.setQueryRole(1); // 普通用户
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        Page<NotificationRecordPo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(mockRecordPo));
        mockPage.setTotal(1);

        when(notificationRecordMapper.selectPage(any(), any()))
                .thenReturn(mockPage);
        when(notificationTaskMapper.selectBatchIds(anyList()))
                .thenReturn(Arrays.asList(mockTaskPo));

        // Act
        Page<NotificationRecordVo> result = notificationRecordService.queryNotificationRecords(queryDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(notificationRecordMapper, times(1)).selectPage(any(), any());
    }

    @Test
    void testQueryNotificationRecords_Admin_Success() {
        // Arrange
        NotificationRecordQueryDto queryDto = new NotificationRecordQueryDto();
        queryDto.setQueryRole(0); // 管理员
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        Page<NotificationRecordPo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(mockRecordPo));
        mockPage.setTotal(1);

        mockTaskPo.setNotificationRole(0); // 管理员角色的通知

        when(notificationTaskMapper.selectList(any()))
                .thenReturn(Arrays.asList(mockTaskPo));
        when(notificationRecordMapper.selectPage(any(), any()))
                .thenReturn(mockPage);
        when(notificationTaskMapper.selectBatchIds(anyList()))
                .thenReturn(Arrays.asList(mockTaskPo));

        // Act
        Page<NotificationRecordVo> result = notificationRecordService.queryNotificationRecords(queryDto);

        // Assert
        assertNotNull(result);
        verify(notificationTaskMapper, times(1)).selectList(any());
        verify(notificationRecordMapper, times(1)).selectPage(any(), any());
    }

    @Test
    void testQueryNotificationRecords_NullUserId_ThrowsException() {
        // Arrange
        NotificationRecordQueryDto queryDto = new NotificationRecordQueryDto();
        queryDto.setUserId(null);
        queryDto.setQueryRole(1);
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            notificationRecordService.queryNotificationRecords(queryDto);
        });
    }

    @Test
    void testQueryNotificationRecords_WithFilters() {
        // Arrange
        NotificationRecordQueryDto queryDto = new NotificationRecordQueryDto();
        queryDto.setUserId(200L);
        queryDto.setQueryRole(1);
        queryDto.setReadStatus(0); // 只查未读
        queryDto.setStartTime(System.currentTimeMillis() / 1000 - 86400); // 1天前
        queryDto.setEndTime(System.currentTimeMillis() / 1000);
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        Page<NotificationRecordPo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(mockRecordPo));
        mockPage.setTotal(1);

        when(notificationRecordMapper.selectPage(any(), any()))
                .thenReturn(mockPage);
        when(notificationTaskMapper.selectBatchIds(anyList()))
                .thenReturn(Arrays.asList(mockTaskPo));

        // Act
        Page<NotificationRecordVo> result = notificationRecordService.queryNotificationRecords(queryDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    void testBatchMarkAsRead_Success() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.batchMarkAsRead(ids);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testBatchMarkAsRead_EmptyList_ThrowsException() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            notificationRecordService.batchMarkAsRead(Arrays.asList());
        });
    }

    @Test
    void testBatchMarkAsRead_NullList_ThrowsException() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            notificationRecordService.batchMarkAsRead(null);
        });
    }

    @Test
    void testDeleteNotificationRecord_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.deleteNotificationRecord(1L);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testBatchDeleteNotificationRecords_Success() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.batchDeleteNotificationRecords(ids);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }

    @Test
    void testClearUserNotifications_Success() {
        // Arrange
        when(notificationRecordMapper.update(any(), any()))
                .thenReturn(1);

        // Act
        boolean result = notificationRecordService.clearUserNotifications(200L);

        // Assert
        assertTrue(result);
        verify(notificationRecordMapper, times(1)).update(any(), any());
    }
}

