package com.emiliaovo.yukitodo.dto.course;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程列表接口的最外层响应。
 */
public class CourseListResponse {

    private List<CourseSummaryResponse> courses = new ArrayList<>();

    public CourseListResponse() {
    }

    public List<CourseSummaryResponse> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseSummaryResponse> courses) {
        this.courses = courses;
    }
}
