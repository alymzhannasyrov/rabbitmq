package kg.alymzhan.petchatgpt.controller;

import kg.alymzhan.petchatgpt.dto.UserDto;
import kg.alymzhan.petchatgpt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(
            @RequestBody @Valid UserDto newUser) {
        UserDto userDto = userService.addUser(newUser);
        URI uri = URI.create("http://localhost:8080/api/users/%s".formatted(userDto.id()));

        return ResponseEntity.created(uri).body(userDto);
    }

}
