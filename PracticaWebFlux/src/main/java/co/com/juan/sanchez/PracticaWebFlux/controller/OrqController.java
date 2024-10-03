package co.com.juan.sanchez.PracticaWebFlux.controller;

import co.com.juan.sanchez.PracticaWebFlux.service.Orq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class OrqController {
    private final Orq orqService;

    // Inyecta el servicio Orq en el controlador.
    public OrqController(Orq orqService) {
        this.orqService = orqService;
    }

    // Endpoint para iniciar el proceso de ejecución de los tres servicios.
    @PostMapping("/steps")
    public Mono<String> iniciarProceso(@RequestBody String requestBody) {
        // Delegar la ejecución de los servicios al servicio Orq.
        return orqService.ejecutarServicios(requestBody);
    }
}
