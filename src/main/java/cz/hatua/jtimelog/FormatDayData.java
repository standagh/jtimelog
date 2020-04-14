/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

import cz.hatua.jtimelog.logs.DayLogEntries;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author standa
 */
public class FormatDayData implements FormatDayGeneral {
    
    @Override
    public String formatDayData(DayLogEntries dayLogEntries) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    static String formatTimespanMinutes(long mins) {
        if(mins < 60) {
            return String.format("   %2d", mins);
        }
    	return String.format("%2d:%02d", mins / 60, mins % 60);
    }

    String formatDate(LocalDate ld) {
        return ld.format(DateTimeFormatter.ofPattern(Configuration.getConfiguration().get("DATEWITHDAYPATTERN")));
    }
    
}
