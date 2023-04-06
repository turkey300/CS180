import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Customer {
    private String username;
    private String password;
    public Customer(String username, String password, boolean newUser) {
        if (newUser) {
            this.username = username;
            this.password = password;
            writeAccount();
        }

    }

    public void writeAccount() {
        File f = new File("customers.txt");
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(f, true))) {
            pw.println(username + ":" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
