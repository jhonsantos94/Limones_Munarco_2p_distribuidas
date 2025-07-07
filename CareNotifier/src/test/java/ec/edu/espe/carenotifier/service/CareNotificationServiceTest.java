package ec.edu.espe.carenotifier.service;

import ec.edu.espe.carenotifier.dto.NotificationRequestDto;
import ec.edu.espe.carenotifier.dto.NotificationResponseDto;
import ec.edu.espe.carenotifier.model.CareNotification;
import ec.edu.espe.carenotifier.model.NotificationStatus;
import ec.edu.espe.carenotifier.model.NotificationType;
import ec.edu.espe.carenotifier.model.PriorityLevel;
import ec.edu.espe.carenotifier.repository.CareNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CareNotificationServiceTest {

    @Mock
    private CareNotificationRepository notificationRepository;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private SmsNotificationService smsNotificationService;

    @Mock
    private EmergencyContactService emergencyContactService;

    @InjectMocks
    private CareNotificationService careNotificationService;

    private NotificationRequestDto requestDto;
    private CareNotification notification;

    @BeforeEach
    void setUp() {
        requestDto = NotificationRequestDto.builder()
                .patientId("PAT123")
                .type(NotificationType.HEALTH_ALERT)
                .priority(PriorityLevel.HIGH)
                .title("Test Alert")
                .message("This is a test health alert")
                .recipientEmail("test@example.com")
                .recipientPhone("+1234567890")
                .build();

        notification = new CareNotification();
        notification.setId(1L);
        notification.setPatientId("PAT123");
        notification.setType(NotificationType.HEALTH_ALERT);
        notification.setPriority(PriorityLevel.HIGH);
        notification.setTitle("Test Alert");
        notification.setMessage("This is a test health alert");
        notification.setRecipientEmail("test@example.com");
        notification.setRecipientPhone("+1234567890");
        notification.setStatus(NotificationStatus.PENDING);
    }

    @Test
    void createNotification_ShouldReturnResponse_WhenValidRequest() {
        // Given
        when(notificationRepository.save(any(CareNotification.class))).thenReturn(notification);

        // When
        NotificationResponseDto response = careNotificationService.createNotification(requestDto);

        // Then
        assertNotNull(response);
        assertEquals("PAT123", response.getPatientId());
        assertEquals(NotificationType.HEALTH_ALERT, response.getType());
        assertEquals(PriorityLevel.HIGH, response.getPriority());
        assertEquals("Test Alert", response.getTitle());
        assertEquals("This is a test health alert", response.getMessage());
        
        verify(notificationRepository).save(any(CareNotification.class));
    }

    @Test
    void getNotificationsByPatient_ShouldReturnNotifications_WhenPatientExists() {
        // Given
        String patientId = "PAT123";
        List<CareNotification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByPatientIdOrderByCreatedAtDesc(patientId)).thenReturn(notifications);

        // When
        List<CareNotification> result = careNotificationService.getNotificationsByPatient(patientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PAT123", result.get(0).getPatientId());
        
        verify(notificationRepository).findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Test
    void getPendingNotifications_ShouldReturnPendingNotifications() {
        // Given
        List<CareNotification> pendingNotifications = Arrays.asList(notification);
        when(notificationRepository.findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING))
                .thenReturn(pendingNotifications);

        // When
        List<CareNotification> result = careNotificationService.getPendingNotifications();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NotificationStatus.PENDING, result.get(0).getStatus());
        
        verify(notificationRepository).findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING);
    }
}
