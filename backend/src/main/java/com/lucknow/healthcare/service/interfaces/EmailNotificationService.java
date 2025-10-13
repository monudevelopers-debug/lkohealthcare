package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.User;

/**
 * Service interface for email notifications
 * 
 * Defines methods for sending various types of email notifications
 * including booking confirmations, status updates, reminders, and system notifications.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface EmailNotificationService {
    
    /**
     * Send booking confirmation email to customer
     * 
     * @param booking the booking to confirm
     */
    void sendBookingConfirmation(Booking booking);
    
    /**
     * Send booking status update email to customer
     * 
     * @param booking the updated booking
     * @param previousStatus the previous status of the booking
     */
    void sendBookingStatusUpdate(Booking booking, String previousStatus);
    
    /**
     * Send provider assignment notification emails
     * 
     * @param booking the booking with assigned provider
     * @param provider the assigned provider
     */
    void sendProviderAssignment(Booking booking, com.lucknow.healthcare.entity.Provider provider);
    
    /**
     * Send booking reminder email to customer
     * 
     * @param booking the booking to remind about
     */
    void sendBookingReminder(Booking booking);
    
    /**
     * Send password reset email to user
     * 
     * @param user the user requesting password reset
     * @param resetToken the password reset token
     */
    void sendPasswordResetEmail(User user, String resetToken);
    
    /**
     * Send welcome email to new user
     * 
     * @param user the new user
     */
    void sendWelcomeEmail(User user);
    
    /**
     * Send system notification email
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param message email message
     */
    void sendSystemNotification(String to, String subject, String message);
}
