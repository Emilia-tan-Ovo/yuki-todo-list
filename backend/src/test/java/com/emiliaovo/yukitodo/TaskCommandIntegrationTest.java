package com.emiliaovo.yukitodo;

import com.emiliaovo.yukitodo.entity.Course;
import com.emiliaovo.yukitodo.entity.Task;
import com.emiliaovo.yukitodo.entity.TaskStatus;
import com.emiliaovo.yukitodo.entity.User;
import com.emiliaovo.yukitodo.repository.CourseRepository;
import com.emiliaovo.yukitodo.repository.TaskRepository;
import com.emiliaovo.yukitodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskCommandIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Course currentCourse;
    private Course secondCourse;
    private Course otherUserCourse;
    private Task currentTask;
    private Task otherUserTask;
    private MockHttpSession currentUserSession;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);

        User currentUser = saveUser(
                "task_command_user_" + suffix,
                "task_command_" + suffix + "@example.com"
        );
        User otherUser = saveUser(
                "task_command_other_" + suffix,
                "task_command_other_" + suffix + "@example.com"
        );

        currentCourse = saveCourse(currentUser, "当前课程");
        secondCourse = saveCourse(currentUser, "可移动到的课程");
        otherUserCourse = saveCourse(otherUser, "其他用户课程");

        currentTask = saveTask(
                currentCourse,
                "原任务标题",
                LocalDate.of(2026, 8, 1),
                TaskStatus.TODO
        );
        otherUserTask = saveTask(
                otherUserCourse,
                "其他用户任务",
                LocalDate.of(2026, 8, 2),
                TaskStatus.TODO
        );

        currentUserSession = new MockHttpSession();
        currentUserSession.setAttribute("userId", currentUser.getId());
    }

    @Test
    void shouldUpdateAllTaskFieldsAndMoveTaskToOwnCourse() throws Exception {
        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson(
                                "  修改后的任务  ",
                                secondCourse.getId(),
                                "2026-09-01",
                                "DONE"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(currentTask.getId()))
                .andExpect(jsonPath("$.title").value("修改后的任务"))
                .andExpect(jsonPath("$.courseId").value(secondCourse.getId()))
                .andExpect(jsonPath("$.deadline").value("2026-09-01"))
                .andExpect(jsonPath("$.status").value("DONE"));

        taskRepository.flush();
        Task savedTask = taskRepository.findById(currentTask.getId())
                .orElseThrow();

        assertEquals("修改后的任务", savedTask.getTitle());
        assertEquals(secondCourse.getId(), savedTask.getCourse().getId());
        assertEquals(LocalDate.of(2026, 9, 1), savedTask.getDeadline());
        assertEquals(TaskStatus.DONE, savedTask.getStatus());
    }

    @Test
    void shouldRejectMovingTaskToOtherUsersCourse() throws Exception {
        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson(
                                "修改任务",
                                otherUserCourse.getId(),
                                "2026-09-01",
                                "TODO"
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("COURSE_NOT_FOUND"));
    }

    @Test
    void shouldHideOtherUsersTaskWhenUpdating() throws Exception {
        mockMvc.perform(put("/api/tasks/{taskId}", otherUserTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson(
                                "修改任务",
                                currentCourse.getId(),
                                "2026-09-01",
                                "TODO"
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
    }

    @Test
    void shouldRejectMissingPutFields() throws Exception {
        String courseId = secondCourse.getId().toString();

        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseId":%s,"deadline":"2026-09-01","status":"DONE"}
                                """.formatted(courseId)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"修改任务","deadline":"2026-09-01","status":"DONE"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"修改任务","courseId":%s,"status":"DONE"}
                                """.formatted(courseId)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"修改任务","courseId":%s,"deadline":"2026-09-01"}
                                """.formatted(courseId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnValidationErrorForInvalidPutStatus() throws Exception {
        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson(
                                "修改任务",
                                currentCourse.getId(),
                                "2026-09-01",
                                "INVALID"
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldRequireLoginForPut() throws Exception {
        mockMvc.perform(put("/api/tasks/{taskId}", currentTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTaskJson(
                                "修改任务",
                                currentCourse.getId(),
                                "2026-09-01",
                                "TODO"
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldUpdateTaskStatusAndReturnOnlyIdAndStatus() throws Exception {
        mockMvc.perform(patch(
                        "/api/tasks/{taskId}/status",
                        currentTask.getId()
                )
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.id").value(currentTask.getId()))
                .andExpect(jsonPath("$.status").value("DONE"));

        taskRepository.flush();
        Task savedTask = taskRepository.findById(currentTask.getId())
                .orElseThrow();
        assertEquals(TaskStatus.DONE, savedTask.getStatus());
    }

    @Test
    void shouldRejectMissingOrInvalidPatchStatus() throws Exception {
        mockMvc.perform(patch(
                        "/api/tasks/{taskId}/status",
                        currentTask.getId()
                )
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch(
                        "/api/tasks/{taskId}/status",
                        currentTask.getId()
                )
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"INVALID"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldHideOtherUsersTaskWhenPatchingStatus() throws Exception {
        mockMvc.perform(patch(
                        "/api/tasks/{taskId}/status",
                        otherUserTask.getId()
                )
                        .session(currentUserSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
    }

    @Test
    void shouldRequireLoginForPatch() throws Exception {
        mockMvc.perform(patch(
                        "/api/tasks/{taskId}/status",
                        currentTask.getId()
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldDeleteOwnTaskAndReturnEmptyBody() throws Exception {
        Long taskId = currentTask.getId();

        mockMvc.perform(delete("/api/tasks/{taskId}", taskId)
                        .session(currentUserSession))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        taskRepository.flush();
        assertFalse(taskRepository.findById(taskId).isPresent());
    }

    @Test
    void shouldHideOtherUsersTaskWhenDeleting() throws Exception {
        mockMvc.perform(delete(
                        "/api/tasks/{taskId}",
                        otherUserTask.getId()
                )
                        .session(currentUserSession))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));

        assertTrue(taskRepository.findById(otherUserTask.getId()).isPresent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/{taskId}", Long.MAX_VALUE)
                        .session(currentUserSession))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
    }

    @Test
    void shouldRequireLoginForDelete() throws Exception {
        mockMvc.perform(delete(
                        "/api/tasks/{taskId}",
                        currentTask.getId()
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private String updateTaskJson(
            String title,
            Long courseId,
            String deadline,
            String status
    ) {
        return """
                {
                  "title": "%s",
                  "courseId": %d,
                  "deadline": "%s",
                  "status": "%s"
                }
                """.formatted(title, courseId, deadline, status);
    }

    private User saveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("integration-test-password-hash");
        return userRepository.save(user);
    }

    private Course saveCourse(User user, String name) {
        Course course = new Course();
        course.setUser(user);
        course.setName(name);
        course.setColor("#4F46E5");
        return courseRepository.save(course);
    }

    private Task saveTask(
            Course course,
            String title,
            LocalDate deadline,
            TaskStatus status
    ) {
        Task task = new Task();
        task.setCourse(course);
        task.setTitle(title);
        task.setDeadline(deadline);
        task.setStatus(status);
        return taskRepository.save(task);
    }
}
