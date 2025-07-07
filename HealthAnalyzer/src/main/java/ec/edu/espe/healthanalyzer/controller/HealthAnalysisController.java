package ec.edu.espe.healthanalyzer.controller;

import ec.edu.espe.healthanalyzer.dto.AnalysisRequestDto;
import ec.edu.espe.healthanalyzer.dto.AnalysisResponseDto;
import ec.edu.espe.healthanalyzer.model.HealthAnalysis;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import ec.edu.espe.healthanalyzer.service.HealthAnalysisService;
import ec.edu.espe.healthanalyzer.service.RiskAssessmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/health-analyzer")
@CrossOrigin(origins = "*")
public class HealthAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(HealthAnalysisController.class);
    
    @Autowired
    private HealthAnalysisService healthAnalysisService;
    
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponseDto> analyzeVitalSigns(@Valid @RequestBody AnalysisRequestDto request) {
        try {
            logger.info("Received analysis request for patient: {}", request.getPatientId());
            
            AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(request);
            
            logger.info("Analysis completed for patient: {} with risk level: {}", 
                request.getPatientId(), response.getRiskLevel());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error analyzing vital signs for patient {}: {}", 
                request.getPatientId(), e.getMessage());
            
            AnalysisResponseDto errorResponse = AnalysisResponseDto.builder()
                .patientId(request.getPatientId())
                .riskLevel("MODERATE")
                .recommendations(List.of("Error occurred during analysis. Please consult healthcare provider."))
                .build();
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/analysis/{patientId}")
    public ResponseEntity<List<HealthAnalysis>> getPatientAnalyses(
            @PathVariable String patientId,
            Pageable pageable) {
        try {
            logger.info("Retrieving analyses for patient: {}", patientId);
            
            List<HealthAnalysis> analyses = healthAnalysisService.getPatientAnalyses(patientId, pageable);
            
            return ResponseEntity.ok(analyses);
            
        } catch (Exception e) {
            logger.error("Error retrieving analyses for patient {}: {}", patientId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/analysis/{patientId}/latest")
    public ResponseEntity<HealthAnalysis> getLatestAnalysis(@PathVariable String patientId) {
        try {
            logger.info("Retrieving latest analysis for patient: {}", patientId);
            
            HealthAnalysis latestAnalysis = healthAnalysisService.getLatestAnalysis(patientId);
            
            if (latestAnalysis != null) {
                return ResponseEntity.ok(latestAnalysis);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving latest analysis for patient {}: {}", patientId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/analysis/high-risk")
    public ResponseEntity<List<PatientHealthProfile>> getHighRiskPatients() {
        try {
            logger.info("Retrieving high-risk patients");
            
            List<PatientHealthProfile> highRiskPatients = healthAnalysisService.getHighRiskPatients();
            
            return ResponseEntity.ok(highRiskPatients);
            
        } catch (Exception e) {
            logger.error("Error retrieving high-risk patients: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/profile/{patientId}")
    public ResponseEntity<PatientHealthProfile> getPatientProfile(@PathVariable String patientId) {
        try {
            logger.info("Retrieving profile for patient: {}", patientId);
            
            PatientHealthProfile profile = healthAnalysisService.getPatientProfile(patientId);
            
            if (profile != null) {
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving profile for patient {}: {}", patientId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/profile")
    public ResponseEntity<PatientHealthProfile> createOrUpdateProfile(
            @Valid @RequestBody PatientHealthProfile profile) {
        try {
            logger.info("Creating/updating profile for patient: {}", profile.getPatientId());
            
            PatientHealthProfile savedProfile = healthAnalysisService.savePatientProfile(profile);
            
            return ResponseEntity.ok(savedProfile);
            
        } catch (Exception e) {
            logger.error("Error creating/updating profile for patient {}: {}", 
                profile.getPatientId(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/statistics/{patientId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getPatientStatistics(@PathVariable String patientId) {
        try {
            logger.info("Retrieving statistics for patient: {}", patientId);
            
            Object statisticsObj = healthAnalysisService.getPatientStatistics(patientId);
            Map<String, Object> statistics = (Map<String, Object>) statisticsObj;
            
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error retrieving statistics for patient {}: {}", patientId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "HealthAnalyzer",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
