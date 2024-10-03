package co.com.juan.sanchez.PracticaWebFlux.controller;

import co.com.juan.sanchez.PracticaWebFlux.service.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orquestador")
public class OrqController {

    private final StepOne stepOne;
    private final StepTwo stepTwo;
    private final StepThree stepThree;
    private final FullAnswer fullCompleted;



    // Inyectar los servicios en el constructor
    public OrqController(StepOne stepOne, StepTwo stepTwo, StepThree stepThree, FullAnswer fullCompleted) {
        this.stepOne = stepOne;
        this.stepTwo = stepTwo;
        this.stepThree = stepThree;
        this.fullCompleted = fullCompleted;
    }

    // Llamar al StepOne desde un endpoint
    @PostMapping("/stepOne")
    public Mono<ResponseEntity<String>> callStepOne(@RequestBody String requestBody) {
        return stepOne.callStepOne(requestBody)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error en Step 1: " + throwable.getMessage())));
    }

    // Llamar al StepTwo desde otro endpoint
    @PostMapping("/stepTwo")
    public Mono<ResponseEntity<String>> callStepTwo(@RequestBody String requestBody) {
        return stepTwo.StepTwoCall(requestBody)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error en Step 2: " + throwable.getMessage())));
    }

    // Llamar al StepThree desde otro endpoint
    @PostMapping("/stepThree")
    public Mono<ResponseEntity<String>> callStepThree(@RequestBody String requestBody) {
        return stepThree.StepThreeCall(requestBody)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error en Step 3: " + throwable.getMessage())));
    }

    // Llamar al FullAnswer para unificar todas las respuestas
    @PostMapping("/completeAnswer")
    public Mono<ResponseEntity<String>> getCompleteAnswer(@RequestBody String requestBody) {
        return fullCompleted.callAllSteps(requestBody)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error en la respuesta completa: " + throwable.getMessage())));
    }





}
