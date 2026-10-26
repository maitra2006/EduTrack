package com.edutrack.app.model;

public class AttendanceRecord {
    private long id;
    private long studentId;
    private String studentName;
    private String rollNo;
    private long subjectId;
    private String date;
    private int status; // 1 for Present, 0 for Absent

    public AttendanceRecord() {
    }

    public AttendanceRecord(long id, long studentId, String studentName, String rollNo, long subjectId, String date, int status) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.subjectId = subjectId;
        this.date = date;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isPresent() {
        return status == 1;
    }
}
