/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog.cats;

import cz.hatua.jtimelog.Configuration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.JTimeLogException;

/**
 *
 * @author standa
 */
public class Cats {
    private static final Logger log = LoggerFactory.getLogger(Cats.class);
    
    AllCats allCats;
    
    public Cats() throws JTimeLogException {
        log.debug("initializing constructor");
        reloadCats();
    }

    public String getCategoriesFileName() {
        return Configuration.getConfiguration().get("CATEGORIESFILE");
    }
    
    public void reloadCats() throws JTimeLogException {
        allCats = new AllCats();
    }
    
    public Map<String, List<Cat>> getAllCats() {
        return allCats.getAllCats();
    }
   
    
}
