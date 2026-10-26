package com.edutrack.app.model;

public class Student {
    private long studentId;
    private long userId;
    private String name;
    private String email;
    private String phone;
    private String rollNo;
    private String className;

    public Student() {
    }

    public Student(long studentId, long userId, String name, String email, String phone, String rollNo, String className) {
        this.studentId = studentId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rollNo = rollNo;
        this.className = className;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
