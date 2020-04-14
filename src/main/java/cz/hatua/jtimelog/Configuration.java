package cz.hatua.jtimelog;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration
 */
public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Configuration instance = null;
    private static String ENVPrefix = "JTIMELOG_";

    Map<String, String> items;

    private Configuration() {
        items = new HashMap<>();
        items.put("DATAFILE", "timelog.txt");
        items.put("CATEGORIESFILE", "categories.txt");
        // NEWDAYSTART is used only when adding entries from GUI. From file day separator is empty line
        items.put("NEWDAYSTART", "4");
        items.put("DATETIMEPATTERN", "yyyy-MM-dd HH:mm");
        items.put("TIMEPATTERN", "HH:mm");
        items.put("DATEWITHDAYPATTERN", "yyyy-MM-dd (EEE)");
        items.put("REPORTTOOLPATH", "/opt/gtimelog_tools/");
    }

    public static Configuration getConfiguration() {
        if(instance == null) {
            instance = new Configuration();
            instance.replaceFromEnvVars();
            instance.dumpConfiguration();
        }
        return instance;
    }

    void replaceFromEnvVars() {
        Map<String,String> envVars = System.getenv();
        for(String k: instance.items.keySet()) {
            if(envVars.containsKey(ENVPrefix + k)) {
                String newVal = envVars.get(ENVPrefix + k);
                instance.items.put(k, newVal);
                log.debug("Replacing {}=\"{}\"", ENVPrefix+k, newVal);
            }
        }
    }
    
    void dumpConfiguration() {
        for(String k: instance.items.keySet()) {
            log.debug("{}=\"{}\"", ENVPrefix+k, instance.get(k));
        }
    }
    
    public void resetConfiguration(Map<String,String> cfg) {
    	items = new HashMap<>();
    	for(String k: cfg.keySet()) {
    		items.put(k, cfg.get(k));
    	}
    }
    
    public String get(String key) {
        return items.get(key);
    }
}
