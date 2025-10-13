package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.service.interfaces.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service implementation for email notifications
 * 
 * Handles sending various types of email notifications including
 * booking confirmations, status updates, reminders, and system notifications.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    private static final String FROM_EMAIL = "noreply@lucknowhealthcare.com";
    private static final String COMPANY_NAME = "Lucknow Healthcare Services";
    
    @Override
    public void sendBookingConfirmation(Booking booking) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", booking.getUser().getName());
            templateData.put("serviceName", booking.getService().getName());
            templateData.put("scheduledDate", formatDate(booking.getScheduledDate()));
            templateData.put("scheduledTime", booking.getScheduledTime());
            templateData.put("totalAmount", booking.getTotalAmount());
            templateData.put("bookingId", booking.getId().toString());
            templateData.put("companyName", COMPANY_NAME);
            
            sendTemplateEmail(
                booking.getUser().getEmail(),
                "Booking Confirmation - " + booking.getService().getName(),
                "email/booking-confirmation",
                templateData
            );
        } catch (Exception e) {
            // Log error but don't throw to avoid breaking the booking flow
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendBookingStatusUpdate(Booking booking, String previousStatus) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", booking.getUser().getName());
            templateData.put("serviceName", booking.getService().getName());
            templateData.put("previousStatus", previousStatus);
            templateData.put("newStatus", booking.getStatus().toString());
            templateData.put("scheduledDate", formatDate(booking.getScheduledDate()));
            templateData.put("scheduledTime", booking.getScheduledTime());
            templateData.put("companyName", COMPANY_NAME);
            
            String subject = "Booking Status Update - " + booking.getService().getName();
            String template = "email/booking-status-update";
            
            sendTemplateEmail(
                booking.getUser().getEmail(),
                subject,
                template,
                templateData
            );
        } catch (Exception e) {
            System.err.println("Failed to send booking status update email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendProviderAssignment(Booking booking, Provider provider) {
        try {
            // Email to customer
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("userName", booking.getUser().getName());
            customerData.put("serviceName", booking.getService().getName());
            customerData.put("providerName", provider.getName());
            customerData.put("providerPhone", provider.getPhone());
            customerData.put("scheduledDate", formatDate(booking.getScheduledDate()));
            customerData.put("scheduledTime", booking.getScheduledTime());
            customerData.put("companyName", COMPANY_NAME);
            
            sendTemplateEmail(
                booking.getUser().getEmail(),
                "Healthcare Provider Assigned - " + booking.getService().getName(),
                "email/provider-assignment-customer",
                customerData
            );
            
            // Email to provider
            Map<String, Object> providerData = new HashMap<>();
            providerData.put("providerName", provider.getName());
            providerData.put("customerName", booking.getUser().getName());
            providerData.put("customerPhone", booking.getUser().getPhone());
            providerData.put("serviceName", booking.getService().getName());
            providerData.put("scheduledDate", formatDate(booking.getScheduledDate()));
            providerData.put("scheduledTime", booking.getScheduledTime());
            providerData.put("totalAmount", booking.getTotalAmount());
            providerData.put("notes", booking.getNotes());
            providerData.put("companyName", COMPANY_NAME);
            
            sendTemplateEmail(
                provider.getEmail(),
                "New Booking Assignment - " + booking.getService().getName(),
                "email/provider-assignment-provider",
                providerData
            );
        } catch (Exception e) {
            System.err.println("Failed to send provider assignment emails: " + e.getMessage());
        }
    }
    
    @Override
    public void sendBookingReminder(Booking booking) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", booking.getUser().getName());
            templateData.put("serviceName", booking.getService().getName());
            templateData.put("scheduledDate", formatDate(booking.getScheduledDate()));
            templateData.put("scheduledTime", booking.getScheduledTime());
            templateData.put("providerName", booking.getProvider() != null ? booking.getProvider().getName() : "TBD");
            templateData.put("companyName", COMPANY_NAME);
            
            sendTemplateEmail(
                booking.getUser().getEmail(),
                "Reminder: Upcoming Healthcare Service",
                "email/booking-reminder",
                templateData
            );
        } catch (Exception e) {
            System.err.println("Failed to send booking reminder email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getName());
            templateData.put("resetToken", resetToken);
            templateData.put("companyName", COMPANY_NAME);
            templateData.put("resetUrl", "http://localhost:3000/reset-password?token=" + resetToken);
            
            sendTemplateEmail(
                user.getEmail(),
                "Password Reset Request - " + COMPANY_NAME,
                "email/password-reset",
                templateData
            );
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendWelcomeEmail(User user) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getName());
            templateData.put("userRole", user.getRole().toString());
            templateData.put("companyName", COMPANY_NAME);
            templateData.put("dashboardUrl", getDashboardUrl(user.getRole()));
            
            sendTemplateEmail(
                user.getEmail(),
                "Welcome to " + COMPANY_NAME,
                "email/welcome",
                templateData
            );
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendSystemNotification(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(FROM_EMAIL);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Failed to send system notification: " + e.getMessage());
        }
    }
    
    private void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> data) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            
            helper.setFrom(FROM_EMAIL);
            helper.setTo(to);
            helper.setSubject(subject);
            
            Context context = new Context();
            context.setVariables(data);
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"));
    }
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
    }
    
    private String getDashboardUrl(UserRole role) {
        switch (role) {
            case ADMIN:
                return "http://localhost:3002";
            case PROVIDER:
                return "http://localhost:3001";
            default:
                return "http://localhost:3000";
        }
    }
}
