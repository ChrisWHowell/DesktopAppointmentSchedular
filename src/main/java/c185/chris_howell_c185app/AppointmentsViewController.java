package c185.chris_howell_c185app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Chris Howell
 * @version 1.0
 * The AppointmentsViewController class is responsible for managing the user interface and
 * interactions for the appointment management view represented by the appointmentsView.fxml.
 *****************************************************/
public class AppointmentsViewController implements Initializable {
    /**
     * The time zone for the application, set to Central Time Zone.
     */
    public static final ZoneId CENTRAL_TIME_ZONE = ZoneId.of("America/Chicago");


    /**
     * The date-time formatter for displaying and parsing date-time values in the application.
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @FXML
    private ComboBox cb_Contact, cb_userID, cb_customer_ID;
    @FXML
    private TextField tf_title, tf_type, tf_Location, tf_description, tf_startTime, tf_endTime;
    @FXML
    private Label lb_appointment_id;
    @FXML
    private DatePicker dp_StartDate, dp_EndDate;
    @FXML
    Button btn_Delete, btn_SaveAsEdit, btn_SaveAsNew, btn_ClearID, btn_GO_TO_custumerdata;
    @FXML
    private TableView<Appointment> tv_AppointmentList;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> titleColumn;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;
    @FXML
    private TableColumn<Appointment, String> locationColumn;
    @FXML
    private TableColumn<Appointment, String> contactColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startDateTimeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endDateTimeColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> userIdColumn;

    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private RadioButton tg_weekToggle, tg_monthToggle;

    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private ObservableList<String> userIDOlist, contactOList, customerIDOlist;

    private String loginuserID;
    private Stage stage;

    /**
     * Constructs a new AppointmentsViewController that uses the login ID of the user currently logged in and the stage
     * object used to set the scene for the appointmentsView.fxml
     *
     * @param stage the stage which holds the scene of
     * @param loginuserID the login id associated with the user currently logged in

     */
    public AppointmentsViewController(Stage stage, String loginuserID) {
        this.stage = stage;
        this.loginuserID = loginuserID;
    }

    /**
     * This method is called after the FXML file has been loaded and the scene graph
     * has been constructed. This method is invoked automatically by the JavaFX runtime when
     * the controller is instantiated. It is responsible for initializing the JavaFX objects ,setting up the event listeners
     * for the objects that are clickable within the GUI, and defining the methods calls used with the event associated with
     * the listeners are triggered. It also calls the methods which populate combo boxes and TableViews.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointment_ID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("start_DateTime"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("end_DateTime"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customer_ID"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("user_ID"));

        tv_AppointmentList.setItems(appointments);

        this.toggleGroup = new ToggleGroup();
        tg_monthToggle.setToggleGroup(toggleGroup);
        tg_weekToggle.setToggleGroup(toggleGroup);

        tg_monthToggle.setSelected(true);
        populateAppointmentsTableByMonth();

        initUserIDComboBox();
        initContactComboBox();
        initCustIDComboBox();

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tg_monthToggle) {
                populateAppointmentsTableByMonth();
            } else if (newValue == tg_weekToggle) {
                populateAppointmentsTableByWeek();
            }
        });

        btn_Delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    deleteAppointment();
                }
            }
        });
        btn_SaveAsNew.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    addAppointment();
                }
            }
        });
        btn_SaveAsEdit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    editAppointment();
                }
            }
        });
        btn_ClearID.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    clearAppointmentID();
                }
            }
        });

        btn_GO_TO_custumerdata.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    try {
                        goBacktoCustomerPage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception as needed
                    }

                }
            }
        });
        tv_AppointmentList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    handleAppointmentTableMouseClicked();
                }
            }
        });
    }


    /**
     * The initContactComboBox() method is responsible for collecting the Contacts information
     * from the database and then populating that data in the ComboBox as an
     * observableArrayList so that the user can see and select an appropriate contact.
     * It returns no value
     */
    private void initContactComboBox() {
        List<String> contacts = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            // prepare the SQL statement
            String sql = "SELECT Contact_Name FROM contacts";
            PreparedStatement stmt = connect.prepareStatement(sql);

            // execute the query
            ResultSet rs = stmt.executeQuery();

            // get the results and add them to the list
            while (rs.next()) {
                contacts.add(rs.getString("Contact_Name"));
            }
            connect.close();
            contactOList = FXCollections.observableArrayList(contacts);
            cb_Contact.setItems(contactOList);
            if (!contactOList.isEmpty()) {
                cb_Contact.getSelectionModel().select(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * The initUserIDComboBox() method is responsible for collecting the User ID information
     * from the database and then populating that data in the ComboBox as an
     * observableArrayList so that the user can see and select an appropriate User ID.
     * It returns no value and takes no parameters.
     */
    private void initUserIDComboBox() {
        List<String> userIDs = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            // prepare the SQL statement
            String sql = "SELECT User_ID FROM users";
            PreparedStatement stmt = connect.prepareStatement(sql);

            // execute the query
            ResultSet rs = stmt.executeQuery();

            // get the results and add them to the list
            while (rs.next()) {
                userIDs.add(rs.getString("User_ID"));
            }
            connect.close();
            userIDOlist = FXCollections.observableArrayList(userIDs);
            cb_userID.setItems(userIDOlist);
            if (!userIDOlist.isEmpty()) {
                cb_userID.getSelectionModel().select(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The initCustIDComboBox() method is responsible for collecting the Customer ID information
     * from the database and then populating that data in the ComboBox as an
     * observableArrayList so that the user can see and select an appropriate Customer ID.
     * It returns no value and takes no parameters.
     */
    private void initCustIDComboBox() {
        List<String> customerIDs = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            // prepare the SQL statement
            String sql = "SELECT Customer_ID FROM customers";
            PreparedStatement stmt = connect.prepareStatement(sql);

            // execute the query
            ResultSet rs = stmt.executeQuery();

            // get the results and add them to the list
            while (rs.next()) {
                customerIDs.add(rs.getString("Customer_ID"));
            }
            connect.close();
            customerIDOlist = FXCollections.observableArrayList(customerIDs);
            cb_customer_ID.setItems(customerIDOlist);
            if (!customerIDOlist.isEmpty()) {
                cb_customer_ID.getSelectionModel().select(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The addAppointment() method extracts the information from the Combo boxes and Text Fields. It then
     * checks the data for potential errors and handles notifications to the user if corrections needs to be made.
     * If these checks are passed it then takes that data, connects to the database and adds a new appointment to the
     * appointment table.
     * This method returns no value and takes no parameters.
     */
    @FXML
    private void addAppointment() {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();

        String appointmentID = lb_appointment_id.getText();
        String contact = (String) cb_Contact.getValue();
        String strUserID = (String) cb_userID.getValue();
        String customer_id = (String) cb_customer_ID.getValue();
        String title = tf_title.getText();
        String type = tf_type.getText();
        String location = tf_Location.getText();
        String description = tf_description.getText();

        String startTime = tf_startTime.getText();
        String endTime = tf_endTime.getText();
        LocalDate startDate = dp_StartDate.getValue();
        LocalDate endDate = dp_EndDate.getValue();

        //If statement that checks to see if any elements were left blank if so gives error and exits method
        if (title.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || type.isEmpty() || customer_id == null
                || location.isEmpty() || contact == null || strUserID == null || startDate == null || endDate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }
        //checks to ensure the time is within business hours
        else if (!(isWithinBusinessHours(startDate, startTime, endDate, endTime))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("The selected start and end date and times do not fall within specified business hours");
            alert.showAndWait();
            return;
        } else {
            int contact_ID = 0;
            int intloginuserID = 0;
            try {
                String query = "Select Contact_ID FROM contacts WHERE Contact_Name = '" + contact + "';";
                ResultSet rs = connect.createStatement().executeQuery(query);
                while (rs.next()) {
                    contact_ID = rs.getInt("Contact_ID");
                }
                String query2 = "Select User_ID FROM users WHERE User_Name = '" + loginuserID + "';";
                ResultSet rs2 = connect.createStatement().executeQuery(query2);
                while (rs2.next()) {
                    intloginuserID = rs2.getInt("User_ID");
                }
                // Get the current date and time in the user's time zone
                LocalDateTime userDateTime = LocalDateTime.now();

                // Format the date and time as strings using a DateTimeFormatter object
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String userDateStr = userDateTime.format(dateFormatter);
                String userTimeStr = userDateTime.format(timeFormatter);
                // Convert the user date and time to central time using the convertToCentralTime() method
                String centralDateTimeStr = convertToCentralTime(LocalDate.parse(userDateStr), userTimeStr);

                String startDatetimeCentral = convertToCentralTime(startDate, startTime);
                String endDateTimeCentral = convertToCentralTime(endDate, endTime);
                PreparedStatement stmt = connect.prepareStatement("INSERT INTO appointments (Title,Description,Location,Type,Start,End,Create_Date,Created_By,Last_Update,"
                        + "Last_Updated_By,Customer_ID,User_ID,Contact_ID) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, title);
                stmt.setString(2, description);
                stmt.setString(3, location);
                stmt.setString(4, type);
                stmt.setString(5, startDatetimeCentral);
                stmt.setString(6, endDateTimeCentral);
                stmt.setString(7, centralDateTimeStr);
                stmt.setString(8, loginuserID);
                stmt.setString(9, centralDateTimeStr);
                stmt.setString(10, loginuserID);
                stmt.setInt(11, Integer.parseInt(customer_id));
                stmt.setInt(12, intloginuserID);
                stmt.setInt(13, contact_ID);
                stmt.executeUpdate();

                tv_AppointmentList.getItems().clear();
                Toggle selectedToggle = toggleGroup.getSelectedToggle();
                if (selectedToggle == tg_monthToggle) {
                    populateAppointmentsTableByMonth();
                } else if (selectedToggle == tg_weekToggle) {
                    populateAppointmentsTableByWeek();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The  deleteAppointment() method extracts the appointmenet ID from the label which is populated by clicking
     * on a specific row in the Tableview. If no selection has been made and the Appointment ID is blank or not a number
     * it then notifies the user that it must make a selection. If a selection is made the method connects to the database
     * and removes the row that contains that appointment ID
     * This method returns no value and takes no parameters.
     */
    private void deleteAppointment() {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        // Get the customer ID from the lb_custID label
        String appointmentIdStr = lb_appointment_id.getText();
        String type = tf_type.getText();
        // Check that the Appointment ID string contains only digits and is greater than 0
        if (!appointmentIdStr.matches("\\d+") || Integer.parseInt(appointmentIdStr) <= 0) {
            // Display an error message and return
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid Appointment ID. Please select a appointment from the table below");
            alert.showAndWait();
            return;
        }
        int appointmentId = Integer.parseInt(appointmentIdStr);
        try {
            PreparedStatement deleteAppointments = connect.prepareStatement(
                    "DELETE FROM appointments WHERE Appointment_ID = ?");
            deleteAppointments.setInt(1, appointmentId);
            deleteAppointments.executeUpdate();
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setContentText("Appointment with ID " + appointmentId + " and type " + type + " deleted successfully");
            successAlert.showAndWait();
            clearAppointmentID();
            tv_AppointmentList.getItems().clear();
            Toggle selectedToggle = toggleGroup.getSelectedToggle();
            if (selectedToggle == tg_monthToggle) {
                populateAppointmentsTableByMonth();
            } else if (selectedToggle == tg_weekToggle) {
                populateAppointmentsTableByWeek();
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error deleting appointments");
            alert.showAndWait();
            return;
        }
    }

    /**
     * The  editAppointment() method extracts all of the data from the combo boxes and text fields, including the appointment ID.
     * If no selection was made from the TableView then there will be no number associated with it and the user will be notified.
     * If the user has filled in the neccesary data then the method connects to the database and changes the appointment associated with
     * that specific appointment ID
     * This method returns no value and takes no parameters.
     */
    private void editAppointment() {
        String appointmentID = lb_appointment_id.getText();
        if (!appointmentID.matches("\\d+") || Integer.parseInt(appointmentID) <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer from the customer list below so that a customer ID is selected.");
            alert.showAndWait();
            return;
        }
        int intappointmentID = Integer.parseInt(appointmentID);
        String title = tf_title.getText();
        String description = tf_description.getText();
        String location = tf_Location.getText();
        String type = tf_type.getText();
        String str_customer_id = cb_customer_ID.getValue().toString();
        int customer_ID = Integer.parseInt(str_customer_id);
        String str_User_id = cb_userID.getValue().toString();
        int user_id = Integer.parseInt(str_User_id);
        String contact = cb_Contact.getValue().toString();
        String startTime = tf_startTime.getText();
        String endTime = tf_endTime.getText();

        LocalDate startDate = dp_StartDate.getValue();
        LocalDate endDate = dp_EndDate.getValue();
        if(checkforOverlappingAppointments(startDate,startTime,endDate,endTime)){
            // Show an error message if any required field is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("You are attempting to schedule an appointment where there are conflicting existing appointments. Please choose another time.");
            alert.showAndWait();
            return;
        }
        String startDateTime = convertToCentralTime(startDate, startTime);
        String endDateTime = convertToCentralTime(endDate, endTime);

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || type.isEmpty() || contact.isEmpty() || startTime.isEmpty() || endTime.isEmpty()
                || str_customer_id.isEmpty() || str_User_id.isEmpty() || startDate == null || endDate == null) {

            // Show an error message if any required field is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }
        try {
            int contact_ID = 0;
            DatabaseConnection dBC = new DatabaseConnection();
            Connection connect = dBC.getConnection();

            String query = "Select Contact_ID FROM contacts WHERE Contact_Name = '" + contact + "';";
            ResultSet rs = connect.createStatement().executeQuery(query);
            while (rs.next()) {
                contact_ID = rs.getInt("Contact_ID");
            }
            LocalDateTime userDateTime = LocalDateTime.now();

            // Format the date and time as strings using a DateTimeFormatter object
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String userDateStr = userDateTime.format(dateFormatter);
            String userTimeStr = userDateTime.format(timeFormatter);
            // Convert the user date and time to central time using the convertToCentralTime() method
            String centralDateTimeStr = convertToCentralTime(LocalDate.parse(userDateStr), userTimeStr);

            PreparedStatement stmt = connect.prepareStatement("UPDATE appointments SET Title = ?, Description  = ?, Location = ?, Type = ?, " +
                    "Start = ?," + " End = ?, Last_Update = ?, Last_Updated_By= ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?");
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, location);
            stmt.setString(4, type);
            stmt.setString(5, startDateTime);
            stmt.setString(6, endDateTime);
            stmt.setString(7, centralDateTimeStr);
            stmt.setString(8, loginuserID);
            stmt.setInt(9, customer_ID);
            stmt.setInt(10, user_id);
            stmt.setInt(11, contact_ID);
            stmt.setInt(12, intappointmentID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 1) {
                // Show a success message if the customer information was updated successfully
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Customer information updated successfully.");
                alert.showAndWait();
                tv_AppointmentList.getItems().clear();
                Toggle selectedToggle = toggleGroup.getSelectedToggle();
                if (selectedToggle == tg_monthToggle) {
                    populateAppointmentsTableByMonth();
                } else if (selectedToggle == tg_weekToggle) {
                    populateAppointmentsTableByWeek();
                }

            } else {
                // Show an error message if the customer information was not updated
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Unable to update customer information.");
                alert.showAndWait();
            }
            connect.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The isWithinBusinessHours method takes 4 parameters associated with the Start and End Dates and times. It converts
     * the dates and times from the users Time zone to the business time zone (Eastern) . It performs the neccesary calculations
     * to determine if they are within the business hours. It returns true if they are within the business hours and false otherwise.
     * @param startDate
     * @param startTimeStr
     * @param endDate
     * @param endTimeStr
     * @return boolean
     */
    private boolean isWithinBusinessHours(LocalDate startDate, String startTimeStr, LocalDate endDate, String endTimeStr) {
        // Define the start and end times for the business day in the business time zone
        LocalTime businessStartTime = LocalTime.of(8, 0);
        LocalTime businessEndTime = LocalTime.of(22, 0);

        // Parse the start and end time strings into LocalTime objects

        if (startTimeStr.length() == 4) {
            startTimeStr = "0" + startTimeStr;
        }
        if (endTimeStr.length() == 4) {
            endTimeStr = "0" + endTimeStr;
        }

        LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

        // Combine the start and end date and time values into LocalDateTime objects
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // Get the user's time zone and the business time zone
        ZoneId userTimeZone = ZoneId.systemDefault();
        ZoneId businessTimeZone = ZoneId.of("America/New_York"); // Gets business time zone ID

        // Convert the start and end times of the appointment from the user's time zone to the business time zone
        ZonedDateTime userStartDateTime = startDateTime.atZone(userTimeZone);
        ZonedDateTime businessStartDateTime = userStartDateTime.withZoneSameInstant(businessTimeZone);
        LocalTime businessStartTimeOfDay = businessStartDateTime.toLocalTime();

        ZonedDateTime userEndDateTime = endDateTime.atZone(userTimeZone);
        ZonedDateTime businessEndDateTime = userEndDateTime.withZoneSameInstant(businessTimeZone);
        LocalTime businessEndTimeOfDay = businessEndDateTime.toLocalTime();

        // Check if the start and end times of the appointment fall within the business hours
        if (businessStartTimeOfDay.isBefore(businessStartTime) || businessEndTimeOfDay.isAfter(businessEndTime)) {
            return false;
        }

        // Check if the appointment falls on a weekend
        DayOfWeek startDayOfWeek = businessStartDateTime.getDayOfWeek();
        DayOfWeek endDayOfWeek = businessEndDateTime.getDayOfWeek();
        if (startDayOfWeek == DayOfWeek.SATURDAY || startDayOfWeek == DayOfWeek.SUNDAY || endDayOfWeek == DayOfWeek.SATURDAY || endDayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        return true;
    }

    /**
     * The clearAppointmentID() take the TextFields, Combo Boxes and relevant labels and clears the data from them
     * This method returns no value and takes no parameters.
     */
    private void clearAppointmentID() {
        lb_appointment_id.setText("#");
        cb_userID.getSelectionModel().clearSelection();
        cb_Contact.getSelectionModel().clearSelection();
        cb_customer_ID.getSelectionModel().clearSelection();
        tf_title.clear();
        tf_type.clear();
        tf_Location.clear();
        tf_description.clear();
        tf_startTime.clear();
        tf_endTime.clear();
        //dp_StartDate.
        //dp_StartDate,dp_EndDate;tf_startTime,tf_endTime
    }

    /**
     * THe convertToCentralTime takes two parameters of type LocalDate and String that are associated with the users
     * local time. It then converts them into Central Time as one String so that it can be entered into the database and
     * be consistent with the dates and times there.
     *
     * @param date
     * @param timeStr
     * @return String
     */
    public static String convertToCentralTime(LocalDate date, String timeStr) {
        if (timeStr.length() == 4) {
            timeStr = "0" + timeStr;
        }
        // Parse the time string into a LocalTime object using the ISO format
        LocalTime time = LocalTime.parse(timeStr);

        // Combine the date and time into a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // Define the time zone IDs for the user's time zone and the central time zone
        ZoneId userTimeZone = ZoneId.systemDefault(); // Replace with the user's time zone ID
        ZoneId centralTimeZone = ZoneId.of("America/Chicago"); // Central time zone ID

        // Convert the LocalDateTime object to a ZonedDateTime object in the user's time zone
        ZonedDateTime userDateTime = dateTime.atZone(userTimeZone);

        // Convert the ZonedDateTime object to a new ZonedDateTime object in the central time zone
        ZonedDateTime centralDateTime = userDateTime.withZoneSameInstant(centralTimeZone);

        // Define a format for the date and time string that is compatible with MySQL's DATETIME type
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the centralDateTime object as a string using the specified format
        String centralDateTimeStr = centralDateTime.format(formatter);

        // Return the formatted string
        return centralDateTimeStr;
    }

    /**
     * The convertToUserTime method takes a String typically from the database associated with a value with DateTime type,
     * and converts it from central time to the users local time for the purpose of display in the GUI.
     *
     * @param centralDateTimeStr
     * @return String
     */
    public static String convertToUserTime(String centralDateTimeStr) {
        // Parse the central time string to a ZonedDateTime object
        ZonedDateTime centralDateTime = ZonedDateTime.parse(centralDateTimeStr, DATE_TIME_FORMATTER.withZone(CENTRAL_TIME_ZONE));

        // Get the user's time zone
        ZoneId userTimeZone = ZoneId.systemDefault();

        // Convert the central time to the user's time zone
        ZonedDateTime userDateTime = centralDateTime.withZoneSameInstant(userTimeZone);

        // Format the user date and time as a string
        return DATE_TIME_FORMATTER.format(userDateTime);
    }

    /**
     * The populateAppointmentsTableByMonth() is used for when the user selects the Radio Button with the display Month.
     * This changes what appointments are seen in the Table View. It shows all of the appointments scheduled for the
     * current month .
     * This method returns no value and takes no parameters.
     */
    private void populateAppointmentsTableByMonth() {
        appointments.clear();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        String query = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, "
                + "contacts.Contact_Name, appointments.Type, appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID "
                + "FROM appointments "
                + "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID "
                + "WHERE MONTH(appointments.Start) = MONTH(NOW()) AND YEAR(appointments.Start) = YEAR(NOW())";

        try {
            ResultSet rs = connect.createStatement().executeQuery(query);
            //this.appointments = FXCollections.observableArrayList();
            while (rs.next()) {
                appointments.add(new Appointment(rs.getInt(("Appointment_ID")), rs.getInt("Customer_ID"), rs.getString("Title"),
                        rs.getString("Description"), rs.getString("Location"), rs.getString("Contact_Name"),
                        rs.getString("Type"), convertToUserTime(rs.getString("Start")), convertToUserTime(rs.getString("End")), rs.getInt("User_ID")));
            }
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * The populateAppointmentsTableByWeek() is used for when the user selects the Radio Button with the display Week.
     * This changes what appointments are seen in the Table View. It shows all of the appointments scheduled for the
     * current week and leaves out the rest .
     * This method returns no value and takes no parameters.
     */
    private void populateAppointmentsTableByWeek() {
        appointments.clear();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        String query = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, "
                + "contacts.Contact_Name, appointments.Type, appointments.Start,appointments.End,appointments.Customer_ID,appointments.User_ID "
                + "FROM appointments "
                + "INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID "
                + "WHERE WEEK(appointments.Start) = WEEK(NOW()) AND YEAR(appointments.Start) = YEAR(NOW())";
        ;

        try {
            ResultSet rs = connect.createStatement().executeQuery(query);
            //this.appointments = FXCollections.observableArrayList();
            while (rs.next()) {
                appointments.add(new Appointment(rs.getInt(("Appointment_ID")), rs.getInt("Customer_ID"), rs.getString("Title"),
                        rs.getString("Description"), rs.getString("Location"), rs.getString("Contact_Name"),
                        rs.getString("Type"), convertToUserTime(rs.getString("Start")), convertToUserTime(rs.getString("End")), rs.getInt("User_ID")));
            }
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The goBacktoCustomerPage() method is responsible for closing the current stage, creating a new one and sending the
     * user to Page associated with the customer information. This method is linked to the Customers button.
     * @throws IOException
     */
    private void goBacktoCustomerPage() throws IOException {
        this.stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("displayCustomer.fxml"));
        Stage stage = new Stage();
        DisplayCustomerController dcc = new DisplayCustomerController(stage, loginuserID);
        loader.setController(dcc);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The handleAppointmentTableMouseClicked() defines what is to happen when a row within the Appointment Table View is clicked.
     * It takes all the values defined in that row and then populates them into their appropriate textfields and combo
     * boxes within the user interface.
     */
    @FXML
    private void handleAppointmentTableMouseClicked() {

        Appointment appointment = tv_AppointmentList.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            lb_appointment_id.setText(String.valueOf(appointment.getAppointment_ID()));
            cb_customer_ID.setValue("" + appointment.getCustomer_ID());
            tf_title.setText(appointment.getTitle());
            tf_description.setText(appointment.getDescription());
            tf_Location.setText(appointment.getLocation());
            cb_Contact.setValue(appointment.getContact());
            tf_type.setText(appointment.getType());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // specify the pattern of the date string
            String fullStartDateTime = appointment.getStart_DateTime();
            String fullEndDateTime = appointment.getEnd_DateTime();
            String[] parts1a = fullStartDateTime.split(" ");
            String startTime = parts1a[1];
            String[] parts1b = startTime.split(":");
            startTime = parts1b[0] + ":" + parts1b[1];

            String[] parts2a = fullEndDateTime.split(" ");
            String endTime = parts2a[1];
            String[] parts2b = endTime.split(":");
            endTime = parts2b[0] + ":" + parts2b[1];
            tf_startTime.setText(startTime);
            tf_endTime.setText(endTime);
            LocalDate localDateStart = LocalDate.parse(fullStartDateTime, formatter); // parse the date string into a LocalDate object
            dp_StartDate.setValue(localDateStart); // set the value of the datepicker to the LocalDate object
            LocalDate localDateEnd = LocalDate.parse(fullEndDateTime, formatter);
            dp_EndDate.setValue(localDateEnd);


            cb_userID.setValue(("" + appointment.getUser_ID()));

        }
    }


    /**
     * The checkforOverlappingAppointments() takes the values takes from the DatePicker objects and Textfields associated
     * with the End and Start date and times. It takes these Dates and times and converts them from local time to central
     * time. Once converted it then checks them with other data in the Appointments table to ensure there are no conflicts
     * or overlapping dates and times. If a conflict is found then it returns true so the user can be notified to pick another time.
     * @param startDate
     * @param startTimeStr
     * @param endDate
     * @param endTimeStr
     * @return boolean
     */
    private boolean checkforOverlappingAppointments(LocalDate startDate, String startTimeStr, LocalDate endDate, String endTimeStr) {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        // Convert start and end times to central time
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.parse(startTimeStr));
        String centralStartTimeStr = convertToCentralTime(startDateTime.toLocalDate(), startDateTime.toLocalTime().toString());
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.parse(endTimeStr));
        String centralEndTimeStr = convertToCentralTime(endDateTime.toLocalDate(), endDateTime.toLocalTime().toString());

        // Check for overlapping appointments in the database
        try {
            String query = "SELECT * FROM appointments WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR Start BETWEEN ? AND ? OR End BETWEEN ? AND ?)";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, centralStartTimeStr);
            statement.setString(2, centralEndTimeStr);
            statement.setString(3, centralStartTimeStr);
            statement.setString(4, centralEndTimeStr);
            statement.setString(5, centralStartTimeStr);
            statement.setString(6, centralEndTimeStr);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

