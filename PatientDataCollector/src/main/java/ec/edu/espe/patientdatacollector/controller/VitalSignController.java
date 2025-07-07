package ec.edu.espe.patientdatacollector.controller;

import ec.edu.espe.patientdatacollector.dto.VitalSignRequestDto;
import ec.edu.espe.patientdatacollector.dto.VitalSignResponseDto;
import ec.edu.espe.patientdatacollector.service.VitalSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/conjunta/2p/vital-signs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient Data Collector", description = "API for collecting and managing vital signs from medical devices")
public class VitalSignController {
    
    private final VitalSignService vitalSignService;
    
    @PostMapping
    @Operation(summary = "Receive vital signs data from medical devices")
    @ApiResponse(responseCode = "201", description = "Vital sign data received and processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid vital sign data")
    public ResponseEntity<VitalSignResponseDto> receiveVitalSigns(
            @Valid @RequestBody VitalSignRequestDto vitalSignRequest) {
        
        log.info("Received vital sign data from device: {}", vitalSignRequest.getDeviceId());
        
        try {
            VitalSignResponseDto response = vitalSignService.saveVitalSign(vitalSignRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid vital sign data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Error processing vital sign data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{deviceId}")
    @Operation(summary = "Get vital signs history for a specific device")
    @ApiResponse(responseCode = "200", description = "Vital signs history retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    public ResponseEntity<List<VitalSignResponseDto>> getVitalSignsByDevice(
            @Parameter(description = "Device ID") @PathVariable String deviceId) {
        
        log.info("Retrieving vital signs for device: {}", deviceId);
        
        List<VitalSignResponseDto> vitalSigns = vitalSignService.getVitalSignsByDevice(deviceId);
        
        if (vitalSigns.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(vitalSigns);
    }
    
    @GetMapping("/{deviceId}/range")
    @Operation(summary = "Get vital signs for a device within a date range")
    @ApiResponse(responseCode = "200", description = "Vital signs retrieved successfully")
    public ResponseEntity<List<VitalSignResponseDto>> getVitalSignsByDeviceAndDateRange(
            @Parameter(description = "Device ID") @PathVariable String deviceId,
            @Parameter(description = "Start date and time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date and time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Retrieving vital signs for device: {} between {} and {}", deviceId, startDate, endDate);
        
        List<VitalSignResponseDto> vitalSigns = vitalSignService.getVitalSignsByDeviceAndDateRange(deviceId, startDate, endDate);
        
        return ResponseEntity.ok(vitalSigns);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("PatientDataCollector service is running");
    }
}
