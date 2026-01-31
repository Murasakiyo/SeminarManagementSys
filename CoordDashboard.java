import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.*;

public class CoordDashboard {
    private JFrame frame;
    private JPanel cards;
    private String username;
    private String coordID;
    private DefaultTableModel assignTableModel;
    private JTable assignTable;
    static final String SESSION_FILE = "sessions.txt";

    public static void main(String[] args) {
        new CoordDashboard("Dan", "COORD12");
    }

    public CoordDashboard(String username, String coordID) {
        this.username = username;
        this.coordID = coordID;
        frame = new MyFrame(750, 600);
        frame.setTitle("Coordinator Dashboard");
        frame.setLayout(new BorderLayout());

        // HEADER --------------------------------------------------------------------------------
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // ----- SIDEBAR -----
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1, 0, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        sidebar.setBackground(new Color(230, 230, 230));

        JButton homeBtn = new JButton("Main");
        JButton sessionBtn = new JButton("Manage Sessions");
        JButton assignBtn = new JButton("Assign Roles");
        JButton reportBtn = new JButton("Schedules & Reports");
        JButton awardBtn = new JButton("Awards");

        sidebar.add(homeBtn);
        sidebar.add(sessionBtn);
        sidebar.add(assignBtn);
        sidebar.add(reportBtn);
        sidebar.add(awardBtn);

        frame.add(sidebar, BorderLayout.WEST);

        // ----- MAIN CONTENT (CARDS) -----
        cards = new JPanel(new CardLayout());

        cards.add(createMainPanel(), "main");
        cards.add(createSessionPanel(), "sessions");
        cards.add(createAssignPanel(), "assign");
        cards.add(createReportPanel(), "reports");
        cards.add(createAwardPanel(), "awards");

        frame.add(cards, BorderLayout.CENTER);

        // ----- BUTTON ACTIONS -----
        homeBtn.addActionListener(e -> showCard("main"));
        sessionBtn.addActionListener(e -> showCard("sessions"));
        assignBtn.addActionListener(e -> showCard("assign"));
        reportBtn.addActionListener(e -> showCard("reports"));
        awardBtn.addActionListener(e -> showCard("awards"));

        frame.setVisible(true);
    }

    private void showCard(String name) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, name);

        if (name.equals("assign")) {
            loadAssignments(assignTableModel); // ✅ reload EVERY time
        }
    }

    // ---------------- PANELS ----------------
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Main Menu"));

        panel.add(new JLabel("WELCOME " + username + "! YOUR ROLE IS COORDINATOR")); 

        return panel;
    }

    private JPanel createSessionPanel() { // --------------------CREATE SESSION--------------------------------------
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Create Seminar Session"));

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Host
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Session Host:"), gbc);

        gbc.gridx = 1;
        JTextField hostField = new JTextField(username);
        hostField.setEditable(false);
        hostField.setBackground(Color.LIGHT_GRAY);
        form.add(hostField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Date (DD-MM-YYYY):"), gbc);

        gbc.gridx = 1;
        JTextField dateField = new JTextField(10);
        form.add(dateField, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Time Slot:"), gbc);

        gbc.gridx = 1;
        JTextField startField = new JTextField(5);
        JTextField endField = new JTextField(5);
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.add(startField);
        timePanel.add(new JLabel("to"));
        timePanel.add(endField);
        form.add(timePanel, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        JTextField locationField = new JTextField(15);
        form.add(locationField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Presentation Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeBox = new JComboBox<>(
                new String[]{"Oral", "Poster"}
        );
        form.add(typeBox, gbc);

        // Button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton createBtn = new JButton("Create Session");
        form.add(createBtn, gbc);
        panel.add(form, BorderLayout.NORTH);
        

        // ---------- SESSION TABLE ----------
        String[] columnNames = {
            "Session ID",
            "Coordinator Name",
            "Date",
            "Time",
            "Location",
            "Presentation Type"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is read-only
            }
        };

        JTable sessionTable = new JTable(tableModel);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.setRowHeight(22);

        // Load data
        loadSessionsIntoTable(tableModel);

        // Title
        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableTitle = new JLabel("Existing Sessions");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 13));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(sessionTable), BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        // ---------- DELETE ----------
        JButton deleteBtn = new JButton("Delete Selected Session");
        panel.add(deleteBtn, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        createBtn.addActionListener(e -> {
            String start = startField.getText();
            String end = endField.getText();
            String location = locationField.getText();
            String type = (String) typeBox.getSelectedItem();
            String date = dateField.getText();
            if (start.isEmpty() || end.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields");
                return;
            }

            saveSession(username, date, start, end, location, type);
            loadSessionsIntoTable(tableModel); // refresh table

            dateField.setText("");
            startField.setText("");
            endField.setText("");
            locationField.setText("");
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session to delete");
                return;
            }

            deleteSession(selectedRow);
            loadSessionsIntoTable(tableModel);
        });

    return panel;
    }

    static String generateSessionID() {
        int maxID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 1) continue;

                String id = data[0]; // first column is sessionID
                if (id.startsWith("SES")) {
                    try {
                        int num = Integer.parseInt(id.substring(3));
                        if (num > maxID) maxID = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {}
        // return next ID, padded to 3 digits
        return "SES" + String.format("%03d", maxID + 1);
    }


    public static void loadSessionsIntoTable(DefaultTableModel model) {
        model.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;

                model.addRow(new Object[]{
                        data[0],                   // Session ID
                        data[1],                   // Host Name
                        data[2],                   // Date
                        data[3] + " - " + data[4], // Time
                        data[5],                   // Location
                        data[6]                    // Type
                });
            }
        } catch (IOException ignored) {}
    }

    public static void saveSession(String hostName, String date, String start, String end, String location, String type) {
        String sessionID = generateSessionID(); // auto-generate ID
        try (FileWriter fw = new FileWriter(SESSION_FILE, true)) {
            fw.write(sessionID + "," + hostName + "," + date + "," + start + "," + end + "," +
                    location + "," + type + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadSessions(DefaultListModel<String> model) {
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    model.addElement(
                            data[0] + "   |   " + data[1] + " - " + data[2] +
                            "   |   " + data[3] + "   |   " + data[4]
                    );
                }
            }
        } catch (IOException ignored) {}
    }

    static void deleteSession(int indexToDelete) {
        try {
            File inputFile = new File(SESSION_FILE);
            File tempFile = new File("temp_sessions.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                if (index != indexToDelete) {
                    writer.write(line);
                    writer.newLine();
                }
                index++;
            }
            reader.close();
            writer.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    } // ------------------------------------------------- CREATE SESSION END -----------------------------------------------

    private JPanel createAssignPanel() { // -------------------------ASSIGN EVALUATORS AND STUDENTS--------------------------
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assign Evaluators & Student Presenter"));

        String[] columnNames = {"Session ID", "Date", "Evaluators", "Student Presenter"};

        assignTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        assignTable = new JTable(assignTableModel);
        assignTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignTable.setRowHeight(25);

        panel.add(new JScrollPane(assignTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addEvaluatorBtn = new JButton("Add Evaluator(s)");
        JButton setPresenterBtn = new JButton("Set Student Presenter");

        buttonPanel.add(addEvaluatorBtn);
        buttonPanel.add(setPresenterBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------

        addEvaluatorBtn.addActionListener(e -> {
            int selectedRow = assignTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session first!");
                return;
            }

            String[] evaluatorsList = loadUsersByRole("Evaluator");
            JList<String> evaluatorJList = new JList<>(evaluatorsList);
            evaluatorJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            int result = JOptionPane.showConfirmDialog(frame,
                    new JScrollPane(evaluatorJList),
                    "Select Evaluators",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String evalStr = String.join(",", evaluatorJList.getSelectedValuesList());
                assignTableModel.setValueAt(evalStr, selectedRow, 2);
                saveAssignments(assignTable);
            }
        });

        setPresenterBtn.addActionListener(e -> {
            int selectedRow = assignTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session first!");
                return;
            }

            String[] studentList = loadUsersByRole("Student");
            String selectedStudent = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Student Presenter:",
                    "Student Presenter",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    studentList,
                    assignTableModel.getValueAt(selectedRow, 3)
            );

            if (selectedStudent != null) {
                assignTableModel.setValueAt(selectedStudent, selectedRow, 3);
                saveAssignments(assignTable);
            }
        });

        return panel;
    }

    private void saveAssignments(JTable table) {
        try (FileWriter fw = new FileWriter("assignments.txt")) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String sessionID = (String) model.getValueAt(i, 0);
                String evaluators = (String) model.getValueAt(i, 2);
                String presenter = (String) model.getValueAt(i, 3);

                fw.write(sessionID + "," + evaluators + "," + presenter + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadAssignments(DefaultTableModel model) {
        model.setRowCount(0); // clear table
        // Load session info from sessions.txt
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;
                String sessionID = data[0];
                String date = data[2];

                // Load existing assignments if available
                String evaluators = "";
                String presenter = "";
                File assignFile = new File("assignments.txt");
                if (assignFile.exists()) {
                    try (BufferedReader abr = new BufferedReader(new FileReader(assignFile))) {
                        String aLine;
                        while ((aLine = abr.readLine()) != null) {
                            String[] aData = aLine.split(",", -1); // sessionID,evaluators,presenter
                            if (aData.length >= 3 && aData[0].equals(sessionID)) {
                                evaluators = aData[1];
                                presenter = aData[2];
                            }
                        }
                    }
                }
                model.addRow(new Object[]{sessionID, date, evaluators, presenter});
            }
        } catch (IOException ignored) {}
    }

    private static String[] loadUsersByRole(String role) {
        java.util.List<String> list = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LoginSignupUI.FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4 && data[2].equalsIgnoreCase(role)) {
                    list.add(data[0]); // username
                }
            }
        } catch (IOException ignored) {}
        return list.toArray(new String[0]);
    }// ---------------------------------------------------------END ASSIGN PANEL --------------------------------------------


    // ------------------------------- CREATE REPORT PANEL ----------------------------------------
    private JPanel createReportPanel() { 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Schedules & Reports"));

        JTextArea reportArea = new JTextArea();
        reportArea.setText("• Seminar Schedule\n• Evaluation Results\n• Final Reports");

        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAwardPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Award Nominations"));

        panel.add(new JCheckBox("Best Oral Presentation"));
        panel.add(new JCheckBox("Best Poster"));
        panel.add(new JCheckBox("People's Choice Award"));

        return panel;
    }

}
