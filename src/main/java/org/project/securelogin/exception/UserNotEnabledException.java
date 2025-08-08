package org.project.securelogin.exception;

import javax.naming.AuthenticationException;

// 사용자가 로그인 시도 중 계정이 활성화되지 않았을 때 발생하는 예외
public class UserNotEnabledException extends AuthenticationException {
    public UserNotEnabledException(String message) {
        super(message);
    }
}
