package group5.sebm.notifiation.sender;

import group5.sebm.User.service.UserServiceInterface.UserService;
import group5.sebm.common.dto.UserDto;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 邮件发送器测试
 */
@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    @Mock
    private ResendEmailService resendEmailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailSender emailSender;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setUsername("testuser");
    }

    @Test
    void testGetChannelType() {
        // When & Then
        assertEquals(NotificationMethodEnum.EMAIL, emailSender.getChannelType());
    }

    @Test
    void testSendNotification_NullUserId() {
        // When
        boolean result = emailSender.sendNotification(null, "Test", "Content");

        // Then
        assertFalse(result);
        verify(userService, never()).getCurrentUserDtoFromID(anyLong());
    }

    @Test
    void testSendNotification_NullEmail() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(null);

        // When
        boolean result = emailSender.sendNotification(1L, "Test", "Content");

        // Then
        assertFalse(result);
        verify(userService).getCurrentUserDtoFromID(1L);
        verify(resendEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendNotification_InvalidEmailFormat() {
        // Given
        userDto.setEmail("invalid-email");
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);

        // When
        boolean result = emailSender.sendNotification(1L, "Test", "Content");

        // Then
        assertFalse(result);
        verify(userService).getCurrentUserDtoFromID(1L);
        verify(resendEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendNotification_SuccessWithResend() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(resendEmailService.isConfigured()).thenReturn(true);
        when(resendEmailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        // When
        boolean result = emailSender.sendNotification(1L, "Test Subject", "Test Content");

        // Then
        assertTrue(result);
        verify(userService).getCurrentUserDtoFromID(1L);
        verify(resendEmailService).isConfigured();
        verify(resendEmailService).sendEmail("test@example.com", "Test Subject", "Test Content");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotification_ResendFallbackToSMTP() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(resendEmailService.isConfigured()).thenReturn(true);
        when(resendEmailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(false);

        // When
        boolean result = emailSender.sendNotification(1L, "Test Subject", "Test Content");

        // Then
        assertTrue(result);
        verify(userService).getCurrentUserDtoFromID(1L);
        verify(resendEmailService).isConfigured();
        verify(resendEmailService).sendEmail("test@example.com", "Test Subject", "Test Content");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotification_SMTPDirectly() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(resendEmailService.isConfigured()).thenReturn(false);

        // When
        boolean result = emailSender.sendNotification(1L, "Test Subject", "Test Content");

        // Then
        assertTrue(result);
        verify(userService).getCurrentUserDtoFromID(1L);
        verify(resendEmailService).isConfigured();
        verify(resendEmailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendNotification_NullSubject() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(resendEmailService.isConfigured()).thenReturn(true);
        when(resendEmailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        // When
        boolean result = emailSender.sendNotification(1L, null, "Test Content");

        // Then
        assertTrue(result);
        verify(resendEmailService).sendEmail("test@example.com", "系统通知", "Test Content");
    }

    @Test
    void testSendNotification_SMTPException() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);
        when(resendEmailService.isConfigured()).thenReturn(false);
        doThrow(new RuntimeException("SMTP Error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        boolean result = emailSender.sendNotification(1L, "Test", "Content");

        // Then
        assertFalse(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testGetUserContactInfo_ValidUser() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(userDto);

        // When
        String email = emailSender.getUserContactInfo(1L);

        // Then
        assertEquals("test@example.com", email);
        verify(userService).getCurrentUserDtoFromID(1L);
    }

    @Test
    void testGetUserContactInfo_NullUser() {
        // Given
        when(userService.getCurrentUserDtoFromID(1L)).thenReturn(null);

        // When
        String email = emailSender.getUserContactInfo(1L);

        // Then
        assertNull(email);
        verify(userService).getCurrentUserDtoFromID(1L);
    }
}

