package edu.espe.sincronizacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraServidorDTO {
    private long horaServidor;
    private Map<String, Long> diferenciasHoras;
}
