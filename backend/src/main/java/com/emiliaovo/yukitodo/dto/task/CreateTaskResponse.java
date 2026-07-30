package com.emiliaovo.yukitodo.dto.task;

import com.emiliaovo.yukitodo.entity.TaskStatus;

import java.time.LocalDate;

/**
 * 创建任务成功后返回的数据。
 *
 * 使用 DTO 返回必要字段，避免直接暴露 Task 和 Course 实体。
 */
public class CreateTaskResponse {

    private Long id;
    private String title;
    private Long courseId;
    private LocalDate deadline;
    private TaskStatus status;

    public CreateTaskResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
