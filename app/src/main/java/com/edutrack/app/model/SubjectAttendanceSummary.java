package com.edutrack.app.model;

import com.edutrack.app.util.Constants;

public class SubjectAttendanceSummary {
    private long subjectId;
    private String subjectCode;
    private String subjectName;
    private String facultyName;
    private int attendedLectures;
    private int totalLectures;
    private double percentage;
    private int requiredLecturesTo75;

    public SubjectAttendanceSummary() {
    }

    public SubjectAttendanceSummary(long subjectId, String subjectCode, String subjectName, String facultyName,
                                    int attendedLectures, int totalLectures) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.facultyName = facultyName;
        this.attendedLectures = attendedLectures;
        this.totalLectures = totalLectures;
        calculateStats();
    }

    public void calculateStats() {
        if (totalLectures > 0) {
            this.percentage = ((double) attendedLectures / totalLectures) * 100.0;
        } else {
            this.percentage = 0.0;
        }

        if (this.percentage >= Constants.MIN_ATTENDANCE || totalLectures == 0) {
            this.requiredLecturesTo75 = 0;
        } else {
            // Required formula: ceil((0.75 * total - attended) / 0.25)
            double required = Math.ceil(((Constants.MIN_ATTENDANCE / 100.0) * totalLectures - attendedLectures) / (1.0 - (Constants.MIN_ATTENDANCE / 100.0)));
            this.requiredLecturesTo75 = Math.max(0, (int) required);
        }
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public int getAttendedLectures() {
        return attendedLectures;
    }

    public void setAttendedLectures(int attendedLectures) {
        this.attendedLectures = attendedLectures;
        calculateStats();
    }

    public int getTotalLectures() {
        return totalLectures;
    }

    public void setTotalLectures(int totalLectures) {
        this.totalLectures = totalLectures;
        calculateStats();
    }

    public double getPercentage() {
        return percentage;
    }

    public int getRequiredLecturesTo75() {
        return requiredLecturesTo75;
    }

    public boolean isOnTrack() {
        return percentage >= Constants.MIN_ATTENDANCE;
    }

    public boolean isWarning() {
        return percentage >= 65.0 && percentage < Constants.MIN_ATTENDANCE;
    }

    public boolean isCritical() {
        return percentage < 65.0;
    }
}
