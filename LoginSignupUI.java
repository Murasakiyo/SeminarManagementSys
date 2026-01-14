import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginSignupUI {

    static final String FILE_NAME = "users.txt";
    public static void main(String[] args) {
        showLogin();
    }

    // LOGIN WINDOW
    static void showLogin() {
        
        MyFrame frame = new MyFrame();
        frame.setTitle("Login");

        // HEADER --------------------------------------------------------------------------------
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // FORM PANEL ----------------------------------------------------------------------------
        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        JPanel userFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        JPanel passFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));


        userFieldPanel.add(new JLabel("Username: "));
        JTextField userField = new JTextField(12);
        userFieldPanel.add(userField);

        passFieldPanel.add(new JLabel("Password: "));
        JPasswordField passField = new JPasswordField(12);
        passFieldPanel.add(passField);

        formPanel.add(userFieldPanel);
        formPanel.add(passFieldPanel);

        // CENTER WRAPPER
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS)); 
        centerWrapper.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 5),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            )
        ); 
        centerWrapper.add(Box.createVerticalStrut(50)); 
        centerWrapper.add(formPanel);

        // USERTYPE SELECTION ----------------------------------------------------------------
        JRadioButton student = new JRadioButton("Student");
        JRadioButton coordinator = new JRadioButton("Coordinator");
        JRadioButton evaluator = new JRadioButton("Evaluator");

        ButtonGroup usertype = new ButtonGroup();
        usertype.add(student);
        usertype.add(coordinator);
        usertype.add(evaluator);

        // Default selection
        student.setSelected(true);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        rolePanel.add(student);
        rolePanel.add(coordinator);
        rolePanel.add(evaluator);
        centerWrapper.add(rolePanel);

        // BUTTON PANEL ----------------------------------------------------------------------
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        centerWrapper.add(buttonPanel);
        centerWrapper.add(Box.createVerticalGlue()); // absorbs extra space  

        frame.add(centerWrapper, BorderLayout.CENTER);

        // ACTIONS -------------------------------------------------------------------------------------

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            int userRole = 1;

            if (student.isSelected()){
                userRole = 1;
            }
            else if (coordinator.isSelected()){
                userRole = 2;
            }
            else if (evaluator.isSelected()){
                userRole = 3;
            }

            if (checkLogin(user, pass, userRole)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password or role");
            }
        });

        signupBtn.addActionListener(e -> {
            frame.dispose();
            showSignup();
        });

        
        // --------------------------------------------------------------------------------------------

        frame.setVisible(true);
    }

    // SIGN UP WINDOW ------------------------------------------------------------------------------------
    static void showSignup() {

        MyFrame frame = new MyFrame();
        frame.setTitle("Sign Up");

        // HEADER
        frame.add(new HeaderPanel(), BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 20));
        JPanel userFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,20));
        JPanel passFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));

        userFieldPanel.add(new JLabel("Username: "));
        JTextField userField = new JTextField(12);
        userFieldPanel.add(userField);

        passFieldPanel.add(new JLabel("Password: "));
        JPasswordField passField = new JPasswordField(12);
        passFieldPanel.add(passField);

        formPanel.add(userFieldPanel);
        formPanel.add(passFieldPanel);

        // USERTYPE SELECTION ----------------------------------------------------------------
        JRadioButton student = new JRadioButton("Student");
        JRadioButton coordinator = new JRadioButton("Coordinator");
        JRadioButton evaluator = new JRadioButton("Evaluator");

        ButtonGroup usertypeGroup = new ButtonGroup();
        usertypeGroup.add(student);
        usertypeGroup.add(coordinator);
        usertypeGroup.add(evaluator);

        // Default selection
        student.setSelected(true);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rolePanel.add(student);
        rolePanel.add(coordinator);
        rolePanel.add(evaluator);
        formPanel.add(rolePanel);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton signupBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back");
        buttonPanel.add(signupBtn);
        buttonPanel.add(backBtn);
        formPanel.add(buttonPanel);

        frame.add(formPanel, BorderLayout.WEST);

        signupBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            // Get selected user type
            String usertype = "Student"; // default
            if (coordinator.isSelected()) usertype = "Coordinator";
            else if (evaluator.isSelected()) usertype = "Evaluator";

            if (!user.isEmpty() && !pass.isEmpty()) {
                saveUser(user, pass, usertype);
                JOptionPane.showMessageDialog(frame, "Account Created!");
                frame.dispose();
                showLogin();
            } else {
                JOptionPane.showMessageDialog(frame, "Fields cannot be left empty");
            }
        });

        backBtn.addActionListener(e ->{
            frame.dispose();
            showLogin();
        });

        frame.setVisible(true);
    }


    // SAVE USER TO FILE
    static void saveUser(String username, String password, String usertype) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(username + "," + password + "," + usertype + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CHECK LOGIN DETAILS
    static boolean checkLogin(String username, String password, int userNum) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            int userInt;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(username) && data[1].equals(password)) {
                    if (data[2].equals("Student")){
                    userInt = 1;
                    }
                    else if (data[2].equals("Coordinator")){
                        userInt = 2;
                    }
                    else{
                        userInt = 3;
                    }

                    if (userInt == userNum){
                        return true;
                    }
                    
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
