package com.edutrack.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.edutrack.app.R;
import com.edutrack.app.database.DatabaseHelper;
import com.edutrack.app.databinding.ActivityLoginBinding;
import com.edutrack.app.model.Faculty;
import com.edutrack.app.model.Student;
import com.edutrack.app.model.User;
import com.edutrack.app.ui.admin.AdminMainActivity;
import com.edutrack.app.ui.faculty.FacultyMainActivity;
import com.edutrack.app.ui.student.StudentMainActivity;
import com.edutrack.app.util.Constants;
import com.edutrack.app.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private boolean isDemoExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        setupRoleToggle();
        setupDemoAccountsSection();
        setupLoginButton();
    }

    private void setupRoleToggle() {
        // Default selection: Student
        binding.toggleGroupRole.check(R.id.btnRoleStudent);

        binding.toggleGroupRole.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                hideErrorMessage();
            }
        });
    }

    private String getSelectedRole() {
        int checkedId = binding.toggleGroupRole.getCheckedButtonId();
        if (checkedId == R.id.btnRoleFaculty) {
            return Constants.ROLE_FACULTY;
        } else if (checkedId == R.id.btnRoleAdmin) {
            return Constants.ROLE_ADMIN;
        } else {
            return Constants.ROLE_STUDENT;
        }
    }

    private void setSelectedRole(String role) {
        if (Constants.ROLE_FACULTY.equals(role)) {
            binding.toggleGroupRole.check(R.id.btnRoleFaculty);
        } else if (Constants.ROLE_ADMIN.equals(role)) {
            binding.toggleGroupRole.check(R.id.btnRoleAdmin);
        } else {
            binding.toggleGroupRole.check(R.id.btnRoleStudent);
        }
    }

    private void setupDemoAccountsSection() {
        // Toggle expansion on header click
        binding.layoutDemoHeader.setOnClickListener(v -> {
            isDemoExpanded = !isDemoExpanded;
            binding.layoutDemoContent.setVisibility(isDemoExpanded ? View.VISIBLE : View.GONE);
            binding.ivDemoExpandIcon.setImageResource(
                    isDemoExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more
            );
        });

        // Quick auto-fill for Admin
        binding.layoutDemoAdmin.setOnClickListener(v -> {
            setSelectedRole(Constants.ROLE_ADMIN);
            binding.etEmail.setText("admin@edutrack.com");
            binding.etPassword.setText("admin123");
            hideErrorMessage();
        });

        // Quick auto-fill for Faculty
        binding.layoutDemoFaculty.setOnClickListener(v -> {
            setSelectedRole(Constants.ROLE_FACULTY);
            binding.etEmail.setText("meera@edutrack.com");
            binding.etPassword.setText("faculty123");
            hideErrorMessage();
        });

        // Quick auto-fill for Student
        binding.layoutDemoStudent.setOnClickListener(v -> {
            setSelectedRole(Constants.ROLE_STUDENT);
            binding.etEmail.setText("aarav@edutrack.com");
            binding.etPassword.setText("student123");
            hideErrorMessage();
        });
    }

    private void setupLoginButton() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        hideErrorMessage();

        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String role = getSelectedRole();

        // Validation
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_email_empty));
            binding.etEmail.requestFocus();
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_password_empty));
            binding.etPassword.requestFocus();
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        // Authenticate against SQLite
        User user = databaseHelper.authenticateUser(email, password, role);

        if (user == null) {
            showErrorMessage(getString(R.string.error_invalid_credentials));
            return;
        }

        // Successful authentication: save session with role-specific details
        Long roleSpecificId = null;
        String rollNo = null;
        String className = null;

        if (Constants.ROLE_STUDENT.equals(user.getRole())) {
            Student student = databaseHelper.getStudentByUserId(user.getId());
            if (student != null) {
                roleSpecificId = student.getStudentId();
                rollNo = student.getRollNo();
                className = student.getClassName();
            }
        } else if (Constants.ROLE_FACULTY.equals(user.getRole())) {
            Faculty faculty = databaseHelper.getFacultyByUserId(user.getId());
            if (faculty != null) {
                roleSpecificId = faculty.getFacultyId();
            }
        }

        sessionManager.saveUserSession(user, roleSpecificId, rollNo, className);

        // Navigate to corresponding dashboard
        Intent intent;
        switch (user.getRole()) {
            case Constants.ROLE_FACULTY:
                intent = new Intent(this, FacultyMainActivity.class);
                break;
            case Constants.ROLE_ADMIN:
                intent = new Intent(this, AdminMainActivity.class);
                break;
            case Constants.ROLE_STUDENT:
            default:
                intent = new Intent(this, StudentMainActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }

    private void showErrorMessage(String message) {
        binding.tvErrorMessage.setText(message);
        binding.tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        binding.tvErrorMessage.setVisibility(View.GONE);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
    }
}
