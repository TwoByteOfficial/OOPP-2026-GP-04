package gui.admin.EnrollmentComponents;

import javax.swing.*;
import java.awt.*;

/**
 * EnrollmentActions - Action toolbar aggregating dedicated action button components:
 * ApproveEnrollmentButton, DeclineEnrollmentButton, ViewEnrollmentButton, NewEnrollmentButton, and RefreshEnrollmentButton.
 */
public class EnrollmentActions extends JPanel {

    private final ApproveEnrollmentButton btnApprove;
    private final DeclineEnrollmentButton btnDecline;
    private final ViewEnrollmentButton btnView;
    private final NewEnrollmentButton btnNew;
    private final RefreshEnrollmentButton btnRefresh;

    public EnrollmentActions(Runnable onApprove, Runnable onDecline, Runnable onView, Runnable onNew, Runnable onRefresh) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        setOpaque(false);
        setPreferredSize(new Dimension(1620, 50));

        btnApprove = new ApproveEnrollmentButton(onApprove);
        btnDecline = new DeclineEnrollmentButton(onDecline);
        btnView    = new ViewEnrollmentButton(onView);
        btnNew     = new NewEnrollmentButton(onNew);
        btnRefresh = new RefreshEnrollmentButton(onRefresh);

        add(btnApprove);
        add(btnDecline);
        add(btnView);
        add(btnNew);
        add(btnRefresh);
    }
}
