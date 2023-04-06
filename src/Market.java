import java.util.Scanner;
public class Market {
    public static void main(String[] args) {
        // logging in/creating account
        Scanner scanner = new Scanner(System.in);
        String userType;
        String accountType; // create an account or login
        String username;
        String password;
        Customer customer = null; // customer account if they select customer
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
            if (userType.equals("2")) {
                try {
                    Customer newCustomer = new Customer(username, password, true);
                } catch (NoUserException e) {
                    e.printStackTrace();
                } catch (AlreadyUserException e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("Please login"); // logging in now
                while (true) {
                    System.out.println("Please enter your username/email");
                    username = scanner.nextLine();
                    System.out.println("Please enter your password");
                    password = scanner.nextLine();
                    try {
                        customer = new Customer(username, password, false);
                        break;
                    } catch (NoUserException e) {
                        System.out.println(e.getMessage());
                    } catch (AlreadyUserException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else if (userType.equals("1")) {
                // Do something to setup seller account
            }
        } else if (accountType.equals("1")) { // if they want to login
            if (userType.equals("2")) {
                while (true) {
                    System.out.println("Please enter your username/email");
                    username = scanner.nextLine();
                    System.out.println("Please enter your password");
                    password = scanner.nextLine();
                    try {
                        customer = new Customer(username, password, false);
                        break;
                    } catch (NoUserException e) {
                        System.out.println(e.getMessage());
                    } catch (AlreadyUserException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else if (userType.equals("1")) {
                // Do something to login seller account
            }
        }

        // main marketplace
        if (customer == null) {
            // marketplace for customer
        } else {
            // marketplace for seller
        }
    }
}
