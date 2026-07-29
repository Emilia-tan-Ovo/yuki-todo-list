package com.emiliaovo.yukitodo.controller;

import com.emiliaovo.yukitodo.dto.course.CreateCourseRequest;
import com.emiliaovo.yukitodo.dto.course.CreateCourseResponse;
import com.emiliaovo.yukitodo.dto.course.CourseDetailResponse;
import com.emiliaovo.yukitodo.dto.course.CourseListResponse;
import com.emiliaovo.yukitodo.dto.course.UpdateCourseRequest;
import com.emiliaovo.yukitodo.dto.course.UpdateCourseResponse;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    /**
     * 查询当前登录用户的课程首页摘要。
     */
    @GetMapping
    public ResponseEntity<CourseListResponse> getCourses(
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        CourseListResponse response = courseService.getCourses(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 查询当前登录用户拥有的单门课程。
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(
            @PathVariable Long courseId,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        CourseDetailResponse response =
                courseService.getCourseDetail(userId, courseId);

        return ResponseEntity.ok(response);
    }

    /**
     * 修改当前登录用户拥有的课程。
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<UpdateCourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        UpdateCourseResponse response =
                courseService.updateCourse(userId, courseId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 删除当前登录用户拥有的课程。
     */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");
        courseService.deleteCourse(userId, courseId);

        return ResponseEntity.noContent().build();
    }
}
