package com.emiliaovo.yukitodo.dto.course;

/**
 * 查询单门课程时返回的课程基本信息和任务数量。
 *
 * 使用响应 DTO，避免直接向前端暴露 Course 实体及其用户关联。
 */
public class CourseDetailResponse {

    private Long id;
    private String name;
    private String color;
    private long todoCount;
    private long doneCount;

    public CourseDetailResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(long todoCount) {
        this.todoCount = todoCount;
    }

    public long getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(long doneCount) {
        this.doneCount = doneCount;
    }
}
