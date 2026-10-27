/**
 * Represents one row of the students table.
 */
public class Student {
    private int id;
    private String name;
    private String branch;
    private int year;

    public Student(int id, String name, String branch, int year) {
        this.id = id;
        this.name = name;
        this.branch = branch;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-20s %-12s %d", id, name, branch, year);
    }
}
