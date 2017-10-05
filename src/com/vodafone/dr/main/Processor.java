/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.main;

import com.vodafone.dr.collector.Collector;
import com.vodafone.dr.configuration.AppConf;
import com.vodafone.dr.configuration.DR;
import com.vodafone.dr.configuration.DR_Plan;
import com.vodafone.dr.generator.ScriptGenerator;
import com.vodafone.dr.mongo.MongoDB;
import com.vodafone.dr.parsers.Element;
import com.vodafone.dr.parsers.RNCParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Processor {
    
    private static String workingDir = null;
    private static String printoutsDir = null;
    private static String scriptsDir = null;
    private static PrintWriter errPW = null;
    
    public static void initApp(String confPath){
        try {
            System.out.println("Initializing App");
            AppConf.configureApp(confPath);
            System.out.println("Building DR Plan");
            AppConf.configureDR();
            System.out.println("Initializing Mongo DB");
            MongoDB.initializeDB();
            
            workingDir = AppConf.getWorkingDir()+"\\DR_3G_"+AppConf.getMydate();
//            workingDir = "C:\\tmp\\3G\\VDF";
            printoutsDir = workingDir+"\\printouts";
            scriptsDir = workingDir+"\\scripts";
            new File(printoutsDir).mkdirs();
            new File(scriptsDir).mkdirs();
            errPW = new PrintWriter(new File(workingDir+"\\error.log"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean collectPrintout(){
            ExecutorService printoutExecutor = Executors.newCachedThreadPool();
            System.out.println("Submitting Collection Tasks");
            for (Map.Entry<String, Integer> en : AppConf.getNodes().entrySet()) {
                System.out.println("Submitting Task for :"+en.getKey());
                printoutExecutor.submit(new Collector().init(en.getKey(), printoutsDir, errPW));
            }  
            printoutExecutor.shutdown();
            while(!printoutExecutor.isTerminated()){}
            System.out.println("Executer finished");
            return true;
    }
    
    public static void loopAndParse(){
//            File file = new File("C:\\tmp\\3G\\PRX17.log");
            File [] files = new File(printoutsDir).listFiles();
            ExecutorService executor = Executors.newCachedThreadPool();
            System.out.println("Submitting Parsing Tasks");
            for (File file : files) {
                System.out.println("Submitting Task for File: "+file.getName());
                executor.submit(new RNCParser().init(Element.ALL, file));
            }
            executor.shutdown();
            while(!executor.isTerminated()){}
            System.out.println("Executer finished: All Files are now parsed and loaded in Db");   
    }
        
    public static void generateDR(){
        TreeMap<String, DR> Plan = DR_Plan.getDR_PLAN();
        String siteScript = null;
        File mtxDir;
        File targetRNCFile;
        PrintWriter pw;
        for (Map.Entry<String, DR> entry : Plan.entrySet()) {
            siteScript = ScriptGenerator.generateScriptForSite(entry.getValue().getSiteName(),
                                                    entry.getValue().getSourceMTX(),
                                                  entry.getValue().getSourceRNC(), 
                                                  entry.getValue().getTargetMTX(), 
                                                  entry.getValue().getTargetRNC());
            mtxDir = new File(scriptsDir+"/"+entry.getValue().getSourceMTX());
            if(!mtxDir.exists()){
                mtxDir.mkdir();
            }
            targetRNCFile = new File(scriptsDir+"/"+entry.getValue().getSourceMTX()+"/"+entry.getValue().getTargetRNC()+".txt");
        try {
            pw = new PrintWriter(targetRNCFile);
            pw.append(siteScript);
            pw.flush();
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    
    public static void main(String[] args) {
        
        
        if(args.length!=4){
            System.out.println("Please set the input paramters");
            System.out.println("Configuration File");
            System.out.println("Collection Mode: 0/1");
            System.out.println("Parsing Mode: 0/1");
            System.out.println("Generation Mode: 0/1");
            System.exit(1);
        }
//        String conf = "C:\\Documents\\DR_3G\\DR3G.conf";
        

        String conf = args[0];
        initApp(conf);
        
        String collect = args[1];
        String parse = args[2];
        String generate = args[3];
//        String collect = "0";
//        String parse = "1";
//        String generate = "0";
        boolean collected = false;
        
        // Collect 
        if(collect.equals("1")){
            System.out.println("Running in Collection mode");
            System.out.println("Calling Collector");
            collected = collectPrintout();
        }else{
            collected = true;
        }
        // Parse
        System.out.println("Checking Collector Status");
        if(collected && parse.equals("1")){
        System.out.println("Collector is good, going to Loop on files now");
        loopAndParse();
        System.out.println("Finished Parsing Files. ");
        }
        // Generate
        if(generate.equals("1")){
            generateDR();
        }
        
            errPW.flush();
            errPW.close();
       
    }
}
