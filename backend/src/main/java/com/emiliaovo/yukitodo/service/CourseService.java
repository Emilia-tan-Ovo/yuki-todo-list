package com.emiliaovo.yukitodo.service;

import com.emiliaovo.yukitodo.dto.course.CreateCourseRequest;
import com.emiliaovo.yukitodo.dto.course.CreateCourseResponse;
import com.emiliaovo.yukitodo.entity.Course;
import com.emiliaovo.yukitodo.entity.User;
import com.emiliaovo.yukitodo.exception.CourseAlreadyExistsException;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.repository.CourseRepository;
import com.emiliaovo.yukitodo.repository.UserRepository;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository
    ) {
        this.courseRepository = courseRepository;
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
     * 从预设颜色池中随机选择一个颜色。
     */
    private String chooseRandomColor() {
        int index = ThreadLocalRandom.current().nextInt(COURSE_COLORS.length);
        return COURSE_COLORS[index];
    }
}
