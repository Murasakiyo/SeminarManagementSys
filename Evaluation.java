import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Evaluation class - Represents an evaluator's assessment of a presentation
 * Includes rubric marks and comments
 */
public class Evaluation {
    private String evaluationID;
    private String sessionID;
    private String studentUsername;
    private String researchTitle;
    private String evaluatorUsername;
    
    // Rubric marks (each out of 20)
    private int researchQualityMark;   // Problem clarity, significance
    private int resultMark;             // Results quality, analysis
    private int methodologyMark;        // Research methodology
    private int presentationMark;       // Presentation skills
    private int originalityMark;        // Originality, innovation
    
    private int finalMark;              // Sum of all marks (max 100)
    private String comments;
    
    // File constant
    private static final String EVALUATION_FILE = "evaluations.txt";
    
    /**
     * Constructor for new evaluation
     */
    public Evaluation(String sessionID, String studentUsername, String evaluatorUsername, String researchTitle) {
        this.evaluationID = generateEvaluationID();
        this.sessionID = sessionID;
        this.studentUsername = studentUsername;
        this.evaluatorUsername = evaluatorUsername;
        this.researchTitle = researchTitle;
        this.finalMark = 0;
        this.comments = "";
    }
    
    /**
     * Legacy constructor for compatibility
     */
    public Evaluation(String evaluatorUsername, String researchTitle) {
        this.evaluationID = generateEvaluationID();
        this.evaluatorUsername = evaluatorUsername;
        this.researchTitle = researchTitle;
        this.finalMark = 0;
        this.comments = "";
    }
    
    /**
     * Generate unique evaluation ID
     */
    private String generateEvaluationID() {
        return "EVAL" + System.currentTimeMillis();
    }
    
    /**
     * Set all rubric marks at once and calculate final mark
     */
    public void setMarks(int researchQuality, int result, int methodology, 
                        int presentation, int originality) {
        // Validate marks (0-20 each)
        this.researchQualityMark = validateMark(researchQuality);
        this.resultMark = validateMark(result);
        this.methodologyMark = validateMark(methodology);
        this.presentationMark = validateMark(presentation);
        this.originalityMark = validateMark(originality);
        
        calculateFinalMark();
    }
    
    /**
     * Validate individual mark (must be 0-20)
     */
    private int validateMark(int mark) {
        if (mark < 0) return 0;
        if (mark > 20) return 20;
        return mark;
    }
    
    /**
     * Calculate final mark (sum of all components)
     */
    private void calculateFinalMark() {
        this.finalMark = researchQualityMark + resultMark + methodologyMark + 
                        presentationMark + originalityMark;
    }
    
    /**
     * Add or update comment
     */
    public void addComment(String comment) {
        this.comments = comment;
    }
    
    /**
     * Edit evaluation - update marks and comments
     */
    public void editEvaluation(int researchQuality, int result, int methodology,
                              int presentation, int originality, String newComments) {
        setMarks(researchQuality, result, methodology, presentation, originality);
        this.comments = newComments;
    }
    
    /**
     * Evaluate research - basic setter for research title
     */
    public void evaluateResearch(String researchTitle) {
        this.researchTitle = researchTitle;
    }
    
    /**
     * Save evaluation to file
     * Format: EvalID,SessionID,Student,Evaluator,FinalMark,RQ,Res,Meth,Pres,Orig,Comments,ResearchTitle
     */
    public boolean saveToFile() {
        try (FileWriter fw = new FileWriter(EVALUATION_FILE, true)) {
            String safeComments = comments.replace(",", ";").replace("\n", " ");
            String safeTitle = researchTitle.replace(",", ";").replace("\n", " ");
            
            fw.write(String.format("%s,%s,%s,%s,%d,%d,%d,%d,%d,%d,%s,%s\n",
                evaluationID,
                sessionID != null ? sessionID : "N/A",
                studentUsername != null ? studentUsername : "N/A",
                evaluatorUsername,
                finalMark,
                researchQualityMark,
                resultMark,
                methodologyMark,
                presentationMark,
                originalityMark,
                safeComments,
                safeTitle));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Load evaluation from file by ID
     */
    public static Evaluation loadEvaluation(String evaluationID) {
        try (BufferedReader br = new BufferedReader(new FileReader(EVALUATION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 12 && data[0].equals(evaluationID)) {
                    Evaluation eval = new Evaluation(data[1], data[2], data[3], data[11]);
                    eval.evaluationID = data[0];
                    eval.finalMark = Integer.parseInt(data[4]);
                    eval.researchQualityMark = Integer.parseInt(data[5]);
                    eval.resultMark = Integer.parseInt(data[6]);
                    eval.methodologyMark = Integer.parseInt(data[7]);
                    eval.presentationMark = Integer.parseInt(data[8]);
                    eval.originalityMark = Integer.parseInt(data[9]);
                    eval.comments = data[10];
                    return eval;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all evaluations for a specific session
     */
    public static List<Evaluation> getEvaluationsForSession(String sessionID) {
        List<Evaluation> evals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EVALUATION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 12 && data[1].equals(sessionID)) {
                    Evaluation eval = new Evaluation(data[1], data[2], data[3], data[11]);
                    eval.evaluationID = data[0];
                    eval.finalMark = Integer.parseInt(data[4]);
                    eval.researchQualityMark = Integer.parseInt(data[5]);
                    eval.resultMark = Integer.parseInt(data[6]);
                    eval.methodologyMark = Integer.parseInt(data[7]);
                    eval.presentationMark = Integer.parseInt(data[8]);
                    eval.originalityMark = Integer.parseInt(data[9]);
                    eval.comments = data[10];
                    evals.add(eval);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return evals;
    }
    
    /**
     * Get all evaluations by a specific evaluator
     */
    public static List<Evaluation> getEvaluationsByEvaluator(String evaluatorUsername) {
        List<Evaluation> evals = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EVALUATION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 12 && data[3].equals(evaluatorUsername)) {
                    Evaluation eval = new Evaluation(data[1], data[2], data[3], data[11]);
                    eval.evaluationID = data[0];
                    eval.finalMark = Integer.parseInt(data[4]);
                    eval.researchQualityMark = Integer.parseInt(data[5]);
                    eval.resultMark = Integer.parseInt(data[6]);
                    eval.methodologyMark = Integer.parseInt(data[7]);
                    eval.presentationMark = Integer.parseInt(data[8]);
                    eval.originalityMark = Integer.parseInt(data[9]);
                    eval.comments = data[10];
                    evals.add(eval);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return evals;
    }
    
    /**
     * Calculate average mark for a student across all evaluations
     */
    public static double getAverageMarkForStudent(String studentUsername) {
        List<Integer> marks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EVALUATION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 12 && data[2].equals(studentUsername)) {
                    marks.add(Integer.parseInt(data[4]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        
        if (marks.isEmpty()) return 0.0;
        
        int sum = 0;
        for (int mark : marks) {
            sum += mark;
        }
        return (double) sum / marks.size();
    }
    
    // Getters
    public String getEvaluationID() { return evaluationID; }
    public String getSessionID() { return sessionID; }
    public String getStudentUsername() { return studentUsername; }
    public String getResearchTitle() { return researchTitle; }
    public String getEvaluatorUsername() { return evaluatorUsername; }
    public int getFinalMark() { return finalMark; }
    public int getResearchQualityMark() { return researchQualityMark; }
    public int getResultMark() { return resultMark; }
    public int getMethodologyMark() { return methodologyMark; }
    public int getPresentationMark() { return presentationMark; }
    public int getOriginalityMark() { return originalityMark; }
    public String getComments() { return comments; }
    
    // Setters for individual marks
    public void setResearchQualityMark(int mark) {
        this.researchQualityMark = validateMark(mark);
        calculateFinalMark();
    }
    
    public void setResultMark(int mark) {
        this.resultMark = validateMark(mark);
        calculateFinalMark();
    }
    
    public void setMethodologyMark(int mark) {
        this.methodologyMark = validateMark(mark);
        calculateFinalMark();
    }
    
    public void setPresentationMark(int mark) {
        this.presentationMark = validateMark(mark);
        calculateFinalMark();
    }
    
    public void setOriginalityMark(int mark) {
        this.originalityMark = validateMark(mark);
        calculateFinalMark();
    }
    
    @Override
    public String toString() {
        return String.format("Evaluation[ID=%s, Session=%s, Student=%s, Evaluator=%s, FinalMark=%d/100]",
            evaluationID, sessionID, studentUsername, evaluatorUsername, finalMark);
    }
}
