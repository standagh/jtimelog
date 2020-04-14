/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog.logs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hamcrest.core.IsSame;

import cz.hatua.jtimelog.Utils;

/**
 *
 * @author standa
 */
public class LogEntry {
    
    String entry;
    LocalDateTime endTime;
    String message;
    String task;

    LogEntry(String s) {
        entry = s;
        endTime = getEndTimeFromEntry(s);
        message = getMessageFromEntry(s);
        task = getTaskFromMessage(message);
    }

    LocalDateTime getEndTimeFromEntry(String s) {
        // we expect line starts with: '2020-01-10 10:10: msg'
        return Utils.getDateTimeFromEntry(s);
    }

    String getMessageFromEntry(String s) {
    	 return Utils.getMessageFromEntry(s);
    }
    
    String getTaskFromMessage(String s) {
        String p1 = s.split(":")[0];
        if(p1.matches("^[\\p{Alnum}_-]+$")) {
            return p1;
        } else {
            return null;
        }
    }
    
    public String getEntry() {
    	return entry;
    }
    
    public boolean hasTask() {
        if(task == null) {
            return false;
        }
        return true;
    }
    
    public String getTask() {
        if(!hasTask()) throw new NullPointerException("Task is not set");
        return task;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getDateTime() {
        return endTime;
    }

    LocalDate getDate() {
        return endTime.toLocalDate();
    }

    LocalTime getTime() {
        return endTime.toLocalTime();
    }

    public boolean isWork() {
    	return !isSlack();
    }
    
    public boolean isSlack() {
    	if(message.startsWith("** ")) return true;
    	return false;
    }
    
    @Override
    public String toString() {
        return (endTime == null ? "?? " : "") + entry;
    }

}
