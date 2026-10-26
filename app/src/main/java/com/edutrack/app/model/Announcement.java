package com.edutrack.app.model;

public class Announcement {
    private long id;
    private String facultyName;
    private String className;
    private String title;
    private String message;
    private String createdAt;

    public Announcement() {
    }

    public Announcement(long id, String facultyName, String className, String title, String message, String createdAt) {
        this.id = id;
        this.facultyName = facultyName;
        this.className = className;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
