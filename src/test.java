public class test {
    public static void main(String[] args) {
        Seller seller = Seller.loadSeller("seller");
//        Customer customer = Customer.loadCustomer("customer");
//        customer.addShoppingCart(new Product("wee", "wee", 1, 1, "wee"), 1, 1, 1, 1);
//        customer.checkShoppingCart();
        Product product1 = new Product("p1","dcp",10,10.5,"s1");
        Product product2 = new Product("p2","dcp",5,5.5,"s1");
        Store store = new Store("s1","seller");
        store.addProduct(product1);
        store.addProduct(product2);
        seller.addStore(store);
        System.out.println(seller.getStores().size());
    }
}