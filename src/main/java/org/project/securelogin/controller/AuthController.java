package org.project.securelogin.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.project.securelogin.dto.JsonResponse;
import org.project.securelogin.exception.UserAccountLockedException;
import org.project.securelogin.exception.UserNotEnabledException;
import org.project.securelogin.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JsonResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            HttpHeaders headers = authService.login(authRequest.getEmail(), authRequest.getPassword());
            JsonResponse response = new JsonResponse(HttpStatus.OK.value(), "로그인에 성공했습니다.", null);
            return ResponseEntity.ok().headers(headers).body(response);

        }catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JsonResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }catch (UserNotEnabledException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JsonResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
        }catch (UserAccountLockedException e){
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new JsonResponse(HttpStatus.LOCKED.value(), e.getMessage(), null));
        }
        catch (AuthenticationException e) {
            int remainingAttempts = authService.getRemainingLoginAttempts(authRequest.getEmail());

            String message = "이메일 주소나 비밀번호가 올바르지 않습니다." + remainingAttempts + "번 더 로그인에 실패하면 계정이 잠길 수 있습니다.";
            JsonResponse errorResponse = new JsonResponse(HttpStatus.UNAUTHORIZED.value(), "이메일 주소나 비밀번호가 올바르지 않습니다.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JsonResponse(HttpStatus.UNAUTHORIZED.value(), message, null));
        }
    }

    @Getter
    public static class AuthRequest {
        private String email;
        private String password;
    }

//    @Getter
//    public static class Response {
//        private int statusCode;
//        private String message;
//
//        public Response(int statusCode, String message) {
//            this.statusCode = statusCode;
//            this.message = message;
//        }
//    }

    @DeleteMapping("/logout")
    public ResponseEntity<JsonResponse> logout(@RequestHeader(name = "Refresh-Token") String refreshToken) {
        boolean logoutSuccess = authService.logout(refreshToken);

        if (logoutSuccess) {
            JsonResponse response = new JsonResponse(HttpStatus.NO_CONTENT.value(), "성공적으로 로그아웃되었습니다.", null);
            return ResponseEntity.ok().body(response); //204 No Content(로그아웃 성공)
        }else {
            JsonResponse errorResponse = new JsonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 접근입니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // or INTERNAL_SERVER_ERROR (로그아웃 실패)
        }
    }
}
