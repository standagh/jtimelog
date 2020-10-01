/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog.cats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author standa
 */
public class Cat {
    private static final Logger log = LoggerFactory.getLogger(Cat.class);
	
    final static String DEFAULT_GROUP_NAME = "Default";
    final static String DEFAULT_CATEGORY_NOTE = "";
	
    String group;
    String cat;
    String note;

    Cat(String ln) {
    	ln = ln.trim();
    	if(ln.length() == 0) throw new EmptyCatException(String.format("Line is empty: '%s'", ln));
    	if(ln.indexOf('#') == 0) throw new CommentCatException(String.format("Line is comment: '%s'", ln));

    	// Parse group name
        log.debug("Ignoring groups in categories file");
        group = new String(DEFAULT_GROUP_NAME);
    	if(ln.indexOf(':') > 0) {
            ln = ln.substring(ln.indexOf(':') + 1);
    	}
    	
    	// Parse cat name
    	if(ln.indexOf(';') == -1) {
    		cat = ln;
    		ln = "";
    	} else {
    		cat = ln.substring(0, ln.indexOf(';'));
    		ln = ln.substring(ln.indexOf(';') + 1);
    	}
    	if(cat.length() == 0) {
    		throw new IllegalArgumentException(String.format("Category in line is empty: '%s'", ln));
    	}

    	// Parse note
		note = ln;
    }

    
	public String getGroup() {
		return group;
	}
	
	public String getCat() {
		return cat;
	}

	public String getNote() {
		return note;
	}
	
	@Override
	public String toString() {
		return String.format("Group: '%s'; Cat: '%s', Note: '%s'", group.toString(), cat, note);
	}
}
