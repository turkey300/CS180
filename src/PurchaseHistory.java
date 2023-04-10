import java.io.Serializable;
/**
 * PurchaseHistory class
 * <p>
 * record purchases
 *
 * @author Ekaterina Tszyao, Ryan Timmerman, Dimitri Paikos
 * @version 04/10/23
 */
public class PurchaseHistory implements Serializable {
    private static final long serialVersionUID = 47L;
    private Product product;
    private int amount;
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