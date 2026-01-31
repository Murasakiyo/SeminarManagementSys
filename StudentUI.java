import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StudentUI {

    private Student student;
    private Presentation presentation;
    public StudentUI(Student student, Presentation presentation) {

        this.student = student;
        this.presentation = presentation;

        StudentPage(this.student, this.presentation);
    }
    public static void StudentPage(Student student, Presentation presentation) {

        MyFrame frame = new MyFrame(750, 600);
        frame.setTitle("Student Dashboard");

        // HEADER ------------------------------------------------------
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // INFO LABEL
        JTextArea studentInfoArea = new JTextArea(student.studentDetails());
        studentInfoArea.setEditable(false);
        studentInfoArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        studentInfoArea.setLineWrap(true);
        studentInfoArea.setWrapStyleWord(true);
        studentInfoArea.setOpaque(false); // let panel background show

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


        // BUTTONS ----------------------------------------------------
        JButton setTypeBtn = new JButton("Set Presentation Type");
        JButton setTitleBtn = new JButton("Set Presentation Title");
        JButton setDescBtn = new JButton("Set Presentation Description");
        JButton uploadBtn = new JButton("Upload Slides");
        JButton deleteSlidesBtn = new JButton("Delete Slides");
        JButton detailBtn = new JButton("View Presentation Details");
        JButton uploadPosterBtn = new JButton("Upload Poster");
        JButton deletePosterBtn = new JButton("Delete Poster");

        // CENTER PANEL
        JPanel actionPanel = new JPanel(new GridLayout(4,1,0,15));
        actionPanel.add(detailBtn);
        actionPanel.add(setTypeBtn);
        actionPanel.add(setTitleBtn);
        actionPanel.add(setDescBtn);

        if(student.getPresentationType().equals("Oral")){
            actionPanel.add(uploadBtn);
            actionPanel.add(deleteSlidesBtn);
        }else{
            actionPanel.add(uploadPosterBtn);
            actionPanel.add(deletePosterBtn);
        }

        // CENTER WRAPPER (same as login style)
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE,5),
                        BorderFactory.createEmptyBorder(20,30,20,30)
                )
        );

        centerWrapper.add(Box.createVerticalStrut(30));
        centerWrapper.add(detailsRow);
        centerWrapper.add(Box.createVerticalStrut(20));
        centerWrapper.add(actionPanel);
        centerWrapper.add(Box.createVerticalGlue());

        frame.add(centerWrapper, BorderLayout.CENTER);

        // ACTIONS ----------------------------------------------------

        uploadBtn.addActionListener(e -> uploadFile(frame, presentation));
        uploadPosterBtn.addActionListener(e -> uploadFile(frame, presentation));

        detailBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        presentation.presentationDetails()
                )
        );

        deletePosterBtn.addActionListener(e -> {
            if(presentation.deletePresentation())
                JOptionPane.showMessageDialog(frame,"Presentation file deleted!");
        });

        deleteSlidesBtn.addActionListener(e -> {
            if(presentation.deletePresentation())
                JOptionPane.showMessageDialog(frame,"Presentation file deleted!");
        });

        setTitleBtn.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(frame, "Enter Presentation Title:", presentation.getTitle());
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                presentation.setTitle(newTitle.trim());
                JOptionPane.showMessageDialog(frame, "Title updated!");
            }
        });

        setDescBtn.addActionListener(e -> {
            String newDesc = JOptionPane.showInputDialog(frame, "Enter Presentation Description:", presentation.getDescription());
            if (newDesc != null && !newDesc.trim().isEmpty()) {
                presentation.setDescription(newDesc.trim());
                JOptionPane.showMessageDialog(frame, "Description updated!");
            }
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

            if (choice != null) {
                // update both student + presentation
                student.setPresentationType(choice);
                presentation.setPresentationType(choice);

                JOptionPane.showMessageDialog(frame, "Presentation type updated to: " + choice);

                // Refresh UI to show correct buttons
                frame.dispose();
                StudentPage(student, presentation);
            }
        });

        frame.setVisible(true);
    }

    // FILE UPLOAD
    private static void uploadFile(JFrame frame, Presentation presentation){

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();
            boolean ok = presentation.uploadPresentation(file);

            if(ok)
                JOptionPane.showMessageDialog(frame,"File uploaded!");
        }
    }
}
