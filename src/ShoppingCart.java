import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is a Shopping Cart records the product, its seller and amount a customer had put into their shopping cart.
 *
 * @author Dimitri Paikos, Tyler Kei, Lab12
 * @version 4/10/2023
 */
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
    public int checkIfValid() {
        // returns 0 if product is in store, 1 if product cannot be found,
        //and 2 if there is not enough amount of product left
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

        return 0;
    }

    public Product getProduct() {
        return product;
    }

    public int getAmount() {
        return amount;
    }

    public Seller getSeller() {
        return seller;
    }
}

