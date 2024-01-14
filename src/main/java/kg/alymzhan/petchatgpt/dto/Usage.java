package kg.alymzhan.petchatgpt.dto;

import com.alibaba.fastjson.annotation.JSONField;

public record Usage(
        @JSONField(name = "completion_tokens")
        int completionTokens,
        @JSONField(name = "prompt_tokens")
        int promptTokens,
        @JSONField(name = "total_tokens")
        int totalTokens
) {
}