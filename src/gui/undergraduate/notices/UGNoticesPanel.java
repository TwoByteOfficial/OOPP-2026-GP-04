package gui.undergraduate.notices;

import gui.admin.AdminTheme;
import gui.undergraduate.notices.filter.NoticeTypeFilterBar;
import gui.undergraduate.notices.header.NoticesHeaderPanel;
import gui.undergraduate.notices.list.NoticesListPanel;
import gui.undergraduate.notices.model.NoticeData;
import gui.undergraduate.notices.reader.NoticeReaderDialog;
import gui.undergraduate.notices.summary.NoticesSummaryStripPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module 1.7: Official Notices & Circulars view coordinator.
 */
public class UGNoticesPanel extends JPanel {

    private final NoticesSummaryStripPanel summaryStrip;
    private final NoticesListPanel listPanel;
    private final List<NoticeData> masterNotices = new ArrayList<>();

    public UGNoticesPanel(long userId, String username) {
        setLayout(new BorderLayout(0, 16));
        setBackground(AdminTheme.LIGHT_BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        initSampleNotices();

        // 1. Top Section: Header + Metric Strip
        JPanel topWrapper = new JPanel(new BorderLayout(0, 14));
        topWrapper.setOpaque(false);
        topWrapper.add(new NoticesHeaderPanel(), BorderLayout.NORTH);

        summaryStrip = new NoticesSummaryStripPanel();
        recalculateMetrics();
        topWrapper.add(summaryStrip, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // 2. Center Section: Filter Bar on Top + Notice Cards Feed
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setOpaque(false);

        NoticeTypeFilterBar filterBar = new NoticeTypeFilterBar(this::filterNotices);
        centerWrapper.add(filterBar, BorderLayout.NORTH);

        listPanel = new NoticesListPanel(this::openNoticeReader);
        centerWrapper.add(listPanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // Initial view
        filterNotices("All Notices");
    }

    private void filterNotices(String category) {
        if ("All Notices".equalsIgnoreCase(category)) {
            listPanel.setNotices(masterNotices);
        } else {
            List<NoticeData> filtered = masterNotices.stream()
                    .filter(n -> n.getNoticeType().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
            listPanel.setNotices(filtered);
        }
    }

    private void recalculateMetrics() {
        int academic = 0, exam = 0, urgent = 0;
        for (NoticeData n : masterNotices) {
            if ("Academic".equalsIgnoreCase(n.getNoticeType())) academic++;
            else if ("Exam".equalsIgnoreCase(n.getNoticeType())) exam++;
            else if ("Urgent".equalsIgnoreCase(n.getNoticeType())) urgent++;
        }
        summaryStrip.updateCounts(masterNotices.size(), academic, exam, urgent);
    }

    private void openNoticeReader(NoticeData data) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        NoticeReaderDialog dialog = new NoticeReaderDialog(owner, data);
        dialog.setVisible(true);
    }

    private void initSampleNotices() {
        masterNotices.add(new NoticeData(1, "Semester I End-Semester Examination Schedule Draft Published",
                "The examination department has finalized the draft timetable for Level II Semester I examinations. Students are instructed to review the course schedule and report any lecture/lab conflicts to the AR office before 2026-10-30.",
                "Exam", "Undergraduate", "2026-10-18", "Examination Branch"));

        masterNotices.add(new NoticeData(2, "Urgent: Medical Submission Final Deadline for Mid-Semester Absences",
                "Undergraduates who were absent for continuous assessments (CA) or labs between Weeks 4 and 8 must submit their stamped university medical officer certified forms before Friday 4:00 PM. Late submissions will not be excused under university statute.",
                "Urgent", "Undergraduate", "2026-10-15", "Senior Assistant Registrar"));

        masterNotices.add(new NoticeData(3, "OOP Project Milestone 2 Deliverable Guidelines & Repository Submission",
                "All student groups enrolled in ICT2132 are required to submit their micro-component architecture diagrams and complete GUI prototypes to GitHub classroom before 2026-11-05.",
                "Academic", "Undergraduate", "2026-10-10", "Dept. of Information Technology"));

        masterNotices.add(new NoticeData(4, "Mandatory Attendance Threshold (80%) Regulatory Notice",
                "Students are strictly reminded that an attendance percentage below 80% across lectures and laboratory practicals will automatically deem a student ineligible to sit for the upcoming semester examinations.",
                "Attendance", "Undergraduate", "2026-10-05", "Faculty Academic Affairs"));

        masterNotices.add(new NoticeData(5, "Library Digital Database Access Renewal (IEEE Xplore & ACM)",
                "The university library consortium has successfully renewed off-campus VPN access for IEEE Xplore and ACM Digital Libraries for all registered undergraduates.",
                "Academic", "Undergraduate", "2026-09-28", "Main University Library"));
    }
}