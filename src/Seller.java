import java.io.*;
import java.util.ArrayList;

/**
 * Seller object
 * <p>
 * seller object to save data for people who log on to marketplace as a seller user
 *
 * @author Ryan Timmerman, Ekaterina Tszyao, Dimitri Paikos, Tyler Kei
 * @version 04/10/23
 */
public class Seller implements Serializable {
    private static final long serialVersionUID = 42L;
    private final ArrayList<Store> stores = new ArrayList<>();
    private String username;
    private String password;

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

    public static boolean checkIfSeller(String username) { // checks if username is seller
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

    public void setPassword(String password) {
        this.password = password;
        saveSeller();

        File file = new File("sellers.txt");
        ArrayList<String> lines2 = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(":")).equals(username)) {
                    lines2.add(line.substring(0, line.indexOf(":")) + ":" + password);
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

    public ArrayList<Store> getStores() {
        return stores;
    }

    public Store getStore(int i) {
        return (stores.get(i));
    }

    public int getStoreIndex(Store store) {
        return (stores.indexOf(store));
    }

    public void addStore(Store stores) {
        this.stores.add(stores);
        saveSeller();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        File file = new File(this.username);
        file.delete();

        file = new File("sellers.txt");
        ArrayList<String> lines2 = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
            String line = bfr.readLine();
            while (line != null) {
                if (line.substring(0, line.indexOf(":")).equals(this.username)) {
                    lines2.add(username + ":" + password);
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
                    lines.add(username);
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

    public void viewDashBoard(File f) {

    }

    public String toString() {
        return (username + password + stores);
    }
}
