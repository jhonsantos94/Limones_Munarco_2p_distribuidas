package ec.edu.espe.healthanalyzer.service;

import ec.edu.espe.healthanalyzer.dto.AnalysisRequestDto;
import ec.edu.espe.healthanalyzer.dto.AnalysisResponseDto;
import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.model.HealthAnalysis;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import ec.edu.espe.healthanalyzer.constant.RiskLevel;
import ec.edu.espe.healthanalyzer.repository.HealthAnalysisRepository;
import ec.edu.espe.healthanalyzer.repository.PatientHealthProfileRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private static final String TEST_PATIENT_ID = "PATIENT-001";

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

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        healthAnalysisService = new HealthAnalysisService(
            healthAnalysisRepository,
            patientHealthProfileRepository,
            rabbitTemplate,
            healthAlertService,
            trendAnalysisService,
            anomalyDetectionService,
            riskAssessmentService
        );

        // Setup test data
        vitalSignEvent = new VitalSignEventDto();
        vitalSignEvent.setPatientId(TEST_PATIENT_ID);
        vitalSignEvent.setTimestamp(LocalDateTime.now());
        vitalSignEvent.setHeartRate(75.0);
        vitalSignEvent.setBloodPressureSystolic(120.0);
        vitalSignEvent.setBloodPressureDiastolic(80.0);
        vitalSignEvent.setTemperature(36.5);
        vitalSignEvent.setOxygenSaturation(98.0);
        vitalSignEvent.setRespiratoryRate(16.0);

        patientProfile = new PatientHealthProfile();
        patientProfile.setPatientId(TEST_PATIENT_ID);
        patientProfile.setAge(45);
        patientProfile.setGender("M");
        patientProfile.setMedicalConditions("Hypertension");
    }

    @Test
    void analyzeVitalSignsNormalValues() {
        // Arrange
        when(patientHealthProfileRepository.findByPatientId(TEST_PATIENT_ID))
            .thenReturn(Optional.of(patientProfile));
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.Collections.emptyList());
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskLevel.LOW);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AnalysisRequestDto request = new AnalysisRequestDto();
        request.setPatientId(TEST_PATIENT_ID);
        request.setVitalSignEvent(vitalSignEvent);
        request.setPatientProfile(patientProfile);

        AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_PATIENT_ID, response.getPatientId());
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
        assertNotNull(response.getRecommendations());
        
        verify(healthAnalysisRepository).save(any(HealthAnalysis.class));
    }

    @Test
    void analyzeVitalSignsHighRiskValues() {
        // Arrange
        vitalSignEvent.setHeartRate(150.0);
        vitalSignEvent.setBloodPressureSystolic(180.0);
        
        when(patientHealthProfileRepository.findByPatientId(TEST_PATIENT_ID))
            .thenReturn(Optional.of(patientProfile));
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.List.of("Tachycardia detected", "Hypertension detected"));
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskLevel.HIGH);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AnalysisRequestDto request = new AnalysisRequestDto();
        request.setPatientId(TEST_PATIENT_ID);
        request.setVitalSignEvent(vitalSignEvent);
        request.setPatientProfile(patientProfile);

        AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_PATIENT_ID, response.getPatientId());
        assertEquals(RiskLevel.HIGH, response.getRiskLevel());
        assertNotNull(response.getAnomalies());
        assertTrue(response.getAnomalies().size() > 0);
        
        verify(healthAlertService).sendAlert(any());
    }

    @Test
    void testProcessVitalSignEvent() {
        // Arrange
        when(patientHealthProfileRepository.findByPatientId(TEST_PATIENT_ID))
            .thenReturn(Optional.of(patientProfile));
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.Collections.emptyList());
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskLevel.LOW);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        assertDoesNotThrow(() -> healthAnalysisService.processVitalSignEvent(vitalSignEvent));

        // Assert
        verify(healthAnalysisRepository).save(any(HealthAnalysis.class));
        verify(patientHealthProfileRepository).save(any(PatientHealthProfile.class));
    }

    @Test
    void analyzeVitalSignsWithNullPatientProfile() {
        // Arrange
        when(patientHealthProfileRepository.findByPatientId(TEST_PATIENT_ID))
            .thenReturn(Optional.empty());
        when(anomalyDetectionService.detectAnomalies(any(), any()))
            .thenReturn(java.util.Collections.emptyList());
        when(riskAssessmentService.assessOverallRisk(any(), any(), any()))
            .thenReturn(RiskLevel.LOW);
        when(healthAnalysisRepository.save(any(HealthAnalysis.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AnalysisRequestDto request = new AnalysisRequestDto();
        request.setPatientId(TEST_PATIENT_ID);
        request.setVitalSignEvent(vitalSignEvent);
        request.setPatientProfile(null);

        AnalysisResponseDto response = healthAnalysisService.analyzeVitalSigns(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_PATIENT_ID, response.getPatientId());
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
    }
}
