package cz.hatua.jtimelog.logs;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;


public class StorageTest {

	private static final Logger log = LoggerFactory.getLogger(StorageTest.class);
	
	Map<String,String> cfg;

	@Before
	public void setupConfig() {
        cfg = new HashMap<>();
        cfg.put("DATAFILE", "/home/standa/jtimelog/jtimelog_test.txt");
        cfg.put("CATEGORIESFILE", "/home/standa/jtimelog/jcategories.txt");
        //cfg.put("NEWDAYSTART", new Integer((LocalTime.now().getHour()+1) % 24).toString());
        cfg.put("NEWDAYSTART", "4");
        cfg.put("DATETIMEPATTERN", "yyyy-MM-dd HH:mm");
        cfg.put("TIMEPATTERN", "HH:mm");
		Configuration.getConfiguration().resetConfiguration(cfg);
		new File(cfg.get("DATAFILE")).delete();
		log.debug("Init complete");
	}
	
	@Test
	public void initTest() throws JTimeLogException {
		Storage s = new Storage();
		assertTrue(s.fileName.equals(cfg.get("DATAFILE")));
	}
	
	@Test
	public void readAllLineTest() throws JTimeLogException {
		List<String> lsrc = new ArrayList<>();
		lsrc.add("2020-03-20 09:30: MDT_Support: tickety, reseni s Jirkou a Lukasem mergovani 556; jak se postavit k hotfixu");
		lsrc.add("2020-03-20 09:55: MDT_Schuzka: stand-up");
		lsrc.add("");
		lsrc.add("2020-03-21 10:40: MDT_Support: hotifx UATu @Schuzka");
		
		try {
			FileUtils.write(new File(cfg.get("DATAFILE")), String.join("\n",lsrc), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Storage s = new Storage();
		List<String> lns = s.readAllLinesFromDataFile();
		
		assertTrue(lns.size() == 4);
		for(int i = 0; i<lsrc.size(); i++) {
			assertTrue(lsrc.get(i).equals(lns.get(i)));
		}
	}
	
	@Test
	public void classifyLinesTest() throws JTimeLogException {
		List<String> lns;
		Map<LocalDate, List<LogEntry>> out;
		
		lns = new ArrayList<>();
		lns.add("2020-01-10 10:10: line11");
		lns.add("2020-01-10 11:11: line12");
		lns.add("2020-01-11 01:10: line13");
		lns.add("");
		lns.add("2020-01-11 10:10: line21");
		lns.add("2020-01-11 11:11: line22");
		lns.add("2020-01-11 23:11: line23");
		lns.add("2020-01-12 01:10: line24");
		lns.add("");
		lns.add("");
		lns.add("2020-01-13 10:10: line31");
		lns.add("2020-01-13 11:11: line32");
		lns.add("");
		lns.add("");

		out = new Storage().classifyLines(lns);
		assertTrue( "line 10 keys", out.keySet().contains(LocalDate.of(2020, 1, 10)));
//		for(LogEntry le: out.get(LocalDate.of(2020, 1, 10))) {
//			log.debug(String.format("le: '%s'", le.entry));
//		}
		assertTrue( "line 10", (out.get(LocalDate.of(2020, 1, 10))).size() == 3);
		
		assertTrue( "line 11 keys", out.keySet().contains(LocalDate.of(2020, 1, 11)));
		assertTrue( "line 11", (out.get(LocalDate.of(2020, 1, 11))).size() == 4);
		
		assertTrue( !out.keySet().contains(LocalDate.of(2020, 1, 12)));
		
		assertTrue( "line 13 keys", out.keySet().contains(LocalDate.of(2020, 1, 13)));
		assertTrue( "line 13", (out.get(LocalDate.of(2020, 1, 13))).size() == 2);
	}


	// TODO: add tests to:
	// 		- ale nestoupajici posloupnost timestampu
	//		- timestamp je starsi nez dalsi den - NEWDAYSTART hodina
}
