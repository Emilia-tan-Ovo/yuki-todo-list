package com.emiliaovo.yukitodo.repository;

import com.emiliaovo.yukitodo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程数据访问接口
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 检查指定用户是否已经拥有同名课程，课程名称比较时忽略大小写。
     */
    boolean existsByUser_IdAndNameIgnoreCase(Long userId, String name);
}
