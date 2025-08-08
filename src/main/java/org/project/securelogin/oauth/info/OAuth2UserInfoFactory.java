package org.project.securelogin.oauth.info;

import java.util.Map;

//다양한 소셜 로그인 제공자에 대한 사용자 정보를 처리함
//OAuth2UserInfo의 구현체를 생성하는 팩토리 클래스
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String,Object> attributes){
        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        else if ("kakao".equalsIgnoreCase(registrationId)) {
            return new KaKaoOAuth2UserInfo(attributes);
        }
        else if ("naver".equalsIgnoreCase(registrationId)) {
            return new NaverOAuth2UserInfo(attributes);
        }
        else{
            throw new IllegalArgumentException("Invalid registration ID");
        }
    }
}
