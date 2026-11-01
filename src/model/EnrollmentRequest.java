package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * EnrollmentRequest - Model representing a course enrollment request
 * mapped from table enrollment_requests and view v_enrollment_requests.
 */
public class EnrollmentRequest {

    private long request_id;
    private long student_id;
    private String registration_no;
    private String student_name;
    private String student_type;

    private int course_id;
    private String course_code;
    private String course_name;
    private BigDecimal total_credits;

    private String academic_year;
    private String semester;
    private String enrollment_type;
    private String request_note;
    private String status;
    private Timestamp requested_at;

    private Long decided_by;
    private String decided_by_name;
    private Timestamp decided_at;
    private String decision_note;

    private boolean already_enrolled;

    public EnrollmentRequest() {}

    // Getters and Setters
    public long getRequestId() { return request_id; }
    public void setRequestId(long request_id) { this.request_id = request_id; }

    public long getStudentId() { return student_id; }
    public void setStudentId(long student_id) { this.student_id = student_id; }

    public String getRegistrationNo() { return registration_no; }
    public void setRegistrationNo(String registration_no) { this.registration_no = registration_no; }

    public String getStudentName() { return student_name; }
    public void setStudentName(String student_name) { this.student_name = student_name; }

    public String getStudentType() { return student_type; }
    public void setStudentType(String student_type) { this.student_type = student_type; }

    public int getCourseId() { return course_id; }
    public void setCourseId(int course_id) { this.course_id = course_id; }

    public String getCourseCode() { return course_code; }
    public void setCourseCode(String course_code) { this.course_code = course_code; }

    public String getCourseName() { return course_name; }
    public void setCourseName(String course_name) { this.course_name = course_name; }

    public BigDecimal getTotalCredits() { return total_credits; }
    public void setTotalCredits(BigDecimal total_credits) { this.total_credits = total_credits; }

    public String getAcademicYear() { return academic_year; }
    public void setAcademicYear(String academic_year) { this.academic_year = academic_year; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getEnrollmentType() { return enrollment_type; }
    public void setEnrollmentType(String enrollment_type) { this.enrollment_type = enrollment_type; }

    public String getRequestNote() { return request_note; }
    public void setRequestNote(String request_note) { this.request_note = request_note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRequestedAt() { return requested_at; }
    public void setRequestedAt(Timestamp requested_at) { this.requested_at = requested_at; }

    public Long getDecidedBy() { return decided_by; }
    public void setDecidedBy(Long decided_by) { this.decided_by = decided_by; }

    public String getDecidedByName() { return decided_by_name; }
    public void setDecidedByName(String decided_by_name) { this.decided_by_name = decided_by_name; }

    public Timestamp getDecidedAt() { return decided_at; }
    public void setDecidedAt(Timestamp decided_at) { this.decided_at = decided_at; }

    public String getDecisionNote() { return decision_note; }
    public void setDecisionNote(String decision_note) { this.decision_note = decision_note; }

    public boolean isAlreadyEnrolled() { return already_enrolled; }
    public void setAlreadyEnrolled(boolean already_enrolled) { this.already_enrolled = already_enrolled; }
}
