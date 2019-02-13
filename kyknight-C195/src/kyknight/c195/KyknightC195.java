package kyknight.c195;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.TimeZone;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author kyleighknight
 */
public class KyknightC195 extends Application {
    
    @Override
    /**
     * This method displays the login page on initial run of program.
     */
    public void start(Stage stage) throws Exception {
        //Locale.setDefault(new Locale.Builder().setLanguage("en").build());
        //TimeZone.setDefault(TimeZone.getTimeZone("EST"));
        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * @param args 
     * @throws java.lang.ClassNotFoundException 
     */
    public static void main(String[] args) throws ClassNotFoundException{
        launch(args);
    }
    
}
