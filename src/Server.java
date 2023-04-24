import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242); // Port for connections 4242
        // creates new thread for every connection
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Server server = new Server(socket);
                new Thread(server).start();
            } catch (IOException e) {
            }
        }
    }

    public void run() {
        // code for server
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                String command = (String) ois.readObject();
                if (command.equals("Create an account")) {
                    String userType = (String) ois.readObject();
                    String username = (String) ois.readObject();
                    String password = (String) ois.readObject();
                    if (userType.equals("Seller")) {    //create seller account
                        try {
                            Seller newSeller = new Seller(username, password, true);
                            oos.writeObject(newSeller);
                        } catch (AlreadyUserException | OtherUserException e) {
                            oos.writeObject(e);
                        }
                    } else if (userType.equals("Customer")) {    //create customer account
                        try {
                            Customer newCustomer = new Customer(username, password, true);
                            oos.writeObject(newCustomer);
                        } catch (AlreadyUserException | OtherUserException e) {
                            oos.writeObject(e);
                        }
                    }
                } else if (command.equals("Log in")) {
                    String userType = (String) ois.readObject();
                    String username = (String) ois.readObject();
                    String password = (String) ois.readObject();
                    if (userType.equals("Seller")) {
                        if (Seller.checkAccount(username, password)) {
                            Seller seller = Seller.loadSeller(username);
                            oos.writeObject(seller);
                        } else {    // if account details are wrong throws error
//                            oos.writeObject(new NoUserException("This account does not exist!"));
                            oos.writeObject("This account does not exist!");
                        }
                    } else if (userType.equals("Customer")) {    //log in as customer
                        if (Customer.checkAccount(username, password)) {
                            Customer customer = Customer.loadCustomer(username);
                            oos.writeObject(customer);
                        } else {    // if account details are wrong throws error
//                            oos.writeObject(new NoUserException("This account does not exist!"));
                            oos.writeObject("This account does not exist!");
                        }
                    }
                }
                //other commands


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
