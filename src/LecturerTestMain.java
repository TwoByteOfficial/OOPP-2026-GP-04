
import gui.lecturer.Lecturer;

import javax.swing.SwingUtilities;

public class LecturerTestMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Actual lecturer details from the database
            long userId = 2L;
            String username = "lecturer1";
            String fullName = "Dr. N. Perera";
            String email = "lecturer1@fot.ruh.ac.lk";
            String role = "Lecturer";

            // Open the Lecturer portal directly for testing
            Lecturer lecturer = new Lecturer(userId, username, fullName, email, role);
            lecturer.setVisible(true);
        });
    }
}
