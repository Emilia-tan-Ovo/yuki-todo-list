package com.emiliaovo.yukitodo.repository;

import com.emiliaovo.yukitodo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问接口
 * User -> Repository 管理 User 实体
 * Long -> User 实体的主键类型(Long id)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
