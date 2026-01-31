public class Student {

    private String id;
    private String researchTitle;
    private String supervisorName;
    private Presentation presentation;
    private String presentationType ;

    public Student(String id, String researchTitle,
                   String supervisorName, String presentationType) {

        this.id = id;
        this.researchTitle = researchTitle;
        this.supervisorName = supervisorName;
        this.presentationType = presentationType ;
    }

    public Student(String id) {

        this.id = id;
        this.researchTitle = null;
        this.supervisorName = null;
        this.presentationType = "poster" ;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getResearchTitle() { return researchTitle; }
    public void setResearchTitle(String researchTitle) {
        this.researchTitle = researchTitle;
    }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getPresentationType() { return presentationType ; }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }

    public String studentDetails() {
        return "Student ID: " + id
                + "\nResearch Title: " + researchTitle
                + "\nSupervisor Name: " + supervisorName
                + "\nPresentation Type: " + presentationType;
    }
}
