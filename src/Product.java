import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Product implements Serializable {
    private static final long serialVersionUID = 45L;
    private String productName;
    private String description;
    private int availableQuantity;
    private double price;
    private String storeName;    //the store and product are connected to each other, so each modification to the
    // product should also be updated in the store class by "store.saveStore()".
    private int sale;    //number of items sold

    public Product(String productName, String description, int availableQuantity, double price, String storeName) {
        this.productName = productName;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.price = price;
        this.storeName = storeName;
        saveProduct();
    }

    public String getDescription() {
        return description;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductName() {
        return productName;
    }

    public int getSale() {return sale;}

    public double getPrice() {
        return price;
    }

    public boolean purchase(int purchasedAmt) {    //return boolean to indicate weather purchased successfully
        if (purchasedAmt > availableQuantity) {
            return false;    //cannot purchase
        }
        this.availableQuantity -= purchasedAmt;
        this.sale += purchasedAmt;
        saveProduct();
        return true;
    }

    public String marketplaceDisplay() {    //returns info displayed on the centralized page
        String info = String.format("Product name: %s, price: %.2f, available in store: %s",
                productName, price, storeName);
        return info;
    }

    public String productPageDisplay() {    //returns info displayed on the product's page
        String info = String.format("Product name: %s\nDescription:%s\nQuantity Available:%d\n" +
                        "Price: %.2f\nAvailable in store: %s\n",
                productName, description, availableQuantity, price, storeName);
        return info;
    }


    public void editProductName(String productName) {
        this.productName = productName;
        saveProduct();
    }

    public void editDescription(String description) {
        this.description = description;
        saveProduct();
    }

    public void editAvailableQuantity(int availableQuantity) {
        //directly editing this field should only be allowed to sellers
        this.availableQuantity = availableQuantity;
        saveProduct();
    }

    public void editPrice(double price) {
        this.price = price;
        saveProduct();
    }

    public String toString() {
        String info = String.format("%s,%s,%d,%.2f,%s",
                productName, description, availableQuantity, price, storeName);
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
}

class ProductComparatorByPrice implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        if (o1.getPrice() == o2.getPrice()) {
            return 0;
        }
        int dif = (o1.getPrice() - o2.getPrice()) > 0 ? 1 : -1;
        return dif;
    }
}

class ProductComparatorByAvailability implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o1.getAvailableQuantity() - o2.getAvailableQuantity();
    }
}