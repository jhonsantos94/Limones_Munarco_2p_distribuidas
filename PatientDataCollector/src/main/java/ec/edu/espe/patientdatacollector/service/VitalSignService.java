package ec.edu.espe.patientdatacollector.service;

import ec.edu.espe.patientdatacollector.dto.NewVitalSignEventDto;
import ec.edu.espe.patientdatacollector.dto.VitalSignRequestDto;
import ec.edu.espe.patientdatacollector.dto.VitalSignResponseDto;
import ec.edu.espe.patientdatacollector.model.VitalSign;
import ec.edu.espe.patientdatacollector.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VitalSignService {
    
    private final VitalSignRepository vitalSignRepository;
    private final EventPublisherService eventPublisherService;
    
    @Transactional
    public VitalSignResponseDto saveVitalSign(VitalSignRequestDto requestDto) {
        // Validar rangos médicos
        validateVitalSignRanges(requestDto);
        
        // Crear entidad
        VitalSign vitalSign = new VitalSign();
        vitalSign.setDeviceId(requestDto.getDeviceId());
        vitalSign.setType(requestDto.getType());
        vitalSign.setValue(requestDto.getValue());
        vitalSign.setTimestamp(requestDto.getTimestamp());
        vitalSign.setPatientId(requestDto.getPatientId());
        vitalSign.setUnit(requestDto.getUnit());
        vitalSign.setStatus(determineStatus(requestDto.getType(), requestDto.getValue()));
        
        // Guardar en base de datos
        VitalSign saved = vitalSignRepository.save(vitalSign);
        
        // Crear evento para publicar
        NewVitalSignEventDto eventDto = new NewVitalSignEventDto();
        eventDto.setEventId("EVT-" + UUID.randomUUID().toString());
        eventDto.setDeviceId(saved.getDeviceId());
        eventDto.setType(saved.getType());
        eventDto.setValue(saved.getValue());
        eventDto.setTimestamp(saved.getTimestamp());
        eventDto.setPatientId(saved.getPatientId());
        eventDto.setUnit(saved.getUnit());
        eventDto.setStatus(saved.getStatus());
        
        // Publicar evento
        eventPublisherService.publishNewVitalSignEvent(eventDto);
        
        log.info("Vital sign saved: {} for device {}", saved.getType(), saved.getDeviceId());
        
        return mapToResponseDto(saved);
    }
    
    public List<VitalSignResponseDto> getVitalSignsByDevice(String deviceId) {
        List<VitalSign> vitalSigns = vitalSignRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
        return vitalSigns.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<VitalSignResponseDto> getVitalSignsByDeviceAndDateRange(String deviceId, LocalDateTime start, LocalDateTime end) {
        List<VitalSign> vitalSigns = vitalSignRepository.findByDeviceIdAndTimestampBetween(deviceId, start, end);
        return vitalSigns.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    private void validateVitalSignRanges(VitalSignRequestDto requestDto) {
        String type = requestDto.getType();
        Double value = requestDto.getValue();
        
        switch (type) {
            case "heart-rate":
                if (value < 30 || value > 200) {
                    throw new IllegalArgumentException("Heart rate must be between 30 and 200 bpm");
                }
                break;
            case "blood-pressure-systolic":
                if (value < 70 || value > 250) {
                    throw new IllegalArgumentException("Systolic blood pressure must be between 70 and 250 mmHg");
                }
                break;
            case "blood-pressure-diastolic":
                if (value < 40 || value > 150) {
                    throw new IllegalArgumentException("Diastolic blood pressure must be between 40 and 150 mmHg");
                }
                break;
            case "oxygen-saturation":
                if (value < 50 || value > 100) {
                    throw new IllegalArgumentException("Oxygen saturation must be between 50 and 100%");
                }
                break;
            case "temperature":
                if (value < 30 || value > 45) {
                    throw new IllegalArgumentException("Temperature must be between 30 and 45°C");
                }
                break;
            case "respiratory-rate":
                if (value < 8 || value > 40) {
                    throw new IllegalArgumentException("Respiratory rate must be between 8 and 40 breaths/min");
                }
                break;
        }
    }
    
    private String determineStatus(String type, Double value) {
        switch (type) {
            case "heart-rate":
                if (value > 140 || value < 40) return "CRITICAL";
                if (value > 120 || value < 50) return "WARNING";
                return "NORMAL";
            case "oxygen-saturation":
                if (value < 90) return "CRITICAL";
                if (value < 95) return "WARNING";
                return "NORMAL";
            case "blood-pressure-systolic":
                if (value > 180) return "CRITICAL";
                if (value > 140) return "WARNING";
                return "NORMAL";
            case "blood-pressure-diastolic":
                if (value > 120) return "CRITICAL";
                if (value > 90) return "WARNING";
                return "NORMAL";
            default:
                return "NORMAL";
        }
    }
    
    private VitalSignResponseDto mapToResponseDto(VitalSign vitalSign) {
        VitalSignResponseDto dto = new VitalSignResponseDto();
        dto.setId(vitalSign.getId());
        dto.setDeviceId(vitalSign.getDeviceId());
        dto.setType(vitalSign.getType());
        dto.setValue(vitalSign.getValue());
        dto.setTimestamp(vitalSign.getTimestamp());
        dto.setPatientId(vitalSign.getPatientId());
        dto.setUnit(vitalSign.getUnit());
        dto.setStatus(vitalSign.getStatus());
        return dto;
    }
}
