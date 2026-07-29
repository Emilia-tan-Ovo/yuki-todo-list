package com.emiliaovo.yukitodo.dto.course;

import java.time.LocalDate;

/**
 * 课程卡片中展示的最近截止任务。
 */
public class NearestTaskResponse {

    private Long id;
    private String title;
    private LocalDate deadline;

    public NearestTaskResponse() {
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
