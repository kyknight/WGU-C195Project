/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Customer;
import static Model.DBManager.addNewCust;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class AddCustomerController implements Initializable {

    // add customer labels
    @FXML private Label addCustomerScreenLabel, addCustomerIDLabel, addCustomerNameLabel, addCustomerAddress1Label,
            addCustomerAddress2Label, addCustomerCityLabel, addCustomerStateLabel, addCustomerZipLabel, addCustomerCountryLabel,
            addCustomerPhoneLabel;
    
    // add customer textfields
    @FXML private TextField addCustomerIDTextField, addCustomerNameTextField, addCustomerAddress1TextField, addCustomerAddress2TextField,
            addCustomerCityTextField, addCustomerStateTextField, addCustomerZipTextField, addCustomerCountryTextField, addCustomerPhoneTextField;
    
    // add customer buttons
    @FXML private Button addCustomerSaveButton, addCustomerCancelButton;
    
    /**
     * When the save button is pushed, this method will submit the customer information to the DB 
     * once the entered fields have been validated. If errorMessages = true, a dialog box will display. 
     * @param event 
     */
    public void AddCustomerSaveButtonPushed (ActionEvent event){
        String custName = addCustomerNameTextField.getText();
        String address1 = addCustomerAddress1TextField.getText();
        String address2 = addCustomerAddress2TextField.getText();
        String city = addCustomerCityTextField.getText();
        String zip = addCustomerZipTextField.getText();
        String country = addCustomerCountryTextField.getText();
        String phone = addCustomerPhoneTextField.getText();
        
        //customer validation
        String errorMessage = Customer.isCustValid(custName, address1, city, zip, country, phone);
        
        //if errorMessage = true, display error dialog box
        if (errorMessage.length() > 0){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Customer");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        
        //if errorMessage = false, add the new customer to the DB and return to the main screen.
        try {
            addNewCust(custName, address1, address2, city, zip, country, phone);
            Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene mainScreenScene = new Scene(mainScreenParent);
            Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainScreenStage.setScene(mainScreenScene);
            mainScreenStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * When the cancel button is selected, this method will display an alert dialog box asking for 
     * confirmation on cancel of record then returns to the main screen when user selects OK on the 
     * confirmation dialog box. 
     * @param event 
     */
    public void AddModCustomerCancelButtonPushed (ActionEvent event){
        // alert box when cancel button is selected
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to cancel adding a new customer?");
        Optional<ButtonType> result = alert.showAndWait();
        
        // if OK button selection = true, returns to main screen
        if (result.get() == ButtonType.OK){
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addCustomerSaveButton.setOnAction(event -> AddCustomerSaveButtonPushed(event));
        addCustomerCancelButton.setOnAction(event -> AddModCustomerCancelButtonPushed(event));
    }    
    
}
