package ec.edu.espe.healthanalyzer.controller;

import ec.edu.espe.healthanalyzer.constant.RiskLevel;
import ec.edu.espe.healthanalyzer.dto.AnalysisRequestDto;
import ec.edu.espe.healthanalyzer.dto.AnalysisResponseDto;
import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import ec.edu.espe.healthanalyzer.service.HealthAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(HealthAnalysisController.class)
@ActiveProfiles("test")
class HealthAnalysisControllerTest {
    
    private static final String TEST_PATIENT_ID = "PATIENT-001";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private HealthAnalysisService healthAnalysisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAnalyzeVitalSigns() throws Exception {
        // Arrange
        VitalSignEventDto vitalSignEvent = new VitalSignEventDto();
        vitalSignEvent.setPatientId(TEST_PATIENT_ID);
        vitalSignEvent.setTimestamp(LocalDateTime.now());
        vitalSignEvent.setHeartRate(75.0);
        vitalSignEvent.setBloodPressureSystolic(120.0);
        vitalSignEvent.setBloodPressureDiastolic(80.0);
        vitalSignEvent.setTemperature(36.5);

        PatientHealthProfile profile = new PatientHealthProfile();
        profile.setPatientId(TEST_PATIENT_ID);
        profile.setAge(45);

        AnalysisRequestDto request = new AnalysisRequestDto();
        request.setVitalSignEvent(vitalSignEvent);
        request.setPatientProfile(profile);
        request.setPatientId(TEST_PATIENT_ID);

        AnalysisResponseDto expectedResponse = new AnalysisResponseDto();
        expectedResponse.setPatientId(TEST_PATIENT_ID);
        expectedResponse.setRiskLevel(RiskLevel.LOW);
        expectedResponse.setRecommendations(List.of("Continue normal monitoring"));

        when(healthAnalysisService.analyzeVitalSigns(any(AnalysisRequestDto.class)))
            .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/health-analyzer/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(TEST_PATIENT_ID))
                .andExpect(jsonPath("$.riskLevel").value("LOW"));
    }

    @Test
    void testGetPatientProfile() throws Exception {
        // Arrange
        PatientHealthProfile profile = new PatientHealthProfile();
        profile.setPatientId(TEST_PATIENT_ID);
        profile.setAge(45);
        profile.setGender("M");

        when(healthAnalysisService.getPatientProfile(any(String.class)))
            .thenAnswer(invocation -> Optional.of(profile));

        // Act & Assert
        mockMvc.perform(get("/api/health-analyzer/profile/" + TEST_PATIENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(TEST_PATIENT_ID))
                .andExpect(jsonPath("$.age").value(45))
                .andExpect(jsonPath("$.gender").value("M"));
    }

    @Test
    void testGetPatientProfileNotFound() throws Exception {
        // Arrange
        when(healthAnalysisService.getPatientProfile("NONEXISTENT"))
            .thenAnswer(invocation -> Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/health-analyzer/profile/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/health-analyzer/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("HealthAnalyzer"));
    }
}
