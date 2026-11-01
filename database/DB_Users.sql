-- ============================================================================
-- UNIMIS - Database Users & Privileges
-- ============================================================================
-- Run this AFTER UniMis.sql (the tables and views must already exist), while
-- connected as root or another account that is allowed to use GRANT.
-- The script can be re-run safely: it first drops any of these accounts that
-- already exist, then re-creates them and grants the privileges below.
--
-- Accounts
--   unimisadmin   Admin
--   unimislec     Lecturer
--   unimisto      Technical Officer
--   unimisug      Undergraduate
--   unimisauth    Login / authentication service (login + forgot-password only)
--
-- NOTE: MySQL privileges work at table / column level, not row level.
--       "Own data only" (a student seeing only their own marks, a technical
--       officer seeing only their department's timetable, etc.) must be done
--       in the Java code with WHERE user_id = ? / department_id = ?.
--
-- The grade/GPA/eligibility views are created with the default
-- SQL SECURITY DEFINER, so a role can read them without having direct access
-- to the underlying marks / assessments tables.
-- ============================================================================


-- ----------------------------------------------------------------------------
-- 1. DROP EXISTING USERS, THEN RE-CREATE THEM
--    (host = localhost; change to '%' if the app connects remotely)
-- ----------------------------------------------------------------------------
-- Dropping an account also removes every privilege it had, so the accounts
-- below always end up with exactly the grants in this script.
DROP USER IF EXISTS 'unimisadmin'@'localhost';
DROP USER IF EXISTS 'unimislec'@'localhost';
DROP USER IF EXISTS 'unimisto'@'localhost';
DROP USER IF EXISTS 'unimisug'@'localhost';
DROP USER IF EXISTS 'unimisauth'@'localhost';

CREATE USER 'unimisadmin'@'localhost' IDENTIFIED BY 'Admin@Unimis@123';
CREATE USER 'unimislec'@'localhost'   IDENTIFIED BY 'Lec@Unimis@123';
CREATE USER 'unimisto'@'localhost'    IDENTIFIED BY 'To@Unimis@123';
CREATE USER 'unimisug'@'localhost'    IDENTIFIED BY 'Ug@Unimis@123';
CREATE USER 'unimisauth'@'localhost'  IDENTIFIED BY 'Auth@Unimis@123';

-- ----------------------------------------------------------------------------
-- 2. ADMIN  -  creates and maintains users, courses, notices, timetables
-- ----------------------------------------------------------------------------
-- Full data access on every table (and read access on every view) in unimis.
-- No DDL (CREATE / ALTER / DROP) and no GRANT OPTION.
GRANT SELECT, INSERT, UPDATE, DELETE ON unimis.* TO 'unimisadmin'@'localhost';

-- ----------------------------------------------------------------------------
-- 3. LECTURER
-- ----------------------------------------------------------------------------
-- Profile: can read users (needed for login + viewing undergraduate details) and
-- update their own profile fields, but NOT username, password_hash, role,
-- department_id or user_status.
GRANT SELECT ON unimis.users TO 'unimislec'@'localhost';
GRANT UPDATE (full_name, nic, date_of_birth, gender, email, contact_no, address, profile_picture) ON unimis.users TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.lecturers TO 'unimislec'@'localhost';
GRANT UPDATE (title, specialization) ON unimis.lecturers TO 'unimislec'@'localhost';

-- Undergraduate details / course information (read-only)
GRANT SELECT ON unimis.undergraduates  TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.departments     TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.courses         TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.course_lecturers TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.enrollments     TO 'unimislec'@'localhost';

-- Course materials: add and modify (add DELETE here if lecturers may remove them)
GRANT SELECT, INSERT, UPDATE, DELETE ON unimis.course_materials TO 'unimislec'@'localhost';

-- Marks: upload marks for every kind of exam
-- (assessments hold the quiz / assignment / mid / end-exam definitions that marks point to)
GRANT SELECT, INSERT, UPDATE ON unimis.assessments TO 'unimislec'@'localhost';
GRANT SELECT, INSERT, UPDATE ON unimis.marks       TO 'unimislec'@'localhost';

-- Attendance and medical records of undergraduates (read-only)
GRANT SELECT ON unimis.attendance_sessions TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.attendance_records  TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.medicals            TO 'unimislec'@'localhost';

-- Notices, grading reference data and settings (read-only)
GRANT SELECT ON unimis.notices         TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.grade_scales    TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.system_settings TO 'unimislec'@'localhost';


-- Eligibility, marks, grades and GPA (read-only views)
GRANT SELECT ON unimis.v_users              TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_attendance_summary TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_quiz_best2         TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_ca_summary         TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_final_exam_marks   TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_eligibility        TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_final_results      TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_sgpa               TO 'unimislec'@'localhost';
GRANT SELECT ON unimis.v_cgpa               TO 'unimislec'@'localhost';

-- (timetable_entries is intentionally not granted: it is not in the lecturer requirements)


-- ----------------------------------------------------------------------------
-- 4. TECHNICAL OFFICER
-- ----------------------------------------------------------------------------
-- Profile: read users (login + student names) and update own profile fields,
-- but NOT username, password_hash, role, department_id or user_status.
-- (designation / assigned_labs on technical_officers stay admin-managed.)
GRANT SELECT ON unimis.users TO 'unimisto'@'localhost';
GRANT UPDATE (full_name, nic, date_of_birth, gender, email, contact_no, address, profile_picture)
    ON unimis.users TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.technical_officers TO 'unimisto'@'localhost';
GRANT UPDATE (assigned_labs) ON unimis.technical_officers TO 'unimisto'@'localhost';

GRANT SELECT ON unimis.course_materials TO 'unimisto'@'localhost';
-- Reference data needed to record attendance / medicals (read-only)
GRANT SELECT ON unimis.undergraduates   TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.lecturers        TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.departments      TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.courses          TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.course_lecturers TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.enrollments      TO 'unimisto'@'localhost';

-- Attendance and medicals: add and maintain
GRANT SELECT, INSERT, UPDATE, DELETE ON unimis.attendance_sessions TO 'unimisto'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON unimis.attendance_records  TO 'unimisto'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON unimis.medicals            TO 'unimisto'@'localhost';

-- Notices and timetables (read-only; filter by their department in the Java code)
GRANT SELECT ON unimis.notices           TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.timetable_entries TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.system_settings   TO 'unimisto'@'localhost';

-- Views
GRANT SELECT ON unimis.v_users              TO 'unimisto'@'localhost';
GRANT SELECT ON unimis.v_attendance_summary TO 'unimisto'@'localhost';


-- ----------------------------------------------------------------------------
-- 5. UNDERGRADUATE
-- ----------------------------------------------------------------------------
-- Profile: read users (login + own profile) and update ONLY contact details
-- and profile picture.
GRANT SELECT ON unimis.users TO 'unimisug'@'localhost';
GRANT UPDATE (contact_no, email, address, profile_picture)
    ON unimis.users TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.undergraduates TO 'unimisug'@'localhost';

-- Course details (read-only)
GRANT SELECT ON unimis.departments      TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.courses          TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.course_lecturers TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.lecturers        TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.course_materials TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.enrollments      TO 'unimisug'@'localhost';

-- Attendance and medical details (read-only)
GRANT SELECT ON unimis.attendance_sessions TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.attendance_records  TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.medicals            TO 'unimisug'@'localhost';
GRANT INSERT (student_id, course_id, medical_date, reason, document_path, approval_status) ON unimis.medicals TO 'unimisug'@'localhost';

-- Timetables, notices, grading reference data (read-only)
GRANT SELECT ON unimis.timetable_entries TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.notices           TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.grade_scales      TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.system_settings   TO 'unimisug'@'localhost';

-- Grades and GPA (read-only views; no direct access to marks / assessments)
GRANT SELECT ON unimis.v_users              TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.v_attendance_summary TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.v_final_results      TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.v_sgpa               TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.v_cgpa               TO 'unimisug'@'localhost';
GRANT SELECT ON unimis.v_ca_summary TO 'unimisug'@'localhost';


-- ----------------------------------------------------------------------------
-- 6. AUTH SERVICE  -  used only by the login screen to check credentials
-- ----------------------------------------------------------------------------
-- Only the users columns that AuthService needs, and no other table or view:
--   login           find a user by username, compare password_hash, then read the
--                   role / department / status so the app knows which role account
--                   (admin / lec / to / ug) to connect with next
--   forgot password verify username + email + nic, then set a new password_hash
--                   (updated_at refreshes itself through ON UPDATE CURRENT_TIMESTAMP,
--                   so password_hash is the only column this account can write)
GRANT SELECT (user_id, username, password_hash, role, full_name, email, nic,
              department_id, user_status)
    ON unimis.users TO 'unimisauth'@'localhost';
GRANT UPDATE (password_hash)
    ON unimis.users TO 'unimisauth'@'localhost';


-- ----------------------------------------------------------------------------
-- 7. VERIFY (optional)
-- ----------------------------------------------------------------------------
-- SHOW GRANTS FOR 'unimisadmin'@'localhost';
-- SHOW GRANTS FOR 'unimislec'@'localhost';
-- SHOW GRANTS FOR 'unimisto'@'localhost';
-- SHOW GRANTS FOR 'unimisug'@'localhost';
-- SHOW GRANTS FOR 'unimisauth'@'localhost';

-- ============================================================================
-- END OF USERS & PRIVILEGES SCRIPT
-- ============================================================================