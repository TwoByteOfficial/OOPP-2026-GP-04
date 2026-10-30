package gui.undergraduate.notices.summary;

import gui.admin.AdminTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Metric strip displaying notice counts per category (All, Academic, Exam, Urgent).
 */
public class NoticesSummaryStripPanel extends JPanel {

    private final NoticeStatCard totalCard;
    private final NoticeStatCard academicCard;
    private final NoticeStatCard examCard;
    private final NoticeStatCard urgentCard;

    public NoticesSummaryStripPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 4, 15, 0));
        setPreferredSize(new Dimension(0, 75));

        totalCard = new NoticeStatCard("0 Notices", "Active Bulletins", AdminTheme.FOREST_GREEN);
        academicCard = new NoticeStatCard("0 Circulars", "Academic Updates", AdminTheme.FOREST_GREEN);
        examCard = new NoticeStatCard("0 Notices", "Exam Department", AdminTheme.MEDIUM_GREEN);
        urgentCard = new NoticeStatCard("0 Urgent", "Immediate Action", AdminTheme.WARNING);

        add(totalCard);
        add(academicCard);
        add(examCard);
        add(urgentCard);
    }

    public void updateCounts(int total, int academic, int exam, int urgent) {
        totalCard.setValue(total + " Notices");
        academicCard.setValue(academic + " Circulars");
        examCard.setValue(exam + " Notices");
        urgentCard.setValue(urgent + " Urgent");
    }
}