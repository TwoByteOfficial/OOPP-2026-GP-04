import dao.UserDAO;
import model.User;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       USER DAO TEST");
        System.out.println("=================================");

        UserDAO userDAO = new UserDAO();

        // Get all users
        List<User> users = userDAO.findAll();

        System.out.println("Total Users: " + users.size());

        for (User user : users) {

            System.out.println("----------------------------");
            System.out.println("User ID   : " + user.getUserId());
            System.out.println("Username  : " + user.getUsername());
            System.out.println("Full Name : " + user.getFullName());
            System.out.println("Email     : " + user.getEmail());
            System.out.println("Status    : " + user.getUserStatus());
        }

        System.out.println("=================================");
        System.out.println("       DAO TEST FINISHED");
        System.out.println("=================================");
    }
}