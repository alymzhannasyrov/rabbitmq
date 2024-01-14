package kg.alymzhan.petchatgpt.dto;

import com.alibaba.fastjson.annotation.JSONField;

public record ChoicesItem(
        @JSONField(name = "finish_reason")
        String finishReason,
        int index,
        Message message
) {
}