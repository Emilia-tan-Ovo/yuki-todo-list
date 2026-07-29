package com.emiliaovo.yukitodo.service;

import com.emiliaovo.yukitodo.dto.course.CreateCourseRequest;
import com.emiliaovo.yukitodo.dto.course.CreateCourseResponse;
import com.emiliaovo.yukitodo.dto.course.CourseDetailResponse;
import com.emiliaovo.yukitodo.dto.course.CourseListResponse;
import com.emiliaovo.yukitodo.dto.course.CourseSummaryResponse;
import com.emiliaovo.yukitodo.dto.course.NearestTaskResponse;
import com.emiliaovo.yukitodo.dto.course.UpdateCourseRequest;
import com.emiliaovo.yukitodo.dto.course.UpdateCourseResponse;
import com.emiliaovo.yukitodo.entity.Course;
import com.emiliaovo.yukitodo.entity.Task;
import com.emiliaovo.yukitodo.entity.TaskStatus;
import com.emiliaovo.yukitodo.entity.User;
import com.emiliaovo.yukitodo.exception.CourseAlreadyExistsException;
import com.emiliaovo.yukitodo.exception.CourseNotFoundException;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.repository.CourseRepository;
import com.emiliaovo.yukitodo.repository.TaskRepository;
import com.emiliaovo.yukitodo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CourseService {

    private static final String[] COURSE_COLORS = {
            "#4F46E5",
            "#8B5CF6",
            "#3B82F6",
            "#06B6D4",
            "#10B981",
            "#F59E0B",
            "#EF4444",
            "#EC4899"
    };

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CourseService(
            CourseRepository courseRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建属于当前登录用户的课程。
     */
    public CreateCourseResponse createCourse(
            Long userId,
            CreateCourseRequest request
    ) {
        // Session 中的用户如果已经不存在，则当前登录状态也视为无效。
        User user = userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        // 查重和保存都使用去除前后空格后的名称。
        String trimmedName = request.getName().trim();

        if (courseRepository.existsByUser_IdAndNameIgnoreCase(
                userId,
                trimmedName
        )) {
            throw new CourseAlreadyExistsException();
        }

        String color = request.getColor() == null
                ? chooseRandomColor()
                : request.getColor();

        Course course = new Course();
        course.setUser(user);
        course.setName(trimmedName);
        course.setColor(color);

        Course savedCourse = courseRepository.save(course);

        CreateCourseResponse response = new CreateCourseResponse();
        response.setId(savedCourse.getId());
        response.setName(savedCourse.getName());
        response.setColor(savedCourse.getColor());
        return response;
    }

    /**
     * 查询当前用户的课程首页摘要。
     */
    public CourseListResponse getCourses(Long userId) {
        // Session 中的用户如果已经不存在，则当前登录状态也视为无效。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        List<Course> courses =
                courseRepository.findByUser_IdOrderByCreatedAtAsc(userId);
        List<CourseSummaryResponse> courseResponses = new ArrayList<>();

        for (Course course : courses) {
            long todoCount = taskRepository.countByCourse_IdAndStatus(
                    course.getId(),
                    TaskStatus.TODO
            );

            List<Task> nearestTasks =
                    taskRepository
                            .findTop3ByCourse_IdAndStatusOrderByDeadlineAsc(
                                    course.getId(),
                                    TaskStatus.TODO
                            );

            CourseSummaryResponse courseResponse =
                    toCourseSummaryResponse(course, todoCount, nearestTasks);
            courseResponses.add(courseResponse);
        }

        CourseListResponse response = new CourseListResponse();
        response.setCourses(courseResponses);
        return response;
    }

    /**
     * 查询当前登录用户拥有的单门课程及其任务数量。
     */
    public CourseDetailResponse getCourseDetail(
            Long userId,
            Long courseId
    ) {
        // Session 中的用户如果已经不存在，则当前登录状态也视为无效。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        // 同时使用课程 id 和用户 id 查询，避免访问其他用户的课程。
        Course course = courseRepository.findByIdAndUser_Id(courseId, userId)
                .orElseThrow(CourseNotFoundException::new);

        long todoCount = taskRepository.countByCourse_IdAndStatus(
                courseId,
                TaskStatus.TODO
        );
        long doneCount = taskRepository.countByCourse_IdAndStatus(
                courseId,
                TaskStatus.DONE
        );

        CourseDetailResponse response = new CourseDetailResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setColor(course.getColor());
        response.setTodoCount(todoCount);
        response.setDoneCount(doneCount);
        return response;
    }

    /**
     * 修改当前登录用户拥有的课程。
     */
    public UpdateCourseResponse updateCourse(
            Long userId,
            Long courseId,
            UpdateCourseRequest request
    ) {
        // Session 中的用户如果已经不存在，则当前登录状态也视为无效。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        Course course = courseRepository.findByIdAndUser_Id(courseId, userId)
                .orElseThrow(CourseNotFoundException::new);

        // 查重和保存都使用去除前后空格后的名称。
        String trimmedName = request.getName().trim();

        // 排除当前课程，避免名称不变或只修改大小写时误判为重复。
        if (courseRepository.existsByUser_IdAndNameIgnoreCaseAndIdNot(
                userId,
                trimmedName,
                courseId
        )) {
            throw new CourseAlreadyExistsException();
        }

        course.setName(trimmedName);
        course.setColor(request.getColor());

        Course savedCourse = courseRepository.save(course);

        UpdateCourseResponse response = new UpdateCourseResponse();
        response.setId(savedCourse.getId());
        response.setName(savedCourse.getName());
        response.setColor(savedCourse.getColor());
        return response;
    }

    /**
     * 删除当前登录用户拥有的课程。
     *
     * 课程下的任务由数据库外键 ON DELETE CASCADE 自动删除。
     */
    public void deleteCourse(Long userId, Long courseId) {
        // Session 中的用户如果已经不存在，则当前登录状态也视为无效。
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        Course course = courseRepository.findByIdAndUser_Id(courseId, userId)
                .orElseThrow(CourseNotFoundException::new);

        courseRepository.delete(course);
    }

    /**
     * 把课程及其任务统计转换成首页课程摘要 DTO。
     */
    private CourseSummaryResponse toCourseSummaryResponse(
            Course course,
            long todoCount,
            List<Task> nearestTasks
    ) {
        CourseSummaryResponse response = new CourseSummaryResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setColor(course.getColor());
        response.setTodoCount(todoCount);

        List<NearestTaskResponse> taskResponses = new ArrayList<>();
        for (Task task : nearestTasks) {
            NearestTaskResponse taskResponse = new NearestTaskResponse();
            taskResponse.setId(task.getId());
            taskResponse.setTitle(task.getTitle());
            taskResponse.setDeadline(task.getDeadline());
            taskResponses.add(taskResponse);
        }
        response.setNearestTasks(taskResponses);

        return response;
    }

    /**
     * 从预设颜色池中随机选择一个颜色。
     */
    private String chooseRandomColor() {
        int index = ThreadLocalRandom.current().nextInt(COURSE_COLORS.length);
        return COURSE_COLORS[index];
    }
}
