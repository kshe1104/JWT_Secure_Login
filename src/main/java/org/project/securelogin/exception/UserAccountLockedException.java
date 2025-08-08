package org.project.securelogin.exception;

import javax.naming.AuthenticationException;

// 사용자가 로그인 시도 중 계정이 잠금 상태일 때 발생하는 예외
public class UserAccountLockedException extends AuthenticationException {
    public UserAccountLockedException(String message) {
        super(message);
    }
}
