import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User base class - Parent class for Student, Evaluator, and Coordinator
 * All user types inherit common attributes and methods from this class
 */
public class User {
    // Protected attributes - accessible by child classes
    protected String username;
    protected String name;
    protected String email_address;
    protected String password;
    protected String usertype; // "Student", "Evaluator", or "Coordinator"
    
    // File constant
    private static final String USER_FILE = "users.txt";
    
    /**
     * Constructor for User class
     */
    public User(String username, String name, String email_address, String password, String usertype) {
        this.username = username;
        this.name = name;
        this.email_address = email_address;
        this.password = password;
        this.usertype = usertype;
    }
    
    /**
     * Sign up a new user - saves to users.txt
     * Format: username,name,email,password,usertype
     */
    public boolean signUp() {
        try (FileWriter fw = new FileWriter(USER_FILE, true)) {
            fw.write(String.format("%s,%s,%s,%s,%s\n",
                username, name, email_address, password, usertype));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Login - verify credentials against users.txt
     */
    public boolean login() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    if (data[0].equals(username) && data[3].equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete user account
     */
    public boolean deleteAccount() {
        List<String> lines = new ArrayList<>();
        boolean deleted = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (!data[0].equals(username)) {
                    lines.add(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        // Write back
        try (FileWriter fw = new FileWriter(USER_FILE)) {
            for (String line : lines) {
                fw.write(line + "\n");
            }
            return deleted;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Modify account details
     */
    public boolean modifyAccount(String newName, String newEmail, String newPassword) {
        this.name = newName;
        this.email_address = newEmail;
        this.password = newPassword;
        
        // Update in file
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    // Replace with updated info
                    lines.add(String.format("%s,%s,%s,%s,%s",
                        username, newName, newEmail, newPassword, usertype));
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        // Write back
        try (FileWriter fw = new FileWriter(USER_FILE)) {
            for (String line : lines) {
                fw.write(line + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail_address() { return email_address; }
    public String getPassword() { return password; }
    public String getUsertype() { return usertype; }
    
    // Setters (if needed)
    public void setName(String name) { this.name = name; }
    public void setEmail_address(String email) { this.email_address = email; }
    public void setPassword(String password) { this.password = password; }
}
