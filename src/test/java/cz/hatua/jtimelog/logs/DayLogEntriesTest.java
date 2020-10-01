package cz.hatua.jtimelog.logs;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;
//import cz.hatua.jtimelog.logs.DayLogEntries;
//import cz.hatua.jtimelog.logs.LogEntry;

public class DayLogEntriesTest {

	private static final Logger log = LoggerFactory.getLogger(StorageTest.class);
	
	Map<String,String> cfg;

	@Before
	public void setupConfig() {
        cfg = new HashMap<>();
        cfg.put("DATAFILE", "/home/standa/jtimelog/jtimelog_test.txt");
        cfg.put("CATEGORIESFILE", "/home/standa/jtimelog//jtasks.txt");
        cfg.put("NEWDAYSTART", "4");
        cfg.put("DATETIMEPATTERN", "yyyy-MM-dd HH:mm");
        cfg.put("TIMEPATTERN", "HH:mm");
		Configuration.getConfiguration().resetConfiguration(cfg);
		new File(cfg.get("DATAFILE")).delete();
		log.debug("Setup finished");
	}
	
	@Test
	public void validateLogEntryEndTime() {
		
		DayLogEntries dle = new DayLogEntries(LocalDate.of(2010, 10, 10));
		
		boolean exc = false;

		try {
			exc = false;
			dle.validateLogEntry(new LogEntry("2010-10-09 04:00: m1"));
		} catch(JTimeLogException e) {
			exc = true;
		} finally {
			assertTrue(exc == true);
		}

		try {
			exc = false;
			dle.validateLogEntry(new LogEntry("2010-10-10 03:59: m1"));
		} catch(JTimeLogException e) {
			exc = true;
		} finally {
			assertTrue(exc == true);
		}

		try {
			dle.validateLogEntry(new LogEntry("2010-10-10 04:00: m1"));
			dle.validateLogEntry(new LogEntry("2010-10-11 03:59: m2"));
		} catch (JTimeLogException e) {
			throw new RuntimeException(e);
		}
		
		
		try {
			exc = false;
			dle.validateLogEntry(new LogEntry("2010-10-11 04:00: m1"));
		} catch(JTimeLogException e) {
			exc = true;
		} finally {
			assertTrue(exc == true);
		}

		try {
			exc = false;
			dle.validateLogEntry(new LogEntry("2010-10-11 04:00: m1"));
		} catch(JTimeLogException e) {
			exc = true;
		} finally {
			assertTrue(exc == true);
		}
	}
	
	@Test
	public void addNotGrowingEntry() throws JTimeLogException {
		boolean exc = false;
		
		DayLogEntries dle = new DayLogEntries(LocalDate.of(2010, 10, 10));
		dle.addLogEntry(new LogEntry("2010-10-10 10:00: m1"));
		dle.addLogEntry(new LogEntry("2010-10-10 10:00: m2"));
		try {
			exc = false;
			dle.validateSequence(new LogEntry("2010-10-10 09:59: m3"));
		} catch(JTimeLogException e) {
			exc = true;
		} finally {
			assertTrue(exc == true);
		}
	}
	
}
