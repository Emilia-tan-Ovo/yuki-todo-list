package com.emiliaovo.yukitodo.dto.task;

import com.emiliaovo.yukitodo.entity.TaskStatus;

import java.time.LocalDate;

/**
 * 查询单个任务时返回的完整任务数据。
 */
public class TaskDetailResponse {

    private Long id;
    private String title;
    private Long courseId;
    private LocalDate deadline;
    private TaskStatus status;

    public TaskDetailResponse() {
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
