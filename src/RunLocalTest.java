/**
import org.junit.Test;

import org.junit.After;
import org.junit.Before;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.*;

import static org.junit.Assert.*;

/**
 * A framework to run public test cases.
 *
 * <p>Purdue University -- CS18000 -- Fall 2022</p>
 *
 * @author Purdue CS
 * @version August 22, 2022

public class RunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        System.out.printf("Test Count: %d.\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Excellent - all local tests ran successfully.");
        } else {
            System.out.printf("Tests failed: %d.\n", result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.getMessage());
                System.out.println(failure.getTestHeader());
                System.out.println(failure.getDescription());
                System.out.println(failure);
            }
        }
    }

    /**
     * A set of public test cases.
     *
     * <p>Purdue University -- CS18000 -- Fall 2022</p>
     *
     * @author Purdue CS
     * @version August 22, 2022

    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        // Each of the correct outputs
        public static final String WELCOME = "Welcome to the marketplace!\nPlease select your account type";
        public static final String SELLERCUSTOMER = "1. Seller\n2. Customer";
        public static final String SELLERCUSTOMERINVALID = "Please enter 1 for seller or 2 for customer";
        public static final String LOGINCREATEACCOUNT = "1. Login\n2. Create an account";
        public static final String LOGINCREATEACCOUNTINVALID = "Please enter 1 to login or 2 to create an account";
        public static final String ENTERUSERNAME = "Please enter a username/email";
        public static final String ENTERPASSWORD = "Please enter a password";
        public static final String USERCREATED = "User successfully created!";
        public static final String PLEASELOGIN = "Please login";
        public static final String USERNAMELOGIN = "Please enter your username/email";
        public static final String PASSWORDLOGIN = "Please enter your password";
        public static final String LOGINERROR = "This account does not exist!";
        public static final String LOGINSUCCESS = "Successfully logged in!";
        public static final String SELLERQUESTION = "What would you like to do?";
        public static final String SELLERDASHBOARD = "1. Modify products.\n" +
                "2. View a list of sales by store.\n" +
                "3. View a dashboard with statistics for each stores.\n" +
                "4. View number of products in shopping carts.\n" +
                "5. Modify Account.\n" +
                "6. Create a store.\n" +
                "7. Exit.";
        public static final String RETURNMAINMENU = "Returning to main menu.";
        public static final String GOODBYE = "Goodbye!";
        public static final String STORENAME = "Please enter a store name:";
        public static final String STOREMENU = "What would you like to do?\n" +
                "1. Add a product\n" +
                "2. Edit a product\n" +
                "3. Delete a product\n" +
                "4. Export products";
        public static final String STOREEDIT = "Which store would you like to edit?";
        public static final String ADDPRODUCT = "1. Import product from csv.\n" +
                "2. Create product in terminal.";
        public static final String PRODUCTNAME = "Please enter a product name:";
        public static final String PRODUCTDESC = "Please enter a product description:";
        public static final String PRODUCTQUANTITY = "Please enter an available quantity.";
        public static final String PRODUCTPRICE = "Please enter a price for the product.";
        public static final String PRODUCTADD = "Product added!";
        public static final String PRODUCTIMPORT = "Please enter the file path to the csv file.";

        /**
         * Test cases modified for this program.
         *
         * <p>Purdue University -- CS18000 -- Fall 2022</p>
         *
         * @author Purdue CS, Dimitri Paikos, Lab12
         * @version 4/10/2023

        @Test(timeout = 1000)
        public void testExpectedOne() { // makes a seller, creates one product, imports another
            // Set the input
            String input = "1" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "seller1" + System.lineSeparator() +
                    "pass" + System.lineSeparator() +
                    "seller1" + System.lineSeparator() +
                    "pass" + System.lineSeparator() +
                    "6" + System.lineSeparator() +
                    "store1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "prod1" + System.lineSeparator() +
                    "desc" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "testImport.csv" + System.lineSeparator() +
                    "7" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = WELCOME + System.lineSeparator() +
                    SELLERCUSTOMER + System.lineSeparator() +
                    LOGINCREATEACCOUNT + System.lineSeparator() +
                    ENTERUSERNAME + System.lineSeparator() +
                    ENTERPASSWORD + System.lineSeparator() +
                    USERCREATED + System.lineSeparator() +
                    PLEASELOGIN + System.lineSeparator() +
                    USERNAMELOGIN + System.lineSeparator() +
                    PASSWORDLOGIN + System.lineSeparator() +
                    LOGINSUCCESS + System.lineSeparator() +
                    SELLERQUESTION + System.lineSeparator() +
                    SELLERDASHBOARD + System.lineSeparator() +
                    STORENAME + System.lineSeparator() +
                    SELLERQUESTION + System.lineSeparator() +
                    SELLERDASHBOARD + System.lineSeparator() +
                    STOREEDIT + System.lineSeparator() +
                    "1. store1" + System.lineSeparator() +
                    STOREMENU + System.lineSeparator() +
                    ADDPRODUCT + System.lineSeparator() +
                    PRODUCTNAME + System.lineSeparator() +
                    PRODUCTDESC + System.lineSeparator() +
                    PRODUCTQUANTITY + System.lineSeparator() +
                    PRODUCTPRICE + System.lineSeparator() +
                    PRODUCTADD + System.lineSeparator() +
                    RETURNMAINMENU + System.lineSeparator() +
                    SELLERQUESTION + System.lineSeparator() +
                    SELLERDASHBOARD + System.lineSeparator() +
                    STOREEDIT + System.lineSeparator() +
                    "1. store1" + System.lineSeparator() +
                    STOREMENU + System.lineSeparator() +
                    ADDPRODUCT + System.lineSeparator() +
                    PRODUCTIMPORT + System.lineSeparator() +
                    PRODUCTADD + System.lineSeparator() +
                    RETURNMAINMENU + System.lineSeparator() +
                    SELLERQUESTION + System.lineSeparator() +
                    SELLERDASHBOARD + System.lineSeparator() +
                    GOODBYE + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Market.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n", "\n");
            output = output.replaceAll("\r\n", "\n");
            assertEquals("Error in expected output!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        public void testExpectedTwo() { // customer account and does various stuff
            // Set the input
            String input = "2" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "cust1" + System.lineSeparator() +
                    "pass" + System.lineSeparator() +
                    "cuust1" + System.lineSeparator() +
                    "pass" + System.lineSeparator() +
                    "cust1" + System.lineSeparator() +
                    "pass" + System.lineSeparator() +
                    "6" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "3" + System.lineSeparator() +
                    "12" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "11" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "3" + System.lineSeparator() +
                    "14" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "export.csv" + System.lineSeparator() +
                    "15" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = "Welcome to the marketplace!\n" +
                    "Please select your account type\n" +
                    "1. Seller\n" +
                    "2. Customer\n" +
                    "1. Login\n" +
                    "2. Create an account\n" +
                    "Please enter a username/email\n" +
                    "Please enter a password\n" +
                    "User successfully created!\n" +
                    "Please login\n" +
                    "Please enter your username/email\n" +
                    "Please enter your password\n" +
                    "This account does not exist!\n" +
                    "Please enter your username/email\n" +
                    "Please enter your password\n" +
                    "Successfully logged in!\n" +
                    "1. Product name: p1, price: 5.00, available in store: s1\n" +
                    "2. Product name: p2, price: 5.50, available in store: s1\n" +
                    "3. Product name: p3, price: 5.00, available in store: s1\n" +
                    "4. Product name: new product, price: 5.00, available in store: s2\n" +
                    "5. Product name: new product2, price: 10.00, available in store: s3\n" +
                    "6. Product name: prod1, price: 1.00, available in store: store1\n" +
                    "7. Product name: prod2, price: 1.00, available in store: store1\n" +
                    "8. Search for specific products.\n" +
                    "9. Sort the marketplace on price.\n" +
                    "10. Sort the marketplace on quantity available.\n" +
                    "11. View a dashboard with store and seller information.\n" +
                    "12. View shopping cart.\n" +
                    "13. Modify account.\n" +
                    "14. View purchase history.\n" +
                    "15. Exit.\n" +
                    "Please select a number to visit product's page or option you want to perform.\n" +
                    "Product name: prod1\n" +
                    "Description:desc\n" +
                    "Quantity Available:1\n" +
                    "Price: 1.00\n" +
                    "Available in store: store1\n" +
                    "\n" +
                    "1. Purchase this product.\n" +
                    "2. Add this product to shopping cart.\n" +
                    "3. Back to main page.\n" +
                    "What amount would you like to purchase?\n" +
                    "Sorry, we don't have enough items available.\n" +
                    "Returning to product's page...\n" +
                    "\nProduct name: prod1\n" +
                    "Description:desc\n" +
                    "Quantity Available:1\n" +
                    "Price: 1.00\n" +
                    "Available in store: store1\n" +
                    "\n1. Purchase this product.\n" +
                    "2. Add this product to shopping cart.\n" +
                    "3. Back to main page.\n" +
                    "What amount would you like to put into shopping cart?\n" +
                    "Successfully added to shopping cart!\n" +
                    "Returning to product's page...\n" +
                    "\nProduct name: prod1\n" +
                    "Description:desc\n" +
                    "Quantity Available:1\n" +
                    "Price: 1.00\n" +
                    "Available in store: store1\n" +
                    "\n1. Purchase this product.\n" +
                    "2. Add this product to shopping cart.\n" +
                    "3. Back to main page.\n" +
                    "1. Product name: p1, price: 5.00, available in store: s1\n" +
                    "2. Product name: p2, price: 5.50, available in store: s1\n" +
                    "3. Product name: p3, price: 5.00, available in store: s1\n" +
                    "4. Product name: new product, price: 5.00, available in store: s2\n" +
                    "5. Product name: new product2, price: 10.00, available in store: s3\n" +
                    "6. Product name: prod1, price: 1.00, available in store: store1\n" +
                    "7. Product name: prod2, price: 1.00, available in store: store1\n" +
                    "8. Search for specific products.\n" +
                    "9. Sort the marketplace on price.\n" +
                    "10. Sort the marketplace on quantity available.\n" +
                    "11. View a dashboard with store and seller information.\n" +
                    "12. View shopping cart.\n" +
                    "13. Modify account.\n" +
                    "14. View purchase history.\n" +
                    "15. Exit.\n" +
                    "Please select a number to visit product's page or option you want to perform.\n" +
                    "Products in your shopping cart:\n" +
                    "1. Product: prod1. " +
                    "Amount: 1.\n" +
                    "\n1. Purchase all products.\n" +
                    "2. Delete product from shopping cart.\n" +
                    "3. Leave shopping cart.\n" +
                    "Purchased prod1 successfully!\n" +
                    "Purchased all available products, leaving shopping cart!\n" +
                    "1. Product name: p1, price: 5.00, available in store: s1\n" +
                    "2. Product name: p2, price: 5.50, available in store: s1\n" +
                    "3. Product name: p3, price: 5.00, available in store: s1\n" +
                    "4. Product name: new product, price: 5.00, available in store: s2\n" +
                    "5. Product name: new product2, price: 10.00, available in store: s3\n" +
                    "6. Product name: prod1, price: 1.00, available in store: store1\n" +
                    "7. Product name: prod2, price: 1.00, available in store: store1\n" +
                    "8. Search for specific products.\n" +
                    "9. Sort the marketplace on price.\n" +
                    "10. Sort the marketplace on quantity available.\n" +
                    "11. View a dashboard with store and seller information.\n" +
                    "12. View shopping cart.\n" +
                    "13. Modify account.\n" +
                    "14. View purchase history.\n" +
                    "15. Exit.\n" +
                    "Please select a number to visit product's page or option you want to perform.\n" +
                    "1. View a list of stores by number of products sold.\n" +
                    "2. View a list of stores by the products purchased by you.\n" +
                    "1. Store name: s1, products sold: 4\n" +
                    "2. Store name: s2, products sold: 1\n" +
                    "3. Store name: s3, products sold: 10\n" +
                    "4. Store name: store1, products sold: 1\n" +
                    "\n1. Sort by low - high.\n" +
                    "2. Sort by high - low.\n" +
                    "3. Back to main page.\n" +
                    "1. Product name: p1, price: 5.00, available in store: s1\n" +
                    "2. Product name: p2, price: 5.50, available in store: s1\n" +
                    "3. Product name: p3, price: 5.00, available in store: s1\n" +
                    "4. Product name: new product, price: 5.00, available in store: s2\n" +
                    "5. Product name: new product2, price: 10.00, available in store: s3\n" +
                    "6. Product name: prod1, price: 1.00, available in store: store1\n" +
                    "7. Product name: prod2, price: 1.00, available in store: store1\n" +
                    "8. Search for specific products.\n" +
                    "9. Sort the marketplace on price.\n" +
                    "10. Sort the marketplace on quantity available.\n" +
                    "11. View a dashboard with store and seller information.\n" +
                    "12. View shopping cart.\n" +
                    "13. Modify account.\n" +
                    "14. View purchase history.\n" +
                    "15. Exit.\n" +
                    "Please select a number to visit product's page or option you want to perform.\n" +
                    "Purchase history: (Newest products purchased listed first)\n" +
                    "Product: prod1. Amount purchased: 1. Store: store1\n" +
                    "\n1. Export purchase history.\n" +
                    "2. Back to main page.\n" +
                    "Please enter the file path to export to.\n" +
                    "Purchase history exported!\n" +
                    "1. Product name: p1, price: 5.00, available in store: s1\n" +
                    "2. Product name: p2, price: 5.50, available in store: s1\n" +
                    "3. Product name: p3, price: 5.00, available in store: s1\n" +
                    "4. Product name: new product, price: 5.00, available in store: s2\n" +
                    "5. Product name: new product2, price: 10.00, available in store: s3\n" +
                    "6. Product name: prod1, price: 1.00, available in store: store1\n" +
                    "7. Product name: prod2, price: 1.00, available in store: store1\n" +
                    "8. Search for specific products.\n" +
                    "9. Sort the marketplace on price.\n" +
                    "10. Sort the marketplace on quantity available.\n" +
                    "11. View a dashboard with store and seller information.\n" +
                    "12. View shopping cart.\n" +
                    "13. Modify account.\n" +
                    "14. View purchase history.\n" +
                    "15. Exit.\n" +
                    "Please select a number to visit product's page or option you want to perform.\n" +
                    "Goodbye!\n";

            // Runs the program with the input values
            receiveInput(input);
            Market.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n", "\n");
            output = output.replaceAll("\r\n", "\n");
            assertEquals("Error in expected output!",
                    expected.trim(), output.trim());
        }
    }
}
*/