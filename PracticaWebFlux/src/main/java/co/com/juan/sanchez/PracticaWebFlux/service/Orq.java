package co.com.juan.sanchez.PracticaWebFlux.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class Orq {

    private final WebClient webClient;

    //El constructor me permite acceder a los metodos de Flux
    public Orq(WebClient.Builder webClient) {
        this.webClient = webClient.build(); //Seguramente la configuración para conectar con la información
    }

    public Mono<String> runService1(String requestBody) {
        return webClient.post()
                .uri("http://localhost:8081/getStep")  // URL del otro servicio
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)  // Pasar el JSON como String
                .retrieve()
                .bodyToMono(String.class)  // Esperar la respuesta como String
                .doOnNext(response -> System.out.println("Respuestax del servicio: " + response));  // Imprimir la respuesta en la consola
    }

    public Mono<String> runService2(String requestBody) {
        return webClient.post()
                .uri("http://localhost:8082/getStep")  // URL del otro servicio
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)  // Pasar el JSON como String
                .retrieve()
                .bodyToMono(String.class)  // Esperar la respuesta como String
                .doOnNext(response -> System.out.println("Respuesta del servicio: " + response));  // Imprimir la respuesta en la consola
    }

    public Mono<String> runService3(String requestBody) {
        return webClient.post()
                .uri("http://localhost:8083/getStep")  // URL del otro servicio
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)  // Pasar el JSON como String
                .retrieve()
                .bodyToMono(String.class)  // Esperar la respuesta como String
                .doOnNext(response -> System.out.println("Respuesta del servicio: " + response));  // Imprimir la respuesta en la consola
    }
    ///////////////////////////
    public Mono<String> runServices(String requestBody) {
        // Solicitud al primer servicio
        Mono<String> servicio1 = webClient.post()
                .uri("http://localhost:8081/getStep")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extraerAnswer);


        // Solicitud al segundo servicio
        Mono<String> servicio2 = webClient.post()
                .uri("http://localhost:8082/getStep")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extraerAnswer);

        // Solicitud al tercer servicio
        Mono<String> servicio3 = webClient.post()
                .uri("http://localhost:8083/getStep")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extraerAnswer);

        // Unificar las respuestas de los tres servicios en un solo String
        return Mono.zip(servicio1, servicio2, servicio3)
                .map(tuple -> {
                    String step1Answer = tuple.getT1();
                    String step2Answer = tuple.getT2();
                    String step3Answer = tuple.getT3();

                    // Combinar las respuestas en un solo String
                    String finalAnswer = String.format(
                            "{\"data\": [{\"header\": {\"id\": \"12345\", \"type\": \"TestGiraffeRefrigerator\"}, \"answer\": \"Step1: %s - Step2: %s - Step3: %s\"}]}",
                            step1Answer, step2Answer, step3Answer
                    );

                    return finalAnswer;
                });


    }

    // Metodo para extraer el campo "answer" de la respuesta
    private String extraerAnswer(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.at("/0/data/0/answer").asText();  // Ruta hacia el campo "answer"
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error al procesar la respuesta";
        }
    }

}
