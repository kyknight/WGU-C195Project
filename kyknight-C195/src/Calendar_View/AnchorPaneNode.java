/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Calendar_View;

import java.time.LocalDate;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author kyleighknight
 */
public class AnchorPaneNode extends AnchorPane{
    
    //individual calendar pane date
    private LocalDate date;
    
    //creates individual date panes for day in calendar
    public AnchorPaneNode (Node... children){
        super(children);
    }
    
    //date setter
    public void setDate(LocalDate date){
        this.date = date;
    }
    
    //date getter
    public LocalDate getDate(){
        return date;
    }
}
