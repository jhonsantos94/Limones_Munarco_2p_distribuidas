package ec.edu.espe.healthanalyzer.controller;

import ec.edu.espe.healthanalyzer.dto.AnalysisRequestDto;
import ec.edu.espe.healthanalyzer.dto.AnalysisResponseDto;
import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import ec.edu.espe.healthanalyzer.service.HealthAnalysisService;
import ec.edu.espe.healthanalyzer.service.RiskAssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(HealthAnalysisController.class)
@ActiveProfiles("test")
class HealthAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthAnalysisService healthAnalysisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAnalyzeVitalSigns() throws Exception {
        // Arrange
        VitalSignEventDto vitalSignEvent = new VitalSignEventDto();
        vitalSignEvent.setPatientId("PATIENT-001");
        vitalSignEvent.setTimestamp(LocalDateTime.now());
        vitalSignEvent.setHeartRate(75.0);
        vitalSignEvent.setBloodPressureSystolic(120.0);
        vitalSignEvent.setBloodPressureDiastolic(80.0);
        vitalSignEvent.setTemperature(36.5);

        PatientHealthProfile profile = new PatientHealthProfile();
        profile.setPatientId("PATIENT-001");
        profile.setAge(45);

        AnalysisRequestDto request = new AnalysisRequestDto();
        request.setVitalSignEvent(vitalSignEvent);
        request.setPatientProfile(profile);
        request.setPatientId("PATIENT-001");

        AnalysisResponseDto expectedResponse = new AnalysisResponseDto();
        expectedResponse.setPatientId("PATIENT-001");
        expectedResponse.setRiskLevel(RiskAssessmentService.RiskLevel.LOW);
        expectedResponse.setRecommendations(List.of("Continue normal monitoring"));

        when(healthAnalysisService.analyzeVitalSigns(any(), any()))
            .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/health-analyzer/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value("PATIENT-001"))
                .andExpect(jsonPath("$.riskLevel").value("LOW"));
    }

    @Test
    void testGetPatientProfile() throws Exception {
        // Arrange
        PatientHealthProfile profile = new PatientHealthProfile();
        profile.setPatientId("PATIENT-001");
        profile.setAge(45);
        profile.setGender("M");

        when(healthAnalysisService.getPatientProfile("PATIENT-001"))
            .thenReturn(Optional.of(profile));

        // Act & Assert
        mockMvc.perform(get("/api/health-analyzer/profile/PATIENT-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value("PATIENT-001"))
                .andExpect(jsonPath("$.age").value(45))
                .andExpect(jsonPath("$.gender").value("M"));
    }

    @Test
    void testGetPatientProfileNotFound() throws Exception {
        // Arrange
        when(healthAnalysisService.getPatientProfile("NONEXISTENT"))
            .thenReturn(Optional.empty());

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
