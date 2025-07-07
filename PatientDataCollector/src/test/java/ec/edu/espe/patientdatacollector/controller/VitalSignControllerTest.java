package ec.edu.espe.patientdatacollector.controller;

import ec.edu.espe.patientdatacollector.dto.VitalSignRequestDto;
import ec.edu.espe.patientdatacollector.service.VitalSignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VitalSignController.class)
class VitalSignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VitalSignService vitalSignService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/conjunta/2p/vital-signs/health"))
                .andExpect(status().isOk());
    }

    @Test
    void receiveVitalSigns_WithValidData_ShouldReturnCreated() throws Exception {
        VitalSignRequestDto requestDto = new VitalSignRequestDto();
        requestDto.setDeviceId("D001");
        requestDto.setType("heart-rate");
        requestDto.setValue(75.0);
        requestDto.setTimestamp(LocalDateTime.now());
        requestDto.setPatientId("P001");
        requestDto.setUnit("bpm");

        mockMvc.perform(post("/conjunta/2p/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void receiveVitalSigns_WithInvalidType_ShouldReturnBadRequest() throws Exception {
        VitalSignRequestDto requestDto = new VitalSignRequestDto();
        requestDto.setDeviceId("D001");
        requestDto.setType("invalid-type");
        requestDto.setValue(75.0);
        requestDto.setTimestamp(LocalDateTime.now());

        mockMvc.perform(post("/conjunta/2p/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
