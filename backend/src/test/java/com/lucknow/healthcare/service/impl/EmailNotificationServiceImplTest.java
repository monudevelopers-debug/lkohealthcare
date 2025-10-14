package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailNotificationServiceImpl
 * 
 * Tests all email notification methods including booking confirmations,
 * status updates, reminders, and system notifications.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    private Booking testBooking;
    private User testUser;
    private Service testService;
    private Provider testProvider;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");

        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Home Nursing");
        testService.setPrice(new BigDecimal("500.00"));
        testService.setDuration(60);

        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setName("Dr. Jane Smith");
        testProvider.setEmail("jane.smith@example.com");
        testProvider.setPhone("+91-9876543210");

        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setUser(testUser);
        testBooking.setService(testService);
        testBooking.setProvider(testProvider);
        testBooking.setScheduledDate(LocalDate.now().plusDays(1));
        testBooking.setScheduledTime(LocalTime.of(10, 0));
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setTotalAmount(new BigDecimal("500.00"));
        testBooking.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSendBookingConfirmation_Success() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingConfirmation(testBooking);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendBookingConfirmation_WithNullBooking_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBookingConfirmation(null);
        });
    }

    @Test
    void testSendBookingStatusUpdate_Success() {
        // Given
        BookingStatus newStatus = BookingStatus.CONFIRMED;
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingStatusUpdate(testBooking, newStatus);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendBookingStatusUpdate_WithNullBooking_ThrowsException() {
        // Given
        BookingStatus newStatus = BookingStatus.CONFIRMED;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBookingStatusUpdate(null, newStatus);
        });
    }

    @Test
    void testSendBookingStatusUpdate_WithNullStatus_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBookingStatusUpdate(testBooking, null);
        });
    }

    @Test
    void testSendBookingReminder_Success() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingReminder(testBooking);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendBookingReminder_WithNullBooking_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBookingReminder(null);
        });
    }

    @Test
    void testSendBookingCancellation_Success() {
        // Given
        String reason = "Customer requested cancellation";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingCancellation(testBooking, reason);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendBookingCancellation_WithNullBooking_ThrowsException() {
        // Given
        String reason = "Customer requested cancellation";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBookingCancellation(null, reason);
        });
    }

    @Test
    void testSendBookingCancellation_WithNullReason_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBookingCancellation(testBooking, null);
        });
    }

    @Test
    void testSendProviderAssignment_Success() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendProviderAssignment(testBooking);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendProviderAssignment_WithNullBooking_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendProviderAssignment(null);
        });
    }

    @Test
    void testSendWelcomeEmail_Success() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendWelcomeEmail(testUser);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendWelcomeEmail_WithNullUser_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendWelcomeEmail(null);
        });
    }

    @Test
    void testSendPasswordResetEmail_Success() {
        // Given
        String resetToken = "reset-token-123";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendPasswordResetEmail(testUser, resetToken);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendPasswordResetEmail_WithNullUser_ThrowsException() {
        // Given
        String resetToken = "reset-token-123";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendPasswordResetEmail(null, resetToken);
        });
    }

    @Test
    void testSendPasswordResetEmail_WithNullToken_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendPasswordResetEmail(testUser, null);
        });
    }

    @Test
    void testSendProviderWelcomeEmail_Success() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendProviderWelcomeEmail(testProvider);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendProviderWelcomeEmail_WithNullProvider_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendProviderWelcomeEmail(null);
        });
    }

    @Test
    void testSendSystemNotification_Success() {
        // Given
        String subject = "System Maintenance";
        String message = "System will be under maintenance from 2 AM to 4 AM";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendSystemNotification(testUser, subject, message);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendSystemNotification_WithNullUser_ThrowsException() {
        // Given
        String subject = "System Maintenance";
        String message = "System will be under maintenance from 2 AM to 4 AM";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendSystemNotification(null, subject, message);
        });
    }

    @Test
    void testSendSystemNotification_WithNullSubject_ThrowsException() {
        // Given
        String message = "System will be under maintenance from 2 AM to 4 AM";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendSystemNotification(testUser, null, message);
        });
    }

    @Test
    void testSendSystemNotification_WithNullMessage_ThrowsException() {
        // Given
        String subject = "System Maintenance";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendSystemNotification(testUser, subject, null);
        });
    }

    @Test
    void testSendBookingConfirmation_WithMessagingException_HandlesGracefully() throws MessagingException {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MessagingException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        // When & Then
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingConfirmation(testBooking);
        });
    }

    @Test
    void testSendSimpleEmail_Success() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String message = "Test Message";

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendSimpleEmail(to, subject, message);
        });

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendSimpleEmail_WithNullTo_ThrowsException() {
        // Given
        String subject = "Test Subject";
        String message = "Test Message";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendSimpleEmail(null, subject, message);
        });
    }

    @Test
    void testSendSimpleEmail_WithNullSubject_ThrowsException() {
        // Given
        String to = "test@example.com";
        String message = "Test Message";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendSimpleEmail(to, null, message);
        });
    }

    @Test
    void testSendSimpleEmail_WithNullMessage_ThrowsException() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendSimpleEmail(to, subject, null);
        });
    }

    @Test
    void testSendHtmlEmail_Success() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlContent = "<h1>Test HTML Content</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendHtmlEmail(to, subject, htmlContent);
        });

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendHtmlEmail_WithNullTo_ThrowsException() {
        // Given
        String subject = "Test Subject";
        String htmlContent = "<h1>Test HTML Content</h1>";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendHtmlEmail(null, subject, htmlContent);
        });
    }

    @Test
    void testSendHtmlEmail_WithNullSubject_ThrowsException() {
        // Given
        String to = "test@example.com";
        String htmlContent = "<h1>Test HTML Content</h1>";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendHtmlEmail(to, null, htmlContent);
        });
    }

    @Test
    void testSendHtmlEmail_WithNullContent_ThrowsException() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendHtmlEmail(to, subject, null);
        });
    }

    @Test
    void testSendBulkEmail_Success() {
        // Given
        String[] recipients = {"user1@example.com", "user2@example.com"};
        String subject = "Bulk Email Subject";
        String message = "Bulk email message";

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBulkEmail(recipients, subject, message);
        });

        // Then
        verify(mailSender, times(recipients.length)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendBulkEmail_WithNullRecipients_ThrowsException() {
        // Given
        String subject = "Bulk Email Subject";
        String message = "Bulk email message";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBulkEmail(null, subject, message);
        });
    }

    @Test
    void testSendBulkEmail_WithEmptyRecipients_ThrowsException() {
        // Given
        String[] recipients = {};
        String subject = "Bulk Email Subject";
        String message = "Bulk email message";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailNotificationService.sendBulkEmail(recipients, subject, message);
        });
    }

    @Test
    void testSendBookingConfirmation_WithTemplateEngine_Success() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Template content</html>");

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingConfirmation(testBooking);
        });

        // Then
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendBookingStatusUpdate_WithTemplateEngine_Success() {
        // Given
        BookingStatus newStatus = BookingStatus.CONFIRMED;
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Template content</html>");

        // When
        assertDoesNotThrow(() -> {
            emailNotificationService.sendBookingStatusUpdate(testBooking, newStatus);
        });

        // Then
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
        verify(mailSender, times(1)).createMimeMessage();
    }
}
