package kg.alymzhan.petchatgpt.dto;

import java.util.List;

public record ChatCompletionResponse(
        int created,
        Usage usage,
        String model,
        String id,
        List<ChoicesItem> choices,
        String object
) {
}