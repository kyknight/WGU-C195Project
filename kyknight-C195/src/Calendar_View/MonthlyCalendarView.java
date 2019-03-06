/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Calendar_View;

import Model.Appointment;
import Model.AppointmentList;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author kyleighknight
 */
public class MonthlyCalendarView {
    
    private Text monthTitle;
    private YearMonth currYearMonth;
    //ArrayList holding all day panes
    private ArrayList<AnchorPaneNode> calendarDayPanes = new ArrayList<>(35);
    //VBox holding all calendar items
    private VBox monthlyCalendarView;
    
    /**
     * This method creates the monthly calendar view with the day title and prior month
     * and next month arrow buttons. 
     * @param yearMonth 
     */
    public MonthlyCalendarView(YearMonth yearMonth){
        currYearMonth = yearMonth;
        //Calendar grid pane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600,400); //
        calendar.setGridLinesVisible(true);
        
        //create daily individual panes using AnchorPaneNode
        for (int i=0; i<5; i++){ //5 rows
            for (int j=0; j<7; j++){ //7 columns
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200,200);
                calendar.add(ap, j, i);
                calendarDayPanes.add(ap);
            }
        }
        
        //days of the week array
        Text[] daysOfWeek;
        daysOfWeek = new Text[]{
            new Text("Sunday"), new Text("Monday"), 
            new Text("Tuesday"), new Text("Wednesday"), 
            new Text("Thursday"), new Text("Friday"), 
            new Text("Saturday")
        };
        //days of week label panes
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        int col = 0;
        for (Text day : daysOfWeek){
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200,10);
            ap.setBottomAnchor(day,5.0);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            ap.getChildren().add(day);
            dayLabels.add(ap, col++, 0);
        }
        
        //adding month title and buttons to move between months
        monthTitle = new Text();
        Button priorMonthButton = new Button("\u21E6"); //left arrow symbol
        priorMonthButton.setOnAction(event -> PriorMonth());
        Button nextMonthButton = new Button("\u21E8"); //right arrow symbol
        nextMonthButton.setOnAction(event -> NextMonth());
        //HBox holding title and buttons
        HBox titleBar = new HBox(priorMonthButton, monthTitle, nextMonthButton);
        titleBar.setAlignment(Pos.BASELINE_CENTER);
        
        //numbering of days on calendar populates
        PopCalendar(yearMonth);
        
        //Layout finalization
        monthlyCalendarView = new VBox(titleBar, dayLabels, calendar);
    }
    
    /**
     * This method counts the appointments for the viewed day, gets the day numbers, and
     * the day title then populates this information into the calendar.
     * @param yearMonth 
     */
    public void PopCalendar(YearMonth yearMonth){
        //date to start calendar with
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        //search prior days one day at a time until there is a sunday
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")){
            calendarDate = calendarDate.minusDays(1);
        }
        
        //title set for current month
        String localizedMonth = new DateFormatSymbols().getMonths()[yearMonth.getMonthValue()-1];
        String properMonth = localizedMonth.substring(0,1).toUpperCase() + localizedMonth.substring(1);
        monthTitle.setText( " " + properMonth + " " + String.valueOf(yearMonth.getYear()) + " ");
        
        //day numbers and appointment count filled in
        for (AnchorPaneNode ap : calendarDayPanes){
            //existing children cleared
            if (!ap.getChildren().isEmpty()) {
                ap.getChildren().remove(0,ap.getChildren().size());
            }
            //adds day of the month in corner
            Text date = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            ap.setTopAnchor(date,5.0);
            ap.setLeftAnchor(date,5.0);
            ap.getChildren().add(date);
            //calculate appointment count for selected day
            ObservableList<Appointment> appList = AppointmentList.getAppList();
            int calendarDateYear = calendarDate.getYear();
            int calendarDateMonth = calendarDate.getMonthValue();
            int calendarDateDay = calendarDate.getDayOfMonth();
            int appCount = 0;
            for (Appointment app : appList){
                Date appDate = app.getStartDate();
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(appDate);
                int appYear = calendar.get(Calendar.YEAR);
                int appMonth = calendar.get(Calendar.MONTH) + 1;
                int appDay = calendar.get(Calendar.DAY_OF_MONTH);
                if(calendarDateYear == appYear && calendarDateMonth == appMonth &&
                        calendarDateDay == appDay){
                    appCount++;
                }
            }
            //adds to appointment count if appointment is on viewed day
            if (appCount != 0){
                Text dayApp = new Text(String.valueOf(appCount));
                dayApp.setFont(Font.font(30));
                dayApp.setTextAlignment(TextAlignment.CENTER);
                dayApp.setFill(Color.DARKCYAN);
                
                ap.getChildren().add(dayApp);
                ap.setTopAnchor(dayApp, 20.0);
                ap.setLeftAnchor(dayApp, 40.0);
            }
            calendarDate = calendarDate.plusDays(1);
        }
    }
    
    /**
     * This method moves the calendar view to the prior month when called.
     */
    private void PriorMonth(){
        currYearMonth = currYearMonth.minusMonths(1);
        PopCalendar(currYearMonth);
    }
    
    /**
     * This method moves the calendar view to the next month when called.
     */
    private void NextMonth(){
        currYearMonth = currYearMonth.plusMonths(1);
        PopCalendar(currYearMonth);
    }
    
    public VBox getView(){
        return monthlyCalendarView;
    }
    
    public YearMonth getCurrentMonth(){
        return currYearMonth;
    }
}
