package group5.sebm.notifiation.converter;

import group5.sebm.exception.BusinessException;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.service.dto.TemplateDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 模板转换器测试
 */
@ExtendWith(MockitoExtension.class)
class TemplateConverterTest {

    @Mock
    private HttpServletRequest request;

    private final TemplateConverter converter = Mappers.getMapper(TemplateConverter.class);

    private CreateTemplateDto createDto;
    private TemplatePo templatePo;

    @BeforeEach
    void setUp() {
        createDto = new CreateTemplateDto();
        createDto.setNotificationEvent(1001);
        createDto.setNotificationNode(1);
        createDto.setNotificationRole(1);
        createDto.setTemplateTitle("Test Template");
        createDto.setContent("Test Content");
        createDto.setTemplateDesc("Test Description");
        createDto.setNotificationMethod(Arrays.asList(1));
        createDto.setRelateTimeOffset(3600);

        templatePo = new TemplatePo();
        templatePo.setId(1L);
        templatePo.setNotificationEvent(1001);
        templatePo.setNotificationNode("1");
        templatePo.setNotificationRole(1);
        templatePo.setTemplateTitle("Test Template");
        templatePo.setTemplateContent("Test Content");
        templatePo.setTemplateDesc("Test Description");
        templatePo.setNotificationMethod(Arrays.asList(1));
        templatePo.setRelateTimeOffset(3600L);
        templatePo.setUserId(1L);
        templatePo.setStatus("1");
        templatePo.setIsDelete(0);
        templatePo.setCreateTime(LocalDateTime.now());
        templatePo.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testGetCurrentTime() {
        // When
        LocalDateTime time1 = converter.getCurrentTime();
        LocalDateTime time2 = converter.getCurrentTime();

        // Then
        assertNotNull(time1);
        assertNotNull(time2);
        assertTrue(time1.isBefore(time2) || time1.isEqual(time2));
    }

    @Test
    void testIntegerToString_ValidValue() {
        // When
        String result = converter.integerToString(123);

        // Then
        assertEquals("123", result);
    }

    @Test
    void testIntegerToString_NullValue() {
        // When
        String result = converter.integerToString(null);

        // Then
        assertNull(result);
    }

    @Test
    void testIntegerToLong_ValidValue() {
        // When
        Long result = converter.integerToLong(123);

        // Then
        assertEquals(123L, result);
    }

    @Test
    void testIntegerToLong_NullValue() {
        // When
        Long result = converter.integerToLong(null);

        // Then
        assertNull(result);
    }

    @Test
    void testGetUserIdFromRequest_ValidUserId() {
        // Given
        when(request.getAttribute("userId")).thenReturn(1L);

        // When
        Long userId = converter.getUserIdFromRequest(request);

        // Then
        assertEquals(1L, userId);
        verify(request).getAttribute("userId");
    }

    @Test
    void testGetUserIdFromRequest_NullUserId() {
        // Given
        when(request.getAttribute("userId")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            converter.getUserIdFromRequest(request);
        });
        verify(request).getAttribute("userId");
    }

    @Test
    void testToPo() {
        // Given
        when(request.getAttribute("userId")).thenReturn(1L);

        // When
        TemplatePo result = converter.toPo(createDto, request);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getNotificationNode());
        assertEquals(1, result.getNotificationRole());
        assertEquals("Test Template", result.getTemplateTitle());
        assertEquals("Test Content", result.getTemplateContent());
        assertEquals("Test Description", result.getTemplateDesc());
        assertEquals(Arrays.asList(1), result.getNotificationMethod());
        assertEquals(3600L, result.getRelateTimeOffset());
        assertEquals(1L, result.getUserId());
        assertEquals("1", result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
    }

    @Test
    void testToVo() {
        // When
        TemplateVo result = converter.toVo(templatePo);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Template", result.getTemplateTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals("Test Description", result.getTemplateDesc());
        assertEquals(Arrays.asList(1), result.getNotificationMethod());
    }

    @Test
    void testToDto() {
        // When
        TemplateDto result = converter.toDto(templatePo);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Template", result.getTemplateTitle());
        assertEquals("Test Content", result.getTemplateContent());
        assertEquals("Test Description", result.getTemplateDesc());
        assertEquals(Arrays.asList(1), result.getNotificationMethod());
        assertEquals(3600L, result.getRelateTimeOffset());
    }
}

