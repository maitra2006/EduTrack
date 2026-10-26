package com.edutrack.app.util;

public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // Minimum Required Attendance Percentage (as required: stored in ONE constant)
    public static final double MIN_ATTENDANCE = 75.0;

    // Database
    public static final String DATABASE_NAME = "edutrack.db";
    public static final int DATABASE_VERSION = 1;

    // User Roles
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_FACULTY = "FACULTY";
    public static final String ROLE_ADMIN = "ADMIN";

    // Session Preferences
    public static final String PREF_NAME = "edutrack_user_session";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_FACULTY_ID = "faculty_id";
    public static final String KEY_ROLL_NO = "roll_no";
    public static final String KEY_CLASS_NAME = "class_name";

    // Attendance Status
    public static final int STATUS_ABSENT = 0;
    public static final int STATUS_PRESENT = 1;

    // Notifications
    public static final String NOTIFICATION_CHANNEL_ID = "attendance_alerts_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "EduTrack Attendance Alerts";
    public static final int NOTIFICATION_ID_ALERT = 1001;

    // Default Demo Class Name
    public static final String DEMO_CLASS_NAME = "SE Computer Engineering - Div A";
}
