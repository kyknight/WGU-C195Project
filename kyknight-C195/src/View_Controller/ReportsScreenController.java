/**
 * This controller file sets actions to the buttons on the fxml (GUI) file and to 
 * redirect to the user to the Main Screen (MainScreen.fxml), when selecting to exit 
 * the Report screen (ReportsScreen.fxml).
 */
package View_Controller;

import Model.DBManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author kyleighknight
 */
public class ReportsScreenController implements Initializable {
    
    //Title Labels
    @FXML private Label reportScreenLabel, reportApp101Label, reportApp102Label, reportApp103Label;
    //Description Labels
    @FXML private Label report101DescLabel, report102DescLabel, report103DescLabel;
    
    //Buttons
    @FXML private Button reportRun101Button, reportRun102Button, reportRun103Button, reportExitButton;
    
    /**
     * This method, when cancel button is selected and 'OK' is selected, will redirect 
     * the user to the Main Screen (MainScreen.fxml).
     * @param event 
     */
    private void ReportsExitButtonPushed (ActionEvent event){
        //called the method from AddAppointmentController.java
        AddAppointmentController exitPushed = new AddAppointmentController();
        exitPushed.AddModAppCancelButtonPushed(event);
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //action assignment
        reportRun101Button.setOnAction(event -> DBManager.run101());
        reportRun102Button.setOnAction(event -> DBManager.run102());
        reportRun103Button.setOnAction(event -> DBManager.run103());
        reportExitButton.setOnAction(event -> ReportsExitButtonPushed(event));
    }    
    
}
