import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PurchaseHistoryGUI extends JFrame {
    private JButton backButton;
    private JButton exportButton;

    private JLabel textLabel;

    private boolean willbreak;

    public PurchaseHistoryGUI(ArrayList<PurchaseHistory> purchaseHistory, String[] purhist) {

        setTitle("Purchase History");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        textLabel = new JLabel();
        textLabel.setText("Purchase History: " + String.join(", ", purhist));
        textLabel.setHorizontalAlignment(JLabel.CENTER);


        backButton = new JButton("Back to main page");
        exportButton = new JButton("Export purchase history");


        setLayout(new FlowLayout());
        add(backButton);
        add(exportButton);


        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                willbreak = true;
                dispose();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                while (true) {
                    String file = JOptionPane.showInputDialog(null, "Please enter the file path to export to.", "File", JOptionPane.QUESTION_MESSAGE);
                    File f = new File(file);
                    if (f.exists()) {
                        JOptionPane.showMessageDialog(null, "This file already exists! Try a new file path.", "Error", JOptionPane.ERROR_MESSAGE);

                    } else {
                        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
                            for (int j = purchaseHistory.size() - 1; j >= 0; j--) {
                                PurchaseHistory product = purchaseHistory.get(j);
                                pw.printf("Product: %s. Amount purchased: %d. Store: %s\n", product
                                                .getProduct().getProductName(), product.getAmount(),
                                        product.getStoreName());
                            }
                            JOptionPane.showMessageDialog(null, "Purchase history exported!", "Export", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        } catch (Exception g) {
                            JOptionPane.showMessageDialog(null, "Error while writing to file!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                dispose();
            }

        });
    }

    public boolean breakloop() {
        return willbreak;
    }
}