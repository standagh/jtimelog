package cz.hatua.jtimelog.logs;

import java.time.LocalDate;

/**
 * EntriesChangedNotification
 */
public interface EntriesChangedNotificationListener {
    void entriesChanged(LocalDate day);
}