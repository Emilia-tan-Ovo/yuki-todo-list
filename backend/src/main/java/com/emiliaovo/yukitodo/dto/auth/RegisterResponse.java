package com.emiliaovo.yukitodo.dto.auth;

/**
 * 注册响应DTO
 */
public class RegisterResponse {
    private Long id;
    private String username;
    private String email;

    public RegisterResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
