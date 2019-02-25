/**
 * This java class is used in aide to get the listed fields and set the desired fields to be called
 * by other methods for appointment information.
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
    
    private IntegerProperty appId, custId, userId;
    private StringProperty title, desc, location, type, contact, url, dateString, startString, endString, createdBy, createDateString;
    private Timestamp startTimestamp, endTimestamp;
    private Date startDate, endDate, createDate;
    
    /**
     * This is a constructor
     * @param appId
     * @param custId
     * @param userId
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
     * @param type 
     * @param createDate 
     */
    public Appointment(int appId, int custId, int userId, String title, String desc, String location, String contact,
                       String url, Timestamp startTimestamp, Timestamp endTimestamp, Date startDate, Date endDate, 
                       String createdBy, String type, Date createDate){
        this.appId = new SimpleIntegerProperty(appId);
        this.custId = new SimpleIntegerProperty(custId);
        this.userId = new SimpleIntegerProperty(userId);
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
        this.type = new SimpleStringProperty(type);
        this.createDateString = new SimpleStringProperty(format.format(createDate)); 
    }
    
    /**
     * *************************
     * Properties
     * *************************
     * @return 
     */
    public IntegerProperty appoIdIntegerProperty() {
        return this.appId;
    }
    public IntegerProperty custIdIntegerProperty() {
        return this.custId;
    }
    public IntegerProperty userIdIntegerProperty() {
        return this.userId;
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
    public StringProperty typeProperty() {
        return this.type;
    }
    public StringProperty createDateProperty(){
        return this.createDateString;
    }
    
    /**
     * *********************
     * Getter methods
     * *********************
     * @return 
     */
    public int getAppId(){
        return this.appId.get();
    }
    public int getCustId(){
        return this.custId.get();
    }
    public int getUserId(){
        return this.userId.get();
    }
    public String getTitle(){
        return this.title.get();
    }
    public String getDesc(){
        return this.desc.get();
    }
    public String getLocation(){
        return this.location.get();
    }
    public String getContact(){
        return this.contact.get();
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
    public Timestamp getStartTimestamp(){
        return this.startTimestamp;
    }
    public Timestamp getEndTimestamp(){
        return this.endTimestamp;
    }
    public String getCreatedBy(){
        return this.createdBy.get();
    }
    public String getType(){
        return this.type.get();
    }
    public String getCreateDateString(){
        return this.createDateString.get();
    }
    
    
    /**
     * ***********************
     * Setter methods
     * ***********************
     *  
     */
    public void setTitle(String title){
        this.title.set(title);
    }
    public void setDesc(String desc){
        this.desc.set(desc);
    }
    public void setLocation(String location){
        this.location.set(location);
    }
    public void setContact(String contact){
        this.contact.set(contact);
    }
    public void setUrl(String url){
        this.url.set(url);
    }
    public void setStartTimestamp(Timestamp startTimestamp){
        this.startTimestamp = startTimestamp;
    }
    public void setEndTimestamp(Timestamp endTimestamp){
        this.endTimestamp = endTimestamp;
    }
    public void setCreatedBy(String createdBy){
        this.createdBy.set(createdBy);
    }
    public void setType(String type){
        this.type.set(type);
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    /**
     * This method checks that appointment fields are entered then alerts the user
     * to which field needs to be entered before the appointment can be added.
     * 
     * @param customer
     * @param title
     * @param desc
     * @param location
     * @param appDate
     * @param startHr
     * @param startMin
     * @param startAmPm
     * @param endHr
     * @param endMin
     * @param endAmPm
     * @param type
     * @return
     * @throws NumberFormatException 
     */
    public static String isAppValid(Customer customer, String title, String desc, String location, LocalDate appDate,
            String startHr, String startMin, String startAmPm, String endHr, String endMin, String endAmPm, String type) throws NumberFormatException{
        String errorMessage = "";
        
        try{
            if (customer == null) {
                errorMessage = errorMessage + "A customer is required to be associated with each appointment.";
            }
            if (type.length() == 0){
                errorMessage = errorMessage + "The type field is required.";
            }
            if (title.length() == 0) {
                errorMessage = errorMessage + "The tile field is required.";
            }
            if (desc.length() == 0) {
                errorMessage = errorMessage + "A description of the appointment is required.";
            }
            if (location.length() == 0) {
                errorMessage = errorMessage + "A location for the appointment is required.";
            }
            if (appDate == null || startHr.equals("") || startMin.equals("") || startAmPm.equals("") ||
                    endHr.equals("") || endMin.equals("") || endAmPm.equals("")) {
                errorMessage = errorMessage + "A complete start and end time are required.";
            }
            if (Integer.parseInt(startHr) < 1 || Integer.parseInt(startHr) > 12 || Integer.parseInt(endHr) < 1 || Integer.parseInt(endHr) > 12 ||
                    Integer.parseInt(startMin) < 0 || Integer.parseInt(startMin) > 59 || Integer.parseInt(endMin) < 0 || Integer.parseInt(endMin) > 59) {
                errorMessage = errorMessage + "The start and end times must be valid times.";
            }
            if ((startAmPm.equals("PM") && endAmPm.equals("AM")) || (startAmPm.equals(endAmPm) && Integer.parseInt(startHr) != 12 && Integer.parseInt(startHr) > Integer.parseInt(endHr)) ||
                    (startAmPm.equals(endAmPm) && startHr.equals(endHr) && Integer.parseInt(startMin) > Integer.parseInt(endMin))) {
                errorMessage = errorMessage + "Start time cannot be after end time.";
            }
            if ((Integer.parseInt(startHr) < 9 && startAmPm.equals("AM")) || (Integer.parseInt(endHr) < 9 && endAmPm.equals("AM")) ||
                    (Integer.parseInt(startHr) >= 5 && Integer.parseInt(startHr) < 12 && startAmPm.equals("PM")) || (Integer.parseInt(endHr) >= 5 && Integer.parseInt(endHr) < 12 && endAmPm.equals("PM")) ||
                    (Integer.parseInt(startHr) == 12 && startAmPm.equals("AM")) || (Integer.parseInt(endHr)) == 12 && endAmPm.equals("AM")) {
                errorMessage = errorMessage + "The start and end times cannot be outside of business hours (9 AM - 5 PM) Monday - Friday.";
            }
            if (appDate.getDayOfWeek().toString().toUpperCase().equals("SATURDAY") || appDate.getDayOfWeek().toString().toUpperCase().equals("SUNDAY")) {
                errorMessage = errorMessage + "The start and end times cannot be outside of business hours (9 AM - 5 PM) Monday - Friday.";
            }
        } catch(NumberFormatException e){
            errorMessage = errorMessage + "Start and end time values must be integers.";
        } finally {
            return errorMessage;
        }
    }
}
