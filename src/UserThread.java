import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class UserThread implements Runnable {
    Socket socket;
    public UserThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            PrintWriter sender = new PrintWriter(socket.getOutputStream(), true);
            Scanner receiver = new Scanner(socket.getInputStream());
        ) {
            sender.println("Enter username: ");
            String username = receiver.nextLine();
            sender.println("Enter password: ");
            String password = receiver.nextLine();

            User user = Server.getUser(username, password);

            if (user != null) {
                System.out.println("logged successfully!");
            }
            else {
                System.out.println("unsuccessful login");
                return;
            }

            switch (user.getUserType()) {
                case STUDENT -> studentMenu(sender, receiver, (Student)user);
                case TEACHER -> teacherMenu(sender, receiver);
                case ADMIN -> adminMenu(sender, receiver);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void studentMenu(PrintWriter sender, Scanner receiver, Student student) {
//        student.grades.sort((grade1, grade2) -> {
//            return grade1.semester - grade2.semester;
//        });
        student.grades.sort((o1, o2) -> {
            if (o1.semester == o2.semester) {
                return o1.subject.compareTo(o2.subject);
            }
            if (o1.semester > o2.semester) {
                return 1;
            }
            else {
                return -1;
            }
        });
        for (Grade grade : student.grades) {
            sender.print("semester " + grade.semester + "\t" + grade.subject + "\t" + grade.value + ";");
        }
        sender.println("END");
    }
    public void teacherMenu(PrintWriter sender, Scanner receiver) {
        sender.println("Welcome to the teacher menu! Enter student id to write grades to: ");
        String studentID = receiver.nextLine();
        Student student = null;
        for (User user: Server.users) {
            if (user.getUserType() == UserType.STUDENT && ((Student)user).studentID.equals(studentID)) {
                student = (Student)user;
                break;
            }
        }
        if (student == null) {
            return;
        }
        sender.println("enter subject: ");
        String subject = receiver.nextLine();
        sender.println("enter semester number: ");
        int semester = receiver.nextInt();
        sender.println("enter grade: ");
        double value = receiver.nextDouble();
        student.grades.add(new Grade(subject, semester, value));
        sender.println("END");
    }
    public void adminMenu(PrintWriter sender, Scanner receiver) {
        String message = "Welcome to the admin menu! What user do you want to create: 1 - student 2 - teacher 3 - admin: ";
        sender.println(message);
        String type = receiver.nextLine();
        if (!type.equals("1") && !type.equals("2") && !type.equals("3")) {
            return;
        }
        sender.println("enter username: ");
        String username = receiver.nextLine();
        sender.println("enter password: ");
        String password = receiver.nextLine();
        if (!password.matches(".{5,}")){
            return;
        }

        switch (type) {
            case "1":
                sender.println("enter studentID: ");
                String studentID = receiver.nextLine();
                if (!studentID.matches("[0-9]{9}")) {
                    return;
                }
                sender.println("enter id: ");
                String id = receiver.nextLine();
                if (!id.matches("[0-9]{10}")) {
                    return;
                }
                Server.users.add(new Student(username, password, id, studentID));
                break;
            case "2":
                sender.println("enter email: ");
                String email = receiver.nextLine();
                if (!email.matches("[a-z]+@tu-sofia.bg")) {
                    return;
                }
                Server.users.add(new Teacher(username, password, email));
                break;
            case "3":
                Server.users.add(new Admin(username, password));
                break;
        }
        sender.println("END");
    }
}
