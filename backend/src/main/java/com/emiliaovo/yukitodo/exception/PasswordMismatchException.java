package com.emiliaovo.yukitodo.exception;

/**
 * 注册时
 * 两次输入的密码不一致
 */
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("两次输入的密码不一致");
    }
}
