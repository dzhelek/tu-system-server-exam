import java.util.ArrayList;
import java.util.List;

public class Student extends User{
    List<Grade> grades = new ArrayList<>();
    String id, studentID;

    public Student(String username, String password, String id, String studentID) {
        super(username, password);
        this.id = id;
        this.studentID = studentID;
    }
}
