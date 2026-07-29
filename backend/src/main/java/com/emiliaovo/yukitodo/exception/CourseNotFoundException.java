package com.emiliaovo.yukitodo.exception;

/**
 * 课程不存在，或课程不属于当前登录用户。
 */
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException() {
        super("课程不存在");
    }
}
