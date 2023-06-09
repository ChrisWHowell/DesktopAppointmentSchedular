package c185.chris_howell_c185app;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.Event;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * @author Chris Howell
 * @version 1.0
 * The DisplayCustomerController class is responsible for managing the user interface and
 * interactions for the Customer management view represented by the displayCustomer.fxml.
 */

public class DisplayCustomerController implements Initializable {
    @FXML
    private ComboBox cb_Country, cb_state;
    @FXML
    private TextField tf_PostalC, tf_Phone, tf_Name, tf_address;
    @FXML
    private Label lb_custID;
    @FXML
    private TableView<Customer> tv_CustomerList;
    @FXML
    private Button btn_Filter, btn_SaveAsEdit, btn_SaveAsNew, btn_viewAppointments, btn_ResetFilter, bt_Delete,btn_viewReports;
    @FXML
    private TableColumn<Customer, Integer> col_customerId;
    @FXML
    private TableColumn<Customer, String> col_customerName;
    @FXML
    private TableColumn<Customer, String> col_customerAdd;
    @FXML
    private TableColumn<Customer, String> col_customerPC;
    @FXML
    private TableColumn<Customer, String> col_customerDiv;
    @FXML
    private TableColumn<Customer, String> col_customerCountry;
    @FXML
    private TableColumn<Customer, String> col_customerPhone;
    private ObservableList<String> countryList;
    private ObservableList<String> stateList;
    private Stage stage;
    private String user_id;

    /**
     * The DisplayCustomerController Constructor creates an object.It also sets the class variable stage associated with the GUI page, as well
     * as the User Name of the person currently logged into the Application
     * @param stage
     * @param user_id
     */
    public DisplayCustomerController(Stage stage, String user_id) {
        this.stage = stage;
        this.user_id = user_id;
    }

    /**
     * The initialize method handles the initialization of the fxml objects and creates the listeners for those
     * objects that require listeners. It handles calls to method to fill the combo boxes and Table Views with data.
     * It also calls the methods that relate to the specific events associated with mouse clicks. This method also
     * contains two lambda expressions for handling Mouse click events and are described in more detail above where
     * they are executed
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize the country combobox
        initCountryComboBox();

        // initialize the state combobox
        initStateComboBox();

        /**
         * Below is a lambda expression. Its purpose is to handle the event of changing the value stored in the
         * ComboBox associated with the country. By using the lambda expression it makes the code much more readable
         * and compact.
         */
        cb_Country.setOnAction((Event event) -> onCountrySelect(event));

        col_customerId.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        col_customerName.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        col_customerAdd.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        col_customerPC.setCellValueFactory(new PropertyValueFactory<Customer, String>("postal_code"));
        col_customerPhone.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
        col_customerDiv.setCellValueFactory(new PropertyValueFactory<Customer, String>("division"));
        col_customerCountry.setCellValueFactory(new PropertyValueFactory<Customer, String>("country"));

        tv_CustomerList.setItems(getCustomers());

        // Add listener to TableView to handle row clicks
        /**
         * Below is a lambda expression. Its purpose is to handle the event when the user selects a row from the
         * TableView containing the list of customers. By using the lambda expression it makes the code much more readable
         * and compact. It calls the method that populates the values into the boxes above associated with the customer
         * selected. The remaining objects are handled without using a lambda expression and can be seen to have much more
         * to them to do the same thing.
         */
        tv_CustomerList.setOnMouseClicked((MouseEvent event) -> handleCustomerTableMouseClicked(event));


        btn_Filter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    handleFilter(event);
                }
            }
        });

        btn_SaveAsEdit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    handleSaveAsEdit(event);
                }
            }
        });
        btn_SaveAsNew.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    handleSaveAsNew(event);
                }
            }
        });
        btn_viewAppointments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    try{
                        handleViewAppointments(event);
                    }catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception as needed
                    }

                }
            }
        });
        btn_ResetFilter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    handleResetFilter();
                }
            }
        });
        bt_Delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    handleDeleteCustomer(event);
                }
            }
        });
        btn_viewReports.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    try{
                        goToReports();
                    }catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception as needed
                    }

                }
            }
        });
    }

    /**
     * The initCountryComboBox is responsible for populating the Combo Box that provides all the countries
     * listed in the database
     */
    private void initCountryComboBox() {
        // get the list of countries from the database
        List<String> countries = getCountries();

        // create an observable list and set it to the country combobox
        countryList = FXCollections.observableArrayList(countries);
        cb_Country.setItems(countryList);

        // set the default selection to the first item
        if (!countryList.isEmpty()) {
            cb_Country.getSelectionModel().select(0);
        }
    }

    /**
     * The initStateComboBox is responsible for populating the Combo Box that provides all the first level
     * divisions listed in the database
     */
    private void initStateComboBox() {
        // get the default selection
        String country = (String) cb_Country.getSelectionModel().getSelectedItem();

        // get the list of states from the database
        List<String> states = getStates(country);

        // create an observable list and set it to the state combobox
        stateList = FXCollections.observableArrayList(states);
        cb_state.setItems(stateList);

        // set the default selection to the first item
        if (!stateList.isEmpty()) {
            cb_state.getSelectionModel().select(0);
        }
    }

    /**
     * The getCountries method interacts with the database to get the countries and return them in a list
     * to be used by the initCountryComboBox method
     * @return List<String>
     */
    private List<String> getCountries() {
        List<String> countries = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            // prepare the SQL statement
            String sql = "SELECT Country FROM countries";
            PreparedStatement stmt = connect.prepareStatement(sql);

            // execute the query
            ResultSet rs = stmt.executeQuery();

            // get the results and add them to the list
            while (rs.next()) {
                countries.add(rs.getString("Country"));
            }
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return countries;
    }

    /**
     * The getStates method interacts with the database to get the first level divisions and return them in a list
     * to be used by the initStateComboBox() method
     * @param country
     * @return List<String>
     */
    private List<String> getStates(String country) {
        List<String> states = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            // prepare the SQL statement
            String sql = "SELECT Division FROM first_level_divisions WHERE Country_ID = ?";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.setInt(1, getCountryId(country));

            // execute the query
            ResultSet rs = stmt.executeQuery();

            // get the results and add them to the list
            while (rs.next()) {
                states.add(rs.getString("Division"));
            }
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return states;
    }

    /**
     * Gets the Country ID from the database associated with the Country Name
     * @param country
     * @return int CountryId
     * @throws SQLException
     */
    private int getCountryId(String country) throws SQLException {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        String query = "SELECT Country_ID FROM countries WHERE Country=?";
        PreparedStatement statement = connect.prepareStatement(query);
        statement.setString(1, country);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int x = resultSet.getInt("Country_ID");
            connect.close();
            return x;

        } else {
            connect.close();
            return -1; // Country not found
        }

    }

    /**
     * onCountrySelect method is responsible for altering the combo box associated with first level divisions so that
     * only those divisions associated with the chosen countries are displayed.
     * @param event
     */
    @FXML
    private void onCountrySelect(Event event) {
        // Get the selected country from the combo box
        String selectedCountry = (String) cb_Country.getSelectionModel().getSelectedItem();

        // Get the list of first level divisions for the selected country
        List<String> firstLevelDivisions = getStates(String.valueOf(selectedCountry));

        // Clear the state combo box and add the new options
        cb_state.getItems().clear();
        cb_state.getItems().addAll(firstLevelDivisions);

    }

    /**
     * The getCustomers method is responsible for getting all the Customer data from the database. It creates a Customer
     * object for each row in the table and the adds each object to a ObservableList. Once completed it returns the list.
     * @return ObservableList<Customer>
     */
    private ObservableList<Customer> getCustomers() {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        // retrieve data from the customers, first_level_divisions, and countries tables and join them
        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, first_level_divisions.Division, countries.Country "
                + "FROM customers "
                + "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID "
                + "INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
        try {
            // execute the query and return the results as an ObservableList
            ResultSet rs = connect.createStatement().executeQuery(query);
            ObservableList<Customer> customers = FXCollections.observableArrayList();
            while (rs.next()) {
                customers.add(new Customer(rs.getInt(("Customer_ID")), rs.getString("Customer_Name"), rs.getString("Address"),
                        rs.getString("Postal_Code"), rs.getString("Phone"), rs.getString("Division"), rs.getString("Country")));
            }
            connect.close();
            return customers;
        } catch (SQLException e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * The handleCustomerTableMouseClicked method is called when the user clicks on a specific row within the Tableview.
     * It then takes the data in the Customer object associated with that row and populates the data in appropriate fields
     * at the top of the application.
     * @param event
     */
    @FXML
    private void handleCustomerTableMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Customer customer = tv_CustomerList.getSelectionModel().getSelectedItem();
            if (customer != null) {
                lb_custID.setText(String.valueOf(customer.getId()));
                tf_Name.setText(customer.getName());
                tf_address.setText(customer.getAddress());
                tf_PostalC.setText(customer.getPostal_code());
                cb_state.setValue(customer.getDivision());
                cb_Country.setValue(customer.getCountry());
                tf_Phone.setText(customer.getPhone());
            }
        }
    }

    /**
     * The handleSaveAsEdit method is triggered by the Save as Edit Button. It takes the values inputted by the user and
     * checks them for specific errors. If it passes the checks then it makes a connection with the Database and changes
     * the row associated with the Customer ID.
     * @param event
     */
    @FXML
    private void handleSaveAsEdit(MouseEvent event) {
        // Get the selected values from the UI controls
        String name = tf_Name.getText();
        String address = tf_address.getText();
        String postalCode = tf_PostalC.getText();
        String phone = tf_Phone.getText();
        String country = cb_Country.getValue().toString();
        String state = cb_state.getValue().toString();
        int customerId = 0;// Integer.parseInt(lb_custID.getText());
        int division_ID = 1;


        if (!lb_custID.getText().matches("\\d+") || Integer.parseInt(lb_custID.getText()) <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer from the customer list below so that a customer ID is selected.");
            alert.showAndWait();
            return;
        }
        // Validate that all fields are filled in
        else if (name.isEmpty() || address.isEmpty() || postalCode.isEmpty() || phone.isEmpty() || country.isEmpty() || state.isEmpty()) {
            // Show an error message if any required field is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }
        customerId = Integer.parseInt(lb_custID.getText());

        // Update the customer information in the database
        try {
            DatabaseConnection dBC = new DatabaseConnection();
            Connection connect = dBC.getConnection();
            String query = "Select Division_ID FROM first_level_divisions WHERE Division = '" + state + "';";
            ResultSet rs = connect.createStatement().executeQuery(query);
            while (rs.next()) {
                division_ID = rs.getInt("Division_ID");
            }

            PreparedStatement stmt = connect.prepareStatement("UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                    "Last_Update = ?," + " Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?");
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, postalCode);
            stmt.setString(4, phone);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(6, user_id);
            stmt.setInt(7, division_ID);
            stmt.setInt(8, customerId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 1) {
                // Show a success message if the customer information was updated successfully
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Customer information updated successfully.");
                alert.showAndWait();
                tv_CustomerList.getItems().clear();
                tv_CustomerList.getItems().addAll(getCustomers());

            } else {
                // Show an error message if the customer information was not updated
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Unable to update customer information.");
                alert.showAndWait();
            }
            connect.close();
        } catch (SQLException ex) {
            // Show an error message if there was a database error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to update customer information. " + ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * The handleSaveAsNew method is called when the user clicks the Save as New Button. It ignores any value stored in
     * the Customer Id .It checks the data inputed by the user for specific errors . If it passes these checks then it
     * creates a new row in the customer table with the data provided
     * @param event
     */
    private void handleSaveAsNew(MouseEvent event) {
        int division_ID = 1;
        String name = tf_Name.getText();
        String address = tf_address.getText();
        String postalCode = tf_PostalC.getText();
        String phone = tf_Phone.getText();
        String country = (String) cb_Country.getValue();
        String state = (String) cb_state.getValue();

        Timestamp now = new Timestamp(System.currentTimeMillis());


        if (name.isEmpty() || address.isEmpty() || postalCode.isEmpty() || phone.isEmpty() || country == null || state == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }

        try {
            DatabaseConnection dBC = new DatabaseConnection();
            Connection connect = dBC.getConnection();
            String query = "Select Division_ID FROM first_level_divisions WHERE Division = '" + state + "';";
            ResultSet rs = connect.createStatement().executeQuery(query);
            while (rs.next()) {
                division_ID = rs.getInt("Division_ID");
            }

            PreparedStatement stmt = connect.prepareStatement(
                    "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, postalCode);
            stmt.setString(4, phone);
            stmt.setTimestamp(5, now);
            stmt.setString(6, user_id);
            stmt.setTimestamp(7, now);
            stmt.setString(8, user_id);
            stmt.setInt(9, division_ID);
            stmt.executeUpdate();

            // refresh the table view to show the new customer
            tv_CustomerList.getItems().clear();
            tv_CustomerList.getItems().addAll(getCustomers());
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    /**
     * The handleFilter method is called when the user clicks the Filter button. it takes the values stored in the FXML
     * input objects in top of the application and filters the values in the TableView to only include inputed values.
     * @param event
     */
    @FXML
    private void handleFilter(MouseEvent event) {
        // Get filter values
        String country = (String) cb_Country.getValue();
        String state = (String) cb_state.getValue();
        String postalCode = tf_PostalC.getText();
        String phone = tf_Phone.getText();
        String name = tf_Name.getText();
        String address = tf_address.getText();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();

        String query = "SELECT * FROM customers " +
                "JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                "JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID " +
                "WHERE ";

        boolean hasPreviousFilter = false;

        if (country != null && !country.isEmpty()) {
            query += "countries.Country = '" + country + "'";
            hasPreviousFilter = true;
        }

        if (state != null && !state.isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "first_level_divisions.Division = '" + state + "'";
            hasPreviousFilter = true;
        }

        if (postalCode != null && !postalCode.isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Postal_Code = '" + postalCode + "'";
            hasPreviousFilter = true;
        }

        if (phone != null && !phone.isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Phone = '" + phone + "'";
            hasPreviousFilter = true;
        }

        if (name != null && !name.isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Customer_Name = '" + name + "'";
            hasPreviousFilter = true;
        }

        if (address != null && !address.isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Address = '" + address + "'";
        }

        // Execute the query and update the table view
        try {
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ObservableList<Customer> customerList = FXCollections.observableArrayList();
            while (rs.next()) {
                customerList.add(new Customer(rs.getInt(("Customer_ID")), rs.getString("Customer_Name"), rs.getString("Address"),
                        rs.getString("Postal_Code"), rs.getString("Phone"), rs.getString("Division"), rs.getString("Country")));
            }
            connect.close();
            tv_CustomerList.setItems(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The handleResetFilter Clears the values stored in the objects in top of application and repopulates the data
     * in the TableView without any filter being applied on them
     */
    @FXML
    private void handleResetFilter() {

        cb_state.getSelectionModel().clearSelection();
        tf_PostalC.setText("");
        tf_Phone.setText("");
        tf_Name.setText("");
        tf_address.setText("");
        tv_CustomerList.getItems().clear();
        tv_CustomerList.setItems(getCustomers());
    }

    /**
     * The handleDeleteCustomer method is called when the user clicks the Delete Customer button. It checks to see if
     * the user has clicked on a specific customer by checking the label associated with the Customer ID. If the value
     * stored there is a number then a connection is made with the database. It then deletes the Customer from the
     * customer table.
     * @param event
     */
    @FXML
    private void handleDeleteCustomer(MouseEvent event) {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        // Get the customer ID from the lb_custID label
        String customerIdStr = lb_custID.getText();

        // Check that the customer ID string contains only digits and is greater than 0
        if (!customerIdStr.matches("\\d+") || Integer.parseInt(customerIdStr) <= 0) {
            // Display an error message and return
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid customer ID. Please select a customer from the table below");
            alert.showAndWait();
            return;
        }
        //converts string to integer
        int customerId = Integer.parseInt(customerIdStr);

        // Check if there are appointments for this customer
        boolean hasAppointments = false;
        try {
            PreparedStatement appointmentQuery = connect.prepareStatement(
                    "SELECT * FROM appointments WHERE Customer_ID = ?"
            );
            appointmentQuery.setInt(1, customerId);
            ResultSet appointmentResult = appointmentQuery.executeQuery();
            hasAppointments = appointmentResult.next();
        } catch (SQLException e) {
            // Display an error message and return
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error checking for appointments");
            alert.showAndWait();

            return;
        }

        // If there are appointments, ask the user if they want to proceed
        if (hasAppointments) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setContentText("This customer has appointments. Are you sure you want to delete them?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return;
            }

            // Delete all appointments for this customer
            try {
                PreparedStatement deleteAppointments = connect.prepareStatement(
                        "DELETE FROM appointments WHERE Customer_ID = ?"
                );
                deleteAppointments.setInt(1, customerId);
                deleteAppointments.executeUpdate();

            } catch (SQLException e) {
                // Display an error message and return
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Error deleting appointments");
                alert.showAndWait();
                return;
            }
        }

        // Delete the customer
        try {
            PreparedStatement deleteCustomer = connect.prepareStatement(
                    "DELETE FROM customers WHERE Customer_ID = ?"
            );
            deleteCustomer.setInt(1, customerId);
            deleteCustomer.executeUpdate();
        } catch (SQLException e) {
            // Display an error message and return
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error deleting customer");
            alert.showAndWait();
            return;
        }

        // Display a success message
        handleResetFilter();
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setContentText("Customer deleted successfully");
        successAlert.showAndWait();

        // Clear the form
        clearForm();
    }

    /**
     * The handleViewAppointments method is responsible for redirecting the user to the Appointments page of the application
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleViewAppointments(MouseEvent event)  throws IOException {
        this.stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("appointmentsView.fxml"));
        Stage stage = new Stage();
        AppointmentsViewController avc = new AppointmentsViewController(stage,user_id);
        loader.setController(avc);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The clearForm method is responsible for clearing the data stored in the JavaFX object in the top of the application
     */
    private void clearForm() {
        cb_state.getSelectionModel().clearSelection();
        tf_PostalC.clear();
        tf_Phone.clear();
        tf_Name.clear();
        tf_address.clear();
        lb_custID.setText("#");
    }

    /**
     * The goToReports() method is responsible for redirecting the user to the Reports page of the application
     * @throws IOException
     */
    private void goToReports() throws IOException{
        this.stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewreports.fxml"));
        Stage stage = new Stage();
        ReportsController rc = new ReportsController(stage,user_id);
        loader.setController(rc);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
