package ec.edu.espe.healthanalyzer.service;

import ec.edu.espe.healthanalyzer.dto.AnalysisResponseDto;
import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.model.HealthAnalysis;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import ec.edu.espe.healthanalyzer.repository.HealthAnalysisRepository;
import ec.edu.espe.healthanalyzer.repository.PatientHealthProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class HealthAnalysisServiceTest {

    @MockBean
    private HealthAnalysisRepository healthAnalysisRepository;

    @MockBean
    private PatientHealthProfileRepository patientHealthProfileRepository;

    @MockBean
    private AnomalyDetectionService anomalyDetectionService;

    @MockBean
    private RiskAssessmentService riskAssessmentService;

    @MockBean
    private HealthAlertService healthAlertService;

    @MockBean
    private TrendAnalysisService trendAnalysisService;

    private HealthAnalysisService healthAnalysisService;

    private VitalSignEventDto vitalSignEvent;
    private PatientHealthProfile patientProfile;

    @BeforeEach
    void setUp() {
        healthAnalysisService = new HealthAnalysisService(
            healthAnalysisRepository,
            patientHealthProfileRepository,
            anomalyDetectionService,
            riskAssessmentService,
            healthAlertService,
            trendAnalysisService
        );

        // Setup test data
        vitalSignEvent = new VitalSignEventDto();
        vitalSignEvent.setPatientId("PATIENT-001");
        vitalSignEvent.setTimestamp(LocalDateTime.now());
        vitalSignEvent.setHeartRate(75.0);
        vitalSignEvent.setBloodPressureSystolic(120.0);
        vitalSignEvent.setBloodPressureDiastolic(80.0);
        vitalSignEvent.setTemperature(36.5);
        vitalSignEvent.setOxygenSaturation(98.0);
        vitalSignEvent.setRespiratoryRate(16.0);

        patientProfile = new PatientHealthProfile();
        patientProfile.setPatientId("PATIENT-001");
        patientProfile.setAge(45);
        patientProfile.setGender("M");
        patientProfile.setMedicalConditions("Hypertension");
    }

    @Test
    void testAnalyzeVitalSigns_NormalValues() {
        // Arrange
        when(patientHealthProfileRepository.findByPatientId("PATIENT-001"))
            .thenReturn(Optional.of(patientProfile));
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.Collections.emptyList());
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskAssessmentService.RiskLevel.LOW);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(vitalSignEvent, patientProfile);

        // Assert
        assertNotNull(response);
        assertEquals("PATIENT-001", response.getPatientId());
        assertEquals(RiskAssessmentService.RiskLevel.LOW, response.getRiskLevel());
        assertNotNull(response.getRecommendations());
        
        verify(healthAnalysisRepository).save(any(HealthAnalysis.class));
    }

    @Test
    void testAnalyzeVitalSigns_HighRiskValues() {
        // Arrange
        vitalSignEvent.setHeartRate(150.0);
        vitalSignEvent.setBloodPressureSystolic(180.0);
        
        when(patientHealthProfileRepository.findByPatientId("PATIENT-001"))
            .thenReturn(Optional.of(patientProfile));
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.List.of("Tachycardia detected", "Hypertension detected"));
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskAssessmentService.RiskLevel.HIGH);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(vitalSignEvent, patientProfile);

        // Assert
        assertNotNull(response);
        assertEquals("PATIENT-001", response.getPatientId());
        assertEquals(RiskAssessmentService.RiskLevel.HIGH, response.getRiskLevel());
        assertNotNull(response.getAnomalies());
        assertTrue(response.getAnomalies().size() > 0);
        
        verify(healthAlertService).sendAlert(any());
    }

    @Test
    void testProcessVitalSignEvent() {
        // Arrange
        when(patientHealthProfileRepository.findByPatientId("PATIENT-001"))
            .thenReturn(Optional.of(patientProfile));
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.Collections.emptyList());
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskAssessmentService.RiskLevel.LOW);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        assertDoesNotThrow(() -> healthAnalysisService.processVitalSignEvent(vitalSignEvent));

        // Assert
        verify(healthAnalysisRepository).save(any(HealthAnalysis.class));
        verify(patientHealthProfileRepository).save(any(PatientHealthProfile.class));
    }

    @Test
    void testAnalyzeVitalSigns_WithNullPatientProfile() {
        // Arrange
        when(patientHealthProfileRepository.findByPatientId("PATIENT-001"))
            .thenReturn(Optional.empty());
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.Collections.emptyList());
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskAssessmentService.RiskLevel.LOW);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(vitalSignEvent, null);

        // Assert
        assertNotNull(response);
        assertEquals("PATIENT-001", response.getPatientId());
        assertEquals(RiskAssessmentService.RiskLevel.LOW, response.getRiskLevel());
    }
}
