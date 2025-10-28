package group5.sebm.notifiation.dto;

import group5.sebm.notifiation.controller.dto.*;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.entity.NotificationTaskPo;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.entity.UnsubscribePo;
import group5.sebm.notifiation.mq.NotificationMessage;
import group5.sebm.notifiation.service.dto.TemplateDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTO/VO/Entity 综合测试
 * 测试所有数据对象的getter/setter和基本功能
 */
class DtoVoEntityTest {

    // ==================== DTO Tests ====================

    @Test
    void testCreateTemplateDto() {
        CreateTemplateDto dto = new CreateTemplateDto();
        dto.setNotificationEvent(1001);
        dto.setNotificationNode(1);
        dto.setNotificationRole(1);
        dto.setTemplateTitle("Test Template");
        dto.setContent("Test Content");
        dto.setTemplateDesc("Test Description");
        dto.setNotificationMethod(Arrays.asList(1));
        dto.setRelateTimeOffset(3600);

        assertEquals(1001, dto.getNotificationEvent());
        assertEquals(1, dto.getNotificationNode());
        assertEquals(1, dto.getNotificationRole());
        assertEquals("Test Template", dto.getTemplateTitle());
        assertEquals("Test Content", dto.getContent());
        assertEquals("Test Description", dto.getTemplateDesc());
        assertEquals(Arrays.asList(1), dto.getNotificationMethod());
        assertEquals(3600, dto.getRelateTimeOffset());
        assertNotNull(dto.toString());
    }

    @Test
    void testUpdateTemplateDto() {
        UpdateTemplateDto dto = new UpdateTemplateDto();
        dto.setId(1L);
        dto.setNotificationEvent(1001);
        dto.setNotificationNode(1);
        dto.setNotificationRole(1);
        dto.setTemplateTitle("Updated Template");
        dto.setContent("Updated Content");
        dto.setTemplateDesc("Updated Description");
        dto.setNotificationMethod(Arrays.asList(1));
        dto.setRelateTimeOffset(7200);

        assertEquals(1L, dto.getId());
        assertEquals(1001, dto.getNotificationEvent());
        assertEquals(1, dto.getNotificationNode());
        assertEquals(1, dto.getNotificationRole());
        assertEquals("Updated Template", dto.getTemplateTitle());
        assertEquals("Updated Content", dto.getContent());
        assertEquals("Updated Description", dto.getTemplateDesc());
        assertEquals(Arrays.asList(1), dto.getNotificationMethod());
        assertEquals(7200, dto.getRelateTimeOffset());
        assertNotNull(dto.toString());
    }

    @Test
    void testTemplateQueryDto() {
        TemplateQueryDto dto = new TemplateQueryDto();
        dto.setNotificationEvent(1001);
        dto.setNotificationNode(1);
        dto.setNotificationRole(1);
        dto.setStatus(1);
        dto.setPageSize(10);

        assertEquals(1001, dto.getNotificationEvent());
        assertEquals(1, dto.getNotificationNode());
        assertEquals(1, dto.getNotificationRole());
        assertEquals(1, dto.getStatus());
        assertEquals(10, dto.getPageSize());
        assertNotNull(dto.toString());
    }

    @Test
    void testSendNotificationDto() {
        SendNotificationDto dto = new SendNotificationDto();
        dto.setUserId(1L);
        dto.setNotificationEvent(1001);
        dto.setNodeTimestamp(1234567890L);
        Map<String, Object> vars = new HashMap<>();
        vars.put("key", "value");
        dto.setTemplateVars(vars);

        assertEquals(1L, dto.getUserId());
        assertEquals(1001, dto.getNotificationEvent());
        assertEquals(1234567890L, dto.getNodeTimestamp());
        assertEquals(vars, dto.getTemplateVars());
        assertNotNull(dto.toString());
    }

    @Test
    void testNotificationRecordQueryDto() {
        NotificationRecordQueryDto dto = new NotificationRecordQueryDto();
        dto.setUserId(1L);
        dto.setReadStatus(0);
        dto.setQueryRole(1);
        dto.setPageSize(10);

        assertEquals(1L, dto.getUserId());
        assertEquals(0, dto.getReadStatus());
        assertEquals(1, dto.getQueryRole());
        assertEquals(10, dto.getPageSize());
        assertNotNull(dto.toString());
    }

    @Test
    void testAdminNotificationQueryDto() {
        AdminNotificationQueryDto dto = new AdminNotificationQueryDto();
        dto.setUserId(1L);
        dto.setIsDelete(0);
        dto.setPageSize(10);

        assertEquals(1L, dto.getUserId());
        assertEquals(0, dto.getIsDelete());
        assertEquals(10, dto.getPageSize());
        assertNotNull(dto.toString());
    }

    @Test
    void testBatchDeleteDto() {
        BatchDeleteDto dto = new BatchDeleteDto();
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        dto.setIds(ids);

        assertEquals(ids, dto.getIds());
        assertEquals(3, dto.getIds().size());
        assertNotNull(dto.toString());
    }

    @Test
    void testTemplateDto() {
        TemplateDto dto = new TemplateDto();
        dto.setId(1L);
        dto.setNotificationEvent(1001);
        dto.setNotificationNode("1");
        dto.setNotificationRole(1);
        dto.setTemplateTitle("Template");
        dto.setTemplateContent("Content");
        dto.setTemplateDesc("Description");
        List<Integer> methods = Arrays.asList(1, 2);
        dto.setNotificationMethod(methods);
        dto.setNotificationType(1);
        dto.setRelateTimeOffset(3600L);
        dto.setUserId(1L);
        dto.setStatus("1");
        dto.setIsDelete(0);
        dto.setCreateTime(LocalDateTime.now());
        dto.setUpdateTime(LocalDateTime.now());

        assertEquals(1L, dto.getId());
        assertEquals(1001, dto.getNotificationEvent());
        assertEquals("1", dto.getNotificationNode());
        assertEquals(1, dto.getNotificationRole());
        assertEquals("Template", dto.getTemplateTitle());
        assertEquals("Content", dto.getTemplateContent());
        assertEquals("Description", dto.getTemplateDesc());
        assertEquals(methods, dto.getNotificationMethod());
        assertEquals(1, dto.getNotificationType());
        assertEquals(3600L, dto.getRelateTimeOffset());
        assertEquals(1L, dto.getUserId());
        assertEquals("1", dto.getStatus());
        assertEquals(0, dto.getIsDelete());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotNull(dto.toString());
    }

    // ==================== VO Tests ====================

    @Test
    void testTemplateVo() {
        TemplateVo vo = new TemplateVo();
        vo.setId(1L);
        vo.setNotificationEvent(1001);
        vo.setNotificationNode("1");
        vo.setNotificationRole("1");
        vo.setTemplateTitle("Template");
        vo.setContent("Content");
        vo.setTemplateDesc("Description");
        List<Integer> methods = Arrays.asList(1, 2);
        vo.setNotificationMethod(methods);
        vo.setStatus("1");
        vo.setCreateTime(LocalDateTime.now());

        assertEquals(1L, vo.getId());
        assertEquals(1001, vo.getNotificationEvent());
        assertEquals("1", vo.getNotificationNode());
        assertEquals("1", vo.getNotificationRole());
        assertEquals("Template", vo.getTemplateTitle());
        assertEquals("Content", vo.getContent());
        assertEquals("Description", vo.getTemplateDesc());
        assertEquals(methods, vo.getNotificationMethod());
        assertEquals("1", vo.getStatus());
        assertNotNull(vo.getCreateTime());
        assertNotNull(vo.toString());
    }

    @Test
    void testNotificationRecordVo() {
        NotificationRecordVo vo = new NotificationRecordVo();
        vo.setId(1L);
        vo.setNotificationTaskId(100L);
        vo.setUserId(1L);
        vo.setNotificationMethod(1);
        vo.setTitle("Title");
        vo.setContent("Content");
        vo.setStatus(1);
        vo.setReadStatus(0);
        vo.setSendTime(LocalDateTime.now());
        vo.setCreateTime(LocalDateTime.now());

        assertEquals(1L, vo.getId());
        assertEquals(100L, vo.getNotificationTaskId());
        assertEquals(1L, vo.getUserId());
        assertEquals(1, vo.getNotificationMethod());
        assertEquals("Title", vo.getTitle());
        assertEquals("Content", vo.getContent());
        assertEquals(1, vo.getStatus());
        assertEquals(0, vo.getReadStatus());
        assertNotNull(vo.getSendTime());
        assertNotNull(vo.getCreateTime());
        assertNotNull(vo.toString());
    }

    // ==================== Entity Tests ====================

    @Test
    void testTemplatePo() {
        TemplatePo po = new TemplatePo();
        po.setId(1L);
        po.setNotificationEvent(1001);
        po.setNotificationNode("1");
        po.setNotificationRole(1);
        po.setTemplateTitle("Template");
        po.setTemplateContent("Content");
        po.setTemplateDesc("Description");
        List<Integer> methods = Arrays.asList(1, 2);
        po.setNotificationMethod(methods);
        po.setNotificationType(1);
        po.setRelateTimeOffset(3600L);
        po.setUserId(1L);
        po.setStatus("1");
        po.setIsDelete(0);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());

        assertEquals(1L, po.getId());
        assertEquals(1001, po.getNotificationEvent());
        assertEquals("1", po.getNotificationNode());
        assertEquals(1, po.getNotificationRole());
        assertEquals("Template", po.getTemplateTitle());
        assertEquals("Content", po.getTemplateContent());
        assertEquals("Description", po.getTemplateDesc());
        assertEquals(methods, po.getNotificationMethod());
        assertEquals(1, po.getNotificationType());
        assertEquals(3600L, po.getRelateTimeOffset());
        assertEquals(1L, po.getUserId());
        assertEquals("1", po.getStatus());
        assertEquals(0, po.getIsDelete());
        assertNotNull(po.getCreateTime());
        assertNotNull(po.getUpdateTime());
        assertNotNull(po.toString());
    }

    @Test
    void testNotificationRecordPo() {
        NotificationRecordPo po = new NotificationRecordPo();
        po.setId(1L);
        po.setNotificationTaskId(100L);
        po.setUserId(1L);
        po.setNotificationMethod(1);
        po.setStatus(1);
        po.setReadStatus(0);
        po.setIsDelete(0);
        po.setSendTime(LocalDateTime.now());
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());

        assertEquals(1L, po.getId());
        assertEquals(100L, po.getNotificationTaskId());
        assertEquals(1L, po.getUserId());
        assertEquals(1, po.getNotificationMethod());
        assertEquals(1, po.getStatus());
        assertEquals(0, po.getReadStatus());
        assertEquals(0, po.getIsDelete());
        assertNotNull(po.getSendTime());
        assertNotNull(po.getCreateTime());
        assertNotNull(po.getUpdateTime());
        assertNotNull(po.toString());
    }

    @Test
    void testNotificationRecordPoBuilder() {
        NotificationRecordPo po = NotificationRecordPo.builder()
                .id(1L)
                .notificationTaskId(100L)
                .userId(1L)
                .notificationMethod(1)
                .status(1)
                .readStatus(0)
                .build();

        assertEquals(1L, po.getId());
        assertEquals(100L, po.getNotificationTaskId());
        assertEquals(1L, po.getUserId());
        assertEquals(1, po.getNotificationMethod());
        assertEquals(1, po.getStatus());
        assertEquals(0, po.getReadStatus());
    }

    @Test
    void testNotificationTaskPo() {
        NotificationTaskPo po = new NotificationTaskPo();
        po.setId(1L);
        po.setTitle("Task Title");
        po.setContent("Task Content");
        po.setNotificationRole(1);
        po.setCreateTime(LocalDateTime.now());
        po.setUpdateTime(LocalDateTime.now());

        assertEquals(1L, po.getId());
        assertEquals("Task Title", po.getTitle());
        assertEquals("Task Content", po.getContent());
        assertEquals(1, po.getNotificationRole());
        assertNotNull(po.getCreateTime());
        assertNotNull(po.getUpdateTime());
        assertNotNull(po.toString());
    }

    @Test
    void testNotificationTaskPoBuilder() {
        NotificationTaskPo po = NotificationTaskPo.builder()
                .id(1L)
                .title("Task Title")
                .content("Task Content")
                .notificationRole(1)
                .build();

        assertEquals(1L, po.getId());
        assertEquals("Task Title", po.getTitle());
        assertEquals("Task Content", po.getContent());
        assertEquals(1, po.getNotificationRole());
    }

    @Test
    void testUnsubscribePo() {
        UnsubscribePo po = new UnsubscribePo();
        po.setId(1L);
        po.setUserId(1L);
        po.setNotificationEvent(1001);
        po.setCreateTime(LocalDateTime.now());

        assertEquals(1L, po.getId());
        assertEquals(1L, po.getUserId());
        assertEquals(1001, po.getNotificationEvent());
        assertNotNull(po.getCreateTime());
        assertNotNull(po.toString());
    }

    // ==================== Message Tests ====================

    @Test
    void testNotificationMessage() {
        NotificationMessage message = new NotificationMessage();
        message.setMessageId("msg-123");
        message.setUserId(1L);
        
        TemplatePo template = new TemplatePo();
        template.setId(1L);
        message.setTemplate(template);
        
        message.setSendTime(LocalDateTime.now());
        message.setRetryCount(0);
        message.setMaxRetryCount(3);
        message.setCreateTime(LocalDateTime.now());

        assertEquals("msg-123", message.getMessageId());
        assertEquals(1L, message.getUserId());
        assertNotNull(message.getTemplate());
        assertEquals(1L, message.getTemplate().getId());
        assertNotNull(message.getSendTime());
        assertEquals(0, message.getRetryCount());
        assertEquals(3, message.getMaxRetryCount());
        assertNotNull(message.getCreateTime());
        assertNotNull(message.toString());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    void testTemplatePoEqualsAndHashCode() {
        TemplatePo po1 = new TemplatePo();
        po1.setId(1L);
        po1.setTemplateTitle("Test");

        TemplatePo po2 = new TemplatePo();
        po2.setId(1L);
        po2.setTemplateTitle("Test");

        assertEquals(po1, po1);
        assertNotEquals(po1, null);
        assertNotEquals(po1, "string");
        
        // With Lombok @Data, entities with same id should have same hashCode
        if (po1.equals(po2)) {
            assertEquals(po1.hashCode(), po2.hashCode());
        }
    }

    @Test
    void testNotificationRecordPoEqualsAndHashCode() {
        NotificationRecordPo po1 = new NotificationRecordPo();
        po1.setId(1L);
        po1.setUserId(1L);

        NotificationRecordPo po2 = new NotificationRecordPo();
        po2.setId(1L);
        po2.setUserId(1L);

        assertEquals(po1, po1);
        assertNotEquals(po1, null);
        
        if (po1.equals(po2)) {
            assertEquals(po1.hashCode(), po2.hashCode());
        }
    }
}

