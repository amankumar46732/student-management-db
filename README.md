# Student Management System (Java + JDBC + SQLite)

A console application that stores students and their marks in a relational database.

## Features
- Add, view, search, update, and delete students
- Add marks (subject and score) for a student
- View the marks of a student
- Report: number of subjects and average score per student (JOIN + GROUP BY)

## Database design
Two tables with a one-to-many relationship (one student has many marks):

```sql
CREATE TABLE students (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    name   TEXT NOT NULL,
    branch TEXT NOT NULL,
    year   INTEGER
);

CREATE TABLE marks (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    subject    TEXT NOT NULL,
    score      INTEGER CHECK (score BETWEEN 0 AND 100),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);
```

Marks are kept in a separate table so student details are not repeated for every subject.
Deleting a student also deletes their marks (`ON DELETE CASCADE`).

## Concepts used
- **DBMS:** primary key, foreign key, constraints, one-to-many relationship, normalization
- **SQL:** INSERT, SELECT with WHERE and LIKE, UPDATE, DELETE, LEFT JOIN, GROUP BY with COUNT and AVG
- **Java:** JDBC (`Connection`, `PreparedStatement`, `ResultSet`), classes and objects, DAO pattern, try-with-resources, exception handling

## Project structure
- `Student.java`: data class for one student
- `Database.java`: opens the connection and creates the tables
- `StudentDAO.java`: all SQL queries
- `Main.java`: console menu

## How to run
1. Download the SQLite JDBC driver jar (`sqlite-jdbc`) from its official GitHub releases or Maven Central page, and put it in a `lib` folder inside the project.
2. Compile:
   ```
   javac -cp "lib/*" *.java
   ```
3. Run (Mac/Linux):
   ```
   java -cp ".:lib/*" Main
   ```
   Run (Windows):
   ```
   java -cp ".;lib/*" Main
   ```

The database file `students.db` is created automatically on the first run.

## What I learned
[Write 2 to 3 lines in your own words, for example: why PreparedStatement is used, or what the JOIN query does.]

## Possible improvements
- Edit or delete individual marks
- Export the report to a CSV file
- Add a simple login
