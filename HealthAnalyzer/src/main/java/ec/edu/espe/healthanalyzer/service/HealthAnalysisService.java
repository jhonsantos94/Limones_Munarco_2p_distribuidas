package ec.edu.espe.healthanalyzer.service;

import ec.edu.espe.healthanalyzer.constant.RiskLevel;
import ec.edu.espe.healthanalyzer.dto.AnalysisRequestDto;
import ec.edu.espe.healthanalyzer.dto.AnalysisResponseDto;
import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.dto.HealthAlertEventDto;
import ec.edu.espe.healthanalyzer.exception.VitalSignAnalysisException;
import ec.edu.espe.healthanalyzer.model.HealthAnalysis;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import ec.edu.espe.healthanalyzer.repository.HealthAnalysisRepository;
import ec.edu.espe.healthanalyzer.repository.PatientHealthProfileRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HealthAnalysisService {

    private final HealthAnalysisRepository healthAnalysisRepository;
    private final PatientHealthProfileRepository patientHealthProfileRepository;
    private final RabbitTemplate rabbitTemplate;
    private final HealthAlertService healthAlertService;
    private final TrendAnalysisService trendAnalysisService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final RiskAssessmentService riskAssessmentService;

    @Transactional(readOnly = true)
    public AnalysisResponseDto analyzeVitalSigns(AnalysisRequestDto request) {
        try {
            VitalSignEventDto vitalSignEvent = request.getVitalSignEvent();
            // Ensure patient ID is set
            vitalSignEvent.setPatientId(request.getPatientId());
            vitalSignEvent.setTimestamp(LocalDateTime.now());

            // Get or create patient profile
            PatientHealthProfile profile = patientHealthProfileRepository.findByPatientId(request.getPatientId())
                    .orElseGet(() -> {
                        PatientHealthProfile newProfile = new PatientHealthProfile();
                        newProfile.setPatientId(request.getPatientId());
                        newProfile.setCreatedAt(LocalDateTime.now());
                        newProfile.setReadingCount(0);
                        return patientHealthProfileRepository.save(newProfile);
                    });

            // Perform analysis
            List<String> anomalies = anomalyDetectionService.detectAnomalies(vitalSignEvent, profile);
            String riskLevel = riskAssessmentService.assessRisk(vitalSignEvent, anomalies);
            List<String> recommendations = generateRecommendations(riskLevel);

            // Create and save analysis
            HealthAnalysis analysis = new HealthAnalysis();
            analysis.setPatientId(request.getPatientId());
            analysis.setTimestamp(LocalDateTime.now());
            analysis.setAnomalies(String.join(", ", anomalies));
            analysis.setRiskLevel(riskLevel);
            analysis.setRecommendations(String.join(", ", recommendations));
            healthAnalysisRepository.save(analysis);

            // If risk level is high or critical, send alert
            if (RiskLevel.HIGH.equals(riskLevel) || RiskLevel.CRITICAL.equals(riskLevel)) {
                sendHealthAlert(vitalSignEvent, analysis);
            }

            // Update patient profile asynchronously
            updatePatientProfile(vitalSignEvent, profile);

            return AnalysisResponseDto.builder()
                    .patientId(request.getPatientId())
                    .analysisType(request.getAnalysisType())
                    .anomalies(anomalies)
                    .riskLevel(riskLevel)
                    .recommendations(recommendations)
                    .createdAt(LocalDateTime.now())
                    .analysisTimestamp(LocalDateTime.now())
                    .status("COMPLETED")
                    .build();

        } catch (Exception e) {
            throw new VitalSignAnalysisException("Error analyzing vital signs", e);
        }
    }

    private List<String> generateRecommendations(String riskLevel) {
        return switch (riskLevel) {
            case RiskLevel.CRITICAL -> java.util.Arrays.asList("Immediate medical attention required", "Contact emergency services");
            case RiskLevel.HIGH -> java.util.Arrays.asList("Urgent medical consultation needed", "Contact your healthcare provider immediately");
            case RiskLevel.MEDIUM -> java.util.Arrays.asList("Schedule a check-up with your healthcare provider within 24 hours");
            case RiskLevel.LOW -> java.util.Arrays.asList("Monitor symptoms", "Contact healthcare provider if condition worsens");
            case RiskLevel.NORMAL -> java.util.Arrays.asList("Continue regular monitoring", "Maintain healthy lifestyle");
            default -> java.util.Arrays.asList("Please consult with your healthcare provider for personalized recommendations");
        };
    }

    @Async
    @Transactional
    public void updatePatientProfile(VitalSignEventDto vitalSignEvent, PatientHealthProfile profile) {
        try {
            // Update averages
            updateVitalSignAverages(vitalSignEvent, profile);
            
            // Analyze trends
            String trends = trendAnalysisService.analyzeTrends(profile);
            profile.setTrends(trends);
            
            // Save updates
            profile.setLastUpdate(LocalDateTime.now());
            patientHealthProfileRepository.save(profile);
            
        } catch (Exception e) {
            throw new VitalSignAnalysisException("Error updating patient profile", e);
        }
    }

    private void updateVitalSignAverages(VitalSignEventDto vitalSign, PatientHealthProfile profile) {
        // Update running averages with new values
        int count = profile.getReadingCount() + 1;
        profile.setHeartRateAvg(updateAverage(profile.getHeartRateAvg(), vitalSign.getHeartRate(), count));
        profile.setBloodPressureSystolicAvg(updateAverage(profile.getBloodPressureSystolicAvg(), 
                vitalSign.getBloodPressureSystolic(), count));
        profile.setBloodPressureDiastolicAvg(updateAverage(profile.getBloodPressureDiastolicAvg(), 
                vitalSign.getBloodPressureDiastolic(), count));
        profile.setTemperatureAvg(updateAverage(profile.getTemperatureAvg(), vitalSign.getTemperature(), count));
        profile.setRespiratoryRateAvg(updateAverage(profile.getRespiratoryRateAvg(), 
                vitalSign.getRespiratoryRate(), count));
        profile.setOxygenSaturationAvg(updateAverage(profile.getOxygenSaturationAvg(), 
                vitalSign.getOxygenSaturation(), count));
        profile.setReadingCount(count);
    }

    private double updateAverage(double currentAvg, double newValue, int count) {
        return ((currentAvg * (count - 1)) + newValue) / count;
    }

    @CircuitBreaker(name = "alertService")
    private void sendHealthAlert(VitalSignEventDto vitalSignEvent, HealthAnalysis analysis) {
        try {
            HealthAlertEventDto alertEvent = new HealthAlertEventDto();
            alertEvent.setPatientId(vitalSignEvent.getPatientId());
            alertEvent.setRiskLevel(analysis.getRiskLevel());
            alertEvent.setAnomalies(analysis.getAnomalies());
            alertEvent.setRecommendations(analysis.getRecommendations());
            alertEvent.setTimestamp(LocalDateTime.now());
            
            healthAlertService.processHealthAlert(alertEvent);
            rabbitTemplate.convertAndSend("health.alerts", alertEvent);
            
        } catch (Exception e) {
            log.error("Failed to send health alert", e);
            throw new VitalSignAnalysisException("Failed to send health alert", e);
        }
    }

    // Additional methods required by the controller
    public List<HealthAnalysis> getPatientAnalyses(String patientId, org.springframework.data.domain.Pageable pageable) {
        return healthAnalysisRepository.findByPatientIdOrderByCreatedAtDesc(patientId, pageable).getContent();
    }

    public HealthAnalysis getLatestAnalysis(String patientId) {
        return healthAnalysisRepository.findFirstByPatientIdOrderByCreatedAtDesc(patientId)
                .orElse(null);
    }

    public List<PatientHealthProfile> getHighRiskPatients() {
        // Return patients with high risk based on recent analyses
        return healthAnalysisRepository.findPatientsWithHighRisk();
    }

    public PatientHealthProfile getPatientProfile(String patientId) {
        return patientHealthProfileRepository.findByPatientId(patientId)
                .orElse(null);
    }

    public PatientHealthProfile savePatientProfile(PatientHealthProfile profile) {
        profile.setLastUpdate(LocalDateTime.now());
        return patientHealthProfileRepository.save(profile);
    }

    public Object getPatientStatistics(String patientId) {
        PatientHealthProfile profile = getPatientProfile(patientId);
        if (profile == null) {
            return null;
        }
        
        // Return statistics object (could be a custom DTO)
        return java.util.Map.of(
            "patientId", patientId,
            "readingCount", profile.getReadingCount() != null ? profile.getReadingCount() : 0,
            "heartRateAvg", profile.getHeartRateAvg(),
            "bloodPressureSystolicAvg", profile.getBloodPressureSystolicAvg(),
            "bloodPressureDiastolicAvg", profile.getBloodPressureDiastolicAvg(),
            "temperatureAvg", profile.getTemperatureAvg(),
            "respiratoryRateAvg", profile.getRespiratoryRateAvg(),
            "oxygenSaturationAvg", profile.getOxygenSaturationAvg(),
            "lastUpdate", profile.getLastUpdate()
        );
    }
    
    // Method required by VitalSignEventListener
    public void processVitalSignEvent(VitalSignEventDto vitalSignEvent) {
        try {
            log.info("Processing vital sign event for patient: {}", vitalSignEvent.getPatientId());
            
            // Create analysis request from vital sign event
            AnalysisRequestDto request = new AnalysisRequestDto();
            request.setPatientId(vitalSignEvent.getPatientId());
            request.setAnalysisType("VITAL_SIGNS");
            request.setDataSource("PATIENT_DATA_COLLECTOR");
            request.setVitalSignEvent(vitalSignEvent);
            
            // Perform analysis
            analyzeVitalSigns(request);
            
        } catch (Exception e) {
            log.error("Error processing vital sign event for patient {}: {}", 
                    vitalSignEvent.getPatientId(), e.getMessage(), e);
            throw new VitalSignAnalysisException("Error processing vital sign event", e);
        }
    }
}
