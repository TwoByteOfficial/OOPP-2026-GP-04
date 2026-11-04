package util;

import java.io.File;

public class FindPath {
    /**
     * Searches the entire file system for a target file or directory name
     * and returns its absolute path.
     *
     * @param targetName The name of the file or folder to find (e.g., "OOPP-2026-GP-04")
     * @return The absolute path string if found, otherwise null.
     */
    public static String findAbsolutePath(String targetName) {
        // Get all root directories (e.g., C:\, D:\ on Windows, or / on Unix)
        File[] roots = File.listRoots();

        if (roots != null) {
            for (File root : roots) {
                String result = searchDirectory(root, targetName);
                if (result != null) {
                    return result; // Return immediately if found
                }
            }
        }
        return null; // Not found
    }

    // Helper method to recursively look through directories
    private static String searchDirectory(File currentFile, String targetName) {
        // Base case: check if the current file/folder matches the target name
        if (currentFile.getName().equalsIgnoreCase(targetName)) {
            return currentFile.getAbsolutePath();
        }

        // If it's a directory, look inside it
        if (currentFile.isDirectory()) {
            File[] files = currentFile.listFiles();

            // listFiles() can return null if permission is denied
            if (files != null) {
                for (File file : files) {
                    String foundPath = searchDirectory(file, targetName);
                    if (foundPath != null) {
                        return foundPath;
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String target = "OOPP-2026-GP-04";
        System.out.println("Searching for the absolute path to: " + target + "...");

        String absolutePath = findAbsolutePath(target);

        if (absolutePath != null) {
            System.out.println("Path Found: " + absolutePath);
        } else {
            System.out.println("Could not find " + target + " on this system.");
        }
    }
}
