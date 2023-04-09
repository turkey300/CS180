import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Market {
    public static void main(String[] args) {
        // logging in/creating account
        Scanner scanner = new Scanner(System.in);
        String userType;    //1 for Seller, 2 for Customer
        String accountType; //1 for login or 2 for creating account
        String username;
        String password;
        Customer customer = null; // customer account if they select customer
        Seller seller = null;
        // add name if we want
        System.out.println("Welcome to the marketplace!");
        System.out.println("Please select your account type");
        do {
            System.out.print("1. Seller\n2. Customer\n");
            userType = scanner.nextLine();
            if (!userType.equals("1") && !userType.equals("2"))
                System.out.println("Please enter 1 for seller or 2 for customer");
        } while (!userType.equals("1") && !userType.equals("2"));

        do {
            System.out.print("1. Login\n2. Create an account\n");
            accountType = scanner.nextLine();
            if (!accountType.equals("1") && !accountType.equals("2"))
                System.out.println("Please enter 1 to login or 2 to create an account");
        } while (!accountType.equals("1") && !accountType.equals("2"));

        // creating account
        if (accountType.equals("2")) {
            System.out.println("Please enter a username/email");
            username = scanner.nextLine();
            System.out.println("Please enter a password");
            password = scanner.nextLine();
            if (userType.equals("2")) {    //Creating customer account
                try {
                    Customer newCustomer = new Customer(username, password, true);
                } catch (AlreadyUserException e) {
                    System.out.println(e.getMessage());
                } catch (OtherUserException e) {
                    System.out.println(e.getMessage());
                    return;
                }

                System.out.println("Please login"); // logging in now
                customer = customerLogin(scanner);
            } else if (userType.equals("1")) {    //creating Seller account
                try {
                    Seller newSeller = new Seller(username, password, true);
                } catch (AlreadyUserException e) {
                    System.out.println(e.getMessage());
                } catch (OtherUserException e) {
                    System.out.println(e.getMessage());
                    return;
                }

                System.out.println("Please login"); // logging in now
                seller = sellerLogin(scanner);
            }
        } else if (accountType.equals("1")) { // if they want to login
            if (userType.equals("2")) {    //log in as a customer
                customer = customerLogin(scanner);
            } else if (userType.equals("1")) {    //log in as a seller
                seller = sellerLogin(scanner);
            }
            System.out.println("Goodbye!");
        }

        System.out.println("Successfully logged in!");
        // main marketplace
        if (customer != null) {
            // marketplace for customer
            customerMarketplace(scanner, customer);
        } else {
            // marketplace for seller
            sellerMarketplace(scanner, seller);
        }
    }


    //log in as a customer
    public static Customer customerLogin(Scanner scanner) {
        Customer customer;
        while (true) {
            System.out.println("Please enter your username/email");
            String username = scanner.nextLine();
            System.out.println("Please enter your password");
            String password = scanner.nextLine();
            try {
                if (Customer.checkAccount(username, password)) {
                    customer = Customer.loadCustomer(username);
                    break;
                } else { // if account details are wrong throws error
                    throw new NoUserException("This account does not exist!");
                }
            } catch (NoUserException e) {
                System.out.println(e.getMessage());
            }
        }

        return customer;
    }

    //log in as a seller
    public static Seller sellerLogin(Scanner scanner) {
        Seller seller;
        while (true) {
            System.out.println("Please enter your username/email");
            String username = scanner.nextLine();
            System.out.println("Please enter your password");
            String password = scanner.nextLine();
            try {
                if (Seller.checkAccount(username, password)) {
                    seller = Seller.loadSeller(username);
                    break;
                } else { // if account details are wrong throws error
                    throw new NoUserException("This account does not exist!");
                }
            } catch (NoUserException e) {
                System.out.println(e.getMessage());
            }
        }

        return seller;
    }

    public static void customerMarketplace(Scanner scanner, Customer customer) {
        //checks shopping cart for updated products
        customer.checkShoppingCart();
        //display of products
        ArrayList<Seller> sellers = Seller.loadAllSellers();
        while (true) {   //loop for the main page
            int i = 1;   //index used to number products and other choices
            ArrayList<Product> allProducts = new ArrayList<>();
            ArrayList<Store> allStores = new ArrayList<>();
            for (int j = 0; j < sellers.size(); j++) {
                ArrayList<Store> stores = sellers.get(j).getStores();
                allStores.addAll(stores);
                for (int k = 0; k < stores.size(); k++) {
                    ArrayList<Product> products = stores.get(k).getProducts();
                    allProducts.addAll(products);
                    for (int l = 0; l < products.size(); l++) {
                        System.out.print(i + ". ");   //display product number
                        System.out.println(products.get(l).marketplaceDisplay());   //display product info
                        i++;
                    }
                }
            }
            System.out.println((i++) + ". Search for specific products.");
            System.out.println((i++) + ". Sort the marketplace on price.");
            System.out.println((i++) + ". Sort the marketplace on quantity available.");
            System.out.println((i++) + ". View a dashboard with store and seller information.");
            System.out.println((i++) + ". View shopping cart.");
            System.out.println((i++) + ". Modify account.");
            System.out.println((i) + ". Exit.");
            System.out.println("Please select a number to visit product's page or option you want to perform.");
            int choice;
            try {    //if input is not Integer, catch exception and repeat main page prompt
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("You didn't input an integer number.");
                continue;   //start the main page prompts again
            }
            if (choice > i || choice <= 0) {    //user chose a number not from the list
                System.out.println("Please enter an existing option.");
                continue;    //start the main page prompts again
            } else if (choice <= (i - 7)) {    //user selected a product
                Product currentProduct = allProducts.get((choice - 1));
                productPage(scanner, currentProduct, allStores, sellers, customer);

            } else {    //user selected an option below listed products
                if (choice == (i - 6)) {   //Search for specific products
                    System.out.println("Please enter a term to search for.");
                    String term = scanner.nextLine().toLowerCase();

                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        ArrayList<Product> filteredProducts = new ArrayList<>();
                        for (int j = 0; j < allProducts.size(); j++) {
                            if (allProducts.get(j).toString().toLowerCase().contains(term)) {
                                filteredProducts.add(allProducts.get(j));
                                System.out.print(i + ". ");
                                System.out.println(allProducts.get(j).marketplaceDisplay());
                                i++;
                            }
                        }
                        System.out.println(i + ". Back to main page.");
                        System.out.println("Please select a number to visit product's page.");
                        try {    //if input is not Integer, catch exception and repeat main page prompt
                            choice = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("You didn't input an integer number.");
                            continue;   //start the main page prompts again
                        }
                        if (choice > i || choice <= 0) {    //user chose a number not from the list
                            System.out.println("Please enter an existing option.");
                            continue;    //start the main page prompts again
                        } else if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = filteredProducts.get((choice - 1));
                            productPage(scanner, currentProduct, allStores, sellers, customer);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 5)) {
                    //Sort the marketplace on price:products with lower price are on the top
                    Collections.sort(allProducts, new ProductComparatorByPrice());
                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        for (int j = 0; j < allProducts.size(); j++) {
                            System.out.print(i + ". ");
                            System.out.println(allProducts.get(j).marketplaceDisplay());
                            i++;
                        }
                        System.out.println(i + ". Back to main page.");
                        System.out.println("Please select a number to visit product's page.");
                        try {    //if input is not Integer, catch exception and repeat main page prompt
                            choice = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("You didn't input an integer number.");
                            continue;   //start the main page prompts again
                        }
                        if (choice > i || choice <= 0) {    //user chose a number not from the list
                            System.out.println("Please enter an existing option.");
                            continue;    //start the main page prompts again
                        } else if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = allProducts.get((choice - 1));
                            productPage(scanner, currentProduct, allStores, sellers, customer);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 4)) {
                    //Sort the marketplace on quantity available:
                    //products with more items available are on the top
                    Collections.sort(allProducts, new ProductComparatorByAvailability());
                    Collections.reverse(allProducts);
                    boolean toMainPage = false;
                    while (!toMainPage) {
                        i = 1;
                        for (int j = 0; j < allProducts.size(); j++) {
                            System.out.print(i + ". ");
                            System.out.println(allProducts.get(j).marketplaceDisplay());
                            i++;
                        }
                        System.out.println(i + ". Back to main page.");
                        System.out.println("Please select a number to visit product's page.");
                        try {    //if input is not Integer, catch exception and repeat main page prompt
                            choice = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("You didn't input an integer number.");
                            continue;   //start the main page prompts again
                        }
                        if (choice > i || choice <= 0) {    //user chose a number not from the list
                            System.out.println("Please enter an existing option.");
                            continue;    //start the main page prompts again
                        } else if (choice == i) {   //user selected to go back to main page
                            toMainPage = true;
                        } else {    //user selected a product
                            Product currentProduct = allProducts.get((choice - 1));
                            productPage(scanner, currentProduct, allStores, sellers, customer);
                            toMainPage = true;
                        }
                    }
                } else if (choice == (i - 3)) {
                    //TODO:View a dashboard with store and seller information.
                } else if (choice == (i - 2)) {
                    do {
                        ArrayList<ShoppingCart> shoppingCart = customer.getShoppingCart();
                        if (shoppingCart.isEmpty()) {
                            System.out.println("Shopping cart is empty!");
                            break;
                        }
                        System.out.println("Products in your shopping cart:");
                        for (int a = 0; a < shoppingCart.size(); a++) {
                            System.out.printf("%d. Product: %s. Amount: %d.\n",
                                    a + 1, shoppingCart.get(a).getProduct().getProductName(), shoppingCart.get(a).getAmount());
                        }

                        String input;
                        do {
                            System.out.println("1. Purchase all products.");
                            System.out.println("2. Delete product from shopping cart.");
                            System.out.println("3. Leave shopping cart.");
                            input = scanner.nextLine();
                            if (!(input.equals("1") || input.equals("2") || input.equals("3")))
                                System.out.println("Please enter a number corresponding to an option.");
                        } while (!(input.equals("1") || input.equals("2") || input.equals("3")));

                        if (input.equals("1")) { // this seems bad but idk of a better way
                            for (int b = 0; b < shoppingCart.size(); b++) { // loops through all products, if one matches name purchases products
                                for (int e = 0; e < sellers.size(); e++) {
                                    ArrayList<Store> stores = sellers.get(e).getStores();
                                    for (int f = 0; f < stores.size(); f++) {
                                        ArrayList<Product> products = stores.get(f).getProducts();
                                        for (int k = 0; k < products.size(); k++) {
                                            if (products.get(k).getProductName().equals(shoppingCart.get(b).getProduct().getProductName())) {
                                                if (stores.get(f).purchaseProductFromStore(products.get(k), shoppingCart.get(b).getAmount(), customer)) {
                                                    System.out.printf("Purchased %s successfully!\n", products.get(k).getProductName());
                                                    for (int o = 0; o < sellers.size(); o++) {
                                                        if (sellers.get(o).getUsername().equals(stores.get(f).getSeller())) {
                                                            sellers.get(o).saveSeller();
                                                        }
                                                    }
                                                }
                                            } else {
                                                System.out.printf("Sorry, we don't have enough items of %s available.\n", products.get(k).getProductName());
                                            }
                                        }
                                    }
                                }
                            }

                            System.out.println("Purchased all available products, leaving shopping cart!");
                            break;
                        } else if (input.equals("2")) { // deletes product
                            do {
                                System.out.println("Which product would you like to delete?");
                                input = scanner.nextLine();
                                try {
                                    int intInput = Integer.parseInt(input);
                                    if (intInput > 0 && intInput <= shoppingCart.size()) {
                                        shoppingCart.remove(intInput - 1);
                                        System.out.println("Product removed from shopping cart!");
                                    } else {
                                        System.out.println("Please enter an option corresponding to a product.");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Please enter an option corresponding to a product.");
                                }
                            } while (true);
                        } else if (input.equals("3")) { // leaves shopping cart
                            System.out.println("Leaving shopping cart!");
                            break;
                        }
                    } while (true);

                } else if (choice == (i - 1)) {    //Modify account
                    String input;
                    do {
                        System.out.println("1. Edit username.");
                        System.out.println("2. Edit password.");
                        System.out.println("3. Delete account.");
                        input = scanner.nextLine();
                        if (!(input.equals("1") || input.equals("2") || input.equals("3")))
                            System.out.println("Please enter a number corresponding to an option.");
                    } while (!(input.equals("1") || input.equals("2") || input.equals("3")));

                    if (input.equals("1")) {
                        System.out.println("What is your new username?");
                        input = scanner.nextLine();
                        customer.setUsername(input);
                    } else if (input.equals("2")) {
                        System.out.println("What is your new password?");
                        input = scanner.nextLine();
                        customer.setPassword(input);
                    } else {
                        System.out.println("Are you sure you want to delete your account?");
                        System.out.println("1. Yes\n2. No");
                        input = scanner.nextLine();
                        if (input.equals("1")) {
                            customer.deleteAccount();
                            System.out.println("Account has been deleted.");
                            return;
                        } else {
                            System.out.println("You chose not to delete this account! Returning to main page...");
                            continue;
                        }
                    }
                    System.out.println("Success! Returning to main page...");
                } else if (choice == 1) return;
            }
        }
    }

    public static void productPage(Scanner scanner, Product currentProduct, ArrayList<Store> stores,
                                   ArrayList<Seller> sellers, Customer customer) {
        //this is a separated method used to display product's page and realize further operations
        //it ends and returns void only when user selects "back to main page"
        Store currentStore = null;
        for (int j = 0; j < stores.size(); j++) {
            if (stores.get(j).getStoreName().equals(currentProduct.getStoreName())) {
                currentStore = stores.get(j);
            }
        }
        while (true) {
            System.out.println(currentProduct.productPageDisplay());
            System.out.println("1. Purchase this product.");
            System.out.println("2. Add this product to shopping cart.");
            System.out.println("3. Back to main page.");
            String choiceOnProductPage = scanner.nextLine();
            switch (choiceOnProductPage) {
                case "1":   //purchase this product
                    int amount;
                    while (true) {
                        System.out.println("What amount would you like to purchase?");
                        try {
                            amount = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Please input valid number.");
                            continue;
                        }
                        if (amount < 0) {
                            System.out.println("Please input valid number.");
                            continue;
                        }
                        if (currentStore.purchaseProductFromStore(currentProduct, amount, customer)) {
                            System.out.println("Purchased successfully!");
                            System.out.println("Returning to product's page...\n");
                            for (int i = 0; i < sellers.size(); i++) {
                                if (sellers.get(i).getUsername().equals(currentStore.getSeller())) {
                                    sellers.get(i).saveSeller();
                                }
                            }
                            break;
                        } else {
                            System.out.println("Sorry, we don't have enough items available.");
                            System.out.println("Returning to product's page...\n");
                            break;
                        }
                    }
                    break;
                case "2":    // Add this product to shopping cart
                    int amount1;
                    while (true) {
                        System.out.println("What amount would you like to put into shopping cart?");
                        try {
                            amount1 = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Please input valid number.");
                            continue;
                        }
                        if (amount1 < 0) {
                            System.out.println("Please input valid number.");
                            continue;
                        }
                        if (amount1 <= currentProduct.getAvailableQuantity()) {
                            for (int i = 0; i < sellers.size(); i++) {
                                if (sellers.get(i).getUsername().equals(currentStore.getSeller())) {
                                    customer.addShoppingCart(currentProduct, sellers.get(i), amount1);
                                    System.out.println("Successfully added to shopping cart!");
                                    System.out.println("Returning to product's page...\n");
                                }
                            }
                            break;
                        } else {
                            System.out.println("Sorry, we don't have enough items available.");
                            System.out.println("Returning to product's page...\n");
                            break;
                        }
                    }
                    break;
                case "3":
                    return;   //returns true indicating user wants to return to main page
                default:
                    System.out.println("Please enter an existing option.");
            }
        }
    }

    public static void sellerMarketplace(Scanner scanner, Seller seller) {
        do {
            int choice = 0;
            System.out.println("What would you like to do?");
            do {
                System.out.println("1. Modify products.");
                System.out.println("2. View a list of sales by store.");
                System.out.println("3. View a dashboard with statistics for each stores.");
                System.out.println("4. View number of products in shopping carts.");
                System.out.println("5. Modify Account.");
                System.out.println("6. Exit.");
                try {    //if input is not Integer, catch exception and repeat main page prompt
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice < 1 || choice > 6)
                        System.out.println("Please enter an available option.");
                } catch (NumberFormatException e) {
                    System.out.println("Please enter an available option.");
                }
            } while (!(choice >= 1 && choice <= 6));

            if (choice == 1) { // Modify products
                boolean valid;
                int storeNum = 0;
                System.out.println("Which store would you like to edit?");
                do {
                    valid = true;
                    for (int i = 0; i < seller.getStores().size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, seller.getStores().get(i).getStoreName());
                    }
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
                } while (!valid);
                Store currentStore = seller.getStore((storeNum - 1));
                System.out.println("What would you like to do?");
                System.out.println("1. Add a product");
                System.out.println("2. Edit a product");
                System.out.println("3. Delete a product");
                int modifyOption = 0;
                do {
                    valid = true;
                    try {
                        modifyOption = Integer.parseInt(scanner.nextLine());
                        if (modifyOption < 1 || modifyOption > 3) {
                            System.out.println("Please enter an available option.");
                            valid = false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter an available option.");
                        valid = false;
                    }
                } while (!valid);
                if (modifyOption == 1) {    //add a product
                    //TODO:"Sellers can import or export products for their stores using a csv file"?
                    String input;
                    do {
                        System.out.println("1. Import product from csv.\n2. Create product in terminal.");
                        input = scanner.nextLine();
                        if (!(input.equals("1") || input.equals("2")))
                            System.out.println("Please enter an option corresponding to a product.");
                    } while (!(input.equals("1") || input.equals("2")));

                    if (input.equals("1")) {
                        System.out.println("Please enter the file path to the csv file.");
                        String file = scanner.nextLine();
                    } else if (input.equals("2")) {
                        System.out.println("Please enter a product name:");
                        String name = scanner.nextLine();
                        System.out.println("Please enter a product description:");
                        String description = scanner.nextLine();
                        System.out.println("Please enter an available quantity.");
                    }
//                productName;description;int availableQuantity;double price;String storeName;
                } else if (modifyOption == 2) {    //edit a product
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
                } else {    //modify option = 3;delete a product
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
                }

                System.out.println("Returning to main menu.");
            } else if (choice == 2) {
                //TODO:View a list of sales by store
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
                for (int i = 0; i < storelist.length; i++) { //Fills the strings arrays from respective Arraylists and then prints information for each customer line by line
                    revenue = storelist[i].getRevenue();
                    customers = storelist[i].getCustList();
                    amount = storelist[i].getPurchased();
                    String[] revlist = new String[revenue.size()];
                    String[] custlist = new String[customers.size()];
                    String[] purchased = new String[amount.size()];
                    System.out.println(storename[i]);
                    for (int j = 0; j < revlist.length; j++) { // I'm not sure why I made the Double and Integer arrays into Strings but I did so
                        revlist[j] = (revenue.get(j)).toString();
                        custlist[j] = (customers.get(j));
                        purchased[j] = (amount.get(j)).toString();
                        System.out.printf("Customer %s purchased %s produces for a total sale of $%s", custlist[j], purchased[j], revlist[j]);
                    }
                }

                System.out.println("Returning to main menu.");
            } else if (choice == 3) {    //choice = 3, statistics
                //TODO:View a dashboard with statistics for each stores
            } else if (choice == 4) { // view shopping carts
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
            } else if (choice == 5) { // modify account
                String input;
                do {
                    System.out.println("1. Edit username.");
                    System.out.println("2. Edit password.");
                    System.out.println("3. Delete account.");
                    input = scanner.nextLine();
                    if (!(input.equals("1") || input.equals("2") || input.equals("3")))
                        System.out.println("Please enter a number corresponding to an option.");
                } while (!(input.equals("1") || input.equals("2") || input.equals("3")));

                if (input.equals("1")) {
                    System.out.println("What is your new username?");
                    input = scanner.nextLine();
                    seller.setUsername(input);
                } else if (input.equals("2")) {
                    System.out.println("What is your new password?");
                    input = scanner.nextLine();
                    seller.setPassword(input);
                } else if (input.equals("3")) {
                    System.out.println("Are you sure you want to delete your account?");
                    System.out.println("1. Yes\n2. No");
                    input = scanner.nextLine();
                    if (input.equals("1")) {
                        seller.deleteAccount();
                        System.out.println("Account has been deleted.");
                        return;
                    } else {
                        System.out.println("You chose not to delete this account! Returning to main menu...");
                        continue;
                    }
                }
                System.out.println("Success! Returning to main menu.");
            } else if (choice == 6) return;
        } while (true);
    }
}

