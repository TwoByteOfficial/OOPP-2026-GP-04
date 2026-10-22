
package gui.lecturer.LecturerMarksGrades.EnterMarksPanel.EnterMarksPanelComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MarksAssessmentSelector extends JPanel {

    private static final Color DEEP_GREEN = new Color(27, 94, 32);
    private static final Color BORDER_GREEN = new Color(200, 230, 201);

    private final JComboBox<AssessmentOption> assessmentCombo;

    public MarksAssessmentSelector() {
        setLayout(new BorderLayout(0, 8));
        setOpaque(false);

        JLabel label = new JLabel("Select Assessment");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(DEEP_GREEN);

        assessmentCombo = new JComboBox<>();
        assessmentCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        assessmentCombo.setBackground(Color.WHITE);
        assessmentCombo.setPreferredSize(new Dimension(320, 36));
        assessmentCombo.setBorder(BorderFactory.createLineBorder(BORDER_GREEN));

        add(label, BorderLayout.NORTH);
        add(assessmentCombo, BorderLayout.CENTER);
    }

    public void setAssessments(List<Object[]> assessments) {
        assessmentCombo.removeAllItems();

        for (Object[] assessment : assessments) {
            long assessmentId = ((Number) assessment[0]).longValue();
            String name = String.valueOf(assessment[1]);
            String category = String.valueOf(assessment[2]);
            String type = String.valueOf(assessment[3]);
            String maxMark = String.valueOf(assessment[4]);

            assessmentCombo.addItem(
                    new AssessmentOption(
                            assessmentId, name, category, type, maxMark
                    )
            );
        }
    }

    public long getSelectedAssessmentId() {
        AssessmentOption selected =
                (AssessmentOption) assessmentCombo.getSelectedItem();

        return selected == null ? -1 : selected.assessmentId;
    }

    public void addSelectionListener(ActionListener listener) {
        assessmentCombo.addActionListener(listener);
    }

    private static class AssessmentOption {
        private final long assessmentId;
        private final String name;
        private final String category;
        private final String type;
        private final String maxMark;

        private AssessmentOption(long assessmentId, String name,
                                 String category, String type,
                                 String maxMark) {
            this.assessmentId = assessmentId;
            this.name = name;
            this.category = category;
            this.type = type;
            this.maxMark = maxMark;
        }

        @Override
        public String toString() {
            return name + " (" + category + " - " + type
                    + ", Max: " + maxMark + ")";
        }
    }
}
