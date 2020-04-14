/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog.cats;

import cz.hatua.jtimelog.Configuration;
import cz.hatua.jtimelog.JTimeLogException;

import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author standa
 */
public class Storage {
    private static final Logger log = LoggerFactory.getLogger(Storage.class);
    
    String fileName;
    
    Storage() {
        setFileName(Configuration.getConfiguration().get("CATEGORIESFILE"));
    }

    void setFileName(String fileName) {
        log.debug("Setting storage file '{}'", fileName);
        this.fileName = fileName;
    }
    
    void fillAllCatsFromDataFile(AllCats allCats) throws JTimeLogException {
    	for(Cat c: getAllCatsFromDataFile()) {
    		allCats.addCat(c);
    	}
    }

    List<Cat> getAllCatsFromDataFile() throws JTimeLogException {
        createSampleCatsFile();
    	List<String> lns = null;
    	try {
			lns = FileUtils.readLines(new File(fileName), "UTF-8");
			log.debug("Read '{}' lines from data file", lns.size());
		} catch (IOException e) {
			log.warn("Unable to read data", e);
			throw new JTimeLogException("Unable to read data", e);
		}
    	
    	List<Cat> cats = new ArrayList<>();
    	for(String ln: lns ) {
    		try {
    			cats.add(new Cat(ln));
    		} catch(IllegalArgumentException e) {
    			if(e instanceof EmptyCatException) {
    				log.debug("Empty cat: '{}'", ln);
    				continue;
    			}
    			if(e instanceof CommentCatException) {
    				log.debug("Comment cat: '{}'", ln);
    				continue;
    			}
    			log.debug("Unable to create Category", e);
    		}
    	}
    	return cats;
    }
    
    void createSampleCatsFile() throws JTimeLogException {
        File f = new File(fileName);
        if(f.exists() && f.isFile()) {
            if(f.isFile()) {
                log.debug("Cats file '{}' exists and is file. Skipping.", fileName);
            } else {
                log.error("Categories file '{}' is not a file.", fileName);
                throw new JTimeLogException(String.format("Categories file '%s' is not a file.", fileName));
            }
        } else {
            // create default cats file
            List<String> lsrc = new ArrayList<>();
            lsrc.add("Project_ML1;Milestone 1 of Project PROJECT");
            lsrc.add("Project_ML1_Design;Design part of Milestone 1 of Project PROJECT - ");
            lsrc.add("@Call;Tag call");
            lsrc.add("@Meeting;Tag meeting");

            try {
                    FileUtils.write(f, String.join("\n",lsrc), "UTF-8");
            } catch (IOException e) {
                    log.error(e.toString(), e);
                    throw new JTimeLogException(e.toString(), e);
            }        
        }
        
    }
}
