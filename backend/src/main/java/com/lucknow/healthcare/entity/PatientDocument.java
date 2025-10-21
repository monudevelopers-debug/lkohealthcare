package com.lucknow.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PatientDocument entity representing medical documents uploaded for patients
 * 
 * Stores prescriptions, medical reports, lab results, and other medical documentation.
 * Files are stored locally with proper security and access controls.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "patient_documents", indexes = {
    @Index(name = "idx_patient_documents_patient_id", columnList = "patient_id"),
    @Index(name = "idx_patient_documents_document_type", columnList = "document_type"),
    @Index(name = "idx_patient_documents_uploaded_at", columnList = "uploaded_at"),
    @Index(name = "idx_patient_documents_patient_type", columnList = "patient_id, document_type"),
    @Index(name = "idx_patient_documents_is_verified", columnList = "is_verified")
})
@EntityListeners(AuditingEntityListener.class)
public class PatientDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @NotNull(message = "Document type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;
    
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name too long")
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @NotBlank(message = "File path is required")
    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath; // Local storage path
    
    @Min(value = 0, message = "File size must be positive")
    @Column(name = "file_size")
    private Long fileSize; // in bytes
    
    @Size(max = 100, message = "MIME type too long")
    @Column(name = "mime_type", length = 100)
    private String mimeType; // e.g., "application/pdf", "image/jpeg"
    
    @Size(max = 1000, message = "Description too long")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "document_date")
    private LocalDate documentDate; // Date on the document itself
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy; // Customer who uploaded
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy; // Admin or Provider who verified
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @CreatedDate
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    // Enums
    
    public enum DocumentType {
        PRESCRIPTION,      // Doctor's prescription
        MEDICAL_REPORT,    // General medical report
        LAB_RESULT,        // Laboratory test results
        XRAY,              // X-ray images
        SCAN,              // CT/MRI scans
        INSURANCE,         // Insurance documents
        OTHER              // Other medical documents
    }
    
    // Constructors
    
    public PatientDocument() {}
    
    public PatientDocument(Patient patient, DocumentType documentType, String fileName, String filePath) {
        this.patient = patient;
        this.documentType = documentType;
        this.fileName = fileName;
        this.filePath = filePath;
    }
    
    // Business methods
    
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    public boolean isPdf() {
        return mimeType != null && mimeType.equals("application/pdf");
    }
    
    public String getFileSizeDisplay() {
        if (fileSize == null) return "Unknown";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        }
    }
    
    public void verify(User verifier) {
        this.isVerified = true;
        this.verifiedBy = verifier;
        this.verifiedAt = LocalDateTime.now();
    }
    
    public boolean canBeDeletedBy(User user) {
        // Can be deleted by uploader or admin
        return uploadedBy != null && uploadedBy.getId().equals(user.getId()) ||
               (user.getRole() != null && user.getRole().toString().equals("ADMIN"));
    }
    
    // Getters and Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public DocumentType getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDocumentDate() {
        return documentDate;
    }
    
    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }
    
    public User getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public User getVerifiedBy() {
        return verifiedBy;
    }
    
    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}

