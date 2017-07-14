/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.main;

import com.vodafone.dr.collector.Collector;
import com.vodafone.dr.configuration.AppConf;
import com.vodafone.dr.mongo.MongoDB;
import com.vodafone.dr.parsers.Element;
import com.vodafone.dr.parsers.RNCParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
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
    private static PrintWriter errPW = null;
    
    public static void initApp(String confPath){
        try {
            System.out.println("Initializing App");
            AppConf.configureApp(confPath);
            System.out.println("Initializing Mongo DB");
            MongoDB.initializeDB();
            
//            workingDir = AppConf.getWorkingDir()+"\\DR_3G_"+AppConf.getMydate();
            workingDir = "C:\\tmp\\3G\\DR_3G_Jul 11, 2017";
            printoutsDir = workingDir+"\\printouts";
//            new File(printoutsDir).mkdirs();
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
        
    public static void main(String[] args) {
        args = new String[1];
        args[0] = "C:\\Documents\\DR_3G\\DR3G.conf";
        if(args.length>0){
            String conf = args[0];
            initApp(conf);
            System.out.println("Calling Collector");
            boolean collected = true;
//            boolean collected = collectPrintout();
            System.out.println("Checking Collector Status");
            if(collected){
                System.out.println("Collector is good, going to Loop on files now");
                loopAndParse();
                System.out.println("Finished Parsing Files. ");
            }
//            
//            
            errPW.flush();
            errPW.close();
        }else{
            System.out.println("please enter the path for Configuration file...");
        }
    }
}
