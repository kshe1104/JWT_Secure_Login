package org.project.securelogin.oauth.info;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    //attributes 속 response 안에 정보들이 위치함
    // 따라서 response를 정의한 다음 response안에서 정보를 추출해야 함
    @Override
    public String getId() {
        //네이버 응답에서 'response' 속성 내부의 'id' 값을 추출
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("id");
    }

    @Override
    public String getName() {
        // 네이버 응답에서 'response' 속성 내부의 'name' 값을 추출
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("name");
    }

    @Override
    public String getEmail() {
        // 네이버 응답에서 'response' 속성 내부의 'email' 값을 추출
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("email");
    }
}
