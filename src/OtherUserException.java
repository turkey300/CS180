/**
 * This is an Exception class, which is thrown when a user is already registered as
 * another role (Seller or Customer).
 *
 * @author Dimitri Paikos, Lab12
 * @version 4/10/2023
 */
public class OtherUserException extends Exception {
    public OtherUserException(String message) {
        super(message);
    }
}
