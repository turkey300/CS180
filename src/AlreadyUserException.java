/**
 * Already User exception
 * <p>
 * exception thrown if user exists
 *
 * @author Ryan Timmerman, Ekaterina Tszyao, Dimitri Paikos, Tyler Kei
 * @version 04/10/23
 */
public class AlreadyUserException extends Exception {
    public AlreadyUserException(String message) {
        super(message);
    }
}