/**
 * AlreadyUser Exception
 * <p>
 * exception for existing user
 *
 * @author Ekaterina Tszyao, Ryan Timmerman, Dimitri Paikos
 * @version 04/10/23
 */
public class AlreadyUserException extends Exception {
    public AlreadyUserException(String message) {
        super(message);
    }
}