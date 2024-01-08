import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
//    public static HashMap<String, String> users = new HashMap<>();

    public static List<User> users = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        users.add(new Student("stefi", "1234", "123456789", "381222028"));
        users.add(new Student("yoan", "1212", "123456789", "381222003"));
        users.add(new Teacher("sasho", "1234", "sasho@tu-sofia.bg"));
        users.add(new Teacher("pepi", "1111", "pepi@tu-sofia.bg"));
        users.add(new Admin("silviya", "1234"));
        try (
            ServerSocket server = new ServerSocket(8787);
            ExecutorService pool = Executors.newFixedThreadPool(5);
        ) {
            while (true) {
                Socket socket = server.accept();
                pool.execute(new UserThread(socket));
                System.out.println("client is connected");
            }
        }
    }
    public static User getUser (String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                return user;
            }
        }

        return null;
    }
}
