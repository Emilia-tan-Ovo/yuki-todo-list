package com.emiliaovo.yukitodo.exception;

/**
 * 未登录状态
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("请先登录");
    }
}
