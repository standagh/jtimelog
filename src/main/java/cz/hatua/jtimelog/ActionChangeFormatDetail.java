/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 *
 * @author standa
 */
public class ActionChangeFormatDetail extends AbstractAction {

    FormatDayController formatCtrl;
    JTimeLog app;

    static final long serialVersionUID = 1l; 

    public ActionChangeFormatDetail(String name, Icon icon, KeyStroke shortcut, FormatDayController formatCtrl, JTimeLog app) {
        super(name, icon);
        
        this.formatCtrl = formatCtrl;
        this.app = app;
        super.putValue(ACCELERATOR_KEY, shortcut);
        super.putValue(NAME, name);
        super.putValue(SMALL_ICON, icon);
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        formatCtrl.setCurrent("format_detail");
        app.entriesChanged(null);
    }

}