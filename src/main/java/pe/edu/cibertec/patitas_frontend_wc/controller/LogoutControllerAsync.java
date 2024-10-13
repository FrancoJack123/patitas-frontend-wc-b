package pe.edu.cibertec.patitas_frontend_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LogoutResponseDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/logout")
@CrossOrigin(origins = "http://localhost:5173")
public class LogoutControllerAsync {

    @Autowired
    WebClient webClientAutenticacion;

    @PostMapping("/salir")
    public Mono<LogoutResponseDTO> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        if (logoutRequestDTO.tipoDocumento() == null || logoutRequestDTO.tipoDocumento().trim().length() == 0 ||
                logoutRequestDTO.numeroDocumento() == null || logoutRequestDTO.numeroDocumento().trim().length() == 0) {
            return Mono.just(new LogoutResponseDTO(false, null, "Error: Debe completar correctamente sus credenciales"));
        }
        try {
            return webClientAutenticacion.post()
                    .uri("/logout")
                    .body(Mono.just(logoutRequestDTO), LogoutRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LogoutResponseDTO.class)
                    .flatMap(response -> {
                        if(response.resultado()){
                            return Mono.just(new LogoutResponseDTO(response.resultado(), response.fecha(), response.mensajeError()));
                        } else {
                            return Mono.just(new LogoutResponseDTO(false, null, response.mensajeError()));
                        }
                    });
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return Mono.just(new LogoutResponseDTO(false, null, "Error: Ocurrió un problema en el logout"));
        }
    }
}
