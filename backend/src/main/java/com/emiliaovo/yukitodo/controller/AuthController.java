package com.emiliaovo.yukitodo.controller;

import com.emiliaovo.yukitodo.dto.auth.RegisterRequest;
import com.emiliaovo.yukitodo.dto.auth.RegisterResponse;
import com.emiliaovo.yukitodo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
