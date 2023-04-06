import java.io.*;
import java.util.ArrayList;

public class Product implements Serializable {
    private String productName;
    private String description;
    private int availableQuantity;
    private double price;
    private Store store;    //the store and product are connected to each other, so each modification to the
    // product should also be updated in the store class by "store.saveStore()".
    private int sale;    //number of items sold

    public Product(String productName, String description, int availableQuantity, double price, Store store) {
        this.productName = productName;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.price = price;
        this.store = store;
        saveProduct();
    }

    public Store getStore() {
        return store;
    }

    public String getProductName() {
        return productName;
    }

    public boolean purchase(int purchasedAmt) {    //return boolean to indicate weather purchased successfully
        if (purchasedAmt > availableQuantity) {
            return false;    //cannot purchase
        }
        this.availableQuantity -= purchasedAmt;
        this.sale += purchasedAmt;
        saveProduct();
        store.saveStore();    //also updates the information in associated store
        return true;
    }

    public String marketplaceDisplay() {    //returns info displayed on the centralized page
        String info = String.format("Product name: %s, price: %.2f, available in store: %s",
                productName, price, store.getStoreName());
        return info;
    }

    public String productPageDisplay() {    //returns info displayed on the product's page
        String info = String.format("Product name: %s\nDescription:%s\nQuantity Available:%d\n" +
                        "Price: %.2f\nAvailable in store: %s\n",
                productName, description, availableQuantity, price, store.getStoreName());
        return info;
    }

    public void editProductName(String productName) {
        String oldName = this.productName;
        this.productName = productName;
        saveProduct();
        //As Products.txt, a list of all available products identifies each of them by product name,
        //I have to change the old product name in the file to the new name.
        ArrayList<String> productNames = new ArrayList<>();
        File file = new File("Products.txt");
        //reads all the names out, replacing old name with new name of current product
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(oldName)) {
                    productNames.add(productName);
                } else {
                    productNames.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //write all the names back
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            for (String name : productNames) {
                writer.println(name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        store.saveStore();
    }

    public void editDescription(String description) {
        this.description = description;
        saveProduct();
        store.saveStore();
    }

    public void editAvailableQuantity(int availableQuantity) {     //directly editing this field should only be allowed to sellers
        this.availableQuantity = availableQuantity;
        saveProduct();
        store.saveStore();
    }

    public void editPrice(double price) {
        this.price = price;
        saveProduct();
        store.saveStore();
    }

    public String toString() {
        String info = String.format("%s,%s,%d,%.2f,%s",
                productName, description, availableQuantity, price, store.getStoreName());
        return info;
    }

    public void saveProduct() {    //saves product info into a file named as product name
        File file = new File(productName);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Product loadProduct(String productName) {
        File file = new File(productName);
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
            Product product = (Product) out.readObject();
            return product;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //goes through the products and loads all products available. Used for displaying all products in marketplace.
    public static ArrayList<Product> loadAllProducts() {
        ArrayList<Product> allProducts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allProducts.add(loadProduct(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allProducts;
    }
}
