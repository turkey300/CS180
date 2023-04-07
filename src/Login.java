import java.util.Objects;
import java.util.Scanner;

public class Login {
  public String Login() {
    Scanner scanner = new Scanner(System.in);
    boolean newacc = false;
    System.out.println("Are you a customer or a seller?");
    String role = scanner.nextLine();
    System.out.println("Do you have an existing account? (Yes/No)");
    String exist = scanner.nextLine();
   if (exist.equals("Yes")) {
     newacc = true;
   }
    System.out.println("Please enter your username and password"); // Should be entered one at a time separate lines.
    String username = scanner.nextLine();
    String password = scanner.nextLine();
    if (role.equals("seller") {
     Seller seller = new Seller(username, password, newacc);
     return seller;
    }
    else {
     Customer customer = new Customer(username, password, newacc);
     return customer;
    }
  }
}
