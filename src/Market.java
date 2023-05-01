import java.io.*;
import java.util.*;
import java.net.Socket;
import javax.swing.*;


/**
 * This is the client end of Market containing a main method to run the program. It contains customer marketplace and
 * seller market, which contain functions customer or seller can select from.
 * The end user runs this code, this is the "Client".
 *
 * @author Ekaterina Tszyao, Dimitri Paikos, Tyler Kei, Ryan Timmerman, Lab12
 * @version 5/1/2023
 */
public class Market implements Runnable {
    private Socket socket;

    public Market(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) throws IOException {
        Market market = new Market(connect());
        if (market.getSocket() != null) {
            new Thread(market).start();
        }
    }

    public void run() {
        // logging in/creating account
        while (true) {
            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
//            oos.flush();
                String userType;    //Seller or Customer
                String accountType; //log in or create an account
                String username;
                String password;
                Customer customer = null; // customer account if they select customer
                Seller seller = null;
                JOptionPane.showMessageDialog(null, "Welcome to the marketplace!", "Welcome",
                        JOptionPane.INFORMATION_MESSAGE);
                String[] userTypeOptions = {"Seller", "Customer"};
                // do {
                userType = (String) JOptionPane.showInputDialog(null, "Please select your user " +
                                "type.", "User Type", JOptionPane.QUESTION_MESSAGE, null, userTypeOptions,
                        userTypeOptions[0]);
                //} while (userType == null);
                if (userType == null) {
                    break;
                }

                userTypeOptions = new String[]{"Log in", "Create an account"};
                // do {
                accountType = (String) JOptionPane.showInputDialog(null, "Please select your " +
                                "account type."
                        , "Account Type", JOptionPane.QUESTION_MESSAGE, null, userTypeOptions,
                        userTypeOptions[0]);
                // } while (accountType == null);
                if (accountType == null) {
                    break;
                }


                // creating account
                if (accountType.equals("Create an account")) {
                    username = JOptionPane.showInputDialog(null, "Please enter your username/email."
                            , "Username", JOptionPane.QUESTION_MESSAGE);
                    if (username == null) {
                        break;
                    }

                    password = JOptionPane.showInputDialog(null, "Please enter your password."
                            , "Username", JOptionPane.QUESTION_MESSAGE);
                    if (password == null) {
                        break;
                    }
                    oos.writeObject(accountType);
                    oos.writeObject(userType);
                    oos.writeObject(username);
                    oos.writeObject(password);
//                oos.flush();
                    if (userType.equals("Customer")) {    //Creating customer account
                        Object newCustomer = ois.readObject();
                        if (newCustomer instanceof Customer) {
                            JOptionPane.showMessageDialog(null, "Account created. Please log in."
                                    , "Log in", JOptionPane.INFORMATION_MESSAGE); // logging in now
                            customer = customerLogin(oos, ois);
                        } else {
                            JOptionPane.showMessageDialog(null, newCustomer, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;   //if failed to create an account(AlreadyUser or OtherUser), program ends
                        }

                    } else if (userType.equals("Seller")) {    //creating Seller account
                        Object newSeller = ois.readObject();
                        if (newSeller instanceof Seller) {
                            JOptionPane.showMessageDialog(null, "Account created. Please log in."
                                    , "Log in", JOptionPane.INFORMATION_MESSAGE); // logging in now
                            seller = sellerLogin(oos, ois);
                        } else {
                            JOptionPane.showMessageDialog(null, newSeller, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;   //if failed to create an account(AlreadyUser or OtherUser), program ends
                        }
                    }
                } else if (accountType.equals("Log in")) { // if they want to log in
                    if (userType.equals("Customer")) {    //log in as a customer
                        customer = customerLogin(oos, ois);
                        if (customer == null) {
                            break;
                        }
                    } else if (userType.equals("Seller")) {    //log in as a seller
                        seller = sellerLogin(oos, ois);
                        if (seller == null) {
                            break;
                        }
                    }
                }

                JOptionPane.showMessageDialog(null, "Successfully logged in!"
                        , "Successful Log In", JOptionPane.INFORMATION_MESSAGE);
                // main marketplace
                if (customer != null) {
                    // marketplace for customer
                    customerMarketplace(customer, oos, ois);
                } else {
                    // marketplace for seller
                    sellerMarketplace(seller, oos, ois);
                }
                JOptionPane.showMessageDialog(null, "Goodbye!"
                        , "Farewell", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            break;
        }
    }

    // establish connection with server
    public static Socket connect() {
        String input;
        while (true) {
            input = showInputDialog("Enter hostname");
            if (input == null) {
                Socket socket = null;
                return socket;
            }
            try {
                Socket socket1 = new Socket(input, 4242);
                return socket1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to host!", "Error!",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // if you want to do an input with the dropdown
    public static String showInputDialog(String message, String[] options) {
        String input;
        input = (String) JOptionPane.showInputDialog(null, message
                , "Marketplace", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        while (input == null || input.isBlank()) {
            JOptionPane.showMessageDialog(null, message + "!", "Error!",
                    JOptionPane.ERROR_MESSAGE);
            input = (String) JOptionPane.showInputDialog(null, message
                    , "Marketplace", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        }

        return input;
    }

    public static String showInputDialog(String message) {
        String input;
        while (true) {
            input = JOptionPane.showInputDialog(null, message, "Marketplace",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                break;
            }
            while (input.isBlank()) {
                JOptionPane.showMessageDialog(null, "Input cannot be blank!", "Error!",
                        JOptionPane.ERROR_MESSAGE);
                input = JOptionPane.showInputDialog(null, message, "Marketplace",
                        JOptionPane.QUESTION_MESSAGE);
                if (input == null) {
                    break;
                }
            }
            break;
        }

        return input;
    }

    public static String showInputDialog(String message, String[] options, String title) {
        String input;
        input = (String) JOptionPane.showInputDialog(null, message
                , title, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        while (input == null || input.isBlank()) {
            JOptionPane.showMessageDialog(null, "Please select an option!", "Error!",
                    JOptionPane.ERROR_MESSAGE);
            input = (String) JOptionPane.showInputDialog(null, message
                    , title, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        }
        return input;
    }

    public static String showInputDialog(String message, String title) {
        String input;
        input = JOptionPane.showInputDialog(null, message, title,
                JOptionPane.QUESTION_MESSAGE);
        while (input == null || input.isBlank()) {
            JOptionPane.showMessageDialog(null, "Input cannot be blank!", "Error!",
                    JOptionPane.ERROR_MESSAGE);
            input = JOptionPane.showInputDialog(null, message, title,
                    JOptionPane.QUESTION_MESSAGE);
        }
        return input;
    }

    //log in as a customer
    public static Customer customerLogin(ObjectOutputStream oos, ObjectInputStream ois) {
        Customer customer = null;
        while (true) {
            try {
                String username = JOptionPane.showInputDialog(null, "Please enter your " +
                                "username/email."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                if (username == null) {
                    break;
                }

                String password = JOptionPane.showInputDialog(null, "Please enter your password."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                if (password == null) {
                    break;
                }

                oos.writeObject("Log in");
                oos.writeObject("Customer");
                oos.writeObject(username);
                oos.writeObject(password);
//                oos.flush();
                Object newCustomer = ois.readObject();
                if (newCustomer instanceof Customer) {
                    customer = (Customer) newCustomer;
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, newCustomer, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return customer;
    }

    //log in as a seller
    public static Seller sellerLogin(ObjectOutputStream oos, ObjectInputStream ois) {
        Seller seller = null;
        while (true) {
            try {
                String username = JOptionPane.showInputDialog(null, "Please enter your " +
                                "username/email."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                if (username == null) {
                    break;
                }
                String password = JOptionPane.showInputDialog(null, "Please enter your password."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                if (password == null) {
                    break;
                }
                oos.writeObject("Log in");
                oos.writeObject("Seller");
                oos.writeObject(username);
                oos.writeObject(password);
//                oos.flush();
                Object newSeller = ois.readObject();
                if (newSeller instanceof Seller) {
                    seller = (Seller) newSeller;
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, newSeller, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return seller;
    }

    public static void customerMarketplace(Customer customer, ObjectOutputStream oos, ObjectInputStream ois) {
        //checks shopping cart for updated products

        customer.checkShoppingCart();
        //display of products
        ArrayList<Seller> sellers = new ArrayList<>();
        while (true) {   //loop for the main page
            try { // moved get sellers inside loop so it refreshes
                oos.writeObject("List of sellers");
//                oos.flush();
                sellers = (ArrayList<Seller>) ois.readObject();

                oos.writeObject("refresh user");
                oos.writeObject("customer");
                oos.writeObject(customer.getUsername());
//                oos.flush();
                customer = (Customer) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            int i = 1;   //index used to number products and other choices
            ArrayList<Product> allProducts = new ArrayList<>();
            ArrayList<Store> allStores = new ArrayList<>();
            ArrayList<String> prodstring = new ArrayList<>();
            for (int j = 0; j < sellers.size(); j++) {
                ArrayList<Store> stores = sellers.get(j).getStores();
                allStores.addAll(stores);
                for (int k = 0; k < stores.size(); k++) {
                    ArrayList<Product> products = stores.get(k).getProducts();
                    allProducts.addAll(products);
                    for (int l = 0; l < products.size(); l++) {
                        String temp = i + ". " + products.get(l).marketplaceDisplay();
                        prodstring.add(temp);
                        i++;

                    }
                }
            }
            prodstring.add((i++) + ". Search for specific products.");
            prodstring.add((i++) + ". Sort the marketplace on price.");
            prodstring.add((i++) + ". Sort the marketplace on quantity available.");
            prodstring.add((i++) + ". View a dashboard with store and seller information.");
            prodstring.add((i++) + ". View shopping cart.");
            prodstring.add((i++) + ". Modify account.");
            prodstring.add((i++) + ". View purchase history.");
            prodstring.add((i++) + ". Refresh Page.");
            prodstring.add((i) + ". Exit.");
            int choice;
            String choyce;
            String[] choices = prodstring.toArray(new String[0]);
            choyce = showInputDialog("Please select an option to visit a product page or perform an action",
                    choices);
            choyce = choyce.substring(0, choyce.indexOf(".")); // if they press X this errors because its null
            choice = Integer.parseInt(choyce);
            if (choice <= (i - 9)) {    //user selected a product
                Product currentProduct = allProducts.get((choice - 1));
                productPage(currentProduct, sellers, customer, oos, ois);

            } else {    //user selected an option below listed products
                if (choice == (i - 8)) {   //Search for specific products
                    String term = showInputDialog("Please enter a term to search for.");


                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<Product> filteredProducts = new ArrayList<>();
                        ArrayList<String> allprodlist = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            if (allProducts.get(j).toString().toLowerCase().contains(term)) {
                                filteredProducts.add(allProducts.get(j));
                                String temp = i + ". " + allProducts.get(j).marketplaceDisplay();
                                allprodlist.add(temp);
                                i++;
                            }
                        }
                        allprodlist.add(i + ". Back to main page.");
                        String[] markchoice = allprodlist.toArray(new String[0]);
                        String chosen = showInputDialog("Searched products are listed below. Please select an "
                                + "option to visit a product's page", markchoice);
                        chosen = Character.toString(chosen.charAt(0));
                        choice = Integer.parseInt(chosen);
                        if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = filteredProducts.get((choice - 1));
                            productPage(currentProduct, sellers, customer, oos, ois);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 7)) {
                    //Sort the marketplace on price:products with lower price are on the top
                    Collections.sort(allProducts, new ProductComparatorByPrice());
                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<String> sortprodlist = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            String temp = i + ". " + allProducts.get(j).marketplaceDisplay();
                            sortprodlist.add(temp);
                            i++;
                        }
                        sortprodlist.add(i + ". Back to main page.");
                        String[] markchoice = sortprodlist.toArray(new String[0]);
                        String chosen = showInputDialog("Products sorted. Please select an option to visit a " +
                                "product's page", markchoice);
                        chosen = Character.toString(chosen.charAt(0));
                        choice = Integer.parseInt(chosen);
                        if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = allProducts.get((choice - 1));
                            productPage(currentProduct, sellers, customer, oos, ois);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 6)) {
                    //Sort the marketplace on quantity available:
                    //products with more items available are on the top
                    Collections.sort(allProducts, new ProductComparatorByAvailability());
                    Collections.reverse(allProducts);
                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<String> anothersortlist = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            String temp = i + ". " + allProducts.get(j).marketplaceDisplay() + ", available quantity: "
                                    + allProducts.get(j).getAvailableQuantity();
                            anothersortlist.add(temp);
                            i++;
                        }
                        anothersortlist.add(i + ". Back to main page.");
                        String[] markchoice = anothersortlist.toArray(new String[0]);
                        String chosen = showInputDialog("Products sorted. Please select an option to visit a " +
                                "product's page", markchoice);
                        chosen = Character.toString(chosen.charAt(0));
                        choice = Integer.parseInt(chosen);
                        if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = allProducts.get((choice - 1));
                            productPage(currentProduct, sellers, customer, oos, ois);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 5)) {
                    String[] choicest = {"1. View a list of stores by number of products sold.", "2. View a list of " +
                            "stores by the products purchased by you."};
                    String chosen = showInputDialog("View a list of stores by number of products sold or " +
                            "products purchased by you", choicest);
                    String dashboardChoice = Character.toString(chosen.charAt(0));
                    if (dashboardChoice.equals("1")) {
                        ArrayList<String> prods = new ArrayList<>();
                        String[] sort = {"1. Sort by low - high.", "2. Sort by high - low.", "3. Back to main page.",
                                "4. Don't sort"};
                        String sortdec = showInputDialog("Would you like to sort the data?", sort);
                        if (!sortdec.equals("3. Back to main page.")) {
                            if (sortdec.equals("1. Sort by low - high.")) {
                                Collections.sort(allStores, new StoreComparatorByProductsSold());
                            } else if (sortdec.equals("2. Sort by high - low.")) {
                                Collections.sort(allStores, new StoreComparatorByProductsSold());
                                Collections.reverse(allStores);
                            } //if (sortdec.equals("4. Don't sort")), don't do anything
                            for (int j = 0; j < allStores.size(); j++) {
                                String temp = (j + 1) + ". Store name: " + allStores.get(j).getStoreName() + ", " +
                                        "products sold: " + allStores.get(j).getProductsSold();
                                prods.add(temp);
                            }
                            String[] markchoice = prods.toArray(new String[0]);
                            JOptionPane.showMessageDialog(null, markchoice, "Products", JOptionPane.
                                    INFORMATION_MESSAGE);
                        }
//                        else if (sortdec.equals("3. Back to main page.")) {
//                            break;//dashboardChoice = 3; return to main page
//                        }
                    } else if (dashboardChoice.equals("2")) {
                        //View a list of stores by the products purchased by this customer
                        HashMap<String, Integer> historyByStore = customer.purchaseHistoryByStore(allStores);
//                        boolean validChoice2;
                        ArrayList<String> storeprods = new ArrayList<>();
                        String[] sort = {"1. Sort by low - high.", "2. Sort by high - low.", "3. Back to main page.",
                                "4. Don't sort"};
                        String sortdec = showInputDialog("Would you like to sort the data?", sort);

                        ArrayList<Integer> purchasedProducts = new ArrayList<>();  //number of products purchased
                        for (Map.Entry<String, Integer> entry : historyByStore.entrySet()) {
                            purchasedProducts.add(entry.getValue());
                        }

                        if (!sortdec.equals("3. Back to main page.")) {
                            if (sortdec.equals("1. Sort by low - high.")) {   //sort low-to-high
                                Collections.sort(purchasedProducts);
                            } else if (sortdec.equals("2. Sort by high - low.")) {    //sort high-to-low
                                Collections.sort(purchasedProducts);
                                Collections.reverse(purchasedProducts);
                            }

                            int index = 1;
                            for (int num : purchasedProducts) {
                                for (Map.Entry<String, Integer> entry : historyByStore.entrySet()) {
                                    if (entry.getValue() == num) {
                                        String temp = index + ". Store Name: " + entry.getKey() + ", products purchased"
                                                + " by you" + entry.getValue();
                                        storeprods.add(temp);
                                        index++;
                                    }
                                }
                            }
                            String[] sortedStores = storeprods.toArray(new String[0]);
                            JOptionPane.showMessageDialog(null, sortedStores, "Stores",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                } else if (choice == (i - 4)) {    //view shopping cart
                    ArrayList<ShoppingCart> shoppingCart = customer.getShoppingCart();
                    if (shoppingCart.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Shopping Cart is Empty!",
                                "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        ArrayList<String> shopprod = new ArrayList<>();
                        shopprod.add("Products in your shopping cart:");
                        for (int a = 0; a < shoppingCart.size(); a++) {
                            String temp = (a + 1) + ". Product: " + shoppingCart.get(a).getProduct().getProductName()
                                    + ". Amount: " + shoppingCart.get(a).getAmount();
                            shopprod.add(temp);
                        }
                        String[] cartProducts = shopprod.toArray(new String[0]);
                        String input;
                        ShoppingCartGUI gui = new ShoppingCartGUI(cartProducts, sellers, shoppingCart, customer,
                                shopprod);
                        gui.setVisible(true);
                        while (gui.isVisible()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (choice == (i - 3)) {    //Modify account
                    String input;
                    String[] options = {"Edit username", "Edit password", "Delete account", "Exit"};
                    input = (String) showInputDialog("How would you like to modify your account?", options);

                    if (input.equals("Edit username")) {
                        input = showInputDialog("What is your new username?");
                        try {
                            oos.writeObject("Change username");
                            oos.writeObject("Customer");
                            oos.writeObject(customer);
                            oos.writeObject(input);
//                            oos.flush();

                            customer = (Customer) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (input.equals("Edit password")) {
                        input = showInputDialog("What is your new password");
                        try {
                            oos.writeObject("Change password");
                            oos.writeObject("Customer");
                            oos.writeObject(customer);
                            oos.writeObject(input);
//                            oos.flush();

                            customer = (Customer) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (input.equals("Delete account")) {
                        int delete = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to delete your account?",
                                "Marketplace", JOptionPane.YES_NO_OPTION);
                        if (delete == JOptionPane.YES_OPTION) {
                            try {
                                oos.writeObject("Delete account");
                                oos.writeObject("Customer");
                                oos.writeObject(customer);
//                                oos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(null, "Account has been deleted",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "You chose not to delete this account! Returning to main page...",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            continue;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Returning to main page...",
                            "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                } else if (choice == (i - 2)) {  //purchase history
                    try {  //this block refreshes customer info
                        oos.writeObject("refresh user");
                        oos.writeObject("customer");
                        oos.writeObject(customer.getUsername());
                        customer = (Customer) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    ArrayList<PurchaseHistory> purchaseHistory = customer.getPurchaseHistory();
                    if (purchaseHistory.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No purchase history.",
                                "Purchase history", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        ArrayList<String> phistory = new ArrayList<>();
                        for (int j = purchaseHistory.size() - 1; j >= 0; j--) {
                            PurchaseHistory history = purchaseHistory.get(j);
                            String temp = "Product: " + history.getProduct().getProductName() + ". Amount purchased: "
                                    + history.getAmount() + ". Store: " + history.getStoreName();
                            phistory.add(temp);
                        }
                        String[] purhist = phistory.toArray(new String[0]);
                        PurchaseHistoryGUI phist = new PurchaseHistoryGUI(purchaseHistory, purhist);
                        phist.setVisible(true);
                        while (phist.isVisible()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (choice == i) return;
                //if choice == (i-1), return to the beginning of loop and refresh
            }
        }
    }

    public static void productPage(Product currentProduct, ArrayList<Seller> sellers, Customer customer,
                                   ObjectOutputStream oos, ObjectInputStream ois) {
        //this is a separated method used to display product's page and realize further operations
        //it ends and returns void only when user selects "back to main page"
        Store currentStore = null;
        try { // grabs sellers again
            oos.writeObject("List of sellers");
//            oos.flush();
            sellers = (ArrayList<Seller>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<Store> stores = new ArrayList<>(); // grabbing stores again
        for (int j = 0; j < sellers.size(); j++) {
            ArrayList<Store> store = sellers.get(j).getStores();
            stores.addAll(store);
        }

        for (int j = 0; j < stores.size(); j++) {
            if (stores.get(j).getStoreName().equals(currentProduct.getStoreName())) {
                currentStore = stores.get(j);
            }
        }
        if (currentStore == null) {
            JOptionPane.showMessageDialog(null, "Could not find store! It may have been deleted.",
                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        while (true) {
            try {   //this block refreshes product info every time customer opens/refreshes product page
                oos.writeObject("refresh product");
                oos.writeObject(currentProduct);
                Object serverInput = ois.readObject();
                if (!(serverInput instanceof Product)) {
                    JOptionPane.showMessageDialog(null, "Could not find product! " +
                                    "It may have been deleted.",
                            "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                currentProduct = (Product) serverInput;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            String input = showInputDialog(currentProduct.productPageDisplay(), new String[]{"1. Purchase this " +
                    "product.",
                    "2. Add this product to shopping cart.", "3. Refresh page.", "4. Back to main page."});
            char choiceOnProductPage = input.charAt(0);
            switch (choiceOnProductPage) {
                case '1':   //purchase this product
                    int amount;
                    while (true) {
                        input = showInputDialog("What amount would you like to purchase?");
                        try {
                            amount = Integer.parseInt(input);
                            if (amount < 0) {
                                JOptionPane.showMessageDialog(null, "Please input valid number.",
                                        "Error!", JOptionPane.ERROR_MESSAGE);
                                continue;
                            } else break;
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Please input valid number.",
                                    "Error!", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    try {
                        oos.writeObject("Purchase product");
//                        oos.flush();
                        oos.writeObject(currentProduct);
                        boolean stillProduct = (boolean) ois.readObject();

                        if (stillProduct) { // if product is still valid
                            oos.writeObject(amount);
                            oos.writeObject(customer);
//                            oos.flush();

                            String message = (String) ois.readObject();
                            JOptionPane.showMessageDialog(null, message, "Marketplace",
                                    JOptionPane.INFORMATION_MESSAGE);

                        } else { // product cannot be found
                            JOptionPane.showMessageDialog(null, "Could not find product! " +
                                            "It may have been deleted.",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case '2':    // Add this product to shopping cart
                    int amount1;
                    while (true) {
                        input = showInputDialog("What amount would you like to put into shopping cart?");
                        try {
                            amount1 = Integer.parseInt(input);
                            if (amount1 < 0) {
                                JOptionPane.showMessageDialog(null, "Please input valid number.",
                                        "Error!", JOptionPane.ERROR_MESSAGE);
                            } else break;
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Please input valid number.",
                                    "Error!", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    try {
                        oos.writeObject("Add to cart");
//                        oos.flush();
                        oos.writeObject(currentProduct);
                        boolean stillProduct = (boolean) ois.readObject();

                        if (stillProduct) { // if product is still valid
                            oos.writeObject(amount1);
                            oos.writeObject(customer);
//                            oos.flush();

                            String message = (String) ois.readObject();
                            JOptionPane.showMessageDialog(null, message, "Marketplace",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else { // product cannot be found
                            JOptionPane.showMessageDialog(null, "Could not find product! " +
                                            "It may have been deleted.",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case '4':
                    return;   //returns to main page
                case '3':    //returns to the beginning of while-true loop and refreshes page
            }
        }

    }

    public static void sellerMarketplace(Seller seller, ObjectOutputStream oos, ObjectInputStream ois) {
        do {
            try {  //this block refreshes seller info
                oos.writeObject("refresh user");
                oos.writeObject("seller");
                oos.writeObject(seller.getUsername());
                oos.flush();
                seller = (Seller) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            String[] selloptions = {"Modify Products", "View a list of sales by store", "View a dashboard with " +
                    "statistics" +
                    " for each stores", "View number of products in shopping carts", "Modify Account", "Create a store"
                    , "Exit"};
            String choice;
            choice = showInputDialog("What would you like to do", selloptions, "Action");

            if (choice.equals("Modify Products")) { // Modify products
                boolean valid;
                try {  //this block refreshes seller info
                    oos.writeObject("refresh user");
                    oos.writeObject("seller");
                    oos.writeObject(seller.getUsername());
                    seller = (Seller) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ArrayList<Store> astores = seller.getStores();
                if (astores == null) {
                    JOptionPane.showMessageDialog(null, "You don't have any stores! " +
                            "Please first" +
                            "create a store!", "No Stores", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                Store[] stores = new Store[astores.size()];
                String[] names = new String[astores.size()];
                for (int i = 0; i < astores.size(); i++) {
                    stores[i] = astores.get(i);
                    names[i] = stores[i].getStoreName();
                }
                String currentStores;
                currentStores = showInputDialog("Which store would you like to edit?", names, "Store?");
                int j;
                j = Arrays.asList(names).indexOf(currentStores);
                Store currentStore = stores[j];
                String[] choices = {"1. Add a product", "2. Edit a product", "3. Delete a product", "4. Export products"
                        + ""};
                String action;
                action = showInputDialog("What would you like to do?", choices, "Choice?");
                if (action.equals("1. Add a product")) {    //add a product
                    String input;
                    input = showInputDialog("How would you like to add this product?", new String[]{
                            "1. Import product from csv.", "2. Create product in gui."}, "Add Product");

                    if (input.equals("1. Import product from csv.")) {    //Import product from csv.
                        String file = showInputDialog("Please enter the file path to the csv file.",
                                "Import Product");
                        try {
                            oos.writeObject("csv");
                            oos.writeObject(seller.getUsername());
                            oos.writeObject(currentStore.getStoreName());
                            oos.writeObject(file);
                            oos.flush();
                            String message = (String) ois.readObject();
                            JOptionPane.showMessageDialog(null, message, "Import Product",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    } else if (input.equals("2. Create product in terminal.")) {    //Create product in terminal.
                        String name = showInputDialog("Please enter a product name:", "Create Product");
                        String description = showInputDialog("Please enter a product description:",
                                "Create Product");
                        String q;
                        String p;
                        double price = -1;
                        int quantity = 0;

                        do {
                            q = showInputDialog("Please enter an available quantity.", "Create Product");
                            try {
                                quantity = Integer.parseInt(q);
                                if (quantity <= 0)
                                    JOptionPane.showMessageDialog(null, "Please enter an " +
                                            "integer " +
                                            "greater than 0", "Error!", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Please enter an integer " +
                                        "greater than 0", "Error!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (quantity <= 0);

                        do {
                            p = showInputDialog("Please enter a price for the product.", "Create Product");
                            try {
                                price = Double.parseDouble(p);
                                if (price < 0)
                                    JOptionPane.showMessageDialog(null, "Please enter a number "
                                            +
                                            "greater or equal to 0", "Error!", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Please enter a number " +
                                        "greater or equal to 0", "Error!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (price < 0);
                        try {
                            oos.writeObject("Add product");
                            oos.writeObject(currentStore);
                            oos.writeObject(seller);
                            oos.writeObject(name);
                            oos.writeObject(description);
                            oos.writeObject(quantity);
                            oos.writeObject(price);
                            oos.writeObject(currentStore.getStoreName());
                            oos.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "Product added!",
                                "Create Product"
                                , JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (action.equals("2. Edit a product")) {    //edit a product
                    int productNum;
                    if (currentStore.getProducts() == null || currentStore.getProducts().size() == 0) {
                        JOptionPane.showMessageDialog(null, "There's no products in this store! "
                                + "Please add products or choose another store!", "No Products", JOptionPane.
                                ERROR_MESSAGE);
                        continue;
                    }
                    ArrayList<String> productArrList = new ArrayList<>();
                    for (int i = 0; i < currentStore.getProducts().size(); i++) {
                        String tmp = String.format("%d. %s, description: %s\n", i + 1, currentStore.getProduct(i)
                                .getProductName(), currentStore.getProduct(i).getDescription());
                        productArrList.add(tmp);
                    }
                    String[] productArr = productArrList.toArray(new String[0]);
                    String productChoice = showInputDialog("Please select a product to edit.", productArr,
                            "Edit Product");
                    productNum = Integer.parseInt(productChoice.substring(0, productChoice.indexOf('.')));

                    Product currentProduct = currentStore.getProduct((productNum - 1));

                    valid = true;
                    String editOption = showInputDialog(currentProduct.productPageDisplay(), new String[]{"1. Edit " +
                            "product name.", "2. Edit product description.", "3. Edit product available quantity."
                            , "4. Edit product price."}, "Edit Product");
                    editOption = editOption.substring(0, 1);
                    if (editOption.equals("1")) {
                        String name = showInputDialog("Please enter a new product name:", "Edit Product");
                        try {
                            oos.writeObject("Edit product");
                            oos.writeObject("name");
                            oos.writeObject(seller);
                            oos.writeObject(currentProduct);
                            oos.writeObject(name);
                            oos.flush();
                        } catch (Exception e) {
//                            e.printStackTrace();
                            //this gives socketexcpetion error so im just not printing it
                        }
                    } else if (editOption.equals("2")) {
                        String description = showInputDialog("Please enter a new product description:",
                                "Edit Product");
                        try {
                            oos.writeObject("Edit product");
                            oos.writeObject("desc");
                            oos.writeObject(currentProduct);
                            oos.writeObject(description);
                            oos.flush();
                        } catch (Exception e) {

                        }
                    } else if (editOption.equals("3")) {
                        int availableQuantity = -1;
                        do {
                            String quantityStr = showInputDialog("Please enter a new available quantity:",
                                    "Edit Product");
                            try {
                                availableQuantity = Integer.parseInt(quantityStr);
                                if (availableQuantity < 0)
                                    JOptionPane.showMessageDialog(null, "Please enter a " +
                                            "non-negative integer.", "Error!", JOptionPane.ERROR_MESSAGE);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Please enter a " +
                                        "non-negative integer.", "Error!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (availableQuantity < 0);
                        try {
                            oos.writeObject("Edit product");
                            oos.writeObject("quantity");
                            oos.writeObject(currentProduct);
                            oos.writeObject(availableQuantity);
                            oos.flush();
                        } catch (Exception e) {

                        }
                    } else if (editOption.equals("4")) {
                        double price = -1;
                        do {
                            String priceStr = showInputDialog("Please enter a new price:", "Edit Product");
                            try {
                                price = Double.parseDouble(priceStr);
                                if (price < 0)
                                    JOptionPane.showMessageDialog(null, "Please enter a " +
                                            "non-negative integer.", "Error!", JOptionPane.ERROR_MESSAGE);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Please enter a " +
                                        "non-negative integer.", "Error!", JOptionPane.ERROR_MESSAGE);
                            }
                        } while (price < 0);
                        try {
                            oos.writeObject("Edit product");
                            oos.writeObject("price");
                            oos.writeObject(currentProduct);
                            oos.writeObject(price);
                            oos.flush();
                        } catch (Exception e) {

                        }
                    }
                    JOptionPane.showMessageDialog(null, "Product edited!", "Edit Product",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (action.equals("3. Delete a product")) {    //modify option = 3;delete a product
                    int productNum;
                    if (currentStore.getProducts() == null || currentStore.getProducts().size() == 0) {
                        JOptionPane.showMessageDialog(null, "There's no products in this store!" +
                                " " +
                                "Please add products or choose another store!", "No Products", JOptionPane.
                                ERROR_MESSAGE);
                        continue;
                    }
                    ArrayList<String> productArrList = new ArrayList<>();
                    for (int i = 0; i < currentStore.getProducts().size(); i++) {
                        String tmp = String.format("%d. %s, description: %s\n", i + 1, currentStore.getProduct(i)
                                .getProductName(), currentStore.getProduct(i).getProductName());
                        productArrList.add(tmp);
                    }
                    String[] productArr = productArrList.toArray(new String[0]);
                    String productChoice = showInputDialog("Please select a product to delete.", productArr,
                            "Delete Product");
                    productNum = Integer.parseInt(productChoice.substring(0, productChoice.indexOf('.')));

                    try {
                        oos.writeObject("Delete product");
                        oos.writeObject(seller);
                        oos.writeObject(currentStore);
                        oos.writeObject(productNum - 1);
                    } catch (Exception e) {

                    }
                    JOptionPane.showMessageDialog(null, "Product deleted!", "Delete Product",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (action.equals("4. Export products")) { //modify option = 4; export products
                    while (true) {
                        String file = showInputDialog("Please enter the file path to export to.",
                                "Export products");
                        try {
                            oos.writeObject("Export products");
                            oos.writeObject(seller.getUsername());
                            oos.writeObject(currentStore.getStoreName());
                            oos.writeObject(file);
                            oos.flush();
                            String message = (String) ois.readObject();
                            if (message.equals("Products exported!")) {
                                JOptionPane.showMessageDialog(null, message,
                                        "Export Products", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            } else {
                                JOptionPane.showMessageDialog(null, message,
                                        "Error!", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (choice.equals("View a list of sales by store")) {
                ArrayList<Store> sellstore = new ArrayList<>();
                sellstore = seller.getStores();
                Store[] storelist = new Store[sellstore.size()];
                String[] storename = new String[storelist.length];
                ArrayList<Double> revenue = new ArrayList<>(); //Revenue of each purchase
                ArrayList<String> customers = new ArrayList<>(); //Customer username for each purchase
                ArrayList<Integer> amount = new ArrayList<>(); //Number of products each customer purchased
                ArrayList<String> output = new ArrayList<>();
                for (int i = 0; i < storelist.length; i++) {
                    storelist[i] = sellstore.get(i);
                    storename[i] = storelist[i].getStoreName();
                }
                for (int i = 0; i < storelist.length; i++) {
                    //Fills the strings arrays from respective Arraylists
                    //and then prints information for each customer line by line
                    revenue = storelist[i].getRevenue();
                    customers = storelist[i].getCustList();
                    amount = storelist[i].getPurchased();
                    try {
                        String[] revlist = new String[revenue.size()];
                        String[] custlist = new String[customers.size()];
                        String[] purchased = new String[amount.size()];
                        output.add(storename[i]);
                        int j = 0;
                        do {
                            //I'm not sure why I made the Double and Integer arrays into Strings but I did so
                            revlist[j] = (revenue.get(j)).toString();
                            custlist[j] = (customers.get(j));
                            purchased[j] = (amount.get(j)).toString();
                            if ((custlist[j].equals("")) || (purchased[j].equals("0")) || (revlist[j].equals("0.0"))) {
                                output.add("No sales have been made on this store.");
                            }
                            String temp = "Customer " + custlist[j] + " purchased " + purchased[j] + " products for a " +
                                    "total sale of $" + revlist[j];
                            output.add(temp);
                            j++;
                        } while (j < revlist.length);
                    } catch (IndexOutOfBoundsException e) {
                        output.add("No sales have been made on this store.");
                    }
                }
                String[] sales = output.toArray(new String[0]);
                JOptionPane.showMessageDialog(null, sales, "Sales", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("View a dashboard with statistics for each stores")) {    //choice = 3, statistics
                ArrayList<String> sorted = new ArrayList<>();
                int stats;
                String input = showInputDialog("What would you like Statistics of?",
                        new String[]{"1. Product Statistics", "2. Customer Statistics"});
                stats = Integer.parseInt(input.substring(0, 1));
                if (stats == 1) {
                    ArrayList<Product> prodlist = new ArrayList<>();
                    ArrayList<Integer> numsold = new ArrayList<>();
                    ArrayList<Store> storelist = new ArrayList<>();
                    storelist = seller.getStores();
                    Store[] stores = new Store[storelist.size()];
                    String[] storename = new String[stores.length];
                    int choices = 0;
                    boolean valids = true;
                    String[] sorts = {"1. Sort by low - high.", "2. Sort by high - low.", "3. Don't sort"};
                    String sortdec = showInputDialog("Would you like to sort the data?", sorts);
                    if (sortdec.equals("1. Sort by low - high.")) {
                        for (int i = 0; i < stores.length; i++) {
                            stores[i] = storelist.get(i);
                            storename[i] = stores[i].getStoreName();
                        }
                        for (int i = 0; i < stores.length; i++) {

                            prodlist = stores[i].getProducts();
                            Product[] products = new Product[prodlist.size()];
                            Integer[] number = new Integer[prodlist.size()];
                            String[] sproduct = new String[prodlist.size()];
                            for (int j = 0; j < prodlist.size(); j++) {
                                products[j] = prodlist.get(j);
                                number[j] = products[j].getSale();
                                sproduct[j] = products[j].getProductName();
                            }
                            try {
                                int n = products.length;
                                sorted.add(storename[i]);
                                String[] prods = new String[products.length];
                                int tempn = 0;
                                String tempd = "";
                                for (int k = 0; k < n; k++) {
                                    for (int g = 1; g < (n - k); g++) {
                                        if (number[g - 1] > number[g]) {
                                            tempn = number[g - 1];
                                            number[g - 1] = number[g];
                                            number[g] = tempn;
                                            tempd = sproduct[g - 1];
                                            sproduct[g - 1] = sproduct[g];
                                            sproduct[g] = tempd;
                                        }
                                    }
                                }
                                for (int m = 0; m < prods.length; m++) {
                                    String temp = "Product " + sproduct[m] + " sold " + number[m] + " units";
                                    sorted.add(temp);
                                }
                            } catch (NullPointerException e) {
                                JOptionPane.showMessageDialog(null, "No sales have been made " +
                                        "at this store", "Sales", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        String[] sortsed = sorted.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, sortsed, "Sales", JOptionPane.
                                INFORMATION_MESSAGE);
                    } else if (sortdec.equals("2. Sort by high - low.")) {
                        for (int i = 0; i < stores.length; i++) {
                            stores[i] = storelist.get(i);
                            storename[i] = stores[i].getStoreName();
                        }
                        for (int i = 0; i < stores.length; i++) {
                            prodlist = stores[i].getProducts();
                            Product[] products = new Product[prodlist.size()];
                            Integer[] number = new Integer[prodlist.size()];
                            String[] sproduct = new String[prodlist.size()];
                            for (int j = 0; j < prodlist.size(); j++) {
                                products[j] = prodlist.get(j);
                                number[j] = products[j].getSale();
                                sproduct[j] = products[j].getProductName();
                            }
                            try {
                                int n = products.length;
                                sorted.add(storename[i]);
                                String[] prods = new String[products.length];
                                int tempn = 0;
                                String tempd = "";
                                for (int k = 0; k < n; k++) {
                                    for (int g = 1; g < (n - k); g++) {
                                        if (number[g - 1] < number[g]) {
                                            tempn = number[g - 1];
                                            number[g - 1] = number[g];
                                            number[g] = tempn;
                                            tempd = sproduct[g - 1];
                                            sproduct[g - 1] = sproduct[g];
                                            sproduct[g] = tempd;
                                        }
                                    }
                                }
                                for (int m = 0; m < prods.length; m++) {
                                    String temp = "Product " + sproduct[m] + " sold " + number[m] + " units";
                                    sorted.add(temp);
                                }
                            } catch (NullPointerException e) {
                                sorted.add("No sales have been made at this store.");
                            }
                        }
                        String[] sortsed = sorted.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, sortsed, "Sales", JOptionPane.
                                INFORMATION_MESSAGE);
                    } else {
                        for (int i = 0; i < stores.length; i++) {
                            stores[i] = storelist.get(i);
                            storename[i] = stores[i].getStoreName();
                        }
                        for (int i = 0; i < stores.length; i++) {
                            prodlist = stores[i].getProducts();
                            Product[] products = new Product[prodlist.size()];
                            Integer[] number = new Integer[prodlist.size()];
                            String[] sproduct = new String[prodlist.size()];
                            for (int j = 0; j < prodlist.size(); j++) {
                                products[j] = prodlist.get(j);
                                number[j] = products[j].getSale();
                                sproduct[j] = products[j].getProductName();
                            }
                            try {
                                int n = products.length;
                                sorted.add(storename[i]);
                                String[] prods = new String[products.length];
                                for (int m = 0; m < prods.length; m++) {
                                    String temp = "Product " + sproduct[m] + " sold " + number[m] + " units";
                                    sorted.add(temp);
                                }
                            } catch (NullPointerException e) {
                                sorted.add("No sales have been made at this store.");
                            }
                        }
                        String[] sortsed = sorted.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, sortsed, "Sales", JOptionPane.
                                INFORMATION_MESSAGE);
                    }
                } else {
                    String[] sorts = {"1. Sort by low - high.", "2. Sort by high - low.", "3. Don't sort"};
                    String sortdec = showInputDialog("Would you like to sort the data?", sorts);
                    boolean valids = true;
                    int choices = 0;
                    if (sortdec.equals("1. Sort by low - high.")) {
                        ArrayList<Store> sellstore = new ArrayList<>();
                        sellstore = seller.getStores();
                        Store[] storelist = new Store[sellstore.size()];
                        String[] storename = new String[storelist.length];
                        ArrayList<Double> revenue = new ArrayList<>(); //Revenue of each purchase
                        ArrayList<String> customers = new ArrayList<>(); //Customer username for each purchase
                        ArrayList<Integer> amount = new ArrayList<>(); //Number of products each customer purchased
                        for (int i = 0; i < storelist.length; i++) {
                            storelist[i] = sellstore.get(i);
                            storename[i] = storelist[i].getStoreName();
                        }
                        for (int i = 0; i < storelist.length; i++) {
                            //Fills the strings arrays from respective Arraylists
                            //and then prints information for each customer line by line
                            revenue = storelist[i].getRevenue();
                            customers = storelist[i].getCustList();
                            amount = storelist[i].getPurchased();
                            try {
                                Double[] revlist = new Double[revenue.size()];
                                String[] custlist = new String[customers.size()];
                                Integer[] purchased = new Integer[amount.size()];
                                sorted.add(storename[i]);
                                int j = 0;
                                do {
                                    //I'm not sure why I made the Double and Integer arrays into Strings but I did
                                    revlist[j] = (revenue.get(j));
                                    custlist[j] = (customers.get(j));
                                    purchased[j] = (amount.get(j));
                                    int n = revlist.length;
                                    double tempr = 0;
                                    String tempc = "";
                                    int tempp = 0;
                                    for (int k = 0; k < n; k++) {
                                        for (int g = 1; g < (n - k); g++) {
                                            if (revlist[g - 1] > revlist[g]) {
                                                tempr = revlist[g - 1];
                                                revlist[g - 1] = revlist[g];
                                                revlist[g] = tempr;
                                                tempc = custlist[g - 1];
                                                custlist[g - 1] = custlist[g];
                                                custlist[g] = tempc;
                                                tempp = purchased[g - 1];
                                                purchased[g - 1] = purchased[g];
                                                purchased[g] = tempp;
                                            }
                                        }
                                    }
                                    if ((custlist[j].equals("")) || (purchased[j] == 0) || (revlist[j] == 0.0)) {
                                        sorted.add("No sales have been made on this store.");
                                    }
                                    String temp = "Customer " + custlist[j] + " purhcased " + purchased[j] +
                                            "products for a total sale of " + revlist[j];
                                    sorted.add(temp);
                                    j++;
                                } while (j < revlist.length);
                            } catch (Exception e) {
                                sorted.add("No sales have been made on this store.");
                            }
                        }
                        String[] sortsed = sorted.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, sortsed, "Sales", JOptionPane.
                                INFORMATION_MESSAGE);
                    } else if (sortdec.equals("2. Sort by high - low.")) {
                        ArrayList<Store> sellstore = new ArrayList<>();
                        sellstore = seller.getStores();
                        Store[] storelist = new Store[sellstore.size()];
                        String[] storename = new String[storelist.length];
                        ArrayList<Double> revenue = new ArrayList<>(); //Revenue of each purchase
                        ArrayList<String> customers = new ArrayList<>(); //Customer username for each purchase
                        ArrayList<Integer> amount = new ArrayList<>(); //Number of products each customer purchased
                        for (int i = 0; i < storelist.length; i++) {
                            storelist[i] = sellstore.get(i);
                            storename[i] = storelist[i].getStoreName();
                        }
                        for (int i = 0; i < storelist.length; i++) {
                            //Fills the strings arrays from respective Arraylists
                            //and then prints information for each customer line by line
                            revenue = storelist[i].getRevenue();
                            customers = storelist[i].getCustList();
                            amount = storelist[i].getPurchased();
                            try {
                                Double[] revlist = new Double[revenue.size()];
                                String[] custlist = new String[customers.size()];
                                Integer[] purchased = new Integer[amount.size()];
                                sorted.add(storename[i]);
                                int j = 0;
                                do {
                                    // I'm not sure why I made the Double and Integer arrays into Strings but I did
                                    revlist[j] = (revenue.get(j));
                                    custlist[j] = (customers.get(j));
                                    purchased[j] = (amount.get(j));
                                    int n = revlist.length;
                                    double tempr = 0;
                                    String tempc = "";
                                    int tempp = 0;
                                    for (int k = 0; k < n; k++) {
                                        for (int g = 1; g < (n - k); g++) {
                                            if (revlist[g - 1] < revlist[g]) {
                                                tempr = revlist[g - 1];
                                                revlist[g - 1] = revlist[g];
                                                revlist[g] = tempr;
                                                tempc = custlist[g - 1];
                                                custlist[g - 1] = custlist[g];
                                                custlist[g] = tempc;
                                                tempp = purchased[g - 1];
                                                purchased[g - 1] = purchased[g];
                                                purchased[g] = tempp;
                                            }
                                        }
                                    }
                                    if ((custlist[j].equals("")) || (purchased[j] == 0) || (revlist[j] == 0.0)) {
                                        sorted.add("No sales have been made at this store.");
                                    }

                                    String temp = "Customer " + custlist[j] + " purhcased " + purchased[j] +
                                            "products for a total sale of " + revlist[j];
                                    sorted.add(temp);
                                    j++;
                                } while (i < revlist.length);
                            } catch (Exception e) {
                                sorted.add("No sales have been made at this store.");
                            }
                        }
                        String[] sortsed = sorted.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, sortsed, "Sales", JOptionPane
                                .INFORMATION_MESSAGE);
                    } else {
                        ArrayList<Store> sellstore = new ArrayList<>();
                        sellstore = seller.getStores();
                        Store[] storelist = new Store[sellstore.size()];
                        String[] storename = new String[storelist.length];
                        ArrayList<Double> revenue = new ArrayList<>(); //Revenue of each purchase
                        ArrayList<String> customers = new ArrayList<>(); //Customer username for each purchase
                        ArrayList<Integer> amount = new ArrayList<>(); //Number of products each customer purchased
                        for (int i = 0; i < storelist.length; i++) {
                            storelist[i] = sellstore.get(i);
                            storename[i] = storelist[i].getStoreName();
                        }
                        for (int i = 0; i < storelist.length; i++) {
                            //Fills the strings arrays from respective Arraylists
                            //and then prints information for each customer line by line
                            revenue = storelist[i].getRevenue();
                            customers = storelist[i].getCustList();
                            amount = storelist[i].getPurchased();
                            try {
                                String[] revlist = new String[revenue.size()];
                                String[] custlist = new String[customers.size()];
                                String[] purchased = new String[amount.size()];
                                sorted.add(storename[i]);
                                for (int j = 0; j < revlist.length; j++) {
                                    // I'm not sure why I made the Double and Integer arrays into Strings but I did so
                                    revlist[j] = (revenue.get(j)).toString();
                                    custlist[j] = (customers.get(j));
                                    purchased[j] = (amount.get(j)).toString();
                                    if ((custlist[j].equals("")) || (purchased[j].equals("0")) ||
                                            (revlist[j].equals("0.0"))) {
                                        sorted.add("No sales have been made at this store.");
                                    }
                                    String temp = "Customer " + custlist[j] + " purhcased " + purchased[j] + "products" +
                                            " for a total sale of " + revlist[j];
                                    sorted.add(temp);
                                }
                            } catch (Exception e) {
                                sorted.add("No sales have been made at this store.");
                            }
                        }
                        String[] sortsed = sorted.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, sortsed, "Sales", JOptionPane.
                                INFORMATION_MESSAGE);
                    }
                }

            } else if (choice.equals("View number of products in shopping carts")) { // view shopping carts
                // this code is really really really bad but i sure hope it works
                ArrayList<Customer> customers = Customer.loadAllCustomers();
                ArrayList<Store> stores = seller.getStores();
                ArrayList<String> sprods = new ArrayList<>();
                try {
                    for (int i = 0; i < stores.size(); i++) {
                        ArrayList<Product> products = stores.get(i).getProducts();
                        for (int j = 0; j < products.size(); j++) {
                            int amount = 0;
                            for (int k = 0; k < customers.size(); k++) {
                                ArrayList<ShoppingCart> cart = customers.get(k).getShoppingCart();
                                for (int l = 0; l < cart.size(); l++) {
                                    if (products.get(j).getProductName().equals(cart.get(l).getProduct().
                                            getProductName()))
                                        amount += cart.get(l).getAmount();
                                }
                            }
                            String temp = "Amount of " + products.get(i).getProductName() + "in shopping carts: " +
                                    amount;
                            sprods.add(temp);
                        }
                    }
                    String[] numProd = sprods.toArray(new String[0]);
                    JOptionPane.showMessageDialog(null, numProd, "Statistics", JOptionPane.
                            INFORMATION_MESSAGE);
                } catch (IndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(null, "No products in any shopping carts!",
                            "Statistics", JOptionPane.INFORMATION_MESSAGE);
                }

            } else if (choice.equals("Modify Account")) { // modify account
                String input;
                String[] options = {"Edit username", "Edit password", "Delete account", "Exit"};
                input = showInputDialog("How would you like to modify your account?", options,
                        "Modify account");

                if (input.equals("Edit username")) {
                    input = showInputDialog("What is your new username?", "Modify account");
                    try {
                        oos.writeObject("Change username");
                        oos.writeObject("Seller");
                        oos.writeObject(seller);
                        oos.writeObject(input);
                        oos.flush();

                        seller = (Seller) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (input.equals("Edit password")) {
                    input = showInputDialog("What is your new password?", "Modify account");
                    try {
                        oos.writeObject("Change password");
                        oos.writeObject("Seller");
                        oos.writeObject(seller);
                        oos.writeObject(input);
                        oos.flush();

                        seller = (Seller) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (input.equals("Delete account")) {
                    int delete = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete your account?",
                            "Delete Account?", JOptionPane.YES_NO_OPTION);
                    if (delete == JOptionPane.YES_OPTION) {
                        try {
                            oos.writeObject("Delete account");
                            oos.writeObject("Seller");
                            oos.writeObject(seller);
                            oos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "Account has been deleted",
                                "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "You chose not to delete this account! Returning to main menu...",
                                "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                        continue;
                    }
                }
                JOptionPane.showMessageDialog(null, "Returning to main menu",
                        "Marketplace", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("Create a store")) {
                String name = showInputDialog("Please enter a store name:", "Create a Store");
                try {
                    oos.writeObject("Create store");
                    oos.writeObject(seller);
                    oos.writeObject(name);
                    oos.flush();
                } catch (Exception e) {

                }
                JOptionPane.showMessageDialog(null, "Store successfully created!",
                        "Marketplace", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("Exit")) return;
        } while (true);
    }
}

