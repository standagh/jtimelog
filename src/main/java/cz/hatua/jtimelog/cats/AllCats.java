/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog.cats;

import java.util.ArrayList;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import cz.hatua.jtimelog.JTimeLogException;

/**
 *
 * @author standa
 */
public class AllCats {
	private static final Logger log = LoggerFactory.getLogger(AllCats.class);
	
    Map<String,List<Cat>> catsMap;
    Storage storage;
    
    AllCats() throws JTimeLogException {
    	reloadData();
    }
    
    private void initAllCats() {
    	catsMap = new TreeMap<>();
        storage = new Storage();
    }
    
    void reloadData() throws JTimeLogException {
    	initAllCats();
    	storage.fillAllCatsFromDataFile(this);
    }
    
    void addCat(Cat cat) {
    	if(!catsMap.containsKey(cat.getGroup())) {
    		catsMap.put(cat.getGroup(), new ArrayList<Cat>());
    	}
    	catsMap.get(cat.getGroup()).add(cat);
    }
    
    Map<String, List<Cat>> getAllCats() {
        return catsMap;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for(String g: catsMap.keySet()) {
    		for(Cat c: catsMap.get(g)) {
    			String s = c.toString();
    			log.debug("adding cat to sb: '{}'", s);
    			sb.append(c.toString() + "\n");
    		}
    	}
    	return sb.toString();
    }
}
