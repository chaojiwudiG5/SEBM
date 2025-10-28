package group5.sebm.notifiation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.notifiation.controller.dto.AdminNotificationQueryDto;
import group5.sebm.notifiation.controller.dto.BatchDeleteDto;
import group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.service.NotificationRecordService;
import group5.sebm.notifiation.service.UnsubscribeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知记录控制器测试
 */
@ExtendWith(MockitoExtension.class)
class NotificationRecordControllerTest {

    @Mock
    private NotificationRecordService notificationRecordService;

    @Mock
    private UnsubscribeService unsubscribeService;

    @InjectMocks
    private NotificationRecordController controller;

    private NotificationRecordQueryDto queryDto;
    private DeleteDto deleteDto;
    private BatchDeleteDto batchDeleteDto;
    private AdminNotificationQueryDto adminQueryDto;

    @BeforeEach
    void setUp() {
        queryDto = new NotificationRecordQueryDto();
        queryDto.setUserId(1L);
        queryDto.setPageSize(10);

        deleteDto = new DeleteDto();
        deleteDto.setId(1L);

        batchDeleteDto = new BatchDeleteDto();
        batchDeleteDto.setIds(Arrays.asList(1L, 2L, 3L));

        adminQueryDto = new AdminNotificationQueryDto();
        adminQueryDto.setUserId(1L);
        adminQueryDto.setPageSize(10);
    }

    @Test
    void testQueryNotificationRecords_Success() {
        // Given
        Page<NotificationRecordVo> mockPage = new Page<>(1, 10);
        mockPage.setTotal(5L);
        when(notificationRecordService.queryNotificationRecords(any(NotificationRecordQueryDto.class)))
                .thenReturn(mockPage);

        // When
        BaseResponse<Page<NotificationRecordVo>> response = controller.queryNotificationRecords(queryDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertNotNull(response.getData());
        assertEquals(5L, response.getData().getTotal());
        verify(notificationRecordService).queryNotificationRecords(queryDto);
    }

    @Test
    void testQueryNotificationRecords_Exception() {
        // Given
        when(notificationRecordService.queryNotificationRecords(any(NotificationRecordQueryDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        BaseResponse<Page<NotificationRecordVo>> response = controller.queryNotificationRecords(queryDto);

        // Then
        assertNotNull(response);
        verify(notificationRecordService).queryNotificationRecords(queryDto);
    }

    @Test
    void testDeleteNotificationRecord_Success() {
        // Given
        when(notificationRecordService.deleteNotificationRecord(anyLong())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.deleteNotificationRecord(deleteDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(notificationRecordService).deleteNotificationRecord(1L);
    }

    @Test
    void testDeleteNotificationRecord_Exception() {
        // Given
        when(notificationRecordService.deleteNotificationRecord(anyLong()))
                .thenThrow(new RuntimeException("Delete failed"));

        // When
        BaseResponse<Boolean> response = controller.deleteNotificationRecord(deleteDto);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(notificationRecordService).deleteNotificationRecord(1L);
    }

    @Test
    void testBatchDeleteNotificationRecords_Success() {
        // Given
        when(notificationRecordService.batchDeleteNotificationRecords(anyList())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.batchDeleteNotificationRecords(batchDeleteDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(notificationRecordService).batchDeleteNotificationRecords(batchDeleteDto.getIds());
    }

    @Test
    void testBatchDeleteNotificationRecords_Exception() {
        // Given
        when(notificationRecordService.batchDeleteNotificationRecords(anyList()))
                .thenThrow(new RuntimeException("Batch delete failed"));

        // When
        BaseResponse<Boolean> response = controller.batchDeleteNotificationRecords(batchDeleteDto);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(notificationRecordService).batchDeleteNotificationRecords(batchDeleteDto.getIds());
    }

    @Test
    void testClearUserNotifications_Success() {
        // Given
        when(notificationRecordService.clearUserNotifications(anyLong())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.clearUserNotifications(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(notificationRecordService).clearUserNotifications(1L);
    }

    @Test
    void testClearUserNotifications_Exception() {
        // Given
        when(notificationRecordService.clearUserNotifications(anyLong()))
                .thenThrow(new RuntimeException("Clear failed"));

        // When
        BaseResponse<Boolean> response = controller.clearUserNotifications(1L);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(notificationRecordService).clearUserNotifications(1L);
    }

    @Test
    void testCountNotifications_Success() {
        // Given
        when(notificationRecordService.count(any(QueryWrapper.class))).thenReturn(10L);

        // When
        BaseResponse<Long> response = controller.countNotifications(1L, null);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertEquals(10L, response.getData());
        verify(notificationRecordService).count(any(QueryWrapper.class));
    }

    @Test
    void testCountNotifications_WithStatus() {
        // Given
        when(notificationRecordService.count(any(QueryWrapper.class))).thenReturn(5L);

        // When
        BaseResponse<Long> response = controller.countNotifications(1L, 1);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertEquals(5L, response.getData());
        verify(notificationRecordService).count(any(QueryWrapper.class));
    }

    @Test
    void testCountNotifications_Exception() {
        // Given
        when(notificationRecordService.count(any(QueryWrapper.class)))
                .thenThrow(new RuntimeException("Count failed"));

        // When
        BaseResponse<Long> response = controller.countNotifications(1L, null);

        // Then
        assertNotNull(response);
        assertEquals(0L, response.getData());
        verify(notificationRecordService).count(any(QueryWrapper.class));
    }

    @Test
    void testGetUnreadCount_Success() {
        // Given
        when(notificationRecordService.count(any(QueryWrapper.class))).thenReturn(3L);

        // When
        BaseResponse<Long> response = controller.getUnreadCount(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertEquals(3L, response.getData());
        verify(notificationRecordService).count(any(QueryWrapper.class));
    }

    @Test
    void testGetUnreadCount_Exception() {
        // Given
        when(notificationRecordService.count(any(QueryWrapper.class)))
                .thenThrow(new RuntimeException("Count failed"));

        // When
        BaseResponse<Long> response = controller.getUnreadCount(1L);

        // Then
        assertNotNull(response);
        assertEquals(0L, response.getData());
        verify(notificationRecordService).count(any(QueryWrapper.class));
    }

    @Test
    void testMarkAsRead_Success() {
        // Given
        when(notificationRecordService.markAsRead(anyLong())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.markAsRead(deleteDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(notificationRecordService).markAsRead(1L);
    }

    @Test
    void testMarkAsRead_Exception() {
        // Given
        when(notificationRecordService.markAsRead(anyLong()))
                .thenThrow(new RuntimeException("Mark failed"));

        // When
        BaseResponse<Boolean> response = controller.markAsRead(deleteDto);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(notificationRecordService).markAsRead(1L);
    }

    @Test
    void testBatchMarkAsRead_Success() {
        // Given
        when(notificationRecordService.batchMarkAsRead(anyList())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.batchMarkAsRead(batchDeleteDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(notificationRecordService).batchMarkAsRead(batchDeleteDto.getIds());
    }

    @Test
    void testBatchMarkAsRead_Exception() {
        // Given
        when(notificationRecordService.batchMarkAsRead(anyList()))
                .thenThrow(new RuntimeException("Batch mark failed"));

        // When
        BaseResponse<Boolean> response = controller.batchMarkAsRead(batchDeleteDto);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(notificationRecordService).batchMarkAsRead(batchDeleteDto.getIds());
    }

    @Test
    void testMarkAllAsRead_Success() {
        // Given
        when(notificationRecordService.markAllAsRead(anyLong(), anyInt())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.markAllAsRead(1L, 1);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(notificationRecordService).markAllAsRead(1L, 1);
    }

    @Test
    void testMarkAllAsRead_Exception() {
        // Given
        when(notificationRecordService.markAllAsRead(anyLong(), anyInt()))
                .thenThrow(new RuntimeException("Mark all failed"));

        // When
        BaseResponse<Boolean> response = controller.markAllAsRead(1L, 1);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(notificationRecordService).markAllAsRead(1L, 1);
    }

    @Test
    void testQueryAllSentNotifications_Success() {
        // Given
        Page<NotificationRecordVo> mockPage = new Page<>(1, 10);
        mockPage.setTotal(20L);
        when(notificationRecordService.queryAllSentNotifications(any(AdminNotificationQueryDto.class)))
                .thenReturn(mockPage);

        // When
        BaseResponse<Page<NotificationRecordVo>> response = controller.queryAllSentNotifications(adminQueryDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertNotNull(response.getData());
        assertEquals(20L, response.getData().getTotal());
        verify(notificationRecordService).queryAllSentNotifications(adminQueryDto);
    }

    @Test
    void testQueryAllSentNotifications_Exception() {
        // Given
        when(notificationRecordService.queryAllSentNotifications(any(AdminNotificationQueryDto.class)))
                .thenThrow(new RuntimeException("Query failed"));

        // When
        BaseResponse<Page<NotificationRecordVo>> response = controller.queryAllSentNotifications(adminQueryDto);

        // Then
        assertNotNull(response);
        verify(notificationRecordService).queryAllSentNotifications(adminQueryDto);
    }

    @Test
    void testUnsubscribe_Success() {
        // Given
        when(unsubscribeService.unsubscribe(anyLong(), anyInt())).thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.unsubscribe(1L, 1001);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(unsubscribeService).unsubscribe(1L, 1001);
    }

    @Test
    void testUnsubscribe_Exception() {
        // Given
        when(unsubscribeService.unsubscribe(anyLong(), anyInt()))
                .thenThrow(new RuntimeException("Unsubscribe failed"));

        // When
        BaseResponse<Boolean> response = controller.unsubscribe(1L, 1001);

        // Then
        assertNotNull(response);
        assertFalse(response.getData());
        verify(unsubscribeService).unsubscribe(1L, 1001);
    }
}

