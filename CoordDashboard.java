import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

public class CoordDashboard {
    private JFrame frame;
    private JPanel cards;
    private String username;
    private String coordID;
    
    // For create events
    private DefaultTableModel assignTableModel;
    // For reports
    private DefaultListModel<String> reportListModel;
    private JTable assignTable;
    private JList<String> reportList;
    private JTextArea reportTextArea;

    static final String SESSION_FILE = "sessions.txt";
    private File reportsDir = new File("reports");
    private File currentReportFile = null;
    private boolean reportIncomplete = false;

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
        JButton reportBtn = new JButton("Reports");
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
            loadAssignments(assignTableModel); // reload every time
        }
    }

    // ---------------- PANELS ----------------
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Main Menu"));

        panel.add(new JLabel("WELCOME " + username + "! YOUR ROLE IS COORDINATOR")); 

        return panel;
    }

    // ------------------------CREATE SESSION--------------------------------------
    private JPanel createSessionPanel() { 
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

            String[] evaluatorOptions = loadUserDisplayByRole("Evaluator"); // "asyraf (EV113)"
            JList<String> list = new JList<>(evaluatorOptions);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    new JScrollPane(list),
                    "Select Evaluators",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                java.util.List<String> ids = new java.util.ArrayList<>();
                for (String s : list.getSelectedValuesList()) {
                    ids.add(extractIdFromDisplay(s));
                }

                // use ';' to avoid CSV conflict
                String evalIds = String.join(";", ids);
                assignTableModel.setValueAt(evalIds, selectedRow, 2);

                saveAssignments(assignTable); // autosave
            }
        });

        setPresenterBtn.addActionListener(e -> {
            int selectedRow = assignTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session first!");
                return;
            }

            String[] studentOptions = loadUserDisplayByRole("Student"); // "kim (STU009)"
            String selected = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Student Presenter:",
                    "Student Presenter",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    studentOptions,
                    null
            );

            if (selected != null) {
                String studentId = extractIdFromDisplay(selected);
                assignTableModel.setValueAt(studentId, selectedRow, 3);

                saveAssignments(assignTable); // autosave
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
                if (evaluators == null) evaluators = "";

                String presenter = (String) model.getValueAt(i, 3);
                if (presenter == null) presenter = "";

                fw.write(sessionID + "," + evaluators + "," + presenter + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] loadUserDisplayByRole(String role) {
        java.util.List<String> list = new java.util.ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(LoginSignupUI.FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                String username = data[0].trim();
                String r = data[2].trim();
                String id = data[3].trim();

                if (r.equalsIgnoreCase(role) && !id.isEmpty()) {
                    list.add(username + " (" + id + ")");
                }
            }
        } catch (IOException ignored) {}

        return list.toArray(new String[0]);
    }

    private static String extractIdFromDisplay(String s) {
        int l = s.lastIndexOf('(');
        int r = s.lastIndexOf(')');
        if (l == -1 || r == -1 || r <= l) return s.trim();
        return s.substring(l + 1, r).trim();
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
// ---------------------------------------------------------END ASSIGN PANEL --------------------------------------------


    // --------------------------------------- CREATE REPORT PANEL -----------------------------------------------------
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Schedules & Reports"));

        if (!reportsDir.exists()) reportsDir.mkdirs();

        // Left: report list
        reportListModel = new DefaultListModel<>();
        reportList = new JList<>(reportListModel);
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(reportList);
        listScroll.setPreferredSize(new Dimension(220, 0));

        // Right: editor
        reportTextArea = new JTextArea();
        reportTextArea.setLineWrap(true);
        reportTextArea.setWrapStyleWord(true);
        JScrollPane editorScroll = new JScrollPane(reportTextArea);

        // Top buttons
        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JButton newBtn = new JButton("New Report");
        JButton saveBtn = new JButton("Save");
        JButton deleteBtn = new JButton("Delete");
        JButton renameBtn = new JButton("Rename");
        topBtns.add(newBtn);
        topBtns.add(saveBtn);
        topBtns.add(deleteBtn);
        topBtns.add(renameBtn);

        panel.add(topBtns, BorderLayout.NORTH);
        panel.add(listScroll, BorderLayout.WEST);
        panel.add(editorScroll, BorderLayout.CENTER);

        refreshReportList();

        // Mark dirty when user edits
        reportTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                reportIncomplete = true;
            }
        });

        // Open selected report
        reportList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String selected = reportList.getSelectedValue();
            if (selected == null) return;

            if (reportIncomplete && currentReportFile != null) {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "You have unsaved changes. Discard them and open another report?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) {
                    if (currentReportFile != null) {
                        reportList.setSelectedValue(currentReportFile.getName(), true);
                    }
                    return;
                }
            }

            currentReportFile = new File(reportsDir, selected);
            reportTextArea.setText(readTextFile(currentReportFile));
            reportIncomplete = false;
        });

        // New report
        newBtn.addActionListener(e -> {
            if (reportIncomplete && currentReportFile != null) {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "You have unsaved changes. Continue and lose them?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) return;
            }

            String name = JOptionPane.showInputDialog(frame, "Enter report name:");
            if (name == null) return;
            name = name.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Report name cannot be empty.");
                return;
            }

            String fileName = makeSafeFileName(name) + ".txt";
            File f = new File(reportsDir, fileName);

            if (f.exists()) {
                JOptionPane.showMessageDialog(frame, "A report with that name already exists.");
                return;
            }

            writeTextFile(f, "");
            refreshReportList();
            reportList.setSelectedValue(fileName, true);
            reportTextArea.requestFocus();
            reportIncomplete = false;
        });

        // Save report
        saveBtn.addActionListener(e -> {
            if (currentReportFile == null) {
                JOptionPane.showMessageDialog(frame, "Select a report first (or create a new one).");
                return;
            }
            writeTextFile(currentReportFile, reportTextArea.getText());
            reportIncomplete = false;
            JOptionPane.showMessageDialog(frame, "Report saved.");
        });

        // Delete report
        deleteBtn.addActionListener(e -> {
            if (currentReportFile == null) {
                JOptionPane.showMessageDialog(frame, "Select a report to delete.");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Delete this report permanently?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice != JOptionPane.YES_OPTION) return;

            boolean ok = currentReportFile.delete();
            if (!ok) {
                JOptionPane.showMessageDialog(frame, "Failed to delete report.");
                return;
            }

            currentReportFile = null;
            reportTextArea.setText("");
            reportIncomplete = false;
            refreshReportList();
        });

        // Rename report
        renameBtn.addActionListener(e -> {
            if (currentReportFile == null) {
                JOptionPane.showMessageDialog(frame, "Select a report to rename.");
                return;
            }

            String newName = JOptionPane.showInputDialog(frame, "Enter new report name:");
            if (newName == null) return;
            newName = newName.trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name cannot be empty.");
                return;
            }

            String newFileName = makeSafeFileName(newName) + ".txt";
            File newFile = new File(reportsDir, newFileName);

            if (newFile.exists()) {
                JOptionPane.showMessageDialog(frame, "A report with that name already exists.");
                return;
            }

            boolean ok = currentReportFile.renameTo(newFile);
            if (!ok) {
                JOptionPane.showMessageDialog(frame, "Failed to rename report.");
                return;
            }

            currentReportFile = newFile;
            refreshReportList();
            reportList.setSelectedValue(newFileName, true);
        });

        return panel;
    }
    private void refreshReportList() {
        reportListModel.clear();
        if (!reportsDir.exists()) reportsDir.mkdirs();

        File[] files = reportsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });

        if (files == null) return;

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        for (File f : files) {
            reportListModel.addElement(f.getName());
        }
    }

    private String readTextFile(File f) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            // ignore
        }
        return sb.toString();
    }

    private void writeTextFile(File f, String text) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(text);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save report.");
        }
    }

    private String makeSafeFileName(String name) {
        String safe = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        safe = safe.replaceAll("\\s+", "_");
        return safe;
    }
    // ---------------------------------- END REPORT CLASS ----------------------------------------------------------------------

    private JPanel createAwardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Award Delegation"));

        JTextArea output = new JTextArea(14, 45);
        output.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Load Evaluations & Pick Winners");
        JButton saveBtn = new JButton("Save Winners");
        top.add(loadBtn);
        top.add(saveBtn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("People's Choice (manual):"));
        JComboBox<String> choiceBox = new JComboBox<>();
        JButton setChoiceBtn = new JButton("Set People's Choice");
        bottom.add(choiceBox);
        bottom.add(setChoiceBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        final SimpleWinner[] bestOral = new SimpleWinner[1];
        final SimpleWinner[] bestPoster = new SimpleWinner[1];
        final SimpleWinner[] peopleChoice = new SimpleWinner[1];

        loadBtn.addActionListener(e -> {
            bestOral[0] = null;
            bestPoster[0] = null;
            peopleChoice[0] = null;

            // Maps for displaying username
            java.util.Map<String, String> idToUser = loadIdToUsernameMap(LoginSignupUI.FILE_NAME);

            // Build sessionId -> type map from sessions.txt
            java.util.Map<String, String> sessionType = loadSessionTypeMap(SESSION_FILE);

            // Fill dropdown with evaluated students (unique IDs)
            java.util.List<String> evaluatedStudentIds = loadEvaluatedStudentIds("evaluations.txt");
            choiceBox.removeAllItems();
            for (String sid : evaluatedStudentIds) {
                String uname = idToUser.getOrDefault(sid, "unknown");
                choiceBox.addItem(uname + " (" + sid + ")");
            }

            // Pick best oral & best poster (highest total mark)
            SimpleWinner oral = pickBestByType("evaluations.txt", "Oral", sessionType, idToUser);
            SimpleWinner poster = pickBestByType("evaluations.txt", "Poster", sessionType, idToUser);

            bestOral[0] = oral;
            bestPoster[0] = poster;

            output.setText("");
            output.append("Best Oral Presentation:\n" + (oral == null ? "None\n" : oral.toDisplay()) + "\n");
            output.append("Best Poster:\n" + (poster == null ? "None\n" : poster.toDisplay()) + "\n");
            output.append("People's Choice:\nNot set (manual)\n");
        });

        setChoiceBtn.addActionListener(e -> {
            int idx = choiceBox.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(frame, "Select a student first.");
                return;
            }

            // Extract ID from "username (STUxxx)"
            String item = (String) choiceBox.getSelectedItem();
            String sid = extractIdFromCombo(item);

            java.util.Map<String, String> idToUser = loadIdToUsernameMap(LoginSignupUI.FILE_NAME);
            String uname = idToUser.getOrDefault(sid, "unknown");

            SimpleWinner pc = new SimpleWinner("People's Choice Award", "-", sid, uname, 0);
            peopleChoice[0] = pc;

            output.append("\nPeople's Choice set to:\n" + pc.toDisplay() + "\n");
        });

        saveBtn.addActionListener(e -> {
            if (bestOral[0] == null && bestPoster[0] == null) {
                JOptionPane.showMessageDialog(frame, "Load evaluations first.");
                return;
            }
            if (peopleChoice[0] == null) {
                JOptionPane.showMessageDialog(frame, "Set People's Choice manually first.");
                return;
            }

            boolean ok = saveSimpleWinners("awards.txt", bestOral[0], bestPoster[0], peopleChoice[0]);
            JOptionPane.showMessageDialog(frame, ok ? "Saved to awards.txt" : "Failed to save.");
        });

        return panel;
    }

    private static class SimpleWinner {
        String award;
        String sessionId;
        String studentId;
        String username;
        int mark;

        SimpleWinner(String award, String sessionId, String studentId, String username, int mark) {
            this.award = award;
            this.sessionId = sessionId;
            this.studentId = studentId;
            this.username = username;
            this.mark = mark;
        }

        String toDisplay() {
            return "Winner: " + username + " (" + studentId + ")\n"
                + "Session: " + sessionId + "\n"
                + "Total Mark: " + mark;
        }
    }

    private SimpleWinner pickBestByType(String evalFile, String wantedType, java.util.Map<String, String> sessionType, 
        java.util.Map<String, String> idToUser) {

        SimpleWinner best = null;

        try (BufferedReader br = new BufferedReader(new FileReader(evalFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                String sessionId = data[0].trim();
                String studentId = data[1].trim();
                if (studentId.isEmpty()) continue;

                String type = sessionType.get(sessionId);
                if (type == null) continue;

                if (!type.equalsIgnoreCase(wantedType)) continue;

                int total = parseIntSafe(data[3], -1);
                if (total < 0) continue;

                if (best == null || total > best.mark) {
                    String uname = idToUser.getOrDefault(studentId, "unknown");
                    best = new SimpleWinner("Best " + wantedType, sessionId, studentId, uname, total);
                }
            }
        } catch (IOException ignored) {}

        return best;
    }

    private java.util.List<String> loadEvaluatedStudentIds(String evalFile) {
        java.util.Set<String> set = new java.util.LinkedHashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(evalFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 2) continue;
                String studentId = data[1].trim();
                if (!studentId.isEmpty()) set.add(studentId);
            }
        } catch (IOException ignored) {}

        return new java.util.ArrayList<>(set);
    }

    private String extractIdFromCombo(String item) {
        // expects "username (STUXXX)"
        int l = item.lastIndexOf('(');
        int r = item.lastIndexOf(')');
        if (l == -1 || r == -1 || r <= l) return item;
        return item.substring(l + 1, r).trim();
    }

    private int parseIntSafe(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private boolean saveSimpleWinners(String fileName, SimpleWinner oral, SimpleWinner poster, SimpleWinner choice) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            writeSimpleLine(bw, oral);
            writeSimpleLine(bw, poster);
            writeSimpleLine(bw, choice);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void writeSimpleLine(BufferedWriter bw, SimpleWinner w) throws IOException {
        if (w == null) return;

        // award,sessionId,studentId,username,mark
        bw.write(w.award + "," + w.sessionId + "," + w.studentId + "," + w.username + "," + w.mark);
        bw.newLine();
    }

    private java.util.Map<String, String> loadIdToUsernameMap(String usersFileName) {
        java.util.Map<String, String> map = new java.util.HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(usersFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                String username = data[0].trim();
                String id = data[3].trim();

                if (!id.isEmpty()) map.put(id, username);
            }
        } catch (IOException ignored) {}

        return map;
    }

    private java.util.Map<String, String> loadSessionTypeMap(String sessionsFileName) {
        java.util.Map<String, String> map = new java.util.HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(sessionsFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 7) continue;

                String sessionId = data[0].trim();
                String type = data[6].trim();
                map.put(sessionId, type);
            }
        } catch (IOException ignored) {}

        return map;
    }

}
