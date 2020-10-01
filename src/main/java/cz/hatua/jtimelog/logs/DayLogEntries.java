package cz.hatua.jtimelog.logs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;

/**
 * DayLogEntries
 */
public class DayLogEntries {

    LocalTime newDayStart;
    LocalDate day;
    List<LogEntry> entries;
    
    
    DayLogEntries(LocalDate day) {
        this.day = day;
        newDayStart = LocalTime.of(Configuration.getCfgInteger("NEWDAYSTART"), 0);
        entries = new ArrayList<LogEntry>();
    }

    void addLogEntry(LogEntry entry) throws JTimeLogException {
    	validateLogEntry(entry);
    	validateSequence(entry);
        entries.add(entry);
    }
    
    void validateLogEntry(LogEntry le) throws JTimeLogException {
    	if( (le.getDate().compareTo(day) == 0 && le.getTime().compareTo(newDayStart) >= 0) ||
    			(le.getDate().compareTo(day.plusDays(1)) == 0 && le.getTime().compareTo(newDayStart) < 0)) {
    		return;
    	}
    	
    	if(le.getDate().compareTo(day) < 0) {
    		throw new JTimeLogException(String.format("Entry in past day: '%s'", le.toString()));
    	}
    	
    	if( le.getDate().equals(day) && le.getTime().compareTo(newDayStart) < 0 ) {
    		throw new JTimeLogException(String.format("Entry in past time: '%s'", le.toString()));
    	}

    	if(le.getDate().compareTo(day.plusDays(1)) > 0) {
    		throw new JTimeLogException(String.format("Entry in future day: '%s'", le.toString()));
    	}
    	
    	if( le.getDate().equals(day.plusDays(1)) && le.getTime().compareTo(newDayStart) >= 0 ) {
    		throw new JTimeLogException(String.format("Entry in future day: '%s'", le.toString()));
    	}

    	throw new IllegalArgumentException(String.format("Not valid date/time and not valid outer interval for entry: '%s'", le.toString()));
    }

    void validateSequence(LogEntry le) throws JTimeLogException {
    	if(entries.size() == 0) {
    		// there is no sequence, nothing to validate
    		return;
    	}
    	if(entries.get(entries.size()-1).getDateTime().compareTo(le.getDateTime()) <= 0) {
    		return;
    	}
    	// this logEntry is older to last stored logEntry in list
    	throw new JTimeLogException(String.format("Tyring to add log entry '%s' that is older than '%s' log entry for day '%s'",
    			le.entry, entries.get(entries.size()-1).entry, day));
    }
    
    String getStatsForDay() {
        return this.toString();
    }

    public LocalDate getDay() {
        return day;
    }

    public List<LogEntry> getLogEntries() {
    	return entries;
    }
        
    /**
     * if there are no entries, return null so caller need to expect null return value
     * @return LocalDateTime or null if no last entry exists
     */
    public LocalDateTime getLastEntryTimestamp() {
    	if(entries.isEmpty()) {
    		return null;
    	}
    	return entries.get(entries.size()-1).endTime;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");

        sb.append(String.format("Day '%s' entries:\n",day.toString()));
        for(LogEntry e: entries) {
            sb.append(e.toString() + "\n");
        }

        return sb.toString();
    }

}