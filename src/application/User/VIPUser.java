package application.User;

public class VIPUser extends User {

    public VIPUser(String username, String password) {
        super(username, password, UserType.VIP);
    }

    @Override
    public void accessFeatures() {
        System.out.println("Some feature of VIPUser");
    }
}
