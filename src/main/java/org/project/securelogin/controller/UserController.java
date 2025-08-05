package org.project.securelogin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.securelogin.dto.UserRequestDTO;
import org.project.securelogin.dto.UserResponseDTO;
import org.project.securelogin.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = null;
    }

    @PostMapping("/signup")

    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.signUp(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }
}
