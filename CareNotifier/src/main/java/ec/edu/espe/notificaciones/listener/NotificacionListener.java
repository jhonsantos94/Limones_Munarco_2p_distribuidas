package ec.edu.espe.notificaciones.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.notificaciones.dto.NotificacionDTO;
import ec.edu.espe.notificaciones.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class NotificacionListener {
    
    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @RabbitListener(queues = "notificaciones.cola")
    public void recibirNotificacion(String mensaje) {
        try {
            NotificacionDTO dto = objectMapper.readValue(mensaje, NotificacionDTO.class);
            dto.setTimestamp(Instant.now().toEpochMilli());
            log.info("Notificación recibida: {}", dto);
            notificacionService.procesarNotificacion(dto);
        } catch (Exception e) {
            log.error("Error al procesar notificación: {}", e.getMessage());
        }
    }
}
