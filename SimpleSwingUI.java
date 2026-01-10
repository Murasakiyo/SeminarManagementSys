import javax.swing.*;
import java.awt.event.*;

public class SimpleSwingUI {

    public static void main(String[] args) {
        // Create frame
        JFrame frame = new JFrame("Simple Swing Example");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Label
        JLabel label = new JLabel("Enter your name:");
        label.setBounds(20, 20, 120, 25);
        frame.add(label);

        // Text field
        JTextField textField = new JTextField();
        textField.setBounds(140, 20, 120, 25);
        frame.add(textField);

        // Button
        JButton button = new JButton("Greet");
        button.setBounds(90, 60, 100, 30);
        frame.add(button);

        // Button action
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Hello, " + textField.getText() + "!"
                );
            }
        });

        // Show frame
        frame.setVisible(true);
    }
}
