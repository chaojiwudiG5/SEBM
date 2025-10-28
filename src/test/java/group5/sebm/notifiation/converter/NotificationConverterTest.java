package group5.sebm.notifiation.converter;

import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.mq.NotificationMessage;
import group5.sebm.notifiation.service.dto.TemplateDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通知转换器测试
 */
class NotificationConverterTest {

    private final NotificationConverter converter = Mappers.getMapper(NotificationConverter.class);

    @Test
    void testGenerateMessageId() {
        // When
        String messageId1 = converter.generateMessageId();
        String messageId2 = converter.generateMessageId();

        // Then
        assertNotNull(messageId1);
        assertNotNull(messageId2);
        assertNotEquals(messageId1, messageId2);
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
    void testReplacePlaceholders_WithVariables() {
        // Given
        String text = "Hello {name}, your order {orderId} is ready.";
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "John");
        variables.put("orderId", 12345);

        // When
        String result = converter.replacePlaceholders(text, variables);

        // Then
        assertEquals("Hello John, your order 12345 is ready.", result);
    }

    @Test
    void testReplacePlaceholders_NoVariables() {
        // Given
        String text = "Hello {name}";
        Map<String, Object> variables = new HashMap<>();

        // When
        String result = converter.replacePlaceholders(text, variables);

        // Then
        assertEquals("Hello {name}", result);
    }

    @Test
    void testReplacePlaceholders_NullText() {
        // Given
        Map<String, Object> variables = new HashMap<>();

        // When
        String result = converter.replacePlaceholders(null, variables);

        // Then
        assertNull(result);
    }

    @Test
    void testReplacePlaceholders_NullVariables() {
        // Given
        String text = "Hello {name}";

        // When
        String result = converter.replacePlaceholders(text, null);

        // Then
        assertEquals("Hello {name}", result);
    }

    @Test
    void testReplacePlaceholders_EmptyVariables() {
        // Given
        String text = "Hello {name}";
        Map<String, Object> variables = new HashMap<>();

        // When
        String result = converter.replacePlaceholders(text, variables);

        // Then
        assertEquals("Hello {name}", result);
    }

    @Test
    void testReplacePlaceholders_MissingKey() {
        // Given
        String text = "Hello {name}, welcome {guest}!";
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "John");

        // When
        String result = converter.replacePlaceholders(text, variables);

        // Then
        assertEquals("Hello John, welcome {guest}!", result);
    }

    @Test
    void testReplacePlaceholders_SpecialCharacters() {
        // Given
        String text = "Price: {price}$";
        Map<String, Object> variables = new HashMap<>();
        variables.put("price", 99.99);

        // When
        String result = converter.replacePlaceholders(text, variables);

        // Then
        assertEquals("Price: 99.99$", result);
    }

    @Test
    void testBuildTemplateWithPlaceholders_Null() {
        // When
        TemplatePo result = converter.buildTemplateWithPlaceholders(null, null);

        // Then
        assertNull(result);
    }

    @Test
    void testBuildTemplateWithPlaceholders_WithVariables() {
        // Given
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(1L);
        templateDto.setTemplateTitle("Hello {name}");
        templateDto.setTemplateContent("Your order {orderId} is ready");
        templateDto.setNotificationMethod(Arrays.asList(1));
        templateDto.setNotificationNode("1");
        templateDto.setNotificationRole(1);
        templateDto.setNotificationType(1);
        templateDto.setNotificationEvent(1001);
        templateDto.setRelateTimeOffset(3600L);
        templateDto.setUserId(1L);
        templateDto.setTemplateDesc("Test template");
        templateDto.setStatus("1");
        templateDto.setIsDelete(0);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "John");
        variables.put("orderId", 12345);

        // When
        TemplatePo result = converter.buildTemplateWithPlaceholders(templateDto, variables);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hello John", result.getTemplateTitle());
        assertEquals("Your order 12345 is ready", result.getTemplateContent());
        assertEquals(Arrays.asList(1), result.getNotificationMethod());
        assertEquals("1", result.getNotificationNode());
        assertEquals(1, result.getNotificationRole());
        assertEquals(1, result.getNotificationType());
        assertEquals(1001, result.getNotificationEvent());
        assertEquals(3600L, result.getRelateTimeOffset());
        assertEquals(1L, result.getUserId());
        assertEquals("Test template", result.getTemplateDesc());
        assertEquals("1", result.getStatus());
        assertEquals(0, result.getIsDelete());
    }

    @Test
    void testBuildTemplateWithPlaceholders_NoVariables() {
        // Given
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(1L);
        templateDto.setTemplateTitle("Hello");
        templateDto.setTemplateContent("Welcome");

        // When
        TemplatePo result = converter.buildTemplateWithPlaceholders(templateDto, null);

        // Then
        assertNotNull(result);
        assertEquals("Hello", result.getTemplateTitle());
        assertEquals("Welcome", result.getTemplateContent());
    }

    @Test
    void testBuildNotificationMessage() {
        // Given
        SendNotificationDto sendDto = new SendNotificationDto();
        sendDto.setUserId(1L);
        sendDto.setNotificationEvent(1001);
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "Test");
        sendDto.setTemplateVars(vars);

        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(1L);
        templateDto.setTemplateTitle("Hello {name}");
        templateDto.setTemplateContent("Welcome");

        // When
        NotificationMessage result = converter.buildNotificationMessage(sendDto, templateDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertNotNull(result.getMessageId());
        assertNotNull(result.getTemplate());
        assertEquals("Hello Test", result.getTemplate().getTemplateTitle());
        assertEquals(0, result.getRetryCount());
        assertEquals(3, result.getMaxRetryCount());
        assertNotNull(result.getCreateTime());
    }
}

