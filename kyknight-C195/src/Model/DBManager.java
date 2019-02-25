/**
 * The purpose of this class file is to communicate with the database of changes 
 * to the appointment and customer tables, to validate user logins, and to generate
 * reports number of appointment types by month(101), each consultant's schedule(102), 
 * each customer's upcoming meetings(103), and a user login text file.
 */
package Model;

import Model.Appointment;
import View_Controller.AppointmentSummaryController;
import View_Controller.LoginScreenController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

/**
 *
 * @author kyleighknight
 */
public class DBManager {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String db = "U04qVf";
    private static final String dbUrl = "jdbc:mysql://52.206.157.109/" + db;
    private static final String user = "U04qVf";
    private static final String pass = "53688319253";

    private static String currUser;
    private static int openCount = 0;

    private static Boolean check;

    /**
     * This method, when called, checks if the user's credentials are valid in the database.
     *
     * @param userName
     * @param password
     * @return
     */
    public static boolean checkLoginCred(String userName, String password) {
        int userId = getUserId(userName);
        boolean correctPassword = checkPassword(userId, password);

        if (correctPassword == true) {
            setCurrUser(userName);
            try {
                Path path = Paths.get("userLog.txt");
                Files.write(path, Arrays.asList("User " + currUser + " logged in at " + Date.from(Instant.now()).toString() + "."),
                        StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method grabs the userId from the userName in the database. If there is no match
     * found for userName, returns -1 and displays error message.
     *
     * @param userName
     * @return
     */
    private static int getUserId(String userName) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int userId = -1;
        try {
            Connection conn = DriverManager.getConnection(dbUrl, user, pass);
            Statement stmt = conn.createStatement();
            //gets the userId for entered username
            ResultSet userIdSet = stmt.executeQuery("SELECT userId FROM user WHERE userName = '" + userName + "'");
            //sets unique value to userId and retrieves it from ResultSet
            if (userIdSet.next()) {
                userId = userIdSet.getInt("userId");
            }
            userIdSet.close();
        } catch (SQLException e) {
            LoginScreenController.IncrementDatabaseError();
            e.printStackTrace();
        }
        return userId;
    }

    /**
     * This method, when called, checks the password and userId entered to see if they match
     * the database record.
     *
     * @param userId
     * @param password
     * @return
     */
    private static boolean checkPassword(int userId, String password) {
        try {
            Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
            Statement stmt = conn.createStatement();
            
            //grabs the password by userId
            ResultSet passwordSet = stmt.executeQuery("SELECT password FROM user WHERE userId = '" + userId + "'");
            
            String dbPassword = null;
            
            // grabs the String from ResultSet
            if (passwordSet.next()) {
                dbPassword = passwordSet.getString("password");
            } else {
                return false;
            }
            passwordSet.close();
            //Checks the user-entered password with password and returns boolean
            if (dbPassword.equals(password)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method, when called, allows public access for the currUser String.
     *
     * @param userName
     */
    private static void setCurrUser(String userName) {
        currUser = userName;
    }

    /**
     * This method, when called, check on whether or not an appointment that the user created
     * is scheduled within the next 15 minutes. If there is, a notification of
     * the scheduled appointment is created.
     */
    public static void loginAppNotify() {
        if (openCount == 0) {
            //creates observableList of appointments which were created by the logged in user
            ObservableList<Appointment> userApp = FXCollections.observableArrayList();
            AppointmentList.getAppList().stream().filter((appointment) -> (appointment.getCreatedBy().equals(currUser))).forEachOrdered((appointment) -> {
                userApp.add(appointment);
            });
            //checks for appointment within 15 minutes of login
            userApp.forEach((appointment) -> {
                //creates an object for 15 minutes from current date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Date.from(Instant.now()));
                calendar.add(Calendar.MINUTE, 15);
                Date notifyCutoff = calendar.getTime();
                //displays an alert with info about appointment, if appointment start date is 15 minutes before current time
                if (appointment.getStartDate().before(notifyCutoff)) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Upcoming Appointments");
                    alert.setHeaderText("Upcoming Appointments");
                    alert.setContentText("More information for upcoming appointment: \n" + "Title: "
                            + appointment.getTitle() + "\n" + "Description: " + appointment.getDesc()
                            + "\n" + "Location" + ": " + appointment.getLocation() + "\n" + "Contact: "
                            + appointment.getContact() + "\n" + "URL: " + appointment.getUrl() + "\n"
                            + "Date: " + appointment.getDateString() + "\n" + "Start Time: "
                            + appointment.getStartString() + "\n" + "End Time: " + appointment.getEndString());
                    alert.showAndWait();
                }
            });
            openCount++;
        }
    }

    /**
     * This method, when called, updates the custList after the database has been changed.
     */
    public static void updateCustList() {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ObservableList<Customer> custList = CustomerList.getCustList();
            custList.clear();
            //all active customers list created
            ResultSet custIdResultSet = stmt.executeQuery("SELECT customerId FROM customer WHERE active = 1");
            ArrayList<Integer> custIdList = new ArrayList<>();
            while (custIdResultSet.next()) {
                custIdList.add(custIdResultSet.getInt(1));
            }
            //creates a customer object for each custId in list then ass Customer to custList
            for (int custId : custIdList) {
                Customer customer = new Customer();
                //gets teh customer info from db then sets to Customer object
                ResultSet custResultSet = stmt.executeQuery("SELECT customerName, active, addressId FROM customer WHERE customerId = '" + custId + "'");
                custResultSet.next();
                String custName = custResultSet.getString(1);
                int active = custResultSet.getInt(2);
                int addressId = custResultSet.getInt(3);
                customer.setCustId(custId);
                customer.setCustName(custName);
                customer.setActive(active);
                customer.setAddressId(addressId);
                //gets the address info from db then sets to Customer object
                ResultSet addressResultSet = stmt.executeQuery("SELECT address, address2, postalCode, phone, cityId FROM address WHERE addressId = '" + addressId +"'");
                addressResultSet.next();
                String address = addressResultSet.getString(1);
                String address2 = addressResultSet.getString(2);
                String zipCode = addressResultSet.getString(3);
                String phone = addressResultSet.getString(4);
                int cityId = addressResultSet.getInt(5);
                customer.setAddress1(address);
                customer.setAddress2(address2);
                customer.setZipCode(zipCode);
                customer.setPhone(phone);
                customer.setCityId(cityId);
                //gets the city info from db then sets to Customer object
                ResultSet cityResultSet = stmt.executeQuery("SELECT city, countryId FROM city WHERE cityId = '" + cityId + "'");
                cityResultSet.next();
                String city = cityResultSet.getString(1);
                int countryId = cityResultSet.getInt(2);
                customer.setCity(city);
                customer.setCountryId(countryId);
                //gets the country info from db then sets to Customer object
                ResultSet countryResultSet = stmt.executeQuery("SELECT country FROM country WHERE countryId = '" + countryId + "'");
                countryResultSet.next();
                String country = countryResultSet.getString(1);
                customer.setCountry(country);
                //add new Customer object to custList
                custList.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Connecting to Database");
            alert.setContentText("There was an error connecting to the database and functionality will be severely limited.");
            alert.show();
        }
    }

    /**
     * This method, when called, checks whether or not a customer already exists in the database and
     * whether it is active or inactive. If customer exists in db, displays an
     * alert that the customer already exists.
     *
     * @param custName
     * @param address
     * @param address2
     * @param city
     * @param country
     * @param zipCode
     * @param phone
     */
    public static void addNewCust(String custName, String address, String address2, String city, String country,
            String zipCode, String phone) {
        //gets the Id's for country, city, and address
        try {
            int countryId = calcCountryId(country); 
            int cityId = calcCityId(city, countryId);
            int addressId = calcAddressId(address, address2, zipCode, phone, cityId); 
            //checks whether or not a customer is new or already exists in db
            if (checkIfCustExists(custName, addressId)) {
                //db connection
                try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                        Statement stmt = conn.createStatement()) {
                    ResultSet activeResultSet = stmt.executeQuery("SELECT active FROM customer WHERE customerName = '" 
                            + custName + "' AND addressId = '" + addressId + "'");
                    activeResultSet.next();
                    int active = activeResultSet.getInt(1);
                    //checks whether or not an existing customer is active or inactive
                    if (active == 1) {
                        //if active, displays alert
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error Adding Customer");
                        alert.setContentText("This customer already exists in the database.");
                        alert.showAndWait();
                    } else if (active == 0) {
                        //sets customer to active if currently set to inactive
                        setCustToActive(custName, addressId);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                addCustomer(custName, addressId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //if no db connection, alert displays that there needs to be a bd connection
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Customer");
            alert.setContentText("This function requires a connection to the database.");
            alert.showAndWait();
        }
    }

    /**
     * This method, when called, checks whether an entered countryId exits in the database. If so, returns
     * countryId. If not, creates new countryId entry and returns the new
     * countryId.
     *
     * @param country
     * @return
     */
    public static int calcCountryId(String country) {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet countryIdCheck = stmt.executeQuery("SELECT countryId FROM country WHERE country = '" + country + "'");
            //checks if already exits then returns countryId if it does
            if (countryIdCheck.next()) {
                int countryId = countryIdCheck.getInt(1);
                countryIdCheck.close();
                return countryId;
            } else {
                countryIdCheck.close();
                int countryId;
                ResultSet allCountryId = stmt.executeQuery("SELECT countryId FROM country ORDER BY countryId");
                //checks the end of the countryId list and adds the new countryId
                if (allCountryId.last()) {
                    countryId = allCountryId.getInt(1) + 1;
                    allCountryId.close();
                } else {
                    allCountryId.close();
                    countryId = 1;
                }
                //creates new entry with new countryId 
                stmt.executeUpdate("INSERT INTO country VALUES ('" + countryId + "', '" + country + "', CURRENT_DATE, "
                        + "'" + currUser + "', CURRENT_TIMESTAMP, '" + currUser + "')");
                return countryId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method, when called, checks whether an entered cityId exits in the database. If so, returns
     * cityId. If not, creates new cityId entry and returns the new cityId.
     *
     * @param city
     * @param countryId
     * @return
     */
    public static int calcCityId(String city, int countryId) {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet cityIdCheck = stmt.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "' AND countryid = '" + countryId + "'");
            //checks if already exits then returns countryId if it does
            if (cityIdCheck.next()) {
                int cityId = cityIdCheck.getInt(1);
                cityIdCheck.close();
                return cityId;
            } else {
                cityIdCheck.close();
                int cityId;
                ResultSet allCityId = stmt.executeQuery("SELECT cityId FROM city ORDER BY cityId");
                //checks the end of the countryId list and adds the new countryId
                if (allCityId.last()) {
                    cityId = allCityId.getInt(1) + 1;
                    allCityId.close();
                } else {
                    allCityId.close();
                    cityId = 1;
                }
                //creates new entry with new cityId value
                stmt.executeUpdate("INSERT INTO city VALUES ('" + cityId + "', '" + city + "', '" + countryId + "', CURRENT_DATE, "
                        + "'" + currUser + "', CURRENT_TIMESTAMP, '" + currUser + "')");
                return cityId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method, when called, checks whether an entered addressId exits in the database. 
     * If so, returns cityId. If not, creates new addressId entry and returns the new
     * addressId.
     *
     * @param address
     * @param address2
     * @param zipCode
     * @param phone
     * @param cityId
     * @return
     */
    public static int calcAddressId(String address, String address2, String zipCode, String phone, int cityId) {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet addressIdCheck = stmt.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "' AND address2 = '" 
                    + address2 + "' AND postalCode = '" + zipCode + "' AND phone = '" + phone + "' AND cityId = '" + cityId + "'");
            //checks if already exits then returns countryId if it does
            if (addressIdCheck.next()) {
                int addressId = addressIdCheck.getInt(1);
                addressIdCheck.close();
                return addressId;
            } else {
                addressIdCheck.close();
                int addressId;
                ResultSet allAddressId = stmt.executeQuery("SELECT addressId FROM address ORDER BY addressId");
                //checks the end of the addressId list and adds the new addressId
                if (allAddressId.last()) {
                    addressId = allAddressId.getInt(1) + 1;
                    allAddressId.close();
                } else {
                    allAddressId.close();
                    addressId = 1;
                }
                //creates new entry with new addressId value
                stmt.executeUpdate("INSERT INTO address VALUES ('" + addressId + "', '" + address + "', '" + address2 + "', '" + cityId + "', "
                        + "'" + zipCode + "', '" + phone + "', CURRENT_DATE, '" + currUser + "', CURRENT_TIMESTAMP, '" + currUser + "')");
                return addressId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method, when called, checks whether the customer already exists in the database. If so, returns
     * true. If not, returns false.
     *
     * @param custName
     * @param addressId
     * @return
     * @throws SQLException
     */
    private static boolean checkIfCustExists(String custName, int addressId) throws SQLException {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet custIdCheck = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + custName + "' "
                    + "AND addressId = '" + addressId + "'");
            //checks if already exits and returns boolean
            if (custIdCheck.next()) {
                custIdCheck.close();
                return true;
            } else {
                custIdCheck.close();
                return false;
            }
        } 
    }

    /**
     * This method, when called, changes the customer from "inactive" to "active" 
     * in the database, after the alert to the user is made and if "OK" is selected.
     *
     * @param custName
     * @param addressId
     */
    public static void setCustToActive(String custName, int addressId) {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("This customer already exists in the database, but is set as \\\\\"inactive\\\\\". Would you like to set this customer to \\\\\"active\\\\\" and make them visible in the customer list?");

            Optional<ButtonType> result = alert.showAndWait();
            //sets customer to active, if 'OK' is selected
            if (result.get() == ButtonType.OK) {
                stmt.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" 
                        + currUser + "' WHERE customerName = '" + custName + "' AND addressId = '" + addressId + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, creates a new customer in the database, if it doesnt already exist.
     *
     * @param custName
     * @param addressId
     * @throws SQLException
     */
    private static void addCustomer(String custName, int addressId) throws SQLException {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet allCustId = stmt.executeQuery("SELECT customerId FROM customer ORDER BY customerId");
            int custId;
            //checks the last custId and adds one to custId list for new custId
            if (allCustId.last()) {
                custId = allCustId.getInt(1) + 1;
                allCustId.close();
            } else {
                allCustId.close();
                custId = 1;
            }
            //creates new custId entry
            stmt.executeUpdate("INSERT INTO customer VALUES ('" + custId + "', '" + custName + "', '" + addressId + "', 1, CURRENT_DATE, '" 
                    + currUser + "', CURRENT_TIMESTAMP, '" + currUser + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, modifies existing customer entry in the database and checks if the customer
     * already exists.
     *
     * @param custId
     * @param custName
     * @param address
     * @param address2
     * @param city
     * @param country
     * @param zipCode
     * @param phone
     * @return
     */
    public static int modCustomer(int custId, String custName, String address, String address2,
            String city, String country, String zipCode, String phone) {
        try {
            int countryId = calcCountryId(country);
            int cityId = calcCityId(city, countryId);
            int addressId = calcAddressId(address, address2, zipCode, phone, cityId);
            //checks if customer already exists in the dd
            if (checkIfCustExists(custName, addressId)) {
                //if customer already exists, get custId and use custId to get and return their active status
                int existingCustId = getCustId(custName, addressId);
                int activeStatus = getActiveStatus(existingCustId);
                return activeStatus;
            } else {
                //if customer does not already exist, update customer entry in db and clean the db of unused entries
                updateCust(custId, custName, addressId);
                cleanDatabase();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Customer");
            alert.setContentText("This function requires a connection to the database.");
            alert.showAndWait();
            return -1;
        }
    }

    /**
     * This method, when called, grabs the custId using the custName and addressId as
     * identifiers.
     *
     * @param custName
     * @param addressId
     * @return
     * @throws SQLException
     */
    private static int getCustId(String custName, int addressId) throws SQLException {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet custIdResultSet = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + custName + "' AND addressId = '" + addressId + "'");
            custIdResultSet.next();
            int custId = custIdResultSet.getInt(1);
            return custId;
        } 
    }

    /**
     * This method, when called, grabs the active status of a customer entry.
     *
     * @param custId
     * @return
     * @throws SQLException
     */
    private static int getActiveStatus(int custId) throws SQLException {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet activeResultSet = stmt.executeQuery("SELECT active FROM customer WHERE customerId = '" + custId + "'");
            activeResultSet.next();
            int active = activeResultSet.getInt(1);
            return active;
        } 
    }

    /**
     * This method, when called, updates customer entry in the database, if unique.
     *
     * @param custId
     * @param custName
     * @param addressId
     * @throws SQLException
     */
    private static void updateCust(int custId, String custName, int addressId) throws SQLException {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE customer SET customerName = '" + custName + "', addressId = '" + addressId + "', "
                    + "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currUser + "' WHERE customerId = '" + custId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, sets the selected customer to 'inactive' hiding the customer
     * in the customer list.
     *
     * @param custToRemove
     */
    public static void setCustToInactive(Customer custToRemove) {
        int custId = custToRemove.getCustId();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Remove");
        alert.setHeaderText("Confirm Removal of Customer");
        alert.setContentText("Are you sure you want to remove this customer from the customer list by setting them as \"inactive\"?");
        Optional<ButtonType> result = alert.showAndWait();
        //sets the customer to inactive is 'OK' button is selected
        if (result.get() == ButtonType.OK) {
            try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass); Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = '" + custId + "'");
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert2 = new Alert(AlertType.INFORMATION);
                alert2.setTitle("Error");
                alert2.setHeaderText("Error Modifying Customer");
                alert2.setContentText("This function requires a connection to the database.");
                alert2.showAndWait();
            }
            updateCustList();
        }
    }

    /**
     * This method, when called, updates the appList with current appointments.
     */
    public static void updateAppList() {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass); Statement stmt = conn.createStatement()) {
            ObservableList<Appointment> appList = AppointmentList.getAppList();
            appList.clear();
            //creates appId list for all future appointments
            ResultSet appResultSet = stmt.executeQuery("SELECT appointmentId FROM appointment WHERE start >= CURRENT_TIMESTAMP");
            ArrayList<Integer> appIdList = new ArrayList<>();
            while (appResultSet.next()) {
                appIdList.add(appResultSet.getInt(1));
            }
            //creates Appointment object for each appId on list and adds to appList
            for (int appId : appIdList) {
                appResultSet = stmt.executeQuery("SELECT customerId, title, description, location, contact, url, start, end, createdBy, type, createDate, userId FROM appointment WHERE appointmentId = '" + appId + "'");
                appResultSet.next();
                int custId = appResultSet.getInt(1);
                String title = appResultSet.getString(2);
                String desc = appResultSet.getString(3);
                String location = appResultSet.getString(4);
                String contact = appResultSet.getString(5);
                String url = appResultSet.getString(6);
                Timestamp startTimestamp = appResultSet.getTimestamp(7);
                Timestamp endTimestamp = appResultSet.getTimestamp(8);
                String createdBy = appResultSet.getString(9);
                String type = appResultSet.getString(10);
                Timestamp createDate = appResultSet.getTimestamp(11);
                int userId = appResultSet.getInt(12);

                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date startDate = utcFormat.parse(startTimestamp.toString());
                java.util.Date endDate = utcFormat.parse(endTimestamp.toString());

                Appointment app = new Appointment(appId, custId, userId, title, desc, location, contact, url, startTimestamp, endTimestamp, startDate, endDate, createdBy, type, createDate);

                appList.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Appointment");
            alert.setContentText("This function requires a connection to the database.");
            alert.showAndWait();
        }
    }

    /**
     * This method, when called, verifies that the appointment created does not overlap
     * another appointment. If not, creates the appointment. If so, alerts the
     * user that there is an overlapping of appointments.
     *
     * @param customer
     * @param title
     * @param desc
     * @param location
     * @param contact
     * @param url
     * @param startUTC
     * @param endUTC
     * @param type
     * @param userId
     * @return
     */
    public static boolean addNewApp(Customer customer, String title, String desc, String location, String contact,
            String url, ZonedDateTime startUTC, ZonedDateTime endUTC, String type) {
        //ZonedDateTimes to Timestamp
        String startUTCString = startUTC.toString();
        startUTCString = startUTCString.substring(0, 10) + " " + startUTCString.substring(11, 16) + ":00";
        Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
        String endUTCString = endUTC.toString();
        endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
        Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
        //checks if the new appointment overlaps with existing appointments and displays alert if this occurs
        if (checkAppOverlap(startTimestamp, endTimestamp)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Appointment");
            alert.setContentText("The times entered overlap with an existing appointment.");
            alert.showAndWait();
            check = false;
            return check;
        } /* no overlap = new appoiment created */ else {
            int custId = customer.getCustId();
            //int userId = userId.getUserId();
            addApp(custId, title, desc, location, contact, url, startTimestamp, endTimestamp, type);
            check = true;
            return check;
        }
    }

    /**
     * This method, when called, checks whether a new appointment overlaps an existing
     * appointments. Returns true if overlapping and false if not.
     *
     * @param startTimestamp
     * @param endTimestamp
     * @return
     */
    private static boolean checkAppOverlap(Timestamp startTimestamp, Timestamp endTimestamp) {
        updateAppList();
        ObservableList<Appointment> appList = AppointmentList.getAppList();
        for (Appointment app : appList) {
            Timestamp existingStartTimestamp = app.getStartTimestamp();
            Timestamp existingEndTimestamp = app.getEndTimestamp();
            //checks different overlapping scenarios and returns true if true
            if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        //if all listed scenarios are not true then returns false
        return false;
    }

    /**
     * This method, when called, creates a new appointment in the database with 
     * new appId.
     *
     * @param custId
     * @param title
     * @param desc
     * @param location
     * @param contact
     * @param url
     * @param startTimestamp
     * @param endTimestamp
     */
    private static void addApp(int custId, String title, String desc, String location, String contact, String url,
            Timestamp startTimestamp, Timestamp endTimestamp, String type) {
        try (Connection conn = (Connection) DriverManager.getConnection(DBManager.dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet allAppId = stmt.executeQuery("SELECT appointmentId FROM appointment Order BY appointmentId");
            int appId;
            int userId = getUserId(currUser);
            //grabs the last appId value and adds one to the new appointment for the new appId
            if (allAppId.last()) {
                appId = allAppId.getInt(1) + 1;
                allAppId.close();
            } else {
                allAppId.close();
                appId = 1;
            }
            //creates a new entry with the new appId
            stmt.executeUpdate("INSERT INTO appointment VALUES ('" + appId + "', '" + custId + "', '" + title + "', '"
                    + desc + "', '" + location + "', '" + contact + "', '" + url + "', '" + startTimestamp + "', '" + endTimestamp + "', CURRENT_DATE, '" 
                    + currUser + "', CURRENT_TIMESTAMP, '" + currUser + "', '" + type + "', '" + userId + "')"); 
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Appointment");
            alert.setContentText("This function requires a connection to the database.");
            alert.showAndWait();
        }
    }

    /**
     * This method, when called, modifies an existing appointment in the database
     * while checking that it does not overlap another appointment.
     *
     * @param appId
     * @param customer
     * @param title
     * @param desc
     * @param location
     * @param contact
     * @param url
     * @param startUTC
     * @param endUTC
     * @param type
     * @return
     */
    public static boolean modApp(int appId, Customer customer, String title, String desc, String location, String contact,
            String url, ZonedDateTime startUTC, ZonedDateTime endUTC, String type, int userId, int custId) {
        try {
            String startUTCString = startUTC.toString();
            startUTCString = startUTCString.substring(0,10) + " " + startUTCString.substring(11,16) + ":00";
            Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
            String endUTCString = endUTC.toString();
            endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
            Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
            // Check if appointment overlaps with other existing appointments. Show alert and return false if it does.
            if (checkAppOverlapOtherApp(startTimestamp, endTimestamp)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Error Modifying Appointment");
                alert.setContentText("Oops... Appointment times entered overlap another appointment. Please select an appropriate time.");
                alert.showAndWait();
                return false;
            } else {
                // If overlap doesn't occur, update appointment entry and return true
                int customerId = customer.getCustId();
                updateApp(appId, customerId, title, desc, location, contact, url, startTimestamp, endTimestamp);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Modifying Appointment");
            alert.setContentText("This function requires a connection to the database.");
            alert.showAndWait();
            return false;
        }
    }

    /**
     * This method, when called, updates appointment entry in the database.
     *
     * @param appId
     * @param custId
     * @param title
     * @param desc
     * @param location
     * @param contact
     * @param url
     * @param startTimestamp
     * @param endTimestamp
     * @throws SQLException
     */
    private static void updateApp(int appId, int custId, String title, String desc, String location, String contact,
            String url, Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException {
        try (Connection conn = (Connection) DriverManager.getConnection(DBManager.dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE appointment SET customerId = '" + custId + "', title = '" + title + "', description = '" + desc + "', location = '" 
                    + location + "', contact = '" + contact + "', url = '" + url + "', start = '" + startTimestamp + "', end = '" + endTimestamp + 
                    "', lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currUser + "' WHERE appointmentId = '" + appId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, verifies that a new appointment does not overlap another
     * existing appointment. If true, alerts user of overlap. If false, returns
     * false.
     *
     * @param startTimestamp
     * @param endTimestamp
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    private static boolean checkAppOverlapOtherApp(Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException, ParseException {
        int appIndexRemove = AppointmentSummaryController.getAppIndexMod();
        ObservableList<Appointment> appList = AppointmentList.getAppList();
        appList.remove(appIndexRemove);
        for (Appointment appointment : appList) {
            Timestamp existingStartTimestamp = appointment.getStartTimestamp();
            Timestamp existingEndTimestamp = appointment.getEndTimestamp();
            if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method, when called, deletes appointment from database by the appId after
     * alerting the user to confirm the deletion of the appointment.
     * @param appDelete
     */
    public static void deleteApp(Appointment appDelete) {
        int appId = appDelete.getAppId();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Confirm Deletion of Appointment");
        alert.setContentText("Are you sure you want to delete this appointment from the database?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                    Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM appointment WHERE appointmentId = '" + appId + "'");
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert2 = new Alert(AlertType.INFORMATION);
                alert2.setTitle("Error");
                alert2.setHeaderText("Error Modifying Appointment");
                alert2.setContentText("This function requires a connection to the database.");
                alert2.showAndWait();
            }
            // Update appointmentList to remove deleted appointment
            updateAppList();
        }
    }

    /**
     * This method, when called, generates a report for the number of appointment types by
     * month with times then places the Text file in the root folder.
     */
    public static void run101() {
        updateAppList();
        //initializes the report string
        String report = "Number of Appointment Types By Month: \n \n";
        ArrayList<String> monthsWithApp = new ArrayList<>();
        //checks the month and year for each appointment and adds year-month to the ArrayList
        AppointmentList.getAppList().stream().map((app) -> app.getStartDate()).map((startDate) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            return calendar;
        }).map((calendar) -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String yearMonth = year + "-" + month;
            if (month < 10) {
                yearMonth = year + "-0" + month;
            }
            return yearMonth;
        }).filter((yearMonth) -> (!monthsWithApp.contains(yearMonth))).forEachOrdered((yearMonth) -> {
            monthsWithApp.add(yearMonth);
        });
        //sorts the year-months
        Collections.sort(monthsWithApp);
        for (String yearMonth : monthsWithApp) {
            int year = Integer.parseInt(yearMonth.substring(0, 4));
            int month = Integer.parseInt(yearMonth.substring(5, 7));
            int typeCount = 0;
            ArrayList<String> descriptions = new ArrayList<>();
            for (Appointment app : AppointmentList.getAppList()) {
                Date startDate = app.getStartDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                int appYear = calendar.get(Calendar.YEAR);
                int appMonth = calendar.get(Calendar.MONTH) + 1;
                //grabs the appointment description if month and year
                if (year == appYear && month == appMonth) {
                    String appDesc = app.getDesc();
                    if (!descriptions.contains(appDesc)) {
                        descriptions.add(appDesc);
                        typeCount++;
                    }
                }
                }
            //adds year-month to report
            report = report + yearMonth + ": " + typeCount + "\n";
            report = report + "Types: ";
            //adds the descriptions to report
            for (String desc : descriptions) {
                report = report + " " + desc + ",";
            }
            report = report.substring(0, report.length() - 1);
            report = report + "\n \n";
        }
        //prints the report to AppTypeByMonth.txt
        try {
            Path path = Paths.get("AppTypeByMonth.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, generates a report for each consultant's schedule
     * with times then places the Text file in the root folder.
     */
    public static void run102() {
        updateAppList();
        String report = "Upcoming Schedule for Each Consultant: \n \n";
        ArrayList<String> consultantWithApps = new ArrayList<>();
        //checks the createdBy field of each appointment and adds new createdBy's to ArrayList
        AppointmentList.getAppList().stream().map((app) -> app.getCreatedBy()).filter((consultant) -> (!consultantWithApps.contains(consultant))).forEachOrdered((consultant) -> {
            consultantWithApps.add(consultant);
        });
        //sorts the consultants for the report
        Collections.sort(consultantWithApps);
        for (String consultant : consultantWithApps) {
            //adds consultant's name to report
            report = report + consultant + ": \n";
            for (Appointment app : AppointmentList.getAppList()) {
                String appConsultant = app.getCreatedBy();
                //checks if appointment createdBy matches consultant
                if (consultant.equals(appConsultant)) {
                    String date = app.getDateString();
                    String title = app.getTitle();
                    Date startDate = app.getStartDate();
                    String startTime = startDate.toString().substring(11, 16);
                    if (Integer.parseInt(startTime.substring(0, 2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0, 2)) - 12 + startTime.substring(2, 5) + "PM";
                    } else if (Integer.parseInt(startTime.substring(0, 2)) == 12) {
                        startTime = startTime + "PM";
                    } else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = app.getEndDate();
                    String endTime = endDate.toString().substring(11, 16);
                    if (Integer.parseInt(endTime.substring(0, 2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0, 2)) - 12 + endTime.substring(2, 5) + "PM";
                    } else if (Integer.parseInt(endTime.substring(0, 2)) == 12) {
                        endTime = endTime + "PM";
                    } else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20, 23);
                    report = report + date + ": " + title + "\n from " + startTime + "\n to "
                            + endTime + " " + timeZone + ". \n";
                }
            }
            report = report + "\n \n";
        }
        //prints the report to consultantSchedule.txt
        try {
            Path path = Paths.get("ConsultantSchedule.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, generates a report for each customer's upcoming meetings
     * with times then places the Text file in the root folder.
     */
    public static void run103() {
        updateAppList();
        //initializes the report string
        String report = "Upcoming Meetings for Each Customer: \n \n";
        ArrayList<Integer> custIdsWithApps = new ArrayList<>();
        //check custId of each appointment then adds new custId's to ArrayList
        AppointmentList.getAppList().stream().map((app) -> app.getCustId()).filter((custId) -> (!custIdsWithApps.contains(custId))).forEachOrdered((custId) -> {
            custIdsWithApps.add(custId);
        });
        Collections.sort(custIdsWithApps);
        updateCustList();
        for (int customerId : custIdsWithApps) {
            for (Customer customer : CustomerList.getCustList()) {
                //goes through custList then matches for custId
                int customerIdToCheck = customer.getCustId();
                if (customerId == customerIdToCheck) {
                    report = report + customer.getCustName() + ": \n";
                }
            }
            for (Appointment appointment : AppointmentList.getAppList()) {
                int appCustId = appointment.getCustId();
                //check if appointment's customerId matches customer
                if (customerId == appCustId) {
                    String date = appointment.getDateString();
                    String description = appointment.getDesc();
                    Date startDate = appointment.getStartDate();
                    String startTime = startDate.toString().substring(11, 16);
                    if (Integer.parseInt(startTime.substring(0, 2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0, 2)) - 12 + startTime.substring(2, 5) + "PM";
                    } else if (Integer.parseInt(startTime.substring(0, 2)) == 12) {
                        startTime = startTime + "PM";
                    } else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11, 16);
                    if (Integer.parseInt(endTime.substring(0, 2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0, 2)) - 12 + endTime.substring(2, 5) + "PM";
                    } else if (Integer.parseInt(endTime.substring(0, 2)) == 12) {
                        endTime = endTime + "PM";
                    } else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20, 23);
                    report = report + date + ": " + description + "\n from " + startTime + "\n to "
                            + endTime + " " + timeZone + ". \n";
                }
            }
            report = report + "\n \n";
        }
        try {
            Path path = Paths.get("ScheduleByCustomer.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method, when called, cleans up the database entries which are no longer associated
     * with a customer. Garbage Collector method.
     */
    private static void cleanDatabase() {
        try (Connection conn = (Connection) DriverManager.getConnection(dbUrl, user, pass);
                Statement stmt = conn.createStatement()) {
            ResultSet addressIdResultSet = stmt.executeQuery("SELECT DISTINCT addressId FROM customer ORDER BY addressId");
            ArrayList<Integer> addressIdListFromCust = new ArrayList<>();
            while (addressIdResultSet.next()) {
                addressIdListFromCust.add(addressIdResultSet.getInt(1));
            }
            addressIdResultSet = stmt.executeQuery("SELECT DISTINCT addressId FROM address ORDER BY addressId");
            ArrayList<Integer> addressIdListFromAddress = new ArrayList<>();
            while (addressIdResultSet.next()) {
                addressIdListFromAddress.add(addressIdResultSet.getInt(1));
            }
            //creates a list of addressId's that exist in address table but are used in customer table
            for (int i = 0; i < addressIdListFromCust.size(); i++) {
                for (int j = 0; j < addressIdListFromAddress.size(); j++) {
                    if (Objects.equals(addressIdListFromCust.get(i), addressIdListFromAddress.get(j))) {
                        addressIdListFromAddress.remove(j);
                        j--;
                    }
                }
            }
            //deletes address table entries by remaining addressId's
            if (addressIdListFromAddress.isEmpty()) {
            } else {
                for (int addressId : addressIdListFromAddress) {
                    stmt.executeUpdate("DELETE FROM address WHERE addressId = '" + addressId + "'");
                }
            }
            ResultSet cityIdResultSet = stmt.executeQuery("SELECT DISTINCT cityId FROM address ORDER BY cityId");
            ArrayList<Integer> cityIdListFromAddress = new ArrayList<>();
            while (cityIdResultSet.next()) {
                cityIdListFromAddress.add(cityIdResultSet.getInt(1));
            }
            cityIdResultSet = stmt.executeQuery("SELECT DISTINCT cityId FROM city ORDER BY cityId");
            ArrayList<Integer> cityIdListFromCity = new ArrayList<>();
            while (cityIdResultSet.next()) {
                cityIdListFromCity.add(cityIdResultSet.getInt(1));
            }
            //creates a list of existing cityId's in the city table but not used in address table
            for (int i = 0; i < cityIdListFromAddress.size(); i++) {
                for (int j = 0; j < cityIdListFromCity.size(); j++) {
                    if (Objects.equals(cityIdListFromAddress.get(i), cityIdListFromCity.get(j))) {
                        cityIdListFromCity.remove(j);
                        j--;
                    }
                }
            }
            //deletes the city table entries by the remaining cityId's
            if (cityIdListFromCity.isEmpty()) {
            } else {
                for (int cityId : cityIdListFromCity) {
                    stmt.executeUpdate("DELETE FROM city WHERE cityId = '" + cityId + "'");
                }
            }
            ResultSet countryIdResultSet = stmt.executeQuery("SELECT DISTINCT countryId FROM city ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCity = new ArrayList<>();
            while (countryIdResultSet.next()) {
                countryIdListFromCity.add(countryIdResultSet.getInt(1));
            }
            countryIdResultSet = stmt.executeQuery("SELECT DISTINCT countryId FROM country ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCountry = new ArrayList<>();
            while (countryIdResultSet.next()) {
                countryIdListFromCountry.add(countryIdResultSet.getInt(1));
            }
            //creates a list of existing countryId's from the Country table but not used in the city table
            for (int i = 0; i < countryIdListFromCity.size(); i++) {
                for (int j = 0; j < countryIdListFromCountry.size(); j++) {
                    if (Objects.equals(countryIdListFromCity.get(i), countryIdListFromCountry.get(j))) {
                        countryIdListFromCountry.remove(j);
                        j--;
                    }
                }
            }
            //deletes the country table entries by remaining countryId's
            if (countryIdListFromCountry.isEmpty()) {
            } else {
                for (int countryId : countryIdListFromCountry) {
                    stmt.executeUpdate("DELETE FROM country WHERE countryId = '" + countryId + "'");
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Appointment");
            alert.setContentText("This function requires a connection to the database.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
