package com.emiliaovo.yukitodo.controller;

import com.emiliaovo.yukitodo.dto.auth.*;
import com.emiliaovo.yukitodo.exception.UnauthorizedException;
import com.emiliaovo.yukitodo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册。
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            // 把前端发送的 JSON 转换成 RegisterRequest
            @Valid @RequestBody RegisterRequest request,
            HttpSession session
    ) {
        // 调用 Service 层的 register 方法完成注册
        RegisterResponse response = authService.register(request);

        // 注册成功后记录当前登录用户
        session.setAttribute("userId", response.getId());

        return ResponseEntity
                // HTTP 状态码 201 Created
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 用户登录。
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        LoginResponse response = authService.login(request);

        session.setAttribute("userId", response.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出/登录状态失效
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // 检查 session 是否存在以及 session 中是否存在 userId
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }
        // 让 Session 失效
        session.invalidate();
        // 返回 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询当前登录用户
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException();
        }

        Long userId = (Long) session.getAttribute("userId");

        CurrentUserResponse response = authService.getCurrentUser(userId);

        return ResponseEntity.ok(response);
    }
}
