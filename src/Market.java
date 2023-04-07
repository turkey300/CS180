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
                } catch (NoUserException e) {
                    e.printStackTrace();
                } catch (AlreadyUserException e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("Please login"); // logging in now
//                while (true) {
//                    System.out.println("Please enter your username/email");
//                    username = scanner.nextLine();
//                    System.out.println("Please enter your password");
//                    password = scanner.nextLine();
//                    try {
//                        customer = new Customer(username, password, false);
//                        break;
//                    } catch (NoUserException e) {
//                        System.out.println(e.getMessage());
//                    } catch (AlreadyUserException e) {
//                        System.out.println(e.getMessage());
//                    }
//                }
                customer = customerLogin(scanner);
            } else if (userType.equals("1")) {    //creating Seller account
                try {
                    Seller newSeller = new Seller(username, password, true);
                } catch (NoUserException e) {
                    e.printStackTrace();
                } catch (AlreadyUserException e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("Please login"); // logging in now
//                while (true) {
//                    System.out.println("Please enter your username/email");
//                    username = scanner.nextLine();
//                    System.out.println("Please enter your password");
//                    password = scanner.nextLine();
//                    try {
//                        seller = new Seller(username, password, false);
//                        break;
//                    } catch (NoUserException e) {
//                        System.out.println(e.getMessage());
//                    } catch (AlreadyUserException e) {
//                        System.out.println(e.getMessage());
//                    }
//                }
                seller = sellerLogin(scanner);
            }
        } else if (accountType.equals("1")) { // if they want to login
            if (userType.equals("2")) {    //log in as a customer
//                    while (true) {
//                        System.out.println("Please enter your username/email");
//                        username = scanner.nextLine();
//                        System.out.println("Please enter your password");
//                        password = scanner.nextLine();
//                        try {
//                            customer = new Customer(username, password, false);
//                            break;
//                        } catch (NoUserException e) {
//                            System.out.println(e.getMessage());
//                        } catch (AlreadyUserException e) {
//                            System.out.println(e.getMessage());
//                        }
//                    }
                customer = customerLogin(scanner);
            } else if (userType.equals("1")) {    //log in as a seller
//                    while (true) {
//                        System.out.println("Please enter your username/email");
//                        username = scanner.nextLine();
//                        System.out.println("Please enter your password");
//                        password = scanner.nextLine();
//                        try {
//                            seller = new Seller(username, password, false);
//                            break;
//                        } catch (NoUserException e) {
//                            System.out.println(e.getMessage());
//                        } catch (AlreadyUserException e) {
//                            System.out.println(e.getMessage());
//                        }
//                    }
                seller = sellerLogin(scanner);
            }
        }

        System.out.println("Successfully logged in!");
        // main marketplace
        if (customer != null) {
            // marketplace for customer
            customerMarketplace(scanner);
        } else {
            // marketplace for seller
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
                customer = new Customer(username, password, false);
                break;
            } catch (NoUserException e) {
                System.out.println(e.getMessage());
            } catch (AlreadyUserException e) {
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
                seller = new Seller(username, password, false);
                break;
            } catch (NoUserException e) {
                System.out.println(e.getMessage());
            } catch (AlreadyUserException e) {
                System.out.println(e.getMessage());
            }
        }
        return seller;
    }

    public static void customerMarketplace(Scanner scanner) {
        ArrayList<Product> products = Product.loadAllProducts();
        while (true) {   //loop for the main page
            int i = 1;   //index used to number products and other choices
            while (i <= products.size()) {
                System.out.print(i + ". ");   //display product number
                System.out.println(products.get(i - 1).marketplaceDisplay());   //display product info
                i++;
            }
            System.out.println((i++) + ". Search for specific products.");
            System.out.println((i++) + ". Sort the marketplace on price.");
            System.out.println((i++) + ". Sort the marketplace on quantity available.");
            System.out.println((i) + ". View a dashboard with store and seller information.");
            System.out.println("Please select a number to visit product's page or option you want to perform.");
            int choice;
            try {    //if input is not String, catch exception and repeat main page prompt
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("You didn't input an integer number.");
                continue;   //start the main page prompts again
            }
            if (choice > i || choice <= 0) {    //user chose a number not from the list
                System.out.println("Please enter an existing option.");
                continue;    //start the main page prompts again
            } else if (choice <= products.size()) {    //user selected a product
                boolean validOption;
                do {
                    validOption = true;
                    Product currentProduct = products.get(choice);
                    System.out.println(currentProduct.productPageDisplay());
                    System.out.println("1. Purchase this product.");
                    System.out.println("2. Add this product to shopping cart.");
                    System.out.println("3. Back to main page.");
                    String choiceOnProductPage = scanner.nextLine();
                    switch (choiceOnProductPage) {
                        case "1":
                            //TODO:purchase product
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
    public static void sellerMarketplace(TODO Scanner scan) {
        ArrayList<Store> sellstore = new ArrayList<>();
        sellstore = seller.loadStores();
        String[] storeName = new String[sellstore.size()];
        for (int n = 0; n < sellname.length; n++){
            storenName[n] = sellstore.get(n);
        }
        System.out.println("What would you like to do?");
        System.out.println("1. Modify products");
        System.out.println("2. View Stores");
        int choice;
        try {    //if input is not String, catch exception and repeat main page prompt
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
               System.out.println("You didn't input an integer number.");
               continue;   //start the main page prompts again
        }
        if (choice != 1 && choice != 2) {
            System.out.println("Please enter a valid integer");
            continue;
        }
        else if (choice == 1) { // Modify products
            boolean valid = true;
            do {
                System.out.println("Which store would you like to edit?");
                for (int i = 0; i < storeName.length; i++){
                     System.out.printf("%d. %s", i, storeName[i]);
                }
                int num;
                try {
                     num = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("You didn't input an integer number.");
                    valid = false;
                } 
            } while (!valid)
            System.out.println("What would you like to do?"):
            System.out.println("1. Add a product");
            System.out.println("2. Edit a product");
            System.out.println("3. Delete a product");
            try {
                action = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("You didn't input an integer number.");
                valid2 = false;
            }
                
                    
        }
        else { // View Store and stats
    }
}

