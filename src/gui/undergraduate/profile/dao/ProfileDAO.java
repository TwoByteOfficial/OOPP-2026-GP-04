package gui.undergraduate.profile.dao;

import gui.undergraduate.profile.model.UserProfileData;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object managing student profiles across the cohort.
 * Dynamically resolves details for logged-in undergraduates.
 */
public class ProfileDAO {

    private static final Map<String, UserProfileData> STUDENT_REGISTRY = new HashMap<>();

    static {
        // Sample cohort entries from the 20 registered undergraduates
        registerStudent(1, "TG2001", "Kavindu Madushan", "kavindu2001@fot.ruh.ac.lk");
        registerStudent(2, "TG2002", "Nimasha Dilrukshi", "nimasha2002@fot.ruh.ac.lk");
        registerStudent(3, "TG2003", "Sachithra Bandara", "sachithra2003@fot.ruh.ac.lk");
        registerStudent(74, "TG2074", "Piumi Senanayake", "tg2074@fot.ruh.ac.lk");
    }

    private static void registerStudent(long id, String username, String fullName, String email) {
        STUDENT_REGISTRY.put(username.toUpperCase(), new UserProfileData(
                id,
                "TG/2022/" + username.replaceAll("\\D+", ""),
                fullName.toUpperCase(),
                email,
                "Department of Information Technology",
                "Bachelor of Information & Communication Technology (BICT Hons)",
                "Level II",
                "Semester I (2026/2027)",
                "Active Enrolment"
        ));
    }

    public static UserProfileData getProfileByUserId(long userId, String username) {
        String key = (username != null && !username.trim().isEmpty()) ? username.trim().toUpperCase() : "TG2074";

        if (STUDENT_REGISTRY.containsKey(key)) {
            return STUDENT_REGISTRY.get(key);
        }

        // Dynamic generator for any student ID (e.g. TG2004 - TG2020)
        String regNumber = "TG/2022/" + (userId > 0 ? userId : "1074");
        String name = key.matches("TG\\d+") ? "UNDERGRADUATE STUDENT (" + key + ")" : key;
        String email = key.toLowerCase() + "@fot.ruh.ac.lk";

        return new UserProfileData(
                userId,
                regNumber,
                name,
                email,
                "Department of Information Technology",
                "Bachelor of Information & Communication Technology (BICT Hons)",
                "Level II",
                "Semester I (2026/2027)",
                "Active Enrolment"
        );
    }
}