-- ============================================================================
-- UNIMIS - Faculty Academic Management System (MySQL Database Schema & Data)
-- ============================================================================
-- Level & Semester: Level 2 Semester 1
-- Courses: ICT2113, ICT2122, ICT2132, ICT2142, ICT2152, ICT2162, TCS2112, TCS2121, ENG2112
-- Degree: Bachelor of Information and Communication Technology (BICT)
-- Faculty: Faculty of Technology, University of Ruhuna
-- Database: unimis
-- Compatibility: MySQL 8.0+
--
-- FIX NOTE (CA eligibility):
--   ca_mark is expressed on the scale of each course's ca_weight (e.g. out of
--   30, 40 or 50). CA eligibility is therefore:
--        ca_mark >= 40% of ca_weight   ->  'Eligible'
--        otherwise                     ->  'Not Eligible'
--   (Previously ca_mark was compared against a flat 40, which was wrong.)
--   Changed in: v_ca_summary (4.4), v_eligibility (4.6), v_final_results (4.7),
--   system_settings.MIN_CA_PERCENT description, and the CA Eligibility notice.
-- ============================================================================

-- ============================================================================
-- UPDATE NOTE (enrollment requests + hashed seed passwords):
--   NEW      2.10a  table  enrollment_requests    students request a course, admin accepts / declines
--   NEW      INDEX  idx_enrollment_requests_status
--   NEW      4.10   view   v_enrollment_requests  admin / student friendly listing of requests
--   NEW      5.16   seed   5 sample enrollment requests (3 Pending, 1 Declined, 1 Approved)
--   CHANGED  5.4    seed   user passwords are stored as SHA-256 hashes (matches AuthService)
--   All other tables, views and seed rows are unchanged.
-- ============================================================================

-- ============================================================================
-- TEST DATA UPDATE NOTE (attendance / medical / timetable / failing-result scenarios):
--   NEW      4.11   view   v_attendance_by_component  attendance split into Theory / Practical / Combined
--   CHANGED  5.15   seed   timetable rewritten as explicit, clash-free weekly slots: 8 Theory + 2 Practical
--                          (ICT2132 is practical-only; ICT2113 has one theory and one practical slot)
--   NEW      5.17   seed   test scenarios on 28 student / course pairs (legend at the top of 5.17):
--                          attendance exactly 80%, below 80% with and without medicals, Pending and
--                          Rejected medicals, CA exactly 40% / just below 40% for CA weights 30, 40 and 50,
--                          Not Eligible (E*) results, and the grades B+, C, C-, D+, D and E
--   Schema, all other views and all other seed rows are unchanged. The students tg2065, tg2070,
--   tg2075 and tg2080 and their original 10 approved medicals are not touched.
-- ============================================================================

DROP DATABASE IF EXISTS unimis;
CREATE DATABASE unimis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE unimis;

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- ----------------------------------------------------------------------------
-- TABLE CREATIONS
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

-- 2.2 User Superclass Table with Physical Role Column
CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('Admin', 'Lecturer', 'Technical Officer', 'Undergraduate') NOT NULL,
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
                           FOREIGN KEY (department_id)
                               REFERENCES departments(department_id)
                               ON UPDATE CASCADE
                               ON DELETE SET NULL
);

-- 2.3 Admin Subclass Table
CREATE TABLE admins (
                        user_id BIGINT PRIMARY KEY,
                        admin_code VARCHAR(30) NOT NULL UNIQUE,
                        designation VARCHAR(100) DEFAULT 'System Administrator',
                        CONSTRAINT fk_admins_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(user_id)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE
);

-- 2.4 Lecturer Subclass Table
CREATE TABLE lecturers (
                           user_id BIGINT PRIMARY KEY,
                           lecturer_code VARCHAR(30) NOT NULL UNIQUE,
                           title VARCHAR(30) DEFAULT 'Dr.',
                           specialization VARCHAR(150),
                           CONSTRAINT fk_lecturers_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(user_id)
                                   ON UPDATE CASCADE
                                   ON DELETE CASCADE
);

-- 2.5 Technical Officer Subclass Table
CREATE TABLE technical_officers (
                                    user_id BIGINT PRIMARY KEY,
                                    technical_officer_code VARCHAR(30) NOT NULL UNIQUE,
                                    designation VARCHAR(100) DEFAULT 'Technical Officer',
                                    assigned_labs VARCHAR(200) DEFAULT 'ICT Laboratories',
                                    CONSTRAINT fk_technical_officers_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES users(user_id)
                                            ON UPDATE CASCADE
                                            ON DELETE CASCADE
);

-- 2.6 Undergraduate Subclass Table
CREATE TABLE undergraduates (
                                user_id BIGINT PRIMARY KEY,
                                registration_no VARCHAR(30) NOT NULL UNIQUE,
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
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(user_id)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE,
                                CONSTRAINT chk_undergraduates_previous
                                    CHECK (previous_credits >= 0 AND previous_grade_points >= 0)
);

-- 2.7 Courses Table (Updated with CA and End Exam Sub-weights)
CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         course_code VARCHAR(20) NOT NULL UNIQUE,
                         course_name VARCHAR(200) NOT NULL,
                         description TEXT,
                         theory_credits DECIMAL(3,1) NOT NULL DEFAULT 0.0,
                         practical_credits DECIMAL(3,1) NOT NULL DEFAULT 0.0,
                         total_credits DECIMAL(4,1) GENERATED ALWAYS AS (theory_credits + practical_credits) STORED,
                         is_gpa BOOLEAN NOT NULL DEFAULT TRUE,

    -- CA Sub-weights breakdown
                         quiz_weight DECIMAL(5,2) NOT NULL DEFAULT 10.00,          -- Max Quiz 2 Weight (Sum of best 2 quizzes weight)
                         assignment_weight DECIMAL(5,2) NOT NULL DEFAULT 10.00,    -- Assignment Weight
                         mid_theory_weight DECIMAL(5,2) NOT NULL DEFAULT 10.00,    -- Mid Exam Theory Weight
                         mid_practical_weight DECIMAL(5,2) NOT NULL DEFAULT 0.00,  -- Mid Exam Practical Weight
                         ca_weight DECIMAL(5,2) NOT NULL DEFAULT 30.00,            -- Total CA Weight

    -- End Exam Sub-weights breakdown
                         end_theory_weight DECIMAL(5,2) NOT NULL DEFAULT 70.00,    -- End Exam Theory Weight
                         end_practical_weight DECIMAL(5,2) NOT NULL DEFAULT 0.00,  -- End Exam Practical Weight
                         final_weight DECIMAL(5,2) GENERATED ALWAYS AS (100.00 - ca_weight) STORED,

                         department_id INT NOT NULL,
                         academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
                         semester ENUM('Semester I', 'Semester II') NOT NULL DEFAULT 'Semester I',
                         status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_courses_department
                             FOREIGN KEY (department_id)
                                 REFERENCES departments(department_id)
                                 ON UPDATE CASCADE
                                 ON DELETE RESTRICT,
                         CONSTRAINT chk_course_credits
                             CHECK (theory_credits >= 0 AND practical_credits >= 0 AND (theory_credits + practical_credits) > 0),
                         CONSTRAINT chk_course_weights
                             CHECK (ca_weight >= 0 AND ca_weight <= 100)
);

-- 2.8 Course Lecturers Mapping Table
CREATE TABLE course_lecturers (
                                  course_id INT NOT NULL,
                                  lecturer_id BIGINT NOT NULL,
                                  responsibility ENUM('Coordinator', 'Lecturer', 'Practical Instructor') NOT NULL DEFAULT 'Lecturer',
                                  assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (course_id, lecturer_id),
                                  CONSTRAINT fk_course_lecturers_course
                                      FOREIGN KEY (course_id)
                                          REFERENCES courses(course_id)
                                          ON UPDATE CASCADE
                                          ON DELETE CASCADE,
                                  CONSTRAINT fk_course_lecturers_lecturer
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
                             UNIQUE KEY uq_student_course_semester (student_id, course_id, academic_year, semester),
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

-- 2.10a Enrollment Requests Table (NEW)
-- A student asks to be enrolled in a course; an admin accepts or declines the request.
-- When a request is accepted the application inserts the row into enrollments.
CREATE TABLE enrollment_requests (
                                     request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     student_id BIGINT NOT NULL,
                                     course_id INT NOT NULL,
                                     academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
                                     semester ENUM('Semester I', 'Semester II') NOT NULL DEFAULT 'Semester I',
                                     enrollment_type ENUM('Normal', 'Repeat', 'Batch Missed') NOT NULL DEFAULT 'Normal',
                                     request_note VARCHAR(255) NULL,
                                     status ENUM('Pending', 'Approved', 'Declined') NOT NULL DEFAULT 'Pending',
                                     decision_note VARCHAR(255) NULL,
                                     requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     decided_by BIGINT NULL,
                                     decided_at DATETIME NULL,
    -- 1 while the request is Pending, NULL otherwise. Together with the UNIQUE key below
    -- this allows only ONE pending request per student / course / semester.
                                     pending_flag TINYINT GENERATED ALWAYS AS (CASE WHEN status = 'Pending' THEN 1 ELSE NULL END) STORED,
                                     UNIQUE KEY uq_one_pending_request (student_id, course_id, academic_year, semester, pending_flag),
                                     CONSTRAINT fk_enrollment_requests_student
                                         FOREIGN KEY (student_id)
                                             REFERENCES undergraduates(user_id)
                                             ON UPDATE CASCADE
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_enrollment_requests_course
                                         FOREIGN KEY (course_id)
                                             REFERENCES courses(course_id)
                                             ON UPDATE CASCADE
                                             ON DELETE CASCADE,
                                     CONSTRAINT fk_enrollment_requests_decider
                                         FOREIGN KEY (decided_by)
                                             REFERENCES users(user_id)
                                             ON UPDATE CASCADE
                                             ON DELETE SET NULL
);

-- 2.11 Assessment Definitions Table
CREATE TABLE assessments (
                             assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             course_id INT NOT NULL,
                             assessment_name VARCHAR(150) NOT NULL,
                             assessment_category ENUM('CA', 'FINAL') NOT NULL,
                             assessment_type ENUM('Quiz1', 'Quiz2', 'Quiz3', 'Assignment', 'Mid Exam Theory', 'Mid Exam Practical', 'End Exam Theory', 'End Exam Practical', 'Other') NOT NULL,
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
                             CONSTRAINT fk_assessments_created_by
                                 FOREIGN KEY (created_by)
                                     REFERENCES users(user_id)
                                     ON UPDATE CASCADE
                                     ON DELETE SET NULL,
                             CONSTRAINT chk_assessment_weight
                                 CHECK (weight_percent >= 0 AND weight_percent <= 100),
                             CONSTRAINT chk_assessment_max_mark
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
                       CONSTRAINT fk_marks_entered_by
                           FOREIGN KEY (entered_by)
                               REFERENCES users(user_id)
                               ON UPDATE CASCADE
                               ON DELETE SET NULL,
                       CONSTRAINT chk_marks_range
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
                                     UNIQUE KEY uq_course_component_session (course_id, component, session_no),
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
                                     CONSTRAINT chk_attendance_session_no
                                         CHECK (session_no BETWEEN 1 AND 15),
                                     CONSTRAINT chk_attendance_duration
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
                                    CONSTRAINT fk_attendance_record_user
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
                          CONSTRAINT fk_medical_student
                              FOREIGN KEY (student_id)
                                  REFERENCES undergraduates(user_id)
                                  ON UPDATE CASCADE
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_medical_course
                              FOREIGN KEY (course_id)
                                  REFERENCES courses(course_id)
                                  ON UPDATE CASCADE
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_medical_attendance
                              FOREIGN KEY (attendance_record_id)
                                  REFERENCES attendance_records(attendance_record_id)
                                  ON UPDATE CASCADE
                                  ON DELETE SET NULL,
                          CONSTRAINT fk_medical_reviewer
                              FOREIGN KEY (reviewed_by)
                                  REFERENCES users(user_id)
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
                                   CONSTRAINT chk_timetable_time
                                       CHECK (end_time > start_time),
                                   INDEX idx_timetable_batch_day (batch_year, day_of_week, start_time),
                                   INDEX idx_timetable_venue_day (venue, day_of_week, start_time)
);

-- 2.18 Grade Scale Table
CREATE TABLE grade_scales (
                              grade_id INT AUTO_INCREMENT PRIMARY KEY,
                              grade_code VARCHAR(5) NOT NULL UNIQUE,
                              minimum_mark DECIMAL(5,2) NOT NULL,
                              grade_point DECIMAL(4,2) NOT NULL,
                              description VARCHAR(100),
                              CONSTRAINT chk_grade_minimum
                                  CHECK (minimum_mark >= 0 AND minimum_mark <= 100),
                              CONSTRAINT chk_grade_point
                                  CHECK (grade_point >= 0 AND grade_point <= 4)
);

-- 2.19 System Settings Table
CREATE TABLE system_settings (
                                 setting_key VARCHAR(100) PRIMARY KEY,
                                 setting_value VARCHAR(500) NOT NULL,
                                 description VARCHAR(500)
);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- INDEXES
-- ----------------------------------------------------------------------------
CREATE INDEX idx_users_role_lookup ON users(role, user_status, department_id);
CREATE INDEX idx_student_batch ON undergraduates(batch_year, student_type);
CREATE INDEX idx_courses_department ON courses(department_id, academic_year, semester);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_assessments_course_category ON assessments(course_id, assessment_category);
CREATE INDEX idx_marks_student ON marks(student_id);
CREATE INDEX idx_medicals_student_status ON medicals(student_id, approval_status);
CREATE INDEX idx_notices_target ON notices(target_role, status, published_at);
CREATE INDEX idx_enrollment_requests_status ON enrollment_requests(status, requested_at);

-- ----------------------------------------------------------------------------
-- DATABASE VIEWS
-- ----------------------------------------------------------------------------

-- 4.1 User Information View
CREATE OR REPLACE VIEW v_users AS
SELECT
    u.user_id,
    u.username,
    u.password_hash,
    u.role,
    u.full_name,
    u.nic,
    u.date_of_birth,
    u.gender,
    u.email,
    u.contact_no,
    u.address,
    u.profile_picture,
    u.department_id,
    u.user_status,
    u.created_at,
    u.updated_at,
    d.department_code,
    d.department_name,
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

-- 4.2 Attendance Summary View
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
    SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(ar.attendance_record_id), 0) AS raw_attendance_percent,
    (
        SUM(CASE WHEN ar.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)
            +
        SUM(CASE WHEN ar.attendance_status = 'MEDICAL' AND EXISTS (
            SELECT 1 FROM medicals m WHERE m.attendance_record_id = ar.attendance_record_id AND m.approval_status = 'Approved'
        ) THEN 1 ELSE 0 END)
        ) * 100.0 / NULLIF(COUNT(ar.attendance_record_id), 0) AS adjusted_attendance_percent
FROM enrollments e
         JOIN undergraduates u ON e.student_id = u.user_id
         JOIN users usr ON u.user_id = usr.user_id
         JOIN courses c ON e.course_id = c.course_id
         LEFT JOIN attendance_sessions ats ON ats.course_id = c.course_id
         LEFT JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id AND ar.student_id = e.student_id
GROUP BY e.student_id, u.registration_no, usr.full_name, e.course_id, c.course_code, c.course_name;

-- 4.3 Best 2 out of 3 Quizzes Calculation View
CREATE OR REPLACE VIEW v_quiz_best2 AS
SELECT
    q.student_id,
    q.course_id,
    ROUND(SUM(q.mark * (q.quiz_weight / 2.0) / 100.0), 2) AS quiz_mark_contribution
FROM (
         SELECT
             m.student_id,
             a.course_id,
             m.mark,
             c.quiz_weight,
             ROW_NUMBER() OVER (PARTITION BY m.student_id, a.course_id ORDER BY m.mark DESC) AS rn
         FROM marks m
                  JOIN assessments a ON m.assessment_id = a.assessment_id
                  JOIN courses c ON a.course_id = c.course_id
         WHERE a.assessment_category = 'CA'
           AND (a.assessment_type IN ('Quiz1', 'Quiz2', 'Quiz3') OR a.assessment_name LIKE '%Quiz%')
     ) q
WHERE q.rn <= 2
GROUP BY q.student_id, q.course_id;

-- 4.4 Continuous Assessment (CA) Summary View
-- ca_mark is on the scale of the course's ca_weight (e.g. out of 30 / 40 / 50).
-- FIXED: ca_eligibility = 'Eligible' when ca_mark >= 40% of ca_weight, else 'Not Eligible'.
-- The ca_mark is calculated once in the inner query, so the value shown in the
-- ca_mark column is exactly the value used for the eligibility decision.
CREATE OR REPLACE VIEW v_ca_summary AS
SELECT
    t.student_id,
    t.registration_no,
    t.full_name,
    t.course_id,
    t.course_code,
    t.course_name,
    t.ca_weight,
    t.ca_mark,
    CASE
        WHEN t.ca_mark >= ROUND(t.ca_weight * 0.40, 2) THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS ca_eligibility
FROM (
         SELECT
             e.student_id,
             ug.registration_no,
             usr.full_name,
             e.course_id,
             c.course_code,
             c.course_name,
             c.ca_weight,
             ROUND(
                     COALESCE(qb.quiz_mark_contribution, 0) +
                     COALESCE(SUM(CASE WHEN a.assessment_type = 'Assignment' THEN m.mark * a.weight_percent / 100.0 ELSE 0 END), 0) +
                     COALESCE(SUM(CASE WHEN a.assessment_type = 'Mid Exam Theory' THEN m.mark * a.weight_percent / 100.0 ELSE 0 END), 0) +
                     COALESCE(SUM(CASE WHEN a.assessment_type = 'Mid Exam Practical' THEN m.mark * a.weight_percent / 100.0 ELSE 0 END), 0),
                     2
             ) AS ca_mark
         FROM enrollments e
                  JOIN undergraduates ug ON e.student_id = ug.user_id
                  JOIN users usr ON ug.user_id = usr.user_id
                  JOIN courses c ON e.course_id = c.course_id
                  LEFT JOIN v_quiz_best2 qb ON qb.student_id = e.student_id AND qb.course_id = e.course_id
                  LEFT JOIN assessments a ON a.course_id = c.course_id AND a.assessment_category = 'CA' AND a.assessment_type NOT IN ('Quiz1', 'Quiz2', 'Quiz3') AND a.assessment_name NOT LIKE '%Quiz%'
                  LEFT JOIN marks m ON m.assessment_id = a.assessment_id AND m.student_id = e.student_id
         GROUP BY e.student_id, ug.registration_no, usr.full_name, e.course_id, c.course_code, c.course_name, c.ca_weight, qb.quiz_mark_contribution
     ) t;

-- 4.5 Final Exam Marks Contribution View
CREATE OR REPLACE VIEW v_final_exam_marks AS
SELECT
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    ROUND(
            COALESCE(SUM(CASE WHEN a.assessment_category = 'FINAL' THEN m.mark * a.weight_percent / 100.0 ELSE 0 END), 0),
            2
    ) AS final_exam_contribution
FROM enrollments e
         JOIN undergraduates ug ON e.student_id = ug.user_id
         JOIN users usr ON ug.user_id = usr.user_id
         JOIN courses c ON e.course_id = c.course_id
         LEFT JOIN assessments a ON a.course_id = c.course_id AND a.assessment_category = 'FINAL'
         LEFT JOIN marks m ON m.assessment_id = a.assessment_id AND m.student_id = e.student_id
GROUP BY e.student_id, ug.registration_no, usr.full_name, e.course_id, c.course_code, c.course_name;

-- 4.6 Eligibility View (Attendance >= 80% AND CA Mark >= 40% of CA weight)
-- FIXED: CA requirement now uses v_ca_summary.ca_eligibility (ca_mark >= 40% of ca_weight)
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
        WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80 AND ca.ca_eligibility = 'Eligible' THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS final_exam_eligibility,
    CASE WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80 THEN 'Pass Attendance Requirement' ELSE 'Fail Attendance Requirement' END AS attendance_requirement,
    CASE WHEN ca.ca_eligibility = 'Eligible' THEN 'Pass CA Requirement' ELSE 'Fail CA Requirement' END AS ca_requirement
FROM v_ca_summary ca
         LEFT JOIN v_attendance_summary att ON att.student_id = ca.student_id AND att.course_id = ca.course_id;

-- 4.7 Final Course Results View
-- FIXED: eligibility / grade / grade_point use the CA rule (ca_mark >= 40% of ca_weight)
CREATE OR REPLACE VIEW v_final_results AS
SELECT
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    c.total_credits,
    c.is_gpa,
    ROUND(COALESCE(ca.ca_mark, 0), 2) AS ca_mark,
    ROUND(COALESCE(fe.final_exam_contribution, 0), 2) AS final_exam_mark,
    ROUND(COALESCE(ca.ca_mark, 0) + COALESCE(fe.final_exam_contribution, 0), 2) AS final_mark,
    COALESCE(att.adjusted_attendance_percent, 0) AS attendance_percent,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80
            AND COALESCE(ca.ca_eligibility, 'Not Eligible') = 'Eligible' THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS eligibility,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) < 80
            OR COALESCE(ca.ca_eligibility, 'Not Eligible') <> 'Eligible' THEN 'E*'
        ELSE (
            SELECT gs.grade_code
            FROM grade_scales gs
            WHERE gs.minimum_mark <= ROUND(COALESCE(ca.ca_mark, 0) + COALESCE(fe.final_exam_contribution, 0), 2)
            ORDER BY gs.minimum_mark DESC LIMIT 1
        )
        END AS grade,
    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) < 80
            OR COALESCE(ca.ca_eligibility, 'Not Eligible') <> 'Eligible' THEN 0.00
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

-- 4.8 Semester GPA (SGPA) View
CREATE OR REPLACE VIEW v_sgpa AS
SELECT
    student_id,
    registration_no,
    full_name,
    ROUND(
            SUM(CASE WHEN is_gpa = TRUE THEN grade_point * total_credits ELSE 0 END) /
            NULLIF(SUM(CASE WHEN is_gpa = TRUE THEN total_credits ELSE 0 END), 0),
            2
    ) AS sgpa,
    SUM(CASE WHEN is_gpa = TRUE THEN total_credits ELSE 0 END) AS semester_credits,
    SUM(total_credits) AS total_semester_credits,
    SUM(CASE WHEN is_gpa = TRUE THEN grade_point * total_credits ELSE 0 END) AS semester_grade_points
FROM v_final_results
GROUP BY student_id, registration_no, full_name;

-- 4.9 Cumulative GPA (CGPA) View
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

-- 4.10 Enrollment Requests View (NEW)
-- One row per request with student / course / admin names; already_enrolled = 1 when the
-- student already has an enrollments row for that course and semester.
CREATE OR REPLACE VIEW v_enrollment_requests AS
SELECT
    r.request_id,
    r.student_id,
    ug.registration_no,
    stu.full_name AS student_name,
    ug.student_type,
    r.course_id,
    c.course_code,
    c.course_name,
    c.total_credits,
    r.academic_year,
    r.semester,
    r.enrollment_type,
    r.request_note,
    r.status,
    r.requested_at,
    r.decided_by,
    adm.full_name AS decided_by_name,
    r.decided_at,
    r.decision_note,
    EXISTS (
        SELECT 1 FROM enrollments e
        WHERE e.student_id = r.student_id AND e.course_id = r.course_id
          AND e.academic_year = r.academic_year AND e.semester = r.semester
    ) AS already_enrolled
FROM enrollment_requests r
         JOIN undergraduates ug ON ug.user_id = r.student_id
         JOIN users stu ON stu.user_id = r.student_id
         JOIN courses c ON c.course_id = r.course_id
         LEFT JOIN users adm ON adm.user_id = r.decided_by;

-- 4.11 Attendance By Component View (NEW)
-- One row per student / course / component, where component is
--     'Theory'    - theory sessions only
--     'Practical' - practical sessions only
--     'Combined'  - theory + practical together (same figures as v_attendance_summary)
-- A theory-only course gets 'Theory' and 'Combined' rows, a practical-only course gets
-- 'Practical' and 'Combined' rows, a course with both gets all three.
-- Same rules as v_attendance_summary:
--     raw_attendance_percent      = (PRESENT + LATE) / sessions
--     adjusted_attendance_percent = (PRESENT + LATE + MEDICAL sessions that have an APPROVED medical) / sessions
-- A MEDICAL session whose medical is Pending or Rejected earns no credit.
CREATE OR REPLACE VIEW v_attendance_by_component AS
SELECT
    p.student_id,
    ug.registration_no,
    usr.full_name,
    p.course_id,
    c.course_code,
    c.course_name,
    p.component,
    p.total_sessions,
    p.attended_sessions,
    p.medical_sessions,
    p.approved_medical_sessions,
    p.absent_sessions,
    p.attended_sessions * 100.0 / NULLIF(p.total_sessions, 0) AS raw_attendance_percent,
    (p.attended_sessions + p.approved_medical_sessions) * 100.0 / NULLIF(p.total_sessions, 0) AS adjusted_attendance_percent,
    CASE
        WHEN (p.attended_sessions + p.approved_medical_sessions) * 100.0 / NULLIF(p.total_sessions, 0) >= 80
            THEN 'Pass Attendance Requirement'
        ELSE 'Fail Attendance Requirement'
        END AS attendance_requirement
FROM (
         SELECT
             d.student_id,
             d.course_id,
             s.component,
             COUNT(*) AS total_sessions,
             SUM(CASE WHEN d.attendance_status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended_sessions,
             SUM(CASE WHEN d.attendance_status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_sessions,
             SUM(CASE WHEN d.attendance_status = 'MEDICAL' AND d.has_approved_medical = 1 THEN 1 ELSE 0 END) AS approved_medical_sessions,
             SUM(CASE WHEN d.attendance_status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_sessions
         FROM (
                  SELECT
                      e.student_id,
                      ats.course_id,
                      ats.component AS session_component,
                      ar.attendance_status,
                      EXISTS (
                          SELECT 1 FROM medicals m
                          WHERE m.attendance_record_id = ar.attendance_record_id AND m.approval_status = 'Approved'
                      ) AS has_approved_medical
                  FROM enrollments e
                           JOIN attendance_sessions ats ON ats.course_id = e.course_id
                           JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id
                      AND ar.student_id = e.student_id
              ) d
                  JOIN (
             SELECT 'Theory' AS component
             UNION ALL SELECT 'Practical'
             UNION ALL SELECT 'Combined'
         ) s ON s.component = 'Combined' OR s.component = d.session_component
         GROUP BY d.student_id, d.course_id, s.component
     ) p
         JOIN undergraduates ug ON ug.user_id = p.student_id
         JOIN users usr ON usr.user_id = p.student_id
         JOIN courses c ON c.course_id = p.course_id;

-- ----------------------------------------------------------------------------
-- INITIAL SEED DATA
-- ----------------------------------------------------------------------------

-- 5.1 Department
INSERT INTO departments (department_code, department_name, description) VALUES
    ('ICT', 'Department of Information and Communication Technology', 'Faculty of Technology - ICT Department');

-- 5.2 UGC Grade Scale
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
-- FIXED: MIN_CA_PERCENT description - it is a percentage of the course's CA weight
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
                                                                          ('UNIVERSITY_NAME', 'University of Ruhuna', 'University name'),
                                                                          ('FACULTY_NAME', 'Faculty of Technology', 'Faculty name'),
                                                                          ('ACADEMIC_YEAR', '2026', 'Current academic year'),
                                                                          ('SEMESTER', 'Semester I', 'Current semester'),
                                                                          ('MIN_ATTENDANCE_PERCENT', '80', 'Minimum attendance percentage required'),
                                                                          ('MIN_CA_PERCENT', '40', 'Minimum CA mark, as a percentage of the course CA weight, required for final examination eligibility'),
                                                                          ('TOTAL_THEORY_SESSIONS', '15', 'Expected theory sessions per course'),
                                                                          ('TOTAL_PRACTICAL_SESSIONS', '15', 'Expected practical sessions per course');

-- 5.4 Users Seed
INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
    ('admin', 'adm@123', 'Admin', 'System Admin', '198512345678', '1985-05-12', 'Male', 'admin@fot.ruh.ac.lk', '0412222222', 'Faculty of Technology, University of Ruhuna', 1);
INSERT INTO admins (user_id, admin_code, designation) VALUES (LAST_INSERT_ID(), 'ADM001', 'System Administrator');

INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
                                                                                                                                        ('lecturer1', 'lec@123', 'Lecturer', 'Dr. N. Perera',       '197823456789', '1978-03-15', 'Male',   'lecturer1@fot.ruh.ac.lk', '0711000001', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('lecturer2', 'lec@123', 'Lecturer', 'Ms. S. Fernando',     '198234567890', '1982-08-22', 'Female', 'lecturer2@fot.ruh.ac.lk', '0711000002', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('lecturer3', 'lec@123', 'Lecturer', 'Mr. A. Gunawardena',  '198045678901', '1980-11-05', 'Male',   'lecturer3@fot.ruh.ac.lk', '0711000003', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('lecturer4', 'lec@123', 'Lecturer', 'Dr. R. Jayasuriya',   '197556789012', '1975-01-30', 'Male',   'lecturer4@fot.ruh.ac.lk', '0711000004', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('lecturer5', 'lec@123', 'Lecturer', 'Ms. T. Karunaratne',  '198867890123', '1988-06-18', 'Female', 'lecturer5@fot.ruh.ac.lk', '0711000005', 'Department of ICT, Faculty of Technology', 1);

INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC001', 'Dr.', 'Data Structures and Algorithms' FROM users WHERE username = 'lecturer1';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC002', 'Ms.', 'Database Systems' FROM users WHERE username = 'lecturer2';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC003', 'Mr.', 'Computer Networks' FROM users WHERE username = 'lecturer3';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC004', 'Dr.', 'Software Engineering' FROM users WHERE username = 'lecturer4';
INSERT INTO lecturers (user_id, lecturer_code, title, specialization) SELECT user_id, 'LEC005', 'Ms.', 'Web and Application Development' FROM users WHERE username = 'lecturer5';

INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
                                                                                                                                        ('to1', 'to@123', 'Technical Officer', 'K. Silva',        '198678901234', '1986-04-10', 'Male', 'to1@fot.ruh.ac.lk', '0712000001', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('to2', 'to@123', 'Technical Officer', 'M. Wijeratne',    '198989012345', '1989-09-25', 'Male', 'to2@fot.ruh.ac.lk', '0712000002', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('to3', 'to@123', 'Technical Officer', 'D. Samarasinghe', '199190123456', '1991-12-03', 'Male', 'to3@fot.ruh.ac.lk', '0712000003', 'Department of ICT, Faculty of Technology', 1),
                                                                                                                                        ('to4', 'to@123', 'Technical Officer', 'P. Nandasena',    '198701234567', '1987-07-14', 'Male', 'to4@fot.ruh.ac.lk', '0712000004', 'Department of ICT, Faculty of Technology', 1);

INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO001', 'Technical Officer' FROM users WHERE username = 'to1';
INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO002', 'Technical Officer' FROM users WHERE username = 'to2';
INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO003', 'Technical Officer' FROM users WHERE username = 'to3';
INSERT INTO technical_officers (user_id, technical_officer_code, designation) SELECT user_id, 'TO004', 'Technical Officer' FROM users WHERE username = 'to4';

INSERT INTO users (username, password_hash, role, full_name, nic, date_of_birth, gender, email, contact_no, address, department_id) VALUES
                                                                                                                                        ('tg2061', 'ug@123', 'Undergraduate', 'Kasun Madushan',       '200210002061', '2002-01-10', 'Male',   'tg2061@fot.ruh.ac.lk', '0713002061', 'Matara, Sri Lanka', 1),
                                                                                                                                        ('tg2062', 'ug@123', 'Undergraduate', 'Nimali Perera',        '200210002062', '2002-02-15', 'Female', 'tg2062@fot.ruh.ac.lk', '0713002062', 'Galle, Sri Lanka', 1),
                                                                                                                                        ('tg2063', 'ug@123', 'Undergraduate', 'Ravindu Silva',        '200210002063', '2002-03-20', 'Male',   'tg2063@fot.ruh.ac.lk', '0713002063', 'Hambantota, Sri Lanka', 1),
                                                                                                                                        ('tg2064', 'ug@123', 'Undergraduate', 'Dilini Jayawardena',   '200210002064', '2002-04-25', 'Female', 'tg2064@fot.ruh.ac.lk', '0713002064', 'Colombo, Sri Lanka', 1),
                                                                                                                                        ('tg2065', 'ug@123', 'Undergraduate', 'Sahan Wickramasinghe', '200210002065', '2002-05-12', 'Male',   'tg2065@fot.ruh.ac.lk', '0713002065', 'Kandy, Sri Lanka', 1),
                                                                                                                                        ('tg2066', 'ug@123', 'Undergraduate', 'Ishara Gunasekara',    '200210002066', '2002-06-18', 'Female', 'tg2066@fot.ruh.ac.lk', '0713002066', 'Matara, Sri Lanka', 1),
                                                                                                                                        ('tg2067', 'ug@123', 'Undergraduate', 'Tharindu Fernando',    '200210002067', '2002-07-22', 'Male',   'tg2067@fot.ruh.ac.lk', '0713002067', 'Galle, Sri Lanka', 1),
                                                                                                                                        ('tg2068', 'ug@123', 'Undergraduate', 'Amaya Rathnayake',     '200210002068', '2002-08-05', 'Female', 'tg2068@fot.ruh.ac.lk', '0713002068', 'Kandy, Sri Lanka', 1),
                                                                                                                                        ('tg2069', 'ug@123', 'Undergraduate', 'Chamod Dissanayake',   '200210002069', '2002-09-14', 'Male',   'tg2069@fot.ruh.ac.lk', '0713002069', 'Matara, Sri Lanka', 1),
                                                                                                                                        ('tg2070', 'ug@123', 'Undergraduate', 'Hasini Ekanayake',     '200210002070', '2002-10-28', 'Female', 'tg2070@fot.ruh.ac.lk', '0713002070', 'Galle, Sri Lanka', 1),
                                                                                                                                        ('tg2071', 'ug@123', 'Undergraduate', 'Pasindu Bandara',      '200210002071', '2002-11-03', 'Male',   'tg2071@fot.ruh.ac.lk', '0713002071', 'Kandy, Sri Lanka', 1),
                                                                                                                                        ('tg2072', 'ug@123', 'Undergraduate', 'Sanduni Herath',       '200210002072', '2002-12-19', 'Female', 'tg2072@fot.ruh.ac.lk', '0713002072', 'Matara, Sri Lanka', 1),
                                                                                                                                        ('tg2073', 'ug@123', 'Undergraduate', 'Nuwan Abeysekara',     '200210002073', '2002-01-31', 'Male',   'tg2073@fot.ruh.ac.lk', '0713002073', 'Galle, Sri Lanka', 1),
                                                                                                                                        ('tg2074', 'ug@123', 'Undergraduate', 'Piumi Senanayake',     '200210002074', '2002-02-24', 'Female', 'tg2074@fot.ruh.ac.lk', '0713002074', 'Colombo, Sri Lanka', 1),
                                                                                                                                        ('tg2075', 'ug@123', 'Undergraduate', 'Lahiru Weerasinghe',   '200210002075', '2002-03-11', 'Male',   'tg2075@fot.ruh.ac.lk', '0713002075', 'Kandy, Sri Lanka', 1),
                                                                                                                                        ('tg2076', 'ug@123', 'Undergraduate', 'Thilini Kumari',       '200210002076', '2002-04-08', 'Female', 'tg2076@fot.ruh.ac.lk', '0713002076', 'Matara, Sri Lanka', 1),
                                                                                                                                        ('tg2077', 'ug@123', 'Undergraduate', 'Janith Rajapaksha',    '200210002077', '2002-05-17', 'Male',   'tg2077@fot.ruh.ac.lk', '0713002077', 'Galle, Sri Lanka', 1),
                                                                                                                                        ('tg2078', 'ug@123', 'Undergraduate', 'Oshadi Wijesinghe',    '200110002078', '2001-06-25', 'Female', 'tg2078@fot.ruh.ac.lk', '0713002078', 'Hambantota, Sri Lanka', 1),
                                                                                                                                        ('tg2079', 'ug@123', 'Undergraduate', 'Chathura Peiris',      '200110002079', '2001-07-30', 'Male',   'tg2079@fot.ruh.ac.lk', '0713002079', 'Colombo, Sri Lanka', 1),
                                                                                                                                        ('tg2080', 'ug@123', 'Undergraduate', 'Menaka Liyanage',      '200010002080', '2000-08-14', 'Female', 'tg2080@fot.ruh.ac.lk', '0713002080', 'Kandy, Sri Lanka', 1);

INSERT INTO undergraduates (user_id, registration_no, index_no, batch_year, academic_year, student_type, intake, current_level, current_semester, previous_credits, previous_grade_points, admission_date)
SELECT
    user_id,
    CONCAT('TG/2024/', 2060 + ROW_NUMBER() OVER (ORDER BY user_id)),
    CONCAT('ICT/24/', LPAD(ROW_NUMBER() OVER (ORDER BY user_id), 3, '0')),
    2024, '2026', 'Proper', '2024', 2, 1, 15.00, 45.00, '2024-09-01'
FROM users WHERE role = 'Undergraduate';

UPDATE undergraduates SET student_type = 'Repeat' WHERE registration_no IN ('TG/2024/2078', 'TG/2024/2079');
UPDATE undergraduates SET student_type = 'Batch Missed', batch_year = 2023 WHERE registration_no = 'TG/2024/2080';

-- CHANGED: store the seed passwords as SHA-256 hashes (same hex format as AuthService.hashPassword).
-- The login passwords themselves are unchanged: admin / adm@123, lecturer1 / lec@123,
-- to1 / to@123, tg2061 / ug@123 ...
UPDATE users SET password_hash = SHA2(password_hash, 256);

-- 5.5 Courses Seed (Level 2 Semester 1 Actual Courses with Sub-weights)
INSERT INTO courses (course_code, course_name, description, theory_credits, practical_credits, is_gpa, quiz_weight, assignment_weight, mid_theory_weight, mid_practical_weight, ca_weight, end_theory_weight, end_practical_weight, department_id, academic_year, semester) VALUES
                                                                                                                                                                                                                                                                                ('ICT2113', 'Data Structures and Algorithms',        'Data structures, algorithms, complexity and problem solving.', 2.0, 1.0, TRUE,  10.00, 10.00, 10.00, 10.00, 40.00, 40.00, 20.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('ICT2122', 'Object Oriented Programming',           'Core OOP concepts, principles, Java programming.',            2.0, 0.0, TRUE,  10.00, 10.00, 20.00,  0.00, 40.00, 60.00,  0.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('ICT2132', 'Object Oriented Programming Practicum', 'Practical Java programming, GUI development, lab tasks.',       0.0, 2.0, TRUE,  10.00, 10.00,  0.00, 30.00, 50.00,  0.00, 50.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('ICT2142', 'E-Business Systems',                    'E-commerce architecture, business models, digital payment.',  2.0, 0.0, TRUE,  10.00, 10.00, 20.00,  0.00, 40.00, 60.00,  0.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('ICT2152', 'Object Oriented Analysis and Design',   'UML modeling, design patterns, system architecture.',          2.0, 0.0, TRUE,  10.00, 10.00, 20.00,  0.00, 40.00, 60.00,  0.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('ICT2162', 'Management Information Systems',        'Enterprise systems, decision support, IT management.',        2.0, 0.0, TRUE,  10.00, 10.00, 20.00,  0.00, 40.00, 60.00,  0.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('TCS2112', 'Business Economics',                    'Micro & macroeconomics principles, business application.',    2.0, 0.0, TRUE,  10.00, 10.00, 10.00,  0.00, 30.00, 70.00,  0.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('TCS2121', 'Soft Skills',                           'Leadership, ethics, team work, career skills.',               1.0, 0.0, TRUE,  20.00, 30.00,  0.00,  0.00, 50.00, 50.00,  0.00, 1, '2026', 'Semester I'),
                                                                                                                                                                                                                                                                                ('ENG2112', 'English III',                           'Advanced English language and communication skills (NGPA).',  2.0, 0.0, FALSE, 20.00, 30.00,  0.00,  0.00, 50.00, 50.00,  0.00, 1, '2026', 'Semester I');

-- 5.6 Lecturer Course Assignments
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'), 'Coordinator' FROM courses WHERE course_code = 'ICT2113';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'), 'Coordinator' FROM courses WHERE course_code = 'ICT2122';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'), 'Coordinator' FROM courses WHERE course_code = 'ICT2132';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC002'), 'Coordinator' FROM courses WHERE course_code = 'ICT2142';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC005'), 'Coordinator' FROM courses WHERE course_code = 'ICT2152';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC004'), 'Coordinator' FROM courses WHERE course_code = 'ICT2162';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC003'), 'Coordinator' FROM courses WHERE course_code = 'TCS2112';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC005'), 'Coordinator' FROM courses WHERE course_code = 'TCS2121';
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility) SELECT course_id, (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC002'), 'Coordinator' FROM courses WHERE course_code = 'ENG2112';

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
    (u.student_type = 'Repeat' AND c.course_code = 'ENG2112')
    );

-- 5.9 Assessments Seed (3 Quizzes per course + Assignments + Mid + Final Exams)
-- 3 Quizzes for each course (each has quiz_weight / 2)
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Quiz 1', 'CA', 'Quiz1', 'Theory', ROUND(quiz_weight / 2.0, 2), 100.00 FROM courses WHERE quiz_weight > 0;

INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Quiz 2', 'CA', 'Quiz2', 'Theory', ROUND(quiz_weight / 2.0, 2), 100.00 FROM courses WHERE quiz_weight > 0;

INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Quiz 3', 'CA', 'Quiz3', 'Theory', ROUND(quiz_weight / 2.0, 2), 100.00 FROM courses WHERE quiz_weight > 0;

-- Assignment
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Assignment 1', 'CA', 'Assignment', 'Theory', assignment_weight, 100.00 FROM courses WHERE assignment_weight > 0;

-- Mid Exam Theory
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Mid Exam Theory', 'CA', 'Mid Exam Theory', 'Theory', mid_theory_weight, 100.00 FROM courses WHERE mid_theory_weight > 0;

-- Mid Exam Practical
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Mid Exam Practical', 'CA', 'Mid Exam Practical', 'Practical', mid_practical_weight, 100.00 FROM courses WHERE mid_practical_weight > 0;

-- End Semester Theory Exam
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'End Semester Theory Exam', 'FINAL', 'End Exam Theory', 'Theory', end_theory_weight, 100.00 FROM courses WHERE end_theory_weight > 0;

-- End Semester Practical Exam
INSERT INTO assessments (course_id, assessment_name, assessment_category, assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'End Semester Practical Exam', 'FINAL', 'End Exam Practical', 'Practical', end_practical_weight, 100.00 FROM courses WHERE end_practical_weight > 0;

-- 5.10 Attendance Sessions Generation
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

-- 5.11 Attendance Records
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

-- 5.12 Medical Records
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

UPDATE attendance_records ar
    JOIN medicals m ON m.attendance_record_id = ar.attendance_record_id
SET ar.attendance_status = 'MEDICAL'
WHERE ar.attendance_record_id > 0 AND m.approval_status = 'Approved';

-- 5.13 Student Marks Seed (including 3 quizzes per student per course)
INSERT INTO marks (assessment_id, student_id, mark, entered_by)
SELECT
    a.assessment_id,
    e.student_id,
    CASE
        WHEN a.assessment_type = 'Quiz1' THEN 70.00 + MOD(e.student_id * 3, 25)
        WHEN a.assessment_type = 'Quiz2' THEN 80.00 + MOD(e.student_id * 5, 18)
        WHEN a.assessment_type = 'Quiz3' THEN 65.00 + MOD(e.student_id * 7, 30)
        WHEN MOD(e.student_id, 5) = 0 THEN 88.00
        WHEN MOD(e.student_id, 5) = 1 THEN 78.00
        WHEN MOD(e.student_id, 5) = 2 THEN 68.00
        WHEN MOD(e.student_id, 5) = 3 THEN 52.00
        ELSE 42.00
        END,
    (SELECT cl.lecturer_id FROM course_lecturers cl WHERE cl.course_id = a.course_id LIMIT 1)
FROM assessments a
         JOIN enrollments e ON e.course_id = a.course_id;

-- 5.14 System Notices
-- FIXED: CA Eligibility Notice wording - 40% of the course CA weight
INSERT INTO notices (title, content, notice_type, target_role, department_id, published_by, status) VALUES
                                                                                                        ('Semester I Academic Notice', 'Students are requested to regularly check attendance, course and examination information.', 'Academic', 'All', 1, (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), 'Published'),
                                                                                                        ('Attendance Eligibility Reminder', 'Students must maintain at least 80% attendance according to academic regulations.', 'Attendance', 'Undergraduate', 1, (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), 'Published'),
                                                                                                        ('CA Eligibility Notice', 'Continuous Assessment (CA) marks must be at least 40% of the total CA weight of the course (e.g. 16 out of 40, 20 out of 50, 12 out of 30) to be eligible for the final examination.', 'Exam', 'Undergraduate', 1, (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), 'Published');

-- 5.15 Timetable Schedule (REWRITTEN)
-- Explicit weekly timetable for the 2024 batch, one row per weekly slot (2-hour slots, same
-- length as the attendance sessions in 5.10):
--   * 8 theory slots  : every course that has theory credits
--   * 2 practical slots: ICT2113 (theory + practical course) and ICT2132 (practical-only course)
-- The lecturer is the course coordinator from 5.6. The schedule has no clashes: no two courses
-- of the batch overlap, no lecturer is in two places at once and no venue is double-booked.
--
--         08:00-10:00            10:00-12:00             13:00-15:00
--   Mon   ICT2122 Theory         ICT2142 Theory          TCS2112 Theory
--   Tue   ICT2113 Theory         ICT2152 Theory          TCS2121 Theory
--   Wed   ICT2162 Theory         ENG2112 Theory          ICT2113 Practical
--   Thu   -                     -                       ICT2132 Practical
CREATE TEMPORARY TABLE tmp_timetable_plan (
                                              course_code  VARCHAR(20) NOT NULL,
                                              component    VARCHAR(10) NOT NULL,
                                              day_of_week  VARCHAR(10) NOT NULL,
                                              start_time   TIME        NOT NULL,
                                              end_time     TIME        NOT NULL,
                                              venue        VARCHAR(100) NOT NULL
);

INSERT INTO tmp_timetable_plan (course_code, component, day_of_week, start_time, end_time, venue) VALUES
                                                                                                      ('ICT2122', 'Theory',    'Monday',    '08:00:00', '10:00:00', 'Lecture Hall 1'),
                                                                                                      ('ICT2142', 'Theory',    'Monday',    '10:00:00', '12:00:00', 'Lecture Hall 1'),
                                                                                                      ('TCS2112', 'Theory',    'Monday',    '13:00:00', '15:00:00', 'Lecture Hall 2'),
                                                                                                      ('ICT2113', 'Theory',    'Tuesday',   '08:00:00', '10:00:00', 'Lecture Hall 1'),
                                                                                                      ('ICT2152', 'Theory',    'Tuesday',   '10:00:00', '12:00:00', 'Lecture Hall 1'),
                                                                                                      ('TCS2121', 'Theory',    'Tuesday',   '13:00:00', '15:00:00', 'Lecture Hall 2'),
                                                                                                      ('ICT2162', 'Theory',    'Wednesday', '08:00:00', '10:00:00', 'Lecture Hall 1'),
                                                                                                      ('ENG2112', 'Theory',    'Wednesday', '10:00:00', '12:00:00', 'Lecture Hall 2'),
                                                                                                      ('ICT2113', 'Practical', 'Wednesday', '13:00:00', '15:00:00', 'ICT Lab 1'),
                                                                                                      ('ICT2132', 'Practical', 'Thursday',  '13:00:00', '15:00:00', 'ICT Lab 1');

INSERT INTO timetable_entries (department_id, course_id, lecturer_id, batch_year, component, day_of_week, start_time, end_time, venue, academic_year, semester)
SELECT
    1, c.course_id, cl.lecturer_id, 2024, p.component, p.day_of_week, p.start_time, p.end_time, p.venue, '2026', 'Semester I'
FROM tmp_timetable_plan p
         JOIN courses c ON c.course_code = p.course_code
         JOIN course_lecturers cl ON cl.course_id = c.course_id AND cl.responsibility = 'Coordinator';

DROP TEMPORARY TABLE tmp_timetable_plan;

-- 5.16 Enrollment Requests Seed (NEW) - sample data for the admin accept / decline screen
--   3 Pending  : students asking for courses they are not enrolled in yet
--   1 Declined : with the admin's reason
--   1 Approved : (its enrollments row already exists from 5.8)
INSERT INTO enrollment_requests
(student_id, course_id, academic_year, semester, enrollment_type, request_note, status, decision_note, decided_by, decided_at)
VALUES
    ((SELECT user_id FROM users WHERE username = 'tg2080'), (SELECT course_id FROM courses WHERE course_code = 'ICT2132'),
     '2026', 'Semester I', 'Batch Missed', 'Batch missed - need to follow this module', 'Pending', NULL, NULL, NULL),
    ((SELECT user_id FROM users WHERE username = 'tg2080'), (SELECT course_id FROM courses WHERE course_code = 'ICT2162'),
     '2026', 'Semester I', 'Batch Missed', 'Batch missed - need to follow this module', 'Pending', NULL, NULL, NULL),
    ((SELECT user_id FROM users WHERE username = 'tg2078'), (SELECT course_id FROM courses WHERE course_code = 'ENG2112'),
     '2026', 'Semester I', 'Repeat', 'Repeat attempt for this course', 'Pending', NULL, NULL, NULL),
    ((SELECT user_id FROM users WHERE username = 'tg2079'), (SELECT course_id FROM courses WHERE course_code = 'ENG2112'),
     '2026', 'Semester I', 'Repeat', 'Repeat attempt for this course', 'Declined', 'Course already completed with a pass grade',
     (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), NOW()),
    ((SELECT user_id FROM users WHERE username = 'tg2062'), (SELECT course_id FROM courses WHERE course_code = 'ICT2142'),
     '2026', 'Semester I', 'Normal', 'Late registration request', 'Approved', 'Approved by admin',
     (SELECT user_id FROM admins WHERE admin_code = 'ADM001'), NOW());

-- 5.17 Test Scenarios (NEW) - attendance, medical, component, CA and grade edge cases
-- ----------------------------------------------------------------------------
-- The generated data in 5.8 - 5.13 gives every student 90-100% attendance and a passing
-- result. This section overrides specific student/course pairs so every rule has a row to
-- show. Only the pairs listed below are changed; all other rows keep their generated values.
-- tg2065, tg2070, tg2075 and tg2080 (the students with the original 10 approved medicals)
-- are NOT touched.
--
-- Eligibility = attendance >= 80% (approved medicals counted, as in v_attendance_summary)
--               AND CA mark >= 40% of the course CA weight.   Not eligible => grade E*, 0.00 points.
--
-- ATTENDANCE (theory-only courses, 15 sessions)            attendance        result
--  A1  tg2061 ICT2122  exactly 80%: 9 PRESENT + 3 LATE       80.00            Eligible
--  A2  tg2062 ICT2142  exactly 80% only thanks to 2 medicals 66.67 -> 80.00   Eligible
--  A3  tg2063 ICT2152  below 80%, no medicals, marks ~90     73.33            Not Eligible (E*)
--  A4  tg2064 ICT2162  below 80% raw, 3 approved medicals    66.67 -> 86.67   Eligible
--  A5  tg2066 TCS2112  below 80% even with 2 approved        60.00 -> 73.33   Not Eligible (E*)
--  A6  tg2067 ICT2122  2 medicals still PENDING (no credit)  73.33            Not Eligible (E*)
--  A7  tg2068 ICT2142  2 medicals REJECTED (no credit)       73.33            Not Eligible (E*)
--  A8  tg2069 ICT2152  1 Approved + 1 Pending + 1 Rejected   66.67 -> 80.00   Eligible
--  A9  tg2071 ICT2162  very low attendance                   40.00            Not Eligible (E*)
--  A10 tg2072 ICT2152  attendance AND CA both fail           53.33            Not Eligible (E*)
--
-- THEORY / PRACTICAL / COMBINED (see v_attendance_by_component)   theory  practical  combined
--  C1  tg2073 ICT2113  strong theory, weak practical             100.00   60.00      80.00  Eligible
--  C2  tg2074 ICT2113  strong theory, poor practical             100.00   40.00      70.00  Not Eligible (E*)
--  C3  tg2076 ICT2113  practical absences covered by medicals    100.00   86.67      93.33  Eligible
--  C4  tg2077 ICT2132  practical-only course, exactly 80%                 80.00      80.00  Eligible
--  C5  tg2078 ICT2132  practical-only course, 60%                         60.00      60.00  Not Eligible (E*)
--  C6  tg2079 ICT2132  practical-only course, 3 approved medicals         86.67      86.67  Eligible
--
-- CA RULE (40% of the course CA weight; attendance is normal in all of these)
--  M1  tg2061 TCS2112  CA 12.00 / 30  = exactly 40%          Eligible,     final 47.00 (C+)
--  M2  tg2062 TCS2112  CA 11.70 / 30  = just below 40%       Not Eligible (E*)
--  M3  tg2063 TCS2121  CA 20.00 / 50  = exactly 40%          Eligible,     final 45.00 (C+)
--  M4  tg2064 TCS2121  CA 19.50 / 50  = just below 40%       Not Eligible (E*)
--  M5  tg2066 ICT2122  CA 16.00 / 40  = exactly 40%          Eligible,     final 40.00 (C)
--  M6  tg2068 ICT2122  CA 15.60 / 40  = just below 40%       Not Eligible (E*)
--  M7  tg2071 ICT2152  assignment not submitted, CA 13.00/40 Not Eligible (E*)
--
-- GRADE LADDER (ICT2142, CA exactly 16.00 / 40, only the end exam differs)
--  G1  tg2073  final 22.00 -> E      (eligible but failed the course)
--  G2  tg2074  final 28.00 -> D
--  G3  tg2076  final 32.80 -> D+
--  G4  tg2077  final 37.60 -> C-
--  G5  tg2078  CA 24.00, final 63.00 -> B+
-- ----------------------------------------------------------------------------

-- Attendance plan: each row sets sessions session_from..session_to of one student / course /
-- component. Every plan covers sessions 1..15 of its component. medical_status is set only on
-- MEDICAL sessions and creates the linked medicals row with that approval status.
CREATE TEMPORARY TABLE tmp_att_plan (
                                        username       VARCHAR(50) NOT NULL,
                                        course_code    VARCHAR(20) NOT NULL,
                                        component      VARCHAR(10) NOT NULL,
                                        session_from   INT         NOT NULL,
                                        session_to     INT         NOT NULL,
                                        att_status     VARCHAR(10) NOT NULL,
                                        medical_status VARCHAR(10) NULL
);

INSERT INTO tmp_att_plan (username, course_code, component, session_from, session_to, att_status, medical_status) VALUES
                                                                                                                      -- A1: exactly 80% (12 of 15 attended, 3 of them LATE)
                                                                                                                      ('tg2061', 'ICT2122', 'Theory', 1,  6,  'PRESENT', NULL),
                                                                                                                      ('tg2061', 'ICT2122', 'Theory', 7,  9,  'LATE',    NULL),
                                                                                                                      ('tg2061', 'ICT2122', 'Theory', 10, 12, 'PRESENT', NULL),
                                                                                                                      ('tg2061', 'ICT2122', 'Theory', 13, 15, 'ABSENT',  NULL),
                                                                                                                      -- A2: 10 present + 2 approved medicals = exactly 80%
                                                                                                                      ('tg2062', 'ICT2142', 'Theory', 1,  4,  'PRESENT', NULL),
                                                                                                                      ('tg2062', 'ICT2142', 'Theory', 5,  6,  'MEDICAL', 'Approved'),
                                                                                                                      ('tg2062', 'ICT2142', 'Theory', 7,  12, 'PRESENT', NULL),
                                                                                                                      ('tg2062', 'ICT2142', 'Theory', 13, 15, 'ABSENT',  NULL),
                                                                                                                      -- A3: 11 present, 4 absent, no medicals = 73.33%
                                                                                                                      ('tg2063', 'ICT2152', 'Theory', 1,  6,  'PRESENT', NULL),
                                                                                                                      ('tg2063', 'ICT2152', 'Theory', 7,  8,  'ABSENT',  NULL),
                                                                                                                      ('tg2063', 'ICT2152', 'Theory', 9,  13, 'PRESENT', NULL),
                                                                                                                      ('tg2063', 'ICT2152', 'Theory', 14, 15, 'ABSENT',  NULL),
                                                                                                                      -- A4: 10 present + 3 approved medicals = 86.67% (raw 66.67%)
                                                                                                                      ('tg2064', 'ICT2162', 'Theory', 1,  5,  'PRESENT', NULL),
                                                                                                                      ('tg2064', 'ICT2162', 'Theory', 6,  8,  'MEDICAL', 'Approved'),
                                                                                                                      ('tg2064', 'ICT2162', 'Theory', 9,  13, 'PRESENT', NULL),
                                                                                                                      ('tg2064', 'ICT2162', 'Theory', 14, 15, 'ABSENT',  NULL),
                                                                                                                      -- A5: 9 present + 2 approved medicals = 73.33% (still below 80%)
                                                                                                                      ('tg2066', 'TCS2112', 'Theory', 1,  3,  'PRESENT', NULL),
                                                                                                                      ('tg2066', 'TCS2112', 'Theory', 4,  5,  'MEDICAL', 'Approved'),
                                                                                                                      ('tg2066', 'TCS2112', 'Theory', 6,  11, 'PRESENT', NULL),
                                                                                                                      ('tg2066', 'TCS2112', 'Theory', 12, 15, 'ABSENT',  NULL),
                                                                                                                      -- A6: 11 present, 2 MEDICAL sessions whose medical is Pending -> not credited = 73.33%
                                                                                                                      ('tg2067', 'ICT2122', 'Theory', 1,  6,  'PRESENT', NULL),
                                                                                                                      ('tg2067', 'ICT2122', 'Theory', 7,  8,  'MEDICAL', 'Pending'),
                                                                                                                      ('tg2067', 'ICT2122', 'Theory', 9,  13, 'PRESENT', NULL),
                                                                                                                      ('tg2067', 'ICT2122', 'Theory', 14, 15, 'ABSENT',  NULL),
                                                                                                                      -- A7: 11 present, 2 MEDICAL sessions whose medical was Rejected -> not credited = 73.33%
                                                                                                                      ('tg2068', 'ICT2142', 'Theory', 1,  6,  'PRESENT', NULL),
                                                                                                                      ('tg2068', 'ICT2142', 'Theory', 7,  8,  'MEDICAL', 'Rejected'),
                                                                                                                      ('tg2068', 'ICT2142', 'Theory', 9,  13, 'PRESENT', NULL),
                                                                                                                      ('tg2068', 'ICT2142', 'Theory', 14, 15, 'ABSENT',  NULL),
                                                                                                                      -- A8: one Approved, one Pending, one Rejected medical; 10 present + 2 approved = exactly 80%
                                                                                                                      ('tg2069', 'ICT2152', 'Theory', 1,  4,  'PRESENT', NULL),
                                                                                                                      ('tg2069', 'ICT2152', 'Theory', 5,  6,  'MEDICAL', 'Approved'),
                                                                                                                      ('tg2069', 'ICT2152', 'Theory', 7,  7,  'MEDICAL', 'Pending'),
                                                                                                                      ('tg2069', 'ICT2152', 'Theory', 8,  8,  'MEDICAL', 'Rejected'),
                                                                                                                      ('tg2069', 'ICT2152', 'Theory', 9,  14, 'PRESENT', NULL),
                                                                                                                      ('tg2069', 'ICT2152', 'Theory', 15, 15, 'ABSENT',  NULL),
                                                                                                                      -- A9: 6 present, 9 absent = 40%
                                                                                                                      ('tg2071', 'ICT2162', 'Theory', 1,  3,  'PRESENT', NULL),
                                                                                                                      ('tg2071', 'ICT2162', 'Theory', 4,  7,  'ABSENT',  NULL),
                                                                                                                      ('tg2071', 'ICT2162', 'Theory', 8,  10, 'PRESENT', NULL),
                                                                                                                      ('tg2071', 'ICT2162', 'Theory', 11, 15, 'ABSENT',  NULL),
                                                                                                                      -- A10: 8 present, 7 absent = 53.33% (CA also fails, see marks plan)
                                                                                                                      ('tg2072', 'ICT2152', 'Theory', 1,  4,  'PRESENT', NULL),
                                                                                                                      ('tg2072', 'ICT2152', 'Theory', 5,  8,  'ABSENT',  NULL),
                                                                                                                      ('tg2072', 'ICT2152', 'Theory', 9,  12, 'PRESENT', NULL),
                                                                                                                      ('tg2072', 'ICT2152', 'Theory', 13, 15, 'ABSENT',  NULL),
                                                                                                                      -- C1: ICT2113 theory 15/15, practical 9/15 -> combined 24/30 = exactly 80%
                                                                                                                      ('tg2073', 'ICT2113', 'Theory',    1,  15, 'PRESENT', NULL),
                                                                                                                      ('tg2073', 'ICT2113', 'Practical', 1,  5,  'PRESENT', NULL),
                                                                                                                      ('tg2073', 'ICT2113', 'Practical', 6,  8,  'ABSENT',  NULL),
                                                                                                                      ('tg2073', 'ICT2113', 'Practical', 9,  12, 'PRESENT', NULL),
                                                                                                                      ('tg2073', 'ICT2113', 'Practical', 13, 15, 'ABSENT',  NULL),
                                                                                                                      -- C2: ICT2113 theory 15/15, practical 6/15 -> combined 21/30 = 70%
                                                                                                                      ('tg2074', 'ICT2113', 'Theory',    1,  15, 'PRESENT', NULL),
                                                                                                                      ('tg2074', 'ICT2113', 'Practical', 1,  3,  'PRESENT', NULL),
                                                                                                                      ('tg2074', 'ICT2113', 'Practical', 4,  6,  'ABSENT',  NULL),
                                                                                                                      ('tg2074', 'ICT2113', 'Practical', 7,  9,  'PRESENT', NULL),
                                                                                                                      ('tg2074', 'ICT2113', 'Practical', 10, 15, 'ABSENT',  NULL),
                                                                                                                      -- C3: ICT2113 theory 15/15, practical 10 present + 3 approved medicals -> combined 28/30 = 93.33%
                                                                                                                      ('tg2076', 'ICT2113', 'Theory',    1,  15, 'PRESENT', NULL),
                                                                                                                      ('tg2076', 'ICT2113', 'Practical', 1,  5,  'PRESENT', NULL),
                                                                                                                      ('tg2076', 'ICT2113', 'Practical', 6,  8,  'MEDICAL', 'Approved'),
                                                                                                                      ('tg2076', 'ICT2113', 'Practical', 9,  13, 'PRESENT', NULL),
                                                                                                                      ('tg2076', 'ICT2113', 'Practical', 14, 15, 'ABSENT',  NULL),
                                                                                                                      -- C4: ICT2132 (practical only) 12/15 = exactly 80%
                                                                                                                      ('tg2077', 'ICT2132', 'Practical', 1,  8,  'PRESENT', NULL),
                                                                                                                      ('tg2077', 'ICT2132', 'Practical', 9,  11, 'ABSENT',  NULL),
                                                                                                                      ('tg2077', 'ICT2132', 'Practical', 12, 15, 'PRESENT', NULL),
                                                                                                                      -- C5: ICT2132 9/15 = 60%
                                                                                                                      ('tg2078', 'ICT2132', 'Practical', 1,  4,  'PRESENT', NULL),
                                                                                                                      ('tg2078', 'ICT2132', 'Practical', 5,  9,  'ABSENT',  NULL),
                                                                                                                      ('tg2078', 'ICT2132', 'Practical', 10, 14, 'PRESENT', NULL),
                                                                                                                      ('tg2078', 'ICT2132', 'Practical', 15, 15, 'ABSENT',  NULL),
                                                                                                                      -- C6: ICT2132 10 present + 3 approved medicals = 86.67% (raw 66.67%)
                                                                                                                      ('tg2079', 'ICT2132', 'Practical', 1,  5,  'PRESENT', NULL),
                                                                                                                      ('tg2079', 'ICT2132', 'Practical', 6,  8,  'MEDICAL', 'Approved'),
                                                                                                                      ('tg2079', 'ICT2132', 'Practical', 9,  13, 'PRESENT', NULL),
                                                                                                                      ('tg2079', 'ICT2132', 'Practical', 14, 15, 'ABSENT',  NULL);

UPDATE attendance_records ar
    JOIN attendance_sessions ats ON ats.attendance_session_id = ar.attendance_session_id
    JOIN courses c ON c.course_id = ats.course_id
    JOIN users u ON u.user_id = ar.student_id
    JOIN tmp_att_plan p ON p.username = u.username
        AND p.course_code = c.course_code
        AND p.component = ats.component
        AND ats.session_no BETWEEN p.session_from AND p.session_to
SET ar.attendance_status = p.att_status;

-- One medicals row per MEDICAL session in the plan, with the planned approval status.
-- Pending rows have no reviewer yet; Approved / Rejected rows are reviewed by a technical officer.
INSERT INTO medicals (student_id, course_id, attendance_record_id, medical_date, reason, document_path,
                      approval_status, reviewed_by, reviewed_at, review_comment)
SELECT
    u.user_id, c.course_id, ar.attendance_record_id, ats.session_date,
    CASE p.medical_status
        WHEN 'Pending' THEN 'Medical leave - certificate submitted, awaiting review'
        ELSE 'Medical leave - certified illness'
        END,
    CONCAT('medicals/', u.username, '_', c.course_code, '_', LEFT(ats.component, 1), LPAD(ats.session_no, 2, '0'), '.pdf'),
    p.medical_status,
    CASE WHEN p.medical_status = 'Pending' THEN NULL ELSE (SELECT user_id FROM technical_officers LIMIT 1) END,
    CASE WHEN p.medical_status = 'Pending' THEN NULL ELSE NOW() END,
    CASE p.medical_status
        WHEN 'Approved' THEN 'Medical document verified'
        WHEN 'Rejected' THEN 'Medical certificate does not cover the session date'
        ELSE NULL
        END
FROM tmp_att_plan p
         JOIN users u ON u.username = p.username
         JOIN courses c ON c.course_code = p.course_code
         JOIN attendance_sessions ats ON ats.course_id = c.course_id
    AND ats.component = p.component
    AND ats.session_no BETWEEN p.session_from AND p.session_to
         JOIN attendance_records ar ON ar.attendance_session_id = ats.attendance_session_id
    AND ar.student_id = u.user_id
WHERE p.medical_status IS NOT NULL;

DROP TEMPORARY TABLE tmp_att_plan;

-- Marks plan: every quiz, the assignment, the mid exam(s) and the end exam(s) of a student's
-- course are set to the marks below (all out of 100). Components a course does not have are ignored.
CREATE TEMPORARY TABLE tmp_mark_plan (
                                         username        VARCHAR(50) NOT NULL,
                                         course_code     VARCHAR(20) NOT NULL,
                                         quiz_mark       DECIMAL(5,2) NOT NULL,
                                         assignment_mark DECIMAL(5,2) NOT NULL,
                                         mid_mark        DECIMAL(5,2) NOT NULL,
                                         final_mark      DECIMAL(5,2) NOT NULL
);

INSERT INTO tmp_mark_plan (username, course_code, quiz_mark, assignment_mark, mid_mark, final_mark) VALUES
                                                                                                        -- M1 / M2: CA weight 30 -> pass mark 12.00
                                                                                                        ('tg2061', 'TCS2112', 40, 40, 40, 50),
                                                                                                        ('tg2062', 'TCS2112', 39, 39, 39, 60),
                                                                                                        -- M3 / M4: CA weight 50 -> pass mark 20.00
                                                                                                        ('tg2063', 'TCS2121', 40, 40, 40, 50),
                                                                                                        ('tg2064', 'TCS2121', 39, 39, 39, 60),
                                                                                                        -- M5 / M6: CA weight 40 -> pass mark 16.00
                                                                                                        ('tg2066', 'ICT2122', 40, 40, 40, 40),
                                                                                                        ('tg2068', 'ICT2122', 39, 39, 39, 60),
                                                                                                        -- M7: assignment not submitted (0), CA = 13.00 of 40
                                                                                                        ('tg2071', 'ICT2152', 50,  0, 40, 70),
                                                                                                        -- A3: excellent marks, but attendance is 73.33% -> still E*
                                                                                                        ('tg2063', 'ICT2152', 90, 90, 90, 90),
                                                                                                        -- A10: weak marks (CA 10.00 of 40) and attendance 53.33%
                                                                                                        ('tg2072', 'ICT2152', 30, 20, 25, 35),
                                                                                                        -- G1 - G5: grade ladder, CA 16.00 (G5: 24.00), different end-exam marks
                                                                                                        ('tg2073', 'ICT2142', 40, 40, 40, 10),
                                                                                                        ('tg2074', 'ICT2142', 40, 40, 40, 20),
                                                                                                        ('tg2076', 'ICT2142', 40, 40, 40, 28),
                                                                                                        ('tg2077', 'ICT2142', 40, 40, 40, 36),
                                                                                                        ('tg2078', 'ICT2142', 60, 60, 60, 65);

UPDATE marks m
    JOIN assessments a ON a.assessment_id = m.assessment_id
    JOIN courses c ON c.course_id = a.course_id
    JOIN users u ON u.user_id = m.student_id
    JOIN tmp_mark_plan p ON p.username = u.username AND p.course_code = c.course_code
SET m.mark = CASE
                 WHEN a.assessment_type IN ('Quiz1', 'Quiz2', 'Quiz3') THEN p.quiz_mark
                 WHEN a.assessment_type = 'Assignment' THEN p.assignment_mark
                 WHEN a.assessment_type IN ('Mid Exam Theory', 'Mid Exam Practical') THEN p.mid_mark
                 WHEN a.assessment_type IN ('End Exam Theory', 'End Exam Practical') THEN p.final_mark
                 ELSE m.mark
    END;

DROP TEMPORARY TABLE tmp_mark_plan;

-- Set sample profile picture locations
UPDATE users SET profile_picture = CONCAT('uploads/profiles/', username, '.jpg') WHERE role = 'Undergraduate';

-- ============================================================================
-- END OF UNIMIS DATABASE SCRIPT
-- ============================================================================