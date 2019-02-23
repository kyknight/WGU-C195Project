/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.Customer;
import Model.CustomerList;
import static Model.DBManager.addNewApp;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class AddAppointmentController implements Initializable {

    //Labels
    @FXML
    private Label addAppScreenLabel, addAppTitleLabel, addAppDescriptionLabel, addAppLocationLabel,
            addAppContactLabel, addAppTypeLabel, addAppUrlLabel, addAppDateLabel, addAppStartLabel,
            addAppEndLabel, addAppAddTableLabel, addAppSelectedTableLabel;
    //TextFields
    @FXML
    private TextField addAppTypeTextField, addAppTitleTextField, addAppLocationTextField, addAppContactTextField,
            addAppUrlTextField, addAppStartHourTextField, addAppStartMinuteTextField, addAppEndHourTextField, addAppEndMinuteTextField;
    //TextArea
    @FXML
    private TextArea addAppDescriptionTextField;
    //Buttons
    @FXML
    private Button addAppAddButton, addAppSaveButton, addAppCancelButton, addAppDeleteButton;

    //DatePicker
    @FXML
    private DatePicker addAppDatePicker;

    //ChoiceBox
    @FXML
    private ChoiceBox<String> addAppStartAMPMChoiceBox, addAppEndAMPMChoiceBox;

    //TableViews
    @FXML
    private TableView<Customer> addModAppAddTableView;
    @FXML
    private TableColumn<Customer, String> addAppAddNameTableCol;
    @FXML
    private TableColumn<Customer, String> addAppAddCityTableCol;
    @FXML
    private TableColumn<Customer, String> addAppAddCountryTableCol;
    @FXML
    private TableColumn<Customer, String> addAppAddPhoneTableCol;

    @FXML
    private TableView<Customer> addModAppDeleteTableView;
    @FXML
    private TableColumn<Customer, String> addAppDeleteNameTableCol;
    @FXML
    private TableColumn<Customer, String> addAppDeleteCityTableCol;
    @FXML
    private TableColumn<Customer, String> addAppDeleteCountryTableCol;
    @FXML
    private TableColumn<Customer, String> addAppDeletePhoneTableCol;

    // holds the current customers assigned to the appointment
    private ObservableList<Customer> currCust = FXCollections.observableArrayList();
    
    private Appointment appointment;

    /**
     * This method adds the selected customer to the delete table / lower table
     * - currCust
     *
     * @param event
     */
    public void AddModAppAddButtonPushed(ActionEvent event) {
        //Get the selected customer from top table 
        Customer customer = addModAppAddTableView.getSelectionModel().getSelectedItem();
        //checks if there is no customer selected
        if (customer == null) {
            //alerts user that a customer must be selected to add
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Customer");
            alert.setContentText("Please select a customer from the table above to add to the appointment.");
            alert.showAndWait();
        }
        //checks if ther is a currCust already contains a customer
        if (currCust.size() > 0) {
            //alerts the user that there can only be one customer selected per appointment 
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Customer");
            alert.setContentText("Only one customer may be added to an appointment.");
            alert.showAndWait();
        }
        //if no currCust, add selected customer
        currCust.add(customer);
        //updates the delete tableview
        AddModAppDeleteTableViewUpdate();
    }

    /**
     * This method deletes the selected customer from the delete table / lower
     * table - currCust
     *
     * @param event
     */
    public void AddModAppDeleteButtonPushed(ActionEvent event) {
        // Get the selected customer from lower table
        Customer customer = addModAppDeleteTableView.getSelectionModel().getSelectedItem();
        //checks if there is no customer selected
        if (customer == null) {
            // alerts user that a customer must be selected to remove
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Removing Customer");
            alert.setContentText("Please select a customer from the table above to remove from the appointment.");
            alert.showAndWait();
        }
        // alert to confirm the removal of customer from app/currCust
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Remove");
        alert.setHeaderText("Confirm Removal of Customer");
        alert.setContentText("Are you sure you want to remove this customer from the appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        // ok selected = remove customer from currCust
        if (result.get() == ButtonType.OK) {
            currCust.remove(customer);
            //updates the delete tableview
            AddModAppDeleteTableViewUpdate();
        }
    }

    /**
     * This method, on the save button being selected, the app info is submitted
     * to be added to the DB
     *
     * @param event
     */
    private void AddAppSaveButtonPushed(ActionEvent event) {
        Customer customer = null;
        //checks id  currCust has a customer. If so, gets customer.
        if (currCust.size() == 1) {
            customer = currCust.get(0);
        }
        //grabs the app information
        String title = addAppTitleTextField.getText();
        String desc = addAppDescriptionTextField.getText();
        String location = addAppLocationTextField.getText();
        String contact = addAppContactTextField.getText();
        //if contact field empty, sets customers' name and phone #
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustName() + ": " + customer.getPhone();
        }
        
        String url = addAppUrlTextField.getText();
        LocalDate appDate = addAppDatePicker.getValue();
        String startHr = addAppStartHourTextField.getText();
        String startMin = addAppStartMinuteTextField.getText();
        String startAmPm = addAppStartAMPMChoiceBox.getSelectionModel().getSelectedItem();
        String endHr = addAppEndHourTextField.getText();
        String endMin = addAppEndMinuteTextField.getText();
        String endAmPm = addAppEndAMPMChoiceBox.getSelectionModel().getSelectedItem();
        String type = addAppTypeTextField.getText();

        //submit validation of app information
        String errorMessage = Appointment.isAppValid(customer, title, desc, location, appDate, startHr, startMin, startAmPm, endHr, endMin, endAmPm, type);

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Appointment");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }

        DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        LocalDateTime startLocal = null;
        LocalDateTime endLocal = null;
        if (startAmPm == null || endAmPm == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("AM or PM selection.");
            alert.setContentText("Please select AM or PM for appointment time.");
            alert.showAndWait();
            return;
        } else {
            startLocal = LocalDateTime.parse(appDate.toString() + " " + startHr + ":" + startMin + " " + startAmPm, localDateFormat);
            endLocal = LocalDateTime.parse(appDate.toString() + " " + endHr + ":" + endMin + " " + endAmPm, localDateFormat);
        }
        //create ZonedDateTime with Date objects
        ZonedDateTime startUTC = startLocal.atZone(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endLocal.atZone(ZoneId.of("UTC"));
        int userId = appointment.getUserId(); //null pointer problems?
        //submits info to be added to DB and checks if 'true' is returned
        if (addNewApp(customer, title, desc, location, contact, url, startUTC, endUTC, type, userId)) {
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
    }

    /**
     * This method, when cancel button is selected and 'OK' is selected, will
     * redirect the user to the main screen.
     *
     * @param event
     */
    public void AddModAppCancelButtonPushed(ActionEvent event) {
        //confirm cancel alert
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to go back to the main screen without saving?");
        Optional<ButtonType> result = alert.showAndWait();
        // If the 'OK' button is clicked, return to main screen
        if (result.get() == ButtonType.OK) {
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
    }

    /**
     * This method updates the add table / top table
     */
    public void AddModAppAddTableViewUpdate() {
        addModAppAddTableView.setItems(CustomerList.getCustList());
    }

    /**
     * This method update the delete table / lower table
     */
    public void AddModAppDeleteTableViewUpdate() {
        addModAppDeleteTableView.setItems(currCust);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //buttons assigned actions
        addAppAddButton.setOnAction(event -> AddModAppAddButtonPushed(event));
        addAppDeleteButton.setOnAction(event -> AddModAppDeleteButtonPushed(event));
        addAppSaveButton.setOnAction(event -> AddAppSaveButtonPushed(event));
        addAppCancelButton.setOnAction(event -> AddModAppCancelButtonPushed(event));

        //grab data to set in tableviews
        addAppAddNameTableCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        addAppAddCityTableCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        addAppAddCountryTableCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        addAppAddPhoneTableCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        addAppDeleteNameTableCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        addAppDeleteCityTableCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        addAppDeleteCountryTableCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        addAppDeletePhoneTableCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        //updates the tables
        AddModAppAddTableViewUpdate();
        AddModAppDeleteTableViewUpdate();
    }

}
