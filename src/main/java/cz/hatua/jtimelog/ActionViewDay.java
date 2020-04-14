/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
//import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import cz.hatua.jtimelog.logs.Logs;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author standa
 */
public abstract class ActionViewDay extends AbstractAction {

    Logs ctrl;
    JLabel displayDayL;

    static final long serialVersionUID = 1l; 

    public ActionViewDay(String name, Icon icon, KeyStroke shortcut, Logs ctrl, JLabel lbl) {
        super(name, icon);
        
        this.ctrl = ctrl;
        this.displayDayL = lbl;
        super.putValue(ACCELERATOR_KEY, shortcut);
        super.putValue(NAME, name);
        super.putValue(SMALL_ICON, icon);
    }
    
    String formatDate() {
        return ctrl.getDisplayedDay().format(DateTimeFormatter.ofPattern(Configuration.getConfiguration().get("DATEWITHDAYPATTERN")));
    }
    
}
