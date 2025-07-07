package ec.edu.espe.healthanalyzer.repository;

import ec.edu.espe.healthanalyzer.model.HealthAnalysis;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
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
public interface HealthAnalysisRepository extends JpaRepository<HealthAnalysis, Long> {
    
    List<HealthAnalysis> findByPatientIdOrderByCreatedAtDesc(String patientId);
    
    List<HealthAnalysis> findByPatientIdAndAnalysisTypeOrderByCreatedAtDesc(
            String patientId, String analysisType);
    
    List<HealthAnalysis> findByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String patientId, LocalDateTime start, LocalDateTime end);
    
    List<HealthAnalysis> findByStatusOrderByCreatedAtAsc(String status);
    
    List<HealthAnalysis> findByRiskLevelInOrderByCreatedAtDesc(List<String> riskLevels);
    
    @Query("SELECT ha FROM HealthAnalysis ha WHERE ha.riskLevel IN ('HIGH', 'CRITICAL') " +
           "AND ha.createdAt >= :since ORDER BY ha.createdAt DESC")
    List<HealthAnalysis> findCriticalAnalysesSince(LocalDateTime since);
    
    Page<HealthAnalysis> findByPatientIdOrderByCreatedAtDesc(String patientId, Pageable pageable);
    
    Optional<HealthAnalysis> findFirstByPatientIdOrderByCreatedAtDesc(String patientId);
    
    List<HealthAnalysis> findTop30ByPatientIdOrderByCreatedAtDesc(String patientId);
    
    @Query("SELECT ha FROM HealthAnalysis ha WHERE ha.patientId = :patientId AND ha.riskLevel IN ('HIGH', 'CRITICAL') ORDER BY ha.createdAt DESC")
    List<HealthAnalysis> findHighRiskAnalysesByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT ha FROM HealthAnalysis ha WHERE ha.createdAt >= :fromDate ORDER BY ha.createdAt DESC")
    List<HealthAnalysis> findRecentAnalyses(@Param("fromDate") java.time.LocalDateTime fromDate);
    
    @Query("SELECT DISTINCT php FROM PatientHealthProfile php JOIN HealthAnalysis ha ON php.patientId = ha.patientId WHERE ha.riskLevel IN ('HIGH', 'CRITICAL') AND ha.createdAt >= :since")
    List<PatientHealthProfile> findPatientsWithHighRisk(@Param("since") LocalDateTime since);
    
    default List<PatientHealthProfile> findPatientsWithHighRisk() {
        return findPatientsWithHighRisk(LocalDateTime.now().minusDays(7)); // Last 7 days
    }
    
    long countByPatientId(String patientId);
}
