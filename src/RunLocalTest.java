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
 */
public class RunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        System.out.printf("Test Count: %d.\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Excellent - all local tests ran successfully.");
        } else {
            System.out.printf("Tests failed: %d.\n",result.getFailureCount());
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
     */
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
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Error in expected output!",
                    expected.trim(), output.trim());
        }

//        @Test(timeout = 1000)
//        public void testExpectedTwo() {
//            // Set the input
//            String input = "1,1" + System.lineSeparator() +
//                    "true" + System.lineSeparator() +
//                    "0,0" + System.lineSeparator() +
//                    "No" + System.lineSeparator();
//
//            // Pair the input with the expected result
//            String expected = WELCOME;
//
//            // Runs the program with the input values
//            receiveInput(input);
//            Market.main(new String[0]);
//
//            // Retrieves the output from the program
//            String output = getOutput();
//
//            // Trims the output and verifies it is correct.
//            expected = expected.replaceAll("\r\n","\n");
//            output = output.replaceAll("\r\n","\n");
//            assertEquals("Error in expected output!",
//                    expected.trim(), output.trim());
//        }
//
//        @Test(timeout = 1000)
//        public void testExpectedThree() {
//            // Set the input
//            String input = "2,2" + System.lineSeparator() +
//                    "true,false" + System.lineSeparator() +
//                    "true,false" + System.lineSeparator() +
//                    "1,0" + System.lineSeparator() +
//                    "Yes" + System.lineSeparator() +
//                    "Right" + System.lineSeparator() +
//                    "Left" + System.lineSeparator() +
//                    "Down" + System.lineSeparator();
//
//            // Pair the input with the expected result
//            String expected = WELCOME;
//
//            // Runs the program with the input values
//            receiveInput(input);
//            Market.main(new String[0]);
//
//            // Retrieves the output from the program
//            String output = getOutput();
//
//            // Trims the output and verifies it is correct.
//            expected = expected.replaceAll("\r\n","\n");
//            output = output.replaceAll("\r\n","\n");
//            assertEquals("Error in expected output!",
//                    expected.trim(), output.trim());
//        }
    }
}