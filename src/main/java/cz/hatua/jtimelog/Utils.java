package cz.hatua.jtimelog;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utils
 * 
 * 
 * sample entry: 2020-10-15 10:20: This is message
 * timestamp: '2020-10-15 10:20'
 * message: 'This is message'
 */
public class Utils {

	static int SIZE_OF_TIMESTAMP = 16;
	static int BEGIN_OF_MESSAGE = 18;
	
    public static LocalDateTime getDateTimeFromEntry(String entry) {
        // TODO: change exception type, to custom package defined
        if(entry.length() < SIZE_OF_TIMESTAMP) {
            throw new IllegalArgumentException("DateTime not present, entry too short");
        }
        try {
            return LocalDateTime.parse(entry.substring(0, SIZE_OF_TIMESTAMP).replace(" ", "T"));
        } catch(DateTimeException e) {
            throw new IllegalArgumentException("Invalid DateTime format", e);
        }
    }

    public static String getMessageFromEntry(String entry) {
        if(entry.length() < BEGIN_OF_MESSAGE) {
            throw new IllegalArgumentException(String.format("Size of entry is '%d', entry too short", entry.length()));
        }
        return entry.substring(BEGIN_OF_MESSAGE);
    }
    
    public static String formatLocalDateTime(LocalDateTime ldt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Configuration.getCfgString("DATETIMEPATTERN"));
        return LocalDateTime.now().format(dtf);
    }
}