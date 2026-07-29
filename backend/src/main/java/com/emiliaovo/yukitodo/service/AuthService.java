package com.emiliaovo.yukitodo.service;

import com.emiliaovo.yukitodo.dto.auth.*;
import com.emiliaovo.yukitodo.entity.User;
import com.emiliaovo.yukitodo.exception.AccountAlreadyExistsException;
import com.emiliaovo.yukitodo.exception.InvalidCredentialsException;
import com.emiliaovo.yukitodo.exception.PasswordMismatchException;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 完成用户注册的业务流程：校验密码、检查账号是否重复、编码密码并保存用户。
     */
    public RegisterResponse register(RegisterRequest request) {
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AccountAlreadyExistsException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AccountAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHash);

        // save 返回的对象包含数据库生成的用户 id，因此用它组装响应。
        User savedUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        return response;
    }

    /**
     * 完成用户登录流程：检查账号和密码，返回用户信息。
     */
    public LoginResponse login(LoginRequest request) {
        // 根据账号（用户名或邮箱）查找用户，未找到则抛出凭证无效异常
        User user = userRepository
                .findByUsernameOrEmail(
                        request.getAccount(),
                        request.getAccount()
                )
                .orElseThrow(InvalidCredentialsException::new);

        // 校验用户输入的密码是否与数据库中存储的哈希值匹配
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        // 认证通过，组装登录响应并返回用户基本信息
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }

    /**
     * 查询当前登录用户。
     */
    public CurrentUserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        CurrentUserResponse response = new CurrentUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }
}
