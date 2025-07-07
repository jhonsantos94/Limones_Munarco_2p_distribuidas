package ec.edu.espe.patientdatacollector.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitalSign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "vital_value", nullable = false)
    private Double value;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "patient_id")
    private String patientId;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "status")
    private String status = "NORMAL"; // NORMAL, WARNING, CRITICAL
}
