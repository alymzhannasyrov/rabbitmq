package kg.alymzhan.petchatgpt.service;

import kg.alymzhan.petchatgpt.dto.ChatCompletionRequest;
import kg.alymzhan.petchatgpt.dto.ChatCompletionResponse;
import kg.alymzhan.petchatgpt.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class ChatGptClientService {

    private final WebClient webClient;

    public ChatGptClientService(@Qualifier("openai")
                                WebClient webClient) {
        this.webClient = webClient;
    }

    public ChatCompletionResponse sendMessageToOpenAI(List<Message> messages) {
        var request = new ChatCompletionRequest("gpt-3.5-turbo-0301",
                messages);
        log.info("Request: {}", request);

        try {
            var response = webClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();
            log.info("Response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error during sendMessageToOpenAI {}", e.getMessage());
            return null;
        }
    }

}
