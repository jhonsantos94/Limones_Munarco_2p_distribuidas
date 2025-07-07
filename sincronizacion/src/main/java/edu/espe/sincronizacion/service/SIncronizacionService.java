package edu.espe.sincronizacion.service;

import edu.espe.sincronizacion.DTO.HoraClienteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SIncronizacionService {
    private Map<String, Long> tiempoClientes = new ConcurrentHashMap<>();
    private static final int INTERVALO_SEGUNDOS = 10;

    public void registrarTiempoCliente(HoraClienteDTO horaClienteDTO) {
        tiempoClientes.put(horaClienteDTO.getNombroServidor(), horaClienteDTO.getHoraEnviada());
    }

    public void sincronizarRelojes(){
        if(tiempoClientes.size()>=2){
            long ahora = Instant.now().toEpochMilli();
            long promedio = (ahora+tiempoClientes.values().stream().mapToLong(Long::longValue).sum())/(tiempoClientes.size()+1);
            tiempoClientes.clear();

        }
    }
    public void  enviarAjuste(long promedio){
        log.info("Enviando ajuste de los nodos: {}", new Date(promedio));
    }


}
