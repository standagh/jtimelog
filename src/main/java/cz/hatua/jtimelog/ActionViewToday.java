/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import java.time.format.DateTimeFormatter;

import cz.hatua.jtimelog.logs.Logs;

/**
 *
 * @author standa
 */
public class ActionViewToday extends ActionViewDay {

    static final long serialVersionUID = 1l; 

    public ActionViewToday(String name, Icon icon, KeyStroke shortcut, Logs ctrl, JLabel lbl) {
        super(name, icon, shortcut, ctrl, lbl);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        ctrl.setDisplayedDay(ctrl.getCurrentDay());
        displayDayL.setText(formatDate());
    }
    
    
}
