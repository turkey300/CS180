import java.io.*;

public class Customer implements Serializable {
    private String username;
    private String password;
    // sets up or makes sure account is correct
    public Customer(String username, String password, boolean newUser) throws AlreadyUserException, OtherUserException {
        if (newUser) {
            if (Seller.checkIfSeller(username))
                throw new OtherUserException("This is a seller account!\nTry logging in as a seller");
            if (writeAccount(username, password)) {
                this.username = username;
                this.password = password;
                writeCustomer();
            } else { // if account exists throws error
                throw new AlreadyUserException("This account already exists!");
            }
            System.out.println("User successfully created!");
        }
    }

    public String getUsername() {
        return username;
    }

    public static boolean checkIfCustomer(String username) { // checks if username is customer
        File file = new File("customerList.txt"); // adds username to list
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.equals(username))
                    return true;
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public void writeCustomer() {
        File f = new File(username); // writes customer to own file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File("customerList.txt"); // adds username to list
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, true))) {
            pw.println(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Customer loadCustomer(String username) {
        File file = new File(username);
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
            Customer customer = (Customer) out.readObject();
            return customer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean writeAccount(String username, String password) {
        File f = new File("customers.txt");
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

    public static boolean checkAccount(String username, String password) {
        File f = new File("customers.txt");
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
