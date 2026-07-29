package com.emiliaovo.yukitodo.repository;

import com.emiliaovo.yukitodo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问接口
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 检查指定用户是否已经拥有同名课程，课程名称比较时忽略大小写。
     */
    boolean existsByUser_IdAndNameIgnoreCase(Long userId, String name);

    /**
     * 查询当前用户的课程，并按照创建时间从早到晚排列。
     */
    List<Course> findByUser_IdOrderByCreatedAtAsc(Long userId);

    /**
     * 查询属于指定用户的课程，同时完成课程归属校验。
     */
    Optional<Course> findByIdAndUser_Id(Long courseId, Long userId);

    /**
     * 检查当前用户的其他课程是否已使用指定名称，比较时忽略大小写。
     */
    boolean existsByUser_IdAndNameIgnoreCaseAndIdNot(
            Long userId,
            String name,
            Long courseId
    );
}
