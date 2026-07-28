package com.emiliaovo.yukitodo.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 创建课程时接收的请求数据。
 *
 * 不包含 userId，课程所属用户只能由后端根据 Session 确定。
 */
public class CreateCourseRequest {

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称不能超过100个字符")
    private String name;

    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "课程颜色必须使用#RRGGBB格式"
    )
    private String color;

    public CreateCourseRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
