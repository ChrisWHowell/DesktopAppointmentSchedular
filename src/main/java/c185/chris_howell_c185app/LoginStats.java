package c185.chris_howell_c185app;

/**
 * @author Chris Howell
 * @version 1.0
 * The LoginStats class is responsible for storing the data associated with the Login Stats that will
 * be displayed in the Table View for the Login Stats Report.
 */
public class LoginStats {

    private String userName;
    private int countSuccess;
    private int countFailed;
    private String lastAttempt;

    /**
     * Constructs a LoginStats object with the specified values.
     *
     * @param userName     the user's name
     * @param countSuccess the count of successful login attempts
     * @param countFailed  the count of failed login attempts
     * @param lastAttempt  the timestamp of the last login attempt
     */
    public LoginStats(String userName, int countSuccess, int countFailed, String lastAttempt) {
        this.userName = userName;
        this.countSuccess = countSuccess;
        this.countFailed = countFailed;
        this.lastAttempt = lastAttempt;
    }
    /**
     * Constructs a LoginStats object with the specified user name and initializes the count of successful and failed login attempts to 0.
     *
     * @param userName the user's name
     */
    public LoginStats(String userName){
        this.userName = userName;
        this.countSuccess = 0;
        this.countFailed = 0;
        this.lastAttempt = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCountSuccess() {
        return countSuccess;
    }

    public void setCountSuccess(int countSuccess) {
        this.countSuccess = countSuccess;
    }

    public int getCountFailed() {
        return countFailed;
    }

    public void setCountFailed(int countFailed) {
        this.countFailed = countFailed;
    }

    public String getLastAttempt() {
        return lastAttempt;
    }

    /**
     * This method is called each time a User tried to log in with the same login as the calling object. If done
     * sequentially then the final value stored here will represent the last time that user tried to log in.
     * @param lastAttempt
     */
    public void setLastAttempt(String lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    /**
     * Increments the count of successful login attempts by 1.
     */
    public void incrementSuccessfulCount(){
        this.countSuccess = countSuccess +1;
    }

    /**
     * Increments the count of failed login attempts by 1.
     */
    public void incrementFailedCount(){
        this.countFailed = countFailed +1;
    }
}
