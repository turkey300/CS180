/**
 * no user exception
 * <p>
 * exception thrown if user information doesn't exist
 *
 * @author Ryan Timmerman, Ekaterina Tszyao, Dimitri Paikos, Tyler Kei
 * @version 04/10/23
 */
public class NoUserException extends Exception {
    public NoUserException(String message) {
        super(message);
    }
}
