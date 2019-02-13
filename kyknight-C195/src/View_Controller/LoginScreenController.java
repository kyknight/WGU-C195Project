/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import static Model.DBManager.checkLoginCred;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class LoginScreenController implements Initializable {

    //label
    @FXML private Label appLabel, usernameLabel, passwordLabel, loginErrorMessageLabel;
    
    //textfield
    @FXML private TextField usernameTextField, passwordTextField;
    
    //button
    @FXML private Button loginButton;
    
    // used to signal whether DB error needs to be displayed
    public static int databaseError = 0;
    
    /**
     * This method sets the labels and text fields to the local language.
     */
    private void setLanguage(){
        ResourceBundle rb = ResourceBundle.getBundle("Resources/Login", Locale.getDefault());
        appLabel.setText(rb.getString("appLabel"));
        usernameLabel.setText(rb.getString("usernameLabel"));
        passwordLabel.setText(rb.getString("passwordLabel"));
        loginButton.setText(rb.getString("loginButton"));
    }
    
    /**
     *  When the login button is selected, this method submits the user-entered credentials then verifies
     * the credentials. If not verified, error messages display. If verified, the user is redirected to the
     * main screen.
     * @param event 
     */
    private void LoginButtonPushed(ActionEvent event){
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        passwordTextField.setText("");
        ResourceBundle rb = ResourceBundle.getBundle("Resources/Login", Locale.getDefault());
        
        // If username or password are blank, returns error message
        if (username.equals("") || password.equals("")) {
            loginErrorMessageLabel.setText(rb.getString("noUserPassLabel"));
            return;
        }
        boolean credentialsCorrect = checkLoginCred(username, password);
        if (credentialsCorrect == true){
            try {
                // Show main screen
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else if (databaseError > 0){
            loginErrorMessageLabel.setText(rb.getString("connectionErrorLabel"));
        } else {
            loginErrorMessageLabel.setText(rb.getString("incorrectUserPassLabel"));
        }
    }
    
    /**
     * This method increments the databaseError signal
     */
    public static void IncrementDatabaseError() {
        databaseError++;
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        loginButton.setOnAction(event -> LoginButtonPushed(event));
    }    
    
}
