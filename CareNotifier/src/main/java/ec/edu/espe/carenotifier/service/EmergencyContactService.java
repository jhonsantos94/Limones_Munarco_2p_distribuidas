package ec.edu.espe.carenotifier.service;

import ec.edu.espe.carenotifier.model.EmergencyContact;
import ec.edu.espe.carenotifier.repository.EmergencyContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;

    @Transactional(readOnly = true)
    public List<EmergencyContact> getActiveContactsByPatientId(String patientId) {
        return emergencyContactRepository.findByPatientIdAndIsActiveTrue(patientId);
    }

    @Transactional(readOnly = true)
    public Optional<EmergencyContact> getPrimaryContactByPatientId(String patientId) {
        return emergencyContactRepository.findByPatientIdAndIsPrimaryTrueAndIsActiveTrue(patientId);
    }

    @Transactional
    public EmergencyContact createEmergencyContact(EmergencyContact contact) {
        // If this is set as primary, make sure no other contact is primary for this patient
        if (Boolean.TRUE.equals(contact.getIsPrimary())) {
            List<EmergencyContact> existingPrimary = emergencyContactRepository
                    .findByPatientIdAndIsPrimaryTrue(contact.getPatientId());
            
            for (EmergencyContact existing : existingPrimary) {
                existing.setIsPrimary(false);
                emergencyContactRepository.save(existing);
            }
        }
        
        return emergencyContactRepository.save(contact);
    }

    @Transactional
    public EmergencyContact updateEmergencyContact(Long contactId, EmergencyContact updatedContact) {
        return emergencyContactRepository.findById(contactId)
                .map(existing -> {
                    existing.setContactName(updatedContact.getContactName());
                    existing.setRelationship(updatedContact.getRelationship());
                    existing.setPhoneNumber(updatedContact.getPhoneNumber());
                    existing.setEmail(updatedContact.getEmail());
                    existing.setIsPrimary(updatedContact.getIsPrimary());
                    existing.setIsActive(updatedContact.getIsActive());
                    
                    // Handle primary contact logic
                    if (Boolean.TRUE.equals(updatedContact.getIsPrimary())) {
                        List<EmergencyContact> otherPrimary = emergencyContactRepository
                                .findByPatientIdAndIsPrimaryTrue(existing.getPatientId())
                                .stream()
                                .filter(contact -> !contact.getId().equals(contactId))
                                .toList();
                        
                        for (EmergencyContact other : otherPrimary) {
                            other.setIsPrimary(false);
                            emergencyContactRepository.save(other);
                        }
                    }
                    
                    return emergencyContactRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Emergency contact not found"));
    }

    @Transactional
    public void deactivateEmergencyContact(Long contactId) {
        emergencyContactRepository.findById(contactId)
                .ifPresent(contact -> {
                    contact.setIsActive(false);
                    emergencyContactRepository.save(contact);
                });
    }

    @Transactional(readOnly = true)
    public List<EmergencyContact> getAllContactsByPatientId(String patientId) {
        return emergencyContactRepository.findByPatientIdOrderByIsPrimaryDescCreatedAtDesc(patientId);
    }
}
