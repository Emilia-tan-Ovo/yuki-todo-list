package com.emiliaovo.yukitodo.dto.task;

import com.emiliaovo.yukitodo.entity.TaskStatus;

/**
 * 快速修改任务状态成功后返回的数据。
 */
public class UpdateTaskStatusResponse {

    private Long id;
    private TaskStatus status;

    public UpdateTaskStatusResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
