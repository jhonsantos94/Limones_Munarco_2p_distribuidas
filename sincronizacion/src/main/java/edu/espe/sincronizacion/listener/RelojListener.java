package edu.espe.sincronizacion.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.espe.sincronizacion.DTO.HoraClienteDTO;
import edu.espe.sincronizacion.service.SIncronizacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RelojListener {
    @Autowired
    private SIncronizacionService sincronizacionService;

    @Autowired
    private ObjectMapper mapper;

    @RabbitListener(queues = "reloj.solicitud")
    public void recibirSolicitud(String solicitud){
        try {
            HoraClienteDTO dto = mapper.readValue(solicitud, HoraClienteDTO.class);
            log.info("Recibiendo solicitud: {}", dto);
            sincronizacionService.registrarTiempoCliente(dto);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
