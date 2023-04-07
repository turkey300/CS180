import java.io.*;
import java.util.ArrayList;

public class Store implements Serializable {
    private String storeName;
    private String seller;    //username of the associated seller
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

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getStoreName() {
        return storeName;
    }

    public void addProduct(Product product) {//adds a new product to the store
        products.add(product);
        saveStore();
    }

    public void deleteProduct(Product product) throws IOException {
        products.remove(product);
        saveStore();
    }

    public void purchaseProductFromStore(int productIndex, int amount) {
        this.productsSold += amount;
        products.get(productIndex).purchase(amount);
        saveStore();
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
}
