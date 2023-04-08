import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Seller implements Serializable {
    private String username;
    private String password;
    private ArrayList<Store> stores = new ArrayList<>();
    // sets up or makes sure account is correct
    public Seller(String username, String password, boolean newUser) throws AlreadyUserException, OtherUserException {
        if (newUser) {
            if (Customer.checkIfCustomer(username))
                throw new OtherUserException("This is a customer account!\nTry logging in as a customer.");
            if (writeAccount(username, password)) {
                this.username = username;
                this.password = password;
                writeSeller();
            } else { // if account exists throws error
                throw new AlreadyUserException("This account already exists!");
            }
            System.out.println("User successfully created!");
        }
    }

    public static boolean checkIfSeller(String username){ // checks if username is customer
        File file = new File("sellerList.txt"); // adds username to list
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

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void setStore(Store stores) {
        this.stores.add(stores);
        saveSeller();
    }

    public String getUsername() {
        return username;
    }

    public void saveSeller() { // saves state of seller
        File f = new File(username); // writes seller to own file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeSeller() {
        File f = new File(username); // writes seller to own file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File("sellerList.txt"); // adds username to list
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, true))) {
            pw.println(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Seller loadSeller(String username) {
        File file = new File(username);
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
            Seller seller = (Seller) out.readObject();
            return seller;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    public static boolean checkAccount(String username, String password) {
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
