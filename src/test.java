public class test {
    public static void main(String[] args) {
        Seller seller = Seller.loadSeller("seller");
        Customer customer = Customer.loadCustomer("customer");
        customer.addShoppingCart(new Product("wee", "wee", 1, 1, "wee"), 1, 1, 1, 1);
        customer.checkShoppingCart();
    }
}