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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskQueryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User currentUser;
    private Course currentCourse;
    private Course emptyCourse;
    private Course otherUserCourse;
    private Task earlyTodoTask;
    private Task lateTodoTask;
    private Task otherUserTask;
    private MockHttpSession currentUserSession;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().replace("-", "");

        currentUser = saveUser(
                "task_query_user_" + suffix,
                "task_query_" + suffix + "@example.com"
        );
        User otherUser = saveUser(
                "task_query_other_" + suffix,
                "task_query_other_" + suffix + "@example.com"
        );

        currentCourse = saveCourse(currentUser, "当前用户课程");
        emptyCourse = saveCourse(currentUser, "空任务课程");
        otherUserCourse = saveCourse(otherUser, "其他用户课程");

        lateTodoTask = saveTask(
                currentCourse,
                "较晚截止的任务",
                LocalDate.of(2026, 8, 10),
                TaskStatus.TODO
        );
        earlyTodoTask = saveTask(
                currentCourse,
                "较早截止的任务",
                LocalDate.of(2026, 8, 1),
                TaskStatus.TODO
        );
        saveTask(
                currentCourse,
                "已完成任务",
                LocalDate.of(2026, 7, 31),
                TaskStatus.DONE
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
    void shouldFilterTodoTasksAndOrderByDeadlineAscending() throws Exception {
        mockMvc.perform(get(
                        "/api/courses/{courseId}/tasks",
                        currentCourse.getId()
                )
                        .param("status", "TODO")
                        .session(currentUserSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks.length()").value(2))
                .andExpect(jsonPath("$.tasks[0].id").value(earlyTodoTask.getId()))
                .andExpect(jsonPath("$.tasks[0].status").value("TODO"))
                .andExpect(jsonPath("$.tasks[1].id").value(lateTodoTask.getId()))
                .andExpect(jsonPath("$.tasks[1].status").value("TODO"));
    }

    @Test
    void shouldReturnEmptyTaskArrayWhenNoTaskMatches() throws Exception {
        mockMvc.perform(get(
                        "/api/courses/{courseId}/tasks",
                        emptyCourse.getId()
                )
                        .param("status", "TODO")
                        .session(currentUserSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenStatusIsMissingOrInvalid() throws Exception {
        mockMvc.perform(get(
                        "/api/courses/{courseId}/tasks",
                        currentCourse.getId()
                )
                        .session(currentUserSession))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(
                        "/api/courses/{courseId}/tasks",
                        currentCourse.getId()
                )
                        .param("status", "INVALID")
                        .session(currentUserSession))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHideOtherUsersCourse() throws Exception {
        mockMvc.perform(get(
                        "/api/courses/{courseId}/tasks",
                        otherUserCourse.getId()
                )
                        .param("status", "TODO")
                        .session(currentUserSession))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("COURSE_NOT_FOUND"));
    }

    @Test
    void shouldRequireLoginForCourseTaskList() throws Exception {
        mockMvc.perform(get(
                        "/api/courses/{courseId}/tasks",
                        currentCourse.getId()
                )
                        .param("status", "TODO"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnCurrentUsersTaskDetailWithExpectedFields() throws Exception {
        mockMvc.perform(get(
                        "/api/tasks/{taskId}",
                        earlyTodoTask.getId()
                )
                        .session(currentUserSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(5)))
                .andExpect(jsonPath("$.id").value(earlyTodoTask.getId()))
                .andExpect(jsonPath("$.title").value(earlyTodoTask.getTitle()))
                .andExpect(jsonPath("$.courseId").value(currentCourse.getId()))
                .andExpect(jsonPath("$.deadline").value("2026-08-01"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void shouldHideOtherUsersTask() throws Exception {
        mockMvc.perform(get(
                        "/api/tasks/{taskId}",
                        otherUserTask.getId()
                )
                        .session(currentUserSession))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
    }

    @Test
    void shouldReturnNotFoundForMissingTask() throws Exception {
        mockMvc.perform(get(
                        "/api/tasks/{taskId}",
                        Long.MAX_VALUE
                )
                        .session(currentUserSession))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
    }

    @Test
    void shouldRequireLoginForTaskDetail() throws Exception {
        mockMvc.perform(get(
                        "/api/tasks/{taskId}",
                        earlyTodoTask.getId()
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
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
