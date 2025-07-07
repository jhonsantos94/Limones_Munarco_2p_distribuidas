package ec.edu.espe.patientdatacollector.repository;

import ec.edu.espe.patientdatacollector.model.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    
    List<VitalSign> findByDeviceIdOrderByTimestampDesc(String deviceId);
    
    List<VitalSign> findByDeviceIdAndTimestampBetween(String deviceId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM VitalSign v WHERE v.deviceId = :deviceId AND v.timestamp >= :since ORDER BY v.timestamp DESC")
    List<VitalSign> findRecentByDevice(@Param("deviceId") String deviceId, @Param("since") LocalDateTime since);
    
    @Query("SELECT DISTINCT v.deviceId FROM VitalSign v WHERE v.timestamp >= :since")
    List<String> findActiveDevicesSince(@Param("since") LocalDateTime since);
    
    List<VitalSign> findByTimestampBefore(LocalDateTime before);
}
