
import javax.swing.*;
import java.awt.*;

public class StudentForm extends JFrame {

    private JTextField txtName;
    private JTextField txtID;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtDOB;
    private JTextField txtDepartment;
    private JTextField txtCourse;
    private JTextField txtGPA;
    private JTextField txtAddress;

    private JRadioButton rbMale;
    private JRadioButton rbFemale;
    private JRadioButton rbOther;

    private JComboBox<Integer> cmbYear;

    public StudentForm() {

        setTitle("Student Registration Form");
        setSize(500, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField(18);
        txtID = new JTextField(18);
        txtEmail = new JTextField(18);
        txtPhone = new JTextField(18);
        txtDOB = new JTextField(18);
        txtDepartment = new JTextField(18);
        txtCourse = new JTextField(18);
        txtGPA = new JTextField(18);
        txtAddress = new JTextField(18);

        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbOther = new JRadioButton("Other");

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);

        cmbYear = new JComboBox<>(
                new Integer[]{1, 2, 3, 4}
        );

        int row = 0;

        addRow(panel, gbc, row++, "Student Name:", txtName);
        addRow(panel, gbc, row++, "Student ID:", txtID);
        addRow(panel, gbc, row++, "Email:", txtEmail);
        addRow(panel, gbc, row++, "Phone Number:", txtPhone);
        addRow(panel, gbc, row++, "Date of Birth:", txtDOB);
        addRow(panel, gbc, row++, "Gender:", genderPanel);
        addRow(panel, gbc, row++, "Department:", txtDepartment);
        addRow(panel, gbc, row++, "Course:", txtCourse);
        addRow(panel, gbc, row++, "Year of Study:", cmbYear);
        addRow(panel, gbc, row++, "GPA:", txtGPA);
        addRow(panel, gbc, row++, "Address:", txtAddress);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 15));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> submitForm());

        add(panel);
        setVisible(true);
    }

    private void addRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(component, gbc);
    }

    private void submitForm() {

        // Check empty text fields
        if (txtName.getText().trim().isEmpty()
                || txtID.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()
                || txtPhone.getText().trim().isEmpty()
                || txtDOB.getText().trim().isEmpty()
                || txtDepartment.getText().trim().isEmpty()
                || txtCourse.getText().trim().isEmpty()
                || txtGPA.getText().trim().isEmpty()
                || txtAddress.getText().trim().isEmpty()
                || (!rbMale.isSelected()
                && !rbFemale.isSelected()
                && !rbOther.isSelected())) {

            JOptionPane.showMessageDialog(
                    this,
                    "You need to fill out all the data",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String gender;

        if (rbMale.isSelected()) {
            gender = "Male";
        } else if (rbFemale.isSelected()) {
            gender = "Female";
        } else {
            gender = "Other";
        }

        String data =
                "Student Name: " + txtName.getText().trim()
                        + "\nStudent ID: " + txtID.getText().trim()
                        + "\nEmail: " + txtEmail.getText().trim()
                        + "\nPhone Number: " + txtPhone.getText().trim()
                        + "\nDate of Birth: " + txtDOB.getText().trim()
                        + "\nGender: " + gender
                        + "\nDepartment: " + txtDepartment.getText().trim()
                        + "\nCourse: " + txtCourse.getText().trim()
                        + "\nYear of Study: " + cmbYear.getSelectedItem()
                        + "\nGPA: " + txtGPA.getText().trim()
                        + "\nAddress: " + txtAddress.getText().trim();

        JOptionPane.showMessageDialog(
                this,
                data,
                "Student Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new StudentForm());
    }
}
