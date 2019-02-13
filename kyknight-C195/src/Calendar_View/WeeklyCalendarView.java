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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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
public class WeeklyCalendarView {
    
    private Text weekTitle;
    private LocalDate currLocalDate;
    //ArrayList holding all day panes
    private ArrayList<AnchorPaneNode> calendarDayPanes = new ArrayList<>(7);
    //VBox holding all calendar items
    private VBox weeklyCalendarView;
    
    /**
     * This method creates the weekly calendar view with the day title and prior week
     * and next week arrow buttons. 
     * @param localDate 
     */
    public WeeklyCalendarView(LocalDate localDate){
        currLocalDate = localDate;
        //calendar grid pane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600,400);
        calendar.setGridLinesVisible(true);
        
        //create daily individual panes using AnchorPaneNode
        for (int i=0; i<7; i++){ //7 columns
            AnchorPaneNode ap = new AnchorPaneNode();
            ap.setPrefSize(200,400);
            calendar.add(ap, i, 0);
            calendarDayPanes.add(ap);
        }
        
        //days of the week array
        Text[] daysOfWeek;
        daysOfWeek = new Text[]{
            new Text("Monday"), new Text("Tuesday"),
            new Text("Wednesday"), new Text("Thursday"),
            new Text("Friday"), new Text("Saturday"),
            new Text("Sunday")
        };
        //days of the week label panes
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        int col = 0;
        for (Text day : daysOfWeek){
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200,10);
            ap.setBottomAnchor(day, 5.0);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            ap.getChildren().add(day);
            dayLabels.add(ap, col++, 0);
        }
        
        //adding month title and buttons to move between months
        weekTitle = new Text();
        Button priorWeekButton = new Button("\u21E6"); //left arrow symbol
        priorWeekButton.setOnAction(event -> PriorWeek());
        Button nextWeekButton = new Button("\u21E8"); //right arrow symbol
        nextWeekButton.setOnAction(event -> NextWeek());
        //HBox holding title and buttons
        HBox titleBar = new HBox(priorWeekButton, weekTitle, nextWeekButton);
        titleBar.setAlignment(Pos.BASELINE_CENTER);
        
        //numbering of days on calendar populates
        PopCalendar(localDate);
        
        //Layout finalization
        weeklyCalendarView = new VBox(titleBar, dayLabels, calendar);
    }
    
    /**
     * This method counts the appointments for the viewed day, gets the day numbers, and
     * the day title then populates this information into the calendar.
     * @param localDate 
     */
    public void PopCalendar(LocalDate localDate){
        //date to start calendar with
        LocalDate calendarDate = localDate;
        //search prior days one day at a time until there is a monday
        while (!calendarDate.getDayOfWeek().toString().equals("MONDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        //title set for current month
        LocalDate startDate = calendarDate;
        LocalDate endDate = calendarDate.plusDays(6);
        String localizedStartDateMonth = new DateFormatSymbols().getMonths()[startDate.getMonthValue()-1];
        String startDateMonthProper = localizedStartDateMonth.substring(0,1).toUpperCase() + localizedStartDateMonth.substring(1);
        String startDateTitle = startDateMonthProper + " " + startDate.getDayOfMonth();
        String localizedEndDateMonth = new DateFormatSymbols().getMonths()[endDate.getMonthValue()-1];
        String endDateMonthProper = localizedEndDateMonth.substring(0,1).toUpperCase() + localizedEndDateMonth.substring(1);
        String endDateTitle = endDateMonthProper + " " + endDate.getDayOfMonth();
        weekTitle.setText("  " + startDateTitle + " - " + endDateTitle + ", " + endDate.getYear() + "  ");

        //day numbers and appointment count filled in
        for (AnchorPaneNode ap : calendarDayPanes) {
            //existing children cleared
            if (!ap.getChildren().isEmpty()) {
                ap.getChildren().remove(0, ap.getChildren().size());
            }
            //adds day of the month in corner
            Text date = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            ap.setTopAnchor(date, 5.0);
            ap.setLeftAnchor(date, 5.0);
            ap.getChildren().add(date);
            //calculate appointment count for selected day
            ObservableList<Appointment> appList = AppointmentList.getAppList();
            int calendarDateYear = calendarDate.getYear();
            int calendarDateMonth = calendarDate.getMonthValue();
            int calendarDateDay = calendarDate.getDayOfMonth();
            int appCount = 0;
            for (Appointment app : appList) {
                Date appDate = app.getStartDate();
                Calendar calendar  = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(appDate);
                int appYear = calendar.get(Calendar.YEAR);
                int appMonth = calendar.get(Calendar.MONTH) + 1;
                int appDay = calendar.get(Calendar.DAY_OF_MONTH);
                if (calendarDateYear == appYear && calendarDateMonth == appMonth && calendarDateDay == appDay) {
                    appCount++;
                }
            }
            //adds to appointment count if appointment occurs on viewed day
            if (appCount != 0) {
                Text appointmentsForDay = new Text(String.valueOf(appCount));
                appointmentsForDay.setFont(Font.font(30));
                appointmentsForDay.setFill(Color.AQUA);

                ap.getChildren().add(appointmentsForDay);
                ap.setTopAnchor(appointmentsForDay, 20.0);
                ap.setLeftAnchor(appointmentsForDay, 40.0);
            }
            calendarDate = calendarDate.plusDays(1);
        }
    }
    
    /**
     * This method moves the calendar view to the prior week when called.
     */
    private void PriorWeek(){
        currLocalDate = currLocalDate.minusWeeks(1);
        PopCalendar(currLocalDate);
    }
    
    /**
     * This method moves the calendar view to the next week when called.
     */
    private void NextWeek(){
        currLocalDate = currLocalDate.plusWeeks(1);
        PopCalendar(currLocalDate);
    }
    
    public VBox getView(){
        return weeklyCalendarView;
    }
    
    public LocalDate getCurrentLocalDate(){
        return currLocalDate;
    }
}
