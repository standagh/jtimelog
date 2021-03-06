package cz.hatua.jtimelog.logs;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;
import java.util.Collections;

/**
 * Storage
 */
public class Storage {

	private static final Logger log = LoggerFactory.getLogger(Storage.class);

	boolean readingData = false;
	String fileName;
	/*
	 * if null - it means we don't know start day of last read entry -> either empty file or empty line
	 * otherwise startDay for last stored entry in file
	 */
	LocalDate startDayOfLastEntry;

	Storage() throws JTimeLogException {
		readingData = false;
		setFileName(Configuration.getCfgString("DATAFILE"));
                touchDataFile(fileName);
		startDayOfLastEntry = null;
	}

	void setFileName(String fileName) {
		log.debug("Setting storage file '{}'", fileName);
		this.fileName = fileName;
	}

        private void touchDataFile(String fileName) throws JTimeLogException {
		if(! new File(fileName).exists()) {
			try {
				FileUtils.write(new File(fileName), "", "UTF-8");
			} catch (IOException e) {
				throw new JTimeLogException("Unable to create data file", e);
			}
		}
        }
        
	/**
	 * 
	 * @param target - what structure will be updated by reading data from file
	 * 
	 * TODO: do proper testing !!!
	 * TODO: after data are read, new line is not written in front of first line for new accounting day - should be fixed
	 * TODO: 2020-02-04 - after data are read, currentDay is not displayed in GUI
	 */
	void fillAllLogEntriesFromDataFile(AllLogEntries ale) throws JTimeLogException {
		log.debug("Disabling saveLine");
		readingData = true;

		try {
			// read all lines from file
			// parse and split to accounted days
			Map<LocalDate, List<LogEntry>> lnsPerDay = classifyLines(readAllLinesFromDataFile());
			// add day after day to ale.addLogEntry(logEntry, day)
			// TreeSet ensures natural ordering
			for(LocalDate theDay: new TreeSet<LocalDate>(lnsPerDay.keySet())) {
				ale.setCurrentDay(theDay);
				for(LogEntry le: lnsPerDay.get(theDay)) {
					try {
						ale.addLogEntry(le, theDay);
					} catch(JTimeLogException e) {
						String msg = String.format("Error while reading log entry '%s' from file '%s': '%s'", le.entry, fileName, e.getMessage()); 
						log.error(msg, e);
						throw new JTimeLogException(msg, e);
					}
				}
			}
		} finally {
			ale.setCurrentDay(ale.countCurrentDay());
			readingData = false;
			log.debug("Enabling saveLine");
		}
	}

	List<String> readAllLinesFromDataFile() throws JTimeLogException {
		log.debug("Reading lines from data file");
		List<String> lns = getLinesOfDataFile();
		setStartDateOfLastEntry(getLastEntry(lns));
		return lns;
	}
	
	List<String> getLinesOfDataFile() throws JTimeLogException {
		List<String> lns = null;
		try {
			lns = FileUtils.readLines(new File(fileName), "UTF-8");
		} catch (IOException e) {
			log.warn("Unable to read data", e);
			throw new JTimeLogException("Unable to read data", e);
		}
		return lns;
	}
	
	LogEntry getLastEntry(List<String> lns) {
		if(lns.size() == 0) {
			log.debug("list of lines is empty");
			return null;
		}
		return new LogEntry(lns.get(lns.size()-1).trim());
	}
	
	void setStartDateOfLastEntry(LogEntry le) {
		if(le == null) {
			startDayOfLastEntry = null;
			log.debug("startDayOfLastEntry set to null");
		} else {
			startDayOfLastEntry = getStartDayForLogEntry(le);
			log.debug("startDayOfLastEntry set to '{}'", startDayOfLastEntry);
		}
	}
	
	/*
	 * split lines to days and determine accounting day
	 */
	Map<LocalDate, List<LogEntry>> classifyLines(List<String> lns) throws JTimeLogException {
		// we want to be first line empty one, so parsing is simpler
		addEmptyLineToTheBeginning(lns);

		Map<LocalDate, List<LogEntry>> out = new HashMap<>();

		boolean firstLine = false;
		LocalDate theDay = null;
		List<LogEntry> dayEntries = new ArrayList<>();
		
		// usually this will be -1, but we added empty line to the beginning 
		int lnCounter = -2;
		for (String ln : lns) {
			lnCounter++;
			if (ln.trim().length() == 0) {
				firstLine = true;
				continue;
			}
			LogEntry le = new LogEntry(ln);
			if (firstLine) {
				theDay = getStartDayForLogEntry(le);
				if (out.containsKey(theDay)) {
					throw new JTimeLogException(
							String.format("Duplicate theDay '%s' for line no '%d'. NewDayStart is configured to '%s'.",
									theDay, lnCounter, Configuration.getCfgInteger("NEWDAYSTART")));
				}
				dayEntries = new ArrayList<LogEntry>();
				out.put(theDay, dayEntries);
				firstLine = false;
			}
			dayEntries.add(le);
		}
		
		return out;
	}

	void addEmptyLineToTheBeginning(List<String> lns) {
		lns.add(0, "");
	}
	
	static LocalDate getStartDayForLogEntry(LogEntry le) {
		LocalTime lt = le.getTime();
		if (lt.compareTo(LocalTime.of(Configuration.getCfgInteger("NEWDAYSTART"), 0)) >= 0) {
			return le.getDate();
		} else {
			return le.getDate().minusDays(1);
		}
	}

	// TODO: not tested so far
	void saveEntry(LogEntry le) throws JTimeLogException {
		LocalDate eStartDay = getStartDayForLogEntry(le);
		int cmp;
		if(startDayOfLastEntry == null) {
			// if startDayOfLastEntry was not set, yet
			cmp = 0;
		} else {
			cmp = eStartDay.compareTo(startDayOfLastEntry);
		}
		if(cmp < 0) {
			// this is an error in data
			throw new RuntimeException(String.format("StartDay of last stored entry is '%s' while adding entry with start day '%s'", startDayOfLastEntry.toString(), eStartDay.toString()));
		}
		if(cmp > 0) {
			saveEmptyLine();
		}
		saveLine(le.entry);
        setStartDateOfLastEntry(le);
	}
	
	private void saveEmptyLine() throws JTimeLogException {
		log.debug("Writing empty line to data file");
		saveLine("");
	}

	void saveLine(String s) throws JTimeLogException {
		if (readingData == true) {
			throw new JTimeLogException("Trying to write data when reading data flag is true");
		}
		log.debug("Writing message to data file");
		try {
			FileUtils.writeStringToFile(new File(fileName), s.trim()+ "\n", "UTF-8", true);
		} catch (IOException e) {
			log.warn("Unable to write message to datafile", e);
		}
	}

}
