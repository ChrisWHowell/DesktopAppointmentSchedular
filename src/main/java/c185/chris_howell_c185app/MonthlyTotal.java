package c185.chris_howell_c185app;

/**
 * @author Chris Howell
 * @version 1.0
 * The MonthlyTotal class is responsible for storing two values. It contains the month and the count for that month
 */
public class MonthlyTotal {
    private String monthName;
    private int appointmentCount;

    /**
     * The Constructor to create a MonthlyTotal object
     * @param monthName
     * @param appointmentCount
     */
    public MonthlyTotal(String monthName, int appointmentCount) {
        this.monthName = monthName;
        this.appointmentCount = appointmentCount;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(int appointmentCount) {
        this.appointmentCount = appointmentCount;
    }
}
