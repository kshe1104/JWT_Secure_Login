package org.project.securelogin.service;

import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.project.securelogin.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest,OAuth2User>{
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest);

}
