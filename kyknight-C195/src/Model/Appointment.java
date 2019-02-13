/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author kyleighknight
 */
public class Appointment {
    
    private IntegerProperty appId, custId;
    private StringProperty title, desc, location, type, contact, url, dateString, startString, endString, createdBy;
    private Timestamp startTimestamp, endTimestamp;
    private Date startDate, endDate;
    
    /**
     * This is a constructor
     * @param appId
     * @param custId
     * @param title
     * @param desc
     * @param location
     * @param contact
     * @param url
     * @param startTimestamp
     * @param endTimestamp
     * @param startDate
     * @param endDate
     * @param createdBy 
     */
    public Appointment(int appId, int custId, String title, String desc, String location, String contact,
                       String url, Timestamp startTimestamp, Timestamp endTimestamp, Date startDate, Date endDate, String createdBy){
        this.appId = new SimpleIntegerProperty(appId);
        this.custId = new SimpleIntegerProperty(custId);
        this.title = new SimpleStringProperty(title);
        this.desc = new SimpleStringProperty(desc);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.url = new SimpleStringProperty(url);
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.startDate = startDate;
        this.endDate = endDate;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        this.dateString = new SimpleStringProperty(format.format(startDate));
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a z");
        this.startString = new SimpleStringProperty(formatTime.format(startDate));
        this.endString = new SimpleStringProperty(formatTime.format(endDate));
        this.createdBy = new SimpleStringProperty(createdBy);
    }
    
    //Properties
    public IntegerProperty appoIdIntegerProperty() {
        return this.appId;
    }
    public IntegerProperty custIdIntegerProperty() {
        return this.custId;
    }
    public StringProperty titleStringProperty() {
        return this.title;
    }
    public StringProperty descStringProperty() {
        return this.desc;
    }
    public StringProperty locationStringProperty() {
        return this.location;
    }
    public StringProperty typeStringProperty() {
        return this.type;
    }
    public StringProperty contactStringProperty() {
        return this.contact;
    }
    public StringProperty urlStringProperty() {
        return this.url;
    }
    public StringProperty dateStringProperty() {
        return this.dateString;
    }
    public StringProperty startStringProperty() {
        return this.startString;
    }
    public StringProperty endStringProperty() {
        return this.endString;
    }
    public StringProperty createdByProperty() {
        return this.createdBy;
    }
    
    //Getters and Setters
    public void setAppId(int appId){
        this.appId.set(appId);
    }
    public int getAppId(){
        return this.appId.get();
    }
    
    public void setCustId(int custId){
        this.custId.set(custId);
    }
    public int getCustId(){
        return this.custId.get();
    }
    
    public void setTitle(String title){
        this.title.set(title);
    }
    public String getTitle(){
        return this.title.get();
    }
    
    public void setDesc(String desc){
        this.desc.set(desc);
    }
    public String getDesc(){
        return this.desc.get();
    }
    
    public void setLocation(String location){
        this.location.set(location);
    }
    public String getLocation(){
        return this.location.get();
    }
    
    public void setType(String type){
        this.type.set(type);
    }
    public String getType(){
        return this.type.get();
    }
    
    public void setContact(String contact){
        this.contact.set(contact);
    }
    public String getContact(){
        return this.contact.get();
    }
    
    public void setUrl(String url){
        this.url.set(url);
    }
    public String getUrl(){
        return this.url.get();
    }
    
    public Date getStartDate(){
        return this.startDate;
    }
    
    public Date getEndDate(){
        return this.endDate;
    }
    
    public String getDateString(){
        return this.dateString.get();
    }
    
    public String getStartString(){
        return this.startString.get();
    }
    
    public String getEndString(){
        return this.endString.get();
    }
    
    public void setStartTimestamp(Timestamp startTimestamp){
        this.startTimestamp = startTimestamp;
    }
    public Timestamp getStartTimestamp(){
        return this.startTimestamp;
    }
    
    public void setEndTimestamp(Timestamp endTimestamp){
        this.endTimestamp = endTimestamp;
    }
    public Timestamp getEndTimestamp(){
        return this.endTimestamp;
    }
    
    public void setCreatedBy(String createdBy){
        this.createdBy.set(createdBy);
    }
    public String getCreatedBy(){
        return this.createdBy.get();
    }
    
    public static String isAppValid(Customer customer, String title, String desc, String location, LocalDate appDate,
            String startHr, String startMin, String startAmPm, String endHr, String endMin, String endAmPm) throws NumberFormatException{
        String errorMessage = "";
        
        try{
            if (customer == null) {
                errorMessage = errorMessage + "A customer is required to be associated with each appointment. \n";
            }
            if (title.length() == 0) {
                errorMessage = errorMessage + "The tile field is required. \n";
            }
            if (desc.length() == 0) {
                errorMessage = errorMessage + "A description of the appointment is required. \n";
            }
            if (location.length() == 0) {
                errorMessage = errorMessage + "A location for the appointment is required. \n";
            }
            if (appDate == null || startHr.equals("") || startMin.equals("") || startAmPm.equals("") ||
                    endHr.equals("") || endMin.equals("") || endAmPm.equals("")) {
                errorMessage = errorMessage + "A complete start and end time are required. \n";
            }
            if (Integer.parseInt(startHr) < 1 || Integer.parseInt(startHr) > 12 || Integer.parseInt(endHr) < 1 || Integer.parseInt(endHr) > 12 ||
                    Integer.parseInt(startMin) < 0 || Integer.parseInt(startMin) > 59 || Integer.parseInt(endMin) < 0 || Integer.parseInt(endMin) > 59) {
                errorMessage = errorMessage + "The start and end times must be valid times. \n";
            }
            if ((startAmPm.equals("PM") && endAmPm.equals("AM")) || (startAmPm.equals(endAmPm) && Integer.parseInt(startHr) != 12 && Integer.parseInt(startHr) > Integer.parseInt(endHr)) ||
                    (startAmPm.equals(endAmPm) && startHr.equals(endHr) && Integer.parseInt(startMin) > Integer.parseInt(endMin))) {
                errorMessage = errorMessage + "Start time cannot be after end time. \n";
            }
            if ((Integer.parseInt(startHr) < 9 && startAmPm.equals("AM")) || (Integer.parseInt(endHr) < 9 && endAmPm.equals("AM")) ||
                    (Integer.parseInt(startHr) >= 5 && Integer.parseInt(startHr) < 12 && startAmPm.equals("PM")) || (Integer.parseInt(endHr) >= 5 && Integer.parseInt(endHr) < 12 && endAmPm.equals("PM")) ||
                    (Integer.parseInt(startHr) == 12 && startAmPm.equals("AM")) || (Integer.parseInt(endHr)) == 12 && endAmPm.equals("AM")) {
                errorMessage = errorMessage + "errorStartEndOutsideHours";
            }
            if (appDate.getDayOfWeek().toString().toUpperCase().equals("SATURDAY") || appDate.getDayOfWeek().toString().toUpperCase().equals("SUNDAY")) {
                errorMessage = errorMessage + "The start and end times cannot be outside of business hours (9 AM - 5 PM). \n";
            }
        } catch(NumberFormatException e){
            errorMessage = errorMessage + "Start and end time values must be integers. \n";
        } finally {
            return errorMessage;
        }
    }
}
