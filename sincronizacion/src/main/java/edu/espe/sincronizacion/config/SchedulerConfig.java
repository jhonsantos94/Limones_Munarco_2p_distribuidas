package edu.espe.sincronizacion.config;

import edu.espe.sincronizacion.service.SIncronizacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {



    @Autowired
    private SIncronizacionService sincronizacionService;

    @Scheduled(fixedRateString  = "${intervalo}")
    public void SchedulerConfig() {
        log.info("Ejecutando Sincronizacion...");
        sincronizacionService.sincronizarRelojes();
    }
}
