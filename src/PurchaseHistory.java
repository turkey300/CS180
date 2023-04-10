import java.io.Serializable;

/**
 * Purchase History Class
 * <p>
 * used to record product's search history for dashboard in the marketplace
 *
 * @author Ryan Timmerman, Ekaterina Tszyao, Dimitri Paikos, Tyler Kei
 * @version 04/10/23
 */
public class PurchaseHistory implements Serializable {
    private static final long serialVersionUID = 47L;
    private final Product product;
    private final int amount;

    public PurchaseHistory(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public int getAmount() {
        return amount;
    }

    public String getStoreName() {
        return (product.getStoreName());
    }
}