public class Student {

    private int id;
    private String researchTitle;
    private String supervisorName;
    private Presentation presentation;
    private String presentationType ;

    public Student(int id, String researchTitle,
                   String supervisorName, String presentationType) {

        this.id = id;
        this.researchTitle = researchTitle;
        this.supervisorName = supervisorName;
        this.presentationType = presentationType ;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
}