package com.edutrack.app.model;

public class Subject {
    private long id;
    private String code;
    private String name;
    private String department;
    private String facultyName; // helper field when querying subject with assigned faculty

    public Subject() {
    }

    public Subject(long id, String code, String name, String department) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.department = department;
    }

    public Subject(long id, String code, String name, String department, String facultyName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.department = department;
        this.facultyName = facultyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}
