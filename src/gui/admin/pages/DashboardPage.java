package gui.admin.pages;

import dao.*;
import gui.admin.AdminDashboard;
import gui.admin.AdminTheme;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPage extends JPanel {

    private final AdminDashboard dash;

    public DashboardPage(AdminDashboard dash) {
        this.dash = dash;
        setLayout(new BorderLayout());
        setBackground(AdminTheme.LIGHT);
        setBorder(new EmptyBorder(28, 32, 28, 32));
        buildUI();
    }

    private void buildUI() {
        try {
            UserDAO userDAO = new UserDAO();
            CourseDAO courseDAO = new CourseDAO();
            List<User> users = userDAO.getAllUsers();
            List<Course> courses = courseDAO.getAllCourses();
            long students = users.stream().filter(u -> "Undergraduate".equalsIgnoreCase(u.getRole())).count();
            long lecturers = users.stream().filter(u -> "Lecturer".equalsIgnoreCase(u.getRole())).count();

            JPanel stats = new JPanel(new GridLayout(1, 4, 18, 0));
            stats.setOpaque(false);
            stats.add(statCard("\uD83D\uDC65", String.valueOf(users.size()), "Total Users", new Color(0x25, 0x63, 0xEB)));
            stats.add(statCard("\uD83C\uDF93", String.valueOf(students),       "Undergraduates", AdminTheme.SUCCESS));
            stats.add(statCard("\uD83D\uDC68\u200D\uD83C\uDFEB", String.valueOf(lecturers), "Lecturers", new Color(0x7D, 0x3C, 0x98)));
            stats.add(statCard("\uD83D\uDCDA", String.valueOf(courses.size()), "Active Courses", new Color(0xD9, 0x77, 0x06)));

            JPanel wrap = new JPanel();
            wrap.setOpaque(false);
            wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
            wrap.add(stats);
            wrap.add(Box.createVerticalStrut(24));
            wrap.add(recentUsersPanel(users));
            wrap.add(Box.createVerticalStrut(20));
            wrap.add(recentNoticesPanel());

            add(new JScrollPane(wrap) {{
                setBorder(null);
                getViewport().setBackground(AdminTheme.LIGHT);
                getVerticalScrollBar().setUnitIncrement(16);
            }}, BorderLayout.CENTER);
        } catch (Exception ex) {
            add(AdminTheme.label("Could not load dashboard: " + ex.getMessage(), AdminTheme.F_BODY, AdminTheme.DANGER));
        }
    }

    private JPanel statCard(String icon, String value, String label, Color color) {
        JPanel card = AdminTheme.card();
        card.setLayout(new BorderLayout(16, 0));

        JLabel ic = new JLabel(icon, SwingConstants.CENTER);
        ic.setOpaque(true);
        ic.setBackground(color);
        ic.setForeground(AdminTheme.WHITE);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        ic.setPreferredSize(new Dimension(52, 52));
        card.add(ic, BorderLayout.WEST);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel v = AdminTheme.label(value, new Font("Segoe UI", Font.BOLD, 22), AdminTheme.TEXT);
        JLabel l = AdminTheme.label(label, AdminTheme.F_SMALL, AdminTheme.MUTED);
        txt.add(v);
        txt.add(l);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    private JPanel recentUsersPanel(List<User> users) {
        JPanel p = AdminTheme.card();
        p.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(AdminTheme.label("Recently Added Users", AdminTheme.F_H3, AdminTheme.PRIMARY), BorderLayout.WEST);
        JButton viewAll = AdminTheme.button("View All", new Color(0xE9, 0xED, 0xF3));
        viewAll.addActionListener(e -> dash.showPage("users"));
        header.add(viewAll, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        int n = Math.min(6, users.size());
        Object[][] rows = new Object[n][5];
        for (int i = 0; i < n; i++) {
            User u = users.get(users.size() - 1 - i);
            rows[i] = new Object[]{ u.getUserId(), safe(u.getFullName()), safe(u.getRole()),
                    safe(u.getDepartmentId()), safe(u.getUserStatus()) };
        }
        p.add(AdminTheme.table(rows, new String[]{"User ID","Name","Role","Dept ID","Status"}), BorderLayout.CENTER);
        return p;
    }

    private JPanel recentNoticesPanel() {
        JPanel p = AdminTheme.card();
        p.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(AdminTheme.label("Latest Notices", AdminTheme.F_H3, AdminTheme.PRIMARY), BorderLayout.WEST);
        JButton viewAll = AdminTheme.button("View All", new Color(0xE9, 0xED, 0xF3));
        viewAll.addActionListener(e -> dash.showPage("notices"));
        header.add(viewAll, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        try {
            List<Notice> notices = new NoticeDAO().getAllNotices();
            int n = Math.min(5, notices.size());
            Object[][] rows = new Object[n][3];
            for (int i = 0; i < n; i++) {
                Notice nn = notices.get(i);
                rows[i] = new Object[]{ safe(nn.getTitle()), safe(nn.getTargetRole()),
                        nn.getPublishedAt() == null ? "-" : nn.getPublishedAt().toString().substring(0, 10) };
            }
            p.add(AdminTheme.table(rows, new String[]{"Title","Audience","Published"}), BorderLayout.CENTER);
        } catch (Exception ex) {
            p.add(AdminTheme.label("Notices unavailable", AdminTheme.F_SMALL, AdminTheme.MUTED), BorderLayout.CENTER);
        }
        return p;
    }

    private static String safe(Object o) { return o == null ? "-" : String.valueOf(o); }
}