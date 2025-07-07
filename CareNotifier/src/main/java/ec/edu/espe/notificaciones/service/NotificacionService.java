package ec.edu.espe.notificaciones.service;

import ec.edu.espe.notificaciones.dto.NotificacionDTO;
import ec.edu.espe.notificaciones.model.Notificacion;
import ec.edu.espe.notificaciones.repository.NotificacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;
    
    public void procesarNotificacion(NotificacionDTO dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());
        notificacion.setFechaCreacion(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dto.getTimestamp()),
                ZoneId.systemDefault()
            )
        );
        notificacion.setLeida(false);
        
        notificacionRepository.save(notificacion);
        log.info("Notificación guardada: {}", notificacion);
    }
    
    public List<Notificacion> obtenerNotificacionesPorTipo(String tipo) {
        return notificacionRepository.findByTipoOrderByFechaCreacionDesc(tipo);
    }
    
    public List<Notificacion> obtenerNotificacionesNoLeidas() {
        return notificacionRepository.findByLeidaFalseOrderByFechaCreacionDesc();
    }
    
    public List<Notificacion> obtenerTodasNotificaciones() {
        return notificacionRepository.findAll();
    }
    
    public Notificacion marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));
        
        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }
}
