/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
