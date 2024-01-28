package kg.alymzhan.petchatgpt.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id=%s not found"
                .formatted(id));
    }
}
