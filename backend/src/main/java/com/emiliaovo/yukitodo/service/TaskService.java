package com.emiliaovo.yukitodo.service;

import com.emiliaovo.yukitodo.dto.task.CreateTaskRequest;
import com.emiliaovo.yukitodo.dto.task.CreateTaskResponse;
import com.emiliaovo.yukitodo.dto.task.TaskDetailResponse;
import com.emiliaovo.yukitodo.dto.task.TaskListResponse;
import com.emiliaovo.yukitodo.dto.task.TaskSummaryResponse;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskRequest;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskResponse;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskStatusRequest;
import com.emiliaovo.yukitodo.dto.task.UpdateTaskStatusResponse;
import com.emiliaovo.yukitodo.entity.Course;
import com.emiliaovo.yukitodo.entity.Task;
import com.emiliaovo.yukitodo.entity.TaskStatus;
import com.emiliaovo.yukitodo.exception.CourseNotFoundException;
import com.emiliaovo.yukitodo.exception.TaskNotFoundException;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.repository.CourseRepository;
import com.emiliaovo.yukitodo.repository.TaskRepository;
import com.emiliaovo.yukitodo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            CourseRepository courseRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建属于当前登录用户课程的任务。
     */
    public CreateTaskResponse createTask(
            Long userId,
            CreateTaskRequest request
    ) {
        // Session 中的用户如果已经不存在，则当前登录状态也视为无效。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        // 同时使用课程 id 和用户 id 查询，避免向其他用户的课程添加任务。
        Course course = courseRepository.findByIdAndUser_Id(
                        request.getCourseId(),
                        userId
                )
                .orElseThrow(CourseNotFoundException::new);

        Task task = new Task();
        task.setCourse(course);
        task.setTitle(request.getTitle().trim());
        task.setDeadline(request.getDeadline());
        // 创建任务时不接收前端状态，业务上明确固定为 TODO。
        task.setStatus(TaskStatus.TODO);

        Task savedTask = taskRepository.save(task);

        CreateTaskResponse response = new CreateTaskResponse();
        response.setId(savedTask.getId());
        response.setTitle(savedTask.getTitle());
        response.setCourseId(savedTask.getCourse().getId());
        response.setDeadline(savedTask.getDeadline());
        response.setStatus(savedTask.getStatus());
        return response;
    }

    /**
     * 查询当前用户指定课程下某种状态的任务。
     */
    public TaskListResponse getCourseTasks(
            Long userId,
            Long courseId,
            TaskStatus status
    ) {
        // Session 中的用户已被删除时，登录状态也视为无效。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        // 先确认课程存在且属于当前用户，避免读取其他用户的任务。
        courseRepository.findByIdAndUser_Id(courseId, userId)
                .orElseThrow(CourseNotFoundException::new);

        List<Task> tasks =
                taskRepository.findByCourse_IdAndStatusOrderByDeadlineAsc(
                        courseId,
                        status
                );

        List<TaskSummaryResponse> taskResponses = new ArrayList<>();
        for (Task task : tasks) {
            TaskSummaryResponse taskResponse = new TaskSummaryResponse();
            taskResponse.setId(task.getId());
            taskResponse.setTitle(task.getTitle());
            taskResponse.setCourseId(task.getCourse().getId());
            taskResponse.setDeadline(task.getDeadline());
            taskResponse.setStatus(task.getStatus());
            taskResponses.add(taskResponse);
        }

        TaskListResponse response = new TaskListResponse();
        response.setTasks(taskResponses);
        return response;
    }

    /**
     * 查询属于当前登录用户的单个任务。
     */
    public TaskDetailResponse getTaskDetail(Long userId, Long taskId) {
        // 先区分 Session 用户失效和任务不存在两种情况。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        // 查询条件包含任务所属课程的用户 id，防止泄露其他用户的任务。
        Task task = taskRepository.findByIdAndCourse_User_Id(taskId, userId)
                .orElseThrow(TaskNotFoundException::new);

        TaskDetailResponse response = new TaskDetailResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setCourseId(task.getCourse().getId());
        response.setDeadline(task.getDeadline());
        response.setStatus(task.getStatus());
        return response;
    }

    /**
     * 完整修改属于当前登录用户的任务。
     */
    public UpdateTaskResponse updateTask(
            Long userId,
            Long taskId,
            UpdateTaskRequest request
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        Task task = taskRepository.findByIdAndCourse_User_Id(taskId, userId)
                .orElseThrow(TaskNotFoundException::new);

        // 修改所属课程时，新课程也必须属于当前用户。
        Course course = courseRepository.findByIdAndUser_Id(
                        request.getCourseId(),
                        userId
                )
                .orElseThrow(CourseNotFoundException::new);

        task.setTitle(request.getTitle().trim());
        task.setCourse(course);
        task.setDeadline(request.getDeadline());
        task.setStatus(request.getStatus());

        Task savedTask = taskRepository.save(task);

        UpdateTaskResponse response = new UpdateTaskResponse();
        response.setId(savedTask.getId());
        response.setTitle(savedTask.getTitle());
        response.setCourseId(savedTask.getCourse().getId());
        response.setDeadline(savedTask.getDeadline());
        response.setStatus(savedTask.getStatus());
        return response;
    }

    /**
     * 只修改属于当前登录用户的任务状态。
     */
    public UpdateTaskStatusResponse updateTaskStatus(
            Long userId,
            Long taskId,
            UpdateTaskStatusRequest request
    ) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        Task task = taskRepository.findByIdAndCourse_User_Id(taskId, userId)
                .orElseThrow(TaskNotFoundException::new);

        task.setStatus(request.getStatus());
        Task savedTask = taskRepository.save(task);

        UpdateTaskStatusResponse response = new UpdateTaskStatusResponse();
        response.setId(savedTask.getId());
        response.setStatus(savedTask.getStatus());
        return response;
    }

    /**
     * 删除属于当前登录用户的任务。
     */
    public void deleteTask(Long userId, Long taskId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        Task task = taskRepository.findByIdAndCourse_User_Id(taskId, userId)
                .orElseThrow(TaskNotFoundException::new);

        taskRepository.delete(task);
    }
}
