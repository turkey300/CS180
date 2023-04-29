import java.io.*;
import java.sql.Array;
import java.util.*;
import java.net.Socket;
import javax.swing.*;


/**
 * This is the Market class containing a main method to run the program. It contains customer marketplace and seller
 * market, which contain functions customer or seller can select from.
 *
 * @author Ekaterina Tszyao, Dimitri Paikos, Tyler Kei, Ryan Timmerman, Lab12
 * @version 4/10/2023
 */
public class Market implements Runnable {
    private Socket socket;

    public Market(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        Market market = new Market(connect());
        new Thread(market).start();
    }

    public void run() {
        // logging in/creating account
        Scanner scanner = new Scanner(System.in);//TODO:delete this
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.flush();
            String userType;    //Seller or Customer
            String accountType; //log in or create an account
            String username;
            String password;
            Customer customer = null; // customer account if they select customer
            Seller seller = null;
            JOptionPane.showMessageDialog(null, "Welcome to the marketplace!", "Welcome",
                    JOptionPane.INFORMATION_MESSAGE);
            String[] userTypeOptions = {"Seller", "Customer"};
            do {
                userType = (String) JOptionPane.showInputDialog(null, "Please select your user type."
                        , "User Type", JOptionPane.QUESTION_MESSAGE, null, userTypeOptions, userTypeOptions[0]);
                if (userType == null)
                    JOptionPane.showMessageDialog(null, "Please select your user type!", "Error",
                            JOptionPane.ERROR_MESSAGE);
            } while (userType == null);

            userTypeOptions = new String[]{"Log in", "Create an account"};
            do {
                accountType = (String) JOptionPane.showInputDialog(null, "Please select your account type."
                        , "Account Type", JOptionPane.QUESTION_MESSAGE, null, userTypeOptions, userTypeOptions[0]);
                if (accountType == null)
                    JOptionPane.showMessageDialog(null, "Please select your account type!", "Error",
                            JOptionPane.ERROR_MESSAGE);
            } while (accountType == null);


            // creating account
            if (accountType.equals("Create an account")) {
                username = JOptionPane.showInputDialog(null, "Please enter your username/email."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                password = JOptionPane.showInputDialog(null, "Please enter your password."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                oos.writeObject(accountType);
                oos.writeObject(userType);
                oos.writeObject(username);
                oos.writeObject(password);
                oos.flush();
                if (userType.equals("Customer")) {    //Creating customer account
//                    try {
//                        Customer newCustomer = new Customer(username, password, true);
//                    } catch (AlreadyUserException e) {
//                        System.out.println(e.getMessage());
//                    } catch (OtherUserException e) {
//                        System.out.println(e.getMessage());
//                        return;
//                    }
                    Object newCustomer = ois.readObject();
                    //didn't test so not sure if this will work
                    if (newCustomer instanceof Customer) {
                        JOptionPane.showMessageDialog(null, "Account created. Please log in."
                                , "Log in", JOptionPane.INFORMATION_MESSAGE); // logging in now
                        customer = customerLogin(oos, ois);   //delete scanner here
                    } else {
                        JOptionPane.showMessageDialog(null, newCustomer, "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;   //if failed to create an account(AlreadyUser or OtherUser), program ends
                    }

                } else if (userType.equals("Seller")) {    //creating Seller account
//                    try {
//                        Seller newSeller = new Seller(username, password, true);
//                    } catch (AlreadyUserException e) {
//                        System.out.println(e.getMessage());
//                    } catch (OtherUserException e) {
//                        System.out.println(e.getMessage());
//                        return;
//                    }
                    Object newSeller = ois.readObject();
                    //didn't test so not sure if this will work
                    if (newSeller instanceof Seller) {
                        JOptionPane.showMessageDialog(null, "Account created. Please log in."
                                , "Log in", JOptionPane.INFORMATION_MESSAGE); // logging in now
                        seller = sellerLogin(oos, ois);   //delete scanner here
                    } else {
                        JOptionPane.showMessageDialog(null, newSeller, "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;   //if failed to create an account(AlreadyUser or OtherUser), program ends
                    }
                }
            } else if (accountType.equals("Log in")) { // if they want to log in
                if (userType.equals("Customer")) {    //log in as a customer
                    customer = customerLogin(oos, ois);
                } else if (userType.equals("Seller")) {    //log in as a seller
                    seller = sellerLogin(oos, ois);
                }
            }

            JOptionPane.showMessageDialog(null, "Successfully logged in!"
                    , "Successful Log In", JOptionPane.INFORMATION_MESSAGE);
            // main marketplace
            if (customer != null) {
                // marketplace for customer
                customerMarketplace(customer, oos, ois, scanner);
            } else {
                // marketplace for seller
                sellerMarketplace(seller, oos, ois, scanner);
            }
            JOptionPane.showMessageDialog(null, "Goodbye!"
                    , "Farewell", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // establish connection with server
    public static Socket connect() {
        String input;
//        input = JOptionPane.showInputDialog(null, "Enter hostname", "Server?",
//                JOptionPane.QUESTION_MESSAGE);
        while (true) {
            input = showInputDialog("Enter hostname");
//            while (input == null || input.equals("-1") || input.isBlank()) {
//                if (input == null) {
//
//                }
//                if (input.isBlank())
//                    JOptionPane.showMessageDialog(null, "Input cannot be blank!", "Error!",
//                            JOptionPane.ERROR_MESSAGE);
//                input = JOptionPane.showInputDialog(null, "Enter hostname", "Server?",
//                        JOptionPane.QUESTION_MESSAGE);
//            }
            try {
                Socket socket1 = new Socket(input, 4242);
                return socket1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to host!", "Error!",
                        JOptionPane.ERROR_MESSAGE);
//                input = "-1";
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
        input = JOptionPane.showInputDialog(null, message, "Marketplace",
                JOptionPane.QUESTION_MESSAGE);
        while (input == null || input.isBlank()) {
            JOptionPane.showMessageDialog(null, "Input cannot be blank!", "Error!",
                    JOptionPane.ERROR_MESSAGE);
            input = JOptionPane.showInputDialog(null, message, "Marketplace",
                    JOptionPane.QUESTION_MESSAGE);
        }

        return input;
    }

    //log in as a customer
    public static Customer customerLogin(ObjectOutputStream oos, ObjectInputStream ois) {
        Customer customer;
        while (true) {
            try {
                String username = JOptionPane.showInputDialog(null, "Please enter your username/email."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                String password = JOptionPane.showInputDialog(null, "Please enter your password."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                oos.writeObject("Log in");
                oos.writeObject("Customer");
                oos.writeObject(username);
                oos.writeObject(password);
                oos.flush();
                Object newCustomer = ois.readObject();
//            try {
//                if (Customer.checkAccount(username, password)) {
//                    customer = Customer.loadCustomer(username);
//                    break;
//                } else { // if account details are wrong throws error
//                    throw new NoUserException("This account does not exist!");
//                }
//            } catch (NoUserException e) {
//                System.out.println(e.getMessage());
//            }
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
        Seller seller;
        while (true) {
            try {
                String username = JOptionPane.showInputDialog(null, "Please enter your username/email."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                String password = JOptionPane.showInputDialog(null, "Please enter your password."
                        , "Username", JOptionPane.QUESTION_MESSAGE);
                oos.writeObject("Log in");
                oos.writeObject("Seller");
                oos.writeObject(username);
                oos.writeObject(password);
                oos.flush();
                Object newSeller = ois.readObject();
//                try {
//                    if (Seller.checkAccount(username, password)) {
//                        seller = Seller.loadSeller(username);
//                        break;
//                    } else { // if account details are wrong throws error
//                        throw new NoUserException("This account does not exist!");
//                    }
//                } catch (NoUserException e) {
//                    System.out.println(e.getMessage());
//                }
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

    public static void customerMarketplace(Customer customer, ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) {
        //checks shopping cart for updated products
        customer.checkShoppingCart();
        //display of products
        ArrayList<Seller> sellers = new ArrayList<>();
//        ArrayList<Seller> sellers = Seller.loadAllSellers();
        while (true) {   //loop for the main page
            try { // moved get sellers inside loop so it refreshes
                oos.writeObject("List of sellers");
                oos.flush();
                sellers = (ArrayList<Seller>) ois.readObject();
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
                        //System.out.print(i + ". ");   //display product number
                        //System.out.println(products.get(l).marketplaceDisplay());   //display product info
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
            prodstring.add((i) + ". Exit.");
            // prodstring.add("Please select a number to visit product's page or option you want to perform.");
            int choice;
            String choyce;
            String[] choices = prodstring.toArray(new String[0]);
            choyce = (String) JOptionPane.showInputDialog(null, "Please select an option to visit a product page or perform an action", "Option?", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
            choyce = choyce.substring(0, choyce.indexOf(".")); // if they press X this errors because its null
            choice = Integer.parseInt(choyce);
            //  try {    //if input is not Integer, catch exception and repeat main page prompt
            // choice = Integer.parseInt(scanner.nextLine());
            // } catch (NumberFormatException e) {
            //  System.out.println("You didn't input an integer number.");
            // continue;   //start the main page prompts again
            // }
            // if (choice > i || choice <= 0) {    //user chose a number not from the list
            //  System.out.println("Please enter an existing option.");
            //  continue;    //start the main page prompts again
            if (choice <= (i - 8)) {    //user selected a product
                Product currentProduct = allProducts.get((choice - 1));
                productPage(scanner, currentProduct, allStores, sellers, customer, oos, ois);

            } else {    //user selected an option below listed products
                if (choice == (i - 7)) {   //Search for specific products
                    String term = showInputDialog("Please enter a term to search for.");


                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<Product> filteredProducts = new ArrayList<>();
                        ArrayList<String> allprodlist = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            if (allProducts.get(j).toString().toLowerCase().contains(term)) {
                                filteredProducts.add(allProducts.get(j));
                                // System.out.print(i + ". ");
                                // System.out.println(allProducts.get(j).marketplaceDisplay());
                                String temp = i + ". " + allProducts.get(j).marketplaceDisplay();
                                allprodlist.add(temp);
                                i++;
                            }
                        }
                        allprodlist.add(i + ". Back to main page.");
                        String[] markchoice = prodstring.toArray(new String[0]);
                        String chosen = (String) JOptionPane.showInputDialog(null, "Please select an option to visit a product's page", "Product?", JOptionPane.QUESTION_MESSAGE, null, markchoice, markchoice[0]);
                        chosen = Character.toString(chosen.charAt(0));
                        choice = Integer.parseInt(chosen);
                        // System.out.println(i + ". Back to main page.");
                        // System.out.println("Please select a number to visit product's page.");
                        // try {    //if input is not Integer, catch exception and repeat main page prompt
                        //     choice = Integer.parseInt(scanner.nextLine());
                        //   } catch (NumberFormatException e) {
                        //      System.out.println("You didn't input an integer number.");
                        //      continue;   //start the main page prompts again
                        //   }
                        //   if (choice > i || choice <= 0) {    //user chose a number not from the list
                        //     System.out.println("Please enter an existing option.");
                        //    continue;    //start the main page prompts again
                        if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = filteredProducts.get((choice - 1));
                            productPage(scanner, currentProduct, allStores, sellers, customer, oos, ois);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 6)) {
                    //Sort the marketplace on price:products with lower price are on the top
                    Collections.sort(allProducts, new ProductComparatorByPrice());
                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<String> sortprodlist = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            //System.out.print(i + ". ");
                            // System.out.println(allProducts.get(j).marketplaceDisplay());
                            String temp = i + ". " + allProducts.get(j).marketplaceDisplay();
                            sortprodlist.add(temp);
                            i++;
                        }
                        sortprodlist.add(i + ". Back to main page.");
                        String[] markchoice = prodstring.toArray(new String[0]);
                        String chosen = (String) JOptionPane.showInputDialog(null, "Please select an option to visit a product's page", "Product?", JOptionPane.QUESTION_MESSAGE, null, markchoice, markchoice[0]);
                        chosen = Character.toString(chosen.charAt(0));
                        choice = Integer.parseInt(chosen);
                        //  System.out.println(i + ". Back to main page.");
                        //  System.out.println("Please select a number to visit product's page.");
                        //  try {    //if input is not Integer, catch exception and repeat main page prompt
                        //     choice = Integer.parseInt(scanner.nextLine());
                        //  } catch (NumberFormatException e) {
                        //   System.out.println("You didn't input an integer number.");
                        //     continue;   //start the main page prompts again
                        // }
                        // if (choice > i || choice <= 0) {    //user chose a number not from the list
                        //     System.out.println("Please enter an existing option.");
                        //      continue;    //start the main page prompts again
                        if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = allProducts.get((choice - 1));
                            productPage(scanner, currentProduct, allStores, sellers, customer, oos, ois);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 5)) {
                    //Sort the marketplace on quantity available:
                    //products with more items available are on the top
                    Collections.sort(allProducts, new ProductComparatorByAvailability());
                    Collections.reverse(allProducts);
                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<String> anothersortlist = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            // System.out.print(i + ". ");
                            // System.out.println(allProducts.get(j).marketplaceDisplay());
                            String temp = i + ". " + allProducts.get(j).marketplaceDisplay();
                            anothersortlist.add(temp);
                            i++;
                        }
                        anothersortlist.add(i + ". Back to main page.");
                        String[] markchoice = prodstring.toArray(new String[0]);
                        String chosen = (String) JOptionPane.showInputDialog(null, "Please select an option to visit a product's page", "Product?", JOptionPane.QUESTION_MESSAGE, null, markchoice, markchoice[0]);
                        chosen = Character.toString(chosen.charAt(0));
                        choice = Integer.parseInt(chosen);
                        //  System.out.println(i + ". Back to main page.");
                        //  System.out.println("Please select a number to visit product's page.");
                        //   try {    //if input is not Integer, catch exception and repeat main page prompt
                        //      choice = Integer.parseInt(scanner.nextLine());
                        //   } catch (NumberFormatException e) {
                        //     System.out.println("You didn't input an integer number.");
                        //      continue;   //start the main page prompts again
                        //  }
                        //   if (choice > i || choice <= 0) {    //user chose a number not from the list
                        //        System.out.println("Please enter an existing option.");
                        //       continue;    //start the main page prompts again
                        if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = allProducts.get((choice - 1));
                            productPage(scanner, currentProduct, allStores, sellers, customer, oos, ois);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 4)) {
                    boolean validChoice;
                    //do {
                    validChoice = true;
                    // System.out.println("1. View a list of stores by number of products sold.");
                    // System.out.println("2. View a list of stores by the products purchased by you.");
                    String[] choicest = {"1. View a list of stores by number of products sold.", "2. View a list of stores by the products purchased by you."};
                    String chosen = showInputDialog("View a list of stores by number of products sold or products purchased by you", choicest);
                    String dashboardChoice = Character.toString(chosen.charAt(0));
                    //  String dashboardChoice = scanner.nextLine();
                    if (dashboardChoice.equals("1")) {
                        boolean validChoice2;
                        ArrayList<String> prods = new ArrayList<>();
                        //  do {
                        validChoice2 = true;
                        // for (int j = 0; j < allStores.size(); j++) {
                        //      System.out.printf("%d. Store name: %s, products sold: %d\n", (j + 1),
                        //             allStores.get(j).getStoreName(), allStores.get(j).getProductsSold());
                        //      String temp = (j+1) + "Store name: " + allStores.get(j).getStoreName() + ", products sold: " + allStores.get(j).getProductsSold();
                        //       prods.add(temp);
                        //    }
                        //  String[] markchoice = prods.toArray(new String[0]);
                        //  System.out.println();
                        //  System.out.println("1. Sort by low - high.");
                        //  System.out.println("2. Sort by high - low.");
                        //  System.out.println("3. Back to main page.");
                        String[] sort = {"1. Sort by low - high.", "2. Sort by high - low.", "3. Back to main page.", "4. Don't sort"};
                        String sortdec = showInputDialog("Would you like to sort the data?", sort);
                        // dashboardChoice = scanner.nextLine();
                        //if (!(dashboardChoice.equals("1") || dashboardChoice.equals("2") ||
                        //         dashboardChoice.equals("3"))) {
                        //     System.out.println("Please enter a valid option.");
                        //     validChoice2 = false;
                        //  }
                        // } while (!validChoice2);
                        if (sortdec.equals("1. Sort by low - high.")) {
                            Collections.sort(allStores, new StoreComparatorByProductsSold());
                        } else if (sortdec.equals("2")) {
                            Collections.sort(allStores, new StoreComparatorByProductsSold());
                            Collections.reverse(allStores);
                        } else if (sortdec.equals("3. Back to main page.")) {
                            break;//dashboardChoice = 3; return to main page
                        }
                        for (int j = 0; j < allStores.size(); j++) {
                            // System.out.printf("%d. Store name: %s, products sold: %d\n", (j + 1),
                            //    allStores.get(j).getStoreName(), allStores.get(j).getProductsSold();
                            String temp = (j + 1) + "Store name: " + allStores.get(j).getStoreName() + ", products sold: " + allStores.get(j).getProductsSold();
                            prods.add(temp);
                        }
                        String[] markchoice = prods.toArray(new String[0]);
                        JOptionPane.showMessageDialog(null, markchoice, "Proudcts", JOptionPane.INFORMATION_MESSAGE);
                        // System.out.println("Press any key to return to main page.");
                        // scanner.nextLine();
                    } else if (dashboardChoice.equals("2")) {
                        //View a list of stores by the products purchased by this customer
                        HashMap<String, Integer> historyByStore = customer.purchaseHistoryByStore(allStores);
                        boolean validChoice2;
                        ArrayList<String> storeprods = new ArrayList<>();
                        // do {
                        validChoice2 = true;
                        int index = 1;
                        // for (Map.Entry<String, Integer> entry : historyByStore.entrySet()) {
                        //  System.out.printf("%d. Store name: %s, products purchased by you: %d\n", index,
                        //         entry.getKey(), entry.getValue());

                        //   index++;
                        //   }
                        //  System.out.println();
                        //  System.out.println("1. Sort by low - high.");
                        //  System.out.println("2. Sort by high - low.");
                        // System.out.println("3. Back to main page.");
                        //  dashboardChoice = scanner.nextLine();
                        String[] sort = {"1. Sort by low - high.", "2. Sort by high - low.", "3. Back to main page.", "4. Don't sort"};
                        String sortdec = showInputDialog("Would you like to sort the data?", sort);
                        //  if (!(dashboardChoice.equals("1") || dashboardChoice.equals("2") ||
                        //          dashboardChoice.equals("3"))) {
                        //      System.out.println("Please enter a valid option.");
                        //      validChoice2 = false;
                        //   }
                        //  } while (!validChoice2);

                        ArrayList<Integer> purchasedProducts = new ArrayList<>();  //number of products purchased
                        for (Map.Entry<String, Integer> entry : historyByStore.entrySet()) {
                            purchasedProducts.add(entry.getValue());
                        }


                        if (sortdec.equals("1. Sort by low - high.")) {   //sort low-to-high
                            Collections.sort(purchasedProducts);
                        } else if (sortdec.equals("2. Sort by high - low.")) {    //sort high-to-low
                            Collections.sort(purchasedProducts);
                            Collections.reverse(purchasedProducts);
                        } else if (sortdec.equals("3. Back to main page.")) {
                            break;    //dashboardChoice = 3; return to main page
                        }

                        index = 1;
                        for (int num : purchasedProducts) {
                            for (Map.Entry<String, Integer> entry : historyByStore.entrySet()) {
                                if (entry.getValue() == num) {
                                    // System.out.printf("%d. Store name: %s, products purchased by you: %d\n", index,
                                    //        entry.getKey(), entry.getValue());
                                    String temp = index + ". Store Name: " + entry.getKey() + ", products purchased by you" + entry.getValue();
                                    storeprods.add(temp);
                                    index++;
                                }
                            }
                        }
                        //  System.out.println("Press any key to return to main page.");
                        // scanner.nextLine();
                    }
                    //  else {
                    // System.out.println("Please enter a valid option.");
                    //  validChoice = false;
                    //}
                    // } while (!validChoice);

                } else if (choice == (i - 3)) {    //view shopping cart
                   // do {
                        ArrayList<ShoppingCart> shoppingCart = customer.getShoppingCart();
                        if (shoppingCart.isEmpty()) {
                            //   System.out.println("Shopping cart is empty!");
                            JOptionPane.showMessageDialog(null, "Shopping Cart is Empty!", "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        //    System.out.println("Products in your shopping cart:");
                        ArrayList<String> shopprod = new ArrayList<>();
                        shopprod.add("Products in your shopping cart:");
                        for (int a = 0; a < shoppingCart.size(); a++) {
                            //   System.out.printf("%d. Product: %s. Amount: %d.\n",
                            //          a + 1, shoppingCart.get(a).getProduct().getProductName(),
                            //        shoppingCart.get(a).getAmount();
                            String temp = (a + 1) + ". Product: " + shoppingCart.get(a).getProduct().getProductName() + ". Amount: " + shoppingCart.get(a).getAmount();
                            shopprod.add(temp);
                        }
                        String[] cartproducts = shopprod.toArray(new String[0]);
                        String input;
                        ShoppingCartGUI gui = new ShoppingCartGUI(cartproducts, sellers, shoppingCart, customer, shopprod);
                        gui.setVisible(true);
                   // } while (true);

                } else if (choice == (i - 2)) {    //Modify account
                    String input;
                    String[] options = {"Edit username", "Edit password", "Delete account", "Exit"};
                    input = (String) showInputDialog("How would you like to modify your account?", options);
//                    do {
//                        System.out.println("1. Edit username.");
//                        System.out.println("2. Edit password.");
//                        System.out.println("3. Delete account.");
//                        input = scanner.nextLine();
//                        if (!(input.equals("1") || input.equals("2") || input.equals("3")))
//                            System.out.println("Please enter a number corresponding to an option.");
//                    } while (!(input.equals("1") || input.equals("2") || input.equals("3")));

                    if (input.equals("Edit username")) {
//                        System.out.println("What is your new username?");
//                        input = scanner.nextLine();
                        input = showInputDialog("What is your new username?");
                        try {
                            oos.writeObject("Change username");
                            oos.writeObject("Customer");
                            oos.writeObject(customer);
                            oos.writeObject(input);
                            oos.flush();

                            customer = (Customer) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
//                        customer.setUsername(input);
                    } else if (input.equals("Edit password")) {
//                        System.out.println("What is your new password?");
//                        input = scanner.nextLine();
                        input = showInputDialog("What is your new password");
                        try {
                            oos.writeObject("Change password");
                            oos.writeObject("Customer");
                            oos.writeObject(customer);
                            oos.writeObject(input);
                            oos.flush();

                            customer = (Customer) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
//                        customer.setPassword(input);
                    } else if (input.equals("Delete account")) {
//                        System.out.println("Are you sure you want to delete your account?");
//                        System.out.println("1. Yes\n2. No");
//                        input = scanner.nextLine();
                        int delete = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to delete your account?",
                                "Marketplace", JOptionPane.YES_NO_OPTION);
                        if (delete == JOptionPane.YES_OPTION) {
                            try {
                                oos.writeObject("Delete account");
                                oos.writeObject("Customer");
                                oos.writeObject(customer);
                                oos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            customer.deleteAccount();
//                            System.out.println("Account has been deleted.");
                            JOptionPane.showMessageDialog(null, "Account has been deleted",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        } else {
//                            System.out.println("You chose not to delete this account! Returning to main page...");
                            JOptionPane.showMessageDialog(null,
                                    "You chose not to delete this account! Returning to main page...",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            continue;
                        }
                    }
//                    System.out.println("Success! Returning to main page...");
                    JOptionPane.showMessageDialog(null, "Returning to main page...",
                            "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                } else if (choice == (i - 1)) {
                    ArrayList<PurchaseHistory> purchaseHistory = customer.getPurchaseHistory();
                    if (purchaseHistory.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No purchase history.", "Purhcase history", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        //System.out.println("Purchase history: (Newest products purchased listed first)");
                        ArrayList<String> phistory = new ArrayList<>();
                        for (int j = purchaseHistory.size() - 1; j >= 0; j--) {
                            PurchaseHistory history = purchaseHistory.get(j);
                         //   System.out.printf("Product: %s. Amount purchased: %d. Store: %s\n",
                         //           history.getProduct().getProductName(), history.getAmount(), history.getStoreName());
                            String temp = "Product: " + history.getProduct().getProductName() + ". Amount purchased: " + history.getAmount() + ". Store: " + history.getStoreName();
                            phistory.add(temp);
                        }
                        String[] purhist = phistory.toArray(new String[0]);
                        PurchaseHistoryGUI phist = new PurchaseHistoryGUI(purchaseHistory, purhist);
                       do {
                           phist.setVisible(true);
                       } while(!phist.breakloop());
                      //  if (phist.breakloop()) {
                        //    break;
                      //  }
                        System.out.println(phist.breakloop());
                    }
                } else if (choice == i) return;
            }
        }
    }

    public static void productPage(Scanner scanner, Product currentProduct, ArrayList<Store> stores,
                                   ArrayList<Seller> sellers, Customer customer, ObjectOutputStream oos,
                                   ObjectInputStream ois) {
        //this is a separated method used to display product's page and realize further operations
        //it ends and returns void only when user selects "back to main page"
        Store currentStore = null;
        try { // grabs sellers again
            oos.writeObject("List of sellers");
            oos.flush();
            sellers = (ArrayList<Seller>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        stores = new ArrayList<>(); // grabbing stores again
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
                System.out.println("Before refresh:" + currentProduct.productPageDisplay());
                oos.writeObject("refresh");
                oos.writeObject(currentProduct);
                Object serverInput = ois.readObject();
                if(!(serverInput instanceof Product)){
                    JOptionPane.showMessageDialog(null, "Could not find product! " +
                                    "It may have been deleted.",
                            "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                currentProduct = (Product) serverInput;
                System.out.println("After refresh:"+currentProduct.productPageDisplay());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            String input = showInputDialog(currentProduct.productPageDisplay(), new String[]{"1. Purchase this product.",
                    "2. Add this product to shopping cart.", "3. Refresh page.", "4. Back to main page."});
//            System.out.println(currentProduct.productPageDisplay());
//            System.out.println("1. Purchase this product.");
//            System.out.println("2. Add this product to shopping cart.");
//            System.out.println("3. Back to main page.");
//            String choiceOnProductPage = scanner.nextLine();
            char choiceOnProductPage = input.charAt(0);
            switch (choiceOnProductPage) {
                case '1':   //purchase this product
                    int amount;
                    while (true) {
                        input = showInputDialog("What amount would you like to purchase?");
//                        System.out.println("What amount would you like to purchase?");
                        try {
                            amount = Integer.parseInt(input);
                            if (amount < 0) {
                                JOptionPane.showMessageDialog(null, "Please input valid number.",
                                        "Error!", JOptionPane.ERROR_MESSAGE);
//                            System.out.println("Please input valid number.");
                                continue;
                            } else break;
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Please input valid number.",
                                    "Error!", JOptionPane.ERROR_MESSAGE);
//                            System.out.println("Please input valid number.");
                        }
                    }
                    try {
                        System.out.println("Before purchase:"+currentProduct.productPageDisplay());
                        oos.writeObject("Purchase product");
                        oos.flush();
                        oos.writeObject(currentProduct);
//                        ois.readObject();
//                        oos.writeObject("Waiting");
//                        oos.flush();
//                        try { // grabs sellers again
//                            oos.writeObject("List of sellers");
//                            oos.flush();
//                            sellers = (ArrayList<Seller>) ois.readObject();
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//
//                        stores = new ArrayList<>(); // grabbing stores again
//                        for (int j = 0; j < sellers.size(); j++) {
//                            ArrayList<Store> store = sellers.get(j).getStores();
//                            stores.addAll(stores);
//                        }
//
//                        boolean stillProduct = false;
//                        for (int k = 0; k < stores.size(); k++) { // checking if product is in store
//                            if (currentProduct.getStoreName().equals(stores.get(k).getStoreName())) {
//                                stillProduct = true;
//                                currentStore = stores.get(k);
//                            }
//                        }
                        boolean stillProduct = (boolean) ois.readObject();

                        if (stillProduct) { // if product is still valid
//                            oos.writeObject("True");
//                            oos.writeObject(currentStore);
//                            oos.writeObject(currentProduct);
                            oos.writeObject(amount);
                            oos.writeObject(customer);
                            oos.flush();

                            String message = (String) ois.readObject();
                            JOptionPane.showMessageDialog(null, message, "Marketplace",
                                    JOptionPane.INFORMATION_MESSAGE);
                            System.out.println("After purchase:"+currentProduct.productPageDisplay());

                        } else { // product cannot be found
                            JOptionPane.showMessageDialog(null, "Could not find product! " +
                                            "It may have been deleted.",
                                    "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            return;
//                            oos.writeObject("False");
//                            oos.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                        if (currentStore.purchaseProductFromStore(currentProduct, amount, customer)) {
//                            System.out.println("Purchased successfully!");
//                            System.out.println("Returning to product's page...\n");
//                            for (int i = 0; i < sellers.size(); i++) {
//                                if (sellers.get(i).getUsername().equals(currentStore.getSeller())) {
//                                    sellers.get(i).saveSeller();
//                                }
//                            }
//                            break;
//                        } else {
//                            System.out.println("Sorry, we don't have enough items available.");
//                            System.out.println("Returning to product's page...\n");
//                            break;
//                        }
                    break;
                case '2':    // Add this product to shopping cart
                    int amount1;
                    while (true) {
                        input = showInputDialog("What amount would you like to put into shopping cart?");
//                        System.out.println("What amount would you like to put into shopping cart?");
                        try {
                            amount1 = Integer.parseInt(input);
                            if (amount1 < 0) {
                                JOptionPane.showMessageDialog(null, "Please input valid number.",
                                        "Error!", JOptionPane.ERROR_MESSAGE);
//                            System.out.println("Please input valid number.");
                            } else break;
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Please input valid number.",
                                    "Error!", JOptionPane.ERROR_MESSAGE);
//                            System.out.println("Please input valid number.");
                        }
                    }
                    try {
                        oos.writeObject("Add to cart");
                        oos.flush();
                        oos.writeObject(currentProduct);
                        boolean stillProduct = (boolean) ois.readObject();

                        if (stillProduct) { // if product is still valid
//                            oos.writeObject("True");
//                            oos.writeObject(currentStore);
//                            oos.writeObject(currentProduct);
                            oos.writeObject(amount1);
                            oos.writeObject(customer);
                            oos.flush();

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

//                        if (amount1 <= currentProduct.getAvailableQuantity()) {
//                            for (int i = 0; i < sellers.size(); i++) {
//                                if (sellers.get(i).getUsername().equals(currentStore.getSeller())) {
//                                    customer.addShoppingCart(currentProduct, sellers.get(i), amount1);
//                                    customer.saveCustomer();
//                                    System.out.println("Successfully added to shopping cart!");
//                                    System.out.println("Returning to product's page...\n");
//                                }
//                            }
//                            break;
//                        } else {
//                            String message = "Sorry, we don't have enough items available.\nReturning to product's page...\n";
//                            JOptionPane.showMessageDialog(null, message, "Purchase Status", JOptionPane.INFORMATION_MESSAGE);
////                            System.out.println("Sorry, we don't have enough items available.");
////                            System.out.println("Returning to product's page...\n");
//                            break;
//                        }
                    break;
                case '4':
                    return;   //returns to main page
                case '3':    //returns to the beginning of while-true loop and refreshes page
//                default:
//                    System.out.println("Please enter an existing option.");
            }
        }

    }

    public static void sellerMarketplace(Seller seller, ObjectOutputStream oos, ObjectInputStream ois, Scanner scanner) {
        do {
            //int choice = 0;
            //System.out.println("What would you like to do?");
            //do {
            //   System.out.println("1. Modify products.");
            //  System.out.println("2. View a list of sales by store.");
            // System.out.println("3. View a dashboard with statistics for each stores.");
            // System.out.println("4. View number of products in shopping carts.");
            // System.out.println("5. Modify Account.");
            // System.out.println("6. Create a store.");
            // System.out.println("7. Exit.");
            String[] selloptions = {"Modify Products", "View a list of sales by store", "View a dashboard with statistics for each stores", "View number of products in shopping carts", "Modify Account", "Create a store", "Exit"};
            String choice;
            choice = (String) JOptionPane.showInputDialog(null, "What would you like to do", "Action", JOptionPane.QUESTION_MESSAGE, null, selloptions, selloptions[0]);

            //  try {    //if input is not Integer, catch exception and repeat main page prompt
            //    choice = Integer.parseInt(scanner.nextLine());
            //  if (choice < 1 || choice > 7)
            //    System.out.println("Please enter an available option.");
            //} catch (NumberFormatException e) {
            //  System.out.println("Please enter an available option.");
            //}
            //    } while (!(choice >= 1 && choice <= 7));

            if (choice.equals("Modify Products")) { // Modify products
                boolean valid;
                int storeNum = 0;
                //System.out.println("Which store would you like to edit?");
                //do {
                valid = true;
                ArrayList<Store> astores = seller.getStores();
                Store[] stores = new Store[astores.size()];
                String[] names = new String[astores.size()];
                for (int i = 0; i < astores.size(); i++) {
                    // System.out.printf("%d. %s\n", i + 1, seller.getStores().get(i).getStoreName());
                    stores[i] = astores.get(i);
                    names[i] = stores[i].getStoreName();
                }
                /**
                 try {
                 storeNum = Integer.parseInt(scanner.nextLine());
                 if (!(storeNum >= 1 && storeNum <= seller.getStores().size())) {
                 System.out.println("Please enter a number corresponding to a store.");
                 valid = false;
                 }
                 } catch (NumberFormatException e) {
                 System.out.println("Please enter a number corresponding to a store.");
                 valid = false;
                 }
                 */
                String currentStores;
                //  System.out.println(stores[0]); just testing
                currentStores = (String) JOptionPane.showInputDialog(null, "Which store would you like to edit?", "Store?", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
                int j;
                j = Arrays.asList(names).indexOf(currentStores);
                Store currentStore = stores[j];
                // currentStore = stores[j];
                // System.out.println(currentStore); more tests
                // } while (!valid);
                // Store currentStore = seller.getStore((storeNum - 1));
                /**
                 System.out.println("What would you like to do?");
                 System.out.println("1. Add a product");
                 System.out.println("2. Edit a product");
                 System.out.println("3. Delete a product");
                 System.out.println("4. Export products");
                 */
                String[] choices = {"1. Add a product", "2. Edit a product", "3. Delete a product", "4. Export products"};
                String action;
                action = (String) JOptionPane.showInputDialog(null, "What would you like to do?", "Choice?", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
                /**
                 int modifyOption = 0;
                 do {
                 valid = true;
                 try {
                 modifyOption = Integer.parseInt(scanner.nextLine());
                 if (modifyOption < 1 || modifyOption > 4) {
                 System.out.println("Please enter an available option.");
                 valid = false;
                 }
                 } catch (NumberFormatException e) {
                 System.out.println("Please enter an available option.");
                 valid = false;
                 }
                 } while (!valid);
                 */
                if (action.equals("1. Add a product")) {    //add a product
                    String input;
                    do {
                        System.out.println("1. Import product from csv.\n2. Create product in terminal.");
                        input = scanner.nextLine();
                        if (!(input.equals("1") || input.equals("2")))
                            System.out.println("Please enter an option corresponding to a product.");
                    } while (!(input.equals("1") || input.equals("2")));

                    if (input.equals("1")) {    //Import product from csv.
                        System.out.println("Please enter the file path to the csv file.");
                        String file = scanner.nextLine();

                        File f = new File(file);
                        try (BufferedReader bfr = new BufferedReader(new FileReader(f))) {
                            String line = bfr.readLine();
                            while (line != null) {
//                productName;description;int availableQuantity;double price;String storeName
                                try {
                                    String productName;
                                    String description;
                                    int availableQuantity;
                                    double price;
                                    String storeName;
                                    String substring;

                                    productName = line.substring(0, line.indexOf(","));
                                    productName = productName.replaceAll("\"", "");
                                    substring = line.substring(line.indexOf(",") + 1);
                                    description = substring.substring(0, substring.indexOf(","));
                                    description = description.replaceAll("\"", "");
                                    substring = substring.substring(substring.indexOf(",") + 1);
                                    availableQuantity = Integer.parseInt(substring.substring(0, substring.indexOf("," +
                                            "")).replaceAll("\"", ""));
                                    substring = substring.substring(substring.indexOf(",") + 1);
                                    price = Double.parseDouble(substring.substring(0, substring.indexOf("," +
                                            "")).replaceAll("\"", ""));
                                    storeName = substring.substring(substring.indexOf(",") + 1);
                                    storeName = storeName.replaceAll("\"", "");


                                    if (availableQuantity > 0 && price >= 0) {
                                        currentStore.addProduct(new Product(productName, description,
                                                availableQuantity, price, currentStore.getStoreName()));
                                        System.out.println("Product added!");
                                        seller.saveSeller();
                                    } else {
                                        System.out.println("Product has invalid data!");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Error parsing product!");
                                }

                                line = bfr.readLine();
                            }
                        } catch (Exception e) {
                            System.out.println("Error reading in product!");
                        }
                    } else if (input.equals("2")) {    //Create product in terminal.
                        System.out.println("Please enter a product name:");
                        String name = scanner.nextLine();
                        System.out.println("Please enter a product description:");
                        String description = scanner.nextLine();
                        String q;
                        String p;
                        double price = -1;
                        int quantity = 0;

                        do {
                            System.out.println("Please enter an available quantity.");
                            q = scanner.nextLine();
                            try {
                                quantity = Integer.parseInt(q);
                                if (quantity <= 0)
                                    System.out.println("Please enter an integer greater than 0");
                            } catch (Exception e) {
                                System.out.println("Please enter an integer greater than 0");
                            }
                        } while (quantity <= 0);

                        do {
                            System.out.println("Please enter a price for the product.");
                            p = scanner.nextLine();
                            try {
                                price = Double.parseDouble(p);
                                if (price < 0)
                                    System.out.println("Please enter a number greater or equal to 0");
                            } catch (Exception e) {
                                System.out.println("Please enter a number greater or equal to 0");
                            }
                        } while (!(price >= 0));

                        currentStore.addProduct(new Product(name, description,
                                quantity, price, currentStore.getStoreName()));
                        System.out.println("Product added!");
                        currentStore.saveStore();
                        seller.saveSeller();
                    }
                } else if (action.equals("2. Edit a product")) {    //edit a product
                    int productNum = -1;
                    do {
                        valid = true;
                        for (int i = 0; i < currentStore.getProducts().size(); i++) {
                            System.out.printf("%d. %s, description: %s\n", i + 1, currentStore.getProduct(i)
                                    .getProductName(), currentStore.getProduct(i).getDescription());
                        }
                        System.out.println("Please select a product to edit.");
                        try {
                            productNum = Integer.parseInt(scanner.nextLine());
                            if (productNum < 0 || productNum > currentStore.getProducts().size()) {
                                System.out.println("Please enter a number corresponding to a product.");
                                valid = false;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a number corresponding to a product.");
                            valid = false;
                        }
                    } while (!valid);
                    Product currentProduct = currentStore.getProduct((productNum - 1));
                    System.out.println(currentProduct.productPageDisplay());
                    System.out.println("1. Edit product name.");
                    System.out.println("2. Edit product description.");
                    System.out.println("3. Edit product available quantity.");
                    System.out.println("4. Edit product price.");
                    do {
                        valid = true;
                        String editOption = scanner.nextLine();
                        if (editOption.equals("1")) {
                            System.out.println("Please enter a new product name:");
                            String name = scanner.nextLine();
                            currentProduct.editProductName(name);
                        } else if (editOption.equals("2")) {
                            System.out.println("Please enter a new product description:");
                            String description = scanner.nextLine();
                            currentProduct.editDescription(description);
                        } else if (editOption.equals("3")) {
                            int availableQuantity = -1;
                            do {
                                System.out.println("Please enter a new available quantity:");
                                try {
                                    availableQuantity = Integer.parseInt(scanner.nextLine());
                                    if (availableQuantity < 0)
                                        System.out.println("Please enter a non-negative integer.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a non-negative integer.");
                                }
                            } while (availableQuantity < 0);
                            currentProduct.editAvailableQuantity(availableQuantity);
                        } else if (editOption.equals("4")) {
                            double price = -1;
                            do {
                                System.out.println("Please enter a new price:");
                                try {
                                    price = Double.parseDouble(scanner.nextLine());
                                    if (price < 0)
                                        System.out.println("Please enter a non-negative number.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a non-negative number.");
                                }
                            } while (price < 0);
                            currentProduct.editPrice(price);
                        } else {
                            System.out.println("Please enter an available option.");
                            valid = false;
                        }
                    } while (!valid);
                    seller.saveSeller();
                } else if (action.equals("3. Delete a product")) {    //modify option = 3;delete a product
                    int productNum = -1;
                    do {
                        valid = true;
                        for (int i = 0; i < currentStore.getProducts().size(); i++) {
                            System.out.printf("%d. %s, description: %s\n", i + 1, currentStore.getProduct(i)
                                    .getProductName(), currentStore.getProduct(i).getProductName());
                        }
                        System.out.println("Please select a product to edit.");
                        try {
                            productNum = Integer.parseInt(scanner.nextLine());
                            if (productNum < 0 || productNum > currentStore.getProducts().size()) {
                                System.out.println("Please enter a number corresponding to a product.");
                                valid = false;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a number corresponding to a product.");
                            valid = false;
                        }
                    } while (!valid);
                    Product currentProduct = currentStore.getProduct(productNum);
                    currentStore.deleteProduct(currentProduct);
                    seller.saveSeller();
                } else if (action.equals("4. Export products")) { //modify option = 4; export products
                    while (true) {
                        System.out.println("Please enter the file path to export to.");
                        String file = scanner.nextLine();
                        File f = new File(file);
                        if (f.exists()) {
                            System.out.println("This file already exists! Try a new file path.");
                        } else {
                            ArrayList<Product> products = currentStore.getProducts();
                            try (PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
                                for (int i = 0; i < products.size(); i++) {
//                productName;description;int availableQuantity;double price;String storeName
                                    Product product = products.get(i);
                                    pw.printf("%s,%s,%d,%f,%s\n", product.getProductName(), product.getDescription(),
                                            product.getAvailableQuantity(), product.getPrice(), product.getStoreName());
                                }
                            } catch (Exception e) {
                                System.out.println("Error exporting file!");
                            }

                            System.out.println("Products exported!");
                            break;
                        }
                    }
                }

                System.out.println("Returning to main menu.");
            } else if (choice.equals("View a list of sales by store")) {
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
                        System.out.println(storename[i]);
                        int j = 0;
                        do {
                            //for (int j = 0; j < revlist.length; j++) {
                            //I'm not sure why I made the Double and Integer arrays into Strings but I did so
                            revlist[j] = (revenue.get(j)).toString();
                            custlist[j] = (customers.get(j));
                            purchased[j] = (amount.get(j)).toString();
                            if ((custlist[j].equals("")) || (purchased[j].equals("0")) || (revlist[j].equals("0.0"))) {
                                System.out.println("No sales have been made on this store");
                            }
                            System.out.printf("Customer %s purchased %s produces for a total sale of $%s",
                                    custlist[j], purchased[j], revlist[j]);
                            j++;
                        } while (j < revlist.length);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("No sales have been made on this store");
                    }
                }

                System.out.println("Returning to main menu.");
            } else if (choice.equals("View a dashboard with statistics for each stores")) {    //choice = 3, statistics
                System.out.println("What would you like Statistics of?");
                boolean validate;
                int stats = 0;
                do {
                    validate = true;
                    System.out.println("1. Product Statistics");
                    System.out.println("2. Customer Statistics");
                    try {
                        stats = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer.");
                        validate = false;
                    }
                    if (stats != 1 && stats != 2) {
                        System.out.println("Please enter 1 or 2.");
                        validate = false;
                    }
                } while (!validate);
                if (stats == 1) {
                    ArrayList<Product> prodlist = new ArrayList<>();
                    ArrayList<Integer> numsold = new ArrayList<>();
                    ArrayList<Store> storelist = new ArrayList<>();
                    storelist = seller.getStores();
                    Store[] stores = new Store[storelist.size()];
                    String[] storename = new String[stores.length];
                    int choices = 0;
                    boolean valids = true;
                    System.out.println("Would you like to sort the statistics? (yes/no)");
                    String sort = scanner.nextLine();
                    if (sort.equals("yes")) {
                        do {
                            System.out.println("How would you like to sort?");
                            System.out.println("1. Sale price low - high");
                            System.out.println("2. Sale price high - low");
                            try {
                                choices = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid integer.");
                                valids = false;
                            }
                            if (choices != 1 && choices != 2) {
                                System.out.println("Please enter either 1 or 2");
                                valids = false;
                            }
                        } while (!valids);
                        if (choices == 1) {
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
                                    System.out.println(storename[i]);
                                    String[] prods = new String[products.length];
                                    //for (int l = 0; l < products.length; l++) {
                                    // prods[l] = products[l].toString();
                                    // }
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
                                        System.out.printf("Product %s sold %d units\n", sproduct[m], number[m]);
                                    }
                                } catch (NullPointerException e) {
                                    System.out.println("No sales have been made at this store");
                                }
                            }
                        }
                        if (choices == 2) {
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
                                    System.out.println(storename[i]);
                                    String[] prods = new String[products.length];
                                    // for (int l = 0; l < products.length; l++) {
                                    // prods[l] = products[l].toString();
                                    // }
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
                                        System.out.printf("Product %s sold %d units\n", sproduct[m], number[m]);
                                    }
                                } catch (NullPointerException e) {
                                    System.out.println("No sales have been made at this store");
                                }
                            }
                        }
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
                                System.out.println(storename[i]);
                                String[] prods = new String[products.length];
                                // for (int l = 0; l < products.length; l++) {
                                //     prods[l] = products[l].toString();
                                // }
                                for (int m = 0; m < prods.length; m++) {
                                    System.out.printf("Product %s sold %d units\n", sproduct[m], number[m]);
                                }
                            } catch (NullPointerException e) {
                                System.out.println("No sales have been made at this store");
                            }
                        }
                    }
                } else {
                    System.out.println("Would you like to sort the statistics? (yes/no)");
                    String sort = scanner.nextLine();
                    boolean valids = true;
                    if (sort.equals("yes")) {
                        int choices = 0;
                        do {
                            System.out.println("How would you like to sort?");
                            System.out.println("1. Sale price low - high");
                            System.out.println("2. Sale price high - low");
                            try {
                                choices = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid integer.");
                                valids = false;
                            }
                            if (choices != 1 && choices != 2) {
                                System.out.println("Please enter either 1 or 2");
                                valids = false;
                            }
                        } while (!valids);
                        if (choices == 1) {
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
                                    System.out.println(storename[i]);
                                    int j = 0;
                                    do {
                                        //for (int j = 0; j < revlist.length; j++) {
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
                                            System.out.println("No sales have been made on this store");
                                        }
                                        System.out.printf("Customer %s purchased %d produces for a total sale of $%f\n",
                                                custlist[j], purchased[j], revlist[j]);
                                        j++;
                                    } while (j < revlist.length);
                                } catch (IndexOutOfBoundsException e) {
                                    System.out.println("No sales have been made on this store");
                                }
                            }
                        }
                        if (choices == 2) {
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
                                    System.out.println(storename[i]);
                                    int j = 0;
                                    do {
                                        //for (int j = 0; j < revlist.length; j++) {
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
                                            System.out.println("No sales have been made on this store");
                                        }
                                        System.out.printf("Customer %s purchased %d produces for a total sale of $%f\n",
                                                custlist[j], purchased[j], revlist[j]);
                                        //}
                                        j++;
                                    } while (i < revlist.length);
                                } catch (IndexOutOfBoundsException e) {
                                    System.out.println("No sales have been made on this store");
                                }
                            }
                        }

                    } else if (sort.equals("no")) {
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
                                System.out.println(storename[i]);
                                for (int j = 0; j < revlist.length; j++) {
                                    // I'm not sure why I made the Double and Integer arrays into Strings but I did so
                                    revlist[j] = (revenue.get(j)).toString();
                                    custlist[j] = (customers.get(j));
                                    purchased[j] = (amount.get(j)).toString();
                                    if ((custlist[j].equals("")) || (purchased[j].equals("0")) ||
                                            (revlist[j].equals("0.0"))) {
                                        System.out.println("No sales have been made on this store");
                                    }
                                    System.out.printf("Customer %s purchased %s produces for a total sale of $%s\n",
                                            custlist[j], purchased[j], revlist[j]);
                                }
                            } catch (NullPointerException e) {
                                System.out.println("No sales have been made on this store");
                            }
                        }
                    }
                }

                System.out.println("Returning to main menu.");
            } else if (choice.equals("View number of products in shopping carts")) { // view shopping carts
                // this code is really really really bad but i sure hope it works
                // gotta test this
                ArrayList<Customer> customers = Customer.loadAllCustomers();
                ArrayList<Store> stores = seller.getStores();
                for (int i = 0; i < stores.size(); i++) {
                    ArrayList<Product> products = stores.get(i).getProducts();
                    for (int j = 0; j < products.size(); j++) {
                        int amount = 0;
                        for (int k = 0; k < customers.size(); k++) {
                            ArrayList<ShoppingCart> cart = customers.get(k).getShoppingCart();
                            for (int l = 0; l < cart.size(); l++) {
                                if (products.get(j).getProductName().equals(cart.get(l).getProduct().getProductName()))
                                    amount += cart.get(l).getAmount();
                            }
                        }
                        System.out.println(products.get(i).productPageDisplay());
                        System.out.println("Amount of product in shopping carts: " + amount);
                    }
                }

                System.out.println("Returning to main menu.");
            } else if (choice.equals("Modify Account")) { // modify account
                String input;
                String[] options = {"Edit username", "Edit password", "Delete account", "Exit"};
                input = (String) showInputDialog("How would you like to modify your account?", options);
//                do {
//                    System.out.println("1. Edit username.");
//                    System.out.println("2. Edit password.");
//                    System.out.println("3. Delete account.");
//                    input = scanner.nextLine();
//                    if (!(input.equals("1") || input.equals("2") || input.equals("3")))
//                        System.out.println("Please enter a number corresponding to an option.");
//                } while (!(input.equals("1") || input.equals("2") || input.equals("3")));

                if (input.equals("Edit username")) {
//                    System.out.println("What is your new username?");
//                    input = scanner.nextLine();
                    input = (String) showInputDialog("What is your new username?");
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
//                    seller.setUsername(input);
                } else if (input.equals("Edit password")) {
//                    System.out.println("What is your new password?");
//                    input = scanner.nextLine();
                    input = (String) showInputDialog("What is your new password?");
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
//                    seller.setPassword(input);
                } else if (input.equals("Delete account")) {
//                    System.out.println("Are you sure you want to delete your account?");
//                    System.out.println("1. Yes\n2. No");
//                    input = scanner.nextLine();
                    int delete = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete your account?",
                            "Marketplace", JOptionPane.YES_NO_OPTION);
                    if (delete == JOptionPane.YES_OPTION) {
                        try {
                            oos.writeObject("Delete account");
                            oos.writeObject("Seller");
                            oos.writeObject(seller);
                            oos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        seller.deleteAccount();
//                        System.out.println("Account has been deleted.");
                        JOptionPane.showMessageDialog(null, "Account has been deleted",
                                "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } else {
//                        System.out.println("You chose not to delete this account! Returning to main menu...");
                        JOptionPane.showMessageDialog(null,
                                "You chose not to delete this account! Returning to main menu...",
                                "Marketplace", JOptionPane.INFORMATION_MESSAGE);
                        continue;
                    }
                }
//                System.out.println("Success! Returning to main menu.");
                JOptionPane.showMessageDialog(null, "Returning to main menu",
                        "Marketplace", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("Create a store")) {
                System.out.println("Please enter a store name:");
                String name = scanner.nextLine();
                Store store = new Store(name, seller.getUsername());
                seller.addStore(store);
            } else if (choice.equals("Exit")) return;
        } while (true);

    }
}

