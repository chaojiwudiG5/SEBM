package group5.sebm.notifiation.sender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Resend邮件服务测试
 */
class ResendEmailServiceTest {

    private ResendEmailService resendEmailService;

    @BeforeEach
    void setUp() {
        resendEmailService = new ResendEmailService();
    }

    @Test
    void testIsConfigured_WithApiKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", "test-api-key");

        // When
        boolean isConfigured = resendEmailService.isConfigured();

        // Then
        assertTrue(isConfigured);
    }

    @Test
    void testIsConfigured_WithoutApiKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", "");

        // When
        boolean isConfigured = resendEmailService.isConfigured();

        // Then
        assertFalse(isConfigured);
    }

    @Test
    void testIsConfigured_NullApiKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", null);

        // When
        boolean isConfigured = resendEmailService.isConfigured();

        // Then
        assertFalse(isConfigured);
    }

    @Test
    void testIsConfigured_WhitespaceApiKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", "   ");

        // When
        boolean isConfigured = resendEmailService.isConfigured();

        // Then
        assertFalse(isConfigured);
    }

    @Test
    void testSendEmail_NoApiKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", "");
        ReflectionTestUtils.setField(resendEmailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(resendEmailService, "fromName", "Test");

        // When
        boolean result = resendEmailService.sendEmail("recipient@example.com", "Test", "Content");

        // Then
        assertFalse(result);
    }

    @Test
    void testSendEmail_NullApiKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", null);
        ReflectionTestUtils.setField(resendEmailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(resendEmailService, "fromName", "Test");

        // When
        boolean result = resendEmailService.sendEmail("recipient@example.com", "Test", "Content");

        // Then
        assertFalse(result);
    }

    @Test
    void testSendEmail_WithApiKey_InvalidKey() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", "invalid-key");
        ReflectionTestUtils.setField(resendEmailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(resendEmailService, "fromName", "Test");
        ReflectionTestUtils.setField(resendEmailService, "replyTo", "");

        // When
        boolean result = resendEmailService.sendEmail("recipient@example.com", "Test Subject", "Test Content");

        // Then
        // Will fail due to invalid API key, but shouldn't throw exception
        assertFalse(result);
    }

    @Test
    void testSendEmail_WithReplyTo() {
        // Given
        ReflectionTestUtils.setField(resendEmailService, "resendApiKey", "test-key");
        ReflectionTestUtils.setField(resendEmailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(resendEmailService, "fromName", "Test");
        ReflectionTestUtils.setField(resendEmailService, "replyTo", "reply@example.com");

        // When
        boolean result = resendEmailService.sendEmail("recipient@example.com", "Test", "Content");

        // Then
        // Will fail due to invalid API key, but should include reply-to in request
        assertFalse(result);
    }
}

