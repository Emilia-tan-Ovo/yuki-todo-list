package com.emiliaovo.yukitodo.controller;

import com.emiliaovo.yukitodo.dto.task.TaskListResponse;
import com.emiliaovo.yukitodo.entity.TaskStatus;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理课程与任务之间的查询接口。
 */
@RestController
@RequestMapping("/api/courses/{courseId}/tasks")
public class CourseTaskController {

    private final TaskService taskService;

    public CourseTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 按任务状态查询当前用户指定课程下的任务。
     */
    @GetMapping
    public ResponseEntity<TaskListResponse> getCourseTasks(
            @PathVariable Long courseId,
            @RequestParam TaskStatus status,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        TaskListResponse response =
                taskService.getCourseTasks(userId, courseId, status);

        return ResponseEntity.ok(response);
    }
}
