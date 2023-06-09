module c185.chris_howell_c185app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.java;


    opens c185.chris_howell_c185app to javafx.fxml;
    exports c185.chris_howell_c185app;
}