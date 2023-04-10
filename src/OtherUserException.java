/**
 * Other User exception
 * <p>
 * exception throw to tell if it's a seller account or user account being logged on
 *
 * @author Ryan Timmerman, Ekaterina Tszyao, Dimitri Paikos, Tyler Kei
 * @version 04/10/23
 */
public class OtherUserException extends Exception {
    public OtherUserException(String message) {
        super(message);
    }
}
