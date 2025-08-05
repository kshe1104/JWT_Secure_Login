package org.project.securelogin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRequestDTO {


    @NotBlank(message = "사용자 이름을 입력하세요") //빈칸 허용x
    private final String username;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Pattern(regexp ="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
    message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함하여 8~16자여야 합니다") //정규식을 통해 패턴을 정의함. 어떤 문자포함, 길이 등등
    private final String password;

    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")//이메일 형식이 아니라면 리턴
    private final String email;

    @Override
    public String toString(){
        return "SignUpRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", email='" + email + '\'' +
                '}';
    }

}
