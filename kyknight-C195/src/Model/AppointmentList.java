/**
 * This java class is used to grab the appointment list to be called by other methods.
 */
package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author kyleighknight
 */
public class AppointmentList {
    
    private static ObservableList<Appointment> appList = FXCollections.observableArrayList();
    
    //appList getter
    public static ObservableList<Appointment> getAppList(){
        return appList;
    }
}
