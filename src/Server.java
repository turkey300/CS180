import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements Runnable {
    private Socket socket;
    public static Object sync = new Object(); // this will be the synchronize object

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242); // Port for connections 4242
        // creates new thread for every connection
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connected"); //this line is only for testing
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
                            oos.writeObject(e.getMessage());
                        }
                        oos.flush();
                    } else if (userType.equals("Customer")) {    //create customer account
                        try {
                            Customer newCustomer = new Customer(username, password, true);
                            oos.writeObject(newCustomer);
                        } catch (AlreadyUserException | OtherUserException e) {
                            oos.writeObject(e.getMessage());
                        }
                        oos.flush();
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
                        oos.flush();
                    } else if (userType.equals("Customer")) {    //log in as customer
                        if (Customer.checkAccount(username, password)) {
                            Customer customer = Customer.loadCustomer(username);
                            oos.writeObject(customer);
                        } else {    // if account details are wrong throws error
//                            oos.writeObject(new NoUserException("This account does not exist!"));
                            oos.writeObject("This account does not exist!");
                        }
                        oos.flush();
                    }
                } else if (command.equals("List of sellers")) { // sends list of sellers
                    oos.writeObject(Seller.loadAllSellers());
                    oos.flush();
                } else if (command.equals("Change username")) {
                    synchronized (sync) { // added sync so you cant change name of seller while someone is buying
                        String userType = (String) ois.readObject();
                        if (userType.equals("Customer")) {
                            Customer customer = (Customer) ois.readObject();
                            String username = (String) ois.readObject();

                            customer.setUsername(username);
                            oos.writeObject(customer);
                            oos.flush();
                        } else {
                            Seller seller = (Seller) ois.readObject();
                            String username = (String) ois.readObject();

                            seller.setUsername(username);
                            oos.writeObject(seller);
                            oos.flush();
                        }
                    }
                } else if (command.equals("Change password")) {
                    String userType = (String) ois.readObject();
                    if (userType.equals("Customer")) {
                        Customer customer = (Customer) ois.readObject();
                        String pass = (String) ois.readObject();

                        customer.setPassword(pass);
                        oos.writeObject(customer);
                        oos.flush();
                    } else {
                        Seller seller = (Seller) ois.readObject();
                        String pass = (String) ois.readObject();

                        seller.setPassword(pass);
                        oos.writeObject(seller);
                        oos.flush();
                    }
                } else if (command.equals("Delete account")) {
                    synchronized (sync) { // sync so you cant buy product from account thats deleted
                        String userType = (String) ois.readObject();
                        if (userType.equals("Customer")) {
                            Customer customer = (Customer) ois.readObject();
                            customer.deleteAccount();
                        } else {
                            Seller seller = (Seller) ois.readObject();
                            seller.deleteAccount();
                        }
                    }
                } else if (command.equals("Purchase product")) {
                    synchronized (sync) {
                        String stillProduct = (String) ois.readObject();
                        if (stillProduct.equals("True")) {
                            Store store = (Store) ois.readObject();
                            Product product = (Product) ois.readObject();
                            int amount = (Integer) ois.readObject();
                            Customer customer = (Customer) ois.readObject();

                            ArrayList<Seller> sellers = Seller.loadAllSellers();
                            if (store.purchaseProductFromStore(product, amount, customer)) {
                                for (int i = 0; i < sellers.size(); i++) {
                                    if (sellers.get(i).getUsername().equals(store.getSeller())) {
                                        sellers.get(i).saveSeller();
                                    }
                                }
                                oos.writeObject("Success");
                                oos.flush();
                                break;
                            } else {
                                oos.writeObject("Fail");
                                oos.flush();
                                break;
                            }

                        }
                    }
                }
                //other commands


            }
        } catch (SocketException | EOFException e) {
            //SocketException catches "Connection reset"; I cannot solve EOFException, but seems like everything is working fine
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
