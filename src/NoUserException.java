/**
 * This is an Exception class, which is thrown when a user tries to log in but the indicated account
 * does not exist.
 *
 * @author Dimitri Paikos, Lab12
 * @version 4/10/2023
 */
public class NoUserException extends Exception {
    public NoUserException(String message) {
        super(message);
    }
}
