/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Customer;
import static Model.CustomerList.getCustList;
import static Model.DBManager.calcAddressId;
import static Model.DBManager.calcCityId;
import static Model.DBManager.calcCountryId;
import static Model.DBManager.modCustomer;
import static Model.DBManager.setCustToActive;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class ModifyCustomerController implements Initializable {

    // modify customer labels
    @FXML private Label modifyCustomerScreenLabel, modifyCustomerIDLabel, modifyCustomerNameLabel, modifyCustomerAddress1Label,
            modifyCustomerAddress2Label, modifyCustomerCityLabel, modifyCustomerStateLabel, modifyCustomerZipLabel,
            modifyCustomerCountryLabel, modifyCustomerPhoneLabel;
    
    // modify customer textfields
    @FXML private TextField modifyCustomerIDTextField, modifyCustomerNameTextField, modifyCustomerAddress1TextField,
            modifyCustomerAddress2TextField, modifyCustomerCityTextField, modifyCustomerStateTextField, modifyCustomerZipTextField,
            modifyCustomerCountryTextField, modifyCustomerPhoneTextField;
    
    // modify customer buttons
    @FXML private Button modifyCustomerUpdateButton, modifyCustomerCancelButton;
    
    private Customer customer;
    int custIndexMod = MainScreenController.getCustIndexToMod();
    
    /**
     * When the update button is selected, this method will submit the customer information to the DB 
     * once the entered fields have been validated with the current DB information. If errorMessages = true,
     * a dialog box will display. 
     * @param event 
     */
    private void ModifyCustomerUpdateButtonPushed (ActionEvent event){
        int customerID = customer.getCustId();
        String custName = modifyCustomerNameTextField.getText();
        String address1 = modifyCustomerAddress1TextField.getText();
        String address2 = modifyCustomerAddress2TextField.getText();
        String city = modifyCustomerCityTextField.getText();
        String state = modifyCustomerStateTextField.getText();
        String country = modifyCustomerCountryTextField.getText();
        String zip = modifyCustomerZipTextField.getText();
        String phone = modifyCustomerPhoneTextField.getText();
        
        // customer validation
        String errorMessage = Customer.isCustValid(custName, address1, city, country, zip, phone, state);
        
        // if errorMessage = true, display error dialog box
        if (errorMessage.length() > 0){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        
        // saves the returned active status
        int modifyCustomerChecker = modCustomer(customerID, custName, address1, address2, city, country, zip, phone);
        
        // if active status = 1, display alert dialog box.
        if (modifyCustomerChecker == 1){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("This customer already exists in the database.");
            alert.showAndWait();
        } else if (modifyCustomerChecker == 0){
            int countryID = calcCountryId(country);
            int cityID = calcCityId(city, countryID);
            int addressID = calcAddressId(address1, address2, zip, phone, cityID);
            setCustToActive(custName, addressID);
        }
        
        //Return to main screen
        try {
            Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene mainScreenScene = new Scene(mainScreenParent);
            Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainScreenStage.setScene(mainScreenScene);
            mainScreenStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void ModifyCustomerCancelButtonPushed (ActionEvent event){
        //called the method from AddCustomerController.java because I'm lazy
        AddCustomerController cancelPushed = new AddCustomerController();
        cancelPushed.AddModCustomerCancelButtonPushed(event);
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        modifyCustomerUpdateButton.setOnAction(event -> ModifyCustomerUpdateButtonPushed(event));
        modifyCustomerCancelButton.setOnAction(event -> ModifyCustomerCancelButtonPushed(event));
        
        //grabs customer to be modified
        customer = getCustList().get(custIndexMod);
        
        //grabs current customer information
        Integer custId = customer.getCustId();
        String custName = customer.getCustName();
        String address1 = customer.getAddress1();
        String address2 = customer.getAddress2();
        String city = customer.getCity();
        //String state = customer.getState();
        String country = customer.getCountry();
        String zip = customer.getZipcode();
        String phone = customer.getPhone();
        
        //populates the textfields
        modifyCustomerIDTextField.setText(String.valueOf(custId));
        modifyCustomerNameTextField.setText(custName);
        modifyCustomerAddress1TextField.setText(address1);
        modifyCustomerAddress2TextField.setText(address2);
        modifyCustomerCityTextField.setText(city);
        //modifyCustomerStateTextField.setText(state);
        modifyCustomerCountryTextField.setText(country);
        modifyCustomerZipTextField.setText(zip);
        modifyCustomerPhoneTextField.setText(phone);
    }    
    
}
