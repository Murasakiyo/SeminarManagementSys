import javax.swing.*;
import java.awt.*;
import java.io.*;

public class StudentUI {

    private Student student;
    private Presentation presentation;

    // File format (7 columns):
    // studentID,presentationType,title,description,researchTitle,supervisorName,presentationPath
    static final String PRESENTATION_FILE = "presentations.txt";

    public StudentUI(Student student, Presentation presentation) {
        this.student = student;
        this.presentation = presentation;
        StudentPage(this.student, this.presentation);
    }

    public static void StudentPage(Student student, Presentation presentation) {

        // Load submitted record (if any)
        loadPresentationFromFile(student, presentation);

        MyFrame frame = new MyFrame(750, 600);
        frame.setTitle("Student Dashboard");
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // ---------- TEXT AREAS ----------
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

        // ---------- BUTTONS ----------
        JButton logoutBtn = new JButton("Logout");

        JButton registerBtn = new JButton("Register / Submit");
        JButton removeSubmittedBtn = new JButton("Remove Submitted Presentation");

        JButton detailBtn = new JButton("View Current Details");
        JButton viewSubmittedBtn = new JButton("View Submitted Presentation");

        JButton setTypeBtn = new JButton("Set Presentation Type");
        JButton setTitleBtn = new JButton("Set Presentation Title");
        JButton setDescBtn = new JButton("Set Presentation Description");

        JButton setResearchTitleBtn = new JButton("Set Research Title");
        JButton setSupervisorBtn = new JButton("Set Supervisor Name");

        JButton uploadBtn = new JButton("Upload Slides");
        JButton deleteSlidesBtn = new JButton("Delete Slides");
        JButton uploadPosterBtn = new JButton("Upload Poster");
        JButton deletePosterBtn = new JButton("Delete Poster");

        JPanel actionPanel = new JPanel(new GridLayout(0, 1, 0, 15));

        final boolean[] hasUnsavedChanges = new boolean[]{false};

        Runnable refreshTextAreas = () -> {
            studentInfoArea.setText(student.studentDetails());
            presInfoArea.setText(presentation.presentationDetails());
        };

        Runnable markDirty = () -> {
            hasUnsavedChanges[0] = true;
            registerBtn.setText("Register / Submit (unsaved)");
        };

        Runnable markClean = () -> {
            hasUnsavedChanges[0] = false;
            registerBtn.setText("Register / Submit");
        };

        Runnable rebuildActionPanel = () -> {
            actionPanel.removeAll();

            actionPanel.add(registerBtn);
            actionPanel.add(removeSubmittedBtn);

            actionPanel.add(detailBtn);
            actionPanel.add(viewSubmittedBtn);

            actionPanel.add(setTypeBtn);
            actionPanel.add(setTitleBtn);
            actionPanel.add(setDescBtn);

            actionPanel.add(setResearchTitleBtn);
            actionPanel.add(setSupervisorBtn);

            if (student.getPresentationType() != null && student.getPresentationType().equals("Oral")) {
                actionPanel.add(uploadBtn);
                actionPanel.add(deleteSlidesBtn);
            } else {
                actionPanel.add(uploadPosterBtn);
                actionPanel.add(deletePosterBtn);
            }

            actionPanel.revalidate();
            actionPanel.repaint();
        };

        rebuildActionPanel.run();

        // ---------- MAIN LAYOUT ----------
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 5),
                        BorderFactory.createEmptyBorder(20, 30, 20, 30)
                )
        );

        // LOGOUT AT VERY TOP (ABOVE INFO BOXES)
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrapper.add(logoutBtn);
        centerWrapper.add(Box.createVerticalStrut(20));

        centerWrapper.add(detailsRow);
        centerWrapper.add(Box.createVerticalStrut(20));
        centerWrapper.add(actionPanel);
        centerWrapper.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane, BorderLayout.CENTER);

        // ---------- ACTIONS ----------
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            LoginSignupUI.showLogin();
        });

        // Uploads should NOT auto-save to presentations.txt; only update UI and mark as unsaved
        uploadBtn.addActionListener(e -> {
            if (uploadFile(frame, presentation)) {
                presInfoArea.setText(presentation.presentationDetails());
            }
        });

        uploadPosterBtn.addActionListener(e -> {
            if (uploadFile(frame, presentation)) {
                presInfoArea.setText(presentation.presentationDetails());
            }
        });

        detailBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, presentation.presentationDetails())
        );

        viewSubmittedBtn.addActionListener(e -> {
            String submitted = readSubmittedDetails(student.getId());
            if (submitted == null) JOptionPane.showMessageDialog(frame, "No submitted presentation found.");
            else JOptionPane.showMessageDialog(frame, submitted);
        });

        // Delete uploaded file locally (does NOT remove the record from presentations.txt)
        deletePosterBtn.addActionListener(e -> {
            if (presentation.deletePresentation()) {
                JOptionPane.showMessageDialog(frame, "Presentation file deleted!");
                refreshTextAreas.run();
                markDirty.run();
            }
        });

        deleteSlidesBtn.addActionListener(e -> {
            if (presentation.deletePresentation()) {
                JOptionPane.showMessageDialog(frame, "Presentation file deleted!");
                refreshTextAreas.run();
                markDirty.run();
            }
        });

        setTypeBtn.addActionListener(e -> {
            String[] options = {"Oral", "Poster"};
            String choice = (String) JOptionPane.showInputDialog(
                    frame, "Select Presentation Type:",
                    "Presentation Type",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options,
                    student.getPresentationType()
            );
            if (choice == null) return;

            student.setPresentationType(choice);
            presentation.setPresentationType(choice);

            // If type changes, clear the file path (prevents wrong folder mix-up)
            presentation.setPresentationPath(null);

            refreshTextAreas.run();
            rebuildActionPanel.run();
            markDirty.run();
        });

        setTitleBtn.addActionListener(e -> {
            String t = JOptionPane.showInputDialog(frame, "Enter Presentation Title:", presentation.getTitle());
            if (t == null) return;

            t = t.trim();
            if (t.isEmpty()) return;

            presentation.setTitle(t);
            refreshTextAreas.run();
            markDirty.run();
        });

        setDescBtn.addActionListener(e -> {
            String d = JOptionPane.showInputDialog(frame, "Enter Presentation Description:", presentation.getDescription());
            if (d == null) return;

            d = d.trim();
            if (d.isEmpty()) return;

            presentation.setDescription(d);
            refreshTextAreas.run();
            markDirty.run();
        });

        setResearchTitleBtn.addActionListener(e -> {
            String t = JOptionPane.showInputDialog(frame, "Enter Research Title:", student.getResearchTitle());
            if (t == null) return;

            t = t.trim();
            if (t.isEmpty()) return;

            student.setResearchTitle(t);
            refreshTextAreas.run();
            markDirty.run();
        });

        setSupervisorBtn.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Enter Supervisor Name:", student.getSupervisorName());
            if (s == null) return;

            s = s.trim();
            if (s.isEmpty()) return;

            student.setSupervisorName(s);
            refreshTextAreas.run();
            markDirty.run();
        });

        // Register: only here we write into presentations.txt
        registerBtn.addActionListener(e -> {
            if (!validateBeforeRegister(frame, student, presentation)) return;

            savePresentation(student, presentation);
            JOptionPane.showMessageDialog(frame, "Presentation submitted / registered!");
            markClean.run();
        });

        // Remove: delete row from presentations.txt AND clear current info (also deletes uploaded file)
        removeSubmittedBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Remove your submitted presentation record?\n(This will also delete your uploaded file if it exists.)",
                    "Confirm Remove",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            // delete file first (if any)
            presentation.deletePresentation();

            boolean removed = deleteSubmissionRecord(student.getId());
            presentation.setTitle(null);
            presentation.setDescription(null);
            presentation.setPresentationPath(null);

            refreshTextAreas.run();
            markClean.run();

            JOptionPane.showMessageDialog(frame, removed ? "Submitted record removed." : "No submitted record found.");
        });

        frame.setVisible(true);
    }

    // ---------- VALIDATION ----------
    private static boolean validateBeforeRegister(JFrame frame, Student student, Presentation presentation) {

        if (presentation.getPresentationType() == null || presentation.getPresentationType().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please set a presentation type first.");
            return false;
        }

        if (presentation.getTitle() == null || presentation.getTitle().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please set a presentation title first.");
            return false;
        }

        if (presentation.getDescription() == null || presentation.getDescription().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please set a presentation description first.");
            return false;
        }

        return true;
    }

    // ---------- FILE HELPERS ----------
    private static void loadPresentationFromFile(Student student, Presentation presentation) {
        File f = new File(PRESENTATION_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;
                if (!p[0].trim().equals(student.getId().trim())) continue;

                student.setPresentationType(p[1].trim());
                presentation.setPresentationType(p[1].trim());
                presentation.setTitle(p[2].trim());
                presentation.setDescription(p[3].trim());

                if (p.length >= 6) {
                    student.setResearchTitle(p[4].trim());
                    student.setSupervisorName(p[5].trim());
                }

                if (p.length >= 7) {
                    presentation.setPresentationPath(p[6].trim().isEmpty() ? null : p[6].trim());
                }

                return;
            }
        } catch (IOException ignored) {}
    }

    // Returns true if uploaded OK
    private static boolean uploadFile(JFrame frame, Presentation presentation) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return false;

        boolean ok = presentation.uploadPresentation(chooser.getSelectedFile());
        if (!ok) {
            JOptionPane.showMessageDialog(frame, "Upload failed (check console for errors).");
        }
        return ok;
    }


    // Read submitted details directly from file (so it's truly "submitted")
    private static String readSubmittedDetails(String studentId) {
        File f = new File(PRESENTATION_FILE);
        if (!f.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;
                if (!p[0].trim().equals(studentId.trim())) continue;

                String type = p[1].trim();
                String title = p[2].trim();
                String desc = p[3].trim();
                String research = (p.length >= 5) ? p[4].trim() : "";
                String supervisor = (p.length >= 6) ? p[5].trim() : "";
                String path = (p.length >= 7) ? p[6].trim() : "";

                return "Submitted Presentation\n"
                        + "Student ID: " + studentId + "\n"
                        + "Presentation Type: " + type + "\n"
                        + "Presentation Title: " + title + "\n"
                        + "Presentation Description: " + desc + "\n"
                        + "Research Title: " + research + "\n"
                        + "Supervisor Name: " + supervisor + "\n"
                        + "File Path: " + path;
            }
        } catch (IOException ignored) {}

        return null;
    }

    private static void savePresentation(Student student, Presentation presentation) {
        File input = new File(PRESENTATION_FILE);
        File temp = new File("temp_presentations.txt");

        try (
                BufferedReader br = input.exists() ? new BufferedReader(new FileReader(input)) : null;
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
        ) {
            boolean found = false;

            if (br != null) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(student.getId() + ",")) {
                        bw.write(buildLine(student, presentation));
                        found = true;
                    } else {
                        bw.write(line);
                    }
                    bw.newLine();
                }
            }

            if (!found) {
                bw.write(buildLine(student, presentation));
                bw.newLine();
            }

        } catch (IOException ignored) {}

        if (input.exists()) input.delete();
        temp.renameTo(input);
    }

    private static boolean deleteSubmissionRecord(String studentId) {
        File input = new File(PRESENTATION_FILE);
        if (!input.exists()) return false;

        File temp = new File("temp_presentations.txt");
        boolean removed = false;

        try (
                BufferedReader br = new BufferedReader(new FileReader(input));
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(studentId + ",")) {
                    removed = true;
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ignored) {}

        if (input.exists()) input.delete();
        temp.renameTo(input);

        return removed;
    }

    private static String buildLine(Student student, Presentation presentation) {
        String path = (presentation.getPresentationPath() == null) ? "" : presentation.getPresentationPath();

        return student.getId() + "," +
                safeCsv(presentation.getPresentationType()) + "," +
                safeCsv(presentation.getTitle()) + "," +
                safeCsv(presentation.getDescription()) + "," +
                safeCsv(student.getResearchTitle()) + "," +
                safeCsv(student.getSupervisorName()) + "," +
                safeCsv(path);
    }

    // Minimal safety so null doesn't break the file format
    private static String safeCsv(String s) {
        return (s == null) ? "" : s.replace(",", " "); // avoid commas breaking split()
    }
}
