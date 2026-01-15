import java.io.File;
import java.nio.file.*;
import java.time.LocalDate;

public class Presentation {

    private int presentationID;
    private String title;
    private LocalDate dateCreated;
    private String description;
    private String presentationPath;
    private String presentationType;

    public int getPresentationID() { return presentationID ; }
    public String getTitle() { return title ; }
    public LocalDate getDateCreated() { return dateCreated ; }
    public String getDescription() { return description ; }
    public String getPresentationPath() { return presentationPath ; }
    public String getPresentationType() { return presentationType ; }

    public void setPresentationID(int presentationID) {
        this.presentationID = presentationID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPresentationPath(String presentationPath) {
        this.presentationPath = presentationPath;
    }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }

    public Presentation(int presentationID, String title, LocalDate dateCreated, String description, String presentationPath, String presentationType) {
        this.presentationID = presentationID;
        this.title = title;
        this.dateCreated = dateCreated;
        this.description = description;
        this.presentationPath = presentationPath;
        this.presentationType = presentationType ;
    }

    public Presentation(Student s) {
        this.presentationID = s.getId();
        this.title = null;
        this.dateCreated = LocalDate.now();
        this.description = null;
        this.presentationPath = null ;
        this.presentationType = s.getPresentationType() ;
    }
    public boolean uploadPresentation(File file) {
        if(presentationType == "Poster") {
            try {
                String newPath = "uploads/student_" + presentationID + "/Posters/" + file.getName();

                File dest = new File(newPath);
                dest.getParentFile().mkdirs();

                Files.copy(
                        file.toPath(),
                        dest.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                presentationPath = newPath;
                return true ;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                String newPath = "uploads/student_" + presentationID + "/Slides/" + file.getName();

                File dest = new File(newPath);
                dest.getParentFile().mkdirs(); // create folder

                Files.copy(
                        file.toPath(),
                        dest.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                presentationPath = newPath; // save new path

                return true ;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false ;
    }

    public String reviewPoster() {
        if (presentationPath == null)
            return "No poster uploaded.";

        return "Poster file: " + presentationPath;
    }

    public boolean deletePresentation() {

        try {
            if (presentationPath != null) {
                Files.deleteIfExists(Paths.get(presentationPath));
                presentationPath = null;
                return true ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false ;
    }

    public String presentationDetails() {

        String s = "Presentation ID : "  + presentationID
                + "\nPresentation Type : " + presentationType
                + "\nPresentation Title : " + title
                + "\nDate Created : " + dateCreated
                + "\nPresentation Description : " + description
                + "\nFile Path : " + presentationPath ;

        return s ;
    }
}