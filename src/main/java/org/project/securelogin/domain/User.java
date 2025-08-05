package org.project.securelogin.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
}
