/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.configuration;

/**
 *
 * @author eamrela
 */
public class DR {
    
    private String siteName;
    private String sourceMTX;
    private String sourceRNC;
    private String targetMTX;
    private String targetRNC;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    
    public String getSourceMTX() {
        return sourceMTX;
    }

    public void setSourceMTX(String sourceMTX) {
        this.sourceMTX = sourceMTX;
    }

    public String getSourceRNC() {
        return sourceRNC;
    }

    public void setSourceRNC(String sourceRNC) {
        this.sourceRNC = sourceRNC;
    }

    public String getTargetMTX() {
        return targetMTX;
    }

    public void setTargetMTX(String targetMTX) {
        this.targetMTX = targetMTX;
    }

    public String getTargetRNC() {
        return targetRNC;
    }

    public void setTargetRNC(String targetRNC) {
        this.targetRNC = targetRNC;
    }
    
    
}
