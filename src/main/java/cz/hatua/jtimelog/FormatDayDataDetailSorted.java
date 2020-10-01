/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import cz.hatua.jtimelog.logs.DayLogEntries;
import cz.hatua.jtimelog.logs.LogEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class FormatDayDataDetailSorted extends FormatDayData {
	
    @Override
    public String formatDayData(DayLogEntries dayLogEntries) {
        StringBuffer out = new StringBuffer(formatDate(dayLogEntries.getDay()) + "\n\n");
        LocalDateTime lastEntry = null;
        
        long timeSumaMins = 0;
        long timeSumaWorkMins = 0;
        long timeSumaSlackMins = 0;
        
        Map<String,String> items = new HashMap<>();
        
        for (LogEntry le : dayLogEntries.getLogEntries()) {
        	long spanMins = getDiffMins(lastEntry, le.getDateTime());
        	timeSumaMins = timeSumaMins + spanMins;
        	if(le.isWork()) timeSumaWorkMins = timeSumaWorkMins + spanMins;
        	if(le.isSlack()) timeSumaSlackMins = timeSumaSlackMins + spanMins;
        	
            String timeDiff = formatTimeDiff(lastEntry, le.getDateTime());
            String timeSpan = formatTimeSpan(lastEntry, le.getDateTime());

            String fullLine = String.format("%-6s (%5s)  %s\n", timeSpan, timeDiff, le.getMessage());
            items.put( (le.hasTask() ? le.getTask() : le.getMessage()) + ":: " + le.getEntry(), fullLine);
            
            lastEntry = le.getDateTime();
        }

        for(String k: new TreeSet<>(items.keySet())) {
            out.append(items.get(k));
        }
        
        out.append(String.format("\nSuma work :  %s\nSuma slack:  %s\n", formatTimespanMinutes(timeSumaWorkMins), formatTimespanMinutes(timeSumaSlackMins) ));
        return out.toString();
    }

    /*
     * in case from is null, return 0:0
     */
    static String formatTimeDiff(LocalDateTime from, LocalDateTime to) {
//        if (from == null) {
//            return "00:00";
//        }
        long diffMins = getDiffMins(from, to);
        return formatTimespanMinutes(diffMins);
    }

    static long getDiffMins(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(from, to);
    }
    
    /*
     * in case from is null, use to and to to format interval
     */
    static String formatTimeSpan(LocalDateTime from, LocalDateTime to) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Configuration.getCfgString("TIMEPATTERN"));
        if (from == null) {
            return String.format("%s-%s", to.format(dtf), to.format(dtf));
        }
        return String.format("%s-%s", from.format(dtf), to.format(dtf));
    }

}
