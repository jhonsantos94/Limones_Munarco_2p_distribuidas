package ec.edu.espe.carenotifier.repository;

import ec.edu.espe.carenotifier.model.CareNotification;
import ec.edu.espe.carenotifier.model.NotificationStatus;
import ec.edu.espe.carenotifier.model.NotificationType;
import ec.edu.espe.carenotifier.model.PriorityLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CareNotificationRepository extends JpaRepository<CareNotification, Long> {

    List<CareNotification> findByPatientIdOrderByCreatedAtDesc(String patientId);

    List<CareNotification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);
    
    List<CareNotification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    List<CareNotification> findByTypeOrderByCreatedAtDesc(NotificationType type);

    List<CareNotification> findByPriorityOrderByCreatedAtDesc(PriorityLevel priority);

    Page<CareNotification> findByPatientId(String patientId, Pageable pageable);

    @Query("SELECT n FROM CareNotification n WHERE n.status = :status AND n.retryCount < 3 ORDER BY n.priority DESC, n.createdAt ASC")
    List<CareNotification> findFailedNotificationsForRetry(@Param("status") NotificationStatus status);

    @Query("SELECT n FROM CareNotification n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<CareNotification> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT n FROM CareNotification n WHERE n.patientId = :patientId AND n.status = :status ORDER BY n.createdAt DESC")
    List<CareNotification> findByPatientIdAndStatus(@Param("patientId") String patientId, @Param("status") NotificationStatus status);

    @Query("SELECT n FROM CareNotification n WHERE n.priority = :priority AND n.status IN (:statuses) ORDER BY n.createdAt DESC")
    List<CareNotification> findByPriorityAndStatusIn(@Param("priority") PriorityLevel priority, @Param("statuses") List<NotificationStatus> statuses);

    long countByPatientIdAndStatus(String patientId, NotificationStatus status);

    long countByStatusAndCreatedAtAfter(NotificationStatus status, LocalDateTime date);

    @Query("SELECT COUNT(n) FROM CareNotification n WHERE n.type = :type AND n.createdAt >= :startDate")
    long countByTypeAndCreatedAtAfter(@Param("type") NotificationType type, @Param("startDate") LocalDateTime startDate);
}
