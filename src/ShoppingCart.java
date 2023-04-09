import java.io.Serializable;
import java.util.ArrayList;

public class ShoppingCart implements Serializable {
    private static final long serialVersionUID = 46L;
    private Product product;
    private Seller seller;
    private int amount;

    // initiates shopping cart, contains product and where it is located in seller/store
    public ShoppingCart(Product product, Seller seller, int amount) {
        this.product = product;
        this.seller = seller;
        this.amount = amount;
    }

    //checks if product can be found in the store
    public int checkIfValid() { // returns 0 if product is in store, 1 if product cannot be found, and 2 if there is not enough amount of product left
        ArrayList<Seller> sellers = Seller.loadAllSellers();
        int a = -1;
        int b;
        int c;

        for (int i = 0; i < sellers.size(); i++) {
            ArrayList<Store> stores = sellers.get(i).getStores();
            for (int j = 0; j < stores.size(); j++) {
                ArrayList<Product> products = stores.get(j).getProducts();
                for (int k = 0; k < products.size(); k++) {
                    if (products.get(k).getProductName().equals(product.getProductName())) {
                        a = i;
                        b = j;
                        c = k;
                        if (sellers.get(i).getStore(j).getProduct(k).getAvailableQuantity() < amount)
                            return 2;
                    }
                }
            }
        }
        if (a == -1)
            return 1;

        return 1;
    }
    public Product getProduct() {
        return product;
    }
}

