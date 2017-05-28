/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.parsers;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import com.vodafone.dr.mongo.MongoDB;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author Admin
 */
public class RNCParser {
    
    public static void fetchInternalCells(File file){
        
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document cell = null;
            Document innerDetails = null;
            String[] vals = null;
            String tmp = null;
            String key = null;
            ArrayList<String> reservedBy = null;
            while((line=raf.readLine())!=null){
                if(line.contains("RncFunction=1,UtranCell=") && line.split(",").length==2){
                    if(raf.readLine().contains("========")){
                        cell = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(innerDetails!=null){
                                    cell.append(key, innerDetails);
                                    innerDetails = null;
                                    key = null;
                                }
                                if(reservedBy!=null){
                                    cell.append("reservedBy", reservedBy);
                                }
                                MongoDB.getUtranCellCollection().insertOne(cell);
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>") && !line.contains("uraRef") && !line.contains("reservedBy")){
                                    vals = removeExtraSpaces(line).split(" ");
                                    if(vals.length>1){
                                        if(vals[0].contains("accessClassNBarred")){
                                            tmp = "";
                                            for (String val : vals) {
                                                if(val.matches("\\d+")){
                                                    tmp+=","+val;
                                                }
                                            }
                                             if(tmp.length()>0){
                                            cell.append(vals[0], tmp.substring(1));
                                            }
                                            tmp = null;
                                        }
                                        else if(vals[0].contains("accessClassesBarredCs")){
                                            tmp = "";
                                            for (String val : vals) {
                                                if(val.matches("\\d+")){
                                                    tmp+=","+val;
                                                }
                                            }
                                             if(tmp.length()>0){
                                            cell.append(vals[0], tmp.substring(1));
                                            }
                                            tmp = null;
                                        }
                                        else if(vals[0].contains("accessClassesBarredPs")){
                                            tmp = "";
                                            for (String val : vals) {
                                                if(val.matches("\\d+")){
                                                    tmp+=","+val;
                                                }
                                            }
                                            cell.append(vals[0], tmp);
                                            tmp = null;
                                        }
                                        else if(vals[0].contains("spareA")){
                                            tmp = "";
                                            for (String val : vals) {
                                                if(val.matches("\\d+")){
                                                    tmp+=","+val;
                                                }
                                            }
                                             if(tmp.length()>0){
                                            cell.append(vals[0], tmp.substring(1));
                                            }
                                            tmp = null;
                                        }
                                        else if(vals[0].contains("utranCellPosition")){
                                            tmp = "";
                                            for (String val : vals) {
                                                if(val.matches("\\d+")){
                                                    tmp+=","+val;
                                                }
                                            }
                                            if(tmp.length()>0){
                                            cell.append(vals[0], tmp.substring(1));
                                            }
                                            tmp = null;
                                        }else if(vals[0].contains("UtranCellId")){
                                        cell.append("_id", vals[1]);
                                        cell.append(vals[0], vals[1]);
                                        }else{
                                        cell.append(vals[0], vals[1]);
                                        }
                                    }else if(vals.length==1){
                                        cell.append(vals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(innerDetails!=null){
                                            cell.append(key, innerDetails);
                                            innerDetails = null;
                                            key = null;
                                        }
                                        vals = removeExtraSpaces(line).split(" ");
                                        innerDetails = new Document();
                                        key = vals[0];
                                    }else if(line.contains(">>>") && !line.contains("reservedBy") && !line.contains("uraRef")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        if(innerDetails!=null){
                                        innerDetails.append(vals[1].split("\\.")[1], vals[3]);
                                        }
                                        
                                    }else if(line.contains(">>>") && line.contains("uraRef")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        cell.append("uraRef", new Document().append("uraRef", vals[3]));
                                    
                                    }else if(line.contains(">>>") && line.contains("reservedBy")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        if(reservedBy!=null){
                                        reservedBy.add(vals[3]);
                                        }
                                    }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                        reservedBy = new ArrayList<String>();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void fetchFach(File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document fach = null;
            String utranCellId = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",Fach=") && line.contains("RncFunction=1,UtranCell=")){
                    utranCellId = line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Fach="));
                    if(raf.readLine().contains("=======")){
                    fach = new Document();
                    fach.append("utranCellId", utranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            MongoDB.getUtranCellCollection().updateOne(eq("_id",utranCellId), 
                                    set("fach", fach));
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] vals = line.split(" ");
                            if(vals.length>1){
                            fach.append(vals[0], vals[1]);
                            }else if(vals.length==1){
                            fach.append(vals[0], "");
                            }
                        }
                    }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchRach(File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document rach = null;
            String utranCellId = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",Rach=") && line.contains("RncFunction=1,UtranCell=")){
                    utranCellId = line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Rach="));
                    if(raf.readLine().contains("=======")){
                    rach = new Document();
                    rach.append("utranCellId", utranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            MongoDB.getUtranCellCollection().updateOne(eq("_id",utranCellId), 
                                    set("rach", rach));
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] vals = line.split(" ");
                            if(vals.length>1){
                            rach.append(vals[0], vals[1]);
                            }else if(vals.length==1){
                            rach.append(vals[0], "");
                            }
                        }
                    }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchPch(File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document pch = null;
            String utranCellId = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",Pch=") && line.contains("RncFunction=1,UtranCell=")){
                    utranCellId = line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Pch="));
                    if(raf.readLine().contains("=======")){
                    pch = new Document();
                    pch.append("utranCellId", utranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            MongoDB.getUtranCellCollection().updateOne(eq("_id",utranCellId), 
                                    set("pch", pch));
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] vals = line.split(" ");
                            if(vals.length>1){
                            pch.append(vals[0], vals[1]);
                            }else if(vals.length==1){
                            pch.append(vals[0], "");
                            }
                        }
                    }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchHsdsch(File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document hsdsch = null;
            String utranCellId = null;
            String tmp = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",Hsdsch=") && line.contains("RncFunction=1,UtranCell=")){
                    utranCellId = line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Hsdsch="));
                    if(raf.readLine().contains("=======")){
                    hsdsch = new Document();
                    hsdsch.append("utranCellId", utranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            MongoDB.getUtranCellCollection().updateOne(eq("_id",utranCellId), 
                                    set("Hsdsch", hsdsch));
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] vals = line.split(" ");
                            if(vals.length==2){
                            hsdsch.append(vals[0], vals[1]);
                            }else if(vals.length==1){
                            hsdsch.append(vals[0], "");
                            }else if(line.contains("i[")){
                                tmp = "";
                                for (String val : vals) {
                                    if(val.matches("\\d+")){
                                        tmp+=","+val;
                                    }
                                }
                                if(tmp.length()>0){
                               hsdsch.append(vals[0], tmp.substring(1));
                               }
                                tmp = null;
                            }
                        }
                    }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public static void fetchIuBLink(File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document IuB = null;
            Document innerDetails = null;
            ArrayList<String> reservedBy = null;
            String tmp = null;
            String key = null;
            String[] vals = null;
            while((line=raf.readLine())!=null){
                if(line.contains("RncFunction=1,IubLink=") && line.split(",").length==2){
                    if(raf.readLine().contains("========")){
                        IuB = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(innerDetails!=null){
                                    IuB.append(key, innerDetails);
                                    innerDetails = null;
                                    key = null;
                                }
                                if(reservedBy!=null){
                                    IuB.append("reservedBy", reservedBy);
                                }
                                MongoDB.getIubLinkCollection().insertOne(IuB);
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>") && !line.contains("reservedBy")){
                                    vals = removeExtraSpaces(line).split(" ");
                                    if(vals.length>1){
                                        if(vals[0].contains("spareA")){
                                            tmp = "";
                                            for (String val : vals) {
                                                if(val.matches("\\d+")){
                                                    tmp+=","+val;
                                                }
                                            }
                                             if(tmp.length()>0){
                                            IuB.append(vals[0], tmp.substring(1));
                                            }
                                            tmp = null;
                                        }else if(vals[0].contains("IubLinkId")){
                                            IuB.append("_id", vals[1]);
                                            IuB.append(vals[0], vals[1]);
                                        }else{
                                        IuB.append(vals[0], vals[1]);
                                        }
                                    }else if(vals.length==1){
                                        IuB.append(vals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(innerDetails!=null){
                                            IuB.append(key, innerDetails);
                                            innerDetails = null;
                                            key = null;
                                        }
                                        vals = removeExtraSpaces(line).split(" ");
                                        innerDetails = new Document();
                                        key = vals[0];
                                    }else if(line.contains(">>>") && !line.contains("reservedBy")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        if(innerDetails!=null){
                                        innerDetails.append(vals[1].split("\\.")[1], vals[3]);
                                        }
                                        
                                    }else if(line.contains(">>>") && line.contains("reservedBy")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        if(reservedBy!=null){
                                        reservedBy.add(vals[3]);
                                        }
                                    }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                        reservedBy = new ArrayList<String>();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchCoverageRelation(File file){
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document coverage = null;
            Document innerDetails = null;
            String key = null;
            String[] vals = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",CoverageRelation=") && line.split(",").length==3){
                    if(raf.readLine().contains("========")){
                        coverage = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(innerDetails!=null){
                                    coverage.append(key, innerDetails);
                                    innerDetails = null;
                                    key = null;
                                }
                                
                                MongoDB.getCoverageRelationCollection().insertOne(coverage);
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>")){
                                    vals = removeExtraSpaces(line).split(" ");
                                    if(vals.length>1){
                                        if(vals[0].contains("CoverageRelationId")){
                                            coverage.append("_id", vals[1]);
                                            coverage.append(vals[0], vals[1]);
                                        }else{
                                        coverage.append(vals[0], vals[1]);
                                        }
                                    }else if(vals.length==1){
                                        coverage.append(vals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(innerDetails!=null){
                                            coverage.append(key, innerDetails);
                                            innerDetails = null;
                                            key = null;
                                        }
                                        vals = removeExtraSpaces(line).split(" ");
                                        innerDetails = new Document();
                                        key = vals[0];
                                    }else if(line.contains(">>>")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        if(innerDetails!=null){
                                        innerDetails.append(vals[1].split("\\.")[1], vals[3]);
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchExternalGsmCells(File file){
        
            RandomAccessFile raf;
            try {
            raf = new RandomAccessFile(file, "r");
            String line = null;
            Document externalGsmCell = null;
            String[] vals = null;
            String tmp = null;
            String key = null;
            ArrayList<String> reservedBy = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",ExternalGsmCell=") && line.split(",").length==3){
                    if(raf.readLine().contains("========")){
                        externalGsmCell = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(reservedBy!=null){
                                    externalGsmCell.append("reservedBy", reservedBy);
                                }
                                MongoDB.getExternalGsmCellCollection().insertOne(externalGsmCell);
                                break;
                            }else{
                                    vals = removeExtraSpaces(line).split(" ");
                                    if(!line.contains(">>>") && !line.contains("reservedBy")){
                                        if(vals.length==1){
                                        externalGsmCell.append(vals[0], "0");
                                        }else if(vals[0].contains("ExternalGsmCellId")){
                                        externalGsmCell.append("_id", vals[1]);
                                        externalGsmCell.append(vals[0], vals[1]);
                                        }else{
                                        externalGsmCell.append(vals[0], vals[1]);
                                        }
                                    }else{
                                    if(line.contains(">>>") && line.contains("reservedBy")){
                                        vals = removeExtraSpaces(line).split(" ");
                                        if(reservedBy!=null){
                                        reservedBy.add(vals[3]);
                                        }
                                    }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                        reservedBy = new ArrayList<String>();
                                    }
                                }
                        }
                    }
                }
            }
                }
                } catch (FileNotFoundException ex) {
                            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String removeExtraSpaces(String str){
        str = str.replaceAll(" +", " ");
        return str.trim();
    }
    
   
    

}
