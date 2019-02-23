/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Calendar_View.MonthlyCalendarView;
import Calendar_View.WeeklyCalendarView;
import Model.Customer;
import static Model.CustomerList.getCustList;
import static Model.DBManager.loginAppNotify;
import static Model.DBManager.setCustToInactive;
import static Model.DBManager.updateAppList;
import static Model.DBManager.updateCustList;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class MainScreenController implements Initializable {

    //Buttons
    @FXML private Button mainScreenAddAppButton, mainScreenAppSumButton, mainScreenReportsButton, mainScreenViewToggleButton,
            mainScreenCurrentDateButton, mainScreenAddCustButton, mainScreenModCustButton, mainScreenRemoveCustButton, mainScreenExitButton;
    
    //Grid Pane
    @FXML GridPane mainScreenGrid;
    
    //TableView
    @FXML private TableView<Customer> custTableView;
    @FXML private TableColumn<Customer,String> custNameTableCol;
    @FXML private TableColumn<Customer,String> custAddress1TableCol;
    @FXML private TableColumn<Customer,String> custAddress2TableCol;
    @FXML private TableColumn<Customer,String> custCityTableCol;
    @FXML private TableColumn<Customer,String> custCountryTableCol;
    @FXML private TableColumn<Customer,String> custPhoneTableCol;
    
    //modified customer holder
    private static int custIndexMod;
    //whether or not the calendar is in monthly or weekly view
    private boolean viewMonthly = true;
    //initialize calendar view
    private MonthlyCalendarView monthlyCalendar;
    private WeeklyCalendarView weeklyCalendar;
    private VBox monthlyCalendarView, weeklyCalendarView;
    
    /**
     * This method opens the Add Appointment screen when the Add Appointment button is 
     * pushed
     * @param event 
     */
    private void MainScreenAddAppButtonPushed(ActionEvent event){
        try{
            Parent addAppParent = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
            Scene addAppScene = new Scene(addAppParent);
            Stage addAppStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addAppStage.setScene(addAppScene);
            addAppStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method opens the Appointment Summary screen when the Appointment Summary button
     * is pushed
     * @param event 
     */
    private void MainScreenAppSummaryButtonPushed(ActionEvent event){
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
    }
    
    /**
     * This method opens the Reports screen when the Reports button is pushed
     * @param event 
     */
    private void MainScreenReportsButtonPushed(ActionEvent event){
        try {
            Parent reportsParent = FXMLLoader.load(getClass().getResource("ReportsScreen.fxml"));
            Scene reportsScene = new Scene(reportsParent);
            Stage reportsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            reportsStage.setScene(reportsScene);
            reportsStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method adjusts the calendar to the current date when the View Current Date
     * button is pushed and checks whether the calendar is in monthly or weekly view and
     * adjusts accordingly
     * @param event 
     */
    private void MainScreenCurrentDateButtonPushed(ActionEvent event){
        //calendar in monthly view?
        //yes
        if(viewMonthly == true){
            mainScreenGrid.getChildren().remove(monthlyCalendarView);
            //grabbing current calendar view
            YearMonth currentYearMonth = YearMonth.now();
            //grabs and sets calendar view using current year-month
            monthlyCalendar = new MonthlyCalendarView(currentYearMonth);
            monthlyCalendarView = monthlyCalendar.getView();
            mainScreenGrid.add(monthlyCalendarView,0,0);
        } /*no */ else {
            mainScreenGrid.getChildren().remove(weeklyCalendarView);
            //grabs current date
            LocalDate currentLocalDate = LocalDate.now();
            weeklyCalendar = new WeeklyCalendarView(currentLocalDate);
            weeklyCalendarView = weeklyCalendar.getView();
            mainScreenGrid.add(weeklyCalendarView,0,0);
        }
    }
    
    /**
     * This method toggles between monthly and weekly calendar views when the [weekly/monthly]
     * View button is selected
     * @param event 
     */
    private void MainScreenViewToggleCalendarButtonPushed(ActionEvent event){
        //calendar in monthly view?
        //yes
        if(viewMonthly == true){
            mainScreenGrid.getChildren().remove(monthlyCalendarView);
            //grabbing current year-month calendar view
            YearMonth currentYearMonth = monthlyCalendar.getCurrentMonth();
            //sets the year-month to first day of smae month
            LocalDate currentLocalDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), 1);
            //grabs adn sets calendar view with first day of the month
            weeklyCalendar = new WeeklyCalendarView(currentLocalDate);
            weeklyCalendarView = weeklyCalendar.getView();
            mainScreenGrid.add(weeklyCalendarView,0,0);
            
            //changes the button to say 'View Monthly View'
            mainScreenViewToggleButton.setText("Monthly View");
            viewMonthly = false;
        } /* no */ else {
            mainScreenGrid.getChildren().remove(weeklyCalendarView);
            LocalDate currentLocalDate = weeklyCalendar.getCurrentLocalDate();
            YearMonth currentYearMonth = YearMonth.from(currentLocalDate);
            monthlyCalendar = new MonthlyCalendarView(currentYearMonth);
            monthlyCalendarView = monthlyCalendar.getView();
            mainScreenGrid.add(monthlyCalendarView,0,0);
            mainScreenViewToggleButton.setText("Weekly View");
            viewMonthly = true;
        }
    } 
    
    /**
     * This method opens the Add Customer screen when the Add Customer button is pushed
     * @param event 
     */
    private void MainScreenAddCustButtonPushed(ActionEvent event){
        try {
            Parent addCustParent = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
            Scene addCustScene = new Scene(addCustParent);
            Stage addCustStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addCustStage.setScene(addCustScene);
            addCustStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method opens the Modify Customer screen when the modify customer button
     * is pushed and checks if there is a customer selected. If not, error message displays
     * @param event 
     */
    private void MainScreenModCustButtonPushed(ActionEvent event){
        //grabs the customer info from table
        Customer custToMod = custTableView.getSelectionModel().getSelectedItem();
        //is a customer selected?
        //yes
        if(custToMod == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("Please select a customer from the table to modify.");
            alert.showAndWait();
        } 
        
        //sets index of customer to be modified
        custIndexMod = getCustList().indexOf(custToMod);
        
        //open modify customer screen
         try {
            Parent modCustParent = FXMLLoader.load(getClass().getResource("ModifyCustomer.fxml"));
            Scene modCustScene = new Scene(modCustParent);
            Stage modCustStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modCustStage.setScene(modCustScene);
            modCustStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method marks the customer as inactive (removes them from table view) when the Remove
     * Customer button is pushed and checks if there is a customer selected. If not, error message 
     * displays
     * @param event 
     */
    private void MainScreenRemoveCustButtonPushed(ActionEvent event){
        //grabs the customer info from table
        Customer custToRemove = custTableView.getSelectionModel().getSelectedItem();
        //is a customer selected?
        //yes
        if(custToRemove == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("Please select a customer from the table to modify.");
            alert.showAndWait();
        } 
        
        //submits customer info to be removed
        setCustToInactive(custToRemove);
    }
    
    /**
     * This method exits the program when the exit button is pushed and asks for confirmation 
     * on exit of program
     * @param event 
     */
    private void MainScreenExitButtonPushed(ActionEvent event){
        ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        //if 'OK' selected, exit program
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
    /**
     * This is to return the customer index to be modified
     * @return 
     */
    public static int getCustIndexToMod(){
        return custIndexMod;
    }
    
    /**
     * This method updates the customer table view on load
     */
    public void MainScreenUpdateCustomerTableView(){
        updateCustList();
        custTableView.setItems(getCustList());
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainScreenAddAppButton.setOnAction(event -> MainScreenAddAppButtonPushed(event));
        mainScreenAppSumButton.setOnAction(event -> MainScreenAppSummaryButtonPushed(event));
        mainScreenReportsButton.setOnAction(event -> MainScreenReportsButtonPushed(event));
        mainScreenCurrentDateButton.setOnAction(event -> MainScreenCurrentDateButtonPushed(event));
        mainScreenViewToggleButton.setOnAction(event -> MainScreenViewToggleCalendarButtonPushed(event));
        mainScreenAddCustButton.setOnAction(event -> MainScreenAddCustButtonPushed(event));
        mainScreenModCustButton.setOnAction(event -> MainScreenModCustButtonPushed(event));
        mainScreenRemoveCustButton.setOnAction(event -> MainScreenRemoveCustButtonPushed(event));
        mainScreenExitButton.setOnAction(event -> MainScreenExitButtonPushed(event));
        // Update appointment list
        updateAppList();
        // Create calendarView and add to gridPane
        monthlyCalendar = new MonthlyCalendarView(YearMonth.now());
        monthlyCalendarView = monthlyCalendar.getView();
        mainScreenGrid.add(monthlyCalendarView, 0, 0);
        // Assign data to table view
        custNameTableCol.setCellValueFactory(cellData -> cellData.getValue().custNameProperty());
        custAddress1TableCol.setCellValueFactory(cellData -> cellData.getValue().address1Property());
        custAddress2TableCol.setCellValueFactory(cellData -> cellData.getValue().address2Property());
        custCityTableCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        custCountryTableCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        custPhoneTableCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Update table view
        MainScreenUpdateCustomerTableView();
        // Create appointment notifications if any need to be shown
        loginAppNotify();
    }    
    
}
