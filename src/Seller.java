import java.io.*;
import java.util.ArrayList;

/**
 * This is a Seller class which sorts the user info of a seller, as well as thw stores they own.
 * It provides methods related methods to be called from the Market.
 *
 * @author Dimitri Paikos,Ekaterina Tszyao, Ryan Timmerman, Lab12
 * @version 4/10/2023
 */
public class Seller implements Serializable {
    private static final long serialVersionUID = 42L;
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

    public void setUsername(String username1) {
        File file = new File(this.username);
        file.delete();

        file = new File("sellers.txt");
        ArrayList<String> lines2 = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(":")).equals(this.username)) {
                    lines2.add(username1 + ":" + password);
                } else {
                    lines2.add(line);
                }
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, false))) {
            for (int i = 0; i < lines2.size(); i++) {
                pw.println(lines2.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        file = new File("sellerList.txt");
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.equals(this.username)) {
                    lines.add(username1);
                } else {
                    lines.add(line);
                }
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, false))) {
            for (int i = 0; i < lines.size(); i++) {
                pw.println(lines.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.username = username;
        saveSeller();
    }

    public void setPassword(String password1) {
        this.password = password1;
        saveSeller();

        File file = new File("sellers.txt");
        ArrayList<String> lines2 = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(":")).equals(username)) {
                    lines2.add(line.substring(0, line.indexOf(":")) + ":" + password1);
                } else {
                    lines2.add(line);
                }
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, false))) {
            for (int i = 0; i < lines2.size(); i++) {
                pw.println(lines2.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount() { // deletes all parts of account
        File file = new File("sellerList.txt");
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (!line.equals(username))
                    lines.add(line);
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, false))) {
            for (int i = 0; i < lines.size(); i++) {
                pw.println(lines.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        file = new File(username);
        file.delete();

        file = new File("sellers.txt");
        ArrayList<String> lines2 = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (!line.equals(username + ":" + password))
                    lines2.add(line);
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, false))) {
            for (int i = 0; i < lines2.size(); i++) {
                pw.println(lines2.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfSeller(String username1) { // checks if username is seller
        File file = new File("sellerList.txt"); // adds username to list
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.equals(username1))
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

    public Store getStore(int i) {
        return (stores.get(i));
    }

    public int getStoreIndex(Store store) {
        return (stores.indexOf(store));
    }

    public void addStore(Store stores1) {
        this.stores.add(stores1);
        saveSeller();
    }

    public String getUsername() {
        return username;
    }

    public void saveSeller() { // saves state of seller USE THIS TO UPDATE SELLER OBJECT FILE
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

    public static Seller loadSeller(String username1) {
        File file = new File(username1);
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
            Seller seller = (Seller) out.readObject();
            return seller;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean writeAccount(String username1, String password1) {
        File f = new File("sellers.txt");
        try (BufferedReader bfr = new BufferedReader(new FileReader(f))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(":")).equals(username1))
                    return false;
                line = bfr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(f, true))) {
            pw.println(username1 + ":" + password1);
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

    public static ArrayList<Seller> loadAllSellers() {
        ArrayList<Seller> allSellers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("sellerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allSellers.add(loadSeller(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allSellers;
    }

    public String toString() {
        return (username + password + stores);
    }
}
