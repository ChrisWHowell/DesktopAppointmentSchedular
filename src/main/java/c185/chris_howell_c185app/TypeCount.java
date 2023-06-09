package c185.chris_howell_c185app;

/**
 * @author Chris Howell
 * @version 1.0
 * The TypeCount class is responsible for storing two values. It contains the type of the appointment and the running
 * count for that type
 */
public class TypeCount {
    private String typeName;
    private int appointmentCount;

    /**
     * The constructor responsible for creating the TypeCount object
     * @param typeName
     * @param appointmentCount
     */
    public TypeCount(String typeName, int appointmentCount) {
        this.typeName = typeName;
        this.appointmentCount = appointmentCount;
    }

    /**
     *
     * @return String typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     *
     * @param typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     *
     * @return int appointmentCount
     */
    public int getAppointmentCount() {
        return appointmentCount;
    }

    /**
     *
     * @param appointmentCount
     */
    public void setAppointmentCount(int appointmentCount) {
        this.appointmentCount = appointmentCount;
    }
}
