import javax.swing.*;
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
//                    synchronized (sync) {
                        Product product = (Product) ois.readObject();
//                        oos.writeObject("Waiting");
//                        oos.flush();
                        ArrayList<Seller> sellers = Seller.loadAllSellers();  // grabs sellers again

//                        try {
//                            oos.writeObject("List of sellers");
//                            oos.flush();
//                            sellers = (ArrayList<Seller>) ois.readObject();
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }

                        ArrayList<Store> stores = new ArrayList<>(); // grabbing stores again
                        for (int j = 0; j < sellers.size(); j++) {
                            ArrayList<Store> currentStores = sellers.get(j).getStores();
                            stores.addAll(currentStores);
                        }

                        boolean stillProduct = false;
                        Store store = null;
                        for (int k = 0; k < stores.size(); k++) { // checking if product is in store
                            if (product.getStoreName().equals(stores.get(k).getStoreName())) {
                                stillProduct = true;
                                store = stores.get(k);
                                product = store.getProduct(product.getProductName());
                            }
                        }
                        oos.writeObject(stillProduct);
                        oos.flush();

//                        oos.writeObject("Waiting");
//                        oos.flush();
//                        ois.readObject();
//                        String stillProduct = (String) ois.readObject();
                        if (stillProduct) {
//                            Store store = (Store) ois.readObject();
//                            Product product = (Product) ois.readObject();
                            int amount = (Integer) ois.readObject();
                            Customer customer = (Customer) ois.readObject();

//                            ArrayList<Seller> sellers = Seller.loadAllSellers();
                            String message;
                            //TODO
                            System.out.println("Before purchase:" + product.productPageDisplay());
                            if (store.purchaseProductFromStore(product, amount, customer)) {
                                for (int i = 0; i < sellers.size(); i++) {
                                    sellers.get(i).saveSeller();
                                }
                                message = "Purchased successfully!";
                            } else {
                                message = "Sorry, there were not enough items available.";
                            }
                            customer.saveCustomer();
                            System.out.println("After purchase:" + product.productPageDisplay());
                            oos.writeObject(message);
                            oos.flush();
                            System.out.println(product.productPageDisplay());
                        }
//                    }
                } else if (command.equals("Add to cart")) {
                    synchronized (sync) {
                        Product product = (Product) ois.readObject();

                        ArrayList<Seller> sellers = Seller.loadAllSellers();  // grabs sellers again
                        ArrayList<Store> stores = new ArrayList<>(); // grabbing stores again
                        for (int j = 0; j < sellers.size(); j++) {
                            ArrayList<Store> currentStores = sellers.get(j).getStores();
                            stores.addAll(currentStores);
                        }

                        boolean stillProduct = false;
                        Store store = null;
                        for (int k = 0; k < stores.size(); k++) { // checking if product is in store
                            if (product.getStoreName().equals(stores.get(k).getStoreName())) {
                                stillProduct = true;
                                store = stores.get(k);
                            }
                        }
                        oos.writeObject(stillProduct);
                        oos.flush();
                        if (stillProduct) {
                            int amount = (Integer) ois.readObject();
                            Customer customer = (Customer) ois.readObject();
                            String message = "";
                            if (amount <= product.getAvailableQuantity()) {
                                for (int i = 0; i < sellers.size(); i++) {
                                    if (sellers.get(i).getUsername().equals(store.getSeller())) {
                                        customer.addShoppingCart(product, sellers.get(i), amount);
                                        customer.saveCustomer();
                                        message = "Successfully added to shopping cart!";
                                    }
                                }
                            } else {
                                message = "Sorry, there were not enough items available.";
                            }
                            customer.saveCustomer();
                            oos.writeObject(message);
                            oos.flush();
                        }
                    }
                } else if (command.equals("refresh")) {
                    Product product = (Product) ois.readObject();
                    System.out.println("Before refresh:" + product.productPageDisplay());

                    Product newProduct = null;
                    ArrayList<Seller> sellers = Seller.loadAllSellers();
                    for (int i = 0; i < sellers.size(); i++) {
                        ArrayList<Store> stores = sellers.get(i).getStores();
                        for (int j = 0; j < stores.size(); j++) {
                            ArrayList<Product> products = stores.get(j).getProducts();
                            for (int k = 0; k < products.size(); k++) {
                                if (product.getProductName().equals(products.get(k).getProductName())) {
                                    newProduct = products.get(k);
                                }
                            }
                        }
                    }

                    if (newProduct == null) oos.writeObject("not found");
                    else oos.writeObject(newProduct);
                    oos.flush();
                    System.out.println("After refresh:" + newProduct.productPageDisplay());
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
