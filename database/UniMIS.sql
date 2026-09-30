-- ============================================================
-- Unimis - Faculty of Technology Academic Management System
-- ICT2132 Object Oriented Programming Practicum Mini Project
-- MySQL 8.0+
--
-- Relational inheritance design:
--   users                  = User superclass
--   admins                 = Admin subclass
--   lecturers              = Lecturer subclass
--   technical_officers     = TechnicalOfficer subclass
--   undergraduates         = Undergraduate subclass
--
-- Java can map these as:
--   abstract/class User -> users
--   Admin -> users + admins
--   Lecturer -> users + lecturers
--   TechnicalOfficer -> users + technical_officers
--   Undergraduate -> users + undergraduates
-- ============================================================

-- ============================================================
-- 1. UniMis Database Creation
-- ============================================================

DROP DATABASE IF EXISTS unimis;

CREATE DATABASE unimis
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- 2. Use unimis Database
-- ============================================================

USE unimis;

-- ============================================================
-- 3. Each Table Creation
-- ============================================================

-- ------------------------------------------------------------
-- 3.1 Department
-- ------------------------------------------------------------
CREATE TABLE departments (
                             department_id INT AUTO_INCREMENT PRIMARY KEY,
                             department_code VARCHAR(20) NOT NULL UNIQUE,
                             department_name VARCHAR(150) NOT NULL UNIQUE,
                             description VARCHAR(500),
                             status ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.2 User superclass
-- ------------------------------------------------------------
CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(150) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       contact_no VARCHAR(20),
                       address VARCHAR(255),
                       profile_picture VARCHAR(500),
                       department_id INT NULL,
                       user_status ENUM('Active','Inactive','Suspended') NOT NULL DEFAULT 'Active',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT fk_users_department
                           FOREIGN KEY (department_id)
                               REFERENCES departments(department_id)
                               ON UPDATE CASCADE
                               ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.3 Admin subclass
-- ------------------------------------------------------------
CREATE TABLE admins (
                        user_id BIGINT PRIMARY KEY,
                        admin_code VARCHAR(30) NOT NULL UNIQUE,
                        designation VARCHAR(100) DEFAULT 'System Administrator',

                        CONSTRAINT fk_admins_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(user_id)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.4 Lecturer subclass
-- ------------------------------------------------------------
CREATE TABLE lecturers (
                           user_id BIGINT PRIMARY KEY,
                           lecturer_code VARCHAR(30) NOT NULL UNIQUE,
                           title VARCHAR(30) DEFAULT 'Lecturer',
                           specialization VARCHAR(150),

                           CONSTRAINT fk_lecturers_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(user_id)
                                   ON UPDATE CASCADE
                                   ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.5 Technical Officer subclass
-- ------------------------------------------------------------
CREATE TABLE technical_officers (
                                    user_id BIGINT PRIMARY KEY,
                                    technical_officer_code VARCHAR(30) NOT NULL UNIQUE,
                                    designation VARCHAR(100) DEFAULT 'Technical Officer',

                                    CONSTRAINT fk_technical_officers_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES users(user_id)
                                            ON UPDATE CASCADE
                                            ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.6 Undergraduate subclass
-- ------------------------------------------------------------
CREATE TABLE undergraduates (
                                user_id BIGINT PRIMARY KEY,
                                registration_no VARCHAR(30) NOT NULL UNIQUE,
                                index_no VARCHAR(30) UNIQUE,
                                batch_year YEAR NOT NULL,
                                academic_year VARCHAR(20) DEFAULT '2026',
                                student_type ENUM('Proper','Repeat','Batch Missed')
                                                            NOT NULL DEFAULT 'Proper',
                                intake VARCHAR(50),
                                current_level TINYINT UNSIGNED DEFAULT 2,
                                current_semester TINYINT UNSIGNED DEFAULT 1,

    -- Previous academic information used for CGPA
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
                                    CHECK (
                                        previous_credits >= 0
                                            AND previous_grade_points >= 0
                                        )
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.7 Course
-- ------------------------------------------------------------
CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         course_code VARCHAR(20) NOT NULL UNIQUE,
                         course_name VARCHAR(200) NOT NULL,
                         description TEXT,

                         theory_credits DECIMAL(3,1) NOT NULL DEFAULT 0.0,
                         practical_credits DECIMAL(3,1) NOT NULL DEFAULT 0.0,

    -- Total credits = theory + practical
                         total_credits DECIMAL(4,1)
                             GENERATED ALWAYS AS (theory_credits + practical_credits) STORED,

                         ca_weight DECIMAL(5,2) NOT NULL DEFAULT 40.00,
                         final_weight DECIMAL(5,2)
                             GENERATED ALWAYS AS (100.00 - ca_weight) STORED,

                         department_id INT NOT NULL,
                         academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
                         semester ENUM('Semester I','Semester II') NOT NULL DEFAULT 'Semester I',
                         status ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_courses_department
                             FOREIGN KEY (department_id)
                                 REFERENCES departments(department_id)
                                 ON UPDATE CASCADE
                                 ON DELETE RESTRICT,

                         CONSTRAINT chk_course_credits
                             CHECK (
                                 theory_credits >= 0
                                     AND practical_credits >= 0
                                     AND theory_credits + practical_credits > 0
                                 ),

                         CONSTRAINT chk_course_weights
                             CHECK (ca_weight >= 0 AND ca_weight <= 100)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.8 Lecturer-Course assignment
-- Allows more than one lecturer for a course if needed.
-- ------------------------------------------------------------
CREATE TABLE course_lecturers (
                                  course_id INT NOT NULL,
                                  lecturer_id BIGINT NOT NULL,
                                  responsibility ENUM(
                                      'Coordinator',
                                      'Lecturer',
                                      'Practical Instructor'
                                      ) NOT NULL DEFAULT 'Lecturer',
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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.9 Course Materials
-- ------------------------------------------------------------
CREATE TABLE course_materials (
                                  material_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  course_id INT NOT NULL,
                                  lecturer_id BIGINT NOT NULL,

                                  title VARCHAR(200) NOT NULL,
                                  description TEXT,
                                  material_type ENUM(
                                      'Lecture Note',
                                      'Slide',
                                      'PDF',
                                      'Document',
                                      'Video',
                                      'Link',
                                      'Other'
                                      ) NOT NULL DEFAULT 'Document',

                                  file_path VARCHAR(500),
                                  external_url VARCHAR(500),

                                  uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.10 Undergraduate Course Enrollment
-- ------------------------------------------------------------
CREATE TABLE enrollments (
                             enrollment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             student_id BIGINT NOT NULL,
                             course_id INT NOT NULL,

                             academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
                             semester ENUM('Semester I','Semester II')
                                 NOT NULL DEFAULT 'Semester I',

                             enrollment_type ENUM(
                                 'Normal',
                                 'Repeat',
                                 'Batch Missed'
                                 ) NOT NULL DEFAULT 'Normal',

                             enrollment_status ENUM(
                                 'Enrolled',
                                 'Withdrawn',
                                 'Completed'
                                 ) NOT NULL DEFAULT 'Enrolled',

                             enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             UNIQUE KEY uq_student_course_semester (
                                                                    student_id,
                                                                    course_id,
                                                                    academic_year,
                                                                    semester
                                 ),

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.11 Assessment definitions
--
-- Examples:
--   CA Quiz 1
--   CA Assignment
--   midterm
--   Practical
--   Final Examination
--
-- All assessment marks are out of 100.
-- weight_percent controls contribution to the course total.
-- ------------------------------------------------------------
CREATE TABLE assessments (
                             assessment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             course_id INT NOT NULL,

                             assessment_name VARCHAR(150) NOT NULL,

                             assessment_category ENUM(
                                 'CA',
                                 'FINAL'
                                 ) NOT NULL,

                             assessment_type ENUM(
                                 'Quiz',
                                 'Assignment',
                                 'Midterm',
                                 'Practical',
                                 'Presentation',
                                 'Project',
                                 'Final Examination',
                                 'Other'
                                 ) NOT NULL,

                             component VARCHAR(30) DEFAULT 'Theory',

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.12 Student Marks
-- One mark per student per assessment.
-- mark is always stored on a 100-point scale.
-- ------------------------------------------------------------
CREATE TABLE marks (
                       mark_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       assessment_id BIGINT NOT NULL,
                       student_id BIGINT NOT NULL,

                       mark DECIMAL(5,2) NOT NULL,

                       entered_by BIGINT NULL,
                       entered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       UNIQUE KEY uq_student_assessment (
                                                         assessment_id,
                                                         student_id
                           ),

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.13 Attendance Sessions
--
-- Theory and Practical are separate components.
-- One session represents the attendance event for a course.
-- ------------------------------------------------------------
CREATE TABLE attendance_sessions (
                                     attendance_session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     course_id INT NOT NULL,

                                     component ENUM(
                                         'Theory',
                                         'Practical'
                                         ) NOT NULL,

                                     session_no INT UNSIGNED NOT NULL,
                                     session_date DATE NOT NULL,
                                     start_time TIME,
                                     end_time TIME,

                                     duration_hours DECIMAL(4,2) NOT NULL DEFAULT 2.00,

                                     conducted_by BIGINT NULL,
                                     topic VARCHAR(255),

                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     UNIQUE KEY uq_course_component_session (
                                                                             course_id,
                                                                             component,
                                                                             session_no
                                         ),

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.14 Attendance Records
--
-- P = Present
-- A = Absent
-- M = Medical
-- L = Late
-- ------------------------------------------------------------
CREATE TABLE attendance_records (
                                    attendance_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                    attendance_session_id BIGINT NOT NULL,
                                    student_id BIGINT NOT NULL,

                                    attendance_status ENUM(
                                        'PRESENT',
                                        'ABSENT',
                                        'MEDICAL',
                                        'LATE'
                                        ) NOT NULL DEFAULT 'ABSENT',

                                    remarks VARCHAR(255),

                                    recorded_by BIGINT NULL,
                                    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP,

                                    UNIQUE KEY uq_session_student (
                                                                   attendance_session_id,
                                                                   student_id
                                        ),

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.15 Medical Records
--
-- A medical can be Pending / Approved / Rejected.
-- approved medical attendance can be included in the
-- adjusted attendance calculation.
-- ------------------------------------------------------------
CREATE TABLE medicals (
                          medical_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                          student_id BIGINT NOT NULL,
                          course_id INT NOT NULL,
                          attendance_record_id BIGINT NULL,

                          medical_date DATE NOT NULL,

                          reason VARCHAR(500),
                          document_path VARCHAR(500),

                          approval_status ENUM(
                              'Pending',
                              'Approved',
                              'Rejected'
                              ) NOT NULL DEFAULT 'Pending',

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.16 Notices
-- ------------------------------------------------------------
CREATE TABLE notices (
                         notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                         title VARCHAR(250) NOT NULL,
                         content TEXT NOT NULL,

                         notice_type ENUM(
                             'General',
                             'Academic',
                             'Exam',
                             'Attendance',
                             'Event',
                             'Emergency'
                             ) NOT NULL DEFAULT 'General',

                         target_role ENUM(
                             'All',
                             'Admin',
                             'Lecturer',
                             'Technical Officer',
                             'Undergraduate'
                             ) NOT NULL DEFAULT 'All',

                         department_id INT NULL,

                         published_by BIGINT NULL,

                         published_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         expires_at DATETIME NULL,

                         status ENUM(
                             'Draft',
                             'Published',
                             'Archived'
                             ) NOT NULL DEFAULT 'Published',

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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.17 Timetable
--
-- The UNIQUE constraint prevents two entries for the same
-- department/day/time/venue. The application should also check
-- lecturer clashes and batch clashes before inserting.
-- ------------------------------------------------------------
CREATE TABLE timetable_entries (
                                   timetable_id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                   department_id INT NOT NULL,
                                   course_id INT NOT NULL,
                                   lecturer_id BIGINT NULL,

                                   batch_year YEAR NOT NULL,

                                   component ENUM(
                                       'Theory',
                                       'Practical'
                                       ) NOT NULL,

                                   day_of_week ENUM(
                                       'Monday',
                                       'Tuesday',
                                       'Wednesday',
                                       'Thursday',
                                       'Friday',
                                       'Saturday',
                                       'Sunday'
                                       ) NOT NULL,

                                   start_time TIME NOT NULL,
                                   end_time TIME NOT NULL,

                                   venue VARCHAR(100) NOT NULL,

                                   academic_year VARCHAR(20) NOT NULL DEFAULT '2026',
                                   semester ENUM('Semester I','Semester II')
                                       NOT NULL DEFAULT 'Semester I',

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

                                   INDEX idx_timetable_batch_day (
                                                                  batch_year,
                                                                  day_of_week,
                                                                  start_time
                                       ),

                                   INDEX idx_timetable_venue_day (
                                                                  venue,
                                                                  day_of_week,
                                                                  start_time
                                       )
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.18 Grade Scale
-- Based on the grading scale used by the UI prototype.
-- Keep this configurable so the university can update it.
-- ------------------------------------------------------------
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
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3.19 System Settings
-- ------------------------------------------------------------
CREATE TABLE system_settings (
                                 setting_key VARCHAR(100) PRIMARY KEY,
                                 setting_value VARCHAR(500) NOT NULL,
                                 description VARCHAR(500)
) ENGINE=InnoDB;

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_users_role_lookup
    ON users(user_status, department_id);

CREATE INDEX idx_student_batch
    ON undergraduates(batch_year, student_type);

CREATE INDEX idx_courses_department
    ON courses(department_id, academic_year, semester);

CREATE INDEX idx_enrollments_student
    ON enrollments(student_id);

CREATE INDEX idx_enrollments_course
    ON enrollments(course_id);

CREATE INDEX idx_assessments_course_category
    ON assessments(course_id, assessment_category);

CREATE INDEX idx_marks_student
    ON marks(student_id);

CREATE INDEX idx_medicals_student_status
    ON medicals(student_id, approval_status);

CREATE INDEX idx_notices_target
    ON notices(target_role, status, published_at);

-- ============================================================
-- SEED DATA
-- Required by the project:
-- 01 Admin
-- >= 05 Lecturers
-- >= 04 Technical Officers
-- >= 20 Undergraduates
-- ============================================================

-- ------------------------------------------------------------
-- Departments
-- ------------------------------------------------------------
INSERT INTO departments
(department_code, department_name, description)
VALUES
    ('ICT', 'Department of Information and Communication Technology',
     'Faculty of Technology - ICT Department');

-- ------------------------------------------------------------
-- Grade scale
-- ------------------------------------------------------------
INSERT INTO grade_scales
(grade_code, minimum_mark, grade_point, description)
VALUES
    ('A+', 85, 4.00, 'Excellent'),
    ('A',  70, 4.00, 'Excellent'),
    ('A-', 65, 3.70, 'Very Good'),
    ('B+', 60, 3.30, 'Good'),
    ('B',  55, 3.00, 'Good'),
    ('B-', 50, 2.70, 'Good'),
    ('C+', 45, 2.30, 'Satisfactory'),
    ('C',  40, 2.00, 'Satisfactory'),
    ('C-', 35, 1.70, 'Pass'),
    ('D+', 30, 1.30, 'Weak Pass'),
    ('D',  25, 1.00, 'Weak Pass'),
    ('E',   0, 0.00, 'Fail');

-- ------------------------------------------------------------
-- System settings
-- ------------------------------------------------------------
INSERT INTO system_settings
(setting_key, setting_value, description)
VALUES
    ('UNIVERSITY_NAME', 'University of Ruhuna',
     'University name'),
    ('FACULTY_NAME', 'Faculty of Technology',
     'Faculty name'),
    ('ACADEMIC_YEAR', '2026',
     'Current academic year'),
    ('SEMESTER', 'Semester I',
     'Current semester'),
    ('MIN_ATTENDANCE_PERCENT', '80',
     'Minimum attendance percentage'),
    ('MIN_CA_PERCENT', '40',
     'Minimum CA percentage for final examination eligibility'),
    ('THEORY_SESSION_HOURS', '2',
     'Assumed hours for one theory session'),
    ('PRACTICAL_SESSION_HOURS', '2',
     'Assumed hours for one practical session'),
    ('TOTAL_THEORY_SESSIONS', '15',
     'Expected theory sessions per course'),
    ('TOTAL_PRACTICAL_SESSIONS', '15',
     'Expected practical sessions per course');

-- ------------------------------------------------------------
-- Users
--
-- NOTE:
-- These are demo passwords only.
-- In the Java application, store BCrypt/Argon2/PBKDF2 hashes,
-- not plaintext passwords.
-- ------------------------------------------------------------

-- Admin
INSERT INTO users
(username, password_hash, full_name, email, contact_no,
 address, department_id)
VALUES
    ('admin', 'admin123', 'System Admin',
     'admin@fot.ruh.ac.lk', '0412222222',
     'Faculty of Technology, University of Ruhuna',
     1);

INSERT INTO admins
(user_id, admin_code, designation)
VALUES
    (LAST_INSERT_ID(), 'ADM001', 'System Administrator');

-- Lecturers
INSERT INTO users
(username, password_hash, full_name, email, contact_no,
 address, department_id)
VALUES
    ('lecturer1', 'lec@123', 'Dr. N. Perera',
     'lecturer1@fot.ruh.ac.lk', '0711000001',
     'Department of ICT, Faculty of Technology', 1),
    ('lecturer2', 'lec@123', 'Ms. S. Fernando',
     'lecturer2@fot.ruh.ac.lk', '0711000002',
     'Department of ICT, Faculty of Technology', 1),
    ('lecturer3', 'lec@123', 'Mr. A. Gunawardena',
     'lecturer3@fot.ruh.ac.lk', '0711000003',
     'Department of ICT, Faculty of Technology', 1),
    ('lecturer4', 'lec@123', 'Dr. R. Jayasuriya',
     'lecturer4@fot.ruh.ac.lk', '0711000004',
     'Department of ICT, Faculty of Technology', 1),
    ('lecturer5', 'lec@123', 'Ms. T. Karunaratne',
     'lecturer5@fot.ruh.ac.lk', '0711000005',
     'Department of ICT, Faculty of Technology', 1);

INSERT INTO lecturers
(user_id, lecturer_code, title, specialization)
SELECT
    user_id, 'LEC001', 'Dr.', 'Data Structures and Algorithms'
FROM users WHERE username = 'lecturer1';

INSERT INTO lecturers
(user_id, lecturer_code, title, specialization)
SELECT
    user_id, 'LEC002', 'Ms.', 'Database Systems'
FROM users WHERE username = 'lecturer2';

INSERT INTO lecturers
(user_id, lecturer_code, title, specialization)
SELECT
    user_id, 'LEC003', 'Mr.', 'Computer Networks'
FROM users WHERE username = 'lecturer3';

INSERT INTO lecturers
(user_id, lecturer_code, title, specialization)
SELECT
    user_id, 'LEC004', 'Dr.', 'Software Engineering'
FROM users WHERE username = 'lecturer4';

INSERT INTO lecturers
(user_id, lecturer_code, title, specialization)
SELECT
    user_id, 'LEC005', 'Ms.', 'Web and Application Development'
FROM users WHERE username = 'lecturer5';

-- Technical Officers
INSERT INTO users
(username, password_hash, full_name, email, contact_no,
 address, department_id)
VALUES
    ('to1', 'to@123', 'K. Silva',
     'to1@fot.ruh.ac.lk', '0712000001',
     'Department of ICT, Faculty of Technology', 1),
    ('to2', 'to@123', 'M. Wijeratne',
     'to2@fot.ruh.ac.lk', '0712000002',
     'Department of ICT, Faculty of Technology', 1),
    ('to3', 'to@123', 'D. Samarasinghe',
     'to3@fot.ruh.ac.lk', '0712000003',
     'Department of ICT, Faculty of Technology', 1),
    ('to4', 'to@123', 'P. Nandasena',
     'to4@fot.ruh.ac.lk', '0712000004',
     'Department of ICT, Faculty of Technology', 1);

INSERT INTO technical_officers
(user_id, technical_officer_code, designation)
SELECT user_id, 'TO001', 'Technical Officer'
FROM users WHERE username = 'to1';

INSERT INTO technical_officers
(user_id, technical_officer_code, designation)
SELECT user_id, 'TO002', 'Technical Officer'
FROM users WHERE username = 'to2';

INSERT INTO technical_officers
(user_id, technical_officer_code, designation)
SELECT user_id, 'TO003', 'Technical Officer'
FROM users WHERE username = 'to3';

INSERT INTO technical_officers
(user_id, technical_officer_code, designation)
SELECT user_id, 'TO004', 'Technical Officer'
FROM users WHERE username = 'to4';

-- Undergraduates: 20 students
INSERT INTO users
(username, password_hash, full_name, email, contact_no,
 address, department_id)
VALUES
    ('kasun1', 'ug@123', 'Kasun Madushan',
     'kasun1@fot.ruh.ac.lk', '0713000001', 'Matara, Sri Lanka', 1),
    ('nimali2', 'ug@123', 'Nimali Perera',
     'nimali2@fot.ruh.ac.lk', '0713000002', 'Galle, Sri Lanka', 1),
    ('ravindu3', 'ug@123', 'Ravindu Silva',
     'ravindu3@fot.ruh.ac.lk', '0713000003', 'Hambantota, Sri Lanka', 1),
    ('dilini4', 'ug@123', 'Dilini Jayawardena',
     'dilini4@fot.ruh.ac.lk', '0713000004', 'Colombo, Sri Lanka', 1),
    ('sahan5', 'ug@123', 'Sahan Wickramasinghe',
     'sahan5@fot.ruh.ac.lk', '0713000005', 'Kandy, Sri Lanka', 1),
    ('ishara6', 'ug@123', 'Ishara Gunasekara',
     'ishara6@fot.ruh.ac.lk', '0713000006', 'Matara, Sri Lanka', 1),
    ('tharindu7', 'ug@123', 'Tharindu Fernando',
     'tharindu7@fot.ruh.ac.lk', '0713000007', 'Galle, Sri Lanka', 1),
    ('amaya8', 'ug@123', 'Amaya Rathnayake',
     'amaya8@fot.ruh.ac.lk', '0713000008', 'Kandy, Sri Lanka', 1),
    ('chamod9', 'ug@123', 'Chamod Dissanayake',
     'chamod9@fot.ruh.ac.lk', '0713000009', 'Matara, Sri Lanka', 1),
    ('hasini10', 'ug@123', 'Hasini Ekanayake',
     'hasini10@fot.ruh.ac.lk', '0713000010', 'Galle, Sri Lanka', 1),
    ('pasindu11', 'ug@123', 'Pasindu Bandara',
     'pasindu11@fot.ruh.ac.lk', '0713000011', 'Kandy, Sri Lanka', 1),
    ('sanduni12', 'ug@123', 'Sanduni Herath',
     'sanduni12@fot.ruh.ac.lk', '0713000012', 'Matara, Sri Lanka', 1),
    ('nuwan13', 'ug@123', 'Nuwan Abeysekara',
     'nuwan13@fot.ruh.ac.lk', '0713000013', 'Galle, Sri Lanka', 1),
    ('piumi14', 'ug@123', 'Piumi Senanayake',
     'piumi14@fot.ruh.ac.lk', '0713000014', 'Colombo, Sri Lanka', 1),
    ('lahiru15', 'ug@123', 'Lahiru Weerasinghe',
     'lahiru15@fot.ruh.ac.lk', '0713000015', 'Kandy, Sri Lanka', 1),
    ('thilini16', 'ug@123', 'Thilini Kumari',
     'thilini16@fot.ruh.ac.lk', '0713000016', 'Matara, Sri Lanka', 1),
    ('janith17', 'ug@123', 'Janith Rajapaksha',
     'janith17@fot.ruh.ac.lk', '0713000017', 'Galle, Sri Lanka', 1),
    ('oshadi18', 'ug@123', 'Oshadi Wijesinghe',
     'oshadi18@fot.ruh.ac.lk', '0713000018', 'Hambantota, Sri Lanka', 1),
    ('chathura19', 'ug@123', 'Chathura Peiris',
     'chathura19@fot.ruh.ac.lk', '0713000019', 'Colombo, Sri Lanka', 1),
    ('menaka20', 'ug@123', 'Menaka Liyanage',
     'menaka20@fot.ruh.ac.lk', '0713000020', 'Kandy, Sri Lanka', 1);

INSERT INTO undergraduates
(user_id, registration_no, index_no, batch_year,
 academic_year, student_type, intake, current_level,
 current_semester, previous_credits, previous_grade_points,
 admission_date, guardian_name, guardian_contact)
SELECT user_id, CONCAT('TG/2024/', LPAD(ROW_NUMBER() OVER (ORDER BY user_id), 3, '0')),
       NULL, 2024, '2026', 'Proper', '2024', 2, 1,
       15, 45, '2024-09-01', NULL, NULL
FROM users
WHERE username IN (
                   'kasun1','nimali2','ravindu3','dilini4','sahan5',
                   'ishara6','tharindu7','amaya8','chamod9','hasini10',
                   'pasindu11','sanduni12','nuwan13','piumi14','lahiru15',
                   'thilini16','janith17','oshadi18','chathura19','menaka20'
    );

-- Correct required student types after insertion
UPDATE undergraduates
SET student_type = 'Repeat'
WHERE registration_no IN ('TG/2024/018','TG/2024/019');

UPDATE undergraduates
SET student_type = 'Batch Missed',
    batch_year = 2023
WHERE registration_no = 'TG/2024/020';

-- ------------------------------------------------------------
-- Courses
-- ------------------------------------------------------------
INSERT INTO courses
(course_code, course_name, description,
 theory_credits, practical_credits, ca_weight,
 department_id, academic_year, semester)
VALUES
    ('ICT2112', 'Data Structures and Algorithms',
     'Data structures, algorithms and problem solving.',
     2.0, 0.0, 40.0, 1, '2026', 'Semester I'),

    ('ICT2122', 'Computer Networks',
     'Computer networking concepts and practical networking.',
     1.0, 1.0, 40.0, 1, '2026', 'Semester I'),

    ('ICT2132', 'Object Oriented Programming Practicum',
     'Java, OOP, GUI and database handling practicum.',
     1.0, 1.0, 50.0, 1, '2026', 'Semester I'),

    ('ICT2142', 'Database Management Systems',
     'Database design, SQL and database management.',
     2.0, 1.0, 40.0, 1, '2026', 'Semester I'),

    ('ICT2152', 'Web Application Development',
     'Client and server side web application development.',
     1.0, 1.0, 50.0, 1, '2026', 'Semester I'),

    ('ICT2162', 'Software Engineering',
     'Software engineering principles and development methods.',
     2.0, 0.0, 30.0, 1, '2026', 'Semester I'),

    ('ICT2172', 'Probability and Statistics',
     'Probability, statistics and data analysis.',
     2.0, 0.0, 30.0, 1, '2026', 'Semester I'),

    ('ICT2182', 'Technical Communication Skills',
     'Professional and technical communication.',
     1.0, 0.0, 50.0, 1, '2026', 'Semester I');

-- ------------------------------------------------------------
-- Lecturer assignments
-- ------------------------------------------------------------
INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2112';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC003'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2122';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC001'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2132';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC002'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2142';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC005'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2152';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC004'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2162';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC004'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2172';

INSERT INTO course_lecturers (course_id, lecturer_id, responsibility)
SELECT course_id,
       (SELECT user_id FROM lecturers WHERE lecturer_code = 'LEC005'),
       'Coordinator'
FROM courses WHERE course_code = 'ICT2182';

-- ------------------------------------------------------------
-- Enroll all 20 students in courses.
-- Batch-missed/repeat exceptions are demonstrated.
-- ------------------------------------------------------------
INSERT INTO enrollments
(student_id, course_id, academic_year, semester, enrollment_type)
SELECT
    u.user_id,
    c.course_id,
    '2026',
    'Semester I',
    CASE
        WHEN u.student_type = 'Repeat' THEN 'Repeat'
        WHEN u.student_type = 'Batch Missed' THEN 'Batch Missed'
        ELSE 'Normal'
        END
FROM undergraduates u
         CROSS JOIN courses c
WHERE NOT (
    (u.student_type = 'Batch Missed'
        AND MOD(c.course_id, 3) = 0)
        OR
    (u.student_type = 'Repeat'
        AND c.course_code = 'ICT2182')
    );

-- ------------------------------------------------------------
-- Assessment definitions
--
-- These are examples and can be changed according to the
-- actual course coordinator evaluation criteria.
-- Every mark entered is out of 100.
-- ------------------------------------------------------------

-- ICT2112 CA 40%, Final 60%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Assignment', 'Theory', 40.00, 100
FROM courses WHERE course_code = 'ICT2112';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 60.00, 100
FROM courses WHERE course_code = 'ICT2112';

-- ICT2122 CA 40%, Final 60%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Practical', 'Practical', 40.00, 100
FROM courses WHERE course_code = 'ICT2122';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 60.00, 100
FROM courses WHERE course_code = 'ICT2122';

-- ICT2132 CA 50%, Final 50%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Practical', 'Practical', 50.00, 100
FROM courses WHERE course_code = 'ICT2132';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 50.00, 100
FROM courses WHERE course_code = 'ICT2132';

-- ICT2142 CA 40%, Final 60%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Assignment', 'Theory', 40.00, 100
FROM courses WHERE course_code = 'ICT2142';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 60.00, 100
FROM courses WHERE course_code = 'ICT2142';

-- ICT2152 CA 50%, Final 50%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Project', 'Practical', 50.00, 100
FROM courses WHERE course_code = 'ICT2152';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 50.00, 100
FROM courses WHERE course_code = 'ICT2152';

-- ICT2162 CA 30%, Final 70%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Assignment', 'Theory', 30.00, 100
FROM courses WHERE course_code = 'ICT2162';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 70.00, 100
FROM courses WHERE course_code = 'ICT2162';

-- ICT2172 CA 30%, Final 70%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Assignment', 'Theory', 30.00, 100
FROM courses WHERE course_code = 'ICT2172';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 70.00, 100
FROM courses WHERE course_code = 'ICT2172';

-- ICT2182 CA 50%, Final 50%
INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Continuous Assessment', 'CA',
       'Presentation', 'Theory', 50.00, 100
FROM courses WHERE course_code = 'ICT2182';

INSERT INTO assessments
(course_id, assessment_name, assessment_category,
 assessment_type, component, weight_percent, max_mark)
SELECT course_id, 'Final Examination', 'FINAL',
       'Final Examination', 'Theory', 50.00, 100
FROM courses WHERE course_code = 'ICT2182';

-- ============================================================
-- VIEWS
-- These views make Java SELECT queries much easier.
-- ============================================================

-- ------------------------------------------------------------
-- V1. Complete user list with role
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_users AS
SELECT
    u.user_id,
    u.username,
    u.full_name,
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
         LEFT JOIN departments d
                   ON u.department_id = d.department_id
         LEFT JOIN admins a
                   ON u.user_id = a.user_id
         LEFT JOIN lecturers l
                   ON u.user_id = l.user_id
         LEFT JOIN technical_officers t
                   ON u.user_id = t.user_id
         LEFT JOIN undergraduates s
                   ON u.user_id = s.user_id;

-- ------------------------------------------------------------
-- V2. Attendance summary
--
-- raw attendance:
--   Present + Late / total sessions
--
-- adjusted attendance:
--   Present + Late + approved medical
--   divided by total sessions
--
-- A medical is only added after approval.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_attendance_summary AS
SELECT
    e.student_id,
    u.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,

    COUNT(ar.attendance_record_id) AS total_sessions,

    SUM(
            CASE
                WHEN ar.attendance_status IN ('PRESENT','LATE')
                    THEN 1 ELSE 0
                END
    ) AS attended_sessions,

    SUM(
            CASE
                WHEN ar.attendance_status = 'MEDICAL'
                    THEN 1 ELSE 0
                END
    ) AS medical_sessions,

    SUM(
            CASE
                WHEN ar.attendance_status = 'ABSENT'
                    THEN 1 ELSE 0
                END
    ) AS absent_sessions,

    SUM(
            CASE
                WHEN ar.attendance_status IN ('PRESENT','LATE')
                    THEN 1 ELSE 0
                END
    ) * 100.0
        / NULLIF(COUNT(ar.attendance_record_id), 0)
                                   AS raw_attendance_percent,

    (
        SUM(
                CASE
                    WHEN ar.attendance_status IN ('PRESENT','LATE')
                        THEN 1 ELSE 0
                    END
        )
            +
        SUM(
                CASE
                    WHEN ar.attendance_status = 'MEDICAL'
                        AND EXISTS (
                            SELECT 1
                            FROM medicals m
                            WHERE m.attendance_record_id =
                                  ar.attendance_record_id
                              AND m.approval_status = 'Approved'
                        )
                        THEN 1 ELSE 0
                    END
        )
        ) * 100.0
        / NULLIF(COUNT(ar.attendance_record_id), 0)
                                   AS adjusted_attendance_percent

FROM enrollments e
         JOIN undergraduates u
              ON e.student_id = u.user_id
         JOIN users usr
              ON u.user_id = usr.user_id
         JOIN courses c
              ON e.course_id = c.course_id
         LEFT JOIN attendance_sessions ats
                   ON ats.course_id = c.course_id
         LEFT JOIN attendance_records ar
                   ON ar.attendance_session_id = ats.attendance_session_id
                       AND ar.student_id = e.student_id

GROUP BY
    e.student_id,
    u.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name;

-- ------------------------------------------------------------
-- V3. Attendance by component
-- Theory / Practical
-- ------------------------------------------------------------
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

    SUM(
            CASE
                WHEN ar.attendance_status IN ('PRESENT','LATE')
                    THEN 1 ELSE 0
                END
    ) AS attended_sessions,

    SUM(
            CASE
                WHEN ar.attendance_status = 'MEDICAL'
                    THEN 1 ELSE 0
                END
    ) AS medical_sessions,

    SUM(
            CASE
                WHEN ar.attendance_status = 'ABSENT'
                    THEN 1 ELSE 0
                END
    ) AS absent_sessions,

    ROUND(
            SUM(
                    CASE
                        WHEN ar.attendance_status IN ('PRESENT','LATE')
                            THEN 1 ELSE 0
                        END
            ) * 100.0 / NULLIF(COUNT(*),0),
            2
    ) AS raw_attendance_percent,

    ROUND(
            (
                SUM(
                        CASE
                            WHEN ar.attendance_status IN ('PRESENT','LATE')
                                THEN 1 ELSE 0
                            END
                )
                    +
                SUM(
                        CASE
                            WHEN ar.attendance_status = 'MEDICAL'
                                AND EXISTS (
                                    SELECT 1
                                    FROM medicals m
                                    WHERE m.attendance_record_id =
                                          ar.attendance_record_id
                                      AND m.approval_status = 'Approved'
                                )
                                THEN 1 ELSE 0
                            END
                )
                ) * 100.0 / NULLIF(COUNT(*),0),
            2
    ) AS adjusted_attendance_percent

FROM attendance_records ar
         JOIN attendance_sessions ats
              ON ar.attendance_session_id = ats.attendance_session_id
         JOIN courses c
              ON ats.course_id = c.course_id
         JOIN undergraduates ug
              ON ar.student_id = ug.user_id
         JOIN users usr
              ON ug.user_id = usr.user_id

GROUP BY
    ar.student_id,
    ug.registration_no,
    usr.full_name,
    ats.course_id,
    c.course_code,
    c.course_name,
    ats.component;

-- ------------------------------------------------------------
-- V4. CA marks summary
-- ------------------------------------------------------------
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
            COALESCE(
                    SUM(
                            CASE
                                WHEN a.assessment_category = 'CA'
                                    THEN m.mark * a.weight_percent / 100
                                ELSE 0
                                END
                    ),
                    0
            ),
            2
    ) AS ca_mark,

    CASE
        WHEN
            COALESCE(
                    SUM(
                            CASE
                                WHEN a.assessment_category = 'CA'
                                    THEN m.mark * a.weight_percent / 100
                                ELSE 0
                                END
                    ),
                    0
            ) >= 40
            THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS ca_eligibility

FROM enrollments e
         JOIN undergraduates ug
              ON e.student_id = ug.user_id
         JOIN users usr
              ON ug.user_id = usr.user_id
         JOIN courses c
              ON e.course_id = c.course_id

         LEFT JOIN assessments a
                   ON a.course_id = c.course_id
                       AND a.assessment_category = 'CA'

         LEFT JOIN marks m
                   ON m.assessment_id = a.assessment_id
                       AND m.student_id = e.student_id

GROUP BY
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name,
    c.ca_weight;

-- ------------------------------------------------------------
-- V5. Final examination marks
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_final_exam_marks AS
SELECT
    e.student_id,
    ug.registration_no,
    usr.full_name,

    e.course_id,
    c.course_code,
    c.course_name,

    ROUND(
            COALESCE(
                    SUM(
                            CASE
                                WHEN a.assessment_category = 'FINAL'
                                    THEN m.mark * a.weight_percent / 100
                                ELSE 0
                                END
                    ),
                    0
            ),
            2
    ) AS final_exam_contribution

FROM enrollments e
         JOIN undergraduates ug
              ON e.student_id = ug.user_id
         JOIN users usr
              ON ug.user_id = usr.user_id
         JOIN courses c
              ON e.course_id = c.course_id

         LEFT JOIN assessments a
                   ON a.course_id = c.course_id
                       AND a.assessment_category = 'FINAL'

         LEFT JOIN marks m
                   ON m.assessment_id = a.assessment_id
                       AND m.student_id = e.student_id

GROUP BY
    e.student_id,
    ug.registration_no,
    usr.full_name,
    e.course_id,
    c.course_code,
    c.course_name;

-- ------------------------------------------------------------
-- V6. Complete eligibility
--
-- Attendance must be >= 80%
-- CA must be >= 40%
-- Both are required.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_eligibility AS
SELECT
    ca.student_id,
    ca.registration_no,
    ca.full_name,

    ca.course_id,
    ca.course_code,
    ca.course_name,

    ROUND(
            COALESCE(att.adjusted_attendance_percent, 0),
            2
    ) AS attendance_percent,

    ROUND(ca.ca_mark, 2) AS ca_mark,

    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80
            AND ca.ca_mark >= 40
            THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS final_exam_eligibility,

    CASE
        WHEN COALESCE(att.adjusted_attendance_percent, 0) >= 80
            THEN 'Pass Attendance Requirement'
        ELSE 'Fail Attendance Requirement'
        END AS attendance_requirement,

    CASE
        WHEN ca.ca_mark >= 40
            THEN 'Pass CA Requirement'
        ELSE 'Fail CA Requirement'
        END AS ca_requirement

FROM v_ca_summary ca

         LEFT JOIN v_attendance_summary att
                   ON att.student_id = ca.student_id
                       AND att.course_id = ca.course_id;

-- ------------------------------------------------------------
-- V7. Final course results
-- ------------------------------------------------------------
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

    ROUND(
            COALESCE(fe.final_exam_contribution, 0),
            2
    ) AS final_exam_mark,

    ROUND(
            COALESCE(ca.ca_mark, 0)
                + COALESCE(fe.final_exam_contribution, 0),
            2
    ) AS final_mark,

    COALESCE(
            att.adjusted_attendance_percent,
            0
    ) AS attendance_percent,

    CASE
        WHEN
            COALESCE(att.adjusted_attendance_percent, 0) >= 80
                AND COALESCE(ca.ca_mark, 0) >= 40
            THEN 'Eligible'
        ELSE 'Not Eligible'
        END AS eligibility,

    (
        SELECT gs.grade_code
        FROM grade_scales gs
        WHERE gs.minimum_mark <=
              ROUND(
                      COALESCE(ca.ca_mark, 0)
                          + COALESCE(fe.final_exam_contribution, 0),
                      2
              )
        ORDER BY gs.minimum_mark DESC
        LIMIT 1
    ) AS grade,

    (
        SELECT gs.grade_point
        FROM grade_scales gs
        WHERE gs.minimum_mark <=
              ROUND(
                      COALESCE(ca.ca_mark, 0)
                          + COALESCE(fe.final_exam_contribution, 0),
                      2
              )
        ORDER BY gs.minimum_mark DESC
        LIMIT 1
    ) AS grade_point

FROM enrollments e

         JOIN undergraduates ug
              ON e.student_id = ug.user_id

         JOIN users usr
              ON ug.user_id = usr.user_id

         JOIN courses c
              ON e.course_id = c.course_id

         LEFT JOIN v_ca_summary ca
                   ON ca.student_id = e.student_id
                       AND ca.course_id = e.course_id

         LEFT JOIN v_final_exam_marks fe
                   ON fe.student_id = e.student_id
                       AND fe.course_id = e.course_id

         LEFT JOIN v_attendance_summary att
                   ON att.student_id = e.student_id
                       AND att.course_id = e.course_id;

-- ------------------------------------------------------------
-- V8. SGPA
-- SGPA = SUM(grade point * course credits)
--        / SUM(course credits)
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_sgpa AS
SELECT
    student_id,
    registration_no,
    full_name,

    ROUND(
            SUM(grade_point * total_credits)
                / NULLIF(SUM(total_credits), 0),
            2
    ) AS sgpa,

    SUM(total_credits) AS semester_credits,

    SUM(grade_point * total_credits) AS semester_grade_points

FROM v_final_results

GROUP BY
    student_id,
    registration_no,
    full_name;

-- ------------------------------------------------------------
-- V9. CGPA
--
-- Previous academic credits/points are stored in the
-- undergraduate table, then current semester is added.
-- ------------------------------------------------------------

CREATE OR REPLACE VIEW v_cgpa AS
SELECT
    s.student_id,
    s.registration_no,
    s.full_name,

    u.previous_credits,

    ROUND(
            u.previous_grade_points,
            2
    ) AS previous_grade_points,

    ROUND(
            s.sgpa,
            2
    ) AS current_sgpa,

    s.semester_credits,

    ROUND(
            u.previous_grade_points
                + s.semester_grade_points,
            2
    ) AS total_grade_points,

    ROUND(
            (
                u.previous_grade_points
                    + s.semester_grade_points
                )
                /
            NULLIF(
                    u.previous_credits
                        + s.semester_credits,
                    0
            ),
            2
    ) AS cgpa

FROM v_sgpa s

         JOIN undergraduates u
              ON s.student_id = u.user_id;

-- ------------------------------------------------------------
-- V10. Batch result summary
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW v_batch_results AS
SELECT
    u.batch_year,
    r.course_code,
    r.course_name,

    COUNT(*) AS student_count,

    ROUND(AVG(r.final_mark), 2) AS average_mark,

    SUM(
            CASE
                WHEN r.grade_point > 0
                    THEN 1 ELSE 0
                END
    ) AS passed_count,

    SUM(
            CASE
                WHEN r.grade_point = 0
                    THEN 1 ELSE 0
                END
    ) AS failed_count

FROM v_final_results r

         JOIN undergraduates u
              ON r.student_id = u.user_id

GROUP BY
    u.batch_year,
    r.course_code,
    r.course_name;

-- ============================================================
-- SAMPLE ATTENDANCE DATA GENERATION
--
-- 15 theory sessions and 15 practical sessions are created for
-- every course that has the respective component.
-- The INSERT below creates the sessions.
-- ============================================================

INSERT INTO attendance_sessions
(course_id, component, session_no, session_date,
 start_time, end_time, duration_hours, conducted_by, topic)
SELECT
    c.course_id,
    'Theory',
    n.n,
    DATE_ADD('2026-09-01', INTERVAL (n.n - 1) * 7 DAY),
    '08:00:00',
    '10:00:00',
    2.00,
    (
        SELECT cl.lecturer_id
        FROM course_lecturers cl
        WHERE cl.course_id = c.course_id
        ORDER BY
            CASE
                WHEN cl.responsibility = 'Coordinator' THEN 1
                ELSE 2
                END
        LIMIT 1
    ),
    CONCAT('Theory Session ', n.n)
FROM courses c
         CROSS JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
    UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
    UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
    UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
) n
WHERE c.theory_credits > 0;

INSERT INTO attendance_sessions
(course_id, component, session_no, session_date,
 start_time, end_time, duration_hours, conducted_by, topic)
SELECT
    c.course_id,
    'Practical',
    n.n,
    DATE_ADD('2026-09-03', INTERVAL (n.n - 1) * 7 DAY),
    '13:00:00',
    '15:00:00',
    2.00,
    (
        SELECT cl.lecturer_id
        FROM course_lecturers cl
        WHERE cl.course_id = c.course_id
        ORDER BY
            CASE
                WHEN cl.responsibility = 'Coordinator' THEN 1
                ELSE 2
                END
        LIMIT 1
    ),
    CONCAT('Practical Session ', n.n)
FROM courses c
         CROSS JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
    UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
    UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
    UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
) n
WHERE c.practical_credits > 0;

-- ============================================================
-- DEMO ATTENDANCE
--
-- The Java application should normally create/update these
-- records. This section gives every enrolled student attendance
-- data so the UI can immediately display attendance.
--
-- Pattern:
--   student 1: >80%
--   student 2: exactly 80%
--   student 3: <80%, no medical
--   student 4: raw <80%, approved medicals can bring it above 80%
--   student 5: <80% even with medical
--   then the pattern repeats.
-- ============================================================

INSERT INTO attendance_records
(attendance_session_id, student_id, attendance_status, recorded_by)
SELECT
    ats.attendance_session_id,
    e.student_id,

    CASE
        WHEN MOD(
                     ROW_NUMBER() OVER (
                         PARTITION BY e.student_id, ats.course_id
                         ORDER BY ats.attendance_session_id
                         ), 10
             ) <= 8
            THEN 'PRESENT'
        ELSE 'ABSENT'
        END,

    (
        SELECT user_id
        FROM technical_officers
        ORDER BY user_id
        LIMIT 1
    )

FROM attendance_sessions ats
         JOIN enrollments e
              ON e.course_id = ats.course_id;

-- ============================================================
-- DEMO MEDICAL RECORDS
--
-- Add medical records for selected students.
-- These records demonstrate pending/approved/rejected cases.
-- ============================================================

INSERT INTO medicals
(student_id, course_id, attendance_record_id,
 medical_date, reason, approval_status, reviewed_by,
 reviewed_at, review_comment)

SELECT
    e.student_id,
    e.course_id,
    ar.attendance_record_id,
    ats.session_date,
    'Medical leave - approved demonstration record',
    'Approved',
    (
        SELECT user_id
        FROM technical_officers
        ORDER BY user_id
        LIMIT 1
    ),
    NOW(),
    'Medical document checked and approved'

FROM enrollments e
         JOIN attendance_sessions ats
              ON ats.course_id = e.course_id
         JOIN attendance_records ar
              ON ar.attendance_session_id = ats.attendance_session_id
                  AND ar.student_id = e.student_id

WHERE ar.attendance_status = 'ABSENT'
  AND MOD(e.student_id, 5) = 0
LIMIT 10;

-- ============================================================
-- DEMO MARKS
--
-- Gives each existing assessment a mark for enrolled students.
-- The application can replace these with actual marks.
-- ============================================================

INSERT INTO marks
(assessment_id, student_id, mark, entered_by)

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

    (
        SELECT cl.lecturer_id
        FROM course_lecturers cl
        WHERE cl.course_id = a.course_id
        ORDER BY
            CASE
                WHEN cl.responsibility = 'Coordinator' THEN 1
                ELSE 2
                END
        LIMIT 1
    )

FROM assessments a
         JOIN enrollments e
              ON e.course_id = a.course_id;

-- ============================================================
-- DEMO NOTICES
-- ============================================================

INSERT INTO notices
(title, content, notice_type, target_role,
 department_id, published_by, status)
VALUES
    (
        'Semester I Academic Notice',
        'Students are requested to regularly check attendance,
         course and examination information.',
        'Academic',
        'All',
        1,
        (SELECT user_id FROM admins WHERE admin_code = 'ADM001'),
        'Published'
    ),
    (
        'Attendance Eligibility Reminder',
        'Students must maintain the required attendance percentage
         according to the academic regulations.',
        'Attendance',
        'Undergraduate',
        1,
        (SELECT user_id FROM admins WHERE admin_code = 'ADM001'),
        'Published'
    ),
    (
        'CA Eligibility Notice',
        'Continuous Assessment marks must satisfy the minimum
         eligibility requirement before the final examination.',
        'Exam',
        'Undergraduate',
        1,
        (SELECT user_id FROM admins WHERE admin_code = 'ADM001'),
        'Published'
    );

-- ============================================================
-- DEMO TIMETABLE
-- Application-level clash checking should be performed before
-- insertion/update.
-- ============================================================

INSERT INTO timetable_entries
(department_id, course_id, lecturer_id, batch_year,
 component, day_of_week, start_time, end_time,
 venue, academic_year, semester)

SELECT
    1,
    c.course_id,
    cl.lecturer_id,
    2024,
    'Theory',
    CASE MOD(c.course_id, 5)
        WHEN 0 THEN 'Monday'
        WHEN 1 THEN 'Tuesday'
        WHEN 2 THEN 'Wednesday'
        WHEN 3 THEN 'Thursday'
        ELSE 'Friday'
        END,
    '08:00:00',
    '10:00:00',
    CONCAT('ICT-', 100 + c.course_id),
    '2026',
    'Semester I'

FROM courses c

         JOIN course_lecturers cl
              ON cl.course_id = c.course_id
                  AND cl.responsibility = 'Coordinator';

-- ============================================================
-- USEFUL QUERIES FOR JAVA DEVELOPMENT
-- ============================================================

-- Login
-- SELECT * FROM v_users
-- WHERE username = ? AND user_status = 'Active';

-- Get Admins
-- SELECT * FROM v_users WHERE role = 'Admin';

-- Get Lecturers
-- SELECT * FROM v_users WHERE role = 'Lecturer';

-- Get Technical Officers
-- SELECT * FROM v_users WHERE role = 'Technical Officer';

-- Get Undergraduates
-- SELECT * FROM v_users WHERE role = 'Undergraduate';

-- Student attendance
-- SELECT *
-- FROM v_attendance_summary
-- WHERE student_id = ?;

-- Theory attendance
-- SELECT *
-- FROM v_attendance_component_summary
-- WHERE student_id = ?
--   AND course_id = ?
--   AND component = 'Theory';

-- Practical attendance
-- SELECT *
-- FROM v_attendance_component_summary
-- WHERE student_id = ?
--   AND course_id = ?
--   AND component = 'Practical';

-- Student CA marks
-- SELECT *
-- FROM v_ca_summary
-- WHERE student_id = ?;

-- Student eligibility
-- SELECT *
-- FROM v_eligibility
-- WHERE student_id = ?;

-- Student final results
-- SELECT *
-- FROM v_final_results
-- WHERE student_id = ?;

-- Student SGPA
-- SELECT *
-- FROM v_sgpa
-- WHERE student_id = ?;

-- Student CGPA
-- SELECT *
-- FROM v_cgpa
-- WHERE student_id = ?;

-- Whole batch results
-- SELECT *
-- FROM v_batch_results
-- WHERE batch_year = 2024;

-- ============================================================
-- END OF unimis DATABASE SCRIPT
-- ============================================================