package cz.hatua.jtimelog;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FormatDayController {
    private static final Logger log = LoggerFactory.getLogger(FormatDayController.class);	
	Map<String,FormatDayGeneral> all;
	FormatDayGeneral current;
	
	public FormatDayController() {
		all = new HashMap<>();
		all.put("format_detail", new FormatDayDataDetail());
		all.put("format_detail_sorted", new FormatDayDataDetailSorted());
		all.put("format_summary", new FormatDayDataGroupByTasks());
		setCurrent("format_detail");
	}

	public FormatDayGeneral getCurrent() {
            return current;
	}
	
	public void setCurrent(String name) {
            log.debug("Setting formatter to: {}", name);
            current = all.get(name);
	}
}
