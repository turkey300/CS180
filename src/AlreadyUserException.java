/**
 * This is an Exception class, which is thrown when a user tries to create an account but that account
 * already exists.
 *
 * @author Dimitri Paikos, Lab12
 * @version 4/10/2023
 */
public class AlreadyUserException extends Exception {
    public AlreadyUserException(String message) {
        super(message);
    }
}