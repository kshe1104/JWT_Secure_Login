package org.project.securelogin.service;

import lombok.RequiredArgsConstructor;
import org.project.securelogin.domain.User;
import org.project.securelogin.dto.UserRequestDTO;
import org.project.securelogin.dto.UserResponseDTO;
import org.project.securelogin.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserResponseDTO signUp(UserRequestDTO userRequestDTO) {
        //이메일 중복체크
        if (isEmailAlreadyExists(userRequestDTO.getEmail())) {
            throw new IllegalStateException("이미 등록된 이메일입니다.");
        }
        //비밀번호 암호화
        String encodedPassword = encodePassword(userRequestDTO.getPassword());

        //회원정보 생성
        User user = User.builder()
                .username(userRequestDTO.getUsername())
                .password(encodedPassword)
                .email(userRequestDTO.getEmail())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        //회원정보 저장
        userRepository.save(user);

        //UserRespnseDTO 생성
        return new UserResponseDTO(user.getUsername(), user.getEmail());
    }

    //이메일 중복체크 메서드
    public boolean isEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    //비밀번호 암호화 메서드
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
