package com.emiliaovo.yukitodo.repository;

import com.emiliaovo.yukitodo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程数据访问接口
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
}
