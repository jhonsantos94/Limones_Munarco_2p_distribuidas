package ec.edu.espe.patientdatacollector.repository;

import ec.edu.espe.patientdatacollector.model.EventStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventStorageRepository extends JpaRepository<EventStorage, Long> {
    
    List<EventStorage> findByProcessedFalseOrderByCreatedAt();
    
    Optional<EventStorage> findByEventId(String eventId);
    
    List<EventStorage> findByProcessedTrueAndRetryCountLessThan(Integer maxRetries);
}
