package com.raju.codekatas.refactoring.statemachine;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for State Machine refactoring challenge.
 * Tests expected behavior for the refactored document workflow system.
 */
@DisplayName("Document Workflow State Machine Test Suite")
public class DocumentWorkflowTestSuite {
    
    private DocumentWorkflowL document;
    
    @BeforeEach
    void setUp() {
        document = new DocumentWorkflowL("DOC-001", "Test Document", "John Doe");
    }
    
    @Nested
    @DisplayName("Document Creation Tests")
    class DocumentCreationTests {
        
        @Test
        @DisplayName("Should create document with initial state DRAFT")
        void testDocumentCreation() {
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            assertEquals("DOC-001", document.getDocumentId());
            assertEquals("Test Document", document.getTitle());
            assertEquals("John Doe", document.getAuthor());
            assertNotNull(document.getCreatedAt());
            assertTrue(document.getComments().isEmpty());
            assertEquals("", document.getContent());
        }
        
        @Test
        @DisplayName("Should allow editing in DRAFT state")
        void testCanEditInDraftState() {
            assertTrue(document.canEdit());
            assertTrue(document.canSubmit() == false); // No content yet
        }
    }
    
    @Nested
    @DisplayName("Content Update Tests")
    class ContentUpdateTests {
        
        @Test
        @DisplayName("Should update content in DRAFT state")
        void testUpdateContentInDraft() {
            document.updateContent("This is test content");
            
            assertEquals("This is test content", document.getContent());
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            assertTrue(document.canSubmit());
        }
        
        @Test
        @DisplayName("Should not allow content update in SUBMITTED state")
        void testCannotUpdateContentInSubmitted() {
            document.updateContent("Initial content");
            document.submitForReview();
            
            assertThrows(IllegalStateException.class, 
                () -> document.updateContent("Modified content"));
        }
        
        @Test
        @DisplayName("Should allow content update in REJECTED state and reset to DRAFT")
        void testUpdateContentAfterRejection() {
            // Create path to REJECTED state
            document.updateContent("Initial content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.reject("Needs improvement");
            
            // Now update content in REJECTED state
            document.updateContent("Improved content");
            
            assertEquals("Improved content", document.getContent());
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            assertNull(document.getRejectionReason());
        }
    }
    
    @Nested
    @DisplayName("Submission Tests")
    class SubmissionTests {
        
        @Test
        @DisplayName("Should submit document with content")
        void testSubmitDocumentWithContent() {
            document.updateContent("Valid content");
            document.submitForReview();
            
            assertEquals(DocumentWorkflowL.Status.SUBMITTED, document.getCurrentStatus());
        }
        
        @Test
        @DisplayName("Should not submit document without content")
        void testCannotSubmitWithoutContent() {
            assertThrows(IllegalArgumentException.class, 
                () -> document.submitForReview());
        }
        
        @Test
        @DisplayName("Should not submit document with empty content")
        void testCannotSubmitWithEmptyContent() {
            document.updateContent("   ");
            
            assertThrows(IllegalArgumentException.class, 
                () -> document.submitForReview());
        }
        
        @Test
        @DisplayName("Should not submit from non-DRAFT state")
        void testCannotSubmitFromNonDraftState() {
            document.updateContent("Content");
            document.submitForReview();
            
            assertThrows(IllegalStateException.class, 
                () -> document.submitForReview());
        }
    }
    
    @Nested
    @DisplayName("Review Process Tests")
    class ReviewProcessTests {
        
        @BeforeEach
        void setUpSubmittedDocument() {
            document.updateContent("Test content for review");
            document.submitForReview();
        }
        
        @Test
        @DisplayName("Should start review with valid reviewer")
        void testStartReview() {
            document.startReview("Jane Smith");
            
            assertEquals(DocumentWorkflowL.Status.UNDER_REVIEW, document.getCurrentStatus());
            assertEquals("Jane Smith", document.getReviewer());
        }
        
        @Test
        @DisplayName("Should not start review without reviewer")
        void testCannotStartReviewWithoutReviewer() {
            assertThrows(IllegalArgumentException.class, 
                () -> document.startReview(""));
            assertThrows(IllegalArgumentException.class, 
                () -> document.startReview(null));
        }
        
        @Test
        @DisplayName("Should not start review from non-SUBMITTED state")
        void testCannotStartReviewFromWrongState() {
            DocumentWorkflowL newDoc = new DocumentWorkflowL("DOC-002", "New Doc", "Author");
            
            assertThrows(IllegalStateException.class, 
                () -> newDoc.startReview("Jane Smith"));
        }
    }
    
    @Nested
    @DisplayName("Comment Management Tests")
    class CommentTests {
        
        @BeforeEach
        void setUpUnderReview() {
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
        }
        
        @Test
        @DisplayName("Should add comments during review")
        void testAddComment() {
            document.addComment("This section needs more detail");
            
            assertEquals(1, document.getComments().size());
            assertTrue(document.getComments().get(0).contains("Jane Smith"));
            assertTrue(document.getComments().get(0).contains("This section needs more detail"));
        }
        
        @Test
        @DisplayName("Should not add empty comments")
        void testCannotAddEmptyComment() {
            assertThrows(IllegalArgumentException.class, 
                () -> document.addComment(""));
            assertThrows(IllegalArgumentException.class, 
                () -> document.addComment(null));
        }
        
        @Test
        @DisplayName("Should not add comments outside review state")
        void testCannotAddCommentsOutsideReview() {
            DocumentWorkflowL newDoc = new DocumentWorkflowL("DOC-002", "New Doc", "Author");
            
            assertThrows(IllegalStateException.class, 
                () -> newDoc.addComment("Invalid comment"));
        }
    }
    
    @Nested
    @DisplayName("Approval Tests")
    class ApprovalTests {
        
        @BeforeEach
        void setUpUnderReview() {
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
        }
        
        @Test
        @DisplayName("Should approve document under review")
        void testApproveDocument() {
            document.approve();
            
            assertEquals(DocumentWorkflowL.Status.APPROVED, document.getCurrentStatus());
            assertTrue(document.getComments().stream()
                .anyMatch(comment -> comment.contains("approved")));
        }
        
        @Test
        @DisplayName("Should not approve from non-UNDER_REVIEW state")
        void testCannotApproveFromWrongState() {
            DocumentWorkflowL newDoc = new DocumentWorkflowL("DOC-002", "New Doc", "Author");
            
            assertThrows(IllegalStateException.class, 
                () -> newDoc.approve());
        }
        
        @Test
        @DisplayName("Should check reviewer assignment before approval")
        void testApprovalRequiresReviewer() {
            // Create document in UNDER_REVIEW state but without proper reviewer
            DocumentWorkflowL doc = new DocumentWorkflowL("DOC-002", "Test", "Author");
            doc.updateContent("Content");
            doc.submitForReview();
            doc.startReview("Reviewer");
            
            // This should work since reviewer is properly assigned
            assertDoesNotThrow(() -> doc.approve());
        }
    }
    
    @Nested
    @DisplayName("Rejection Tests")
    class RejectionTests {
        
        @BeforeEach
        void setUpUnderReview() {
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
        }
        
        @Test
        @DisplayName("Should reject document with reason")
        void testRejectDocument() {
            document.reject("Insufficient detail in requirements");
            
            assertEquals(DocumentWorkflowL.Status.REJECTED, document.getCurrentStatus());
            assertEquals("Insufficient detail in requirements", document.getRejectionReason());
            assertTrue(document.getComments().stream()
                .anyMatch(comment -> comment.contains("rejected")));
        }
        
        @Test
        @DisplayName("Should not reject without reason")
        void testCannotRejectWithoutReason() {
            assertThrows(IllegalArgumentException.class, 
                () -> document.reject(""));
            assertThrows(IllegalArgumentException.class, 
                () -> document.reject(null));
        }
        
        @Test
        @DisplayName("Should not reject from non-UNDER_REVIEW state")
        void testCannotRejectFromWrongState() {
            DocumentWorkflowL newDoc = new DocumentWorkflowL("DOC-002", "New Doc", "Author");
            
            assertThrows(IllegalStateException.class, 
                () -> newDoc.reject("Invalid rejection"));
        }
    }
    
    @Nested
    @DisplayName("Publishing Tests")
    class PublishingTests {
        
        @Test
        @DisplayName("Should publish approved document")
        void testPublishApprovedDocument() {
            // Create path to APPROVED state
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.approve();
            
            document.publish();
            
            assertEquals(DocumentWorkflowL.Status.PUBLISHED, document.getCurrentStatus());
            assertTrue(document.getComments().stream()
                .anyMatch(comment -> comment.contains("published")));
        }
        
        @Test
        @DisplayName("Should not publish non-approved document")
        void testCannotPublishNonApprovedDocument() {
            assertThrows(IllegalStateException.class, 
                () -> document.publish());
        }
    }
    
    @Nested
    @DisplayName("Archiving Tests")
    class ArchivingTests {
        
        @Test
        @DisplayName("Should archive published document")
        void testArchivePublishedDocument() {
            // Create path to PUBLISHED state
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.approve();
            document.publish();
            
            document.archive();
            
            assertEquals(DocumentWorkflowL.Status.ARCHIVED, document.getCurrentStatus());
        }
        
        @Test
        @DisplayName("Should archive rejected document")
        void testArchiveRejectedDocument() {
            // Create path to REJECTED state
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.reject("Not good enough");
            
            document.archive();
            
            assertEquals(DocumentWorkflowL.Status.ARCHIVED, document.getCurrentStatus());
        }
        
        @Test
        @DisplayName("Should not archive document in invalid state")
        void testCannotArchiveInvalidState() {
            assertThrows(IllegalStateException.class, 
                () -> document.archive());
        }
    }
    
    @Nested
    @DisplayName("Return to Draft Tests")
    class ReturnToDraftTests {
        
        @Test
        @DisplayName("Should return to draft from SUBMITTED state")
        void testReturnToDraftFromSubmitted() {
            document.updateContent("Test content");
            document.submitForReview();
            
            document.returnToDraft();
            
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
        }
        
        @Test
        @DisplayName("Should return to draft from UNDER_REVIEW state")
        void testReturnToDraftFromUnderReview() {
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.addComment("Need to reconsider approach");
            
            document.returnToDraft();
            
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            assertNull(document.getReviewer());
        }
        
        @Test
        @DisplayName("Should not return to draft from invalid state")
        void testCannotReturnToDraftFromInvalidState() {
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.approve();
            
            assertThrows(IllegalStateException.class, 
                () -> document.returnToDraft());
        }
    }
    
    @Nested
    @DisplayName("Permission Check Tests")
    class PermissionTests {
        
        @Test
        @DisplayName("Should correctly report edit permissions")
        void testEditPermissions() {
            // DRAFT state
            assertTrue(document.canEdit());
            
            // SUBMITTED state
            document.updateContent("Content");
            document.submitForReview();
            assertFalse(document.canEdit());
            
            // REJECTED state
            document.startReview("Reviewer");
            document.reject("Needs work");
            assertTrue(document.canEdit());
        }
        
        @Test
        @DisplayName("Should correctly report submission permissions")
        void testSubmissionPermissions() {
            // No content
            assertFalse(document.canSubmit());
            
            // With content
            document.updateContent("Content");
            assertTrue(document.canSubmit());
            
            // After submission
            document.submitForReview();
            assertFalse(document.canSubmit());
        }
        
        @Test
        @DisplayName("Should correctly report review permissions")
        void testReviewPermissions() {
            assertFalse(document.canReview());
            
            document.updateContent("Content");
            document.submitForReview();
            assertTrue(document.canReview());
            
            document.startReview("Reviewer");
            assertFalse(document.canReview());
        }
        
        @Test
        @DisplayName("Should correctly report approval/rejection permissions")
        void testApprovalRejectionPermissions() {
            assertFalse(document.canApprove());
            assertFalse(document.canReject());
            
            document.updateContent("Content");
            document.submitForReview();
            document.startReview("Reviewer");
            
            assertTrue(document.canApprove());
            assertTrue(document.canReject());
        }
        
        @Test
        @DisplayName("Should correctly report publishing permissions")
        void testPublishingPermissions() {
            assertFalse(document.canPublish());
            
            document.updateContent("Content");
            document.submitForReview();
            document.startReview("Reviewer");
            document.approve();
            
            assertTrue(document.canPublish());
        }
        
        @Test
        @DisplayName("Should correctly report archiving permissions")
        void testArchivingPermissions() {
            assertFalse(document.canArchive());
            
            // Published document can be archived
            document.updateContent("Content");
            document.submitForReview();
            document.startReview("Reviewer");
            document.approve();
            document.publish();
            assertTrue(document.canArchive());
            
            // Rejected document can be archived
            DocumentWorkflowL rejectedDoc = new DocumentWorkflowL("DOC-002", "Test", "Author");
            rejectedDoc.updateContent("Content");
            rejectedDoc.submitForReview();
            rejectedDoc.startReview("Reviewer");
            rejectedDoc.reject("Bad");
            assertTrue(rejectedDoc.canArchive());
        }
    }
    
    @Nested
    @DisplayName("Status Information Tests")
    class StatusInformationTests {
        
        @Test
        @DisplayName("Should provide complete status information")
        void testStatusInformation() {
            document.updateContent("Test content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.addComment("Good work");
            document.approve();
            
            String statusInfo = document.getStatusInfo();
            
            assertTrue(statusInfo.contains("DOC-001"));
            assertTrue(statusInfo.contains("Test Document"));
            assertTrue(statusInfo.contains("John Doe"));
            assertTrue(statusInfo.contains("APPROVED"));
            assertTrue(statusInfo.contains("Jane Smith"));
            assertTrue(statusInfo.contains("Good work"));
        }
        
        @Test
        @DisplayName("Should track last modified timestamp")
        void testLastModifiedTimestamp() {
            LocalDateTime before = document.getLastModified();
            
            try {
                Thread.sleep(10);
                document.updateContent("New content");
                
                assertTrue(document.getLastModified().isAfter(before));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        @Test
        @DisplayName("Should maintain document integrity during transitions")
        void testDocumentIntegrityDuringTransitions() {
            String originalId = document.getDocumentId();
            String originalTitle = document.getTitle();
            String originalAuthor = document.getAuthor();
            
            // Go through multiple state transitions
            document.updateContent("Content");
            document.submitForReview();
            document.startReview("Reviewer");
            document.approve();
            
            assertEquals(originalId, document.getDocumentId());
            assertEquals(originalTitle, document.getTitle());
            assertEquals(originalAuthor, document.getAuthor());
        }
    }
    
    @Nested
    @DisplayName("Complex Workflow Tests")
    class ComplexWorkflowTests {
        
        @Test
        @DisplayName("Should handle complete approval workflow")
        void testCompleteApprovalWorkflow() {
            // Complete workflow: Draft -> Submitted -> Under Review -> Approved -> Published -> Archived
            document.updateContent("Comprehensive document content");
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            
            document.submitForReview();
            assertEquals(DocumentWorkflowL.Status.SUBMITTED, document.getCurrentStatus());
            
            document.startReview("Jane Smith");
            assertEquals(DocumentWorkflowL.Status.UNDER_REVIEW, document.getCurrentStatus());
            
            document.addComment("Looks good overall");
            document.approve();
            assertEquals(DocumentWorkflowL.Status.APPROVED, document.getCurrentStatus());
            
            document.publish();
            assertEquals(DocumentWorkflowL.Status.PUBLISHED, document.getCurrentStatus());
            
            document.archive();
            assertEquals(DocumentWorkflowL.Status.ARCHIVED, document.getCurrentStatus());
        }
        
        @Test
        @DisplayName("Should handle rejection and resubmission workflow")
        void testRejectionAndResubmissionWorkflow() {
            // Workflow: Draft -> Submitted -> Under Review -> Rejected -> Draft -> ...
            document.updateContent("Initial content");
            document.submitForReview();
            document.startReview("Jane Smith");
            document.reject("Needs more detail");
            
            assertEquals(DocumentWorkflowL.Status.REJECTED, document.getCurrentStatus());
            assertEquals("Needs more detail", document.getRejectionReason());
            
            // Update content (should reset to DRAFT)
            document.updateContent("Improved content with more detail");
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            assertNull(document.getRejectionReason());
            
            // Resubmit and approve
            document.submitForReview();
            document.startReview("Jane Smith");
            document.approve();
            assertEquals(DocumentWorkflowL.Status.APPROVED, document.getCurrentStatus());
        }
        
        @Test
        @DisplayName("Should handle cancellation scenarios")
        void testCancellationScenarios() {
            // Cancel from SUBMITTED
            document.updateContent("Content");
            document.submitForReview();
            document.returnToDraft();
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            
            // Cancel from UNDER_REVIEW
            document.submitForReview();
            document.startReview("Jane Smith");
            document.returnToDraft();
            assertEquals(DocumentWorkflowL.Status.DRAFT, document.getCurrentStatus());
            assertNull(document.getReviewer());
        }
    }
}
