import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens connections to the SQLite database file and creates the tables.
 */
public class Database {

    // The database is a single file named students.db in the project folder.
    private static final String URL = "jdbc:sqlite:students.db";

    // Opens a new connection. SQLite needs "PRAGMA foreign_keys = ON"
    // on every connection, otherwise foreign keys are not enforced.
    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return con;
    }

    // Creates the two tables if they do not exist yet.
    public static void createTables() throws SQLException {
        String studentsTable =
                "CREATE TABLE IF NOT EXISTS students ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "branch TEXT NOT NULL, "
                + "year INTEGER)";

        String marksTable =
                "CREATE TABLE IF NOT EXISTS marks ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "student_id INTEGER NOT NULL, "
                + "subject TEXT NOT NULL, "
                + "score INTEGER CHECK (score BETWEEN 0 AND 100), "
                + "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE)";

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            st.execute(studentsTable);
            st.execute(marksTable);
        }
    }
}
