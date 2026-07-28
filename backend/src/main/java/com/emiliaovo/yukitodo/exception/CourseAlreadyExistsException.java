package com.emiliaovo.yukitodo.exception;

/**
 * 当前用户创建了名称重复的课程。
 */
public class CourseAlreadyExistsException extends RuntimeException {

    public CourseAlreadyExistsException() {
        super("课程名称已存在");
    }
}
