import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu for the Student Management System.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentDAO dao = new StudentDAO();

    public static void main(String[] args) {
        try {
            Database.createTables();
        } catch (SQLException e) {
            System.out.println("Could not open the database: " + e.getMessage());
            System.out.println("Check that the sqlite-jdbc jar is on the classpath (see README).");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        viewAllStudents();
                        break;
                    case 3:
                        searchStudents();
                        break;
                    case 4:
                        updateStudent();
                        break;
                    case 5:
                        deleteStudent();
                        break;
                    case 6:
                        addMarks();
                        break;
                    case 7:
                        viewMarks();
                        break;
                    case 8:
                        dao.printReport();
                        break;
                    case 9:
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Please choose a number from 1 to 9.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Student Management System =====");
        System.out.println("1. Add student");
        System.out.println("2. View all students");
        System.out.println("3. Search student by name");
        System.out.println("4. Update student");
        System.out.println("5. Delete student");
        System.out.println("6. Add marks for a student");
        System.out.println("7. View marks of a student");
        System.out.println("8. Report (subjects and average per student)");
        System.out.println("9. Exit");
    }

    // ---------- Menu actions ----------

    private static void addStudent() throws SQLException {
        String name = readText("Name: ");
        String branch = readText("Branch (for example CSE): ");
        int year = readYear();
        dao.addStudent(name, branch, year);
        System.out.println("Student added.");
    }

    private static void viewAllStudents() throws SQLException {
        printStudents(dao.getAllStudents());
    }

    private static void searchStudents() throws SQLException {
        String text = readText("Enter part of the name: ");
        printStudents(dao.searchByName(text));
    }

    private static void updateStudent() throws SQLException {
        int id = readInt("Student id to update: ");
        if (!dao.studentExists(id)) {
            System.out.println("No student with that id.");
            return;
        }
        String name = readText("New name: ");
        String branch = readText("New branch: ");
        int year = readYear();
        dao.updateStudent(id, name, branch, year);
        System.out.println("Student updated.");
    }

    private static void deleteStudent() throws SQLException {
        int id = readInt("Student id to delete: ");
        if (dao.deleteStudent(id)) {
            System.out.println("Student and their marks deleted.");
        } else {
            System.out.println("No student with that id.");
        }
    }

    private static void addMarks() throws SQLException {
        int id = readInt("Student id: ");
        if (!dao.studentExists(id)) {
            System.out.println("No student with that id.");
            return;
        }
        String subject = readText("Subject: ");
        int score = readInt("Score (0 to 100): ");
        if (score < 0 || score > 100) {
            System.out.println("Score must be between 0 and 100.");
            return;
        }
        dao.addMarks(id, subject, score);
        System.out.println("Marks added.");
    }

    private static void viewMarks() throws SQLException {
        int id = readInt("Student id: ");
        if (!dao.studentExists(id)) {
            System.out.println("No student with that id.");
            return;
        }
        dao.printMarksOfStudent(id);
    }

    private static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-5s %-20s %-12s %s%n", "ID", "Name", "Branch", "Year");
        for (Student s : students) {
            System.out.println(s);
        }
    }

    // ---------- Safe input helpers ----------

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static int readYear() {
        while (true) {
            int year = readInt("Year (1 to 4): ");
            if (year >= 1 && year <= 4) {
                return year;
            }
            System.out.println("Year must be between 1 and 4.");
        }
    }

    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String text = scanner.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This cannot be empty.");
        }
    }
}
