import dao.UserDAO;
import model.User;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("        UniMIS - Database Tester");
        System.out.println("========================================");

        boolean running = true;

        while (running) {

            showMenu();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    listUsers();
                    break;

                case "2":
                    findUserById();
                    break;

                case "3":
                    findUserByUsername();
                    break;

                case "4":
                    addUser();
                    break;

                case "5":
                    updateUser();
                    break;

                case "6":
                    updatePassword();
                    break;

                case "7":
                    deleteUser();
                    break;

                case "0":
                    running = false;
                    System.out.println("\nExiting UniMIS...");
                    break;

                default:
                    System.out.println("\nInvalid choice. Try again.");
            }
        }

        scanner.close();
    }


    // ========================================
    // MENU
    // ========================================

    private static void showMenu() {

        System.out.println("\n----------------------------------------");
        System.out.println("              USER MANAGEMENT");
        System.out.println("----------------------------------------");
        System.out.println("1. List all users");
        System.out.println("2. Find user by ID");
        System.out.println("3. Find user by username");
        System.out.println("4. Add user");
        System.out.println("5. Update user");
        System.out.println("6. Update password");
        System.out.println("7. Delete user");
        System.out.println("0. Exit");
        System.out.println("----------------------------------------");
    }


    // ========================================
    // LIST USERS
    // ========================================

    private static void listUsers() {

        System.out.println("\n========== ALL USERS ==========");

        List<User> users = userDAO.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("Total users: " + users.size());
        System.out.println();

        for (User user : users) {
            System.out.println(user);
        }
    }


    // ========================================
    // FIND USER BY ID
    // ========================================

    private static void findUserById() {

        System.out.print("\nEnter User ID: ");

        try {

            long userId = Long.parseLong(scanner.nextLine());

            User user = userDAO.getUserById(userId);

            if (user != null) {
                System.out.println("\nUser found:");
                System.out.println(user);
            } else {
                System.out.println("\nUser not found.");
            }

        } catch (NumberFormatException e) {

            System.out.println("Invalid User ID.");
        }
    }


    // ========================================
    // FIND USER BY USERNAME
    // ========================================

    private static void findUserByUsername() {

        System.out.print("\nEnter username: ");

        String username = scanner.nextLine();

        User user = userDAO.getUserByUsername(username);

        if (user != null) {
            System.out.println("\nUser found:");
            System.out.println(user);
        } else {
            System.out.println("\nUser not found.");
        }
    }


    // ========================================
    // ADD USER
    // ========================================

    private static void addUser() {

        System.out.println("\n========== ADD USER ==========");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password Hash: ");
        String passwordHash = scanner.nextLine();

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Contact No: ");
        String contactNo = scanner.nextLine();

        System.out.print("Address: ");
        String address = scanner.nextLine();

        System.out.print("Profile Picture: ");
        String profilePicture = scanner.nextLine();

        System.out.print("Department ID (press Enter for NULL): ");
        String departmentInput = scanner.nextLine();

        Integer departmentId = null;

        if (!departmentInput.isBlank()) {

            try {
                departmentId = Integer.parseInt(departmentInput);

            } catch (NumberFormatException e) {

                System.out.println("Invalid Department ID.");
                return;
            }
        }

        System.out.print("Status (Active/Inactive/Suspended): ");
        String userStatus = scanner.nextLine();

        User user = new User(
                username,
                passwordHash,
                fullName,
                email,
                contactNo,
                address,
                profilePicture,
                departmentId,
                userStatus
        );

        boolean success = userDAO.addUser(user);

        if (success) {
            System.out.println("\nUser added successfully.");
        } else {
            System.out.println("\nFailed to add user.");
        }
    }


    // ========================================
    // UPDATE USER
    // ========================================

    private static void updateUser() {

        System.out.println("\n========== UPDATE USER ==========");

        System.out.print("Enter User ID: ");

        long userId;

        try {

            userId = Long.parseLong(scanner.nextLine());

        } catch (NumberFormatException e) {

            System.out.println("Invalid User ID.");
            return;
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {

            System.out.println("User not found.");
            return;
        }

        System.out.println("\nCurrent user:");
        System.out.println(user);

        System.out.print("\nNew username: ");
        String username = scanner.nextLine();

        System.out.print("New full name: ");
        String fullName = scanner.nextLine();

        System.out.print("New email: ");
        String email = scanner.nextLine();

        System.out.print("New contact number: ");
        String contactNo = scanner.nextLine();

        System.out.print("New address: ");
        String address = scanner.nextLine();

        System.out.print("New profile picture: ");
        String profilePicture = scanner.nextLine();

        System.out.print("New Department ID (Enter for NULL): ");
        String departmentInput = scanner.nextLine();

        Integer departmentId = null;

        if (!departmentInput.isBlank()) {

            try {

                departmentId =
                        Integer.parseInt(departmentInput);

            } catch (NumberFormatException e) {

                System.out.println("Invalid Department ID.");
                return;
            }
        }

        System.out.print("New status (Active/Inactive/Suspended): ");
        String userStatus = scanner.nextLine();

        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setContactNo(contactNo);
        user.setAddress(address);
        user.setProfilePicture(profilePicture);
        user.setDepartmentId(departmentId);
        user.setUserStatus(userStatus);

        boolean success = userDAO.updateUser(user);

        if (success) {
            System.out.println("\nUser updated successfully.");
        } else {
            System.out.println("\nFailed to update user.");
        }
    }


    // ========================================
    // UPDATE PASSWORD
    // ========================================

    private static void updatePassword() {

        System.out.println("\n========== UPDATE PASSWORD ==========");

        System.out.print("Enter User ID: ");

        long userId;

        try {

            userId = Long.parseLong(scanner.nextLine());

        } catch (NumberFormatException e) {

            System.out.println("Invalid User ID.");
            return;
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {

            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter new password hash: ");

        String passwordHash = scanner.nextLine();

        boolean success =
                userDAO.updatePassword(userId, passwordHash);

        if (success) {
            System.out.println("\nPassword updated successfully.");
        } else {
            System.out.println("\nFailed to update password.");
        }
    }


    // ========================================
    // DELETE USER
    // ========================================

    private static void deleteUser() {

        System.out.println("\n========== DELETE USER ==========");

        System.out.print("Enter User ID: ");

        long userId;

        try {

            userId = Long.parseLong(scanner.nextLine());

        } catch (NumberFormatException e) {

            System.out.println("Invalid User ID.");
            return;
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {

            System.out.println("User not found.");
            return;
        }

        System.out.println("\nUser to delete:");
        System.out.println(user);

        System.out.print("\nAre you sure? (yes/no): ");

        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("yes")) {

            System.out.println("Delete cancelled.");
            return;
        }

        boolean success =
                userDAO.deleteUser(userId);

        if (success) {
            System.out.println("\nUser deleted successfully.");
        } else {
            System.out.println("\nFailed to delete user.");
        }
    }
}