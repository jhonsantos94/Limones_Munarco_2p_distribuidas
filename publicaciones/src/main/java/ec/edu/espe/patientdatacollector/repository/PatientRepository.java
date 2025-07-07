package ec.edu.espe.patientdatacollector.repository;

import ec.edu.espe.patientdatacollector.model.Patient;
import ec.edu.espe.patientdatacollector.model.DocumentType;
import ec.edu.espe.patientdatacollector.model.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find patient by document number and type
     */
    Optional<Patient> findByDocumentNumberAndDocumentType(String documentNumber, DocumentType documentType);

    /**
     * Find patient by medical record number
     */
    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);

    /**
     * Find patients by status
     */
    List<Patient> findByStatus(PatientStatus status);

    /**
     * Find patients by status with pagination
     */
    Page<Patient> findByStatus(PatientStatus status, Pageable pageable);

    /**
     * Find patients by first name and last name (case insensitive)
     */
    @Query("SELECT p FROM Patient p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
           "AND LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Patient> findByFirstNameAndLastNameIgnoreCase(@Param("firstName") String firstName, 
                                                        @Param("lastName") String lastName);

    /**
     * Search patients by name (first or last name) with pagination
     */
    @Query("SELECT p FROM Patient p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Patient> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Find patients by email
     */
    Optional<Patient> findByEmailIgnoreCase(String email);

    /**
     * Find patients by phone number
     */
    Optional<Patient> findByPhoneNumber(String phoneNumber);

    /**
     * Find active patients created between dates
     */
    @Query("SELECT p FROM Patient p WHERE p.status = :status " +
           "AND p.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY p.createdAt DESC")
    List<Patient> findActivePatientsBetweenDates(@Param("status") PatientStatus status,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Count patients by status
     */
    long countByStatus(PatientStatus status);

    /**
     * Find patients with allergies
     */
    @Query("SELECT p FROM Patient p WHERE p.allergies IS NOT NULL AND p.allergies != ''")
    List<Patient> findPatientsWithAllergies();

    /**
     * Find patients with medical conditions
     */
    @Query("SELECT p FROM Patient p WHERE p.medicalConditions IS NOT NULL AND p.medicalConditions != ''")
    List<Patient> findPatientsWithMedicalConditions();

    /**
     * Check if document number exists for a different patient
     */
    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.documentNumber = :documentNumber " +
           "AND p.documentType = :documentType AND (:patientId IS NULL OR p.id != :patientId)")
    boolean existsByDocumentNumberAndDocumentTypeAndIdNot(@Param("documentNumber") String documentNumber,
                                                           @Param("documentType") DocumentType documentType,
                                                           @Param("patientId") Long patientId);

    /**
     * Check if medical record number exists for a different patient
     */
    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.medicalRecordNumber = :medicalRecordNumber " +
           "AND (:patientId IS NULL OR p.id != :patientId)")
    boolean existsByMedicalRecordNumberAndIdNot(@Param("medicalRecordNumber") String medicalRecordNumber,
                                                @Param("patientId") Long patientId);

    /**
     * Find patients with emergency contact information
     */
    @Query("SELECT p FROM Patient p WHERE p.emergencyContactName IS NOT NULL " +
           "AND p.emergencyContactName != '' AND p.emergencyContactPhone IS NOT NULL " +
           "AND p.emergencyContactPhone != ''")
    List<Patient> findPatientsWithEmergencyContact();

    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:documentNumber IS NULL OR p.documentNumber = :documentNumber) AND " +
           "(:documentType IS NULL OR p.documentType = :documentType) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:email IS NULL OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Patient> advancedSearch(@Param("firstName") String firstName,
                                @Param("lastName") String lastName,
                                @Param("documentNumber") String documentNumber,
                                @Param("documentType") DocumentType documentType,
                                @Param("status") PatientStatus status,
                                @Param("email") String email,
                                Pageable pageable);
}
