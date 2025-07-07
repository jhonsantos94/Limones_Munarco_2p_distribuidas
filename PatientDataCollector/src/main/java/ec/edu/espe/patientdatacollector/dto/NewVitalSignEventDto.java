package ec.edu.espe.patientdatacollector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewVitalSignEventDto {
    
    private String eventId;
    private String deviceId;
    private String type;
    private Double value;
    private LocalDateTime timestamp;
    private String patientId;
    private String unit;
    private String status;
}
