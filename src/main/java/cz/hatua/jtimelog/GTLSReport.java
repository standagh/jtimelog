/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author standa
 */
public class GTLSReport {
    private static final Logger log = LoggerFactory.getLogger(GTLSReport.class);
    
    public static List<String> getReportTypes() {
        List<String> rt = new ArrayList<>();
        rt.add("gtlsreportd");
        rt.add("gtlsreports");
        rt.add("gtlsreport");
        return rt;
    }
    
    public static String generateReport(String reportType, List<String> params) {
        String val = Configuration.getConfiguration().get("REPORTTOOLPATH");
        val = val + reportType;
        //params.add(0, val);
        List<String> l = new ArrayList<>();
        l.add(val);
        l.addAll(params);
        ProcessBuilder pb = new ProcessBuilder(l);
        pb.redirectErrorStream(true);
        Map<String, String> env = pb.environment();
        env.put("GTIMELOG_NOCOLOR", "1");
        
        StringBuffer out = new StringBuffer();
        try {
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                log.warn(ex.toString(), ex);
            }
            
            log.debug(String.format("Return code: %d", p.exitValue()));
            
            try (InputStream is = p.getInputStream();
                Reader isr = new InputStreamReader(is);
                BufferedReader r = new BufferedReader(isr)) {
                while(true) {
                    String ln = r.readLine();
                    if(ln == null ) {
                        break;
                    }
                    out.append(ln + "\n");
                }
            }
        } catch (IOException ex) {
            log.error(null, ex);
        }
        
        return out.toString();
             
    }
    
}
