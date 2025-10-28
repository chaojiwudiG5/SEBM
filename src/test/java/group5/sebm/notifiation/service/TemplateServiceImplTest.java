package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.common.constant.NotificationConstant;
import group5.sebm.exception.BusinessException;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.dto.TemplateQueryDto;
import group5.sebm.notifiation.controller.dto.UpdateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.converter.TemplateConverter;
import group5.sebm.notifiation.dao.TemplateMapper;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.service.dto.TemplateDto;
import group5.sebm.notifiation.service.impl.TemplateServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TemplateService单元测试
 */
@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {

    @Mock
    private TemplateMapper templateMapper;

    @Mock
    private TemplateConverter templateConverter;

    @Mock
    private HttpServletRequest request;

    private TemplateServiceImpl templateService;

    private TemplatePo mockTemplatePo;
    private TemplateVo mockTemplateVo;
    private CreateTemplateDto mockCreateDto;
    private UpdateTemplateDto mockUpdateDto;

    @BeforeEach
    void setUp() {
        // 手动创建 service 实例并注入依赖
        templateService = new TemplateServiceImpl();
        // 使用 ReflectionTestUtils 设置 baseMapper（继承自父类）
        ReflectionTestUtils.setField(templateService, "baseMapper", templateMapper);
        // 注入 templateConverter
        ReflectionTestUtils.setField(templateService, "templateConverter", templateConverter);
        // 初始化Mock数据
        mockTemplatePo = new TemplatePo();
        mockTemplatePo.setId(1L);
        mockTemplatePo.setTemplateTitle("测试模板");
        mockTemplatePo.setNotificationMethod(Arrays.asList(1, 3));
        mockTemplatePo.setNotificationNode("BORROW_REQUEST_SUCCESS");
        mockTemplatePo.setNotificationRole(1);
        mockTemplatePo.setNotificationType(0);
        mockTemplatePo.setNotificationEvent(1001);
        mockTemplatePo.setRelateTimeOffset(0L);
        mockTemplatePo.setTemplateContent("测试内容");
        mockTemplatePo.setTemplateDesc("测试描述");
        mockTemplatePo.setStatus(NotificationConstant.TEMPLATE_STATUS_ACTIVE);
        mockTemplatePo.setIsDelete(NotificationConstant.NOT_DELETED);
        mockTemplatePo.setUserId(100L);
        mockTemplatePo.setCreateTime(LocalDateTime.now());
        mockTemplatePo.setUpdateTime(LocalDateTime.now());

        mockTemplateVo = new TemplateVo();
        mockTemplateVo.setId(1L);
        mockTemplateVo.setTemplateTitle("测试模板");
        mockTemplateVo.setContent("测试内容");

        mockCreateDto = new CreateTemplateDto();
        mockCreateDto.setTemplateTitle("新模板");
        mockCreateDto.setNotificationMethod(Arrays.asList(1, 3));
        mockCreateDto.setNotificationNode(1);
        mockCreateDto.setNotificationRole(1);
        mockCreateDto.setNotificationType(0);
        mockCreateDto.setNotificationEvent(1001);
        mockCreateDto.setRelateTimeOffset(0);
        mockCreateDto.setContent("新内容");
        mockCreateDto.setTemplateDesc("新描述");

        mockUpdateDto = new UpdateTemplateDto();
        mockUpdateDto.setId(1L);
        mockUpdateDto.setTemplateTitle("更新模板");
        mockUpdateDto.setNotificationMethod(Arrays.asList(1, 3));
        mockUpdateDto.setNotificationNode(1);
        mockUpdateDto.setNotificationRole(1);
        mockUpdateDto.setNotificationType(0);
        mockUpdateDto.setNotificationEvent(1001);
        mockUpdateDto.setRelateTimeOffset(0);
        mockUpdateDto.setContent("更新内容");
        mockUpdateDto.setTemplateDesc("更新描述");
    }

    @Test
    void testCreateTemplate_Success() {
        // Arrange
        when(templateConverter.toPo(any(CreateTemplateDto.class), any(HttpServletRequest.class)))
                .thenReturn(mockTemplatePo);
        when(templateConverter.toVo(any(TemplatePo.class)))
                .thenReturn(mockTemplateVo);
        when(templateMapper.insert(any(TemplatePo.class)))
                .thenReturn(1);

        // Act
        TemplateVo result = templateService.createTemplate(mockCreateDto, request);

        // Assert
        assertNotNull(result);
        assertEquals(mockTemplateVo.getId(), result.getId());
        verify(templateMapper, times(1)).insert(any(TemplatePo.class));
        verify(templateConverter, times(1)).toPo(any(CreateTemplateDto.class), any(HttpServletRequest.class));
        verify(templateConverter, times(1)).toVo(any(TemplatePo.class));
    }

    @Test
    void testCreateTemplate_Failure() {
        // Arrange
        when(templateConverter.toPo(any(CreateTemplateDto.class), any(HttpServletRequest.class)))
                .thenReturn(mockTemplatePo);
        when(templateMapper.insert(any(TemplatePo.class)))
                .thenReturn(0);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            templateService.createTemplate(mockCreateDto, request);
        });
        verify(templateMapper, times(1)).insert(any(TemplatePo.class));
    }

    @Test
    void testDisableTemplate_Success() {
        // Arrange
        when(templateMapper.selectById(1L))
                .thenReturn(mockTemplatePo);
        when(templateMapper.updateById(any(TemplatePo.class)))
                .thenReturn(1);

        // Act
        Boolean result = templateService.disableTemplate(1L, request);

        // Assert
        assertTrue(result);
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, times(1)).updateById(any(TemplatePo.class));
    }

    @Test
    void testDisableTemplate_NotFound() {
        // Arrange
        when(templateMapper.selectById(1L))
                .thenReturn(null);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            templateService.disableTemplate(1L, request);
        });
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, never()).updateById(any(TemplatePo.class));
    }

    @Test
    void testDisableTemplate_AlreadyDisabled() {
        // Arrange
        mockTemplatePo.setStatus(NotificationConstant.TEMPLATE_STATUS_DISABLED);
        when(templateMapper.selectById(1L))
                .thenReturn(mockTemplatePo);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            templateService.disableTemplate(1L, request);
        });
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, never()).updateById(any(TemplatePo.class));
    }

    @Test
    void testEnableTemplate_Success() {
        // Arrange
        mockTemplatePo.setStatus(NotificationConstant.TEMPLATE_STATUS_DISABLED);
        when(templateMapper.selectById(1L))
                .thenReturn(mockTemplatePo);
        when(templateMapper.updateById(any(TemplatePo.class)))
                .thenReturn(1);

        // Act
        Boolean result = templateService.enableTemplate(1L, request);

        // Assert
        assertTrue(result);
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, times(1)).updateById(any(TemplatePo.class));
    }

    @Test
    void testEnableTemplate_AlreadyEnabled() {
        // Arrange
        when(templateMapper.selectById(1L))
                .thenReturn(mockTemplatePo);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            templateService.enableTemplate(1L, request);
        });
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, never()).updateById(any(TemplatePo.class));
    }

    @Test
    void testUpdateTemplate_Success() {
        // Arrange
        when(templateMapper.selectById(1L))
                .thenReturn(mockTemplatePo);
        when(templateMapper.updateById(any(TemplatePo.class)))
                .thenReturn(1);
        when(templateConverter.toVo(any(TemplatePo.class)))
                .thenReturn(mockTemplateVo);

        // Act
        TemplateVo result = templateService.updateTemplate(mockUpdateDto, request);

        // Assert
        assertNotNull(result);
        verify(templateMapper, times(2)).selectById(1L); // 一次验证存在，一次获取更新后的数据
        verify(templateMapper, times(1)).updateById(any(TemplatePo.class));
    }

    @Test
    void testUpdateTemplate_NotFound() {
        // Arrange
        when(templateMapper.selectById(1L))
                .thenReturn(null);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            templateService.updateTemplate(mockUpdateDto, request);
        });
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, never()).updateById(any(TemplatePo.class));
    }

    @Test
    void testUpdateTemplate_Deleted() {
        // Arrange
        mockTemplatePo.setIsDelete(1);
        when(templateMapper.selectById(1L))
                .thenReturn(mockTemplatePo);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            templateService.updateTemplate(mockUpdateDto, request);
        });
        verify(templateMapper, times(1)).selectById(1L);
        verify(templateMapper, never()).updateById(any(TemplatePo.class));
    }

    @Test
    void testGetTemplateList_Success() {
        // Arrange
        TemplateQueryDto queryDto = new TemplateQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        Page<TemplatePo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(mockTemplatePo));
        mockPage.setTotal(1);

        when(templateMapper.selectPage(any(Page.class), any()))
                .thenReturn(mockPage);
        when(templateConverter.toVo(any(TemplatePo.class)))
                .thenReturn(mockTemplateVo);

        // Act
        Page<TemplateVo> result = templateService.getTemplateList(queryDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(templateMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    void testFindTemplateByParams_Success() {
        // Arrange
        TemplateDto mockTemplateDto = new TemplateDto();
        mockTemplateDto.setId(1L);
        mockTemplateDto.setTemplateTitle("测试模板");
        mockTemplateDto.setTemplateContent("测试内容");

        when(templateMapper.selectOne(any(), anyBoolean()))
                .thenReturn(mockTemplatePo);
        when(templateConverter.toDto(any(TemplatePo.class)))
                .thenReturn(mockTemplateDto);

        // Act
        TemplateDto result = templateService.findTemplateByParams(1001);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试模板", result.getTemplateTitle());
        verify(templateMapper, times(1)).selectOne(any(), anyBoolean());
        verify(templateConverter, times(1)).toDto(any(TemplatePo.class));
    }

    @Test
    void testFindTemplateByParams_NotFound() {
        // Arrange
        when(templateMapper.selectOne(any(), anyBoolean()))
                .thenReturn(null);

        // Act
        TemplateDto result = templateService.findTemplateByParams(1001);

        // Assert
        assertNull(result);
        verify(templateMapper, times(1)).selectOne(any(), anyBoolean());
        verify(templateConverter, never()).toDto(any(TemplatePo.class));
    }

    @Test
    void testGetTemplateList_WithFilters() {
        // Arrange
        TemplateQueryDto queryDto = new TemplateQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);
        queryDto.setTemplateTitle("测试");
        queryDto.setNotificationNode(1);
        queryDto.setNotificationRole(1);
        queryDto.setStatus(1);

        Page<TemplatePo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(mockTemplatePo));
        mockPage.setTotal(1);

        when(templateMapper.selectPage(any(Page.class), any()))
                .thenReturn(mockPage);
        when(templateConverter.toVo(any(TemplatePo.class)))
                .thenReturn(mockTemplateVo);

        // Act
        Page<TemplateVo> result = templateService.getTemplateList(queryDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(templateMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    void testGetTemplateList_EmptyResult() {
        // Arrange
        TemplateQueryDto queryDto = new TemplateQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        Page<TemplatePo> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList());
        mockPage.setTotal(0);

        when(templateMapper.selectPage(any(Page.class), any()))
                .thenReturn(mockPage);

        // Act
        Page<TemplateVo> result = templateService.getTemplateList(queryDto);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
        verify(templateMapper, times(1)).selectPage(any(Page.class), any());
    }
}

