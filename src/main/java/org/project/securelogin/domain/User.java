package org.project.securelogin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.project.securelogin.dto.UserRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id // 데이터베이스에서 자동으로 값을 생성해준다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; // 사용자 IDX

    @Column(nullable = false) // null값 허용X
    private String username; // 사용자 이름

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String email; // 이메일

    @CreationTimestamp // INSERT 쿼리가 발생할 때, 현재 시간을 자동으로 저장
    private LocalDateTime created_at; // 회원가입한 시간

    @UpdateTimestamp // UPDATE 쿼리가 발생할 때, 현재 시간을 자동으로 저장
    private LocalDateTime updated_at; // 마지막으로 수정한 시간



    //계정상태관리
    private boolean accountNonExpired; // 계정 만료 여부
    private boolean accountNonLocked; // 계정 잠김 여부
    private boolean credentialsNonExpired; // 자격 증명 만료 여부
    private boolean enabled; // 계정 활성화 여부

    private int failedLoginAttempts; // 로그인 시도 횟수
    private LocalDateTime lockTime; // 계정 잠금 시간

    private String socialType; // 소셜타입(자체 로그인의 경우 null)
    private String socialId; //소셜 ID(자체 로그인의 경우 null)

    public void updateUser(UserRequestDTO requestDTO, PasswordEncoder passwordEncoder) {
        this.username = requestDTO.getUsername();
        this.email = requestDTO.getEmail();

        // 새 비밀번호가 null이 아니고, 기존 비밀번호와 다를 때만 인코딩하여 업데이트
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().equals(this.password)) {
            this.password = passwordEncoder.encode(requestDTO.getPassword());
        }
    }

    //로그인 실패 시 로그인 시도 횟수 증가
    public void incrementFailedLoginAttempts(){
        this.failedLoginAttempts++;
    }

    //로그인 성공 시 로그인 시도 횟수 초기화
    public void resetFailedLoginAttempts(){
        this.failedLoginAttempts = 0;
    }

    //계정 잠금
    public void lockAccount(){
        this.accountNonLocked = false;
        this.lockTime = LocalDateTime.now();
    }

    //계정 잠금 풀기
    public void unlockAccount(){
        this.accountNonLocked = true;
        this.lockTime = null; //잠금 시간 초기화
    }

    public boolean isLockTimeExpired(int lockDurationMinutes) {
        if (this.lockTime == null) {
            return true; //잠금 시간이 없으면 바로 해제 가능
        }
        LocalDateTime expiryTime = this.lockTime.plusMinutes(lockDurationMinutes);
        return expiryTime.isBefore(LocalDateTime.now());//현재 시간이 잠금 만료 시간 이전이면 해제 가능
    }

    public void updateOAuthUser(String username, String email, String socialType, String socialId) {
        this.username = username;
        if (email != null) {
            this.email = email;
        }
        this.socialId = socialId;
        this.socialType = socialType;
        this.enabled = true; //OAuth 로그인 후 사용자는 활성화 상태
    }
}
