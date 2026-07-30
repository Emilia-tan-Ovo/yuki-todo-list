package com.emiliaovo.yukitodo.controller;

import com.emiliaovo.yukitodo.dto.task.CreateTaskRequest;
import com.emiliaovo.yukitodo.dto.task.CreateTaskResponse;
import com.emiliaovo.yukitodo.dto.task.TaskDetailResponse;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskRequest;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskResponse;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskStatusRequest;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskStatusResponse;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 在当前登录用户拥有的课程下创建任务。
     */
    @PostMapping
    public ResponseEntity<CreateTaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        CreateTaskResponse response =
                taskService.createTask(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 查询当前登录用户拥有的单个任务。
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetailResponse> getTaskDetail(
            @PathVariable Long taskId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        TaskDetailResponse response =
                taskService.getTaskDetail(userId, taskId);

        return ResponseEntity.ok(response);
    }

    /**
     * 完整修改当前登录用户拥有的任务。
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<UpdateTaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        UpdateTaskResponse response =
                taskService.updateTask(userId, taskId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 在课程详情页快速修改任务状态。
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<UpdateTaskStatusResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        UpdateTaskStatusResponse response =
                taskService.updateTaskStatus(userId, taskId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 删除当前登录用户拥有的任务。
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        taskService.deleteTask(userId, taskId);

        return ResponseEntity.noContent().build();
    }
}
