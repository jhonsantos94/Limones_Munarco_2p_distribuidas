package ec.edu.espe.healthanalyzer.repository;

import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientHealthProfileRepository extends JpaRepository<PatientHealthProfile, Long> {
    
    Optional<PatientHealthProfile> findByPatientId(String patientId);
    
    boolean existsByPatientId(String patientId);
}
