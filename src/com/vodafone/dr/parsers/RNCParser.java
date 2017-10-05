/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.parsers;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import static com.mongodb.client.model.Updates.set;
import com.mongodb.client.model.WriteModel;
import com.vodafone.dr.mongo.MongoDB;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author Admin
 */
public class RNCParser implements Runnable{
    
    private Element mode;
    private File file;
    
     public static void parseRNC(File file){
        try {
            String RNCName = file.getName().split("\\.")[0];
            TreeMap<String,Document> fachItems = new TreeMap<String,Document>();
            TreeMap<String,Document> rachItems = new TreeMap<String,Document>();
            TreeMap<String,Document> pchItems = new TreeMap<String,Document>();
            TreeMap<String,Document> hsdschItems = new TreeMap<String,Document>();
            TreeMap<String,Document> nodeSynchItems = new TreeMap<String,Document>();
            TreeMap<String,Document> iubEdchItems = new TreeMap<String,Document>();
            TreeMap<String,Document> multiCarrierItems = new TreeMap<String,Document>();
            TreeMap<String,Document> eulItems = new TreeMap<String,Document>();
//            TreeMap<String,Document> eUtranFrequencyItems = new TreeMap<String,Document>();
            List<WriteModel<Document>> utranItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> iubLinkItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> coverageItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> externalGsmItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> sacItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> utranRelationItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> gsmRelationItems = new ArrayList<WriteModel<Document>>();
            List<WriteModel<Document>> extUtranItems = new ArrayList<WriteModel<Document>>();
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document utranCell = null;
            Document utranInnerDetails = null;
            String[] utranVals = null;
            String utranTmp = null;
            String utrankey = null;
            ArrayList<String> utranReservedBy = null;
            //<editor-fold defaultstate="collapsed" desc="Vatiables">
            Document fach = null;
            String fachUtranCellId = null;
            Document rach = null;
            String rachUtranCellId = null;
            Document pch = null;
            String pchUtranCellId = null;
            Document hsdsch = null;
            String hsdschUtranCellId = null;
            String hsdschTmp = null;
            Document IuB = null;
            Document iubLinkInnerDetails = null;
            ArrayList<String> iubLinkReservedBy = null;
            String iubLinkTmp = null;
            String iubLinkkey = null;
            String[] iubLinkVals = null;
            Document coverage = null;
            Document coverageInnerDetails = null;
            String coveragekey = null;
            String[] coverageVals = null;
            String coverageId=null;
            Document externalGsmCell = null;
            String[] externalGsmCellVals = null;
            ArrayList<String> externalGsmCellReservedBy = null;
            Document sac = null;
            String sacId=null;
            String[] sacVals = null;
            ArrayList<String> sacReservedBy = null;
            Document utranRelation = null;
            Document utranRelationInnerDetails = null;
            String utranRelationkey = null;
            String[] utranRelationVals = null;
            String utranRelationId=null;
            Document gsmRelation = null;
            Document gsmRelationInnerDetails = null;
            String gsmRelationkey = null;
            String[] gsmRelationVals = null;
            String gsmRelationId=null;
            String extUtranCellId=null;
            Document extUtranCell = null;
            String[] extUtranCellVals = null;
            Document extUtranCellInnerDetails = null;
            String extUtranCellkey = null;
            String extUtranCellTmp = null;
            ArrayList<String> extUtranCellReservedBy = null;
            Document nodeSynch = null;
            String iubLinkIdNodeSynch = null;
            String nodeSynchTmp = null;
            Document iubEdch = null;
            String iubLinkIdiubEdch = null;
            String iubEdchTmp = null;
            Document multiCarrier = null;
            String multiCarrierUtranCellId = null;
            Document eul = null;
            String eulUtranCellId = null;
            Document eUtranFreq = null;
            String eUtranFreqUtranCellId = null;
//</editor-fold>
            
            while((line=raf.readLine())!=null){
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="UtranCell">
                if(line.contains("RncFunction=1,UtranCell=") && line.split(",").length==2){
                    if(raf.readLine().contains("========")){
                        utranCell = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(utranInnerDetails!=null){
                                    utranCell.append(utrankey, utranInnerDetails);
                                    utranInnerDetails = null;
                                    utrankey = null;
                                }
                                if(utranReservedBy!=null){
                                    utranCell.append("reservedBy", utranReservedBy);
                                }
                                utranCell.append("RNC", RNCName);
                                utranItems.add(new ReplaceOneModel<Document>(new Document("_id",utranCell.get("_id")),utranCell,new UpdateOptions().upsert(true)));
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>") && !line.contains("uraRef") && !line.contains("reservedBy")){
                                    utranVals = removeExtraSpaces(line).split(" ");
                                    if(utranVals.length>1){
                                        if(utranVals[0].contains("accessClassNBarred")){
                                            utranTmp = "";
                                            for (String val : utranVals) {
                                                if(val.matches("\\d+")){
                                                    utranTmp+=","+val;
                                                }
                                            }
                                            if(utranTmp.length()>0){
                                                utranCell.append(utranVals[0], utranTmp.substring(1));
                                            }
                                            utranTmp = null;
                                        }
                                        else if(utranVals[0].contains("accessClassesBarredCs")){
                                            utranTmp = "";
                                            for (String val : utranVals) {
                                                if(val.matches("\\d+")){
                                                    utranTmp+=","+val;
                                                }
                                            }
                                            if(utranTmp.length()>0){
                                                utranCell.append(utranVals[0], utranTmp.substring(1));
                                            }
                                            utranTmp = null;
                                        }
                                        else if(utranVals[0].contains("accessClassesBarredPs")){
                                            utranTmp = "";
                                            for (String val : utranVals) {
                                                if(val.matches("\\d+")){
                                                    utranTmp+=","+val;
                                                }
                                            }
                                            utranCell.append(utranVals[0], utranTmp);
                                            utranTmp = null;
                                        }
                                        else if(utranVals[0].contains("spareA")){
                                            utranTmp = "";
                                            for (String val : utranVals) {
                                                if(val.matches("\\d+")){
                                                    utranTmp+=","+val;
                                                }
                                            }
                                            if(utranTmp.length()>0){
                                                utranCell.append(utranVals[0], utranTmp.substring(1));
                                            }
                                            utranTmp = null;
                                        }
                                        else if(utranVals[0].contains("utranCellPosition")){
                                            utranTmp = "";
                                            for (String val : utranVals) {
                                                if(val.matches("\\d+")){
                                                    utranTmp+=","+val;
                                                }
                                            }
                                            if(utranTmp.length()>0){
                                                utranCell.append(utranVals[0], utranTmp.substring(1));
                                            }
                                            utranTmp = null;
                                        }else if(utranVals[0].contains("UtranCellId")){
                                            utranCell.append("_id", RNCName+"_"+utranVals[1]);
                                            utranCell.append(utranVals[0], utranVals[1]);
                                        }else{
                                            utranCell.append(utranVals[0], utranVals[1]);
                                        }
                                    }else if(utranVals.length==1){
                                        utranCell.append(utranVals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(utranInnerDetails!=null){
                                            utranCell.append(utrankey, utranInnerDetails);
                                            utranInnerDetails = null;
                                            utrankey = null;
                                        }
                                        utranVals = removeExtraSpaces(line).split(" ");
                                        utranInnerDetails = new Document();
                                        utrankey = utranVals[0];
                                    }else if(line.contains(">>>") && !line.contains("reservedBy") && !line.contains("uraRef")){
                                        utranVals = removeExtraSpaces(line).split(" ");
                                        if(utranInnerDetails!=null){
                                            utranInnerDetails.append(utranVals[1].split("\\.")[1], utranVals[3]);
                                        }

                                    }else if(line.contains(">>>") && line.contains("uraRef")){
                                        utranVals = removeExtraSpaces(line).split(" ");
                                        utranCell.append("uraRef", new Document().append("uraRef", utranVals[3]));

                                    }else if(line.contains(">>>") && line.contains("reservedBy")){
                                        utranVals = removeExtraSpaces(line).split(" ");
                                        if(utranReservedBy!=null){
                                            utranReservedBy.add(utranVals[3]);
                                        }
                                    }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                        utranReservedBy = new ArrayList<String>();
                                    }
                                }
                            }
                        }
                    }
                }

            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="Fach">
            if(line.contains(",Fach=") && line.contains("RncFunction=1,UtranCell=")){
                fachUtranCellId = RNCName+"_"+line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Fach="));
                if(raf.readLine().contains("=======")){
                    fach = new Document();
                    fach.append("utranCellId", fachUtranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            fachItems.put(fachUtranCellId,fach);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] valsFach = line.split(" ");
                            if(valsFach.length>1){
                                fach.append(valsFach[0], valsFach[1]);
                            }else if(valsFach.length==1){
                                fach.append(valsFach[0], "");
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="Rach">
            if(line.contains(",Rach=") && line.contains("RncFunction=1,UtranCell=")){
                rachUtranCellId = RNCName+"_"+line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Rach="));
                if(raf.readLine().contains("=======")){
                    rach = new Document();
                    rach.append("utranCellId", rachUtranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            rachItems.put(rachUtranCellId,rach);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] rachVals = line.split(" ");
                            if(rachVals.length>1){
                                rach.append(rachVals[0], rachVals[1]);
                            }else if(rachVals.length==1){
                                rach.append(rachVals[0], "");
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="Pch">
            if(line.contains(",Pch=") && line.contains("RncFunction=1,UtranCell=")){
                pchUtranCellId = RNCName+"_"+line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Pch="));
                if(raf.readLine().contains("=======")){
                    pch = new Document();
                    pch.append("utranCellId", pchUtranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            pchItems.put(pchUtranCellId,pch);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] pchVals = line.split(" ");
                            if(pchVals.length>1){
                                pch.append(pchVals[0], pchVals[1]);
                            }else if(pchVals.length==1){
                                pch.append(pchVals[0], "");
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="Hsdsch">
            if(line.contains(",Hsdsch=") && !line.contains(",MultiCarrier=") && !line.contains(",Eul=") && line.contains("RncFunction=1,UtranCell=")){
                hsdschUtranCellId = RNCName+"_"+line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Hsdsch="));
                if(raf.readLine().contains("=======")){
                    hsdsch = new Document();
                    hsdsch.append("utranCellId", hsdschUtranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            hsdschItems.put(hsdschUtranCellId,hsdsch);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] hsdschVals = line.split(" ");
                            if(hsdschVals.length>=2 && hsdschVals.length<4){
                                hsdsch.append(hsdschVals[0], hsdschVals[1]);
                            }else if(hsdschVals.length==1){
                                hsdsch.append(hsdschVals[0], "");
                            }else if(line.contains("i[")){
                                hsdschTmp = "";
                                for (String val : hsdschVals) {
                                    if(val.matches("\\d+")){
                                        hsdschTmp+=","+val;
                                    }
                                }
                                if(hsdschTmp.length()>0){
                                    hsdsch.append(hsdschVals[0], hsdschTmp.substring(1));
                                }
                                hsdschTmp = null;
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="IubLink">
            if(line.contains("RncFunction=1,IubLink=") && line.split(",").length==2){
                if(raf.readLine().contains("========")){
                    IuB = new Document();
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // comit and break;
                            if(iubLinkInnerDetails!=null){
                                IuB.append(iubLinkkey, iubLinkInnerDetails);
                                iubLinkInnerDetails = null;
                                iubLinkkey = null;
                            }
                            if(iubLinkReservedBy!=null){
                                IuB.append("reservedBy", iubLinkReservedBy);
                            }
                            IuB.append("RNC", RNCName);
                            iubLinkItems.add(new ReplaceOneModel<Document>(new Document("_id",IuB.get("_id")),IuB,new UpdateOptions().upsert(true)));
                            break;
                        }else{
                            if(!line.contains("Struct{") && !line.contains(">>>") && !line.contains("reservedBy")){
                                iubLinkVals = removeExtraSpaces(line).split(" ");
                                if(iubLinkVals.length>1){
                                    if(iubLinkVals[0].contains("spareA")){
                                        iubLinkTmp = "";
                                        for (String val : iubLinkVals) {
                                            if(val.matches("\\d+")){
                                                iubLinkTmp+=","+val;
                                            }
                                        }
                                        if(iubLinkTmp.length()>0){
                                            IuB.append(iubLinkVals[0], iubLinkTmp.substring(1));
                                        }
                                        iubLinkTmp = null;
                                    }else if(iubLinkVals[0].contains("IubLinkId")){
                                        IuB.append("_id", RNCName+"_"+iubLinkVals[1]);
                                        IuB.append(iubLinkVals[0], iubLinkVals[1]);
                                    }else{
                                        IuB.append(iubLinkVals[0], iubLinkVals[1]);
                                    }
                                }else if(iubLinkVals.length==1){
                                    IuB.append(iubLinkVals[0], "0");
                                }
                            }else{
                                if(line.contains("Struct{")){
                                    if(iubLinkInnerDetails!=null){
                                        IuB.append(iubLinkkey, iubLinkInnerDetails);
                                        iubLinkInnerDetails = null;
                                        iubLinkkey = null;
                                    }
                                    iubLinkVals = removeExtraSpaces(line).split(" ");
                                    iubLinkInnerDetails = new Document();
                                    iubLinkkey = iubLinkVals[0];
                                }else if(line.contains(">>>") && !line.contains("reservedBy")){
                                    iubLinkVals = removeExtraSpaces(line).split(" ");
                                    if(iubLinkInnerDetails!=null){
                                        iubLinkInnerDetails.append(iubLinkVals[1].split("\\.")[1], iubLinkVals[3]);
                                    }

                                }else if(line.contains(">>>") && line.contains("reservedBy")){
                                    iubLinkVals = removeExtraSpaces(line).split(" ");
                                    if(iubLinkReservedBy!=null){
                                        iubLinkReservedBy.add(iubLinkVals[3]);
                                    }
                                }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                    iubLinkReservedBy = new ArrayList<String>();
                                }
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="Coverage">
            if(line.contains(",CoverageRelation=") && line.split(",").length==3){
                  coverageId = line.split(",")[1]+","+line.split(",")[2];
                if(raf.readLine().contains("========")){
                    coverage = new Document();
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // comit and break;
                            if(coverageInnerDetails!=null){
                                coverage.append(coveragekey, coverageInnerDetails);
                                coverageInnerDetails = null;
                                coveragekey = null;
                            }
                            coverage.append("RNC", RNCName);
                            coverageItems.add(new ReplaceOneModel<Document>(new Document("_id",coverage.get("_id")),coverage,new UpdateOptions().upsert(true)));
                            break;
                        }else{
                            if(!line.contains("Struct{") && !line.contains(">>>")){
                                coverageVals = removeExtraSpaces(line).split(" ");
                                if(coverageVals.length>1){
                                    if(coverageVals[0].contains("CoverageRelationId")){
                                        coverage.append("_id", RNCName+"_"+coverageId);
                                        coverage.append(coverageVals[0], coverageVals[1]);
                                    }else{
                                        coverage.append(coverageVals[0], coverageVals[1]);
                                    }
                                }else if(coverageVals.length==1){
                                    coverage.append(coverageVals[0], "0");
                                }
                            }else{
                                if(line.contains("Struct{")){
                                    if(coverageInnerDetails!=null){
                                        coverage.append(coveragekey, coverageInnerDetails);
                                        coverageInnerDetails = null;
                                        coveragekey = null;
                                    }
                                    coverageVals = removeExtraSpaces(line).split(" ");
                                    coverageInnerDetails = new Document();
                                    coveragekey = coverageVals[0];
                                }else if(line.contains(">>>")){
                                    coverageVals = removeExtraSpaces(line).split(" ");
                                    if(coverageInnerDetails!=null){
                                        coverageInnerDetails.append(coverageVals[1].split("\\.")[1], coverageVals[3]);
                                    }

                                }
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="ExternalGSMCells">
            if(line.contains(",ExternalGsmCell=") && line.split(",").length==3){
                if(raf.readLine().contains("========")){
                    externalGsmCell = new Document();
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // comit and break;
                            if(externalGsmCellReservedBy!=null){
                                externalGsmCell.append("reservedBy", externalGsmCellReservedBy);
                            }
                            externalGsmCell.append("RNC", RNCName);
                            externalGsmItems.add(new ReplaceOneModel<Document>(new Document("_id",externalGsmCell.get("_id")),externalGsmCell,new UpdateOptions().upsert(true)));
                            break;
                        }else{
                            externalGsmCellVals = removeExtraSpaces(line).split(" ");
                            if(!line.contains(">>>") && !line.contains("reservedBy")){
                                if(externalGsmCellVals.length==1){
                                    externalGsmCell.append(externalGsmCellVals[0], "0");
                                }else if(externalGsmCellVals[0].contains("ExternalGsmCellId")){
                                    externalGsmCell.append("_id", RNCName+"_"+externalGsmCellVals[1]);
                                    externalGsmCell.append(externalGsmCellVals[0], externalGsmCellVals[1]);
                                }else{
                                    externalGsmCell.append(externalGsmCellVals[0], externalGsmCellVals[1]);
                                }
                            }else{
                                if(line.contains(">>>") && line.contains("reservedBy")){
                                    externalGsmCellVals = removeExtraSpaces(line).split(" ");
                                    if(externalGsmCellReservedBy!=null){
                                        externalGsmCellReservedBy.add(externalGsmCellVals[3]);
                                    }
                                }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                    externalGsmCellReservedBy = new ArrayList<String>();
                                }
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="SAC">
            if(line.contains(",ServiceArea=") && line.split(",").length==3){
                sacId = line.split(",")[1]+","+line.split(",")[2];
                if(raf.readLine().contains("========")){
                    sac = new Document();
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // comit and break;
                            if(sacReservedBy!=null){
                                sac.append("reservedBy", sacReservedBy);
                            }
                            sac.append("RNC", RNCName);
                            sacItems.add(new ReplaceOneModel<Document>(new Document("_id",sac.get("_id")),sac,new UpdateOptions().upsert(true)));
                            break;
                        }else{
                            sacVals = removeExtraSpaces(line).split(" ");
                            if(!line.contains(">>>") && !line.contains("reservedBy")){
                                if(sacVals.length==1){
                                    sac.append(sacVals[0], "0");
                                }else if(sacVals[0].contains("ServiceAreaId")){
                                    sac.append("_id", RNCName+"_"+sacId);
                                    sac.append(sacVals[0], sacVals[1]);
                                }else{
                                    sac.append(sacVals[0], sacVals[1]);
                                }
                            }else{
                                if(line.contains(">>>") && line.contains("reservedBy")){
                                    sacVals = removeExtraSpaces(line).split(" ");
                                    if(sacReservedBy!=null){
                                        sacReservedBy.add(sacVals[3]);
                                    }
                                }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                    sacReservedBy = new ArrayList<String>();
                                }
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="UtranRelation">
            if(line.contains(",UtranRelation=") && line.split(",").length==3){
                    utranRelationId = line.split(",")[1]+","+line.split(",")[2];
                    if(raf.readLine().contains("========")){
                        utranRelation = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(utranRelationInnerDetails!=null){
                                    utranRelation.append(utranRelationkey, utranRelationInnerDetails);
                                    utranRelationInnerDetails = null;
                                    utranRelationkey = null;
                                }
                                utranRelation.append("RNC", RNCName);
                                utranRelationItems.add(new ReplaceOneModel<Document>(new Document("_id",utranRelation.get("_id")),utranRelation,new UpdateOptions().upsert(true)));
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>")){
                                    utranRelationVals = removeExtraSpaces(line).split(" ");
                                    if(utranRelationVals.length>1){
                                        if(utranRelationVals[0].contains("UtranRelationId")){
                                            utranRelation.append("_id", RNCName+"_"+utranRelationId);
                                            utranRelation.append(utranRelationVals[0], utranRelationId);
                                        }else{
                                        utranRelation.append(utranRelationVals[0], utranRelationVals[1]);
                                        }
                                    }else if(utranRelationVals.length==1){
                                        utranRelation.append(utranRelationVals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(utranRelationInnerDetails!=null){
                                            utranRelation.append(utranRelationkey, utranRelationInnerDetails);
                                            utranRelationInnerDetails = null;
                                            utranRelationkey = null;
                                        }
                                        utranRelationVals = removeExtraSpaces(line).split(" ");
                                        utranRelationInnerDetails = new Document();
                                        utranRelationkey = utranRelationVals[0];
                                    }else if(line.contains(">>>")){
                                        utranRelationVals = removeExtraSpaces(line).split(" ");
                                        if(utranRelationInnerDetails!=null){
                                        utranRelationInnerDetails.append(utranRelationVals[1].split("\\.")[1], utranRelationVals[3]);
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
//</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="GsmRelation">
                if(line.contains(",GsmRelation=") && line.split(",").length==3){
                    gsmRelationId = line.split(",")[1]+","+line.split(",")[2];
                    if(raf.readLine().contains("========")){
                        gsmRelation = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(gsmRelationInnerDetails!=null){
                                    gsmRelation.append(gsmRelationkey, gsmRelationInnerDetails);
                                    gsmRelationInnerDetails = null;
                                    gsmRelationkey = null;
                                }
                                 gsmRelation.append("RNC", RNCName);
                                 gsmRelationItems.add(new ReplaceOneModel<Document>(new Document("_id",gsmRelation.get("_id")),gsmRelation,new UpdateOptions().upsert(true)));
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>")){
                                    gsmRelationVals = removeExtraSpaces(line).split(" ");
                                    if(gsmRelationVals.length>1){
                                        if(gsmRelationVals[0].contains("GsmRelationId")){
                                            gsmRelation.append("_id", RNCName+"_"+gsmRelationId);
                                            gsmRelation.append(gsmRelationVals[0], gsmRelationId);
                                        }else{
                                        gsmRelation.append(gsmRelationVals[0], gsmRelationVals[1]);
                                        }
                                    }else if(gsmRelationVals.length==1){
                                        gsmRelation.append(gsmRelationVals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(gsmRelationInnerDetails!=null){
                                            gsmRelation.append(gsmRelationkey, gsmRelationInnerDetails);
                                            gsmRelationInnerDetails = null;
                                            gsmRelationkey = null;
                                        }
                                        gsmRelationVals = removeExtraSpaces(line).split(" ");
                                        gsmRelationInnerDetails = new Document();
                                        gsmRelationkey = gsmRelationVals[0];
                                    }else if(line.contains(">>>")){
                                        gsmRelationVals = removeExtraSpaces(line).split(" ");
                                        if(gsmRelationInnerDetails!=null){
                                        gsmRelationInnerDetails.append(gsmRelationVals[1].split("\\.")[1], gsmRelationVals[3]);
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
//</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="ExternalUtranCell">
                if(line.contains(",ExternalUtranCell=") && line.split(",").length==3){
                     extUtranCellId = line.split(",")[1]+","+line.split(",")[2];
                    if(raf.readLine().contains("========")){
                        extUtranCell = new Document();
                        while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // comit and break;
                            if(extUtranCellInnerDetails!=null){
                                extUtranCell.append(extUtranCellkey, extUtranCellInnerDetails);
                                extUtranCellInnerDetails = null;
                                extUtranCellkey = null;
                            }
                            if(extUtranCellReservedBy!=null){
                                extUtranCell.append("reservedBy", extUtranCellReservedBy);
                            }
                            extUtranCell.append("RNC", RNCName);
                            extUtranItems.add(new ReplaceOneModel<Document>(new Document("_id",extUtranCell.get("_id")),extUtranCell,new UpdateOptions().upsert(true)));
                            break;
                        }else{
                            if(!line.contains("Struct{") && !line.contains(">>>") && !line.contains("reservedBy")){
                                extUtranCellVals = removeExtraSpaces(line).split(" ");
                                if(extUtranCellVals.length>1){
                                    if(extUtranCellVals[0].contains("hsAqmCongCtrlSpiSupport")){
                                        extUtranCellTmp = "";
                                        for (String val : extUtranCellVals) {
                                            if(val.matches("\\d+")){
                                                extUtranCellTmp+=","+val;
                                            }
                                        }
                                        if(extUtranCellTmp.length()>0){
                                            extUtranCell.append(extUtranCellVals[0], extUtranCellTmp.substring(1));
                                        }
                                        extUtranCellTmp = null;
                                    }else if(extUtranCellVals[0].contains("ExternalUtranCellId")){
                                        extUtranCell.append("_id", RNCName+"_"+extUtranCellId);
                                        extUtranCell.append(extUtranCellVals[0], extUtranCellVals[1]);
                                    }else{
                                        extUtranCell.append(extUtranCellVals[0], extUtranCellVals[1]);
                                    }
                                }else if(extUtranCellVals.length==1){
                                    extUtranCell.append(extUtranCellVals[0], "0");
                                }
                            }else{
                                if(line.contains("Struct{")){
                                    if(extUtranCellInnerDetails!=null){
                                        extUtranCell.append(extUtranCellkey, extUtranCellInnerDetails);
                                        extUtranCellInnerDetails = null;
                                        extUtranCellkey = null;
                                    }
                                    extUtranCellVals = removeExtraSpaces(line).split(" ");
                                    extUtranCellInnerDetails = new Document();
                                    extUtranCellkey = extUtranCellVals[0];
                                }else if(line.contains(">>>") && !line.contains("reservedBy")){
                                    extUtranCellVals = removeExtraSpaces(line).split(" ");
                                    if(extUtranCellInnerDetails!=null){
                                        extUtranCellInnerDetails.append(extUtranCellVals[1].split("\\.")[1], extUtranCellVals[3]);
                                    }

                                }else if(line.contains(">>>") && line.contains("reservedBy")){
                                    extUtranCellVals = removeExtraSpaces(line).split(" ");
                                    if(extUtranCellReservedBy!=null){
                                        extUtranCellReservedBy.add(extUtranCellVals[3]);
                                    }
                                }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                    extUtranCellReservedBy = new ArrayList<String>();
                                }
                            }
                        }
                    }
                }
            }
//</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="EulMultiCarrier">
                if(line.contains(",MultiCarrier=") && line.contains("RncFunction=1,UtranCell=")){
                multiCarrierUtranCellId = RNCName+"_"+line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Hsdsch="));
                if(raf.readLine().contains("=======")){
                    multiCarrier = new Document();
                    multiCarrier.append("utranCellId", multiCarrierUtranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            multiCarrierItems.put(multiCarrierUtranCellId,multiCarrier);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] valsMultiCarrier = line.split(" ");
                            if(valsMultiCarrier.length>1){
                                multiCarrier.append(valsMultiCarrier[0], valsMultiCarrier[1]);
                            }else if(valsMultiCarrier.length==1){
                                multiCarrier.append(valsMultiCarrier[0], "");
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="Eul">
            if(line.contains(",Eul=") && !line.contains(",MultiCarrier=") && line.contains("RncFunction=1,UtranCell=")){
                eulUtranCellId = RNCName+"_"+line.substring(line.indexOf("UtranCell=")+10, line.indexOf(",Hsdsch="));
                if(raf.readLine().contains("=======")){
                    eul = new Document();
                    eul.append("utranCellId", eulUtranCellId);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            eulItems.put(eulUtranCellId,eul);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] valsEul = line.split(" ");
                            if(valsEul.length>1){
                                eul.append(valsEul[0], valsEul[1]);
                            }else if(valsEul.length==1){
                                eul.append(valsEul[0], "");
                            }
                        }
                    }
                }
            }
            //</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="NodeSynch">
                    if(line.contains(",NodeSynch=") && line.contains("RncFunction=1,IubLink=")){
                    iubLinkIdNodeSynch = RNCName+"_"+line.substring(line.indexOf("IubLink=")+8, line.indexOf(",NodeSynch="));
                    if(raf.readLine().contains("=======")){
                    nodeSynch = new Document();
                    nodeSynch.append("iubLinkId", iubLinkIdNodeSynch);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            nodeSynchItems.put(iubLinkIdNodeSynch,nodeSynch);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] vals = line.split(" ");
                            if(vals.length==2){
                            nodeSynch.append(vals[0], vals[1]);
                            }else if(vals.length==1){
                            nodeSynch.append(vals[0], "");
                            }else if(line.contains("i[")){
                                nodeSynchTmp = "";
                                for (String val : vals) {
                                    if(val.matches("\\d+")){
                                        nodeSynchTmp+=","+val;
                                    }
                                }
                                if(nodeSynchTmp.length()>0){
                               nodeSynch.append(vals[0], nodeSynchTmp.substring(1));
                               }
                                nodeSynchTmp = null;
                            }
                        }
                    }
                    }
                }
//</editor-fold>
                }
                if(line!=null){
                //<editor-fold defaultstate="collapsed" desc="IubEdch">
                if(line.contains(",IubEdch=") && line.contains("RncFunction=1,IubLink=")){
                    iubLinkIdiubEdch = RNCName+"_"+line.substring(line.indexOf("IubLink=")+8, line.indexOf(",IubEdch="));
                    if(raf.readLine().contains("=======")){
                    iubEdch = new Document();
                    iubEdch.append("iubLinkId", iubLinkIdiubEdch);
                    while((line=raf.readLine())!=null){
                        if(line.contains("======")){
                            // commit
                            iubEdchItems.put(iubLinkIdiubEdch,iubEdch);
                            break;
                        }else{
                            line = removeExtraSpaces(line);
                            String [] vals = line.split(" ");
                            if(vals.length==2){
                            iubEdch.append(vals[0], vals[1]);
                            }else if(vals.length==1){
                            iubEdch.append(vals[0], "");
                            }else if(line.contains("i[")){
                                iubEdchTmp = "";
                                for (String val : vals) {
                                    if(val.matches("\\d+")){
                                        iubEdchTmp+=","+val;
                                    }
                                }
                                if(iubEdchTmp.length()>0){
                               iubEdch.append(vals[0], iubEdchTmp.substring(1));
                               }
                                iubEdchTmp = null;
                            }
                        }
                    }
                    }
                }
//</editor-fold>
                }
               
            }

                System.out.println("Finished Parsing\n ======================");
                System.out.println("UtranCells: "+utranItems.size());
                System.out.println("Rach: "+rachItems.size());
                System.out.println("Fach: "+fachItems.size());
                System.out.println("Pch: "+pchItems.size());
                System.out.println("Hsdch: "+hsdschItems.size());
                System.out.println("Coverage: "+coverageItems.size());
                System.out.println("ExtGSM: "+externalGsmItems.size());
                System.out.println("IuBLink: "+iubLinkItems.size());
                System.out.println("SAC: "+sacItems.size());
                System.out.println("GsmRelation: "+gsmRelationItems.size());
                System.out.println("UtranRelation: "+utranRelationItems.size());
                System.out.println("ExternalUtran: "+extUtranItems.size());
                System.out.println("nodeSynch: "+nodeSynchItems.size());
                System.out.println("IubEdch: "+iubEdchItems.size());
                System.out.println("MultiCarrier: "+multiCarrierItems.size());
                System.out.println("Eul: "+eulItems.size());
//                System.out.println("EutranFreqRelation: "+eUtranFrequencyItems.size());

                
                if(!utranItems.isEmpty()){
                System.out.println("Inserting UtranCells");
                MongoDB.getUtranCellCollection().bulkWrite(utranItems);
                }
                if(!fachItems.isEmpty()){
                System.out.println("Inserting Fach");
                for (Map.Entry<String, Document> entry : fachItems.entrySet()) {
                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
                            set("fach", entry.getValue()));
                }
                }
                if(!rachItems.isEmpty()){
                System.out.println("Inserting Rach");
                for (Map.Entry<String, Document> entry : rachItems.entrySet()) {
                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
                            set("rach", entry.getValue()));

                }
                }
                if(!pchItems.isEmpty()){
                System.out.println("Inserting Pch");
                for (Map.Entry<String, Document> entry : pchItems.entrySet()) {
                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
                            set("pch", entry.getValue()));

                }
                }
                if(!hsdschItems.isEmpty()){
                System.out.println("Inserting Hsdsch");
                for (Map.Entry<String, Document> entry : hsdschItems.entrySet()) {
                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
                            set("Hsdsch", entry.getValue()));
                }
                }
                if(!iubLinkItems.isEmpty()){
                System.out.println("Inserting IuBLinks");
                MongoDB.getIubLinkCollection().bulkWrite(iubLinkItems);
                }
                if(!coverageItems.isEmpty()){
                System.out.println("Inserting Coverage");
                MongoDB.getCoverageRelationCollection().bulkWrite(coverageItems);
                }
                if(!externalGsmItems.isEmpty()){
                System.out.println("Inserting externalGSM");
                 MongoDB.getExternalGsmCellCollection().bulkWrite(externalGsmItems);
                }
                if(!sacItems.isEmpty()){
                System.out.println("Inserting SAC");
                MongoDB.getSacCollection().bulkWrite(sacItems);
                }
                if(!gsmRelationItems.isEmpty()){
                System.out.println("Inserting gsmRelation");
                MongoDB.getGsmRelationCollection().bulkWrite(gsmRelationItems);
                }
                if(!utranRelationItems.isEmpty()){
                System.out.println("Inserting utranRelation");
                MongoDB.getUtranRelationCollection().bulkWrite(utranRelationItems);
                }
                if(!extUtranItems.isEmpty()){
                System.out.println("Inserting externalUtran");
                MongoDB.getExternalUtranCellCollection().bulkWrite(extUtranItems);
                }
                if(!nodeSynchItems.isEmpty()){
                System.out.println("Inserting NodeSynch");
                for (Map.Entry<String, Document> entry : nodeSynchItems.entrySet()) {
                    MongoDB.getIubLinkCollection().updateOne(eq("_id",entry.getKey()),
                            set("nodeSynch", entry.getValue()));
                }
                }
                if(!iubEdchItems.isEmpty()){
                System.out.println("Inserting iubEdch");
                for (Map.Entry<String, Document> entry : iubEdchItems.entrySet()) {
                    MongoDB.getIubLinkCollection().updateOne(eq("_id",entry.getKey()),
                            set("iubEdch", entry.getValue()));
                }
                }
                if(!multiCarrierItems.isEmpty()){
                System.out.println("Inserting multiCarrier");
                for (Map.Entry<String, Document> entry : multiCarrierItems.entrySet()) {
                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
                            set("multiCarrier", entry.getValue()));
                }
                }
                if(!eulItems.isEmpty()){
                System.out.println("Inserting Eul");
                for (Map.Entry<String, Document> entry : eulItems.entrySet()) {
                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
                            set("eul", entry.getValue()));
                }
                }
//                if(!eUtranFrequencyItems.isEmpty()){
//                System.out.println("Inserting Eul");
//                for (Map.Entry<String, Document> entry : eUtranFrequencyItems.entrySet()) {
//                    MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()),
//                            set("EutranFreqRelation", entry.getValue()));
//                }
//                }
        } catch (Exception ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } 


        
    }
    
    
    public static void fetchInternalCells(File file){
        List<Document> items = new ArrayList<Document>();
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
                                items.add(cell);
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

                MongoDB.getUtranCellCollection().insertMany(items);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void fetchFach(File file){
        TreeMap<String,Document> items = new TreeMap<String,Document>();
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
                            items.put(utranCellId,fach);
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
            
            for (Map.Entry<String, Document> entry : items.entrySet()) {
                MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()), 
                                    set("fach", entry.getValue()));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchRach(File file){
       TreeMap<String,Document> items = new TreeMap<String,Document>();
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
                            items.put(utranCellId,rach);
                           
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
            
            for (Map.Entry<String, Document> entry : items.entrySet()) {
                 MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()), 
                                    set("rach", entry.getValue()));
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchPch(File file){
         TreeMap<String,Document> items = new TreeMap<String,Document>();
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
                            items.put(utranCellId,pch);
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
            
            for (Map.Entry<String, Document> entry : items.entrySet()) {
                MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()), 
                                    set("pch", entry.getValue()));
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchHsdsch(File file){
        TreeMap<String,Document> items = new TreeMap<String,Document>();
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
                            items.put(utranCellId,hsdsch);
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
            
            for (Map.Entry<String, Document> entry : items.entrySet()) {
                MongoDB.getUtranCellCollection().updateOne(eq("_id",entry.getKey()), 
                                    set("Hsdsch", entry.getValue()));
                            
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public static void fetchIuBLink(File file){
        List<Document> items = new ArrayList<Document>();
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
                                items.add(IuB);
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

                MongoDB.getIubLinkCollection().insertMany(items);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchCoverageRelation(File file){
        List<Document> items = new ArrayList<Document>();
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
                                items.add(coverage);
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
            
            MongoDB.getCoverageRelationCollection().insertMany(items);
                                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchExternalGsmCells(File file){
            List<Document> items = new ArrayList<Document>();
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
                                items.add(externalGsmCell);
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
            
            MongoDB.getExternalGsmCellCollection().insertMany(items);
                                
                } catch (FileNotFoundException ex) {
                            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchSAC(File file){
            List<Document> items = new ArrayList<Document>();
            RandomAccessFile raf;
            try {
            raf = new RandomAccessFile(file, "r");
            String line = null;
            Document sac = null;
            String[] vals = null;
            String tmp = null;
            String key = null;
            ArrayList<String> reservedBy = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",ServiceArea=") && line.split(",").length==3){
                    if(raf.readLine().contains("========")){
                        sac = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(reservedBy!=null){
                                    sac.append("reservedBy", reservedBy);
                                }
                                items.add(sac);
                                break;
                            }else{
                                    vals = removeExtraSpaces(line).split(" ");
                                    if(!line.contains(">>>") && !line.contains("reservedBy")){
                                        if(vals.length==1){
                                        sac.append(vals[0], "0");
                                        }else if(vals[0].contains("ServiceAreaId")){
                                        sac.append("_id", vals[1]);
                                        sac.append(vals[0], vals[1]);
                                        }else{
                                        sac.append(vals[0], vals[1]);
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
            
            MongoDB.getSacCollection().insertMany(items);
                                
                } catch (FileNotFoundException ex) {
                            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // not added 
    public static void fetchUtranRelation(File file){
         List<Document> items = new ArrayList<Document>();
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document utranRelation = null;
            Document utranRelationInnerDetails = null;
            String utranRelationkey = null;
            String[] utranRelationVals = null;
            String utranRelationId=null;
            while((line=raf.readLine())!=null){
                if(line.contains(",UtranRelation=") && line.split(",").length==3){
                    utranRelationId = line.split(",")[1]+line.split(",")[2];
                    if(raf.readLine().contains("========")){
                        utranRelation = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(utranRelationInnerDetails!=null){
                                    utranRelation.append(utranRelationkey, utranRelationInnerDetails);
                                    utranRelationInnerDetails = null;
                                    utranRelationkey = null;
                                }
                                items.add(utranRelation);
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>")){
                                    utranRelationVals = removeExtraSpaces(line).split(" ");
                                    if(utranRelationVals.length>1){
                                        if(utranRelationVals[0].contains("UtranRelationId")){
                                            utranRelation.append("_id", utranRelationId);
                                            utranRelation.append(utranRelationVals[0], utranRelationId);
                                        }else{
                                        utranRelation.append(utranRelationVals[0], utranRelationVals[1]);
                                        }
                                    }else if(utranRelationVals.length==1){
                                        utranRelation.append(utranRelationVals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(utranRelationInnerDetails!=null){
                                            utranRelation.append(utranRelationkey, utranRelationInnerDetails);
                                            utranRelationInnerDetails = null;
                                            utranRelationkey = null;
                                        }
                                        utranRelationVals = removeExtraSpaces(line).split(" ");
                                        utranRelationInnerDetails = new Document();
                                        utranRelationkey = utranRelationVals[0];
                                    }else if(line.contains(">>>")){
                                        utranRelationVals = removeExtraSpaces(line).split(" ");
                                        if(utranRelationInnerDetails!=null){
                                        utranRelationInnerDetails.append(utranRelationVals[1].split("\\.")[1], utranRelationVals[3]);
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            MongoDB.getUtranRelationCollection().insertMany(items);
                                
        } catch (Exception ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchGsmRelation(File file){
         List<Document> items = new ArrayList<Document>();
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            String line = null;
            Document gsmRelation = null;
            Document gsmRelationInnerDetails = null;
            String gsmRelationkey = null;
            String[] gsmRelationVals = null;
            String gsmRelationId=null;
            while((line=raf.readLine())!=null){
                if(line.contains(",GsmRelation=") && line.split(",").length==3){
                    gsmRelationId = line.split(",")[1]+line.split(",")[2];
                    if(raf.readLine().contains("========")){
                        gsmRelation = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(gsmRelationInnerDetails!=null){
                                    gsmRelation.append(gsmRelationkey, gsmRelationInnerDetails);
                                    gsmRelationInnerDetails = null;
                                    gsmRelationkey = null;
                                }
                                items.add(gsmRelation);
                                break;
                            }else{
                                if(!line.contains("Struct{") && !line.contains(">>>")){
                                    gsmRelationVals = removeExtraSpaces(line).split(" ");
                                    if(gsmRelationVals.length>1){
                                        if(gsmRelationVals[0].contains("GsmRelationId")){
                                            gsmRelation.append("_id", gsmRelationId);
                                            gsmRelation.append(gsmRelationVals[0], gsmRelationId);
                                        }else{
                                        gsmRelation.append(gsmRelationVals[0], gsmRelationVals[1]);
                                        }
                                    }else if(gsmRelationVals.length==1){
                                        gsmRelation.append(gsmRelationVals[0], "0");
                                    }
                                }else{
                                    if(line.contains("Struct{")){
                                        if(gsmRelationInnerDetails!=null){
                                            gsmRelation.append(gsmRelationkey, gsmRelationInnerDetails);
                                            gsmRelationInnerDetails = null;
                                            gsmRelationkey = null;
                                        }
                                        gsmRelationVals = removeExtraSpaces(line).split(" ");
                                        gsmRelationInnerDetails = new Document();
                                        gsmRelationkey = gsmRelationVals[0];
                                    }else if(line.contains(">>>")){
                                        gsmRelationVals = removeExtraSpaces(line).split(" ");
                                        if(gsmRelationInnerDetails!=null){
                                        gsmRelationInnerDetails.append(gsmRelationVals[1].split("\\.")[1], gsmRelationVals[3]);
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            MongoDB.getGsmRelationCollection().insertMany(items);
                                
        } catch (Exception ex) {
            Logger.getLogger(RNCParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fetchExternalUtranCell(File file){
      List<Document> items = new ArrayList<Document>();
            RandomAccessFile raf;
            try {
            raf = new RandomAccessFile(file, "r");
            String line = null;
            Document externalUtranCell = null;
            String[] externalUtranCellVals = null;
            ArrayList<String> externalUtranCellKeservedBy = null;
            while((line=raf.readLine())!=null){
                if(line.contains(",ExternalUtranCell=") && line.split(",").length==3){
                    if(raf.readLine().contains("========")){
                        externalUtranCell = new Document();
                        while((line=raf.readLine())!=null){
                            if(line.contains("======")){
                                // comit and break;
                                if(externalUtranCellKeservedBy!=null){
                                    externalUtranCell.append("reservedBy", externalUtranCellKeservedBy);
                                }
                                items.add(externalUtranCell);
                                break;
                            }else{
                                    externalUtranCellVals = removeExtraSpaces(line).split(" ");
                                    if(!line.contains(">>>") && !line.contains("reservedBy")){
                                        if(externalUtranCellVals.length==1){
                                        externalUtranCell.append(externalUtranCellVals[0], "0");
                                        }else if(externalUtranCellVals[0].contains("ExternalUtranCellId")){
                                        externalUtranCell.append("_id", externalUtranCellVals[1]);
                                        externalUtranCell.append(externalUtranCellVals[0], externalUtranCellVals[1]);
                                        }else{
                                        externalUtranCell.append(externalUtranCellVals[0], externalUtranCellVals[1]);
                                        }
                                    }else{
                                    if(line.contains(">>>") && line.contains("reservedBy")){
                                        externalUtranCellVals = removeExtraSpaces(line).split(" ");
                                        if(externalUtranCellKeservedBy!=null){
                                        externalUtranCellKeservedBy.add(externalUtranCellVals[3]);
                                        }
                                    }else if(!line.contains(">>>") && line.contains("reservedBy")){
                                        externalUtranCellKeservedBy = new ArrayList<String>();
                                    }
                                }
                        }
                    }
                }
            }
                }
            
            MongoDB.getExternalUtranCellCollection().insertMany(items);
                                
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

    public RNCParser init(Element mode,File file){
        this.mode = mode;
        this.file = file;
        return this;
    }
    
    @Override
    public void run() {
            if(null!=mode) switch (mode) {
            case ALL:
                System.out.println("Parsing RNC "+file.getName());
                parseRNC(file);
                break;
            case INTERNAL_CELL:
                System.out.println("Fetching Internal cells "+file.getName());
                fetchInternalCells(file);
                break;
            case FACH:
                System.out.println("Fetching FaCH "+file.getName());
                fetchFach(file);
                break;
            case RACH:
                System.out.println("Fetching RaCH "+file.getName());
                fetchRach(file);
                break;
            case PCH:
                System.out.println("Feting PcH "+file.getName());
                fetchPch(file);
                break;
            case HSDSCH:
                System.out.println("Fetching HsdSCH "+file.getName());
                fetchHsdsch(file);
                break;
            case IUBLINK:
                System.out.println("Fetching IubLink "+file.getName());
                fetchIuBLink(file);
                break;
            case COVERAGE:
                System.out.println("Feting Coverage Relation "+file.getName());
                fetchCoverageRelation(file);
                break;
            case EXTERNAL_GSM_CELL:
                System.out.println("Fetching gsmCells "+file.getName());
                fetchExternalGsmCells(file);
                break;
            case SAC:
                System.out.println("Fetching SAC "+file.getName());
                fetchSAC(file);
                break;
            case UTRAN_RELATIONS:
                System.out.println("Fetching RELATIONS "+file.getName());
//                fetchSAC(file);
                break;
            case GSM_RELATIONS:
                System.out.println("Fetching RELATIONS "+file.getName());
//                fetchSAC(file);
                break;
            default:
                break;
        }
    
    }
    
   
    

}
