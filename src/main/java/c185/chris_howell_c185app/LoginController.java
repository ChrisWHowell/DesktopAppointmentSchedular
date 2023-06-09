package c185.chris_howell_c185app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

import static c185.chris_howell_c185app.AppointmentsViewController.convertToCentralTime;
import static c185.chris_howell_c185app.AppointmentsViewController.convertToUserTime;

/**
 * @author Chris Howell
 * @version 1.0
 * The LoginController class is responsible for managing the user interface and
 * interactions for the login page represented by the loginScrean.fxml.
 */
public class LoginController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private Button btn_Cancel,btn_Login;

    @FXML
    private Label lb_warning_error,zoneIdLabel;
    @FXML
    private TextField tf_Username;
    @FXML
    private PasswordField pf_Password;
    private Stage stage;
    private String user_ID;

    /**
     * This Constructor is called to create a LoginController object
     * @param stage
     */
    public LoginController(Stage stage) {
        this.stage = stage;

    }

    /**
     * Default Constructor
     */
    public LoginController() {
        // Default constructor

    }

    /**
     * This method is called after the FXML file has been loaded and the scene graph
     * has been constructed. This method is invoked automatically by the JavaFX runtime when
     * the controller is instantiated. It is responsible for initializing ZoneID of the users area.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ZoneId zoneId = ZoneId.systemDefault();
        zoneIdLabel.setText(zoneId.getId());
    }

    /**
     * The cancelButtonAction is linked to the Cancel Button directly in the fxml file and used to close the application
     * @param aE
     */
    public void cancelButtonAction(ActionEvent aE) {
        Stage stage = (Stage) btn_Cancel.getScene().getWindow();
        stage.close();
    }

    /**
     * The loginButtonAction is linked to the Login Button in the fxml file and takes the inputs in the textfield
     * associated with the user name and password and calls the validate Login to determine if the input credentials are
     * correct
     * @param aE
     */
    public void loginButtonAction(ActionEvent aE) {
        Locale locale = Locale.getDefault();
        //Locale locale = new Locale("fr");
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",locale);


        if (tf_Username.getText().isBlank() == false && pf_Password.getText().isBlank() == false) {
            validateLogin();
        } else {
            lb_warning_error.setText(bundle.getString("errorMes3"));
        }
        //Stage stage = (Stage) btn_Login.getScene().getWindow();
    }

    /**
     * The validateLogin method connects to the database to compare the username and password with the data stored. This
     * method is also responsible for creating and adding input to the text file to log successful and unsuccessful attempts
     * at logging in. It also notifies the user if their login attempt is successful or not
     */
    public void validateLogin() {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        user_ID = tf_Username.getText();
        String verify = "Select * From users WHERE Password = '" + pf_Password.getText() + "' AND User_Name = '" + user_ID + "';";

        Locale locale = Locale.getDefault();
        //Locale locale = new Locale("fr");
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",locale);

        String activityMessage ="";
        boolean success = true;

        try {
            Statement statement = connect.createStatement();
            ResultSet qResult = statement.executeQuery(verify);
            if (qResult.next()) {
                lb_warning_error.setText(bundle.getString("errorMes2"));
                this.stage.close();

                activityMessage = String.format("User %s attempted to log in at %s. Login %s.\n",
                        tf_Username.getText(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), success ? "successful" : "failed");

                try {
                    // Create or append to login_activity.txt
                    FileWriter fileWriter = new FileWriter("login_activity.txt", true);
                    System.out.println("File path: " + new File("login_activity.txt").getAbsolutePath());
                    fileWriter.write(activityMessage);
                    fileWriter.close();

                } catch (IOException e) {
                    System.out.println("Error writing to login activity log file.");
                    e.printStackTrace();
                    connect.close();
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("displayCustomer.fxml"));
                Stage stage = new Stage();
                DisplayCustomerController dcc = new DisplayCustomerController(stage,user_ID);
                loader.setController(dcc);
                Parent root = loader.load();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
                Appointment appointment = checkAppointments();
                if(!(appointment == null)){
                    //code to get the Appointment_ID, date and time
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointments");
                    alert.setContentText("There is an appointment scheduled with ID "+appointment.getAppointment_ID()
                            +" and a Starting date and time of " + appointment.getStart_DateTime());
                    alert.showAndWait();
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointments");
                    alert.setContentText("There are no appointments scheduled in the next 15 min");
                    alert.showAndWait();
                }

                connect.close();
            } else {
                lb_warning_error.setText(bundle.getString("errorMes1"));
                success = false;
                activityMessage = String.format("User %s attempted to log in at %s. Login %s.\n",
                        tf_Username.getText(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), success ? "successful" : "failed");
                connect.close();
                try {
                    // Create or append to login_activity.txt
                    FileWriter fileWriter = new FileWriter("login_activity.txt", true);
                    fileWriter.write(activityMessage);
                    fileWriter.close();

                } catch (IOException e) {
                    System.out.println("Error writing to login activity log file.");
                    e.printStackTrace();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The checkAppointments method is responsible to check to see if any appointments have been scheduled to start
     * in the next 15 min of successfully logging in. The method does not handle notification to the user and must be done
     * by the calling method.
     * @return Appointment
     */
    public Appointment checkAppointments() {
        DatabaseConnection dbCon = new DatabaseConnection();
        Connection conn = dbCon.getConnection();

        LocalDateTime localNow = LocalDateTime.now();
        LocalDateTime local15MinutesLater = localNow.plusMinutes(15);

        String centralStartTimeStr = AppointmentsViewController.convertToCentralTime(localNow.toLocalDate(), localNow.toLocalTime().toString());

        String centralEndTimeStr = AppointmentsViewController.convertToCentralTime(local15MinutesLater.toLocalDate(), local15MinutesLater.toLocalTime().toString());

        try {
            String query = "SELECT * FROM appointments WHERE Start BETWEEN ? AND ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, centralStartTimeStr);
            ps.setString(2, centralEndTimeStr);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int appointment_ID = rs.getInt("Appointment_ID");
                int customer_ID = rs.getInt("Customer_ID");
                String title = "";
                String description = "";
                String location = "";
                String contact = "";
                String type = "";
                String start_DateTime = AppointmentsViewController.convertToUserTime(rs.getString("Start"));
                String end_DateTime = "" ;
                int user_ID = rs.getInt("User_ID");

                Appointment overlappingAppointment = new Appointment(appointment_ID, customer_ID, title, description, location, contact, type, start_DateTime, end_DateTime, user_ID);
                return overlappingAppointment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}