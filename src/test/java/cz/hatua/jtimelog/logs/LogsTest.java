package cz.hatua.jtimelog.logs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;

/**
 * Controller
 */
public class LogsTest {

	Map<String,String> cfg;

	@Before
	public void setupConfig() {
        cfg = new HashMap<>();
        cfg.put("DATAFILE", "/home/standa/jtimelog/jtimelog_test.txt");
        cfg.put("CATEGORIESFILE", "/home/standa/jtimelog/jtasks.txt");
        cfg.put("NEWDAYSTART", Integer.valueOf((LocalTime.now().getHour()+1) % 24 ).toString());
        cfg.put("DATETIMEPATTERN", "yyyy-MM-dd HH:mm");
        cfg.put("TIMEPATTERN", "HH:mm");
		Configuration.getConfiguration().resetConfiguration(cfg);
		new File(cfg.get("DATAFILE")).delete();
	}

    @Test
    public void init() throws JTimeLogException {
		Logs c = new Logs();
        LocalDate ld = c.ale.countCurrentDay();
        assertEquals(ld, c.getDisplayedDay());
    }

    @Test
    public void addEntryInvalidTimestamp() throws JTimeLogException {
		Logs c = new Logs();
        try {
            c.addMessage("This is invalid entry");
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }
    }

    @Test
    public void addEntry() throws JTimeLogException {
		Logs c = new Logs();
        String msg = "This is valid entry";
        c.addMessage(msg, LocalDateTime.now());
        
        DayLogEntries dle = c.ale.getDayEntries(c.displayedDay);
        assertTrue(String.format("Size of dle.entries: '%d', expected 1", dle.entries.size()), dle.entries.size() == 1);
        assertTrue(String.format("Message: '%s'; expected: '%s'", dle.entries.get(0).message, msg), dle.entries.get(0).message.equals(msg));
    }


}