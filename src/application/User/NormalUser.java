package application.User;

public class NormalUser extends User {

    public NormalUser(String username, String password) {
        super(username, password, UserType.NORMAL);
    }

    @Override
    public void accessFeatures() {
        System.out.println("Some feature of NormalUser");
    }
}
