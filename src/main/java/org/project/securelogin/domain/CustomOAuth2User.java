package org.project.securelogin.domain;

import lombok.Getter;

import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;
@Getter
public class CustomOAuth2User implements OAuth2User{
private final OAuth2User oAuth2User;
private final String soccialType;
private final String id;
private final String name;
private final String email;

    public CustomOAuth2User(OAuth2User oAuth2User, String socialType) {
        this.oAuth2User = oAuth2User;
        this.soccialType = socialType;
        this.id = extractId(OAuth2User, socialType);
        this.name = extractName(OAuth2User, socialType);
        this.email = extractEmail(OAuth2User, socialType);
    }
    private String extractId(OAuth2User oAuth2User, String socialType) {
        switch (socialType) {
            case "google":
                return (String) oAuth2User.getAttribute("sub");
            case "kakao":
                Object id = oAuth2User.getAttribute("id");
                return id != null ? id.toString() : null; // Long 타입을 String으로 변환
            case "naver":
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return (String) response.get("id");
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }
    }


    private String extractName(OAuth2User oAuth2User, String socialType) {
        switch (socialType) {
            case "google":
                return (String) oAuth2User.getAttribute("name");
            case "kakao":
                Map<String, Object> account = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                return (String) profile.get("nickname");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return (String) response.get("name");
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }
    }

    private String extractEmail(OAuth2User oAuth2User, String socialType) {
        switch (socialType) {
            case "google":
                return (String) oAuth2User.getAttribute("email");
            case "kakao":
                Map<String, Object> account = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
                return (String) account.get("email");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return (String) response.get("email");
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }
    }
}
