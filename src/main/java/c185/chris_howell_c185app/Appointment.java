package c185.chris_howell_c185app;
/**
 * @author Chris Howell
 * @version 1.0
 * Represents an appointment in the application.
 */
public class Appointment {
    private int appointment_ID,customer_ID,user_ID;
    private String title,description,location,contact,type,start_DateTime,end_DateTime;

    /**
     * Constructs a new Appointment object with the specified parameters to include all variables
     * associated with any specific appointment.
     *
     * @param appointment_ID the ID of the appointment
     * @param customer_ID the ID of the customer associated with the appointment
     * @param title the title of the appointment
     * @param description the description of the appointment
     * @param location the location of the appointment
     * @param contact the contact associated with the appointment
     * @param type the type of the appointment
     * @param start_DateTime the start date and time of the appointment
     * @param end_DateTime the end date and time of the appointment
     * @param user_ID the ID of the user associated with the appointment
     */
    public Appointment(int appointment_ID, int customer_ID, String title, String description, String location,
                       String contact, String type, String start_DateTime, String end_DateTime, int user_ID) {
        this.appointment_ID = appointment_ID;
        this.customer_ID = customer_ID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start_DateTime = start_DateTime;
        this.end_DateTime = end_DateTime;
        this.user_ID = user_ID;
    }

    /**
     * Returns a string representation of the Appointment object.
     *
     * @return a string representation of the Appointment object
     */
    @Override
    public String toString() {
        return "Appointment{" +
                "appointment_ID=" + appointment_ID +
                ", customer_ID=" + customer_ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", contact='" + contact + '\'' +
                ", type='" + type + '\'' +
                ", start_DateTime='" + start_DateTime + '\'' +
                ", end_DateTime='" + end_DateTime + '\'' +
                ", user_ID='" + user_ID + '\'' +
                '}';
    }

    /**
     * Returns the ID of the appointment.
     *
     * @return The ID of the appointment as an int.
     **********************************************/
    public int getAppointment_ID() {
        return appointment_ID;
    }


    /**
     * Sets the ID of the appointment.
     *
     * @param appointment_ID the ID of the appointment to set
     ************************************************************/
    public void setAppointment_ID(int appointment_ID) {
        this.appointment_ID = appointment_ID;
    }



    /**
     * Returns the ID of the customer associated with the appointment .
     *
     * @return the ID of the customer associated with the appointment as a int
     ********************************************************/
    public int getCustomer_ID() {
        return customer_ID;
    }



    /**
     * Sets the ID of the customer associated with the appointment given through the parameters of the method.
     *
     * @param customer_ID the ID of the customer to set
     ****************************************************/
    public void setCustomer_ID(int customer_ID) {
        this.customer_ID = customer_ID;
    }



    /**
     * Returns the Title of the appointment as a String.
     *
     * @return the ID of the customer associated with the appointment
     *******************************************/
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the appointment given through the parameters of the method.
     *
     * @param title the ID of the customer to set
     *************************************************/
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Get the description of the appointment.
     * @return the description of the appointment
     *********************************************/
    public String getDescription() {
        return description;
    }



    /**
     * Set the description of the appointment.
     * @param description the description of the appointment
     *************************************************/
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Get the location of the appointment.
     * @return the location of the appointment
     *************************************************/
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the appointment.
     * @param location the location of the appointment
     ***********************************************/
    public void setLocation(String location) {
        this.location = location;
    }


    /**
     * Get the contact of the appointment.
     * @return the contact of the appointment as a String
     *************************************************/
    public String getContact() {
        return contact;
    }


    /**
     * Set the contact of the appointment.
     *  @param contact of the appointment
     *************************************************/
    public void setContact(String contact) {
        this.contact = contact;
    }


    /**
     * Get the location of the appointment.
     * @return the location of the appointment
     *************************************************/
    public String getType() {
        return type;
    }


    /**
     * Set the type of the appointment as a String.
     * @param type of the appointment as a String
     *************************************************/
    public void setType(String type) {
        this.type = type;
    }


    /**
     * Get the starting Date and Time of the appointment in a String object.
     * @return the start_DateTime of the appointment
     *************************************************/
    public String getStart_DateTime() {
        return start_DateTime;
    }


    /**
     * Set the starting Date and Time of the appointment in a String object.
     * @param start_DateTime of the appointment as a String
     *************************************************/
    public void setStart_DateTime(String start_DateTime) {
        this.start_DateTime = start_DateTime;
    }

    /**
     * Get the End Date and Time of the appointment in the form of a String.
     * @return the end_DateTime of the appointment as a String
     *************************************************/
    public String getEnd_DateTime() {
        return end_DateTime;
    }

    /**
     * Set the End Date and Time of the appointment togethor in one string.
     * @param end_DateTime of the appointment
     *************************************************/
    public void setEnd_DateTime(String end_DateTime) {
        this.end_DateTime = end_DateTime;
    }

    /**
     * Get the user_ID of the appointment.
     * @return the user_ID of the appointment as a int
     *************************************************/
    public int getUser_ID() {
        return user_ID;
    }

    /**
     * Set the User ID associated with the appointment .
     * @param user_ID of the appointment as an int
     *************************************************/
    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }
}
