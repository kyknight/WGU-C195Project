/**
 * This java class is used to grab the customer list to be called by other methods.
 */
package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author kyleighknight
 */
public class CustomerList {
    
    private static ObservableList<Customer> custList = FXCollections.observableArrayList();
    
    //appList getter
    public static ObservableList<Customer> getCustList(){
        return custList;
    }
}
