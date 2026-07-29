package com.emiliaovo.yukitodo.dto.course;

/**
 * 修改课程成功后返回的数据。
 */
public class UpdateCourseResponse {

    private Long id;
    private String name;
    private String color;

    public UpdateCourseResponse() {
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
