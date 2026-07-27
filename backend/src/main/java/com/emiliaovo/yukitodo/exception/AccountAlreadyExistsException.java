package com.emiliaovo.yukitodo.exception;

/**
 * 注册时
 * 用户名或邮箱已被使用
 */
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {
        super("用户名或邮箱已被使用");
    }
}
