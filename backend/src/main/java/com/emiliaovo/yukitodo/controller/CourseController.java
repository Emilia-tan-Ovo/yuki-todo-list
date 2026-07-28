package com.emiliaovo.yukitodo.controller;

import com.emiliaovo.yukitodo.dto.course.CreateCourseRequest;
import com.emiliaovo.yukitodo.dto.course.CreateCourseResponse;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * 创建课程，并把课程归属到当前 Session 对应的用户。
     */
    @PostMapping
    public ResponseEntity<CreateCourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        CreateCourseResponse response =
                courseService.createCourse(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
