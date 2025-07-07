package ec.edu.espe.carenotifier.controller;

import ec.edu.espe.carenotifier.dto.NotificationRequestDto;
import ec.edu.espe.carenotifier.dto.NotificationResponseDto;
import ec.edu.espe.carenotifier.model.CareNotification;
import ec.edu.espe.carenotifier.model.NotificationStatus;
import ec.edu.espe.carenotifier.service.CareNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class CareNotificationController {

    private final CareNotificationService careNotificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationRequestDto request) {
        
        log.info("Creating notification for patient: {}", request.getPatientId());
        NotificationResponseDto response = careNotificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<CareNotification>> getNotificationsByPatient(
            @PathVariable String patientId) {
        
        log.info("Retrieving notifications for patient: {}", patientId);
        List<CareNotification> notifications = careNotificationService.getNotificationsByPatient(patientId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CareNotification>> getPendingNotifications() {
        log.info("Retrieving pending notifications");
        List<CareNotification> notifications = careNotificationService.getPendingNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/status")
    public ResponseEntity<Void> updateNotificationStatus(
            @PathVariable Long notificationId,
            @RequestParam NotificationStatus status) {
        
        log.info("Updating notification {} status to {}", notificationId, status);
        careNotificationService.updateNotificationStatus(notificationId, status, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CareNotifier service is running");
    }
}
