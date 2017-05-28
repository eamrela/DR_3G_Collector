/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.main;

import com.vodafone.dr.mongo.MongoDB;
import com.vodafone.dr.parsers.RNCParser;
import java.io.File;

/**
 *
 * @author Admin
 */
public class Processor {
    
    public static void main(String[] args) {
        System.out.println("Initializing Mongo DB");
        MongoDB.initializeDB();
        File file = new File("C:\\Documents\\DR_3G\\Schema\\PRX17.log");
            System.out.println("Fetching Internal Cells");
            RNCParser.fetchInternalCells(file);
            System.out.println("Fetching FaCH");
            RNCParser.fetchFach(file);
            System.out.println("Fetching RaCH");
            RNCParser.fetchRach(file);
            System.out.println("Feting PcH");
            RNCParser.fetchPch(file);
            System.out.println("Fetching HsdSCH");
            RNCParser.fetchHsdsch(file);
            System.out.println("Fetching IubLink");
            RNCParser.fetchIuBLink(file);
            System.out.println("Feting Coverage Relation");
            RNCParser.fetchCoverageRelation(file);
            System.out.println("Fetching gsmCells");
            RNCParser.fetchExternalGsmCells(file);
            System.out.println("Finishing Parsing RNC");
      
    }
}
