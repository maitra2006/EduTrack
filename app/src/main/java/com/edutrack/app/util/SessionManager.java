package com.edutrack.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.edutrack.app.model.User;

public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUserSession(User user, Long roleSpecificId, String rollNo, String className) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putLong(Constants.KEY_USER_ID, user.getId());
        editor.putString(Constants.KEY_USER_NAME, user.getName());
        editor.putString(Constants.KEY_USER_EMAIL, user.getEmail());
        editor.putString(Constants.KEY_USER_ROLE, user.getRole());

        if (Constants.ROLE_STUDENT.equals(user.getRole())) {
            if (roleSpecificId != null) {
                editor.putLong(Constants.KEY_STUDENT_ID, roleSpecificId);
            }
            if (rollNo != null) {
                editor.putString(Constants.KEY_ROLL_NO, rollNo);
            }
            if (className != null) {
                editor.putString(Constants.KEY_CLASS_NAME, className);
            }
        } else if (Constants.ROLE_FACULTY.equals(user.getRole())) {
            if (roleSpecificId != null) {
                editor.putLong(Constants.KEY_FACULTY_ID, roleSpecificId);
            }
        }
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return prefs.getLong(Constants.KEY_USER_ID, -1);
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    public String getUserRole() {
        return prefs.getString(Constants.KEY_USER_ROLE, "");
    }

    public long getStudentId() {
        return prefs.getLong(Constants.KEY_STUDENT_ID, -1);
    }

    public long getFacultyId() {
        return prefs.getLong(Constants.KEY_FACULTY_ID, -1);
    }

    public String getRollNo() {
        return prefs.getString(Constants.KEY_ROLL_NO, "");
    }

    public String getClassName() {
        return prefs.getString(Constants.KEY_CLASS_NAME, "");
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
