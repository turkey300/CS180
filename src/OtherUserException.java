/**
 * OtherUser Exception
 * <p>
 * exception to check for seller or customer user
 *
 * @author Ekaterina Tszyao, Ryan Timmerman, Dimitri Paikos
 * @version 04/10/23
 */
public class OtherUserException extends Exception {
    public OtherUserException(String message) {
        super(message);
    }
}
