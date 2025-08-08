package org.project.securelogin.service;

import lombok.RequiredArgsConstructor;
import org.project.securelogin.jwt.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public HttpHeaders login(String email, String password) {
        try{
            //인증 수행
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            //토큰 생성
            String accessToken = jwtTokenProvider.createToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            userDetailsService.processSuccessfulLogin(email);

            //헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.add("Refresh-Token", refreshToken);

            return headers;
        }catch (AuthenticationException e){
//            //인증 실패 시 예외 처리
//            throw new AuthenticationException("Authentication failed: "+e.getMessage()){};
//
            //로그인 실패 처리
            userDetailsService.handleAccountStatus(email);
            userDetailsService.processFailedLogin(email);
            // 예외를 던짐
            throw e;
        }
    }

    // Refresh 토큰을 블랙리스트에 추가하고, 성공적으로 추가되면 true 반환
    // 로그아웃
    public boolean logout(String token) {
        return jwtTokenProvider.blacklistRefreshToken(token);
    }

    public int getRemainingLoginAttempts(String email) {
        return userDetailsService.getRemainingLoginAttempts(email);
    }
}
