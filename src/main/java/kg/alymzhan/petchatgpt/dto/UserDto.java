package kg.alymzhan.petchatgpt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;


public record UserDto(Long id,
                      @NotBlank(message = "Имя не может быть пустым")
                      @Pattern(regexp = "^[a-zA-Zа-яА-ЯЁё ]+$", message = "Имя содержит недопустимые символы") String name,
                      @Email String email,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING) LocalDateTime createdAt) {
}
