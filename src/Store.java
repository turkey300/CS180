import java.io.*;
import java.util.ArrayList;

/**
 * Store class
 * <p>
 * creates store object that are owned by a seller
 *
 * @author Ryan Timmerman, Ekaterina Tszyao, Dimitri Paikos, Tyler Kei
 * @version 04/10/23
 */

public class Store implements Serializable {
    private static final long serialVersionUID = 44L;
    private final String storeName;
    private final String seller;    //username of the associated seller
    private final ArrayList<Integer> purchased = new ArrayList<>();
    private final ArrayList<Double> revenue = new ArrayList<>(); //Revenue for each sale in a list for the store
    private final ArrayList<String> custlist = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();//all products associated with this store
    //as Store contains Product(s) as a field, have to access the product through the store
    private int productsSold;//total number of products sold

    public Store(String storeName, String seller, ArrayList<Product> products) {
        this.storeName = storeName;
        this.seller = seller;
        this.products = products;
        saveStore();
    }

    public Store(String storeName, String seller) {
        this.storeName = storeName;
        this.seller = seller;
        saveStore();
    }

    //given store name, loads the store from file
    public static Store loadStore(String storeName) {
        File file = new File(storeName);
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
            Store store = (Store) out.readObject();
            return store;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Store> loadAllStores() {
        ArrayList<Store> allStores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Stores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allStores.add(loadStore(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allStores;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public Product getProduct(int i) {
        return (products.get(i));
    }

    public int getProductIndex(Product product) {
        return (products.indexOf(product));
    }

    public String getStoreName() {
        return storeName;
    }

    public String getSeller() {
        return seller;
    }

    public int getProductsSold() {
        return productsSold;
    }

    public ArrayList<Double> getRevenue() {
        return revenue;
    }

    public ArrayList<String> getCustList() {
        return custlist;
    }

    public ArrayList<Integer> getPurchased() {
        return purchased;
    }

    public void addProduct(Product product) {//adds a new product to the store
        products.add(product);
        saveStore();
    }

    public void deleteProduct(Product product) {
        products.remove(product);
        saveStore();
    }

    public boolean purchaseProductFromStore(Product product, int amount, Customer customer) {
        if (product.purchase(amount)) {
            this.productsSold += amount;
            this.revenue.add(product.getPrice() * amount);
            this.custlist.add(customer.getUsername());
            this.purchased.add(amount);
            saveStore();
            return true;
        } else {
            return false;
        }
    }

    //saves store info into a file named as the store's name
    public void saveStore() {
        File file = new File(storeName);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return String.format("%s,%s,%d\n%s", storeName, seller, productsSold, products.toString());
    }
}
