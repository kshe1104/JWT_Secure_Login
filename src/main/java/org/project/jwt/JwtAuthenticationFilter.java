package org.project.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.securelogin.domain.CustomUserDetails;
import org.project.securelogin.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 토큰을 사용해 인증을 처리하는 필터
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    //HTTP 요청을 필터링하여 JWT 토큰을 검증하고, 사용자를 인증한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            // JWT 토큰 추출
            String jwt = jwtTokenProvider.resolveToken(request);

            //추출한 토큰이 null이 아니고 유효성이 있다면
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                //토큰에서 사용자 정보 추출
                String email = jwtTokenProvider.getEmail(jwt);

                //이메일을 사용해 사용자 정보를 로드함
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

                //사용자 정보와 권한을 사용해 인증 객체를 생성
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //인증 객체에 요청의 세부 정보를 추가
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //SecurityContextHolder를 사용하여 인증 객체를 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException e) {
            //만료된 JWT 처리
            handleTokenExpiration(request, response);
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        //다음 필터를 체인으로 제어를 넘긴다.
        filterChain.doFilter(request, response);
    }

    //토큰 만료 처리 로직
    private void handleTokenExpiration(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //Refresh Token 추출
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            //refresh token이 블랙리스트에 없다면 새로운 access token 발급
            if (!jwtTokenProvider.isRefreshTokenBlacklisted(refreshToken)) {
                String jwtToken = jwtTokenProvider.createTokenFromRefreshToken(refreshToken);
                response.setHeader("Authorization", "Bearer " + jwtToken);

                //새로 발급받은 Access Token으로 UserDetails 로드
                String email = jwtTokenProvider.getEmail(jwtToken);
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

                // 인증 객체 생성 및 SecurityContext에 설정
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else {
                // Refresh Token이 블랙리스트에 있다면 Unauthorized 에러 반환
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token is blacklisted");
            }
        }else{
            //유효하지 않은 Refresh Token일 경우 SecurityContext 초기화 및 Unauthorized 에러 반환
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
        }
    }

}
