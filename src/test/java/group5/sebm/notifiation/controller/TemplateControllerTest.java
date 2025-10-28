package group5.sebm.notifiation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.common.BaseResponse;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.dto.TemplateQueryDto;
import group5.sebm.notifiation.controller.dto.UpdateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.service.TemplateService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 模板控制器测试
 */
@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TemplateController controller;

    private CreateTemplateDto createDto;
    private UpdateTemplateDto updateDto;
    private TemplateQueryDto queryDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateTemplateDto();
        createDto.setNotificationEvent(1001);
        createDto.setNotificationNode(1);
        createDto.setNotificationRole(1);
        createDto.setTemplateTitle("Test Template");
        createDto.setContent("Test Content");
        createDto.setNotificationMethod(Arrays.asList(1));
        createDto.setRelateTimeOffset(3600);

        updateDto = new UpdateTemplateDto();
        updateDto.setId(1L);
        updateDto.setNotificationEvent(1001);
        updateDto.setNotificationNode(1);
        updateDto.setNotificationRole(1);
        updateDto.setTemplateTitle("Updated Template");
        updateDto.setContent("Updated Content");
        updateDto.setNotificationMethod(Arrays.asList(1));
        updateDto.setRelateTimeOffset(7200);

        queryDto = new TemplateQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);
    }

    @Test
    void testCreateTemplate_Success() {
        // Given
        TemplateVo mockVo = new TemplateVo();
        mockVo.setId(1L);
        mockVo.setTemplateTitle("Test Template");
        when(templateService.createTemplate(any(CreateTemplateDto.class), any(HttpServletRequest.class)))
                .thenReturn(mockVo);

        // When
        BaseResponse<TemplateVo> response = controller.createTemplate(createDto, request);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getId());
        assertEquals("Test Template", response.getData().getTemplateTitle());
        verify(templateService).createTemplate(createDto, request);
    }

    @Test
    void testCreateTemplate_InvalidNotificationNode() {
        // Given
        createDto.setNotificationNode(999); // Invalid node

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_InvalidNotificationRole() {
        // Given
        createDto.setNotificationRole(999); // Invalid role

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_EmptyTitle() {
        // Given
        createDto.setTemplateTitle("");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_TitleTooLong() {
        // Given
        createDto.setTemplateTitle("a".repeat(300)); // Exceeds max length

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_EmptyContent() {
        // Given
        createDto.setContent("");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_ContentTooLong() {
        // Given
        createDto.setContent("a".repeat(2000)); // Exceeds max length

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_InvalidTimeOffset_Negative() {
        // Given
        createDto.setRelateTimeOffset(-1);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_InvalidTimeOffset_TooLarge() {
        // Given
        createDto.setRelateTimeOffset(999999999); // Exceeds max

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testCreateTemplate_InvalidNotificationMethod() {
        // Given
        createDto.setNotificationMethod(Arrays.asList(999)); // Invalid method

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.createTemplate(createDto, request);
        });
    }

    @Test
    void testDisableTemplate_Success() {
        // Given
        when(templateService.disableTemplate(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.disableTemplate(1L, request);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(templateService).disableTemplate(1L, request);
    }

    @Test
    void testDisableTemplate_NullId() {
        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.disableTemplate(null, request);
        });
    }

    @Test
    void testDisableTemplate_InvalidId() {
        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.disableTemplate(0L, request);
        });
    }

    @Test
    void testEnableTemplate_Success() {
        // Given
        when(templateService.enableTemplate(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(true);

        // When
        BaseResponse<Boolean> response = controller.enableTemplate(1L, request);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertTrue(response.getData());
        verify(templateService).enableTemplate(1L, request);
    }

    @Test
    void testEnableTemplate_NullId() {
        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.enableTemplate(null, request);
        });
    }

    @Test
    void testEnableTemplate_InvalidId() {
        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.enableTemplate(-1L, request);
        });
    }

    @Test
    void testUpdateTemplate_Success() {
        // Given
        TemplateVo mockVo = new TemplateVo();
        mockVo.setId(1L);
        mockVo.setTemplateTitle("Updated Template");
        when(templateService.updateTemplate(any(UpdateTemplateDto.class), any(HttpServletRequest.class)))
                .thenReturn(mockVo);

        // When
        BaseResponse<TemplateVo> response = controller.updateTemplate(updateDto, request);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getId());
        assertEquals("Updated Template", response.getData().getTemplateTitle());
        verify(templateService).updateTemplate(updateDto, request);
    }

    @Test
    void testUpdateTemplate_NullId() {
        // Given
        updateDto.setId(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_InvalidId() {
        // Given
        updateDto.setId(0L);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_InvalidNotificationNode() {
        // Given
        updateDto.setNotificationNode(999);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_InvalidNotificationRole() {
        // Given
        updateDto.setNotificationRole(999);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_EmptyTitle() {
        // Given
        updateDto.setTemplateTitle("");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_TitleTooLong() {
        // Given
        updateDto.setTemplateTitle("a".repeat(300));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_EmptyContent() {
        // Given
        updateDto.setContent("");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_ContentTooLong() {
        // Given
        updateDto.setContent("a".repeat(2000));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_InvalidTimeOffset_Negative() {
        // Given
        updateDto.setRelateTimeOffset(-1);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_InvalidTimeOffset_TooLarge() {
        // Given
        updateDto.setRelateTimeOffset(999999999);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testUpdateTemplate_InvalidNotificationMethod() {
        // Given
        updateDto.setNotificationMethod(Arrays.asList(999));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            controller.updateTemplate(updateDto, request);
        });
    }

    @Test
    void testGetTemplateList_Success() {
        // Given
        Page<TemplateVo> mockPage = new Page<>(1, 10);
        mockPage.setTotal(5L);
        when(templateService.getTemplateList(any(TemplateQueryDto.class)))
                .thenReturn(mockPage);

        // When
        BaseResponse<Page<TemplateVo>> response = controller.getTemplateList(queryDto);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertNotNull(response.getData());
        assertEquals(5L, response.getData().getTotal());
        verify(templateService).getTemplateList(queryDto);
    }

    @Test
    void testGetTemplateDetail_ReturnsNull() {
        // When
        BaseResponse<TemplateVo> response = controller.getTemplateDetail(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getCode() >= 0);
        assertNull(response.getData());
    }
}

