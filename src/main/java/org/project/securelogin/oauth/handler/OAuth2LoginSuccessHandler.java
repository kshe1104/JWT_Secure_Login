package org.project.securelogin.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.securelogin.domain.CustomOAuth2User;
import org.project.securelogin.domain.CustomUserDetails;
import org.project.securelogin.domain.User;
import org.project.securelogin.dto.JsonResponse;
import org.project.securelogin.dto.UserResponseDTO;
import org.project.securelogin.jwt.JwtTokenProvider;
import org.project.securelogin.oauth.info.OAuth2UserInfo;
import org.project.securelogin.oauth.info.OAuth2UserInfoFactory;
import org.project.securelogin.service.CustomOAuth2UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
//OAuth2 로그인에 성공했을 때의 작업을 정의하는 로그인 성공 핸들러
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // CustomOAuth2User 객체 생성
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String socialType = customOAuth2User.getSoccialType();

        // OAuth2UserInfo를 사용하여 사용자 정보 가져옴
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType,customOAuth2User.getAttributes());

        User user = oAuth2UserService.getUserByOAuth2UserInfo(userInfo, socialType);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // CstomUserDetails를 Authentication으로 변환
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String accessToken = jwtTokenProvider.createToken(auth);
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        response.setHeader("Authorization", "Bearer", +accessToken);
        response.setHeader("Refresh-Token", refreshToken);

        JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_OK, "로그인 성공", new UserResponseDTO(customOAuth2User.getName(), customOAuth2User.getEmail()));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(jsonResponse.toJson());
    }
}
