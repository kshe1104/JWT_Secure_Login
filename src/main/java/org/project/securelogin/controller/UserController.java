package org.project.securelogin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.securelogin.dto.JsonResponse;
import org.project.securelogin.dto.UserRequestDTO;
import org.project.securelogin.dto.UserResponseDTO;
import org.project.securelogin.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = null;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UserRequestDTO userRequestDTO) {
//        UserResponseDTO userResponseDTO = userService.signUp(userRequestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
//    }

    @PostMapping("/signup")
    // @Valid 어노테이션으로 SignUpRequest의 유효성 검사를 활성화, 통과하면 서비스 코드 호출
    public ResponseEntity<JsonResponse> signUp(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.signUp(userRequestDTO);
        JsonResponse response = new JsonResponse(HttpStatus.CREATED.value(), "회원가입이 성공적으로 실행되었습니다.", userResponseDTO);
        return ResponseEntity.ok().body(response);
    }

    //회원 정보조회
    @GetMapping("/{userId}")
    public ResponseEntity<JsonResponse> getUser(@PathVariable Long userId) {
        try {
            UserResponseDTO userResponseDTO = userService.getUserById(userId);
            JsonResponse response = new JsonResponse(HttpStatus.OK.value(), "회원 정보를 성공적으로 조회했습니다.", userResponseDTO);
            return ResponseEntity.ok().body(response);
        } catch (IllegalStateException e) {
            JsonResponse errorResponse = new JsonResponse(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // 회원 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<JsonResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            UserResponseDTO userResponseDTO = userService.updateUser(userId, userRequestDTO);
            JsonResponse response = new JsonResponse(HttpStatus.OK.value(), "회원 정보를 성공적으로 수정했습니다.", userResponseDTO);
            return ResponseEntity.ok().body(response);
        } catch (IllegalStateException e) {
            JsonResponse errorResponse = new JsonResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // 회원 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<JsonResponse> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            JsonResponse response = new JsonResponse(HttpStatus.NO_CONTENT.value(), "회원정보를 성공적으로 삭제했습니다.", null);
            return ResponseEntity.ok().body(response);
        } catch (IllegalStateException e) {
            JsonResponse errorResponse = new JsonResponse(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
