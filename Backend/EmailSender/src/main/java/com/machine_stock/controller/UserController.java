package com.machine_stock.controller;

import com.machine_stock.model.HttpResponse;
import com.machine_stock.model.User;
import com.machine_stock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody User user)
    {
        User newUser = userService.saveUser(user);
        return ResponseEntity.created(URI.create("")).body(HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", newUser))
                .message("User Created")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value()).build());
    }

    @GetMapping
    public ResponseEntity<HttpResponse> validateUser(@RequestParam("token") String token)
    {
        Boolean isValid = userService.verifyToken(token);
        return ResponseEntity.ok().body(HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("Is valid", isValid))
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value()).build());
    }
}
