/**
 * NoUser Exception
 * <p>
 * exception for non-existing user
 *
 * @author Ekaterina Tszyao, Ryan Timmerman, Dimitri Paikos
 * @version 04/10/23
 */
public class NoUserException extends Exception {
    public NoUserException(String message) {
        super(message);
    }
}
