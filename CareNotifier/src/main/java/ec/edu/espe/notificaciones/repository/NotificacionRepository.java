package ec.edu.espe.notificaciones.repository;

import ec.edu.espe.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByTipoOrderByFechaCreacionDesc(String tipo);
    
    List<Notificacion> findByLeidaFalseOrderByFechaCreacionDesc();
}
