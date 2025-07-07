package ec.edu.espe.notificaciones.controller;

import ec.edu.espe.notificaciones.model.Notificacion;
import ec.edu.espe.notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;
    
    @GetMapping
    public ResponseEntity<List<Notificacion>> listarNotificaciones() {
        return ResponseEntity.ok(notificacionService.obtenerTodasNotificaciones());
    }
    
    @GetMapping("/no-leidas")
    public ResponseEntity<List<Notificacion>> listarNotificacionesNoLeidas() {
        return ResponseEntity.ok(notificacionService.obtenerNotificacionesNoLeidas());
    }
    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Notificacion>> listarNotificacionesPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(notificacionService.obtenerNotificacionesPorTipo(tipo));
    }
    
    @PutMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }
    
    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("Microservicio de Notificaciones - v1.0");
    }
}
