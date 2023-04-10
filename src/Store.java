import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This is Store class, which contains information about a store, a list of products in the store and lists keeping
 * track of statistics for this store. It provides methods related methods to be called from the Market.
 *
 * @author Ekaterina Tszyao, Dimitri Paikos, Rayan Timmerman, Lab12
 * @version 4/10/2023
 */
public class Store implements Serializable {
    private static final long serialVersionUID = 44L;
    private String storeName;
    private String seller;    //username of the associated seller
    private ArrayList<Product> products = new ArrayList<>();//all products associated with this store
    //as Store contains Product(s) as a field, have to access the product through the store
    private int productsSold;//total number of products sold

    private ArrayList<Integer> purchased = new ArrayList<>();

    private ArrayList<Double> revenue = new ArrayList<>(); //Revenue for each sale in a list for the store

    private ArrayList<String> custlist = new ArrayList<>();

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
            customer.addPurchaseHistory(product, amount);
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

    public String toString() {
        return String.format("%s,%s,%d\n%s", storeName, seller, productsSold, products.toString());
    }
}

/**
 * This is a comparator which compares Store objects by comparing their sales (products sold). It's used
 * to sort an ArrayList of stores by their sales (products sold).
 *
 * @author Ekaterina Tszyao, Lab12
 * @version 4/10/2023
 */
class StoreComparatorByProductsSold implements Comparator<Store> {
    @Override
    public int compare(Store o1, Store o2) {
        return o1.getProductsSold() - o2.getProductsSold();
    }
}