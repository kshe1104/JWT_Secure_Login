package org.project.securelogin.oauth.info;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    //추상메서드 각 정보를 반환함 -> 소셜마다 추출방식이 다르기 때문에 하위 클래스에서 구현되어야 함
    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
}
