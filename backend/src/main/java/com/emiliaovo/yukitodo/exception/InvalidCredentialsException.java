package com.emiliaovo.yukitodo.exception;

/**
 * 登录时
 * 账号或密码错误/账号不存在
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("账号或密码错误");
    }
}
