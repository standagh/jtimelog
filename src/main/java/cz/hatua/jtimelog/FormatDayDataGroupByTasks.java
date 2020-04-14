/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

import cz.hatua.jtimelog.logs.DayLogEntries;
import cz.hatua.jtimelog.logs.LogEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author standa
 */
public class FormatDayDataGroupByTasks extends FormatDayData {

    @Override
    public String formatDayData(DayLogEntries dayLogEntries) {
        StringBuffer out = new StringBuffer(formatDate(dayLogEntries.getDay()) + "\n\n");        
        LocalDateTime lastEntry = null;

        long timeSumaMins = 0;
        long timeSumaWorkMins = 0;
        long timeSumaSlackMins = 0;

        Map<String, Long> tasksTimesMins = new HashMap<>();

        for (LogEntry le : dayLogEntries.getLogEntries()) {
            String t;
            if (le.hasTask()) {
                t = le.getTask();
            } else {
                t = le.getMessage();
            }

            long currentTimeSpan;
            if(!tasksTimesMins.containsKey(t)) {
                currentTimeSpan = 0;
                tasksTimesMins.put(t, currentTimeSpan);
            } else {
                currentTimeSpan = tasksTimesMins.get(t);
            }
            
            long spanMins = getDiffMins(lastEntry, le.getDateTime());
            currentTimeSpan = currentTimeSpan + spanMins;
            tasksTimesMins.put(t, currentTimeSpan);
            
            timeSumaMins = timeSumaMins + spanMins;
            
            if (le.isWork()) {
                timeSumaWorkMins = timeSumaWorkMins + spanMins;
            }
            if (le.isSlack()) {
                timeSumaSlackMins = timeSumaSlackMins + spanMins;
            }

            lastEntry = le.getDateTime();
        }

        
        for(String k: new TreeSet<String>(tasksTimesMins.keySet())) {
            out.append(String.format("            (%s)  %s\n", formatTimespanMinutes(tasksTimesMins.get(k)), k));
        }
        
        
        out.append(String.format("\nSuma work :  %s\nSuma slack:  %s\n", formatTimespanMinutes(timeSumaWorkMins), formatTimespanMinutes(timeSumaSlackMins)));
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Configuration.getConfiguration().get("TIMEPATTERN"));
        if (from == null) {
            return String.format("%s-%s", to.format(dtf), to.format(dtf));
        }
        return String.format("%s-%s", from.format(dtf), to.format(dtf));
    }

}
