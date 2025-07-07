package ec.edu.espe.carenotifier.controller;

import ec.edu.espe.carenotifier.model.EmergencyContact;
import ec.edu.espe.carenotifier.service.EmergencyContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emergency-contacts")
@RequiredArgsConstructor
@Slf4j
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @PostMapping
    public ResponseEntity<EmergencyContact> createEmergencyContact(
            @Valid @RequestBody EmergencyContact contact) {
        
        log.info("Creating emergency contact for patient: {}", contact.getPatientId());
        EmergencyContact saved = emergencyContactService.createEmergencyContact(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<EmergencyContact>> getContactsByPatient(
            @PathVariable String patientId) {
        
        log.info("Retrieving emergency contacts for patient: {}", patientId);
        List<EmergencyContact> contacts = emergencyContactService.getAllContactsByPatientId(patientId);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<List<EmergencyContact>> getActiveContactsByPatient(
            @PathVariable String patientId) {
        
        log.info("Retrieving active emergency contacts for patient: {}", patientId);
        List<EmergencyContact> contacts = emergencyContactService.getActiveContactsByPatientId(patientId);
        return ResponseEntity.ok(contacts);
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<EmergencyContact> updateEmergencyContact(
            @PathVariable Long contactId,
            @Valid @RequestBody EmergencyContact contact) {
        
        log.info("Updating emergency contact: {}", contactId);
        EmergencyContact updated = emergencyContactService.updateEmergencyContact(contactId, contact);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deactivateEmergencyContact(
            @PathVariable Long contactId) {
        
        log.info("Deactivating emergency contact: {}", contactId);
        emergencyContactService.deactivateEmergencyContact(contactId);
        return ResponseEntity.ok().build();
    }
}
