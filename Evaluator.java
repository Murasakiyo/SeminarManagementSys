import java.util.ArrayList;
import java.util.List;

/**
 * Evaluator class - extends User
 * Represents panel members who evaluate student presentations
 */
public class Evaluator extends User {
    private List<String> assignedPresentations; // List of presentation IDs or session IDs
    
    /**
     * Constructor for Evaluator
     */
    public Evaluator(String username, String name, String email_address, String password) {
        super(username, name, email_address, password, "Evaluator");
        this.assignedPresentations = new ArrayList<>();
    }
    
    /**
     * Check and display assigned presentations
     * Returns list of assigned presentation/session IDs
     */
    public List<String> checkPresentations() {
        // In GUI implementation, this could populate a table
        // For now, just return the list
        return new ArrayList<>(assignedPresentations);
    }
    
    /**
     * Assign a presentation to this evaluator
     */
    public void assignPresentation(String presentationID) {
        if (!assignedPresentations.contains(presentationID)) {
            assignedPresentations.add(presentationID);
        }
    }
    
    /**
     * Remove a presentation assignment
     */
    public void removePresentation(String presentationID) {
        assignedPresentations.remove(presentationID);
    }
    
    /**
     * Create an evaluation for a presentation
     * This method creates and returns an Evaluation object
     */
    public Evaluation evaluate(String sessionID, String studentName, String researchTitle) {
        // Create new evaluation
        Evaluation eval = new Evaluation(this.username, researchTitle);
        return eval;
    }
    
    /**
     * Get number of assigned presentations
     */
    public int getAssignmentCount() {
        return assignedPresentations.size();
    }
    
    /**
     * Check if this evaluator is assigned to a specific presentation
     */
    public boolean isAssignedTo(String presentationID) {
        return assignedPresentations.contains(presentationID);
    }
    
    // Getters
    public List<String> getAssignedPresentations() {
        return new ArrayList<>(assignedPresentations);
    }
    
    @Override
    public String toString() {
        return String.format("Evaluator[username=%s, name=%s, assignments=%d]",
            username, name, assignedPresentations.size());
    }
}
