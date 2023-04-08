import java.util.ArrayList;
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
            for (int j = 0; j < sellers.size(); j++) {
                ArrayList<Store> stores = sellers.get(j).getStores();
                for (int k = 0; k < stores.size(); k++) {
                    ArrayList<Product> products = stores.get(k).getProducts();
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
            System.out.println((i) + ". View a dashboard with store and seller information.");
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
            } else if (choice <= (i - 4)) {    //user selected a product
                boolean validOption;
                do {
                    validOption = true;
                    int sellerIndex = 0;
                    int storeIndex = 0;
                    int productIndex = 0;
                    Store currentStore = null;
                    i = 1;   //set index of products back to 1, loop and find when i==choice
                    for (int j = 0; j < sellers.size(); j++) {
                        ArrayList<Store> stores = sellers.get(j).getStores();
                        for (int k = 0; k < stores.size(); k++) {
                            ArrayList<Product> products = stores.get(k).getProducts();
                            for (int l = 0; l < products.size(); l++) {
                                if (i == choice) {
                                    currentStore = stores.get(k);
                                    sellerIndex = j;
                                    storeIndex = k;
                                    productIndex = l;
                                }
                                i++;
                            }
                        }
                    }
                    System.out.println(currentStore.getProducts().get(productIndex).productPageDisplay());
                    System.out.println("1. Purchase this product.");
                    System.out.println("2. Add this product to shopping cart.");
                    System.out.println("3. Back to main page.");
                    String choiceOnProductPage = scanner.nextLine();
                    switch (choiceOnProductPage) {
                        case "1":
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
                                if (currentStore.purchaseProductFromStore(productIndex, amount)) {
                                    System.out.println("Purchased successfully!");
                                    System.out.println("Returning to product's page...\n");
                                    break;
                                } else {
                                    System.out.println("Sorry, we don't have enough items available.");
                                    System.out.println("Returning to product's page...\n");
                                    break;
                                }
                            }
                            break;
                        case "2":
                            //TODO:add to cart
                            break;
                        case "3":
                            break;  //returns to main page again
                        default:
                            System.out.println("Please enter an existing option.");
                            validOption = false;  //prompts again for a valid option
                    }
                } while (!validOption);
            } else {    //user selected an option below listed products
                if (choice == (i - 3)) {
                    //TODO:search for a product
                } else if (choice == (i - 2)) {
                    //TODO:Sort the marketplace on price
                } else if (choice == (i - 1)) {
                    //TODO:Sort the marketplace on quantity available.
                } else if (choice == i) {
                    //TODO:View a dashboard with store and seller information.
                }
            }
        }
    }

    public static void sellerMarketplace(Scanner scanner, Seller seller) {
        int choice = 3;
        System.out.println("What would you like to do?");
        do {
            System.out.println("1. Modify products");
            System.out.println("2. View Stores");
            try {    //if input is not String, catch exception and repeat main page prompt
                choice = Integer.parseInt(scanner.nextLine());
                if (!(choice == 1 || choice == 2))
                    System.out.println("Please enter 1 or 2");
            } catch (NumberFormatException e) {
                System.out.println("Please enter 1 or 2");
            }
        } while (!(choice == 1 || choice == 2));

        if (choice == 1) { // Modify products
            boolean valid = true;
            int num = 0;
            System.out.println("Which store would you like to edit?");
            do {
                for (int i = 0; i < seller.getStores().size(); i++) {
                    System.out.printf("%d. %s", i + 1, seller.getStores().get(i));
                }
                try {
                    num = Integer.parseInt(scanner.nextLine());
                    if (!(num >= 1 && num <= seller.getStores().size())) {
                        System.out.println("Please enter a number corresponding to a store.");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number corresponding to a store.");
                    valid = false;
                }
            } while (!valid);
            num -= 1;
            System.out.println("What would you like to do?");
            System.out.println("1. Add a product");
            System.out.println("2. Edit a product");
            System.out.println("3. Delete a product");
//            try {
//                action = Integer.parseInt(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("You didn't input an integer number.");
//                valid2 = false;
//            }
        }
    }
}

