import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
/**
 * Shopping cart gui to display items within shopping cart object
 *
 * @author Ekaterina Tszyao, Dimitri Paikos, Rayan Timmerman, Tyler Kei, Lab12
 * @version 05/01/2023
 */
public class ShoppingCartGUI extends JFrame {
    private JLabel label;
    private ArrayList<ShoppingCart> shoppingCart;
    private JButton purchaseButton;
    private JButton deleteButton;
    private JButton leaveButton;

    private boolean willbreak = false;

    public ShoppingCartGUI(String[] items, ArrayList<Seller> sellers, ArrayList<ShoppingCart> shoppingCart, Customer
            customer, ArrayList<String> shopprod) {
        super("Shopping Cart");

        label = new JLabel();
        label.setText("Shopping Cart: " + String.join(", ", items));

        purchaseButton = new JButton("Purchase all products.");
        purchaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent c) {
                // add code to purchase all products here
                ArrayList<Integer> delete = new ArrayList<>();
                for (int b = 0; b < shoppingCart.size(); b++) {
                    // loops through all products, if one matches name purchases products
                    for (int e = 0; e < sellers.size(); e++) {
                        ArrayList<Store> stores = sellers.get(e).getStores();
                        for (int f = 0; f < stores.size(); f++) {
                            ArrayList<Product> products = stores.get(f).getProducts();
                            for (int k = 0; k < products.size(); k++) {
                                if (products.get(k).getProductName().equals(
                                        shoppingCart.get(b).getProduct().getProductName())) {
                                    if (stores.get(f).purchaseProductFromStore(
                                            products.get(k), shoppingCart.get(b).getAmount(), customer)) {
                                        //shoppingCart.remove(shoppingCart.get(b));
                                        //this would sometimes crash,
                                        //it removes something from shopping cart
                                        //changing the size of shopping cart which is not good
                                        delete.add(b);
                                        System.out.printf("Purchased %s successfully!\n",
                                                products.get(k).getProductName());
                                        customer.saveCustomer();
                                        for (int o = 0; o < sellers.size(); o++) {
                                            if (sellers.get(o).getUsername().equals(
                                                    stores.get(f).getSeller())) {
                                                sellers.get(o).saveSeller();
                                            }
                                        }
                                    } else {
                                        System.out.printf("Sorry, we don't have enough items of %s " +
                                                "available.\n", products.get(k).getProductName());
                                    }
                                }
                            }
                        }
                    }
                }
                for (int b = 0; b < delete.size(); b++) {
                    shoppingCart.remove(shoppingCart.get(delete.get(b)));
                }
                JOptionPane.showMessageDialog(null, "Purchased all available products, leaving" +
                        " shopping cart!", "Shopping Cart", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        });

        deleteButton = new JButton("Delete Product from shopping cart");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // add code to delete product here
                System.out.println("Which product would you like to delete?");
                String[] cartproducts = shopprod.toArray(new String[0]);
                String select = (String) JOptionPane.showInputDialog(null, "Which product would" +
                        " you like to delete?", "Delete?", JOptionPane.QUESTION_MESSAGE, null, cartproducts,
                        cartproducts[0]);
                select = Character.toString(select.charAt(0));
                int intInput = Integer.parseInt(select);
               // String[] prods = products.toArray(new String[0]);

                    if (intInput > 0 && intInput <= shoppingCart.size()) {
                        shoppingCart.remove(intInput - 1);
                        JOptionPane.showMessageDialog(null, "Product removed from shopping cart!"
                                , "Delete?", JOptionPane.INFORMATION_MESSAGE);
                        customer.saveCustomer();
                        dispose();
                    }
            }
        });

        leaveButton = new JButton("Leave Shopping Cart");
        leaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(label);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(purchaseButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(leaveButton);

        panel.add(buttonPanel);
        getContentPane().add(panel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public ArrayList<ShoppingCart> getCart() {
        return shoppingCart;
    }

    public boolean breakloop() {
        return willbreak;
    }

}

/** this is deleted from market method keeping here for now until confirmed works
 *  System.out.println();
 *                         do {
 *                             System.out.println("1. Purchase all products.");
 *                             System.out.println("2. Delete product from shopping cart.");
 *                             System.out.println("3. Leave shopping cart.");
 *                             input = scanner.nextLine();
 *                             if (!(input.equals("1") || input.equals("2") || input.equals("3")))
 *                                 System.out.println("Please enter a number corresponding to an option.");
 *                         } while (!(input.equals("1") || input.equals("2") || input.equals("3")));
 *
 *
 if (input.equals("1")) { // this seems bad but idk of a better way
         ArrayList<Integer> delete = new ArrayList<>();
        for (int b = 0; b < shoppingCart.size(); b++) {
        // loops through all products, if one matches name purchases products
        for (int e = 0; e < sellers.size(); e++) {
        ArrayList<Store> stores = sellers.get(e).getStores();
        for (int f = 0; f < stores.size(); f++) {
        ArrayList<Product> products = stores.get(f).getProducts();
        for (int k = 0; k < products.size(); k++) {
        if (products.get(k).getProductName().equals(
        shoppingCart.get(b).getProduct().getProductName())) {
        if (stores.get(f).purchaseProductFromStore(
        products.get(k), shoppingCart.get(b).getAmount(), customer)) {
        //shoppingCart.remove(shoppingCart.get(b));
        //this would sometimes crash,
        //it removes something from shopping cart
        //changing the size of shopping cart which is not good
        delete.add(b);
        System.out.printf("Purchased %s successfully!\n",
        products.get(k).getProductName());
        customer.saveCustomer();
        for (int o = 0; o < sellers.size(); o++) {
        if (sellers.get(o).getUsername().equals(
        stores.get(f).getSeller())) {
        sellers.get(o).saveSeller();
        }
        }
        } else {
        System.out.printf("Sorry, we don't have enough items of %s " +
        "available.\n", products.get(k).getProductName());
        }
        }
        }
        }
        }
        }

        for (int b = 0; b < delete.size(); b++) {
        shoppingCart.remove(shoppingCart.get(delete.get(b)));
        }

        System.out.println("Purchased all available products, leaving shopping cart!");
        break;
        } else if (input.equals("2")) { // deletes product
        do {
        System.out.println("Which product would you like to delete?");

        input = scanner.nextLine();
        try {
        int intInput = Integer.parseInt(input);
        if (intInput > 0 && intInput <= shoppingCart.size()) {
        shoppingCart.remove(intInput - 1);
        System.out.println("Product removed from shopping cart!");
        customer.saveCustomer();
        break;
        } else {
        System.out.println("Please enter an option corresponding to a product.");
        }
        } catch (Exception e) {
        System.out.println("Please enter an option corresponding to a product.");
        }
        } while (true);
        } else if (input.equals("3")) { // leaves shopping cart
        System.out.println("Leaving shopping cart!");
        break;
        }
*/
