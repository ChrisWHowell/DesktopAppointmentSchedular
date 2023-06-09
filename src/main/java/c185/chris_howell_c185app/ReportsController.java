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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Chris Howell
 * @version 1.0
 * The ReportsController class is responsible for managing the user interface and
 * interactions for the Reports page represented by the viewreports.fxml.
 */
public class ReportsController implements Initializable {
    @FXML
    private Button btn_GoToCustomers,btn_GoToAppointments,btn_reanalyse;
    @FXML
    private Tab tab_ContactSchedules,tab_MonthlyTotal,tab_TypeTotals,tab_loginStats;
    @FXML
    private TableColumn col_Type,col_typeTotal,col_Month,col_MonthTotals,col_AppointmentID,col_Title,col_TypeCon,col_Description,col_StartDateTime,col_EndDateTime,col_CustomerID;
    @FXML
    private TableColumn col_Users,col_successful,col_Failed_Count,col_LastUpdate;
    @FXML
    private TableView tv_MontlyTotals,tv_TypeTotals,tv_ContactSchedule,tv_LoginStats;
    @FXML
    ComboBox cb_Contacts;
    private ObservableList<String> contactOList;
    private Stage stage;
    private String loginuserID;
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private ObservableList<MonthlyTotal> months = FXCollections.observableArrayList();
    private ObservableList<TypeCount> typecounts = FXCollections.observableArrayList();
    private ObservableList<LoginStats> loginStatsList = FXCollections.observableArrayList();
    @FXML
    private DatePicker dp_StartDate,dp_EndDate;

    /**
     * The ReportsController constructor to assign the stage and the User Name of the user currently logged in
     * @param stage
     * @param loginuserID
     */
    public ReportsController(Stage stage, String loginuserID) {
        this.stage = stage;
        this.loginuserID = loginuserID;
    }

    /**
     * The initialize method is responsible for initializing the FXML object and setting the listeners for the
     * appropriate objects that need them. It also calls the methods used to fill the combo boxes and Table Views with
     * data.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initContactComboBox();


        col_Type.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        col_typeTotal.setCellValueFactory(new PropertyValueFactory<>("appointmentCount"));
        try{
            this.typecounts = initTypeCountList();
        }catch(SQLException e){
            e.printStackTrace();
        }
        tv_TypeTotals.setItems(typecounts);

        col_Month.setCellValueFactory(new PropertyValueFactory<>("monthName"));
        col_MonthTotals.setCellValueFactory(new PropertyValueFactory<>("appointmentCount"));

        try{
            this.months = initMonthList();
        }catch(SQLException e){
            e.printStackTrace();
        }
        tv_MontlyTotals.setItems(months);

        //below is lambda function call to handle the click listener for the combobox
        //This is a far more efficient and compact way of coding the event to direct it to the method call
        //Its also much easier to read
        //cb_Contacts.setOnAction(e -> populateAppointmentsByContact());

        col_AppointmentID.setCellValueFactory(new PropertyValueFactory<>("appointment_ID"));
        col_Title.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_Description.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_TypeCon.setCellValueFactory(new PropertyValueFactory<>("type"));
        col_StartDateTime.setCellValueFactory(new PropertyValueFactory<>("start_DateTime"));
        col_EndDateTime.setCellValueFactory(new PropertyValueFactory<>("end_DateTime"));
        col_CustomerID.setCellValueFactory(new PropertyValueFactory<>("customer_ID"));
        tv_ContactSchedule.setItems(appointments);
        populateAppointmentsByContact();
        cb_Contacts.setOnAction(e -> populateAppointmentsByContact());


        col_Users.setCellValueFactory(new PropertyValueFactory<>("userName"));
        col_successful.setCellValueFactory(new PropertyValueFactory<>("countSuccess"));
        col_Failed_Count.setCellValueFactory(new PropertyValueFactory<>("countFailed"));
        col_LastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastAttempt"));
        populateLoginStatsList();

        btn_GoToCustomers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    try{
                        goToCustomers();
                    }catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception as needed
                    }

                }
            }
        });
        btn_GoToAppointments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    try{
                        goToAppointments();
                    }catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception as needed
                    }

                }
            }
        });
        btn_reanalyse.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                        populateLoginStatsList();
                }


            }
        });
    }

    /**
     *The populateAppointmentsByContact() method takes the selected Contact in the combo box and then populates the
     * Table View with all the appointments for that specific Contact
     */
    private void populateAppointmentsByContact() {
        String selectedContact = (String) cb_Contacts.getValue();
        appointments.clear();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String sql = "SELECT * FROM appointments WHERE Contact_ID = (SELECT Contact_ID FROM contacts WHERE Contact_Name = ?)";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.setString(1, selectedContact);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(rs.getInt("Appointment_ID"), rs.getInt("Customer_ID"),
                        rs.getString("Title"), rs.getString("Description"), rs.getString("Location"),
                        rs.getString("Contact_ID"), rs.getString("Type"), rs.getTimestamp("Start").toString(),
                        rs.getTimestamp("End").toString(), rs.getInt("User_ID"));
                appointments.add(appointment);
            }
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tv_ContactSchedule.setItems(appointments);
    }

    /**
     *The initContactComboBox() is responsible for populating the Combo Box in GUI with all the contact names stored in
     * the database.
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
            cb_Contacts.setItems(contactOList);
            if (!contactOList.isEmpty()) {
                cb_Contacts.getSelectionModel().select(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The goToCustomers() method is called when the user Clicks the Customers Button . It then redirects the user to
     * the Customers page.
     * @see DisplayCustomerController
     * @throws IOException
     */
    private void goToCustomers() throws IOException{
        this.stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("displayCustomer.fxml"));
        Stage stage = new Stage();
        DisplayCustomerController dcc = new DisplayCustomerController(stage,loginuserID);
        loader.setController(dcc);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The goToAppointments() method is called when the button Appointments is clicked. It redirects the user to the
     * Appointments page
     * @see AppointmentsViewController
     * @throws IOException
     */
    private void goToAppointments() throws IOException {
        this.stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("appointmentsView.fxml"));
        Stage stage = new Stage();
        AppointmentsViewController avc = new AppointmentsViewController(stage,loginuserID);
        loader.setController(avc);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The initMonthList() method creates MonthlyTotal for each month of the year . It then connects to the database to
     * count and store the amount of appointments for each month
     * @return ObservableList<MonthlyTotal>
     * @throws SQLException
     */
    private ObservableList<MonthlyTotal> initMonthList() throws SQLException {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        ArrayList<MonthlyTotal> months = new ArrayList<>();
        months.add(new MonthlyTotal("January", 0));
        months.add(new MonthlyTotal("February", 0));
        months.add(new MonthlyTotal("March", 0));
        months.add(new MonthlyTotal("April", 0));
        months.add(new MonthlyTotal("May", 0));
        months.add(new MonthlyTotal("June", 0));
        months.add(new MonthlyTotal("July", 0));
        months.add(new MonthlyTotal("August", 0));
        months.add(new MonthlyTotal("September", 0));
        months.add(new MonthlyTotal("October", 0));
        months.add(new MonthlyTotal("November", 0));
        months.add(new MonthlyTotal("December", 0));

        String query = "SELECT MONTH(Start) AS month, COUNT(*) AS count FROM appointments GROUP BY MONTH(Start)";

        PreparedStatement statement = connect.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int month = resultSet.getInt("month");
            int count = resultSet.getInt("count");
            MonthlyTotal monthlyTotal = months.get(month - 1);
            monthlyTotal.setAppointmentCount(count);
        }

        return FXCollections.observableArrayList(months);
    }

    /**
     * The initTypeCountList method is responsible for connecting to the database and retrieving and counting the appointments
     * for the each type so that i can be stored in an ObservableList.
     * @return ObservableList<TypeCount>
     * @throws SQLException
     */
    private ObservableList<TypeCount> initTypeCountList() throws SQLException{
        ArrayList<TypeCount> typeCountAL = new ArrayList<>();

        // Get connection to database
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        // SQL query to get appointment counts by type
        String query = "SELECT Type, COUNT(*) FROM appointments GROUP BY Type";

        // Execute query and get result set
        ResultSet resultSet = connection.createStatement().executeQuery(query);

        // Iterate over result set and create TypeCount objects
        while (resultSet.next()) {
            String typeName = resultSet.getString("Type");
            int appointmentCount = resultSet.getInt("COUNT(*)");
            TypeCount typeCount = new TypeCount(typeName, appointmentCount);
            typeCountAL.add(typeCount);
        }

        // Close database connection and return the type count list
        connection.close();
        typecounts = FXCollections.observableArrayList(typeCountAL);
        return typecounts;
    }

    /**
     *  The populateLoginStatsList method is responsible for taking the data from the Login Text file and filling the
     *  appropriate columns in the Table View in the Login Stats tab
     */
    private void populateLoginStatsList() {
        loginStatsList.clear(); // clear the list before repopulating it

        LocalDate startDate = dp_StartDate.getValue();
        if (startDate == null) {
            startDate = LocalDate.of(2022, 1, 1);
        }

        // Check if end date is null, set default value
        LocalDate endDate = dp_EndDate.getValue();
        if (endDate == null) {
            endDate = LocalDate.of(9999, 12, 31);
        }
        else {
            endDate = dp_EndDate.getValue().plusDays(1); // add one day to end date to include all of it
        }

        try {
            FileReader fileReader = new FileReader("login_activity.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            Map<String, LoginStats> loginStatsMap = new HashMap<>();

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(" ");
                String username = parts[1];
                String loginTimeString = parts[7];
                loginTimeString = loginTimeString.substring(0, parts[7].length() - 1);
                System.out.println("loginTimeString is "+loginTimeString);
                LocalDateTime loginTime = LocalDateTime.parse(loginTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                boolean success = parts[9].equals("successful.");

                if (loginTime.toLocalDate().isBefore(startDate) || loginTime.toLocalDate().isAfter(endDate)) {
                    continue; // skip if login time is not within selected range
                }

                if (!loginStatsMap.containsKey(username)) {
                    LoginStats loginStats = new LoginStats(username);
                    loginStatsMap.put(username, loginStats);
                    loginStatsList.add(loginStats);
                }

                LoginStats loginStats = loginStatsMap.get(username);
                if (success) {
                    loginStats.incrementSuccessfulCount();
                } else {
                    loginStats.incrementFailedCount();
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = loginTime.format(formatter);
                loginStats.setLastAttempt(formattedDateTime.toString());
            }

            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // set the items of the TableView with the updated list
        tv_LoginStats.setItems(loginStatsList);
    }


}
