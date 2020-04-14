package cz.hatua.jtimelog.logs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;
import cz.hatua.jtimelog.Utils;

import org.slf4j.Logger;

/**
 * Controller
 */
public class Logs implements EntriesChangedNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(Logs.class);

    AllLogEntries ale;
    LocalDate displayedDay;
    
    public Logs() throws JTimeLogException {
        log.info("Initializing app {}", this.getClass().getName());
        reInit();
//        ale.setCurrentDay(ale.countCurrentDay());
        displayedDay = ale.getCurrentDay();
//        displayedDay = ale.countCurrentDay();
    }

    public void addMessage(String s) throws JTimeLogException {
    	addMessage(s, LocalDateTime.now());
    }
    
    void addMessage(String s, LocalDateTime ldt) throws JTimeLogException {
    	ale.addMessage(Utils.formatLocalDateTime(ldt) + ": " + s);
    }
    
    public DayLogEntries getDisplayedDayLogEntries() {
    	return getDayLogEntries(displayedDay);
    }
    
    public DayLogEntries getDayLogEntries(LocalDate day) {
    	return ale.getDayEntries(day);
    }
    
    // TODO: remove this
    public String getStatsForDisplayedDay() {
        return ale.getStatsForDay(displayedDay);
    }

    public LocalDate getDisplayedDay() {
        return displayedDay;
    }

    public LocalDate getCurrentDay() {
        // return current accounting day
        return ale.getCurrentDay();
    }
    
    public void setDisplayedDay(LocalDate day) {
        displayedDay = day;
        sendEntriesChangedNotification(displayedDay);
    }
    
    public String getTasksFileName() {
        return Configuration.getConfiguration().get("DATAFILE");
    }

    public void reInit() throws JTimeLogException {
    	// clear notify list, so change notifications won'g be propagated
    	Set<EntriesChangedNotificationListener> backupList = null;
    	if(ale != null) {
    		backupList = ale.getNotifyList();
    	}
    	
        ale = new AllLogEntries();
        ale.reloadData();
        
        if(backupList != null) {
        	ale.setNotifyList(backupList);
        	sendEntriesChangedNotification(displayedDay);
        }
    }

    public void sendEntriesChangedNotification(LocalDate day) {
    	ale.sendEntriesChangedNotification(displayedDay);
    }
    
    public void setNotificationTarget(EntriesChangedNotificationListener obj) {
        ale.registerChangeNotificationListener(obj);
    }

    @Override
    public void entriesChanged(LocalDate day) {
    	// TODO: 2020-02-10 - use logging instead of println
        if(displayedDay.equals(day)) {
            System.out.println(String.format("Entries for displayed day '%s' changed. We need to update stats in app.", day.toString()));
            System.out.println(getStatsForDisplayedDay());
        } else {
            System.out.println(String.format("Entries for day '%s' changed. No need to update stats, displayed day is '%s'.", day.toString(), displayedDay.toString()));
        }
    }

    @Override
    public String toString() {
        return String.format("Displayed day is: '%s'", displayedDay);
    }
}