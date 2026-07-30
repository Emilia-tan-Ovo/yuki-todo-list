package com.emiliaovo.yukitodo.dto.task;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询课程任务列表时返回的响应数据。
 */
public class TaskListResponse {

    private List<TaskSummaryResponse> tasks = new ArrayList<>();

    public TaskListResponse() {
    }

    public List<TaskSummaryResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskSummaryResponse> tasks) {
        this.tasks = tasks;
    }
}
