package com.emiliaovo.yukitodo.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 修改课程时接收的完整课程数据。
 *
 * 不包含 userId，课程归属只能由后端根据 Session 判断。
 */
public class UpdateCourseRequest {

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称不能超过100个字符")
    private String name;

    @NotBlank(message = "课程颜色不能为空")
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "课程颜色必须使用#RRGGBB格式"
    )
    private String color;

    public UpdateCourseRequest() {
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
