package com.emiliaovo.yukitodo.exception;

/**
 * 任务不存在，或任务不属于当前登录用户。
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException() {
        super("任务不存在");
    }
}
