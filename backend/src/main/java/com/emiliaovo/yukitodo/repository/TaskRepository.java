package com.emiliaovo.yukitodo.repository;

import com.emiliaovo.yukitodo.entity.Task;
import com.emiliaovo.yukitodo.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 任务数据访问接口
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * 统计指定课程中某种状态的任务数量。
     */
    long countByCourse_IdAndStatus(Long courseId, TaskStatus status);

    /**
     * 查询指定课程中某种状态、截止日期最早的最多三项任务。
     */
    List<Task> findTop3ByCourse_IdAndStatusOrderByDeadlineAsc(
            Long courseId,
            TaskStatus status
    );

    /**
     * 查询指定课程中某种状态的全部任务，并按截止日期从早到晚排列。
     */
    List<Task> findByCourse_IdAndStatusOrderByDeadlineAsc(
            Long courseId,
            TaskStatus status
    );

    /**
     * 按任务 id 和任务所属用户 id 查询，同时完成用户归属校验。
     */
    Optional<Task> findByIdAndCourse_User_Id(
            Long taskId,
            Long userId
    );
}
