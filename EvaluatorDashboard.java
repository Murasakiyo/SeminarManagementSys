import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluatorDashboard {
    private JFrame frame;
    private JPanel cards;
    private String username;
    private String evaluatorID;
    
    // File constants
    static final String SESSION_FILE = "sessions.txt";
    static final String ASSIGNMENT_FILE = "assignments.txt";
    static final String EVALUATION_FILE = "evaluations.txt";
    static final String USER_FILE = "users.txt";

    public EvaluatorDashboard(String username, String evaluatorID) {
        this.username = username;
        this.evaluatorID = evaluatorID;
        
        // Consistent window size with CoordDashboard
        frame = new MyFrame(750, 600);
        frame.setTitle("Evaluator Dashboard");
        frame.setLayout(new BorderLayout());

        // HEADER
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1, 0, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        sidebar.setBackground(new Color(230, 230, 230));

        JButton homeBtn = new JButton("Home");
        JButton sessionBtn = new JButton("My Sessions");
        JButton evaluateBtn = new JButton("Evaluations");
        JButton logoutBtn = new JButton("Log Out");

        sidebar.add(homeBtn);
        sidebar.add(sessionBtn);
        sidebar.add(evaluateBtn);
        sidebar.add(logoutBtn);
        // spacer
        sidebar.add(new JLabel()); 

        frame.add(sidebar, BorderLayout.WEST);

        // CARDS
        cards = new JPanel(new CardLayout());
        cards.add(createHomePanel(), "home");
        cards.add(createSessionPanel(), "sessions");
        cards.add(createEvaluationPanel(), "evaluations");

        frame.add(cards, BorderLayout.CENTER);

        // ACTIONS
        homeBtn.addActionListener(e -> showCard("home"));
        sessionBtn.addActionListener(e -> showCard("sessions"));
        evaluateBtn.addActionListener(e -> showCard("evaluations"));
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            LoginSignupUI.showLogin();
        });

        frame.setVisible(true);
    }

    private void showCard(String name) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, name);
    }

    // --- PANELS ---

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Home"));
        
        JLabel welcome = new JLabel("Welcome, Evaluator " + username + "!");
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        
        panel.add(welcome);
        return panel;
    }

    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assigned Sessions"));

        String[] columnNames = {"Session ID", "Date", "Presenter", "Location", "Presentation Type"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setEnabled(false); // Read-only

        loadAssignedSessions(model);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAssignedSessions(model));
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEvaluationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Evaluations"));

        // Top: Form to add evaluation
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("New Evaluation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Session Selector
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Session:"), gbc);
        
        JComboBox<String> sessionBox = new JComboBox<>();
        gbc.gridx = 1;
        form.add(sessionBox, gbc);

        // Presenter (auto-filled based on session)
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Student:"), gbc);
        JTextField studentField = new JTextField(15);
        studentField.setEditable(false);
        gbc.gridx = 1;
        form.add(studentField, gbc);

        // Populate sessions for this evaluator
        loadSessionDropdown(sessionBox, studentField);
        
        sessionBox.addActionListener(e -> {
            String selected = (String) sessionBox.getSelectedItem();
            if (selected != null) {
                // Format: "SES001 (Date)"
                String id = selected.split(" ")[0];
                String presenter = getPresenterForSession(id);
                studentField.setText(presenter);
            }
        });

        // Marks
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Research Quality (0-20):"), gbc);
        JTextField rqField = new JTextField(5); gbc.gridx = 1; form.add(rqField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Result (0-20):"), gbc);
        JTextField resField = new JTextField(5); gbc.gridx = 1; form.add(resField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Methodology (0-20):"), gbc);
        JTextField methField = new JTextField(5); gbc.gridx = 1; form.add(methField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Presentation (0-20):"), gbc);
        JTextField presField = new JTextField(5); gbc.gridx = 1; form.add(presField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        form.add(new JLabel("Originality (0-20):"), gbc);
        JTextField origField = new JTextField(5); gbc.gridx = 1; form.add(origField, gbc);

        // Comments
        gbc.gridx = 0; gbc.gridy = 7;
        form.add(new JLabel("Comments:"), gbc);
        JTextArea commentsArea = new JTextArea(3, 20);
        commentsArea.setLineWrap(true);
        gbc.gridx = 1;
        form.add(new JScrollPane(commentsArea), gbc);

        // Submit Button
        JButton submitBtn = new JButton("Submit Evaluation");
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        form.add(submitBtn, gbc);

        panel.add(form, BorderLayout.NORTH);

        // Center: Table of past evaluations
        String[] columns = {"Session", "Student", "Evaluator", "Final Mark", "Comments"};
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0);
        JTable historyTable = new JTable(historyModel);
        loadEvaluationHistory(historyModel);
        
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // Action
        submitBtn.addActionListener(e -> {
            String sessionStr = (String) sessionBox.getSelectedItem();
            if (sessionStr == null) return;
            String sessionID = sessionStr.split(" ")[0];
            String student = studentField.getText();
            
            try {
                int rq = Integer.parseInt(rqField.getText());
                int res = Integer.parseInt(resField.getText());
                int meth = Integer.parseInt(methField.getText());
                int pres = Integer.parseInt(presField.getText());
                int orig = Integer.parseInt(origField.getText());
                String comments = commentsArea.getText().replace(",", " "); // simplistic csv escape

                int finalMark = rq + res + meth + pres + orig;
                
                // Save
                saveEvaluation(sessionID, student, username, finalMark, rq, res, meth, pres, orig, comments);
                
                // Refresh
                loadEvaluationHistory(historyModel);
                JOptionPane.showMessageDialog(frame, "Evaluation Submitted! Final Mark: " + finalMark);
                
                // Clear fields
                rqField.setText(""); resField.setText(""); methField.setText(""); presField.setText(""); origField.setText(""); commentsArea.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for marks.");
            }
        });

        return panel;
    }

    // --- DATA HELPERS ---

    private void loadAssignedSessions(DefaultTableModel model) {
        model.setRowCount(0);
        // 1. Get List of Session IDs assigned to this evaluator from assignments.txt
        List<String> assignedIDs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ASSIGNMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    // Evaluators are comma separated string at index 1?
                    // "SES001,eval1,eval2,student1" -> No, coord dashboard saves as "sessionID,evaluators,presenter"
                    // evaluators is "user1,val2"
                    String sessionID = parts[0];
                    String evaluators = parts[1]; 
                    if (evaluators.contains(username)) { // Simple check, ideally check split
                         // better: check if username is in the list
                         String[] evals = evaluators.split(",");
                         for (String ev : evals) {
                             if (ev.trim().equals(username)) {
                                 assignedIDs.add(sessionID);
                                 break;
                             }
                         }
                    }
                }
            }
        } catch (IOException ignored) {}

        // 2. Load details from sessions.txt
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;
                String sid = data[0];
                
                if (assignedIDs.contains(sid)) {
                    // Need presenter name from assignments... reuse helper?
                    // Actually let's just fetch it again or store it map.
                    String presenter = getPresenterForSession(sid);
                            
                    model.addRow(new Object[]{
                        sid,
                        data[2], // Date
                        presenter,
                        data[5], // Location
                        data[6]  // Type
                    });
                }
            }
        } catch (IOException ignored) {}
    }

    private String getPresenterForSession(String sessionID) {
        try (BufferedReader br = new BufferedReader(new FileReader(ASSIGNMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3 && parts[0].equals(sessionID)) {
                    return parts[2]; // Presenter
                }
            }
        } catch (IOException ignored) {}
        return "N/A";
    }

    private void loadSessionDropdown(JComboBox<String> box, JTextField studentField) {
        box.removeAllItems();
        // Load assigned sessions similar to table
         List<String> assignedIDs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ASSIGNMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                     String sessionID = parts[0];
                    String evaluators = parts[1];
                     String[] evals = evaluators.split(",");
                     for (String ev : evals) {
                         if (ev.trim().equals(username)) {
                             assignedIDs.add(sessionID);
                             break;
                         }
                     }
                }
            }
        } catch (IOException ignored) {}
        
        for (String sess : assignedIDs) {
            box.addItem(sess); 
        }
    }

    private void saveEvaluation(String sessionID, String student, String evaluator, int finalMark, 
                              int rq, int res, int meth, int pres, int orig, String comments) {
        // Format: SessionID,StudentName,EvaluatorName,FinalMark,ResearchQ,Result,Methodology,Presentation,Originality,Comments
        try (FileWriter fw = new FileWriter(EVALUATION_FILE, true)) {
            fw.write(String.format("%s,%s,%s,%d,%d,%d,%d,%d,%d,%s\n",
                    sessionID, student, evaluator, finalMark, rq, res, meth, pres, orig, comments));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEvaluationHistory(DefaultTableModel model) {
        model.setRowCount(0);
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 10) continue;
                
                // Filter by evaluator? Or show all? Usually evaluator sees only their own.
                // Or maybe they can see all for the session. Let's show only their own for now.
                if (parts[2].equals(username)) {
                    model.addRow(new Object[]{
                        parts[0], // Session
                        parts[1], // Student
                        parts[2], // Evaluator
                        parts[3], // Final Mark
                        parts[9]  // Comments
                    });
                }
            }
        } catch (IOException ignored) {}
    }
}
