package com.edutrack.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.edutrack.app.model.Announcement;
import com.edutrack.app.model.AttendanceRecord;
import com.edutrack.app.model.Faculty;
import com.edutrack.app.model.Student;
import com.edutrack.app.model.Subject;
import com.edutrack.app.model.SubjectAttendanceSummary;
import com.edutrack.app.model.User;
import com.edutrack.app.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_FACULTY = "faculty";
    public static final String TABLE_SUBJECTS = "subjects";
    public static final String TABLE_ASSIGNMENTS = "faculty_subject_assignments";
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String TABLE_ANNOUNCEMENTS = "announcements";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "phone TEXT);");

        // 2. Students Table
        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL UNIQUE, " +
                "roll_no TEXT NOT NULL UNIQUE, " +
                "class_name TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id) ON DELETE CASCADE);");

        // 3. Faculty Table
        db.execSQL("CREATE TABLE " + TABLE_FACULTY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL UNIQUE, " +
                "department TEXT NOT NULL, " +
                "designation TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id) ON DELETE CASCADE);");

        // 4. Subjects Table
        db.execSQL("CREATE TABLE " + TABLE_SUBJECTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT NOT NULL UNIQUE, " +
                "name TEXT NOT NULL, " +
                "department TEXT NOT NULL);");

        // 5. Faculty Subject Assignments Table
        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "faculty_id INTEGER NOT NULL, " +
                "subject_id INTEGER NOT NULL, " +
                "class_name TEXT NOT NULL, " +
                "UNIQUE(faculty_id, subject_id, class_name), " +
                "FOREIGN KEY(faculty_id) REFERENCES " + TABLE_FACULTY + "(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(subject_id) REFERENCES " + TABLE_SUBJECTS + "(id) ON DELETE CASCADE);");

        // 6. Attendance Table
        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "subject_id INTEGER NOT NULL, " +
                "date TEXT NOT NULL, " +
                "status INTEGER NOT NULL, " +
                "UNIQUE(student_id, subject_id, date), " +
                "FOREIGN KEY(student_id) REFERENCES " + TABLE_STUDENTS + "(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(subject_id) REFERENCES " + TABLE_SUBJECTS + "(id) ON DELETE CASCADE);");

        // 7. Announcements Table
        db.execSQL("CREATE TABLE " + TABLE_ANNOUNCEMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "faculty_name TEXT NOT NULL, " +
                "class_name TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "created_at TEXT NOT NULL);");

        // Seed demo data on initial creation
        seedDemoData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACULTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =========================================================================
    // SEED DEMO DATA
    // =========================================================================
    private void seedDemoData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // 1. Admin Account
            ContentValues adminCv = new ContentValues();
            adminCv.put("name", "System Administrator");
            adminCv.put("email", "admin@edutrack.com");
            adminCv.put("password", "admin123");
            adminCv.put("role", Constants.ROLE_ADMIN);
            adminCv.put("phone", "+91 98200 11223");
            db.insert(TABLE_USERS, null, adminCv);

            // 2. Faculty Accounts (4 faculty members)
            String[][] facultyData = {
                    {"Dr. Meera Iyer", "meera@edutrack.com", "faculty123", "Computer Engineering", "Associate Professor", "+91 98201 22334"},
                    {"Prof. Rajesh Patel", "rajesh@edutrack.com", "faculty123", "Computer Engineering", "Assistant Professor", "+91 98202 33445"},
                    {"Dr. Anita Deshmukh", "anita@edutrack.com", "faculty123", "Computer Engineering", "Professor & HOD", "+91 98203 44556"},
                    {"Prof. Sandeep Joshi", "sandeep@edutrack.com", "faculty123", "Computer Engineering", "Assistant Professor", "+91 98204 55667"}
            };

            long[] facultyIds = new long[facultyData.length];
            for (int i = 0; i < facultyData.length; i++) {
                ContentValues fUser = new ContentValues();
                fUser.put("name", facultyData[i][0]);
                fUser.put("email", facultyData[i][1]);
                fUser.put("password", facultyData[i][2]);
                fUser.put("role", Constants.ROLE_FACULTY);
                fUser.put("phone", facultyData[i][5]);
                long userId = db.insert(TABLE_USERS, null, fUser);

                ContentValues fEntry = new ContentValues();
                fEntry.put("user_id", userId);
                fEntry.put("department", facultyData[i][3]);
                fEntry.put("designation", facultyData[i][4]);
                facultyIds[i] = db.insert(TABLE_FACULTY, null, fEntry);
            }

            // 3. 4 Subjects
            String[][] subjectsData = {
                    {"CS301", "Data Structures", "Computer Engineering"},
                    {"CS302", "Database Management Systems", "Computer Engineering"},
                    {"CS303", "Operating Systems", "Computer Engineering"},
                    {"CS304", "Computer Networks", "Computer Engineering"}
            };

            long[] subjectIds = new long[subjectsData.length];
            for (int i = 0; i < subjectsData.length; i++) {
                ContentValues sCv = new ContentValues();
                sCv.put("code", subjectsData[i][0]);
                sCv.put("name", subjectsData[i][1]);
                sCv.put("department", subjectsData[i][2]);
                subjectIds[i] = db.insert(TABLE_SUBJECTS, null, sCv);

                // Assign subject to faculty for class "SE Computer Engineering - Div A"
                ContentValues aCv = new ContentValues();
                aCv.put("faculty_id", facultyIds[i]);
                aCv.put("subject_id", subjectIds[i]);
                aCv.put("class_name", Constants.DEMO_CLASS_NAME);
                db.insert(TABLE_ASSIGNMENTS, null, aCv);
            }

            // 4. 15 Students in "SE Computer Engineering - Div A"
            String[][] studentsData = {
                    {"Aarav Sharma", "aarav@edutrack.com", "2101", "+91 97100 11001"},
                    {"Ananya Verma", "ananya@edutrack.com", "2102", "+91 97100 11002"},
                    {"Rohan Kulkarni", "rohan@edutrack.com", "2103", "+91 97100 11003"},
                    {"Priya Nair", "priya@edutrack.com", "2104", "+91 97100 11004"},
                    {"Aditya Patil", "aditya@edutrack.com", "2105", "+91 97100 11005"},
                    {"Sneha Reddy", "sneha@edutrack.com", "2106", "+91 97100 11006"},
                    {"Ishaan Gupta", "ishaan@edutrack.com", "2107", "+91 97100 11007"},
                    {"Diya Joshi", "diya@edutrack.com", "2108", "+91 97100 11008"},
                    {"Tanmay Shinde", "tanmay@edutrack.com", "2109", "+91 97100 11009"},
                    {"Kavya Iyer", "kavya@edutrack.com", "2110", "+91 97100 11010"},
                    {"Siddharth Mehta", "siddharth@edutrack.com", "2111", "+91 97100 11011"},
                    {"Pooja Choudhury", "pooja@edutrack.com", "2112", "+91 97100 11012"},
                    {"Aryan Deshmukh", "aryan@edutrack.com", "2113", "+91 97100 11013"},
                    {"Riya Sen", "riya@edutrack.com", "2114", "+91 97100 11014"},
                    {"Varun Bhat", "varun@edutrack.com", "2115", "+91 97100 11015"}
            };

            long[] studentIds = new long[studentsData.length];
            for (int i = 0; i < studentsData.length; i++) {
                ContentValues sUser = new ContentValues();
                sUser.put("name", studentsData[i][0]);
                sUser.put("email", studentsData[i][1]);
                sUser.put("password", "student123");
                sUser.put("role", Constants.ROLE_STUDENT);
                sUser.put("phone", studentsData[i][3]);
                long userId = db.insert(TABLE_USERS, null, sUser);

                ContentValues sEntry = new ContentValues();
                sEntry.put("user_id", userId);
                sEntry.put("roll_no", studentsData[i][2]);
                sEntry.put("class_name", Constants.DEMO_CLASS_NAME);
                studentIds[i] = db.insert(TABLE_STUDENTS, null, sEntry);
            }

            // 5. Past 30 Lecture Dates
            String[] lectureDates = {
                    "2026-08-18", "2026-08-19", "2026-08-20", "2026-08-21", "2026-08-22",
                    "2026-08-25", "2026-08-26", "2026-08-27", "2026-08-28", "2026-08-29",
                    "2026-09-01", "2026-09-02", "2026-09-03", "2026-09-04", "2026-09-05",
                    "2026-09-08", "2026-09-09", "2026-09-10", "2026-09-11", "2026-09-12",
                    "2026-09-15", "2026-09-16", "2026-09-17", "2026-09-18", "2026-09-19",
                    "2026-09-22", "2026-09-23", "2026-09-24", "2026-09-25", "2026-09-26"
            };

            // Seed attendance:
            // Students 0..5: High attendance >= 75%
            // Note for Student 0 (Aarav): CS301=27/30 (90%), CS302=26/30 (86.7%), CS303=24/30 (80%), CS304=21/30 (70% -> triggers warning card & alert!)
            // Students 6..10: Amber attendance 65-74% (e.g., 20-22 out of 30)
            // Students 11..14: Red attendance < 65% (e.g., 15-18 out of 30)
            for (int sIdx = 0; sIdx < studentIds.length; sIdx++) {
                long stId = studentIds[sIdx];

                for (int subIdx = 0; subIdx < subjectIds.length; subIdx++) {
                    long subId = subjectIds[subIdx];

                    int targetPresent;
                    if (sIdx == 0) {
                        // Aarav specific distribution:
                        if (subIdx == 0) targetPresent = 27; // 90.0%
                        else if (subIdx == 1) targetPresent = 26; // 86.7%
                        else if (subIdx == 2) targetPresent = 24; // 80.0%
                        else targetPresent = 21; // 70.0% (Warning card trigger)
                    } else if (sIdx <= 5) {
                        // High tier: 25 to 28 presents
                        targetPresent = 25 + ((sIdx + subIdx) % 4);
                    } else if (sIdx <= 10) {
                        // Amber tier: 20 to 22 presents (66.7% - 73.3%)
                        targetPresent = 20 + ((sIdx + subIdx) % 3);
                    } else {
                        // Low tier: 15 to 18 presents (50.0% - 60.0%)
                        targetPresent = 15 + ((sIdx + subIdx) % 4);
                    }

                    for (int d = 0; d < lectureDates.length; d++) {
                        // Deterministic spread of attendance
                        int status = (d < targetPresent) ? Constants.STATUS_PRESENT : Constants.STATUS_ABSENT;

                        ContentValues att = new ContentValues();
                        att.put("student_id", stId);
                        att.put("subject_id", subId);
                        att.put("date", lectureDates[d]);
                        att.put("status", status);
                        db.insert(TABLE_ATTENDANCE, null, att);
                    }
                }
            }

            // 6. 3 Sample Announcements
            String[][] announcementsData = {
                    {"Dr. Meera Iyer", Constants.DEMO_CLASS_NAME, "Unit Test 1 Schedule Announced", "Unit Test 1 for Data Structures will be conducted on Monday at 10:00 AM. Portions: Stacks, Queues and Linked Lists.", "2026-10-18 10:30"},
                    {"Prof. Rajesh Patel", Constants.DEMO_CLASS_NAME, "DBMS Mini-Project Submission Guidelines", "Please submit your ER diagrams and schema normalization documents by Friday evening through your group leaders.", "2026-10-20 14:15"},
                    {"Dr. Anita Deshmukh", Constants.DEMO_CLASS_NAME, "Guest Lecture on Cloud Computing", "A specialized guest lecture by industry experts is organized this Wednesday in Seminar Hall 2 at 11:30 AM.", "2026-10-22 09:00"}
            };

            for (String[] ann : announcementsData) {
                ContentValues annCv = new ContentValues();
                annCv.put("faculty_name", ann[0]);
                annCv.put("class_name", ann[1]);
                annCv.put("title", ann[2]);
                annCv.put("message", ann[3]);
                annCv.put("created_at", ann[4]);
                db.insert(TABLE_ANNOUNCEMENTS, null, annCv);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // =========================================================================
    // AUTHENTICATION & USER METHODS
    // =========================================================================
    public User authenticateUser(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                "LOWER(email) = ? AND password = ? AND role = ?",
                new String[]{email.trim().toLowerCase(), password, role},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = new User(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getString(cursor.getColumnIndexOrThrow("role")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                );
            }
            cursor.close();
        }
        return user;
    }

    public boolean updatePassword(long userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    public Student getStudentByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Student student = null;
        String query = "SELECT s.id AS student_id, s.user_id, u.name, u.email, u.phone, s.roll_no, s.class_name " +
                "FROM " + TABLE_STUDENTS + " s " +
                "JOIN " + TABLE_USERS + " u ON s.user_id = u.id " +
                "WHERE s.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                student = new Student(
                        cursor.getLong(cursor.getColumnIndexOrThrow("student_id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("roll_no")),
                        cursor.getString(cursor.getColumnIndexOrThrow("class_name"))
                );
            }
            cursor.close();
        }
        return student;
    }

    public Faculty getFacultyByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Faculty faculty = null;
        String query = "SELECT f.id AS faculty_id, f.user_id, u.name, u.email, u.phone, f.department, f.designation " +
                "FROM " + TABLE_FACULTY + " f " +
                "JOIN " + TABLE_USERS + " u ON f.user_id = u.id " +
                "WHERE f.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                faculty = new Faculty(
                        cursor.getLong(cursor.getColumnIndexOrThrow("faculty_id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("department")),
                        cursor.getString(cursor.getColumnIndexOrThrow("designation"))
                );
            }
            cursor.close();
        }
        return faculty;
    }

    // =========================================================================
    // STUDENT SIDE QUERIES
    // =========================================================================
    public List<SubjectAttendanceSummary> getAllSubjectSummariesForStudent(long studentId) {
        List<SubjectAttendanceSummary> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Retrieve all subjects for the student's class
        String query = "SELECT sub.id AS subject_id, sub.code AS subject_code, sub.name AS subject_name, " +
                "COALESCE(u.name, 'Faculty') AS faculty_name, " +
                "COUNT(a.id) AS total_lectures, " +
                "SUM(CASE WHEN a.status = 1 THEN 1 ELSE 0 END) AS attended_lectures " +
                "FROM " + TABLE_STUDENTS + " st " +
                "JOIN " + TABLE_ASSIGNMENTS + " fsa ON st.class_name = fsa.class_name " +
                "JOIN " + TABLE_SUBJECTS + " sub ON fsa.subject_id = sub.id " +
                "LEFT JOIN " + TABLE_FACULTY + " f ON fsa.faculty_id = f.id " +
                "LEFT JOIN " + TABLE_USERS + " u ON f.user_id = u.id " +
                "LEFT JOIN " + TABLE_ATTENDANCE + " a ON a.subject_id = sub.id AND a.student_id = st.id " +
                "WHERE st.id = ? " +
                "GROUP BY sub.id, sub.code, sub.name, faculty_name " +
                "ORDER BY sub.code ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long subjectId = cursor.getLong(cursor.getColumnIndexOrThrow("subject_id"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("subject_code"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"));
                String facultyName = cursor.getString(cursor.getColumnIndexOrThrow("faculty_name"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_lectures"));
                int attended = cursor.getInt(cursor.getColumnIndexOrThrow("attended_lectures"));

                SubjectAttendanceSummary summary = new SubjectAttendanceSummary(
                        subjectId, code, name, facultyName, attended, total
                );
                list.add(summary);
            }
            cursor.close();
        }
        return list;
    }

    public SubjectAttendanceSummary getOverallAttendanceForStudent(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(id) AS total_lectures, " +
                "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS attended_lectures " +
                "FROM " + TABLE_ATTENDANCE + " " +
                "WHERE student_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        int total = 0;
        int attended = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getInt(cursor.getColumnIndexOrThrow("total_lectures"));
                attended = cursor.getInt(cursor.getColumnIndexOrThrow("attended_lectures"));
            }
            cursor.close();
        }
        return new SubjectAttendanceSummary(0, "ALL", "Overall Attendance", "", attended, total);
    }

    public List<Subject> getSubjectsForClass(String className) {
        List<Subject> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sub.id, sub.code, sub.name, sub.department, u.name AS faculty_name " +
                "FROM " + TABLE_ASSIGNMENTS + " a " +
                "JOIN " + TABLE_SUBJECTS + " sub ON a.subject_id = sub.id " +
                "JOIN " + TABLE_FACULTY + " f ON a.faculty_id = f.id " +
                "JOIN " + TABLE_USERS + " u ON f.user_id = u.id " +
                "WHERE a.class_name = ? " +
                "ORDER BY sub.code ASC";

        Cursor cursor = db.rawQuery(query, new String[]{className});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Subject sub = new Subject(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("department")),
                        cursor.getString(cursor.getColumnIndexOrThrow("faculty_name"))
                );
                list.add(sub);
            }
            cursor.close();
        }
        return list;
    }

    // =========================================================================
    // FACULTY SIDE QUERIES
    // =========================================================================
    public List<Subject> getSubjectsForFaculty(long facultyId) {
        List<Subject> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT sub.id, sub.code, sub.name, sub.department " +
                "FROM " + TABLE_ASSIGNMENTS + " a " +
                "JOIN " + TABLE_SUBJECTS + " sub ON a.subject_id = sub.id " +
                "WHERE a.faculty_id = ? " +
                "ORDER BY sub.code ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(facultyId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Subject sub = new Subject(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("department"))
                );
                list.add(sub);
            }
            cursor.close();
        }
        return list;
    }

    public List<String> getClassesForFacultySubject(long facultyId, long subjectId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ASSIGNMENTS, new String[]{"class_name"},
                "faculty_id = ? AND subject_id = ?",
                new String[]{String.valueOf(facultyId), String.valueOf(subjectId)},
                null, null, "class_name ASC", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            cursor.close();
        }
        return list;
    }

    public List<AttendanceRecord> getAttendanceForClassSubjectAndDate(long subjectId, String className, String date) {
        List<AttendanceRecord> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all students of class and left join attendance for the subject & date
        String query = "SELECT st.id AS student_id, u.name AS student_name, st.roll_no, " +
                "COALESCE(a.id, -1) AS attendance_id, " +
                "COALESCE(a.status, 1) AS status " +
                "FROM " + TABLE_STUDENTS + " st " +
                "JOIN " + TABLE_USERS + " u ON st.user_id = u.id " +
                "LEFT JOIN " + TABLE_ATTENDANCE + " a ON a.student_id = st.id AND a.subject_id = ? AND a.date = ? " +
                "WHERE st.class_name = ? " +
                "ORDER BY st.roll_no ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(subjectId), date, className});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long attId = cursor.getLong(cursor.getColumnIndexOrThrow("attendance_id"));
                long studentId = cursor.getLong(cursor.getColumnIndexOrThrow("student_id"));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                String rollNo = cursor.getString(cursor.getColumnIndexOrThrow("roll_no"));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

                list.add(new AttendanceRecord(attId, studentId, studentName, rollNo, subjectId, date, status));
            }
            cursor.close();
        }
        return list;
    }

    public void saveOrUpdateAttendance(long studentId, long subjectId, String date, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("student_id", studentId);
        cv.put("subject_id", subjectId);
        cv.put("date", date);
        cv.put("status", status);

        // Conflict algorithm REPLACE updates existing record or inserts without duplicate!
        db.insertWithOnConflict(TABLE_ATTENDANCE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // =========================================================================
    // ANNOUNCEMENTS
    // =========================================================================
    public List<Announcement> getAnnouncementsForClass(String className) {
        List<Announcement> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANNOUNCEMENTS, null,
                "class_name = ?", new String[]{className},
                null, null, "id DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Announcement(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("faculty_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("class_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("message")),
                        cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                ));
            }
            cursor.close();
        }
        return list;
    }

    public List<Announcement> getAllAnnouncements() {
        List<Announcement> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANNOUNCEMENTS, null, null, null, null, null, "id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Announcement(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("faculty_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("class_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("message")),
                        cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                ));
            }
            cursor.close();
        }
        return list;
    }

    public long addAnnouncement(String facultyName, String className, String title, String message, String createdAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("faculty_name", facultyName);
        cv.put("class_name", className);
        cv.put("title", title);
        cv.put("message", message);
        cv.put("created_at", createdAt);
        return db.insert(TABLE_ANNOUNCEMENTS, null, cv);
    }

    public boolean deleteAnnouncement(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ANNOUNCEMENTS, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // =========================================================================
    // ADMIN SIDE QUERIES & STATS
    // =========================================================================
    public int getTotalStudentsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STUDENTS, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int getTotalFacultyCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FACULTY, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int getTotalSubjectsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SUBJECTS, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public double getCollegeAverageAttendance() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(id) AS total, SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS attended FROM " + TABLE_ATTENDANCE;
        Cursor cursor = db.rawQuery(query, null);
        double avg = 0.0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                int attended = cursor.getInt(cursor.getColumnIndexOrThrow("attended"));
                if (total > 0) {
                    avg = ((double) attended / total) * 100.0;
                }
            }
            cursor.close();
        }
        return avg;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.id AS student_id, s.user_id, u.name, u.email, u.phone, s.roll_no, s.class_name " +
                "FROM " + TABLE_STUDENTS + " s " +
                "JOIN " + TABLE_USERS + " u ON s.user_id = u.id " +
                "ORDER BY s.class_name ASC, s.roll_no ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Student(
                        cursor.getLong(cursor.getColumnIndexOrThrow("student_id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("roll_no")),
                        cursor.getString(cursor.getColumnIndexOrThrow("class_name"))
                ));
            }
            cursor.close();
        }
        return list;
    }

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT f.id AS faculty_id, f.user_id, u.name, u.email, u.phone, f.department, f.designation " +
                "FROM " + TABLE_FACULTY + " f " +
                "JOIN " + TABLE_USERS + " u ON f.user_id = u.id " +
                "ORDER BY u.name ASC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Faculty(
                        cursor.getLong(cursor.getColumnIndexOrThrow("faculty_id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("department")),
                        cursor.getString(cursor.getColumnIndexOrThrow("designation"))
                ));
            }
            cursor.close();
        }
        return list;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUBJECTS, null, null, null, null, null, "code ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Subject(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("department"))
                ));
            }
            cursor.close();
        }
        return list;
    }

    public long addStudent(String name, String email, String password, String phone, String rollNo, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long studentId = -1;
        try {
            ContentValues uCv = new ContentValues();
            uCv.put("name", name);
            uCv.put("email", email.trim().toLowerCase());
            uCv.put("password", password);
            uCv.put("role", Constants.ROLE_STUDENT);
            uCv.put("phone", phone);
            long userId = db.insert(TABLE_USERS, null, uCv);

            if (userId != -1) {
                ContentValues sCv = new ContentValues();
                sCv.put("user_id", userId);
                sCv.put("roll_no", rollNo);
                sCv.put("class_name", className);
                studentId = db.insert(TABLE_STUDENTS, null, sCv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return studentId;
    }

    public boolean updateStudent(long studentId, long userId, String name, String email, String phone, String rollNo, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            ContentValues uCv = new ContentValues();
            uCv.put("name", name);
            uCv.put("email", email.trim().toLowerCase());
            uCv.put("phone", phone);
            db.update(TABLE_USERS, uCv, "id = ?", new String[]{String.valueOf(userId)});

            ContentValues sCv = new ContentValues();
            sCv.put("roll_no", rollNo);
            sCv.put("class_name", className);
            db.update(TABLE_STUDENTS, sCv, "id = ?", new String[]{String.valueOf(studentId)});

            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public boolean deleteStudent(long studentId, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Deleting user cascades to student, attendance
        return db.delete(TABLE_USERS, "id = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    public long addFaculty(String name, String email, String password, String phone, String department, String designation) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long facultyId = -1;
        try {
            ContentValues uCv = new ContentValues();
            uCv.put("name", name);
            uCv.put("email", email.trim().toLowerCase());
            uCv.put("password", password);
            uCv.put("role", Constants.ROLE_FACULTY);
            uCv.put("phone", phone);
            long userId = db.insert(TABLE_USERS, null, uCv);

            if (userId != -1) {
                ContentValues fCv = new ContentValues();
                fCv.put("user_id", userId);
                fCv.put("department", department);
                fCv.put("designation", designation);
                facultyId = db.insert(TABLE_FACULTY, null, fCv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return facultyId;
    }

    public boolean updateFaculty(long facultyId, long userId, String name, String email, String phone, String department, String designation) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            ContentValues uCv = new ContentValues();
            uCv.put("name", name);
            uCv.put("email", email.trim().toLowerCase());
            uCv.put("phone", phone);
            db.update(TABLE_USERS, uCv, "id = ?", new String[]{String.valueOf(userId)});

            ContentValues fCv = new ContentValues();
            fCv.put("department", department);
            fCv.put("designation", designation);
            db.update(TABLE_FACULTY, fCv, "id = ?", new String[]{String.valueOf(facultyId)});

            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public boolean deleteFaculty(long facultyId, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, "id = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    public long addSubject(String code, String name, String department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("code", code.trim().toUpperCase());
        cv.put("name", name.trim());
        cv.put("department", department.trim());
        return db.insert(TABLE_SUBJECTS, null, cv);
    }

    public boolean updateSubject(long id, String code, String name, String department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("code", code.trim().toUpperCase());
        cv.put("name", name.trim());
        cv.put("department", department.trim());
        return db.update(TABLE_SUBJECTS, cv, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteSubject(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SUBJECTS, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public long assignFacultySubject(long facultyId, long subjectId, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("faculty_id", facultyId);
        cv.put("subject_id", subjectId);
        cv.put("class_name", className);
        return db.insertWithOnConflict(TABLE_ASSIGNMENTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean deleteAssignment(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ASSIGNMENTS, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<AttendanceRecord> getAllAttendanceRecords() {
        List<AttendanceRecord> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.id, a.student_id, u.name AS student_name, st.roll_no, a.subject_id, a.date, a.status " +
                "FROM " + TABLE_ATTENDANCE + " a " +
                "JOIN " + TABLE_STUDENTS + " st ON a.student_id = st.id " +
                "JOIN " + TABLE_USERS + " u ON st.user_id = u.id " +
                "ORDER BY a.date DESC, st.roll_no ASC LIMIT 200";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new AttendanceRecord(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("student_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("student_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("roll_no")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("subject_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("status"))
                ));
            }
            cursor.close();
        }
        return list;
    }

    public boolean updateAttendanceRecord(long id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        return db.update(TABLE_ATTENDANCE, cv, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteAttendanceRecord(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ATTENDANCE, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
