-- ============================================================================
-- UNIMIS - Faculty Academic Management System (MySQL Database Schema & Test Data)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. DATABASE CREATION AND INITIALIZATION
-- ----------------------------------------------------------------------------

DROP DATABASE IF EXISTS unimis;
CREATE DATABASE unimis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE unimis;

-- Disable Foreign Key checks and Safe Updates during setup
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- ----------------------------------------------------------------------------
-- 2. TABLE CREATIONS
-- ----------------------------------------------------------------------------

-- 2.1 Departments Table
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(20) NOT NULL UNIQUE,
    department_name VARCHAR(150) NOT NULL UNIQUE,
    description VARCHAR(500),
    status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.2 User Superclass Table (Relational Inheritance Base)
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    nic VARCHAR(20) UNIQUE,
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other'),
    email VARCHAR(150) NOT NULL UNIQUE,
    contact_no VARCHAR(20),
    address VARCHAR(255),
    profile_picture VARCHAR(500),
    department_id INT NULL,
    user_status ENUM('Active', 'Inactive', 'Suspended') NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_department
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON UPDATE CASCADE ON DELETE SET NULL
);

-- 2.3 Admin Subclass Table
CREATE TABLE admins (
    user_id BIGINT PRIMARY KEY,
    admin_code VARCHAR(30) NOT NULL UNIQUE,
    designation VARCHAR(100) DEFAULT 'System Administrator',
    CONSTRAINT fk_admins_user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- 2.4 Lecturer Subclass Table
CREATE TABLE lecturers (
    user_id BIGINT PRIMARY KEY,
    lecturer_code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(30) DEFAULT 'Dr.',
    specialization VARCHAR(150),
    CONSTRAINT fk_lecturers_user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- 2.5 Technical Officer Subclass Table
CREATE TABLE technical_officers (
    user_id BIGINT PRIMARY KEY,
    technical_officer_code VARCHAR(30) NOT NULL UNIQUE,
    designation VARCHAR(100) DEFAULT 'Technical Officer',
    assigned_labs VARCHAR(200) DEFAULT 'ICT Laboratories',
    CONSTRAINT fk_technical_officers_user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- 2.6 Undergraduate Subclass Table
CREATE TABLE undergraduates (
    user_id BIGINT PRIMARY KEY,
    registration_no VARCHAR(30) NOT NULL UNIQUE, -- e.g. TG/2024/2061
    index_no VARCHAR(30) UNIQUE,
    batch_year YEAR NOT NULL,
    academic_year VARCHAR(20) DEFAULT '2026',
    student_type ENUM('Proper', 'Repeat', 'Batch Missed') NOT NULL DEFAULT 'Proper',
    intake VARCHAR(50) DEFAULT '2024',
    current_level TINYINT UNSIGNED DEFAULT 2,
    current_semester TINYINT UNSIGNED DEFAULT 1,
    previous_credits DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    previous_grade_points DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    admission_date DATE,
    guardian_name VARCHAR(150),
    guardian_contact VARCHAR(20),
    CONSTRAINT fk_undergraduates_user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE, CHECK (previous_credits >= 0 AND previous_grade_points >= 0)
);

-- 2.7 Courses Table
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE, -- e.g. ICT2132
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    theory_credits DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    practical_credits DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    total_credits DECIMAL(4,1) GENERATED ALWAYS AS (theory_credits + practical_credits) STORED,
    ca_weight DECIMAL(5,2) NOT NULL DEFAULT 40.00,
    final_weight DECIMAL(5,2) GENERATED ALWAYS AS (100.00 - ca_weight) STORED,
    department_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
    semester ENUM('Semester I', 'Semester II') NOT NULL DEFAULT 'Semester I',
    status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_department
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON UPDATE CASCADE ON DELETE RESTRICT,
        CHECK (theory_credits >= 0 AND practical_credits >= 0 AND (theory_credits + practical_credits) > 0),
        CHECK (ca_weight >= 0 AND ca_weight <= 100)
);

-- 2.8 Course Lecturers Mapping Table
CREATE TABLE course_lecturers (
    course_id INT NOT NULL,
    lecturer_id BIGINT NOT NULL,
    responsibility ENUM('Coordinator', 'Lecturer', 'Practical Instructor') NOT NULL DEFAULT 'Lecturer',
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id, lecturer_id),
    CONSTRAINT fk_cl_course
      FOREIGN KEY (course_id)
          REFERENCES courses(course_id)
          ON UPDATE CASCADE
          ON DELETE CASCADE,
    CONSTRAINT fk_cl_lecturer
      FOREIGN KEY (lecturer_id)
          REFERENCES lecturers(user_id)
          ON UPDATE CASCADE
          ON DELETE CASCADE
);

-- 2.9 Course Materials Table
CREATE TABLE course_materials (
    material_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    lecturer_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    material_type ENUM('Lecture Note', 'Slide', 'PDF', 'Document', 'Video', 'Link', 'Other') NOT NULL DEFAULT 'Document',
    file_path VARCHAR(500),
    external_url VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_material_course
      FOREIGN KEY (course_id)
          REFERENCES courses(course_id)
          ON UPDATE CASCADE
          ON DELETE CASCADE,
    CONSTRAINT fk_material_lecturer
      FOREIGN KEY (lecturer_id)
          REFERENCES lecturers(user_id)
          ON UPDATE CASCADE
          ON DELETE RESTRICT
);

-- 2.10 Course Enrollments Table
CREATE TABLE enrollments (
    enrollment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
    semester ENUM('Semester I', 'Semester II') NOT NULL DEFAULT 'Semester I',
    enrollment_type ENUM('Normal', 'Repeat', 'Batch Missed') NOT NULL DEFAULT 'Normal',
    enrollment_status ENUM('Enrolled', 'Withdrawn', 'Completed') NOT NULL DEFAULT 'Enrolled',
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_course (student_id, course_id),
    CONSTRAINT fk_enrollments_student
     FOREIGN KEY (student_id)
         REFERENCES undergraduates(user_id)
         ON UPDATE CASCADE
         ON DELETE CASCADE,
    CONSTRAINT fk_enrollments_course
     FOREIGN KEY (course_id)
         REFERENCES courses(course_id)
         ON UPDATE CASCADE
         ON DELETE RESTRICT
);

-- 2.11 Assessment Definitions Table
CREATE TABLE assessments (
    assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    assessment_name VARCHAR(150) NOT NULL,
    assessment_category ENUM('CA', 'FINAL') NOT NULL,
    assessment_type ENUM('Quiz', 'Assignment', 'Midterm', 'Practical', 'Presentation', 'Project', 'Final Examination', 'Other') NOT NULL,
    component ENUM('Theory', 'Practical') NOT NULL DEFAULT 'Theory',
    weight_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    max_mark DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    assessment_date DATE,
    created_by BIGINT NULL,
    CONSTRAINT fk_assessments_course
     FOREIGN KEY (course_id)
         REFERENCES courses(course_id)
         ON UPDATE CASCADE
         ON DELETE CASCADE,
    CONSTRAINT fk_assessments_creator
     FOREIGN KEY (created_by)
         REFERENCES users(user_id)
         ON UPDATE CASCADE
         ON DELETE SET NULL,
    CHECK (weight_percent >= 0 AND weight_percent <= 100),
    CHECK (max_mark > 0)
);

-- 2.12 Student Marks Table
CREATE TABLE marks (
    mark_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    mark DECIMAL(5,2) NOT NULL,
    entered_by BIGINT NULL,
    entered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_assessment (assessment_id, student_id),
    CONSTRAINT fk_marks_assessment
       FOREIGN KEY (assessment_id)
           REFERENCES assessments(assessment_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
    CONSTRAINT fk_marks_student
       FOREIGN KEY (student_id)
           REFERENCES undergraduates(user_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
    CONSTRAINT fk_marks_enterer
       FOREIGN KEY (entered_by)
           REFERENCES users(user_id)
           ON UPDATE CASCADE
           ON DELETE SET NULL,
    CHECK (mark >= 0 AND mark <= 100)
);

-- 2.13 Attendance Sessions Table
CREATE TABLE attendance_sessions (
    attendance_session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    component ENUM('Theory', 'Practical') NOT NULL,
    session_no INT UNSIGNED NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    duration_hours DECIMAL(4,2) NOT NULL DEFAULT 2.00,
    conducted_by BIGINT NULL,
    topic VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_session_course
     FOREIGN KEY (course_id)
         REFERENCES courses(course_id)
         ON UPDATE CASCADE
         ON DELETE CASCADE,
    CONSTRAINT fk_attendance_session_lecturer
     FOREIGN KEY (conducted_by)
         REFERENCES lecturers(user_id)
         ON UPDATE CASCADE
         ON DELETE SET NULL,
    CHECK (session_no BETWEEN 1 AND 15),
    CHECK (duration_hours > 0)
);

-- 2.14 Attendance Records Table
CREATE TABLE attendance_records (
    attendance_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attendance_session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attendance_status ENUM('PRESENT', 'ABSENT', 'MEDICAL', 'LATE') NOT NULL DEFAULT 'ABSENT',
    remarks VARCHAR(255),
    recorded_by BIGINT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_session_student (attendance_session_id, student_id),
    CONSTRAINT fk_attendance_record_session
        FOREIGN KEY (attendance_session_id)
            REFERENCES attendance_sessions(attendance_session_id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_attendance_record_student
        FOREIGN KEY (student_id)
            REFERENCES undergraduates(user_id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_attendance_record_recorder
        FOREIGN KEY (recorded_by)
            REFERENCES users(user_id)
            ON UPDATE CASCADE
            ON DELETE SET NULL
);

-- 2.15 Medical Records Table
CREATE TABLE medicals (
    medical_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id INT NOT NULL,
    attendance_record_id BIGINT NULL,
    medical_date DATE NOT NULL,
    reason VARCHAR(500),
    document_path VARCHAR(500),
    approval_status ENUM('Pending', 'Approved', 'Rejected') NOT NULL DEFAULT 'Pending',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_by BIGINT NULL,
    reviewed_at DATETIME NULL,
    review_comment VARCHAR(500),
    CONSTRAINT fk_medicals_student
      FOREIGN KEY (student_id)
          REFERENCES undergraduates(user_id)
          ON UPDATE CASCADE
          ON DELETE CASCADE,
    CONSTRAINT fk_medicals_course
        FOREIGN KEY (course_id)
            REFERENCES courses (course_id)
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    CONSTRAINT fk_medicals_attendance_record
        FOREIGN KEY (attendance_record_id)
            REFERENCES attendance_records (attendance_record_id)
            ON UPDATE CASCADE
            ON DELETE SET NULL,
    CONSTRAINT fk_medicals_reviewer
        FOREIGN KEY (reviewed_by)
            REFERENCES users (user_id)
            ON UPDATE CASCADE
            ON DELETE SET NULL
);

-- 2.16 Notices Table
CREATE TABLE notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(250) NOT NULL,
    content TEXT NOT NULL,
    notice_type ENUM('General', 'Academic', 'Exam', 'Attendance', 'Event', 'Emergency') NOT NULL DEFAULT 'General',
    target_role ENUM('All', 'Admin', 'Lecturer', 'Technical Officer', 'Undergraduate') NOT NULL DEFAULT 'All',
    department_id INT NULL,
    published_by BIGINT NULL,
    published_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NULL,
    status ENUM('Draft', 'Published', 'Archived') NOT NULL DEFAULT 'Published',
    CONSTRAINT fk_notices_department
     FOREIGN KEY (department_id)
         REFERENCES departments(department_id)
         ON UPDATE CASCADE
         ON DELETE SET NULL,
    CONSTRAINT fk_notices_publisher
     FOREIGN KEY (published_by)
         REFERENCES users(user_id)
         ON UPDATE CASCADE
         ON DELETE SET NULL
);

-- 2.17 Timetable Table
CREATE TABLE timetable_entries (
    timetable_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id INT NOT NULL,
    course_id INT NOT NULL,
    lecturer_id BIGINT NULL,
    batch_year YEAR NOT NULL,
    component ENUM('Theory', 'Practical') NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    venue VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
    semester ENUM('Semester I', 'Semester II') NOT NULL DEFAULT 'Semester I',
    CONSTRAINT fk_timetable_department
       FOREIGN KEY (department_id)
           REFERENCES departments(department_id)
           ON UPDATE CASCADE
           ON DELETE RESTRICT,
    CONSTRAINT fk_timetable_course
       FOREIGN KEY (course_id)
           REFERENCES courses(course_id)
           ON UPDATE CASCADE
           ON DELETE CASCADE,
    CONSTRAINT fk_timetable_lecturer
       FOREIGN KEY (lecturer_id)
           REFERENCES lecturers(user_id)
           ON UPDATE CASCADE
           ON DELETE SET NULL,
    CHECK (end_time > start_time),
    INDEX idx_timetable_batch_day (batch_year, day_of_week, start_time),
    INDEX idx_timetable_venue_day (venue, day_of_week, start_time)
);

-- 2.18 Grade Scale Table (UGC Circular 12-2024)
CREATE TABLE grade_scales (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    grade_code VARCHAR(5) NOT NULL UNIQUE,
    minimum_mark DECIMAL(5,2) NOT NULL,
    grade_point DECIMAL(4,2) NOT NULL,
    description VARCHAR(100),
    CHECK (minimum_mark >= 0 AND minimum_mark <= 100),
    CHECK (grade_point >= 0 AND grade_point <= 4)
);

-- 2.19 System Settings Table
CREATE TABLE system_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value VARCHAR(500) NOT NULL,
    description VARCHAR(500)
);

-- Re-enable Foreign Key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 3. INDEXES FOR PERFORMANCE OPTIMIZATION
-- ----------------------------------------------------------------------------
CREATE INDEX idx_users_role_lookup ON users(user_status, department_id);
CREATE INDEX idx_student_batch ON undergraduates(batch_year, student_type);
CREATE INDEX idx_courses_department ON courses(department_id, academic_year, semester);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_assessments_course_category ON assessments(course_id, assessment_category);
CREATE INDEX idx_marks_student ON marks(student_id);
CREATE INDEX idx_medicals_student_status ON medicals(student_id, approval_status);
CREATE INDEX idx_notices_target ON notices(target_role, status, published_at);

-- ----------------------------------------------------------------------------
-- 4. DATABASE VIEWS (FOR JAVA BACKEND & GUI AGGREGATION)
-- ----------------------------------------------------------------------------

-- V1. Full user list with role resolution
CREATE OR REPLACE VIEW v_users AS
SELECT
    u.user_id,
    u.username,
    u.full_name,
    u.nic,
    u.email,
    u.contact_no,
    u.address,
    u.profile_picture,
    u.user_status,
    d.department_code,
    d.department_name,
    CASE
        WHEN a.user_id IS NOT NULL THEN 'Admin'
        WHEN l.user_id IS NOT NULL THEN 'Lecturer'
        WHEN t.user_id IS NOT NULL THEN 'Technical Officer'
        WHEN s.user_id IS NOT NULL THEN 'Undergraduate'
        ELSE 'Unknown'
        END AS role,
    a.admin_code,
    l.lecturer_code,
    t.technical_officer_code,
    s.registration_no,
    s.batch_year,
    s.student_type
FROM users u
         LEFT JOIN departments d ON u.department_id = d.department_id
         LEFT JOIN admins a ON u.user_id = a.user_id
         LEFT JOIN lecturers l ON u.user_id = l.user_id
         LEFT JOIN technical_officers t ON u.user_id = t.user_id
         LEFT JOIN undergraduates s ON u.user_id = s.user_id;

-- V2. Combined Attendance Summary (Raw % & Adjusted % with Approved Medicals)
CREATE OR REPLACE VIEW v_attendance_summary AS
SELECT
    e.student_id,
    u.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    COUNT(ar.attendance_record_id) AS total_sessions,
    SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended_sessions,
    SUM(CASE WHEN ar.attendance_status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_sessions,
    SUM(CASE WHEN ar.attendance_status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_sessions,
    SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) * 100.0
        / NULLIF(COUNT(ar.attendance_record_id), 0) AS raw_attendance_percent,
    (
        SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)
            +
        SUM(CASE WHEN ar.attendance_status = 'MEDICAL'
            AND EXISTS (
                SELECT 1 FROM medicals m
                WHERE m.attendance_record_id = ar.attendance_record_id
                  AND m.approval_status = 'Approved'
            ) THEN 1 ELSE 0 END)
        ) * 100.0 / NULLIF(COUNT(ar.attendance_record_id), 0) AS adjusted_attendance_percent
FROM enrollments e
         JOIN undergraduates u ON e.student_id = u.user_id
         JOIN users usr ON u.user_id = usr.user_id
         JOIN courses c ON e.course_id = c.course_id
         LEFT JOIN attendance_sessions ats ON ats.course_id = c.course_id
         LEFT JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id AND ar.student_id = e.student_id
GROUP BY e.student_id, u.registration_no, usr.full_name, e.course_id, c.course_code, c.course_name;

-- V3. Attendance Summary broken down by Component (Theory vs Practical)
CREATE OR REPLACE VIEW v_attendance_component_summary AS
SELECT
    ar.student_id,
    ug.registration_no,
    usr.full_name,
    ats.course_id,
    c.course_code,
    c.course_name,
    ats.component,
    COUNT(*) AS total_sessions,
    SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended_sessions,
    SUM(CASE WHEN ar.attendance_status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_sessions,
    SUM(CASE WHEN ar.attendance_status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_sessions,
    ROUND(SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*),0), 2) AS raw_attendance_percent,
    ROUND(
            (
                SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)
                    +
                SUM(CASE WHEN ar.attendance_status = 'MEDICAL' AND EXISTS (
                    SELECT 1 FROM medicals m WHERE m.attendance_record_id = ar.attendance_record_id AND m.approval_status = 'Approved'
                ) THEN 1 ELSE 0 END)
                ) * 100.0 / NULLIF(COUNT(*),0), 2
    ) AS adjusted_attendance_percent
FROM attendance_records ar
         JOIN attendance_sessions ats ON ar.attendance_session_id = ats.attendance_session_id
         JOIN courses c ON ats.course_id = c.course_id
         JOIN undergraduates ug ON ar.student_id = ug.user_id
         JOIN users usr ON ug.user_id = usr.user_id
GROUP BY ar.student_id, ug.registration_no, usr.full_name, ats.course_id, c.course_code, c.course_name, ats.component;

-- V4. Continuous Assessment (CA) Marks Summary
CREATE OR REPLACE VIEW v_ca_summary AS
SELECT
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    c.ca_weight,
    ROUND(
            COALESCE(SUM(CASE WHEN a.assessment_category = 'CA' THEN m.mark * a.weight_percent / 100 ELSE 0 END), 0),
            2
    ) AS ca_mark,
    CASE
        WHEN COALESCE(SUM(CASE WHEN a.assessment_category = 'CA' THEN m.mark * a.weight_percent / 100 ELSE 0 END), 0) >= 40 THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS ca_eligibility
FROM enrollments e
         JOIN undergraduates ug ON e.student_id = ug.user_id
         JOIN users usr ON ug.user_id = usr.user_id
         JOIN courses c ON e.course_id = c.course_id
         LEFT JOIN assessments a ON a.course_id = c.course_id AND a.assessment_category = 'CA'
         LEFT JOIN marks m ON m.assessment_id = a.assessment_id AND m.student_id = e.student_id
GROUP BY e.student_id, ug.registration_no, usr.full_name, e.course_id, c.course_code, c.course_name, c.ca_weight;

-- V5. Final Examination Contribution Marks Summary
CREATE OR REPLACE VIEW v_final_exam_marks AS
SELECT
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    ROUND(
            COALESCE(SUM(CASE WHEN a.assessment_category = 'FINAL' THEN m.mark * a.weight_percent / 100 ELSE 0 END), 0),
            2
    ) AS final_exam_contribution
FROM enrollments e
         JOIN undergraduates ug ON e.student_id = ug.user_id
         JOIN users usr ON ug.user_id = usr.user_id
         JOIN courses c ON e.course_id = c.course_id
         LEFT JOIN assessments a ON a.course_id = c.course_id AND a.assessment_category = 'FINAL'
         LEFT JOIN marks m ON m.assessment_id = a.assessment_id AND m.student_id = e.student_id
GROUP BY e.student_id, ug.registration_no, usr.full_name, e.course_id, c.course_code, c.course_name;

-- V6. Full Examination Eligibility View (Attendance >= 80% AND CA >= 40%)
CREATE OR REPLACE VIEW v_eligibility AS
SELECT
    ca.student_id,
    ca.registration_no,
    ca.full_name,
    ca.course_id,
    ca.course_code,
    ca.course_name,
    ROUND(COALESCE(att.adjusted_attendance_percent, 0), 2) AS attendance_percent,
    ROUND(ca.ca_mark, 2) AS ca_mark,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80 AND ca.ca_mark >= 40 THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS final_exam_eligibility,
    CASE WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80 THEN 'Pass Attendance Requirement' ELSE 'Fail Attendance Requirement' END AS attendance_requirement,
    CASE WHEN ca.ca_mark >= 40 THEN 'Pass CA Requirement' ELSE 'Fail CA Requirement' END AS ca_requirement
FROM v_ca_summary ca
         LEFT JOIN v_attendance_summary att ON att.student_id = ca.student_id AND att.course_id = ca.course_id;

-- V7. Final Course Results, Grades & Grade Points
CREATE OR REPLACE VIEW v_final_results AS
SELECT
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    c.total_credits,
    ROUND(COALESCE(ca.ca_mark, 0), 2) AS ca_mark,
    ROUND(COALESCE(fe.final_exam_contribution, 0), 2) AS final_exam_mark,
    ROUND(COALESCE(ca.ca_mark, 0) + COALESCE(fe.final_exam_contribution, 0), 2) AS final_mark,
    COALESCE(att.adjusted_attendance_percent, 0) AS attendance_percent,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80 AND COALESCE(ca.ca_mark, 0) >= 40 THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS eligibility,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) < 80 OR COALESCE(ca.ca_mark, 0) < 40 THEN 'E*'
        ELSE (
            SELECT gs.grade_code
            FROM grade_scales gs
            WHERE gs.minimum_mark <= ROUND(COALESCE(ca.ca_mark, 0) + COALESCE(fe.final_exam_contribution, 0), 2)
            ORDER BY gs.minimum_mark DESC LIMIT 1
        )
        END AS grade,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) < 80 OR COALESCE(ca.ca_mark, 0) < 40 THEN 0.00
        ELSE (
            SELECT gs.grade_point
            FROM grade_scales gs
            WHERE gs.minimum_mark <= ROUND(COALESCE(ca.ca_mark, 0) + COALESCE(fe.final_exam_contribution, 0), 2)
            ORDER BY gs.minimum_mark DESC LIMIT 1
        )
        END AS grade_point
FROM enrollments e
         JOIN undergraduates ug ON e.student_id = ug.user_id
         JOIN users usr ON ug.user_id = usr.user_id
         JOIN courses c ON e.course_id = c.course_id
         LEFT JOIN v_ca_summary ca ON ca.student_id = e.student_id AND ca.course_id = e.course_id
         LEFT JOIN v_final_exam_marks fe ON fe.student_id = e.student_id AND fe.course_id = e.course_id
         LEFT JOIN v_attendance_summary att ON att.student_id = e.student_id AND att.course_id = e.course_id;

-- V8. Semester GPA (SGPA) View
CREATE OR REPLACE VIEW v_sgpa AS
SELECT
    student_id,
    registration_no,
    full_name,
    ROUND(SUM(grade_point * total_credits) / NULLIF(SUM(total_credits), 0), 2) AS sgpa,
    SUM(total_credits) AS semester_credits,
    SUM(grade_point * total_credits) AS semester_grade_points
FROM v_final_results
GROUP BY student_id, registration_no, full_name;

-- V9. Cumulative GPA (CGPA) View
CREATE OR REPLACE VIEW v_cgpa AS
SELECT
    s.student_id,
    s.registration_no,
    s.full_name,
    u.previous_credits,
    ROUND(u.previous_grade_points, 2) AS previous_grade_points,
    ROUND(s.sgpa, 2) AS current_sgpa,
    s.semester_credits,
    ROUND(u.previous_grade_points + s.semester_grade_points, 2) AS total_grade_points,
    ROUND((u.previous_grade_points + s.semester_grade_points) / NULLIF(u.previous_credits + s.semester_credits, 0), 2) AS cgpa
FROM v_sgpa s
         JOIN undergraduates u ON s.student_id = u.user_id;

-- V10. Batch Overall Result Summary View
CREATE OR REPLACE VIEW v_batch_results AS
SELECT
    u.batch_year,
    r.course_code,
    r.course_name,
    COUNT(*) AS student_count,
    ROUND(AVG(r.final_mark), 2) AS average_mark,
    SUM(CASE WHEN r.grade_point > 0 THEN 1 ELSE 0 END) AS passed_count,
    SUM(CASE WHEN r.grade_point = 0 THEN 1 ELSE 0 END) AS failed_count
FROM v_final_results r
         JOIN undergraduates u ON r.student_id = u.user_id
GROUP BY u.batch_year, r.course_code, r.course_name;

-- ----------------------------------------------------------------------------
-- 5. INITIAL SEED DATA
-- ----------------------------------------------------------------------------

-- 5.1 Department
INSERT INTO departments (department_code, department_name, description) VALUES
    ('ICT', 'Department of Information and Communication Technology', 'Faculty of Technology - ICT Department');

-- 5.2 UGC Circular 12-2024 Grade Scale
INSERT INTO grade_scales (grade_code, minimum_mark, grade_point, description) VALUES
    ('A+', 85.00, 4.00, 'Excellent'),
    ('A',  70.00, 4.00, 'Excellent'),
    ('A-', 65.00, 3.70, 'Very Good'),
    ('B+', 60.00, 3.30, 'Good'),
    ('B',  55.00, 3.00, 'Good'),
    ('B-', 50.00, 2.70, 'Good'),
    ('C+', 45.00, 2.30, 'Satisfactory'),
    ('C',  40.00, 2.00, 'Satisfactory'),
    ('C-', 35.00, 1.70, 'Pass'),
    ('D+', 30.00, 1.30, 'Weak Pass'),
    ('D',  25.00, 1.00, 'Weak Pass'),
    ('E',   0.00, 0.00, 'Fail');

-- 5.3 System Settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
    ('UNIVERSITY_NAME', 'University of Ruhuna', 'University name'),
    ('FACULTY_NAME', 'Faculty of Technology', 'Faculty name'),
    ('ACADEMIC_YEAR', '2026', 'Current academic year'),
    ('SEMESTER', 'Semester I', 'Current semester'),
    ('MIN_ATTENDANCE_PERCENT', '80', 'Minimum attendance percentage required'),
    ('MIN_CA_PERCENT', '40', 'Minimum CA percentage required for final examination eligibility'),
    ('TOTAL_THEORY_SESSIONS', '15', 'Expected theory sessions per course'),
    ('TOTAL_PRACTICAL_SESSIONS', '15', 'Expected practical sessions per course');

-- 5.4 Users Seed (1 Admin, 5 Lecturers, 4 Technical Officers, 20 Undergraduates)

-- Admin (Password: adm@123)
INSERT INTO users (username, password_hash, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
    ('admin', 'adm@123', 'System Admin', '198512345678', '1985-05-12', 'Male', 'admin@fot.ruh.ac.lk', '0412222222', 'Faculty of Technology, University of Ruhuna', 1);
INSERT INTO admins (user_id, admin_code, designation) VALUES (LAST_INSERT_ID(), 'ADM001', 'System Administrator');

-- 5 Lecturers (Password: lec@123)
INSERT INTO users (username, password_hash, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
    ('lecturer1', 'lec@123', 'Dr. N. Perera',       '197823456789', '1978-03-15', 'Male',   'lecturer1@fot.ruh.ac.lk', '0711000001', 'Department of ICT, Faculty of Technology', 1),
    ('lecturer2', 'lec@123', 'Ms. S. Fernando',     '198234567890', '1982-08-22', 'Female', 'lecturer2@fot.ruh.ac.lk', '0711000002', 'Department of ICT, Faculty of Technology', 1),
    ('lecturer3', 'lec@123', 'Mr. A. Gunawardena',  '198045678901', '1980-11-05', 'Male',   'lecturer3@fot.ruh.ac.lk', '0711000003', 'Department of ICT, Faculty of Technology', 1),
    ('lecturer4', 'lec@123', 'Dr. R. Jayasuriya',   '197556789012', '1975-01-30', 'Male',   'lecturer4@fot.ruh.ac.lk', '0711000004', 'Department of ICT, Faculty of Technology', 1),
    ('lecturer5', 'lec@123', 'Ms. T. Karunaratne',  '198867890123', '1988-06-18', 'Female', 'lecturer5@fot.ruh.ac.lk', '0711000005', 'Department of ICT, Faculty of Technology', 1);

INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC001', 'Dr.', 'Data Structures and Algorithms' FROM users WHERE username = 'lecturer1';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC002', 'Ms.', 'Database Systems' FROM users WHERE username = 'lecturer2';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC003', 'Mr.', 'Computer Networks' FROM users WHERE username = 'lecturer3';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC004', 'Dr.', 'Software Engineering' FROM users WHERE username = 'lecturer4';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC005', 'Ms.', 'Web and Application Development' FROM users WHERE username = 'lecturer5';

-- 4 Technical Officers (Password: to@123)
INSERT INTO users (username, password_hash, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
    ('to1', 'to@123', 'K. Silva',        '198678901234', '1986-04-10', 'Male', 'to1@fot.ruh.ac.lk', '0712000001', 'Department of ICT, Faculty of Technology', 1),
    ('to2', 'to@123', 'M. Wijeratne',   '198989012345', '1989-09-25', 'Male', 'to2@fot.ruh.ac.lk', '0712000002', 'Department of ICT, Faculty of Technology', 1),
    ('to3', 'to@123', 'D. Samarasinghe','199190123456', '1991-12-03', 'Male', 'to3@fot.ruh.ac.lk', '0712000003', 'Department of ICT, Faculty of Technology', 1),
    ('to4', 'to@123', 'P. Nandasena',   '198701234567', '1987-07-14', 'Male', 'to4@fot.ruh.ac.lk', '0712000004', 'Department of ICT, Faculty of Technology', 1);

INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO001', 'Technical Officer' FROM users WHERE username = 'to1';
INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO002', 'Technical Officer' FROM users WHERE username = 'to2';
INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO003', 'Technical Officer' FROM users WHERE username = 'to3';
INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO004', 'Technical Officer' FROM users WHERE username = 'to4';

-- 20 Undergraduates (Usernames: tg2061 .. tg2080, Passwords: ug@123, Reg Nos: TG/2024/2061 .. TG/2024/2080)
INSERT INTO users (username, password_hash, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
    ('tg2061', 'ug@123', 'Kasun Madushan',       '200210002061', '2002-01-10', 'Male',   'tg2061@fot.ruh.ac.lk', '0713002061', 'Matara, Sri Lanka', 1),
    ('tg2062', 'ug@123', 'Nimali Perera',        '200210002062', '2002-02-15', 'Female', 'tg2062@fot.ruh.ac.lk', '0713002062', 'Galle, Sri Lanka', 1),
    ('tg2063', 'ug@123', 'Ravindu Silva',        '200210002063', '2002-03-20', 'Male',   'tg2063@fot.ruh.ac.lk', '0713002063', 'Hambantota, Sri Lanka', 1),
    ('tg2064', 'ug@123', 'Dilini Jayawardena',   '200210002064', '2002-04-25', 'Female', 'tg2064@fot.ruh.ac.lk', '0713002064', 'Colombo, Sri Lanka', 1),
    ('tg2065', 'ug@123', 'Sahan Wickramasinghe', '200210002065', '2002-05-12', 'Male',   'tg2065@fot.ruh.ac.lk', '0713002065', 'Kandy, Sri Lanka', 1),
    ('tg2066', 'ug@123', 'Ishara Gunasekara',    '200210002066', '2002-06-18', 'Female', 'tg2066@fot.ruh.ac.lk', '0713002066', 'Matara, Sri Lanka', 1),
    ('tg2067', 'ug@123', 'Tharindu Fernando',    '200210002067', '2002-07-22', 'Male',   'tg2067@fot.ruh.ac.lk', '0713002067', 'Galle, Sri Lanka', 1),
    ('tg2068', 'ug@123', 'Amaya Rathnayake',     '200210002068', '2002-08-05', 'Female', 'tg2068@fot.ruh.ac.lk', '0713002068', 'Kandy, Sri Lanka', 1),
    ('tg2069', 'ug@123', 'Chamod Dissanayake',   '200210002069', '2002-09-14', 'Male',   'tg2069@fot.ruh.ac.lk', '0713002069', 'Matara, Sri Lanka', 1),
    ('tg2070', 'ug@123', 'Hasini Ekanayake',     '200210002070', '2002-10-28', 'Female', 'tg2070@fot.ruh.ac.lk', '0713002070', 'Galle, Sri Lanka', 1),
    ('tg2071', 'ug@123', 'Pasindu Bandara',      '200210002071', '2002-11-03', 'Male',   'tg2071@fot.ruh.ac.lk', '0713002071', 'Kandy, Sri Lanka', 1),
    ('tg2072', 'ug@123', 'Sanduni Herath',       '200210002072', '2002-12-19', 'Female', 'tg2072@fot.ruh.ac.lk', '0713002072', 'Matara, Sri Lanka', 1),
    ('tg2073', 'ug@123', 'Nuwan Abeysekara',     '200210002073', '2002-01-31', 'Male',   'tg2073@fot.ruh.ac.lk', '0713002073', 'Galle, Sri Lanka', 1),
    ('tg2074', 'ug@123', 'Piumi Senanayake',     '200210002074', '2002-02-24', 'Female', 'tg2074@fot.ruh.ac.lk', '0713002074', 'Colombo, Sri Lanka', 1),
    ('tg2075', 'ug@123', 'Lahiru Weerasinghe',   '200210002075', '2002-03-11', 'Male',   'tg2075@fot.ruh.ac.lk', '0713002075', 'Kandy, Sri Lanka', 1),
    ('tg2076', 'ug@123', 'Thilini Kumari',       '200210002076', '2002-04-08', 'Female', 'tg2076@fot.ruh.ac.lk', '0713002076', 'Matara, Sri Lanka', 1),
    ('tg2077', 'ug@123', 'Janith Rajapaksha',    '200210002077', '2002-05-17', 'Male',   'tg2077@fot.ruh.ac.lk', '0713002077', 'Galle, Sri Lanka', 1),
    ('tg2078', 'ug@123', 'Oshadi Wijesinghe',    '200110002078', '2001-06-25', 'Female', 'tg2078@fot.ruh.ac.lk', '0713002078', 'Hambantota, Sri Lanka', 1),
    ('tg2079', 'ug@123', 'Chathura Peiris',      '200110002079', '2001-07-30', 'Male',   'tg2079@fot.ruh.ac.lk', '0713002079', 'Colombo, Sri Lanka', 1),
    ('tg2080', 'ug@123', 'Menaka Liyanage',      '200010002080', '2000-08-14', 'Female', 'tg2080@fot.ruh.ac.lk', '0713002080', 'Kandy, Sri Lanka', 1);

INSERT INTO undergraduates (user_id, registration_no, index_no, batch_year, academic_year, student_type, intake, current_level, current_semester, previous_credits, previous_grade_points, admission_date)
SELECT
    user_id,
    CONCAT('TG/2024/', 2060 + ROW_NUMBER() OVER (ORDER BY user_id)),
    CONCAT('ICT/24/', 2060 + ROW_NUMBER() OVER (ORDER BY user_id)),
    2024, '2026', 'Proper', '2024', 2, 1, 15.00, 45.00, '2024-09-01'
FROM users WHERE username LIKE 'tg20%';

-- Update student types for repeat and batch-missed scenarios
UPDATE undergraduates SET student_type = 'Repeat' WHERE registration_no IN ('TG/2024/2078', 'TG/2024/2079');
UPDATE undergraduates SET student_type = 'Batch Missed', batch_year = 2023 WHERE registration_no = 'TG/2024/2080';

-- 5.5 Courses Seed (ICT2112 to ICT2182)
INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, ca_weight, department_id, academic_year, semester) VALUES
    ('ICT2112', 'Data Structures and Algorithms',        'Data structures, algorithms and problem solving.',            2.0, 0.0, 40.0, 1, '2026', 'Semester I'),
    ('ICT2122', 'Computer Networks',                     'Computer networking concepts and practical networking.',      1.0, 1.0, 40.0, 1, '2026', 'Semester I'),
    ('ICT2132', 'Object Oriented Programming Practicum', 'Java, OOP, GUI and database handling practicum.',         1.0, 1.0, 50.0, 1, '2026', 'Semester I'),
    ('ICT2142', 'Database Management Systems',           'Database design, SQL and database management.',              2.0, 1.0, 40.0, 1, '2026', 'Semester I'),
    ('ICT2152', 'Web Application Development',           'Client and server side web application development.',         1.0, 1.0, 50.0, 1, '2026', 'Semester I'),
    ('ICT2162', 'Software Engineering',                  'Software engineering principles and development methods.',    2.0, 0.0, 30.0, 1, '2026', 'Semester I'),
    ('ICT2172', 'Probability and Statistics',            'Probability, statistics and data analysis.',                  2.0, 0.0, 30.0, 1, '2026', 'Semester I'),
    ('ICT2182', 'Technical Communication Skills',        'Professional and technical communication.',                   1.0, 0.0, 50.0, 1, '2026', 'Semester I');

-- 5.6 Lecturer Course Assignments
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'), 'Coordinator' FROM courses WHERE course_code = 'ICT2112';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC003'), 'Coordinator' FROM courses WHERE course_code = 'ICT2122';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'), 'Coordinator' FROM courses WHERE course_code = 'ICT2132';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC002'), 'Coordinator' FROM courses WHERE course_code = 'ICT2142';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC005'), 'Coordinator' FROM courses WHERE course_code = 'ICT2152';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC004'), 'Coordinator' FROM courses WHERE course_code = 'ICT2162';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC004'), 'Coordinator' FROM courses WHERE course_code = 'ICT2172';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC005'), 'Coordinator' FROM courses WHERE course_code = 'ICT2182';

-- 5.7 Course Materials Seed
INSERT INTO course_materials (course_id, lecturer_id, title, description, material_type, file_path)
SELECT c.course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'), 'OOP Practicum - Week 06 Notes', 'Classes, inheritance, abstraction and exception handling.', 'PDF', 'materials/ICT2132_Week06.pdf'
FROM courses c WHERE c.course_code = 'ICT2132';

INSERT INTO course_materials (course_id, lecturer_id, title, description, material_type, file_path)
SELECT c.course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC002'), 'Database Normalization Guide', '1NF, 2NF, 3NF normalization examples and SQL practice.', 'PDF', 'materials/ICT2142_Normalization.pdf'
FROM courses c WHERE c.course_code = 'ICT2142';

-- 5.8 Enrollments Seed
INSERT INTO enrollments (student_id, course_id, academic_year, semester, enrollment_type)
SELECT
    u.user_id, c.course_id, '2026', 'Semester I',
    CASE
        WHEN u.student_type = 'Repeat' THEN 'Repeat'
        WHEN u.student_type = 'Batch Missed' THEN 'Batch Missed'
        ELSE 'Normal'
        END
FROM undergraduates u
         CROSS JOIN courses c
WHERE NOT (
    (u.student_type = 'Batch Missed' AND MOD(c.course_id, 3) = 0) OR
    (u.student_type = 'Repeat' AND c.course_code = 'ICT2182')
    );

-- 5.9 Assessments Seed
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA', 'Assignment', 'Theory', ca_weight, 100.00 FROM courses;

INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL', 'Final Examination', 'Theory', final_weight, 100.00 FROM courses;

-- 5.10 Attendance Sessions Generation (15 Theory and 15 Practical)
INSERT INTO attendance_sessions (course_id, component, session_no, session_date, start_time, end_time, duration_hours, conducted_by, topic)
SELECT
    c.course_id, 'Theory', n.n,
    DATE_ADD('2026-09-01', INTERVAL (n.n - 1) * 7 DAY),
    '08:00:00', '10:00:00', 2.00,
    (SELECT cl.lecturer_id FROM course_lecturers cl WHERE cl.course_id = c.course_id LIMIT 1),
    CONCAT('Theory Session ', n.n)
FROM courses c
         CROSS JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
    UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
) n
WHERE c.theory_credits > 0;

INSERT INTO attendance_sessions (course_id, component, session_no, session_date, start_time, end_time, duration_hours, conducted_by, topic)
SELECT
    c.course_id, 'Practical', n.n,
    DATE_ADD('2026-09-03', INTERVAL (n.n - 1) * 7 DAY),
    '13:00:00', '15:00:00', 2.00,
    (SELECT cl.lecturer_id FROM course_lecturers cl WHERE cl.course_id = c.course_id LIMIT 1),
    CONCAT('Practical Session ', n.n)
FROM courses c
         CROSS JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
    UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
) n
WHERE c.practical_credits > 0;

-- 5.11 Demo Attendance Records
INSERT INTO attendance_records (attendance_session_id, student_id, attendance_status, recorded_by)
SELECT
    ats.attendance_session_id,
    e.student_id,
    CASE
        WHEN MOD(ROW_NUMBER() OVER (PARTITION BY e.student_id, ats.course_id ORDER BY ats.attendance_session_id), 10) <= 8 THEN 'PRESENT'
        ELSE 'ABSENT'
        END,
    (SELECT user_id FROM technical_officers LIMIT 1)
FROM attendance_sessions ats
         JOIN enrollments e ON e.course_id = ats.course_id;

-- 5.12 Demo Medical Records
INSERT INTO medicals (student_id, course_id, attendance_record_id, medical_date, reason, approval_status, reviewed_by, reviewed_at, review_comment)
SELECT
    e.student_id, e.course_id, ar.attendance_record_id, ats.session_date,
    'Medical leave - certified illness', 'Approved',
    (SELECT user_id FROM technical_officers LIMIT 1), NOW(), 'Medical document verified'
FROM enrollments e
         JOIN attendance_sessions ats ON ats.course_id = e.course_id
         JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id AND ar.student_id = e.student_id
WHERE ar.attendance_status = 'ABSENT' AND MOD(e.student_id, 5) = 0
LIMIT 10;

-- Update medical attendance status in records (Fixed with safe WHERE clause)
UPDATE attendance_records ar
    JOIN medicals m ON m.attendance_record_id = ar.attendance_record_id
SET ar.attendance_status = 'MEDICAL'
WHERE ar.attendance_record_id > 0 AND m.approval_status = 'Approved';

-- 5.13 Demo Marks
INSERT INTO marks (assessment_id, student_id, mark, entered_by)
SELECT
    a.assessment_id,
    e.student_id,
    CASE
        WHEN MOD(e.student_id, 5) = 0 THEN 88.00
        WHEN MOD(e.student_id, 5) = 1 THEN 78.00
        WHEN MOD(e.student_id, 5) = 2 THEN 68.00
        WHEN MOD(e.student_id, 5) = 3 THEN 52.00
        ELSE 35.00
        END,
    (SELECT cl.lecturer_id FROM course_lecturers cl WHERE cl.course_id = a.course_id LIMIT 1)
FROM assessments a
         JOIN enrollments e ON e.course_id = a.course_id;

-- 5.14 Demo Notices
INSERT INTO notices (title, content, notice_type, target_role, department_id, published_by, status) VALUES
                                                                                                        ('Semester I Academic Notice', 'Students are requested to regularly check attendance, course and examination information.', 'Academic', 'All', 1, (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), 'Published'),
                                                                                                        ('Attendance Eligibility Reminder', 'Students must maintain at least 80% attendance according to academic regulations.', 'Attendance', 'Undergraduate', 1, (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), 'Published'),
                                                                                                        ('CA Eligibility Notice', 'Continuous Assessment marks must satisfy the 40% minimum eligibility requirement before the final examination.', 'Exam', 'Undergraduate', 1, (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), 'Published');

-- 5.15 Demo Timetable Schedule
INSERT INTO timetable_entries (department_id, course_id, lecturer_id, batch_year, component, day_of_week, start_time, end_time, venue, academic_year, semester)
SELECT
    1, c.course_id, cl.lecturer_id, 2024, 'Theory',
    CASE MOD(c.course_id, 5)
        WHEN 0 THEN 'Monday'
        WHEN 1 THEN 'Tuesday'
        WHEN 2 THEN 'Wednesday'
        WHEN 3 THEN 'Thursday'
        ELSE 'Friday'
        END,
    '08:00:00', '10:00:00', CONCAT('Lecture Hall ', c.course_id), '2026', 'Semester I'
FROM courses c
         JOIN course_lecturers cl ON cl.course_id = c.course_id AND cl.responsibility = 'Coordinator';

-- Reset safe updates setting
SET SQL_SAFE_UPDATES = 1;

-- ============================================================================
-- END OF UNIMIS DATABASE SCRIPT
-- ============================================================================