import java.util.ArrayList;

public class ShoppingCart {
    private Product product;
    private int sellerIndex;
    private int storeIndex;
    private int productIndex;
    private int amount;

    // initiates shopping cart, contains product and where it is located in seller/store
    public ShoppingCart(Product product, int sellerIndex, int storeIndex, int productIndex, int amount) {
        this.product = product;
        this.sellerIndex = sellerIndex;
        this.storeIndex = storeIndex;
        this.productIndex = productIndex;
        this.amount = amount;
    }

    //checks if product can be found in the store
    public int checkIfValid() { // returns 0 if product is in store, 1 if product cannot be found, and 2 if there is not enough amount of product left
        ArrayList<Seller> sellers = Seller.loadAllSellers();
        int valid = 1;
        try {
            Product testProduct = sellers.get(sellerIndex).getStore(storeIndex).getProduct(productIndex);
            // this only checks if the product is in the same position in store, seller could have removed and added product and this will return 1 but I think this is ok
            if (testProduct.equals(product.getProductName())) { // only checks if product name is equal, could change this if we wanted to
                valid = 0;
                if (product.getAvailableQuantity() < amount)
                    valid = 2;
            }
        } catch (Exception e) {
            valid = 1;
        }

        return valid;
    }
    public Product getProduct() {
        return product;
    }

    public int getSellerIndex() {
        return sellerIndex;
    }

    public int getStoreIndex() {
        return storeIndex;
    }

    public int getProductIndex() {
        return productIndex;
    }


}

