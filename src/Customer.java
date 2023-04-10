import java.io.*;
import java.util.*;

/**
 * This is Customer class, which sorts the user info of a customer, their shopping cart and purchase history.
 * It provides methods related methods to be called from the Market.
 *
 * @author Dimitri Paikos, Rayan Timmerman, Ekaterina Tszyao, Lab12
 * @version 4/10/2023
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 43L;
    private String username;
    private String password;
    private ArrayList<ShoppingCart> shoppingCart = new ArrayList<>();
    private ArrayList<PurchaseHistory> purchaseHistory = new ArrayList<>();

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

    public static ArrayList<Customer> loadAllCustomers() {
        ArrayList<Customer> allCustomers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("customerList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allCustomers.add(loadCustomer(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allCustomers;
    }

    public void addShoppingCart(Product product, Seller seller, int amount) {
        shoppingCart.add(new ShoppingCart(product, seller, amount));
        saveCustomer();
    }

    public void addPurchaseHistory(Product product, int amount) {
        purchaseHistory.add(new PurchaseHistory(product, amount));
        saveCustomer();
    }

    public ArrayList<PurchaseHistory> getPurchaseHistory() {
        return purchaseHistory;
    }

    public ArrayList<ShoppingCart> getShoppingCart() {
        return shoppingCart;
    }

    public void checkShoppingCart() {
        ArrayList<ShoppingCart> delete = new ArrayList<>();
        for (int i = 0; i < shoppingCart.size(); i++) {
            int j = shoppingCart.get(i).checkIfValid();
            if (j != 0) {
                if (j == 1) {
                    System.out.printf("%s in your shopping cart may have been removed or sold out!" +
                            "\nRemoving from shopping cart.", shoppingCart.get(i).getProduct().getProductName());
                    delete.add(shoppingCart.get(i));
                } else if (j == 2) {
                    System.out.printf("%s's quantity is too low for the amount in your shopping cart!" +
                            "\nRemoving from shopping cart.", shoppingCart.get(i).getProduct().getProductName());
                    delete.add(shoppingCart.get(i));
                }
            }
        }
        if (!delete.isEmpty()) {
            for (int i = 0; i < delete.size(); i++) {
                shoppingCart.remove(delete.get(i));
            }
        }
        saveCustomer();
    }

    public void setUsername(String username) {
        File file = new File(this.username);
        file.delete();

        file = new File("customers.txt");
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

        file = new File("customerList.txt");
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
        saveCustomer();
    }

    public void setPassword(String password) {
        this.password = password;
        saveCustomer();

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
        File file = new File("customerList.txt");
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

        file = new File("customers.txt");
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

    public void saveCustomer() { // saves state of customer USE THIS TO UPDATE CUSTOMER OBJECT FILE
        File f = new File(username); // writes seller to own file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public HashMap<String, Integer> purchaseHistoryByStore(ArrayList<Store> stores) {
        HashMap<String, Integer> storeHistory = new HashMap<String, Integer>();
        for (int i = 0; i < stores.size(); i++) {
            storeHistory.put(stores.get(i).getStoreName(), 0);
        }
        for (int i = 0; i < purchaseHistory.size(); i++) {
            String currentStoreName = purchaseHistory.get(i).getStoreName();
            int oldAmount = storeHistory.get(currentStoreName);
            storeHistory.put(currentStoreName, (oldAmount + purchaseHistory.get(i).getAmount()));
        }
        return storeHistory;
    }
}