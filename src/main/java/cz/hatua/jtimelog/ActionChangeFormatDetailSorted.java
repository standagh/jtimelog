package cz.hatua.jtimelog;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 *
 * @author standa
 */
public class ActionChangeFormatDetailSorted extends AbstractAction {

    FormatDayController formatCtrl;
    JTimeLog app;

    static final long serialVersionUID = 1l; 

    public ActionChangeFormatDetailSorted(String name, Icon icon, KeyStroke shortcut, FormatDayController formatCtrl, JTimeLog app) {
        super(name, icon);
        
        this.formatCtrl = formatCtrl;
        this.app = app;
        super.putValue(ACCELERATOR_KEY, shortcut);
        super.putValue(NAME, name);
        super.putValue(SMALL_ICON, icon);
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        formatCtrl.setCurrent("format_detail_sorted");
        app.entriesChanged(null);
    }

}