package kg.alymzhan.petchatgpt.exception;

import kg.alymzhan.petchatgpt.dto.UserDto;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(UserDto userDto) {
        super("User with name=%s and email=%s already exist"
                .formatted(userDto.name(), userDto.email()));
    }
}
