/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author kyleighknight
 */
public class Customer {
    
    private IntegerProperty custId, active, addressId, cityId, countryId;
    private StringProperty custName, address1, address2, zipCode, phone, city, country;
    
    /**
     * This is a constructor
     */
    public Customer() {
        custId = new SimpleIntegerProperty();
        custName = new SimpleStringProperty();
        active = new SimpleIntegerProperty();
        addressId = new SimpleIntegerProperty();
        address1 = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        zipCode = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        cityId = new SimpleIntegerProperty();
        city = new SimpleStringProperty();
        countryId = new SimpleIntegerProperty();
        country = new SimpleStringProperty();
    }
    
    //properties
    public IntegerProperty custIdProperty() {
        return custId;
    }
    public StringProperty custNameProperty() {
        return custName;
    }
    public IntegerProperty activeProperty() {
        return active;
    }
    public IntegerProperty addressIdProperty() {
        return addressId;
    }
    public StringProperty address1Property() {
        return address1;
    }
    public StringProperty address2Property() {
        return address2;
    }
    public StringProperty zipCodeProperty() {
        return zipCode;
    }
    public StringProperty phoneProperty() {
        return phone;
    }
    public IntegerProperty cityIdProperty() {
        return cityId;
    }
    public StringProperty cityProperty() {
        return city;
    }
    public IntegerProperty countryIdProperty() {
        return countryId;
    }
    public StringProperty countryProperty() {
        return country;
    }
    
    //setters and getters
    public void setCustId(int custId){
        this.custId.set(custId);
    }
    public int getCustId(){
        return this.custId.get();
    }
    
    public void setCustName(String custName){
        this.custName.set(custName);
    }
    public String getCustName(){
        return this.custName.get();
    }
    
    public void setActive(int active){
        this.active.set(active);
    }
    public int getActive(){
        return this.active.get();
    }
    
    public void setAddressId(int addressId){
        this.addressId.set(addressId);
    }
    public int getAddressId(){
        return this.addressId.get();
    }
    
    public void setAddress1(String address1){
        this.address1.set(address1);
    }
    public String getAddress1(){
        return this.address1.get();
    }
    
    public void setAddress2(String address2){
        this.address2.set(address2);
    }
    public String getAddress2(){
        return this.address2.get();
    }
    
    public void setZipCode(String zipCode){
        this.zipCode.set(zipCode);
    }
    public String getZipcode(){
        return this.zipCode.get();
    }
    
    public void setPhone(String phone){
        this.phone.set(phone);
    }
    public String getPhone(){
        return this.phone.get();
    }
    
    public void setCityId(int cityId){
        this.cityId.set(cityId);
    }
    public int getCityId(){
        return this.cityId.get();
    }
    
    public void setCity(String city){
        this.city.set(city);
    }
    public String getCity(){
        return this.city.get();
    }
    
    public void setCountryId(int countryId){
        this.countryId.set(countryId);
    }
    public int getCountryId(){
        return this.countryId.get();
    }
    
    public void setCountry(String country){
        this.country.set(country);
    }
    public String getCountry(){
        return this.country.get();
    }
    
    public static String isCustValid(String custName, String address, String city,
                                         String country, String zipCode, String phone) {
        String errorMessage = "";
        if (custName.length() == 0) {
            errorMessage = errorMessage + "The customer name field is required. \n";
        }
        if (address.length() == 0) {
            errorMessage = errorMessage + "At least one address line is required. \n";
        }
        if (city.length() == 0) {
            errorMessage = errorMessage + "The city field is required. \n";
        }
        if (country.length() == 0) {
            errorMessage = errorMessage + "The country field is required. \n";
        }
        if (zipCode.length() == 0) {
            errorMessage = errorMessage + "The postal code field is required. \n";
        }
        if (phone.length() == 0) {
            errorMessage = errorMessage + "The phone field is required. \n";
        }
        return errorMessage;
    }
}
