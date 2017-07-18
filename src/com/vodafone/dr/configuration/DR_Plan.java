/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eamrela
 */
public class DR_Plan {
    private static TreeMap<String,DR> DR_PLAN = new TreeMap<String, DR>();
    
    public static void parsePlan(String filePath){
        try {
            RandomAccessFile raf = new RandomAccessFile(new File(filePath), "r");
            String line = null;
            raf.readLine(); // skipping header
            String[] vals = null;
            DR dr = null;
            while((line=raf.readLine())!=null){
                vals = line.split(",");
                if(vals.length==5){
                    dr = new DR();
                    dr.setSiteName(vals[0]);
                    dr.setSourceMTX(vals[1]);
                    dr.setSourceRNC(vals[2]);
                    dr.setTargetMTX(vals[3]);
                    dr.setTargetRNC(vals[4]);
                    DR_PLAN.put(dr.getSiteName(), dr);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DR_Plan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DR_Plan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getTargetRNC(String cellId){
        for (Map.Entry<String, DR> entry : DR_PLAN.entrySet()) {
            if(entry.getKey().contains(cellId) || cellId.contains(entry.getKey())){
                return entry.getValue().getTargetRNC();
            }
        }
        return null;
    }

    public static TreeMap<String, DR> getDR_PLAN() {
        return DR_PLAN;
    }

    
}
