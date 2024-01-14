package kg.alymzhan.petchatgpt.dto;

import java.util.List;

public record ChatCompletionRequest(
        String model,
        List<Message> messages
) {
}
