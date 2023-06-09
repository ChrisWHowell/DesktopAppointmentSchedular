package c185.chris_howell_c185app;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Chris Howell
 * @version 1.0
 * The Main Class is responsible for the execution of the application along with the definition of the
 * start method
 * */
public class Main extends Application {
    @FXML
    Label zoneIdLabel;

    /**
     *Starts the JavaFX application and displays the login screen.
     * This method initializes the stage, sets up the scene, and loads the login screen FXML file using a {@link FXMLLoader}.
     * The method also sets the controller for the FXML file programmatically, passing the stage object to the {@link LoginController} constructor.
     * The stage style is set to {@link StageStyle#UNDECORATED} for a custom window appearance.
     * @param stageLogin
     * @throws IOException
     */
    @Override
    public void start(Stage stageLogin) throws IOException {

        Locale locale = Locale.getDefault();

        ResourceBundle bundle = ResourceBundle.getBundle("labelText",locale);
        System.out.println(bundle.getString("UserLogin"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginScrean.fxml"),bundle);
        LoginController loginController = new LoginController(stageLogin); // pass the stage object to the constructor
        fxmlLoader.setController(loginController); //sets the controller here as opposed to the fxml to set the stage variable in LoginController class
        stageLogin.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stageLogin.setScene(scene);
        stageLogin.show();
    }

    public static void main(String[] args) {
        launch();
    }
}