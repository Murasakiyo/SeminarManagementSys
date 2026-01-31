import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Session class - Represents a seminar session
 * Can have multiple presenters and evaluators
 */
public class Session {
    private String sessionID;
    private String sessionType; // "Oral" or "Poster"
    private List<String> presenters; // List of presenter usernames
    private List<String> evaluators; // List of evaluator usernames (FIXED: was singular)
    private String date;
    private String venue;
    private String timeStart;
    private String timeEnd;
    
    // File constants
    private static final String SESSION_FILE = "sessions.txt";
    
    /**
     * Constructor for Session
     */
    public Session(String sessionID, String sessionType, String date, 
                   String venue, String timeStart, String timeEnd) {
        this.sessionID = sessionID;
        this.sessionType = sessionType;
        this.date = date;
        this.venue = venue;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.presenters = new ArrayList<>();
        this.evaluators = new ArrayList<>(); // Initialize list
    }
    
    /**
     * Create and save session to file
     * Format: SessionID,SessionType,Date,Venue,TimeStart,TimeEnd,Presenters(semicolon-sep),Evaluators(semicolon-sep)
     */
    public boolean createSession() {
        try (FileWriter fw = new FileWriter(SESSION_FILE, true)) {
            fw.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                sessionID,
                sessionType,
                date,
                venue,
                timeStart,
                timeEnd,
                String.join(";", presenters),
                String.join(";", evaluators)));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Edit existing session in file
     */
    public boolean editSession() {
        List<String> lines = new ArrayList<>();
        boolean edited = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equals(sessionID)) {
                    // Replace with updated session
                    lines.add(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                        sessionID, sessionType, date, venue, timeStart, timeEnd,
                        String.join(";", presenters),
                        String.join(";", evaluators)));
                    edited = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        // Write back to file
        try (FileWriter fw = new FileWriter(SESSION_FILE)) {
            for (String line : lines) {
                fw.write(line + "\n");
            }
            return edited;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete session from file
     */
    public boolean deleteSession() {
        List<String> lines = new ArrayList<>();
        boolean deleted = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (!data[0].equals(sessionID)) {
                    lines.add(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        // Write back
        try (FileWriter fw = new FileWriter(SESSION_FILE)) {
            for (String line : lines) {
                fw.write(line + "\n");
            }
            return deleted;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Register this session (could be used for student registration)
     */
    public boolean registerSession() {
        // This could save session registration to a separate file
        // Or update session status
        return createSession();
    }
    
    /**
     * Load a session from file by ID
     */
    public static Session loadSession(String sessionID) {
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8 && data[0].equals(sessionID)) {
                    Session session = new Session(
                        data[0], // sessionID
                        data[1], // sessionType
                        data[2], // date
                        data[3], // venue
                        data[4], // timeStart
                        data[5]  // timeEnd
                    );
                    
                    // Add presenters
                    if (!data[6].isEmpty()) {
                        String[] presenterArray = data[6].split(";");
                        for (String p : presenterArray) {
                            session.addPresenter(p);
                        }
                    }
                    
                    // Add evaluators
                    if (!data[7].isEmpty()) {
                        String[] evaluatorArray = data[7].split(";");
                        for (String e : evaluatorArray) {
                            session.addEvaluator(e);
                        }
                    }
                    
                    return session;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all sessions from file
     */
    public static List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8) {
                    Session session = new Session(
                        data[0], data[1], data[2], data[3], data[4], data[5]
                    );
                    
                    if (!data[6].isEmpty()) {
                        for (String p : data[6].split(";")) {
                            session.addPresenter(p);
                        }
                    }
                    
                    if (!data[7].isEmpty()) {
                        for (String e : data[7].split(";")) {
                            session.addEvaluator(e);
                        }
                    }
                    
                    sessions.add(session);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessions;
    }
    
    // Presenter management
    public void addPresenter(String presenter) {
        if (!presenters.contains(presenter)) {
            presenters.add(presenter);
        }
    }
    
    public void removePresenter(String presenter) {
        presenters.remove(presenter);
    }
    
    // Evaluator management (NEW - was missing)
    public void addEvaluator(String evaluator) {
        if (!evaluators.contains(evaluator)) {
            evaluators.add(evaluator);
        }
    }
    
    public void removeEvaluator(String evaluator) {
        evaluators.remove(evaluator);
    }
    
    // Getters
    public String getSessionID() { return sessionID; }
    public String getSessionType() { return sessionType; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public String getTimeStart() { return timeStart; }
    public String getTimeEnd() { return timeEnd; }
    public List<String> getPresenters() { return new ArrayList<>(presenters); }
    public List<String> getEvaluators() { return new ArrayList<>(evaluators); }
    
    // Setters
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    public void setDate(String date) { this.date = date; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setTimeStart(String timeStart) { this.timeStart = timeStart; }
    public void setTimeEnd(String timeEnd) { this.timeEnd = timeEnd; }
    
    @Override
    public String toString() {
        return String.format("Session[ID=%s, Type=%s, Date=%s, Venue=%s, Time=%s-%s, Presenters=%d, Evaluators=%d]",
            sessionID, sessionType, date, venue, timeStart, timeEnd, 
            presenters.size(), evaluators.size());
    }
}
