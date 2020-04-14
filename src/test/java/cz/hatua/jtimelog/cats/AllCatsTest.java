package cz.hatua.jtimelog.cats;

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

public class AllCatsTest {

	Map<String, String> cfg;
	
	@Before
	public void setupConfig() {
		cfg = new HashMap<>();
		cfg.put("DATAFILE", "/home/standa/jtimelog/jtimelog_test.txt");
		cfg.put("CATEGORIESFILE", "/home/standa/DataSync/zim_notes_smi/Data/gtimelog/tasks.txt");
		cfg.put("NEWDAYSTART", new Integer((LocalTime.now().getHour() + 1) % 24).toString());
		cfg.put("DATETIMEPATTERN", "yyyy-MM-dd HH:mm");
		cfg.put("TIMEPATTERN", "HH:mm");
		Configuration.getConfiguration().resetConfiguration(cfg);
		new File(cfg.get("DATAFILE")).delete();
	}

	@Test
	public void readCats() throws JTimeLogException {
		AllCats ale = new AllCats();
		System.out.println(ale.toString());
	}
}
