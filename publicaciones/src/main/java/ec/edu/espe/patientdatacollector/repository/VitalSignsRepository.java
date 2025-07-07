package ec.edu.espe.patientdatacollector.repository;

import ec.edu.espe.patientdatacollector.model.VitalSigns;
import ec.edu.espe.patientdatacollector.model.Patient;
import ec.edu.espe.patientdatacollector.model.MeasurementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VitalSignsRepository extends JpaRepository<VitalSigns, Long> {

    /**
     * Find vital signs by patient
     */
    List<VitalSigns> findByPatientOrderByMeasuredAtDesc(Patient patient);

    /**
     * Find vital signs by patient ID
     */
    List<VitalSigns> findByPatientIdOrderByMeasuredAtDesc(Long patientId);

    /**
     * Find vital signs by patient with pagination
     */
    Page<VitalSigns> findByPatientOrderByMeasuredAtDesc(Patient patient, Pageable pageable);

    /**
     * Find vital signs by patient ID with pagination
     */
    Page<VitalSigns> findByPatientIdOrderByMeasuredAtDesc(Long patientId, Pageable pageable);

    /**
     * Find latest vital signs for a patient
     */
    Optional<VitalSigns> findFirstByPatientOrderByMeasuredAtDesc(Patient patient);

    /**
     * Find latest vital signs by patient ID
     */
    Optional<VitalSigns> findFirstByPatientIdOrderByMeasuredAtDesc(Long patientId);

    /**
     * Find vital signs by patient and date range
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.patient = :patient " +
           "AND v.measuredAt BETWEEN :startDate AND :endDate " +
           "ORDER BY v.measuredAt DESC")
    List<VitalSigns> findByPatientAndMeasuredAtBetween(@Param("patient") Patient patient,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find vital signs by patient ID and date range
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.patient.id = :patientId " +
           "AND v.measuredAt BETWEEN :startDate AND :endDate " +
           "ORDER BY v.measuredAt DESC")
    List<VitalSigns> findByPatientIdAndMeasuredAtBetween(@Param("patientId") Long patientId,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find vital signs by measurement type
     */
    List<VitalSigns> findByMeasurementTypeOrderByMeasuredAtDesc(MeasurementType measurementType);

    /**
     * Find vital signs by device ID
     */
    List<VitalSigns> findByDeviceIdOrderByMeasuredAtDesc(String deviceId);

    /**
     * Find abnormal vital signs
     */
    @Query("SELECT v FROM VitalSigns v WHERE " +
           "(v.heartRate IS NOT NULL AND (v.heartRate < 60 OR v.heartRate > 100)) OR " +
           "(v.systolicPressure IS NOT NULL AND (v.systolicPressure < 90 OR v.systolicPressure > 140)) OR " +
           "(v.diastolicPressure IS NOT NULL AND (v.diastolicPressure < 60 OR v.diastolicPressure > 90)) OR " +
           "(v.bodyTemperature IS NOT NULL AND (v.bodyTemperature < 36.1 OR v.bodyTemperature > 37.2)) OR " +
           "(v.oxygenSaturation IS NOT NULL AND v.oxygenSaturation < 95) " +
           "ORDER BY v.measuredAt DESC")
    List<VitalSigns> findAbnormalVitalSigns();

    /**
     * Find abnormal vital signs for a specific patient
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.patient.id = :patientId AND (" +
           "(v.heartRate IS NOT NULL AND (v.heartRate < 60 OR v.heartRate > 100)) OR " +
           "(v.systolicPressure IS NOT NULL AND (v.systolicPressure < 90 OR v.systolicPressure > 140)) OR " +
           "(v.diastolicPressure IS NOT NULL AND (v.diastolicPressure < 60 OR v.diastolicPressure > 90)) OR " +
           "(v.bodyTemperature IS NOT NULL AND (v.bodyTemperature < 36.1 OR v.bodyTemperature > 37.2)) OR " +
           "(v.oxygenSaturation IS NOT NULL AND v.oxygenSaturation < 95)) " +
           "ORDER BY v.measuredAt DESC")
    List<VitalSigns> findAbnormalVitalSignsByPatientId(@Param("patientId") Long patientId);

    /**
     * Find recent vital signs (last 24 hours)
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.measuredAt >= :since ORDER BY v.measuredAt DESC")
    List<VitalSigns> findRecentVitalSigns(@Param("since") LocalDateTime since);

    /**
     * Count vital signs by patient
     */
    long countByPatient(Patient patient);

    /**
     * Count vital signs by patient ID
     */
    long countByPatientId(Long patientId);

    /**
     * Find vital signs with high pain levels
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.painLevel IS NOT NULL AND v.painLevel >= :painThreshold " +
           "ORDER BY v.measuredAt DESC")
    List<VitalSigns> findByPainLevelGreaterThanEqual(@Param("painThreshold") Integer painThreshold);

    /**
     * Find average heart rate for a patient in a date range
     */
    @Query("SELECT AVG(v.heartRate) FROM VitalSigns v WHERE v.patient.id = :patientId " +
           "AND v.heartRate IS NOT NULL AND v.measuredAt BETWEEN :startDate AND :endDate")
    Double findAverageHeartRateByPatientAndDateRange(@Param("patientId") Long patientId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find latest vital signs for multiple patients
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.id IN (" +
           "SELECT MAX(v2.id) FROM VitalSigns v2 WHERE v2.patient.id IN :patientIds GROUP BY v2.patient.id)")
    List<VitalSigns> findLatestVitalSignsForPatients(@Param("patientIds") List<Long> patientIds);

    /**
     * Check if vital signs exist for patient in date range
     */
    @Query("SELECT COUNT(v) > 0 FROM VitalSigns v WHERE v.patient.id = :patientId " +
           "AND v.measuredAt BETWEEN :startDate AND :endDate")
    boolean existsByPatientIdAndMeasuredAtBetween(@Param("patientId") Long patientId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Delete old vital signs records (data retention)
     */
    @Query("DELETE FROM VitalSigns v WHERE v.measuredAt < :cutoffDate")
    void deleteOldVitalSigns(@Param("cutoffDate") LocalDateTime cutoffDate);
}
