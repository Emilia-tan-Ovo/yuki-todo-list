package com.emiliaovo.yukitodo.dto.task;

import com.emiliaovo.yukitodo.entity.TaskStatus;

import java.time.LocalDate;

/**
 * 完整修改任务成功后返回的数据。
 */
public class UpdateTaskResponse {

    private Long id;
    private String title;
    private Long courseId;
    private LocalDate deadline;
    private TaskStatus status;

    public UpdateTaskResponse() {
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
