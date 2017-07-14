/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.mongo;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 *
 * @author Admin
 */
public class MongoDB {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> utranCellCollection;
    private static MongoCollection<Document> iubLinkCollection;
    private static MongoCollection<Document> externalGsmCellCollection;
    private static MongoCollection<Document> sacCollection;
    private static MongoCollection<Document> coverageRelationCollection;
    private static MongoCollection<Document> utranRelationCollection;
    private static MongoCollection<Document> gsmRelationCollection;
    private static MongoCollection<Document> externalUtranCellCollection;

    private static final Block<Document> PRINTBLOCK = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    System.out.println(document.toJson());
                }
         };
    private static String dbHOST;
    private static Integer dbPORT;

    public static void initializeDB(){
        mongoClient = new MongoClient( "localhost" , 27017 );
        database = mongoClient.getDatabase("vodafone_3g_network");
        utranCellCollection = database.getCollection("utranCell"); 
        iubLinkCollection = database.getCollection("iubLink"); 
        coverageRelationCollection = database.getCollection("coverageRelation"); 
        utranRelationCollection = database.getCollection("utranRelation"); 
        gsmRelationCollection = database.getCollection("gsmRelation"); 
        externalGsmCellCollection = database.getCollection("externalGsmCell"); 
        externalUtranCellCollection = database.getCollection("externalUtranCell"); 
        sacCollection = database.getCollection("sac"); 
        
    }

    //<editor-fold defaultstate="collapsed" desc="Setters/Getters">

    public static MongoCollection<Document> getGsmRelationCollection() {
        return gsmRelationCollection;
    }

    public static MongoCollection<Document> getUtranRelationCollection() {
        return utranRelationCollection;
    }

    public static MongoCollection<Document> getExternalUtranCellCollection() {
        return externalUtranCellCollection;
    }

   

    
    public static MongoCollection<Document> getSacCollection() {
        return sacCollection;
    }

    
    
    public static MongoCollection<Document> getExternalGsmCellCollection() {
        return externalGsmCellCollection;
    }

    
    public static MongoCollection<Document> getCoverageRelationCollection() {
        return coverageRelationCollection;
    }

    
    
    public static MongoCollection<Document> getIubLinkCollection() {
        return iubLinkCollection;
    }
    
    
    
    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static void setMongoClient(MongoClient mongoClient) {
        MongoDB.mongoClient = mongoClient;
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(MongoDatabase database) {
        MongoDB.database = database;
    }

    public static MongoCollection<Document> getUtranCellCollection() {
        return utranCellCollection;
    }

    public static void setUtranCellCollection(MongoCollection<Document> cellsCollection) {
        MongoDB.utranCellCollection = cellsCollection;
    }

    public static String getDbHOST() {
        return dbHOST;
    }

    public static void setDbHOST(String dbHOST) {
        MongoDB.dbHOST = dbHOST;
    }

    public static Integer getDbPORT() {
        return dbPORT;
    }

    public static void setDbPORT(Integer dbPORT) {
        MongoDB.dbPORT = dbPORT;
    }
//</editor-fold>
    
    
    
    
}
