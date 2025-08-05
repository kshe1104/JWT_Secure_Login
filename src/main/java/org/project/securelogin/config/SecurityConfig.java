package org.project.securelogin.config;

import lombok.RequiredArgsConstructor;
import org.project.jwt.JwtAuthenticationFilter;
import org.project.jwt.JwtTokenProvider;
import org.project.securelogin.domain.CustomUserDetails;
import org.project.securelogin.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean// 비밀번호 암호화를 위한 PasswordEncoder 빈 생성
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean //AuthenticationManager 빈을 생성 -> 인증 요청 처리
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean //SecurityFilterChain 설정 ->SpringBoot3
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // REST API에서는 불필요한 CSRF 보안 비활성화
            .csrf(AbstractHttpConfigurer::disable)
            // JWT 토큰 인증 시스템을 사용할 것이기에 서버가 세션을 생성하지 않도록 한다.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // HTTP 요청에 대한 인가 규칙 설정
            .authorizeHttpRequests(auth -> auth
                    // 로그인과 회원가입 페이지는 누구나 접근 가능
                    .requestMatchers("/login","/auth/login", "/user/signup","/").permitAll()
                    .anyRequest().authenticated() // 그 외 요청은 인증 필요
            )
                .formLogin(form->form.permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,
                        customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
