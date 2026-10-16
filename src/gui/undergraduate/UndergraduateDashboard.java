package gui.undergraduate;

import dao.UndergraduateDAO;
import gui.login.Login;
import model.Undergraduate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Undergraduate-only portal. Other role dashboards are deliberately untouched. */
public class UndergraduateDashboard extends JFrame {
    private static final Color GREEN = new Color(31, 86, 38);
    private static final Color GREEN_2 = new Color(53, 137, 64);
    private static final Color ACTIVE = new Color(139, 199, 132);
    private static final Color BG = new Color(244, 248, 243);
    private static final Color PALE = new Color(226, 241, 224);
    private static final Color TEXT = new Color(48, 65, 49);
    private static final Color MUTED = new Color(112, 128, 113);
    private static final Color LINE = new Color(222, 232, 220);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final long userId;
    private final String username;
    private final String loginName;
    private final String loginEmail;
    private final UndergraduateDAO dao = new UndergraduateDAO();
    private final JPanel content = new JPanel(new BorderLayout(0, 14));
    private final JPanel navPanel = new JPanel();
    private JButton selectedNav;
    private Undergraduate student;

    public UndergraduateDashboard(long userId, String username, String fullName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.loginName = fullName;
        this.loginEmail = email;
        setTitle("UniMIS - Undergraduate Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setSize(1240, 780);
        setLocationRelativeTo(null);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(), BorderLayout.CENTER);
        setContentPane(root);
        loadStudent();
        showDashboard();
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout(0, 18));
        side.setBackground(GREEN);
        side.setPreferredSize(new Dimension(205, 0));
        side.setBorder(new EmptyBorder(22, 13, 15, 13));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("UniMIS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subtitle = new JLabel("UNDERGRADUATE PORTAL");
        subtitle.setForeground(new Color(201, 226, 199));
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        brand.add(title); brand.add(Box.createVerticalStrut(4)); brand.add(subtitle);
        brand.add(Box.createVerticalStrut(22));
        JSeparator sep = new JSeparator(); sep.setForeground(new Color(70, 120, 73));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); brand.add(sep);

        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        addNav("Dashboard", this::showDashboard);
        addNav("My Profile", this::showProfile);
        addNav("My Courses", this::showCourses);
        addNav("My Attendance", this::showAttendance);
        addNav("My Medicals", () -> showTablePage("My Medical Records", "Medical submissions and review status", new String[]{"Date","Code","Course","Reason","Status","Document","Review Comment"}, () -> dao.getMedicals(userId)));
        addNav("Results & GPA", this::showResults);
        addNav("My Timetable", this::showTimetable);
        addNav("Notices", () -> showTablePage("Faculty Notices", "Announcements for all users and undergraduates", new String[]{"Title","Type","Notice","Published","Expires"}, dao::getNotices));

        JButton logout = new JButton("Logout");
        styleNav(logout, false);
        logout.setBackground(new Color(125, 188, 119));
        logout.setForeground(GREEN);
        logout.addActionListener(e -> { dispose(); Login.showLogin(); });
        side.add(brand, BorderLayout.NORTH);
        side.add(navPanel, BorderLayout.CENTER);
        side.add(logout, BorderLayout.SOUTH);
        return side;
    }

    private void addNav(String label, Runnable action) {
        JButton b = new JButton(label);
        styleNav(b, false);
        b.addActionListener(e -> { selectNav(b); action.run(); });
        navPanel.add(b);
        navPanel.add(Box.createVerticalStrut(5));
        if (label.equals("Dashboard")) { selectedNav = b; styleNav(b, true); }
    }

    private void styleNav(JButton b, boolean active) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));
        b.setPreferredSize(new Dimension(178, 39));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setBackground(active ? ACTIVE : GREEN);
        b.setForeground(active ? GREEN : Color.WHITE);
        b.setBorder(new EmptyBorder(0, 12, 0, 7));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void selectNav(JButton button) {
        if (selectedNav != null) styleNav(selectedNav, false);
        selectedNav = button;
        styleNav(selectedNav, true);
    }

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(0, 20, 18, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(17, 22, 17, 22));
        header.setPreferredSize(new Dimension(0, 76));
        JLabel heading = new JLabel("Undergraduate Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 21)); heading.setForeground(GREEN);
        JLabel sub = new JLabel("  |  Faculty of Technology"); sub.setFont(BODY); sub.setForeground(MUTED);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4)); left.setOpaque(false);
        left.add(heading); left.add(sub);
        String name = (loginName == null || loginName.isBlank()) ? username : loginName;
        JPanel identity = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); identity.setOpaque(false);
        JLabel avatar = new JLabel(initials(name), SwingConstants.CENTER);
        avatar.setOpaque(true); avatar.setBackground(GREEN_2); avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 13)); avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(210, 232, 207), 2));
        JPanel who = new JPanel(); who.setOpaque(false); who.setLayout(new BoxLayout(who, BoxLayout.Y_AXIS));
        JLabel user = new JLabel(name); user.setForeground(TEXT); user.setFont(BOLD);
        JLabel role = new JLabel("Undergraduate"); role.setForeground(MUTED); role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        who.add(user); who.add(Box.createVerticalStrut(3)); who.add(role);
        identity.add(avatar); identity.add(who);
        header.add(left, BorderLayout.WEST); header.add(identity, BorderLayout.EAST);

        content.setOpaque(false);
        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private void loadStudent() {
        try { student = dao.getProfile(userId); }
        catch (SQLException ex) { showError("Could not load your undergraduate profile.", ex); }
    }

    private void showDashboard() {
        content.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 15)); page.setOpaque(false);
        String name = student != null && student.getFullName() != null ? student.getFullName() : loginName;
        JPanel welcome = new JPanel(); welcome.setOpaque(false); welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Welcome back, " + safe(name)); title.setFont(new Font("Segoe UI", Font.BOLD, 22)); title.setForeground(GREEN);
        JLabel desc = new JLabel("Your academic information, all in one place."); desc.setFont(BODY); desc.setForeground(MUTED);
        welcome.add(title); welcome.add(Box.createVerticalStrut(5)); welcome.add(desc);

        JPanel stats = new JPanel(new GridLayout(1, 3, 13, 0)); stats.setOpaque(false);
        stats.add(statCard("REGISTRATION NUMBER", student == null ? "Not available" : safe(student.getRegistrationNo()), "REG"));
        stats.add(statCard("STUDENT CATEGORY", student == null ? "Undergraduate" : safe(student.getStudentType()), "TYPE"));
        stats.add(statCard("CURRENT SEMESTER", student == null ? "Not available" : semesterLabel(student.getCurrentSemester()), "SEM"));

        JPanel quick = new JPanel(new GridLayout(2, 2, 13, 13)); quick.setOpaque(false);
        quick.add(quickCard("My Courses", "View enrolled courses, credits and lecturers.", "COURSES", this::showCourses));
        quick.add(quickCard("Attendance", "Check theory and practical attendance records.", "ATTENDANCE", this::showAttendance));
        quick.add(quickCard("Results & GPA", "Review assessment marks, grades and GPA.", "RESULTS", this::showResults));
        quick.add(quickCard("Weekly Timetable", "Find your scheduled lectures and practicals.", "TIMETABLE", this::showTimetable));

        JPanel top = new JPanel(new BorderLayout(0, 15)); top.setOpaque(false);
        top.add(welcome, BorderLayout.NORTH); top.add(stats, BorderLayout.CENTER);
        page.add(top, BorderLayout.NORTH); page.add(quick, BorderLayout.CENTER);

        String type = student == null ? "" : student.getStudentType();
        if ("Repeat".equalsIgnoreCase(type) || "Batch Missed".equalsIgnoreCase(type)) {
            RoundedPanel note = new RoundedPanel(18, new Color(255, 249, 225));
            note.setBorder(new EmptyBorder(12, 16, 12, 16));
            String message = "Repeat".equalsIgnoreCase(type)
                    ? "Repeat student: check the courses and semester records assigned to your repeat enrolment."
                    : "Batch-missed student: your academic batch and timetable may differ from the current batch. Check your registered courses carefully.";
            JLabel n = new JLabel("ⓘ   " + message); n.setFont(BODY); n.setForeground(new Color(105, 85, 31)); note.add(n);
            JPanel wrapper = new JPanel(new BorderLayout(0, 12)); wrapper.setOpaque(false);
            wrapper.add(page, BorderLayout.CENTER); wrapper.add(note, BorderLayout.SOUTH);
            content.add(wrapper, BorderLayout.CENTER);
        } else content.add(page, BorderLayout.CENTER);
        refresh();
    }

    private JPanel statCard(String label, String value, String iconText) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(10, 9)); card.setBorder(new EmptyBorder(16, 17, 16, 17));
        JLabel icon = new JLabel(iconText, SwingConstants.CENTER); icon.setOpaque(true); icon.setBackground(PALE); icon.setForeground(GREEN_2);
        icon.setFont(new Font("Segoe UI", Font.BOLD, iconText.length() > 5 ? 8 : 10)); icon.setPreferredSize(new Dimension(54, 43));
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 10)); l.setForeground(MUTED);
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.BOLD, 19)); v.setForeground(GREEN);
        JPanel text = new JPanel(); text.setOpaque(false); text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(l); text.add(Box.createVerticalStrut(7)); text.add(v);
        card.add(icon, BorderLayout.WEST); card.add(text, BorderLayout.CENTER); return card;
    }

    private JPanel quickCard(String title, String detail, String iconText, Runnable action) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(0, 12)); card.setBorder(new EmptyBorder(17, 18, 15, 18));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        JLabel icon = new JLabel(iconText, SwingConstants.CENTER); icon.setOpaque(true); icon.setBackground(PALE); icon.setForeground(GREEN_2);
        icon.setFont(new Font("Segoe UI", Font.BOLD, iconText.length() > 6 ? 8 : 10)); icon.setPreferredSize(new Dimension(76, 42));
        JLabel arrow = new JLabel(" ");
        top.add(icon, BorderLayout.WEST); top.add(arrow, BorderLayout.EAST);
        JLabel t = new JLabel(title); t.setFont(new Font("Segoe UI", Font.BOLD, 16)); t.setForeground(GREEN);
        JLabel d = new JLabel("<html><div style='width:230px;color:#708070'>" + detail + "</div></html>"); d.setFont(BODY);
        JButton open = new JButton("Open section  →"); open.setFont(BOLD); open.setForeground(GREEN_2); open.setBackground(new Color(238, 247, 236));
        open.setBorder(new EmptyBorder(9, 12, 9, 12)); open.setFocusPainted(false); open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.addActionListener(e -> action.run());
        JPanel text = new JPanel(); text.setOpaque(false); text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(t); text.add(Box.createVerticalStrut(6)); text.add(d);
        card.add(top, BorderLayout.NORTH); card.add(text, BorderLayout.CENTER); card.add(open, BorderLayout.SOUTH);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return card;
    }

    private void showProfile() {
        if (student == null) loadStudent();
        content.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 14)); page.setOpaque(false);
        page.add(pageHeading("My Profile", "Your academic identity is read-only. You can update only your contact details."), BorderLayout.NORTH);
        RoundedPanel form = new RoundedPanel(18, Color.WHITE); form.setLayout(new GridBagLayout()); form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(7, 8, 7, 8); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        JTextField fullName = field(student == null ? loginName : student.getFullName());
        JTextField regNo = field(student == null ? "" : student.getRegistrationNo());
        JTextField usernameField = field(username);
        JTextField email = field(student == null ? loginEmail : student.getEmail());
        JTextField phone = field(student == null ? "" : student.getContactNo());
        JTextField address = field(student == null ? "" : student.getAddress());
        JTextField picture = field(student == null ? "" : student.getProfilePicture());
        JTextField batch = field(student == null ? "" : String.valueOf(student.getBatchYear()));
        JTextField type = field(student == null ? "" : student.getStudentType());
        JTextField semester = field(student == null ? "" : String.valueOf(student.getCurrentSemester()));
        for (JTextField f : new JTextField[]{fullName, regNo, usernameField, batch, type, semester}) f.setEditable(false);
        int r = 0;
        addFormRow(form, g, r++, "Full Name", fullName); addFormRow(form, g, r++, "Registration Number", regNo);
        addFormRow(form, g, r++, "Username (read-only)", usernameField); addFormRow(form, g, r++, "Email", email);
        addFormRow(form, g, r++, "Phone", phone); addFormRow(form, g, r++, "Address", address);
        addFormRow(form, g, r++, "Profile Picture Path", picture); addFormRow(form, g, r++, "Batch Year", batch);
        addFormRow(form, g, r++, "Student Type", type); addFormRow(form, g, r++, "Current Semester", semester);
        JButton save = primaryButton("Save Contact Details");
        save.addActionListener(e -> {
            String em = email.getText().trim();
            if (em.isEmpty() || !em.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation", JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                if (dao.updateContactDetails(userId, em, phone.getText(), address.getText(), picture.getText())) {
                    JOptionPane.showMessageDialog(this, "Your contact details were updated."); loadStudent(); showProfile();
                } else JOptionPane.showMessageDialog(this, "No profile was updated. Please check your account.", "Update", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) { showError("Could not update your profile. The email may already be used.", ex); }
        });
        g.gridx = 1; g.gridy = r; g.weightx = 0; form.add(save, g);
        page.add(form, BorderLayout.CENTER); content.add(page, BorderLayout.CENTER); refresh();
    }

    private void showCourses() {
        showTablePage("My Courses", "Courses registered to your account, with credits and lecturer details.", new String[]{"Code","Course","Theory Credits","Practical Credits","Total Credits","Lecturers","Academic Year","Semester"}, () -> dao.getCourses(userId));
    }
    private void showAttendance() {
        showTablePage("My Attendance", "Theory and practical attendance, including approved medical sessions.", new String[]{"Code","Course","Component","Sessions","Attended","Medical Sessions","Attendance"}, () -> dao.getAttendance(userId));
    }
    private void showTimetable() {
        showTablePage("My Timetable", "Your weekly timetable based on your batch and current semester.", new String[]{"Day","Start","End","Code","Course","Component","Venue","Academic Year","Semester"}, () -> dao.getTimetable(userId));
    }
    private void showResults() {
        content.removeAll(); JPanel page = new JPanel(new BorderLayout(0, 14)); page.setOpaque(false);
        page.add(pageHeading("Results & GPA", "Assessment marks and grade information recorded by the faculty."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14)); body.setOpaque(false);
        JPanel summary = new JPanel(new GridLayout(1, 2, 13, 0)); summary.setOpaque(false);
        try {
            List<String[]> rows = dao.getGPA(userId); String sgpa = "Not available", cgpa = "Not available";
            for (String[] row : rows) { if ("SGPA".equalsIgnoreCase(row[0])) sgpa = row[1]; if ("CGPA".equalsIgnoreCase(row[0])) cgpa = row[1]; }
            summary.add(statCard("SEMESTER GPA (SGPA)", sgpa, "SGPA")); summary.add(statCard("CUMULATIVE GPA (CGPA)", cgpa, "CGPA"));
        } catch (SQLException ex) { summary.add(statCard("SEMESTER GPA (SGPA)", "Not available", "SGPA")); summary.add(statCard("CUMULATIVE GPA (CGPA)", "Not available", "CGPA")); }
        body.add(summary, BorderLayout.NORTH);
        try {
            List<String[]> rows = dao.getResults(userId);
            String[] columns = {"Code","Course","Assessment","Category","Weight","Mark","Grade","Grade Point"};
            body.add(tablePanel(columns, rows), BorderLayout.CENTER);
        } catch (SQLException ex) { body.add(errorPanel("Could not load results. Check database schema and connection."), BorderLayout.CENTER); }
        page.add(body, BorderLayout.CENTER); content.add(page, BorderLayout.CENTER); refresh();
    }

    private void showTablePage(String title, String description, String[] columns, RowLoader loader) {
        content.removeAll(); JPanel page = new JPanel(new BorderLayout(0, 14)); page.setOpaque(false);
        page.add(pageHeading(title, description), BorderLayout.NORTH);
        try { page.add(tablePanel(columns, loader.load()), BorderLayout.CENTER); }
        catch (SQLException ex) { page.add(errorPanel("Could not load this information. Check the database connection and schema."), BorderLayout.CENTER); }
        content.add(page, BorderLayout.CENTER); refresh();
    }

    private JPanel tablePanel(String[] columns, List<String[]> rows) {
        RoundedPanel panel = new RoundedPanel(18, Color.WHITE); panel.setLayout(new BorderLayout()); panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        DefaultTableModel model = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int col) { return false; } };
        for (String[] row : rows) model.addRow(row);
        JTable table = new JTable(model); table.setFont(BODY); table.setForeground(TEXT); table.setBackground(Color.WHITE);
        table.setRowHeight(34); table.setShowVerticalLines(false); table.setShowHorizontalLines(true); table.setGridColor(LINE);
        table.setSelectionBackground(PALE); table.setSelectionForeground(GREEN); table.setFillsViewportHeight(true); table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(GREEN_2);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        t, value, selected, focused, row, column);
                label.setOpaque(true);
                label.setBackground(GREEN_2);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(44, 111, 53)),
                        new EmptyBorder(0, 9, 0, 9)));
                return label;
            }
        });
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(); renderer.setBorder(new EmptyBorder(0, 8, 0, 8));
        renderer.setForeground(TEXT); renderer.setBackground(Color.WHITE);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        if (rows.isEmpty()) { JLabel empty = new JLabel("  No records available yet."); empty.setForeground(MUTED); empty.setFont(BODY); empty.setBorder(new EmptyBorder(12, 5, 4, 5)); panel.add(empty, BorderLayout.SOUTH); }
        return panel;
    }

    private JPanel errorPanel(String message) {
        RoundedPanel p = new RoundedPanel(18, Color.WHITE); p.setLayout(new BorderLayout()); p.setBorder(new EmptyBorder(24, 24, 24, 24));
        JLabel l = new JLabel("ⓘ   " + message); l.setFont(BODY); l.setForeground(new Color(140, 77, 38)); p.add(l, BorderLayout.NORTH); return p;
    }

    private JPanel pageHeading(String title, String description) {
        JPanel p = new JPanel(); p.setOpaque(false); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel h = new JLabel(title); h.setFont(new Font("Segoe UI", Font.BOLD, 22)); h.setForeground(GREEN);
        JLabel d = new JLabel(description); d.setFont(BODY); d.setForeground(MUTED);
        p.add(h); p.add(Box.createVerticalStrut(5)); p.add(d); p.setBorder(new EmptyBorder(3, 2, 3, 0)); return p;
    }

    private JTextField field(String value) {
        JTextField f = new JTextField(value == null ? "" : value, 28); f.setFont(BODY); f.setForeground(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(8, 10, 8, 10)));
        f.setPreferredSize(new Dimension(330, 36)); return f;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text); b.setFont(BOLD); b.setBackground(GREEN_2); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }

    private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent input) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; JLabel l = new JLabel(label); l.setForeground(TEXT); l.setFont(BOLD); panel.add(l, g);
        g.gridx = 1; g.weightx = 1; panel.add(input, g);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + "\n" + ex.getMessage(), "UniMIS", JOptionPane.ERROR_MESSAGE);
    }
    private String safe(String value) { return value == null || value.trim().isEmpty() ? "Not available" : value; }
    private String semesterLabel(int semester) {
        return semester > 0 ? "Semester " + semester : "Not available";
    }
    private String initials(String name) {
        if (name == null || name.isBlank()) return "UG";
        String[] parts = name.trim().split("\\s+");
        return parts.length > 1 ? ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase() : name.substring(0, 1).toUpperCase();
    }
    private void refresh() { content.revalidate(); content.repaint(); }

    @FunctionalInterface private interface RowLoader { List<String[]> load() throws SQLException; }

    /** Lightweight rounded surface, implemented in Swing without extra dependencies. */
    private static class RoundedPanel extends JPanel {
        private final int arc; private final Color fill;
        RoundedPanel(int arc, Color fill) { this.arc = arc; this.fill = fill; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill); g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g.setColor(new Color(224, 233, 222)); g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g.dispose(); super.paintComponent(graphics);
        }
    }
}
