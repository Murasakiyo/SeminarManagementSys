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

        MyFrame frame = new MyFrame();
        frame.setTitle("Student Dashboard");

        // HEADER ------------------------------------------------------
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // INFO LABEL
        JLabel info = new JLabel(
                "Will put student information here: " + student.getId(),
                JLabel.CENTER
        );
        info.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // BUTTONS ----------------------------------------------------
        JButton uploadBtn = new JButton("Upload Slides");
        JButton deleteSlidesBtn = new JButton("Delete Slides");
        JButton detailBtn = new JButton("View Presentation Details");
        JButton uploadPosterBtn = new JButton("Upload Poster");
        JButton deletePosterBtn = new JButton("Delete Poster");

        // CENTER PANEL
        JPanel actionPanel = new JPanel(new GridLayout(4,1,0,15));
        actionPanel.add(detailBtn);

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
        centerWrapper.add(info);
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