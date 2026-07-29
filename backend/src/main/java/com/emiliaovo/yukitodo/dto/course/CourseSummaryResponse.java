package com.emiliaovo.yukitodo.dto.course;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页一张课程卡片需要展示的课程摘要。
 */
public class CourseSummaryResponse {

    private Long id;
    private String name;
    private String color;
    private long todoCount;
    private List<NearestTaskResponse> nearestTasks = new ArrayList<>();

    public CourseSummaryResponse() {
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

    public List<NearestTaskResponse> getNearestTasks() {
        return nearestTasks;
    }

    public void setNearestTasks(List<NearestTaskResponse> nearestTasks) {
        this.nearestTasks = nearestTasks;
    }
}
