import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StudentUI {

    private Student student;
    private Presentation presentation;

    // File format (6 columns):
    // studentID,presentationType,title,description,researchTitle,supervisorName

    public StudentUI(Student student, Presentation presentation) {
        this.student = student;
        this.presentation = presentation;
        StudentPage(this.student, this.presentation);
    }

    public static void StudentPage(Student student, Presentation presentation) {

        // Load from file first (if record exists)
        loadPresentationFromFile(student, presentation);

        MyFrame frame = new MyFrame(750, 600);
        frame.setTitle("Student Dashboard");

        // IMPORTANT: prevents closing this window from exiting the whole program
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setLayout(new BorderLayout());
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // -------------------- LOGOUT BUTTON (ADDED) --------------------
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            frame.dispose();            // close student window
            LoginSignupUI.showLogin();  // back to login screen (same as evaluator/coordinator)
        });

        // Put logout button just below the header, aligned to the right
        JPanel logoutRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutRow.add(logoutBtn);

        // If MyFrame already has a layout, we create a wrapper for header + logout
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.add(new HeaderPanel());
        topWrapper.add(logoutRow);

        // Replace the old header add with this wrapper:
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.add(topWrapper, BorderLayout.NORTH);
        // -------------------- END LOGOUT BUTTON (ADDED) --------------------

        JTextArea studentInfoArea = new JTextArea(student.studentDetails());
        studentInfoArea.setEditable(false);
        studentInfoArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        studentInfoArea.setLineWrap(true);
        studentInfoArea.setWrapStyleWord(true);
        studentInfoArea.setOpaque(false);

        JTextArea presInfoArea = new JTextArea(presentation.presentationDetails());
        presInfoArea.setEditable(false);
        presInfoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        presInfoArea.setLineWrap(true);
        presInfoArea.setWrapStyleWord(true);
        presInfoArea.setOpaque(false);

        JPanel studentBox = new JPanel(new BorderLayout());
        studentBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        studentBox.add(studentInfoArea, BorderLayout.CENTER);

        JPanel presBox = new JPanel(new BorderLayout());
        presBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        presBox.add(presInfoArea, BorderLayout.CENTER);

        JPanel detailsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        detailsRow.add(studentBox);
        detailsRow.add(presBox);
        detailsRow.setPreferredSize(new Dimension(0, 160));

        JButton setResearchTitleBtn = new JButton("Set Research Title");
        JButton setSupervisorBtn = new JButton("Set Supervisor Name");
        JButton viewSubmittedBtn = new JButton("View Submitted Presentation");
        JButton setTypeBtn = new JButton("Set Presentation Type");
        JButton setTitleBtn = new JButton("Set Presentation Title");
        JButton setDescBtn = new JButton("Set Presentation Description");
        JButton uploadBtn = new JButton("Upload Slides");
        JButton deleteSlidesBtn = new JButton("Delete Slides");
        JButton detailBtn = new JButton("View Presentation Details");
        JButton uploadPosterBtn = new JButton("Upload Poster");
        JButton deletePosterBtn = new JButton("Delete Poster");

        JPanel actionPanel = new JPanel(new GridLayout(0, 1, 0, 15));
        actionPanel.add(detailBtn);
        actionPanel.add(setResearchTitleBtn);
        actionPanel.add(setSupervisorBtn);
        actionPanel.add(viewSubmittedBtn);
        actionPanel.add(setTypeBtn);
        actionPanel.add(setTitleBtn);
        actionPanel.add(setDescBtn);

        if (student.getPresentationType().equals("Oral")) {
            actionPanel.add(uploadBtn);
            actionPanel.add(deleteSlidesBtn);
        } else {
            actionPanel.add(uploadPosterBtn);
            actionPanel.add(deletePosterBtn);
        }

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 5),
                        BorderFactory.createEmptyBorder(20, 30, 20, 30)
                )
        );

        centerWrapper.add(Box.createVerticalStrut(30));
        centerWrapper.add(detailsRow);
        centerWrapper.add(Box.createVerticalStrut(20));
        centerWrapper.add(actionPanel);
        centerWrapper.add(Box.createVerticalGlue());

        //scrollable frame
        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null); // keeps your UI clean
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling

        frame.add(scrollPane, BorderLayout.CENTER);

        // ACTIONS
        uploadBtn.addActionListener(e -> uploadFile(frame, presentation));
        uploadPosterBtn.addActionListener(e -> uploadFile(frame, presentation));

        detailBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, presentation.presentationDetails())
        );

        deletePosterBtn.addActionListener(e -> {
            if (presentation.deletePresentation())
                JOptionPane.showMessageDialog(frame, "Presentation file deleted!");
        });

        deleteSlidesBtn.addActionListener(e -> {
            if (presentation.deletePresentation())
                JOptionPane.showMessageDialog(frame, "Presentation file deleted!");
        });

        setTitleBtn.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(
                    frame,
                    "Enter Presentation Title:",
                    presentation.getTitle()
            );

            if (newTitle == null) return;

            newTitle = newTitle.trim();
            if (newTitle.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Title cannot be empty.");
                return;
            }

            presentation.setTitle(newTitle);
            savePresentation(student, presentation);

            presInfoArea.setText(presentation.presentationDetails());
            JOptionPane.showMessageDialog(frame, "Title updated!");
        });

        setDescBtn.addActionListener(e -> {
            JTextArea input = new JTextArea(6, 25);
            input.setLineWrap(true);
            input.setWrapStyleWord(true);
            input.setText(presentation.getDescription());

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    new JScrollPane(input),
                    "Enter Presentation Description",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result != JOptionPane.OK_OPTION) return;

            String newDesc = input.getText().trim();
            if (newDesc.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Description cannot be empty.");
                return;
            }

            presentation.setDescription(newDesc);
            savePresentation(student, presentation);

            presInfoArea.setText(presentation.presentationDetails());
            JOptionPane.showMessageDialog(frame, "Description updated!");
        });

        setTypeBtn.addActionListener(e -> {
            String[] options = {"Oral", "Poster"};
            String choice = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Presentation Type:",
                    "Presentation Type",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    student.getPresentationType()
            );

            if (choice == null) return;

            student.setPresentationType(choice);
            presentation.setPresentationType(choice);
            savePresentation(student, presentation);

            studentInfoArea.setText(student.studentDetails());
            presInfoArea.setText(presentation.presentationDetails());

            JOptionPane.showMessageDialog(frame, "Presentation type updated to: " + choice);

            actionPanel.removeAll();
            actionPanel.add(detailBtn);
            actionPanel.add(setResearchTitleBtn);
            actionPanel.add(setSupervisorBtn);
            actionPanel.add(viewSubmittedBtn);
            actionPanel.add(setTypeBtn);
            actionPanel.add(setTitleBtn);
            actionPanel.add(setDescBtn);

            if (choice.equals("Oral")) {
                actionPanel.add(uploadBtn);
                actionPanel.add(deleteSlidesBtn);
            } else {
                actionPanel.add(uploadPosterBtn);
                actionPanel.add(deletePosterBtn);
            }

            actionPanel.revalidate();
            actionPanel.repaint();
        });

        setResearchTitleBtn.addActionListener(e -> {
            String newResearchTitle = JOptionPane.showInputDialog(
                    frame,
                    "Enter Research Title:",
                    student.getResearchTitle()
            );

            if (newResearchTitle == null) return;

            newResearchTitle = newResearchTitle.trim();
            if (newResearchTitle.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Research title cannot be empty.");
                return;
            }

            student.setResearchTitle(newResearchTitle);
            savePresentation(student, presentation);

            studentInfoArea.setText(student.studentDetails());
            JOptionPane.showMessageDialog(frame, "Research title updated!");
        });

        setSupervisorBtn.addActionListener(e -> {
            String newSupervisor = JOptionPane.showInputDialog(
                    frame,
                    "Enter Supervisor Name:",
                    student.getSupervisorName()
            );

            if (newSupervisor == null) return;

            newSupervisor = newSupervisor.trim();
            if (newSupervisor.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Supervisor name cannot be empty.");
                return;
            }

            student.setSupervisorName(newSupervisor);
            savePresentation(student, presentation);

            studentInfoArea.setText(student.studentDetails());
            JOptionPane.showMessageDialog(frame, "Supervisor name updated!");
        });

        viewSubmittedBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, presentation.presentationDetails())
        );

        frame.setVisible(true);
    }

    private static void loadPresentationFromFile(Student student, Presentation presentation) {
        PresentationRecord r = PresentationRecord.loadByStudentId(student.getId());
        if (r == null) return;

        if (!r.getType().isEmpty()) {
            student.setPresentationType(r.getType());
            presentation.setPresentationType(r.getType());
        }
        if (!r.getTitle().isEmpty()) presentation.setTitle(r.getTitle());
        if (!r.getDescription().isEmpty()) presentation.setDescription(r.getDescription());
        if (!r.getResearchTitle().isEmpty()) student.setResearchTitle(r.getResearchTitle());
        if (!r.getSupervisorName().isEmpty()) student.setSupervisorName(r.getSupervisorName());
    }

    private static void uploadFile(JFrame frame, Presentation presentation) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(frame);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(frame, "Invalid file.");
            return;
        }

        boolean ok = presentation.uploadPresentation(file);
        JOptionPane.showMessageDialog(frame, ok ? "File uploaded!" : "Upload failed.");
    }

    private static void savePresentation(Student student, Presentation presentation) {
        PresentationRecord r = new PresentationRecord(
                student.getId(),
                presentation.getPresentationType(),
                presentation.getTitle(),
                presentation.getDescription(),
                student.getResearchTitle(),
                student.getSupervisorName()
        );

        if (!PresentationRecord.upsert(r)) {
            JOptionPane.showMessageDialog(null, "Failed to save presentation details.");
        }
    }
}
