package com.emiliaovo.yukitodo.dto.task;

import com.emiliaovo.yukitodo.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 课程详情页快速修改任务状态时接收的数据。
 */
public class UpdateTaskStatusRequest {

    @NotNull(message = "任务状态不能为空")
    private TaskStatus status;

    public UpdateTaskStatusRequest() {
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
