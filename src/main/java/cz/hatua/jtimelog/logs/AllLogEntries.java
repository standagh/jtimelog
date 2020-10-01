package cz.hatua.jtimelog.logs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;

/**
 * AllLogEntries
 * 
 * 
 */
public class AllLogEntries {

	private static final Logger log = LoggerFactory.getLogger(AllLogEntries.class);
	
    Map<LocalDate, DayLogEntries> entries;
    LocalDate currentDay;
    Storage storage;
    // this functionality should be probably in separate class
    Set<EntriesChangedNotificationListener> notifyList;

    AllLogEntries() throws JTimeLogException {
        initAllLogEntries();
    }

    void initAllLogEntries() throws JTimeLogException {
    	log.debug("Initializing AllLogEntries");
        currentDay = countCurrentDay();
        storage = new Storage();
        entries = new HashMap<>();
        notifyList = new HashSet<>();
    }

    void reloadData() throws JTimeLogException {
    	log.debug("Reloading data");
        initAllLogEntries();
        storage.fillAllLogEntriesFromDataFile(this);
    }

    public LocalDate getCurrentDay() {
        return currentDay;
    }

    void setCurrentDay(LocalDate day) {
    	log.debug("Set currentDay: '{}'", day);
    	currentDay = day;
    }

    void addMessage(String msg) throws JTimeLogException {
    	log.debug("Adding message: '{}'", msg);
        updateCurrentDay();
        addLogEntry(new LogEntry(msg), getCurrentDay());
        storage.saveEntry(new LogEntry(msg));
        
        // last operation
        sendEntriesChangedNotification(getCurrentDay());
    }

    /**
     * Add entry to current day
     * There is no need to add entry to any other day than current one
     * @param entry
     * 
     * 
     * TODO:
     * params: String msg, LocalDateTime tstamp
     * if tstamp == null => 
     * 		use current timestamp
     * 		handle currentDay logic
     * else =>
     * 		use timestamp from parameter
     * 		skip currentDay logic
     * storage saveLine
     */
    void addLogEntry(LogEntry le, LocalDate day) throws JTimeLogException {
    	log.debug("Adding message '{}' for a day '{}'", le, day);
        getDayEntries(day).addLogEntry(le);
    }

    void sendEntriesChangedNotification(LocalDate day) {
    	for(EntriesChangedNotificationListener l: notifyList) {
    		l.entriesChanged(day);
    	}
    }
    
    LocalDateTime getLastEntryTimestamp() {
    	return getDayEntries(currentDay).getLastEntryTimestamp();
    }
    
    /**
     * check if current day needs to be updated
     * 
     * simple implementation - there is split hour statically defined in configuration
     * 
     * TODO: neni vyresene jak se pozna pri startu aplikace, ze mam novy den, tj. jak zajistit aby se pridal prazdny radek
     * - zrejme se to vyresi kdyz se dodela nacteni dat.
     * Tj. pri nacteni dat se jako current day nastavi datum posledniho zaznamu v datech
     */
    LocalDate countCurrentDay() {
        LocalTime timeNow = LocalTime.now();
        int NEWDAYSTART = Configuration.getCfgInteger("NEWDAYSTART");
        
        LocalDate ld;
        if(NEWDAYSTART > timeNow.getHour()) {
            ld = LocalDate.now().minusDays(1);
        } else {
            ld = LocalDate.now();
        }
        log.debug("Counting current day as '{}'", ld);
        return ld;
    }

    /**
     * 
     * @return true if currentDay was updated; false if there was no update needed
     */
    boolean updateCurrentDay() {
    	LocalDate curr = currentDay; 
        currentDay = countCurrentDay();
        return !curr.isEqual(currentDay);
    }

    DayLogEntries getDayEntries(LocalDate day) {
        if(entries.containsKey(day)) {
            return entries.get(day);
        }
        return setupNewDay(day);
    }

    String getStatsForDay(LocalDate day) {
        return getDayEntries(day).getStatsForDay();
    }

    private DayLogEntries setupNewDay(LocalDate day) {
        if(entries.containsKey(day)) {
            return entries.get(day);
        }

        DayLogEntries dle = new DayLogEntries(day);
        entries.put(day, dle);
        return dle;
    }

    void registerChangeNotificationListener(EntriesChangedNotificationListener obj) {
        if(!notifyList.contains(obj)) {
            log.debug("Adding listener '{}'", obj);
            notifyList.add(obj);
        }
    }

    Set<EntriesChangedNotificationListener> getNotifyList() {
    	return notifyList;
    }

    void setNotifyList(Set<EntriesChangedNotificationListener> notifyList) {
    	this.notifyList = notifyList;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for(LocalDate d: entries.keySet()) {
            sb.append(entries.get(d).toString() + "\n");
        }
        return sb.toString();
    }
}
