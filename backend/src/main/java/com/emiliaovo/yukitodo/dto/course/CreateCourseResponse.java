package com.emiliaovo.yukitodo.dto.course;

/**
 * 创建课程成功后返回的数据。
 *
 * 使用 DTO 返回必要字段，避免直接把 Course 实体暴露给前端。
 */
public class CreateCourseResponse {

    private Long id;
    private String name;
    private String color;

    public CreateCourseResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
