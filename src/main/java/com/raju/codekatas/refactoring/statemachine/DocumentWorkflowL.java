package com.raju.codekatas.refactoring.statemachine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LEGACY CODE - Document approval workflow with nested conditionals
 * 
 * REFACTORING CHALLENGE:
 * Convert this complex conditional logic into State Pattern implementation.
 * 
 * TIME LIMIT: 40 minutes
 * 
 * REQUIREMENTS:
 * 1. Create State interface and concrete state classes
 * 2. Move transition logic into appropriate state classes
 * 3. Eliminate all nested if/else statements from main class
 * 4. Ensure valid state transitions only
 * 5. Make the system extensible for new states
 */
public class DocumentWorkflowL {
    
    public enum Status {
        DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PUBLISHED, ARCHIVED
    }
    
    private String documentId;
    private String title;
    private String content;
    private String author;
    private Status currentStatus;
    private String reviewer;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private List<String> comments;
    
    public DocumentWorkflowL(String documentId, String title, String author) {
        this.documentId = documentId;
        this.title = title;
        this.author = author;
        this.currentStatus = Status.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.comments = new ArrayList<>();
        this.content = "";
        
        System.out.println("Document created: " + documentId + " by " + author);
    }
    
    public void updateContent(String content) {
        if (currentStatus == Status.DRAFT) {
            this.content = content;
            this.lastModified = LocalDateTime.now();
            System.out.println("Content updated for document: " + documentId);
        } else if (currentStatus == Status.REJECTED) {
            this.content = content;
            this.currentStatus = Status.DRAFT; // Reset to draft after content update
            this.rejectionReason = null;
            this.lastModified = LocalDateTime.now();
            System.out.println("Content updated after rejection, status reset to DRAFT: " + documentId);
        } else {
            throw new IllegalStateException("Cannot update content in status: " + currentStatus);
        }
    }
    
    public void submitForReview() {
        if (currentStatus == Status.DRAFT) {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Cannot submit document with empty content");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Cannot submit document with empty title");
            }
            
            this.currentStatus = Status.SUBMITTED;
            this.lastModified = LocalDateTime.now();
            System.out.println("Document submitted for review: " + documentId);
        } else {
            throw new IllegalStateException("Can only submit documents in DRAFT status, current: " + currentStatus);
        }
    }
    
    public void startReview(String reviewer) {
        if (currentStatus == Status.SUBMITTED) {
            if (reviewer == null || reviewer.trim().isEmpty()) {
                throw new IllegalArgumentException("Reviewer cannot be empty");
            }
            
            this.currentStatus = Status.UNDER_REVIEW;
            this.reviewer = reviewer;
            this.lastModified = LocalDateTime.now();
            System.out.println("Document review started by: " + reviewer + " for document: " + documentId);
        } else {
            throw new IllegalStateException("Can only start review for SUBMITTED documents, current: " + currentStatus);
        }
    }
    
    public void addComment(String comment) {
        if (currentStatus == Status.UNDER_REVIEW) {
            if (comment == null || comment.trim().isEmpty()) {
                throw new IllegalArgumentException("Comment cannot be empty");
            }
            
            comments.add(LocalDateTime.now() + " - " + reviewer + ": " + comment);
            this.lastModified = LocalDateTime.now();
            System.out.println("Comment added to document: " + documentId);
        } else {
            throw new IllegalStateException("Can only add comments during review, current: " + currentStatus);
        }
    }
    
    public void approve() {
        if (currentStatus == Status.UNDER_REVIEW) {
            if (reviewer == null) {
                throw new IllegalStateException("No reviewer assigned");
            }
            
            this.currentStatus = Status.APPROVED;
            this.lastModified = LocalDateTime.now();
            comments.add(LocalDateTime.now() + " - " + reviewer + ": Document approved");
            System.out.println("Document approved: " + documentId + " by " + reviewer);
        } else {
            throw new IllegalStateException("Can only approve documents UNDER_REVIEW, current: " + currentStatus);
        }
    }
    
    public void reject(String reason) {
        if (currentStatus == Status.UNDER_REVIEW) {
            if (reviewer == null) {
                throw new IllegalStateException("No reviewer assigned");
            }
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Rejection reason cannot be empty");
            }
            
            this.currentStatus = Status.REJECTED;
            this.rejectionReason = reason;
            this.lastModified = LocalDateTime.now();
            comments.add(LocalDateTime.now() + " - " + reviewer + ": Document rejected - " + reason);
            System.out.println("Document rejected: " + documentId + " by " + reviewer + ", reason: " + reason);
        } else {
            throw new IllegalStateException("Can only reject documents UNDER_REVIEW, current: " + currentStatus);
        }
    }
    
    public void publish() {
        if (currentStatus == Status.APPROVED) {
            this.currentStatus = Status.PUBLISHED;
            this.lastModified = LocalDateTime.now();
            comments.add(LocalDateTime.now() + " - System: Document published");
            System.out.println("Document published: " + documentId);
        } else {
            throw new IllegalStateException("Can only publish APPROVED documents, current: " + currentStatus);
        }
    }
    
    public void archive() {
        if (currentStatus == Status.PUBLISHED) {
            this.currentStatus = Status.ARCHIVED;
            this.lastModified = LocalDateTime.now();
            comments.add(LocalDateTime.now() + " - System: Document archived");
            System.out.println("Document archived: " + documentId);
        } else if (currentStatus == Status.REJECTED) {
            this.currentStatus = Status.ARCHIVED;
            this.lastModified = LocalDateTime.now();
            comments.add(LocalDateTime.now() + " - System: Rejected document archived");
            System.out.println("Rejected document archived: " + documentId);
        } else {
            throw new IllegalStateException("Can only archive PUBLISHED or REJECTED documents, current: " + currentStatus);
        }
    }
    
    public void returnToDraft() {
        if (currentStatus == Status.SUBMITTED) {
            this.currentStatus = Status.DRAFT;
            this.lastModified = LocalDateTime.now();
            System.out.println("Document returned to draft: " + documentId);
        } else if (currentStatus == Status.UNDER_REVIEW) {
            this.currentStatus = Status.DRAFT;
            this.reviewer = null;
            this.lastModified = LocalDateTime.now();
            comments.add(LocalDateTime.now() + " - System: Review cancelled, returned to draft");
            System.out.println("Document review cancelled, returned to draft: " + documentId);
        } else {
            throw new IllegalStateException("Can only return to draft from SUBMITTED or UNDER_REVIEW, current: " + currentStatus);
        }
    }
    
    public boolean canEdit() {
        return currentStatus == Status.DRAFT || currentStatus == Status.REJECTED;
    }
    
    public boolean canSubmit() {
        return currentStatus == Status.DRAFT && content != null && !content.trim().isEmpty();
    }
    
    public boolean canReview() {
        return currentStatus == Status.SUBMITTED;
    }
    
    public boolean canApprove() {
        return currentStatus == Status.UNDER_REVIEW && reviewer != null;
    }
    
    public boolean canReject() {
        return currentStatus == Status.UNDER_REVIEW && reviewer != null;
    }
    
    public boolean canPublish() {
        return currentStatus == Status.APPROVED;
    }
    
    public boolean canArchive() {
        return currentStatus == Status.PUBLISHED || currentStatus == Status.REJECTED;
    }
    
    // Getters
    public String getDocumentId() { return documentId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public Status getCurrentStatus() { return currentStatus; }
    public String getReviewer() { return reviewer; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    public List<String> getComments() { return new ArrayList<>(comments); }
    
    public String getStatusInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Document: ").append(documentId).append("\n");
        info.append("Title: ").append(title).append("\n");
        info.append("Author: ").append(author).append("\n");
        info.append("Status: ").append(currentStatus).append("\n");
        info.append("Created: ").append(createdAt).append("\n");
        info.append("Last Modified: ").append(lastModified).append("\n");
        
        if (reviewer != null) {
            info.append("Reviewer: ").append(reviewer).append("\n");
        }
        
        if (rejectionReason != null) {
            info.append("Rejection Reason: ").append(rejectionReason).append("\n");
        }
        
        if (!comments.isEmpty()) {
            info.append("Comments:\n");
            for (String comment : comments) {
                info.append("  ").append(comment).append("\n");
            }
        }
        
        return info.toString();
    }
    
    public static void main(String[] args) {
        // Demo of the legacy document workflow system
        DocumentWorkflowL doc = new DocumentWorkflowL("DOC-001", "Software Requirements", "John Doe");
        
        try {
            // Author creates and edits document
            doc.updateContent("This is the initial content of the requirements document.");
            System.out.println("Can edit: " + doc.canEdit());
            System.out.println("Can submit: " + doc.canSubmit());
            
            // Submit for review
            doc.submitForReview();
            System.out.println("Status after submission: " + doc.getCurrentStatus());
            
            // Start review process
            doc.startReview("Jane Smith");
            doc.addComment("The requirements look good overall, but need more detail in section 3.");
            doc.addComment("Please add acceptance criteria for each requirement.");
            
            // Reviewer rejects
            doc.reject("Missing acceptance criteria and insufficient detail in section 3");
            System.out.println("Status after rejection: " + doc.getCurrentStatus());
            System.out.println("Rejection reason: " + doc.getRejectionReason());
            
            // Author updates content after rejection
            doc.updateContent("Updated requirements document with detailed acceptance criteria and expanded section 3.");
            System.out.println("Status after content update: " + doc.getCurrentStatus());
            
            // Resubmit and approve
            doc.submitForReview();
            doc.startReview("Jane Smith");
            doc.addComment("Much better! All requirements are now clear and have acceptance criteria.");
            doc.approve();
            System.out.println("Status after approval: " + doc.getCurrentStatus());
            
            // Publish and archive
            doc.publish();
            System.out.println("Status after publishing: " + doc.getCurrentStatus());
            
            doc.archive();
            System.out.println("Final status: " + doc.getCurrentStatus());
            
            // Show complete document info
            System.out.println("\n=== Final Document Info ===");
            System.out.println(doc.getStatusInfo());
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
