/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import static Model.AppointmentList.getAppList;
import Model.Customer;
import static Model.CustomerList.getCustList;
import static Model.DBManager.modApp;
import View_Controller.AddAppointmentController;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class ModifyAppointmentController{

    //Labels
    @FXML private Label modAppScreenLabel, modAppTitleLabel, modAppDescriptionLabel, modAppLocationLabel, modAppContactLabel,
            modAppTypeLabel, modAppUrlLabel, modAppDateLabel, modAppStartLabel, modAppEndLabel, modAppAddTableLabel,
            modAppSelectedTableLabel;
    //created adn updated Labels
    @FXML private Label modAppCreateDateLabel, modAppCreateDateGrabLabel, modAppCreateByLabel, modAppCreateByGrabLabel,
            modAppLastUpdateLabel, modAppLastUpdateGrabLabel, modAppLastUpdateByLabel, modAppLastUpdateByGrabLabel;
    //TextFields
    @FXML private TextField modAppTitleTextField, modAppLocationTextField, modAppContactTextField,
            modAppUrlTextField, modAppStartHourTextField, modAppStartMinuteTextField, modAppEndHourTextField, modAppEndMinuteTextField;
    //Textarea
    @FXML private TextArea modAppDescriptionTextField;
    //Buttons
    @FXML private Button modAppAddButton, modAppSaveButton, modAppCancelButton, modAppDeleteButton;
    
    //DatePicker
    @FXML private DatePicker modAppDatePicker;
    
    //ChoiceBox
    @FXML private ChoiceBox<String> modAppTypeChoiceBox, modAppStartAMPMChoiceBox, modAppEndAMPMChoiceBox;
    
    //TableView
    @FXML private TableView<Customer> addModAppAddTableView;
    @FXML private TableColumn<Customer, String> modAppAddNameTableCol;
    @FXML private TableColumn<Customer, String> modAppAddCityTableCol;
    @FXML private TableColumn<Customer, String> modAppAddCountryTableCol;
    @FXML private TableColumn<Customer, String> modAppAddPhoneTableCol;
    
    @FXML private TableView<Customer> addModAppDeleteTableView;
    @FXML private TableColumn<Customer, String> modAppDeleteNameTableCol;
    @FXML private TableColumn<Customer, String> modAppDeleteCityTableCol;
    @FXML private TableColumn<Customer, String> modAppDeleteCountryTableCol;
    @FXML private TableColumn<Customer, String> modAppDeletePhoneTableCol;
    
    private Appointment appointment;
    
    int appIndexToModify = AppointmentSummaryController.getAppIndexMod();
    // holds the current customers assigned to the appointment
    private ObservableList<Customer> currCust = FXCollections.observableArrayList();
    
    /**
     * This method adds the selected customer to the delete table / lower table - currCust
     * @param event 
     */
    private void ModAppAddButtonPushed(ActionEvent event){
        //called the method from AddAppointmetnController.java because I'm lazy
        AddAppointmentController addPushed = new AddAppointmentController();
        addPushed.AddModAppAddButtonPushed(event);
    }
    
    /**
     * This method deletes the selected customer from the delete table / lower table - currCust
     * @param event 
     */
    private void ModAppDeleteButtonPushed(ActionEvent event){
        //called the method from AddAppointmetnController.java because I'm lazy
        AddAppointmentController deletePushed = new AddAppointmentController();
        deletePushed.AddModAppDeleteButtonPushed(event);
    }
    
    /**
     * This method, on the save button being selected, the app info is submitted to be updated to the DB
     * @param event 
     */
    private void ModAppSaveButtonPushed(ActionEvent event){
        Customer customer = null;
        //checks id  currCust has a customer. If so, gets customer.
        if (currCust.size() == 1){
            customer = currCust.get(0);
        }
        //grabs the app information
        int appId = appointment.getAppId();
        String title = modAppTitleTextField.getText();
        String desc = modAppDescriptionTextField.getText();
        String location = modAppLocationTextField.getText();
        String contact = modAppContactTextField.getText();
        //if contact field empty, sets customers' name and phone #
        if (contact.length() == 0 && customer != null){
            contact = customer.getCustName() + ": " + customer.getPhone();
        }
            String type = modAppTypeChoiceBox.getValue();
        String url = modAppUrlTextField.getText();
        LocalDate appDate = modAppDatePicker.getValue();
        String startHr = modAppStartHourTextField.getText();
        String startMin = modAppStartMinuteTextField.getText();
        String startAmPm = modAppStartAMPMChoiceBox.getSelectionModel().getSelectedItem();
        String endHr = modAppEndHourTextField.getText();
        String endMin = modAppEndMinuteTextField.getText();
        String endAmPm = modAppEndAMPMChoiceBox.getSelectionModel().getSelectedItem();
        
        //submit validation of app information
        String errorMessage = Appointment.isAppValid(customer, title, desc, location, appDate, startHr, startMin, 
                startAmPm, endHr, endMin, endAmPm);
        
        if (errorMessage.length() > 0){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Appointment");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
        
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyy-MM-dd h:mm a");
        localDateFormat.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;
        try {
            startLocal = localDateFormat.parse(appDate.toString() + " " + startHr + ":" + startMin + " " + startAmPm);
            endLocal = localDateFormat.parse(appDate.toString() + " " + endHr + ":" + endMin + " " + endAmPm);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        //create ZonedDateTime with Date objects
        ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        //submits info to be added to DB and checks if 'true' is returned
        if (modApp(appId,customer,title,desc,location,contact,url,startUTC,endUTC)) {
            try {
                Parent appSummaryParent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
                Scene appSummaryScene = new Scene(appSummaryParent);
                Stage appSummaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                appSummaryStage.setScene(appSummaryScene);
                appSummaryStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }
    
    /**
     * This method, when cancel button is selected and 'OK' is selected, will redirect the user to the Main screen.
     * @param event 
     */
    private void ModAppCancelButtonPushed(ActionEvent event){
        //called the method from AddAppointmentController.java because I'm lazy
        AddAppointmentController cancelPushed = new AddAppointmentController();
        cancelPushed.AddModAppCancelButtonPushed(event);
    }
    
    /**
     * Initializes the controller class.
     */
    public void initialize() {
        //buttons assigned actions
        modAppAddButton.setOnAction(event -> ModAppAddButtonPushed(event));
        modAppDeleteButton.setOnAction(event -> ModAppDeleteButtonPushed(event));
        modAppSaveButton.setOnAction(event -> ModAppSaveButtonPushed(event));
        modAppCancelButton.setOnAction(event -> ModAppCancelButtonPushed(event));
        
        appointment = getAppList().get(appIndexToModify);
        //grabs the app info
        String title = appointment.getTitle();
        String desc = appointment.getDesc();
        String location = appointment.getLocation();
        String contact = appointment.getContact();
            String type = appointment.getType();
        String url = appointment.getUrl();
        Date appDate = appointment.getStartTimestamp();
        
        // appDate to LocalDate
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(appDate);
        int appYr = calendar.get(Calendar.YEAR);
        int appMonth = calendar.get(Calendar.MONTH) + 1;
        int appDay = calendar.get(Calendar.DAY_OF_MONTH);
        LocalDate appLocalDate = LocalDate.of(appYr, appMonth, appDay);
        
        //time strings to hour, minute, and am/pm strings
            //start time
        String startString = appointment.getStartString();
        String startHr = startString.substring(0,2);
        if (Integer.parseInt(startHr) < 10){
            startHr = startHr.substring(1,2);
        }
        String startMin = startString.substring(3,5);
        String startAmPm = startString.substring(6,8);
            //end time
        String endString = appointment.getEndString();
        String endHr = endString.substring(0,2);
        if (Integer.parseInt(endHr) < 10){
            endHr = endHr.substring(1,2);
        }
        String endMin = endString.substring(3,5);
        String endAmPm = endString.substring(6,8);
        
        //add currCust by custId
        int custId = appointment.getCustId();
        ObservableList<Customer> custRoster = getCustList();
        custRoster.stream().filter((customer) -> (customer.getCustId() == custId)).forEachOrdered((customer) -> {
            currCust.add(customer);
        });
        
        //populate info fields with current app info
        modAppTitleTextField.setText(title);
        modAppDescriptionTextField.setText(desc);
        modAppLocationTextField.setText(location);
        modAppContactTextField.setText(contact);
        
            modAppTypeChoiceBox.setValue(type);
            //start time
        modAppUrlTextField.setText(url);
        modAppDatePicker.setValue(appLocalDate);
        modAppStartHourTextField.setText(startHr);
        modAppStartMinuteTextField.setText(startMin);
        modAppStartAMPMChoiceBox.setValue(startAmPm);
            //end time
        modAppEndHourTextField.setText(endHr);
        modAppEndMinuteTextField.setText(endMin);
        modAppEndAMPMChoiceBox.setValue(endAmPm);
        
        //grab data to set in tableviews
        modAppAddNameTableCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        modAppAddCityTableCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        modAppAddCountryTableCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        modAppAddPhoneTableCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        modAppDeleteNameTableCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        modAppDeleteCityTableCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        modAppDeleteCountryTableCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        modAppDeletePhoneTableCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
    }      
}
