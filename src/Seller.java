import java.io.*;

public class Seller implements Serializable{
    private String username;
    private String password;
    // sets up or makes sure account is correct
    public Seller(String username, String password, boolean newUser) throws NoUserException, AlreadyUserException {
        if (newUser) {
            if (writeAccount(username, password)) {
                this.username = username;
                this.password = password;
            } else { // if account exists throws error
                throw new AlreadyUserException("This account already exists!");
            }
            System.out.println("User successfully created!");
        } else {
            if (checkAccount(username, password)) {
                this.username = username;
                this.password = password;
            } else { // if account details are wrong throws error
                throw new NoUserException("This account does not exist!");
            }

        }

    }

    public boolean writeAccount(String username, String password) {
        File f = new File("sellers.txt");
        try (BufferedReader bfr = new BufferedReader(new FileReader(f))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(":")).equals(username))
                    return false;
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(f, true))) {
            pw.println(username + ":" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean checkAccount(String username, String password) {
        File f = new File("sellers.txt");
        try (BufferedReader bfr = new BufferedReader(new FileReader(f))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.equals(username + ":" + password))
                    return true;
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
