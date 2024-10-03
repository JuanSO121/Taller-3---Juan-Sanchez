package co.com.juan.sanchez.PracticaWebFlux.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FullAnswer {

    private final StepOne stepOne;
    private final StepTwo stepTwo;
    private final StepThree stepThree;
    private final WebHook webHook;
    private static final Logger LOG = LoggerFactory.getLogger(FullAnswer.class);


    public FullAnswer(StepOne stepOne, StepTwo stepTwo, StepThree stepThree, WebHook webHook) {
        this.stepOne = stepOne;
        this.stepTwo = stepTwo;
        this.stepThree = stepThree;
        this.webHook = webHook;

    }

    public Mono<String> callAllSteps(String requestBody) {
        // Primero llama al WHStart y luego procesa los pasos
        return webHook.WHStart()
                .then(
                        // Ejecutar los tres pasos después del WHStart
                        Mono.zip(
                                stepOne.callStepOne(requestBody).map(this::extraerAnswer),
                                stepTwo.StepTwoCall(requestBody).map(this::extraerAnswer),
                                stepThree.StepThreeCall(requestBody).map(this::extraerAnswer)
                        ).flatMap(tuple3 -> {
                            String answerStepOne = tuple3.getT1();
                            String answerStepTwo = tuple3.getT2();
                            String answerStepThree = tuple3.getT3();

                            // Crear la respuesta final
                            String finalAnswer = String.format(
                                    "{\"data\": [{\"header\": {\"id\": \"12345\", \"type\": \"TestGiraffeRefrigerator\"}, \"answer\": \"Step1: %s - Step2: %s - Step3: %s\"}]}",
                                    answerStepOne, answerStepTwo, answerStepThree
                            );

                            // Enviar la respuesta final al WH y retornar la respuesta final
                            return webHook.WH(finalAnswer)
                                    .flatMap(webHookResponse ->
                                            Mono.just("Respuesta final enviada al WH: " + webHookResponse + "\nAnswered: " + finalAnswer)
                                    );
                        })
                )
                .onErrorResume(throwable -> {
                    // Manejo de errores
                    LOG.warn("Error en callAllSteps: {}", throwable.getMessage());
                    return Mono.just("Error al procesar los pasos: " + throwable.getMessage());
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

