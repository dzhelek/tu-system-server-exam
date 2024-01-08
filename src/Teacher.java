public class Teacher extends User{
    String email;

    public Teacher(String username, String password, String email) {
        super(username, password);
        this.email = email;
    }
}
