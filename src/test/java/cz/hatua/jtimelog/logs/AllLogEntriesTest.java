package cz.hatua.jtimelog.logs;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;
import cz.hatua.jtimelog.logs.AllLogEntries;

public class AllLogEntriesTest {

	Map<String, String> cfg;

	@Before
	public void setupConfig() {
		cfg = new HashMap<>();
		cfg.put("DATAFILE", "/home/standa/jtimelog/jtimelog_test.txt");
		cfg.put("CATEGORIESFILE", "/home/standa/jtimelog/jtasks.txt");
		cfg.put("NEWDAYSTART", new Integer((LocalTime.now().getHour() + 1) % 24).toString());
		cfg.put("DATETIMEPATTERN", "yyyy-MM-dd HH:mm");
		cfg.put("TIMEPATTERN", "HH:mm");
		Configuration.getConfiguration().resetConfiguration(cfg);
		new File(cfg.get("DATAFILE")).delete();
	}

	@Test
	public void currentDayToday() throws JTimeLogException {
		cfg.put("NEWDAYSTART", new Integer(LocalTime.now().getHour()).toString());
		Configuration.getConfiguration().resetConfiguration(cfg);

		AllLogEntries ale = new AllLogEntries();
		LocalDate ld = LocalDate.now();
		LocalDate cd = ale.getCurrentDay();
		assertTrue(
				String.format("ld: '%s'; cd: '%s'; Time: '%s', Config.NEWDAYSTART: '%s'", ld.toString(), cd.toString(),
						LocalTime.now().toString(), Configuration.getConfiguration().get("NEWDAYSTART")),
				ld.equals(cd));
	}

	@Test
	public void currentDayYesterday() throws JTimeLogException {
		cfg.put("NEWDAYSTART", new Integer(LocalTime.now().getHour() + 1).toString());
		Configuration.getConfiguration().resetConfiguration(cfg);

		AllLogEntries ale = new AllLogEntries();
		LocalDate ld = LocalDate.now().minusDays(1);
		LocalDate cd = ale.getCurrentDay();
		assertTrue(
				String.format("ld: '%s'; cd: '%s'; Time: '%s', Config.NEWDAYSTART: '%s'", ld.toString(), cd.toString(),
						LocalTime.now().toString(), Configuration.getConfiguration().get("NEWDAYSTART")),
				ld.equals(cd));
	}

}
