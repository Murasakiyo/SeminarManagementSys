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
    static final String PRESENTATION_FILE = "presentations.txt";

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

        frame.add(new HeaderPanel(), BorderLayout.NORTH);

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

        // You haven't implemented where the submitted file path is stored,
        // so for now this button just shows the details again.
        viewSubmittedBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, presentation.presentationDetails())
        );

        frame.setVisible(true);
    }

    private static void loadPresentationFromFile(Student student, Presentation presentation) {
        File f = new File(PRESENTATION_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;

                String id = p[0].trim();
                if (!id.equals(student.getId().trim())) continue;

                // Supports both old (4 columns) and new (6 columns) formats:
                // id,type,title,desc[,researchTitle,supervisorName]
                String type = p[1].trim();
                String title = p[2].trim();
                String desc = p[3].trim();

                if (!type.isEmpty()) {
                    student.setPresentationType(type);
                    presentation.setPresentationType(type);
                }
                if (!title.isEmpty()) presentation.setTitle(title);
                if (!desc.isEmpty()) presentation.setDescription(desc);

                if (p.length >= 6) {
                    String researchTitle = p[4].trim();
                    String supervisor = p[5].trim();
                    if (!researchTitle.isEmpty()) student.setResearchTitle(researchTitle);
                    if (!supervisor.isEmpty()) student.setSupervisorName(supervisor);
                }

                return;
            }
        } catch (IOException ignored) {}
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
        File inputFile = new File(PRESENTATION_FILE);
        File tempFile = new File("temp_presentations.txt");
        boolean found = false;

        try (
                BufferedReader br = inputFile.exists() ? new BufferedReader(new FileReader(inputFile)) : null;
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))
        ) {
            if (br != null) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 1 && parts[0].trim().equals(student.getId().trim())) {
                        // Always write the new 6-column format for this student
                        bw.write(buildLine(student, presentation));
                        bw.newLine();
                        found = true;
                    } else {
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }

            if (!found) {
                bw.write(buildLine(student, presentation));
                bw.newLine();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save presentation details.");
            return;
        }

        if (inputFile.exists()) inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private static String buildLine(Student student, Presentation presentation) {
        return safe(student.getId()) + "," +
                safe(presentation.getPresentationType()) + "," +
                safe(presentation.getTitle()) + "," +
                safe(presentation.getDescription()) + "," +
                safe(student.getResearchTitle()) + "," +
                safe(student.getSupervisorName());
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }
}