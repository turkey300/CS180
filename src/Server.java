import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * This is the server end of Market, which responds to request made by user through client. It's mainly responsible for
 * loading and saving data for the client.
 *
 * @author Ekaterina Tszyao, Dimitri Paikos, Rayan Timmerman, Tyler Kei, Lab12
 * @version 05/01/2023
 */
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
                            oos.writeObject("This account does not exist!");
                        }
                        oos.flush();
                    } else if (userType.equals("Customer")) {    //log in as customer
                        if (Customer.checkAccount(username, password)) {
                            Customer customer = Customer.loadCustomer(username);
                            oos.writeObject(customer);
                        } else {    // if account details are wrong throws error
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
                                product = store.getProduct(product.getProductName());
                            }
                        }
                        oos.writeObject(stillProduct);
                        oos.flush();

                        if (stillProduct) {
                            int amount = (Integer) ois.readObject();
                            Customer customer = (Customer) ois.readObject();
                            customer = Customer.loadCustomer(customer.getUsername());
                            String message;
                            if (store.purchaseProductFromStore(product, amount, customer)) {
                                for (int i = 0; i < sellers.size(); i++) {
                                    sellers.get(i).saveSeller();
                                }
                                message = "Purchased successfully!";
                            } else {
                                message = "Sorry, there were not enough items available.";
                            }
                            oos.writeObject(message);
                            oos.flush();
                        }
                    }
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
                                product = store.getProduct(product.getProductName());
                            }
                        }
                        oos.writeObject(stillProduct);
                        oos.flush();
                        if (stillProduct) {
                            int amount = (Integer) ois.readObject();
                            Customer customer = (Customer) ois.readObject();
                            customer = Customer.loadCustomer(customer.getUsername());
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
                } else if (command.equals("refresh product")) {
                    Product product = (Product) ois.readObject();
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
                } else if (command.equals("refresh user")) {
                    String userType = (String) ois.readObject();
                    String username = (String) ois.readObject();
                    if (userType.equals("customer")) {
                        oos.writeObject(Customer.loadCustomer(username));
                        oos.flush();
                    } else if (userType.equals("seller")) {
                        oos.writeObject(Seller.loadSeller(username));
                        oos.flush();
                    }

                } else if (command.equals("csv")) {
                    Seller seller = Seller.loadSeller((String) ois.readObject());
                    String name = (String) ois.readObject();
                    ArrayList<Store> stores = seller.getStores();
                    Store currentStore = null;
                    for (int i = 0; i < stores.size(); i++) {
                        if (stores.get(i).getStoreName().equals(name)) currentStore = stores.get(i);
                    }
                    String file = (String) ois.readObject();


                    File f = new File(file);
                    String message = "";
                    try (BufferedReader bfr = new BufferedReader(new FileReader(f))) {
                        String line = bfr.readLine();
                        while (line != null) {
//                productName;description;int availableQuantity;double price;String storeName
                            try {
                                String productName;
                                String description;
                                int availableQuantity;
                                double price;
                                String storeName;
                                String substring;

                                productName = line.substring(0, line.indexOf(","));
                                productName = productName.replaceAll("\"", "");
                                substring = line.substring(line.indexOf(",") + 1);
                                description = substring.substring(0, substring.indexOf(","));
                                description = description.replaceAll("\"", "");
                                substring = substring.substring(substring.indexOf(",") + 1);
                                availableQuantity = Integer.parseInt(substring.substring(0, substring.indexOf("," +
                                        "")).replaceAll("\"", ""));
                                substring = substring.substring(substring.indexOf(",") + 1);
                                price = Double.parseDouble(substring.substring(0, substring.indexOf("," +
                                        "")).replaceAll("\"", ""));
                                storeName = substring.substring(substring.indexOf(",") + 1);
                                storeName = storeName.replaceAll("\"", "");


                                if (availableQuantity > 0 && price >= 0) {
                                    currentStore.addProduct(new Product(productName, description,
                                            availableQuantity, price, currentStore.getStoreName()));
                                    message = "Product added!";
                                    seller.saveSeller();
                                } else {
                                    message = "Product has invalid data!";
                                }
                            } catch (Exception e) {
                                message = "Error parsing product!";
                            }
                            line = bfr.readLine();
                        }
                    } catch (Exception e) {
                        message = "Error reading in product!";
                    }
                    oos.writeObject(message);
                } else if (command.equals("Export products")) {
                    Seller seller = Seller.loadSeller((String) ois.readObject());
                    String name = (String) ois.readObject();
                    ArrayList<Store> stores = seller.getStores();
                    Store currentStore = null;
                    for (int i = 0; i < stores.size(); i++) {
                        if (stores.get(i).getStoreName().equals(name)) currentStore = stores.get(i);
                    }
                    String file = (String) ois.readObject();
                    File f = new File(file);
                    String message;
                    if (f.exists()) {
                        message = "This file already exists! Try a new file path.";
                    } else {
                        ArrayList<Product> products = currentStore.getProducts();
                        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
                            for (int i = 0; i < products.size(); i++) {
//                productName;description;int availableQuantity;double price;String storeName
                                Product product = products.get(i);
                                pw.printf("%s,%s,%d,%f,%s\n", product.getProductName(), product.getDescription(),
                                        product.getAvailableQuantity(), product.getPrice(), product.getStoreName());
                            }
                        } catch (Exception e) {
                            message = "Error exporting file!";
                        }
                        message = "Products exported!";
                    }
                    oos.writeObject(message);
                } else if (command.equals("Add product")) {
                    Store store = (Store) ois.readObject();
                    Seller seller = (Seller) ois.readObject();
                    String name = (String) ois.readObject();
                    String description = (String) ois.readObject();
                    int quantity = (Integer) ois.readObject();
                    double price = (Double) ois.readObject();
                    String storeName = (String) ois.readObject();

                    seller = Seller.loadSeller(seller.getUsername());
                    ArrayList<Store> stores = seller.getStores();
                    for (int i = 0; i < stores.size(); i++) {
                        if (stores.get(i).getStoreName().equals(storeName))
                            store = stores.get(i);
                    }

                    store.addProduct(new Product(name, description, quantity, price, storeName));
                    seller.saveSeller();
                } else if (command.equals("Edit product")) {
                    synchronized (sync) {
                        String input = (String) ois.readObject();
                        if (input.equals("name")) {
                            Seller seller = (Seller) ois.readObject();
                            Product product = (Product) ois.readObject();
                            String name = (String) ois.readObject();

                            ArrayList<Seller> sellers = Seller.loadAllSellers();
                            for (int i = 0; i < sellers.size(); i++) {
                                if (sellers.get(i).getUsername().equals(seller.getUsername()))
                                    seller = sellers.get(i);
                            }
                            ArrayList<Store> stores = seller.getStores();
                            Store store = null;
                            for (int i = 0; i < stores.size(); i++) {
                                if (stores.get(i).getStoreName().equals(product.getStoreName()))
                                    store = stores.get(i);
                            }

                            product = store.getProduct(product.getProductName());

                            product.editProductName(name);
                            seller.saveSeller();
                            store.saveStore();
                        } else if (input.equals("desc")) {
                            Seller seller = (Seller) ois.readObject();
                            Product product = (Product) ois.readObject();
                            String desc = (String) ois.readObject();

                            ArrayList<Seller> sellers = Seller.loadAllSellers();
                            for (int i = 0; i < sellers.size(); i++) {
                                if (sellers.get(i).getUsername().equals(seller.getUsername()))
                                    seller = sellers.get(i);
                            }
                            ArrayList<Store> stores = seller.getStores();
                            Store store = null;
                            for (int i = 0; i < stores.size(); i++) {
                                if (stores.get(i).getStoreName().equals(product.getStoreName()))
                                    store = stores.get(i);
                            }

                            product = store.getProduct(product.getProductName());

                            product.editDescription(desc);
                            seller.saveSeller();
                            store.saveStore();
                        } else if (input.equals("quantity")) {
                            Seller seller = (Seller) ois.readObject();
                            Product product = (Product) ois.readObject();
                            int quantity = (Integer) ois.readObject();

                            ArrayList<Seller> sellers = Seller.loadAllSellers();
                            for (int i = 0; i < sellers.size(); i++) {
                                if (sellers.get(i).getUsername().equals(seller.getUsername()))
                                    seller = sellers.get(i);
                            }
                            ArrayList<Store> stores = seller.getStores();
                            Store store = null;
                            for (int i = 0; i < stores.size(); i++) {
                                if (stores.get(i).getStoreName().equals(product.getStoreName()))
                                    store = stores.get(i);
                            }

                            product = store.getProduct(product.getProductName());

                            product.editAvailableQuantity(quantity);
                            seller.saveSeller();
                            store.saveStore();
                        } else if (input.equals("price")) {
                            Seller seller = (Seller) ois.readObject();
                            Product product = (Product) ois.readObject();
                            Double price = (Double) ois.readObject();

                            ArrayList<Seller> sellers = Seller.loadAllSellers();
                            for (int i = 0; i < sellers.size(); i++) {
                                if (sellers.get(i).getUsername().equals(seller.getUsername()))
                                    seller = sellers.get(i);
                            }
                            ArrayList<Store> stores = seller.getStores();
                            Store store = null;
                            for (int i = 0; i < stores.size(); i++) {
                                if (stores.get(i).getStoreName().equals(product.getStoreName()))
                                    store = stores.get(i);
                            }

                            product = store.getProduct(product.getProductName());

                            product.editPrice(price);
                            seller.saveSeller();
                            store.saveStore();
                        }
                    }
                } else if (command.equals("Delete product")) {
                    synchronized (sync) {
                        Seller seller = (Seller) ois.readObject();
                        Store store1 = (Store) ois.readObject();
                        int num = (Integer) ois.readObject();

                        ArrayList<Seller> sellers = Seller.loadAllSellers();
                        for (int i = 0; i < sellers.size(); i++) {
                            if (sellers.get(i).getUsername().equals(seller.getUsername()))
                                seller = sellers.get(i);
                        }
                        ArrayList<Store> stores = seller.getStores();
                        Store store = null;
                        for (int i = 0; i < stores.size(); i++) {
                            if (stores.get(i).getStoreName().equals(store1.getStoreName()))
                                store = stores.get(i);
                        }
                        Product product = store.getProduct(num);
                        store.deleteProduct(product);

                        seller.saveSeller();
                        store.saveStore();
                    }
                } else if (command.equals("Create store")) {
                    Seller seller = (Seller) ois.readObject();
                    String name = (String) ois.readObject();

                    Store store = new Store(name, seller.getUsername());
                    ArrayList<Seller> sellers = Seller.loadAllSellers();
                    for (int i = 0; i < sellers.size(); i++) {
                        if (sellers.get(i).getUsername().equals(seller.getUsername()))
                            seller = sellers.get(i);
                    }
                    seller.addStore(store);
                }
                //other commands
            }
        } catch (SocketException | EOFException e) {
            //SocketException catches "Connection reset"; I cannot solve EOFException, but seems like everything is
            // working fine
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
