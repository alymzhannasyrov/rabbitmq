package kg.alymzhan.petchatgpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public static final String TOKEN = "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";

    @Bean("openai")
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeaders(header -> {
                    header.setBearerAuth(TOKEN);
                    header.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }
}
