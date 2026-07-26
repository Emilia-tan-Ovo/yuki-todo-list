package com.emiliaovo.yukitodo.repository;

import com.emiliaovo.yukitodo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 任务数据访问接口
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
