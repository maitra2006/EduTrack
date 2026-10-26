package com.edutrack.app.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.edutrack.app.databinding.ActivitySplashBinding;
import com.edutrack.app.ui.admin.AdminMainActivity;
import com.edutrack.app.ui.faculty.FacultyMainActivity;
import com.edutrack.app.ui.student.StudentMainActivity;
import com.edutrack.app.util.Constants;
import com.edutrack.app.util.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1200;
    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(this::routeNextScreen, SPLASH_DELAY_MS);
    }

    private void routeNextScreen() {
        if (isFinishing()) {
            return;
        }

        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole();
            Intent intent;
            if (Constants.ROLE_STUDENT.equals(role)) {
                intent = new Intent(this, StudentMainActivity.class);
            } else if (Constants.ROLE_FACULTY.equals(role)) {
                intent = new Intent(this, FacultyMainActivity.class);
            } else if (Constants.ROLE_ADMIN.equals(role)) {
                intent = new Intent(this, AdminMainActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
