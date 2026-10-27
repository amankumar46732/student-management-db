import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object.
 * All the SQL of the project is kept in this one class.
 * We always use PreparedStatement with "?" so user input is never joined
 * directly into the SQL text (this prevents SQL injection).
 */
public class StudentDAO {

    // INSERT: add a new student
    public void addStudent(String name, String branch, int year) throws SQLException {
        String sql = "INSERT INTO students(name, branch, year) VALUES (?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, branch);
            ps.setInt(3, year);
            ps.executeUpdate();
        }
    }

    // SELECT: get all students
    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT id, name, branch, year FROM students ORDER BY id";
        List<Student> list = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(readStudent(rs));
            }
        }
        return list;
    }

    // SELECT ... WHERE ... LIKE: search students by part of the name
    public List<Student> searchByName(String text) throws SQLException {
        String sql = "SELECT id, name, branch, year FROM students "
                + "WHERE name LIKE ? ORDER BY id";
        List<Student> list = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + text + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(readStudent(rs));
                }
            }
        }
        return list;
    }

    // UPDATE: change a student's details. Returns true if the student was found.
    public boolean updateStudent(int id, String name, String branch, int year) throws SQLException {
        String sql = "UPDATE students SET name = ?, branch = ?, year = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, branch);
            ps.setInt(3, year);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE: remove a student. Because of ON DELETE CASCADE,
    // the student's marks are deleted automatically too.
    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Checks whether a student with this id exists.
    public boolean studentExists(int id) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // INSERT into the second table: add marks for a student
    public void addMarks(int studentId, String subject, int score) throws SQLException {
        String sql = "INSERT INTO marks(student_id, subject, score) VALUES (?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, subject);
            ps.setInt(3, score);
            ps.executeUpdate();
        }
    }

    // SELECT: print all marks of one student
    public void printMarksOfStudent(int studentId) throws SQLException {
        String sql = "SELECT subject, score FROM marks WHERE student_id = ? ORDER BY subject";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                System.out.printf("%-20s %s%n", "Subject", "Score");
                while (rs.next()) {
                    found = true;
                    System.out.printf("%-20s %d%n", rs.getString("subject"), rs.getInt("score"));
                }
                if (!found) {
                    System.out.println("No marks added yet.");
                }
            }
        }
    }

    // JOIN + GROUP BY report: for every student, count the subjects
    // and calculate the average score.
    // LEFT JOIN keeps students who have no marks yet.
    public void printReport() throws SQLException {
        String sql = "SELECT s.id, s.name, COUNT(m.id) AS subjects, AVG(m.score) AS average "
                + "FROM students s "
                + "LEFT JOIN marks m ON s.id = m.student_id "
                + "GROUP BY s.id, s.name "
                + "ORDER BY s.id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-5s %-20s %-10s %s%n", "ID", "Name", "Subjects", "Average");
            boolean found = false;
            while (rs.next()) {
                found = true;
                int subjects = rs.getInt("subjects");
                String average = subjects == 0 ? "-" : String.format("%.2f", rs.getDouble("average"));
                System.out.printf("%-5d %-20s %-10d %s%n",
                        rs.getInt("id"), rs.getString("name"), subjects, average);
            }
            if (!found) {
                System.out.println("No students yet.");
            }
        }
    }

    // Helper: turns the current row of a ResultSet into a Student object.
    private Student readStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("branch"),
                rs.getInt("year"));
    }
}
