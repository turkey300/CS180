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


        @Test(timeout = 1000)
        public void testExpectedOne() {
            // Set the input
            String input = "2,2" + System.lineSeparator() +
                    "true,true" + System.lineSeparator() +
                    "true,true" + System.lineSeparator() +
                    "1,1" + System.lineSeparator() +
                    "Yes" + System.lineSeparator() +
                    "Up" + System.lineSeparator() +
                    "Right" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = WELCOME + System.lineSeparator() +
                    INITIALIZE_MAZE + System.lineSeparator() +
                    MAZE_DIMENSIONS + System.lineSeparator() +
                    String.format(MAZE_VALUES,0) + System.lineSeparator() +
                    String.format(MAZE_VALUES,1) + System.lineSeparator() +
                    TREASURE_LOCATION + System.lineSeparator() +
                    READY + System.lineSeparator() +
                    String.format(CURRENT_POSITION,0,0) + System.lineSeparator() +
                    MOVE_SELECT + System.lineSeparator() +
                    "1. " + MOVES[0] + System.lineSeparator() +
                    "2. " + MOVES[1] + System.lineSeparator() +
                    "3. " + MOVES[2] + System.lineSeparator() +
                    "4. " + MOVES[3] + System.lineSeparator() +
                    String.format(CURRENT_POSITION,1,0) + System.lineSeparator() +
                    MOVE_SELECT + System.lineSeparator() +
                    "1. " + MOVES[0] + System.lineSeparator() +
                    "2. " + MOVES[1] + System.lineSeparator() +
                    "3. " + MOVES[2] + System.lineSeparator() +
                    "4. " + MOVES[3] + System.lineSeparator() +
                    TREASURE_FOUND + System.lineSeparator() +
                    FAREWELL + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            MazeNavigator.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure players can navigate the maze successfully!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        public void testExpectedTwo() {
            // Set the input
            String input = "1,1" + System.lineSeparator() +
                    "true" + System.lineSeparator() +
                    "0,0" + System.lineSeparator() +
                    "No" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = WELCOME + System.lineSeparator() +
                    INITIALIZE_MAZE + System.lineSeparator() +
                    MAZE_DIMENSIONS + System.lineSeparator() +
                    String.format(MAZE_VALUES,0) + System.lineSeparator() +
                    TREASURE_LOCATION + System.lineSeparator() +
                    READY + System.lineSeparator() +
                    FAREWELL + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            MazeNavigator.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure players can exit before starting the game!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        public void testExpectedThree() {
            // Set the input
            String input = "2,2" + System.lineSeparator() +
                    "true,false" + System.lineSeparator() +
                    "true,false" + System.lineSeparator() +
                    "1,0" + System.lineSeparator() +
                    "Yes" + System.lineSeparator() +
                    "Right" + System.lineSeparator() +
                    "Left" + System.lineSeparator() +
                    "Down" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = WELCOME + System.lineSeparator() +
                    INITIALIZE_MAZE + System.lineSeparator() +
                    MAZE_DIMENSIONS + System.lineSeparator() +
                    String.format(MAZE_VALUES,0) + System.lineSeparator() +
                    String.format(MAZE_VALUES,1) + System.lineSeparator() +
                    TREASURE_LOCATION + System.lineSeparator() +
                    READY + System.lineSeparator() +
                    String.format(CURRENT_POSITION,0,0) + System.lineSeparator() +
                    MOVE_SELECT + System.lineSeparator() +
                    "1. " + MOVES[0] + System.lineSeparator() +
                    "2. " + MOVES[1] + System.lineSeparator() +
                    "3. " + MOVES[2] + System.lineSeparator() +
                    "4. " + MOVES[3] + System.lineSeparator() +
                    INVALID_MOVE + System.lineSeparator() +
                    String.format(CURRENT_POSITION,0,0) + System.lineSeparator() +
                    MOVE_SELECT + System.lineSeparator() +
                    "1. " + MOVES[0] + System.lineSeparator() +
                    "2. " + MOVES[1] + System.lineSeparator() +
                    "3. " + MOVES[2] + System.lineSeparator() +
                    "4. " + MOVES[3] + System.lineSeparator() +
                    INVALID_MOVE + System.lineSeparator() +
                    String.format(CURRENT_POSITION,0,0) + System.lineSeparator() +
                    MOVE_SELECT + System.lineSeparator() +
                    "1. " + MOVES[0] + System.lineSeparator() +
                    "2. " + MOVES[1] + System.lineSeparator() +
                    "3. " + MOVES[2] + System.lineSeparator() +
                    "4. " + MOVES[3] + System.lineSeparator() +
                    TREASURE_FOUND + System.lineSeparator() +
                    FAREWELL + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            MazeNavigator.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure players can navigate the maze successfully, as well as handle invalid moves!",
                    expected.trim(), output.trim());
        }
    }
}