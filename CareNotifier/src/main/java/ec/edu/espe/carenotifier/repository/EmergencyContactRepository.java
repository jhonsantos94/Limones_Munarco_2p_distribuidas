package ec.edu.espe.carenotifier.repository;

import ec.edu.espe.carenotifier.model.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    
    List<EmergencyContact> findByPatientIdAndIsActiveTrue(String patientId);
    
    Optional<EmergencyContact> findByPatientIdAndIsPrimaryTrueAndIsActiveTrue(String patientId);
    
    List<EmergencyContact> findByPatientIdAndIsPrimaryTrue(String patientId);
    
    List<EmergencyContact> findByPatientIdOrderByIsPrimaryDescCreatedAtDesc(String patientId);
}
