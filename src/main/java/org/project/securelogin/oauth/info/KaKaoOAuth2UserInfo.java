package org.project.securelogin.oauth.info;

import java.util.Map;

public class KaKaoOAuth2UserInfo extends OAuth2UserInfo {
    public KaKaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    //id는 가장 바깥에 위치함.
    @Override
    public String getId() {
        Object id = attributes.get("id");
        return id != null ? id.toString() : null; //Long타입을 String으로 변환
    }

    //nickname은 kakao_account 속 profile에 위치함
    @Override
    public String getName() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return (String) profile.get("nickname");
    }

    //email은 kakao_account에 위치함
    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        return (String) account.get("email");
    }
}
