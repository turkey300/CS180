@@ -0,0 +1,21 @@
public class PurchaseHistory {
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