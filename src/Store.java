import java.io.*;
import java.util.ArrayList;

public class Store implements Serializable {
    private String storeName;
    private Seller seller;
    private ArrayList<Product> products = new ArrayList<>();//all products associated with this store
    //as Store and Product(s) are connected to each other, each time store information is modified, all associated products should be updated as well
    private int productsSold;//total number of products sold

    public Store(String storeName, Seller seller, ArrayList<Product> products) {
        this.storeName = storeName;
        this.seller = seller;
        this.products = products;
        saveStore();
        updateProducts();
    }

    public Store(String storeName, Seller seller) {
        this.storeName = storeName;
        this.seller = seller;
        saveStore();
        updateProducts();
        //seller can create a store and add products later
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
        //then add the product to the list with all product names (from all stores) displayed in market
        try (PrintWriter writer = new PrintWriter(new FileOutputStream("Products.txt", true))) {
            writer.println(product.getProductName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        updateProducts();
    }

    public void deleteProduct(Product product) throws IOException {
        products.remove(product);
        //then also remove the product from the list of products displayed in market
        //read the names out, then write all the names except removed one back to file
        ArrayList<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line);
            }
        }
        try (PrintWriter writer = new PrintWriter(new FileOutputStream("Products.txt"))) {
            for (String name : names) {
                if (!name.equals(product.getProductName())) {
                    writer.println(name);
                }
            }
        }
        saveStore();
        updateProducts();
    }

    public void addSale(int amount) {
        this.productsSold += amount;
        saveStore();
        updateProducts();
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

    //As Store and Product(s) are connected to each other, each time store information is modified, all
    //associated products should be updated as well. It ensures info in every file is up-to-date.
    public void updateProducts() {
        for (int i = 0; i < products.size(); i++) {
            products.get(i).saveProduct();
        }
    }

    public String toString() {
        return String.format("%s,%s,%d\n%s", storeName, seller, productsSold, products.toString());
    }
}
