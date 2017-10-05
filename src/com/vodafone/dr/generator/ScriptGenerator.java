/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vodafone.dr.generator;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.and;
import com.vodafone.dr.configuration.AppConf;
import com.vodafone.dr.configuration.DR_Plan;
import com.vodafone.dr.mongo.MongoDB;
import org.bson.Document;

/**
 *
 * @author eamrela
 */
public class ScriptGenerator {
    
    public static String generateScriptForSite(String siteName,String sourceMTX,String sourceRNC,String targetMTX,String targetRNC){
        StringBuilder builder = new StringBuilder("");
        builder.append("!!!!  Source MTX:   ").append(sourceMTX).append("     !!!!\n");
        builder.append("!!!!  Source RNC:   ").append(sourceRNC).append("     !!!!\n");
        builder.append("!!!!  Target MTX:   ").append(targetMTX).append("     !!!!\n");
        builder.append("!!!!  Target RNC:   ").append(targetRNC).append("     !!!!\n");
        builder.append("!!!!  SITE:   ").append(siteName).append("     !!!!\n");
        builder.append("!!!!       Ura       !!!!").append("\n");
        builder.append(generateUra()).append("\n");
        builder.append("!!!!       Location Area       !!!!").append("\n");
        builder.append(generateLocationArea(siteName, sourceRNC)).append("\n");
        builder.append("!!!!       IuBLinks       !!!!").append("\n");
        builder.append(generateIubLink(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       NodeSynch / Edch       !!!!").append("\n");
        builder.append(generateNodeSynchAndEdch(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       SAC       !!!!").append("\n");
        builder.append(generateSac(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       External GSM Cells       !!!!").append("\n");
        builder.append(generateExternalGsmCells(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Internal Utran Cells       !!!!").append("\n");
        builder.append(generateInternalCells(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Pch,Fach,Rach       !!!!").append("\n");
        builder.append(generateFachRachPch(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Hsdsch       !!!!").append("\n");
        builder.append(generateHsdsch(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       MultiCarrier and EUL       !!!!").append("\n");
        builder.append(generateMultiCarrierAndEUL(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       GSM Relations       !!!!").append("\n");
        builder.append(generateGsmRelation(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Coverage Relations       !!!!").append("\n");
        builder.append(generateCoverageRelation(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Delete Reference from all Network     !!!!").append("\n");
        builder.append(generateDeletionOnAllNetwork(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Create External Cells on all Network     !!!!").append("\n");
        builder.append(generateCreationOnAllNetwork_ExternalCells(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Create Relations on all Network     !!!!").append("\n");
        builder.append(generateCreationOnAllNetwork_Relations(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Create External Cells Target RNC     !!!!").append("\n");
        builder.append(generateCreationOnTarget_ExternalCells(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Create External Relations on Target RNC     !!!!").append("\n");
        builder.append(generateCreationOnTarget_Relations_external(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Create Internal Relations on Target RNC     !!!!").append("\n");
        builder.append(generateCreationOnTarget_RelationsAndExternalCells_Internal(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       Power and DirectedRetry     !!!!").append("\n");
        builder.append(generatePowerAndDirectedRetry(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!       LTE     !!!!").append("\n");
        builder.append(generateEutranFreqRelation(siteName,sourceRNC,targetRNC)).append("\n");
        builder.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!#\n\n");
        
        return builder.toString();
    }
    
    public static String generateUra(){
        return "crn RncFunction=1,Ura=65535\n" +
                "uraIdentity 65535\n" +
                "userLabel \n" +
                "end\n\n";
    }
    
    public static String generateLocationArea(String siteName,String sourceRNC){
        Document internallCell = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC))).first();
        
        if(internallCell!=null){
        String lac = internallCell.get("locationAreaRef").toString().replaceAll("LocationArea=", "");
        return "crn RncFunction=1,LocationArea="+lac+"\n" +
                "att 1\n" +
                "lac "+lac+"\n" +
                "t3212 20\n" +
                "userLabel \n" +
                "end\n";
        }
        return "Couldn't find cells for site "+siteName+" on RNC "+sourceRNC;
    }
    
    public static String generateInternalCells(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script="";
        int count = 0;
       
        for(Document utranCell : utranCells) {
            //<editor-fold defaultstate="collapsed" desc="comment">
           script +=    "crn RncFunction=1,UtranCell="+utranCell.get("UtranCellId")+"\n" +
                        "absPrioCellRes cellReselectionPriority="+((Document)utranCell.get("absPrioCellRes")).get("cellReselectionPriority")+
                        ",sPrioritySearch1="+((Document)utranCell.get("absPrioCellRes")).get("sPrioritySearch1")+
                         ",sPrioritySearch2="+((Document)utranCell.get("absPrioCellRes")).get("sPrioritySearch2")+
                        ",threshServingLow="+((Document)utranCell.get("absPrioCellRes")).get("threshServingLow")+
                         ",measIndFach="+((Document)utranCell.get("absPrioCellRes")).get("measIndFach")+"\n" +
                        "accessClassNBarred "+utranCell.get("accessClassNBarred")+"\n" +
                        "accessClassesBarredCs "+utranCell.get("accessClassesBarredCs")+"\n" +
                        "accessClassesBarredPs "+utranCell.get("accessClassesBarredPs")+"\n" +
                        "admBlockRedirection gsmRrc="+((Document)utranCell.get("admBlockRedirection")).get("gsmRrc")+
                        ",rrc="+((Document)utranCell.get("admBlockRedirection")).get("rrc")+
                        ",speech="+((Document)utranCell.get("admBlockRedirection")).get("speech")+"\n" +
                        "administrativeState 0\n" +
                        "agpsEnabled "+utranCell.get("agpsEnabled")+"\n" +
                        "amrNbSelector "+utranCell.get("amrNbSelector")+"\n" +
                        "amrWbRateDlMax "+utranCell.get("amrWbRateDlMax")+"\n" +
                        "amrWbRateUlMax "+utranCell.get("amrWbRateUlMax")+"\n" +
                        "anrIafUtranCellConfig anrEnabled="+((Document)utranCell.get("anrIafUtranCellConfig")).get("anrEnabled")
                       +",relationAddEnabled="+((Document)utranCell.get("anrIafUtranCellConfig")).get("relationAddEnabled")+"\n" +
                        "antennaPosition latitudeSign="+((Document)utranCell.get("antennaPosition")).get("latitudeSign")
                        +",latitude="+((Document)utranCell.get("antennaPosition")).get("latitude")
                        +",longitude="+((Document)utranCell.get("antennaPosition")).get("longitude")+"\n" +
                        "aseDlAdm "+utranCell.get("aseDlAdm")+"\n" +
                        "aseLoadThresholdUlSpeech amr12200="+((Document)utranCell.get("aseLoadThresholdUlSpeech")).get("amr12200")
                      +",amr7950="+((Document)utranCell.get("aseLoadThresholdUlSpeech")).get("amr7950")
                      +",amr5900="+((Document)utranCell.get("aseLoadThresholdUlSpeech")).get("amr5900")+"\n" +
                        "aseUlAdm "+utranCell.get("aseUlAdm")+"\n" +
                        "autoAcbEnabled "+utranCell.get("autoAcbEnabled")+"\n" +
                        "autoAcbMaxPsClassesToBar "+utranCell.get("autoAcbMaxPsClassesToBar")+"\n" +
                        "autoAcbMinRcssrInput "+utranCell.get("autoAcbMinRcssrInput")+"\n" +
                        "autoAcbRcssrThresh "+utranCell.get("autoAcbRcssrThresh")+"\n" +
                        "autoAcbRcssrWeight "+utranCell.get("autoAcbRcssrWeight")+"\n" +
                        "autoAcbRtwpThresh "+utranCell.get("autoAcbRtwpThresh")+"\n" +
                        "autoAcbRtwpWeight "+utranCell.get("autoAcbRtwpWeight")+"\n" +
                        "bchPower "+utranCell.get("bchPower")+"\n" +
                        "cId "+utranCell.get("cId")+"\n" +
                        "cbsSchedulePeriodLength "+utranCell.get("cbsSchedulePeriodLength")+"\n" +
                        "cellBroadcastSac "+utranCell.get("cellBroadcastSac")+"\n" +
                        "cellReserved "+utranCell.get("cellReserved")+"\n" +
                        "cellUpdateConfirmCsInitRepeat "+utranCell.get("cellUpdateConfirmCsInitRepeat")+"\n" +
                        "cellUpdateConfirmPsInitRepeat "+utranCell.get("cellUpdateConfirmPsInitRepeat")+"\n" +
                        "codeLoadThresholdDlSf128 "+utranCell.get("codeLoadThresholdDlSf128")+"\n" +
                        "compModeAdm "+utranCell.get("compModeAdm")+"\n" +
                        "ctchAdmMargin "+utranCell.get("ctchAdmMargin")+"\n" +
                        "ctchOccasionPeriod "+utranCell.get("ctchOccasionPeriod")+"\n" +
                        "cyclicAcb acbEnabled="+((Document)utranCell.get("cyclicAcb")).get("acbEnabled")
                        +",rotationGroupSize="+((Document)utranCell.get("cyclicAcb")).get("rotationGroupSize")+"\n" +
                        "cyclicAcbCs acbEnabled="+((Document)utranCell.get("cyclicAcbCs")).get("acbEnabled")
                        +",rotationGroupSize="+((Document)utranCell.get("cyclicAcbCs")).get("rotationGroupSize")+"\n" +
                        "cyclicAcbPs acbEnabled="+((Document)utranCell.get("cyclicAcbPs")).get("acbEnabled")
                        +",rotationGroupSize="+((Document)utranCell.get("cyclicAcbPs")).get("rotationGroupSize")+"\n" +
                        "dchIflsMarginCode "+utranCell.get("dchIflsMarginCode")+"\n" +
                        "dchIflsMarginPower "+utranCell.get("dchIflsMarginPower")+"\n" +
                        "dchIflsThreshCode "+utranCell.get("dchIflsThreshCode")+"\n" +
                        "dchIflsThreshPower "+utranCell.get("dchIflsThreshPower")+"\n" +
                        "dlCodeAdm "+utranCell.get("dlCodeAdm")+"\n" +
                        "dlCodeOffloadLimit "+utranCell.get("dlCodeOffloadLimit")+"\n" +
                        "dlCodePowerCmEnabled "+utranCell.get("dlCodePowerCmEnabled")+"\n" +
                        "dlPowerOffloadLimit "+utranCell.get("dlPowerOffloadLimit")+"\n" +
                        "dmcrEnabled "+utranCell.get("dmcrEnabled")+"\n" +
                        "dnclEnabled "+utranCell.get("dnclEnabled")+"\n" +
                        "downswitchTimer "+utranCell.get("downswitchTimer")+"\n" +
                        "eulMcServingCellUsersAdmTti2 "+utranCell.get("eulMcServingCellUsersAdmTti2")+"\n" +
                        "eulNonServingCellUsersAdm "+utranCell.get("eulNonServingCellUsersAdm")+"\n" +
                        "eulServingCellUsersAdm "+utranCell.get("eulServingCellUsersAdm")+"\n" +
                        "eulServingCellUsersAdmTti2 "+utranCell.get("eulServingCellUsersAdmTti2")+"\n" +
                        "fachMeasOccaCycLenCoeff "+utranCell.get("fachMeasOccaCycLenCoeff")+"\n" +
                        "ganHoEnabled "+utranCell.get("ganHoEnabled")+"\n" +
                        "hardIfhoCorr "+utranCell.get("hardIfhoCorr")+"\n" +
                        "hcsSib3Config hcsPrio="+((Document)utranCell.get("hcsSib3Config")).get("hcsPrio")
                        +",qHcs="+((Document)utranCell.get("hcsSib3Config")).get("qHcs")
                        +",sSearchHcs="+((Document)utranCell.get("hcsSib3Config")).get("sSearchHcs")+"\n" +
                        "hcsUsage idleMode="+((Document)utranCell.get("hcsUsage")).get("idleMode")
                        +",connectedMode="+((Document)utranCell.get("hcsUsage")).get("connectedMode")+"\n" +
                        "hoType "+utranCell.get("hoType")+"\n" +
                        "hsIflsDownswitchTrigg toFach="+((Document)utranCell.get("hsIflsDownswitchTrigg")).get("toFach")
                        +",toUra="+((Document)utranCell.get("hsIflsDownswitchTrigg")).get("toUra")
                        +",fastDormancy="+((Document)utranCell.get("hsIflsDownswitchTrigg")).get("fastDormancy")+"\n" +
                        "hsIflsHighLoadThresh "+utranCell.get("hsIflsHighLoadThresh")+"\n" +
                        "hsIflsMarginUsers "+utranCell.get("hsIflsMarginUsers")+"\n" +
                        "hsIflsPowerLoadThresh "+utranCell.get("hsIflsPowerLoadThresh")+"\n" +
                        "hsIflsRedirectLoadLimit "+utranCell.get("hsIflsRedirectLoadLimit")+"\n" +
                        "hsIflsSpeechMultiRabTrigg "+utranCell.get("hsIflsSpeechMultiRabTrigg")+"\n" +
                        "hsIflsThreshUsers "+utranCell.get("hsIflsThreshUsers")+"\n" +
                        "hsIflsTrigger fromFach="+((Document)utranCell.get("hsIflsTrigger")).get("fromFach")
                        +",fromUra="+((Document)utranCell.get("hsIflsTrigger")).get("fromUra")+"\n" +
                        ""
                        + "hsdpaUsersAdm "+utranCell.get("hsdpaUsersAdm")+"\n" +
                        "hsdpaUsersOffloadLimit "+utranCell.get("hsdpaUsersOffloadLimit")+"\n" +
                        "hsdschInactivityTimer "+utranCell.get("hsdschInactivityTimer")+"\n" +
                        "hsdschInactivityTimerCpc "+utranCell.get("hsdschInactivityTimerCpc")+"\n" +
                        "iFCong "+utranCell.get("iFCong")+"\n" +
                        "iFHyst "+utranCell.get("iFHyst")+"\n" +
                        "ifIratHoPsIntHsEnabled "+utranCell.get("ifIratHoPsIntHsEnabled")+"\n" +
                        "iflsCpichEcnoThresh "+utranCell.get("iflsCpichEcnoThresh")+"\n" +
                        "iflsMode "+utranCell.get("iflsMode")+"\n" +
                        "iflsRedirectUarfcn "+utranCell.get("iflsRedirectUarfcn")+"\n" +
                        "inactivityTimeMultiPsInteractive "+utranCell.get("inactivityTimeMultiPsInteractive")+"\n" +
                        "inactivityTimer "+utranCell.get("inactivityTimer")+"\n" +
                        "inactivityTimerEnhUeDrx "+utranCell.get("inactivityTimerEnhUeDrx")+"\n" +
                        "inactivityTimerPch "+utranCell.get("inactivityTimerPch")+"\n" +
                        "individualOffset "+utranCell.get("individualOffset")+"\n" +
                        "interFreqFddMeasIndicator "+utranCell.get("interFreqFddMeasIndicator")+"\n" +
                        "interPwrMax "+utranCell.get("interPwrMax")+"\n" +
                        "interRate "+utranCell.get("interRate")+"\n" +
                        "iubLinkRef "+utranCell.get("iubLinkRef")+"\n" +
                        "loadBasedHoSupport "+utranCell.get("loadBasedHoSupport")+"\n" +
                        "loadBasedHoType "+utranCell.get("loadBasedHoType")+"\n" +
                        "loadSharingGsmFraction "+utranCell.get("loadSharingGsmFraction")+"\n" +
                        "loadSharingGsmThreshold "+utranCell.get("loadSharingGsmThreshold")+"\n" +
                        "loadSharingMargin "+utranCell.get("loadSharingMargin")+"\n" +
                        "localCellId "+utranCell.get("localCellId")+"\n" +
                        "locationAreaRef "+utranCell.get("locationAreaRef")+"\n" +
                        "maxPwrMax "+utranCell.get("maxPwrMax")+"\n" +
                        "maxRate "+utranCell.get("maxRate")+"\n" +
                        "maxTxPowerUl "+utranCell.get("maxTxPowerUl")+"\n" +
                        "maximumTransmissionPower "+utranCell.get("maximumTransmissionPower")+"\n" +
                        "minPwrMax "+utranCell.get("minPwrMax")+"\n" +
                        "minPwrRl "+utranCell.get("minPwrRl")+"\n" +
                        "minimumRate "+utranCell.get("minimumRate")+"\n" +
                        "mocnCellProfileRef "+utranCell.get("mocnCellProfileRef")+"\n" +
                        "nOutSyncInd "+utranCell.get("nOutSyncInd")+"\n" +
                        "pagingPermAccessCtrl locRegAcb="+((Document)utranCell.get("pagingPermAccessCtrl")).get("locRegAcb")
                        +" ,locRegRestr="+((Document)utranCell.get("pagingPermAccessCtrl")).get("locRegRestr")
                        +",pagingRespRestr="+((Document)utranCell.get("pagingPermAccessCtrl")).get("pagingRespRestr")+"\n" +
                        "pathlossThreshold "+utranCell.get("pathlossThreshold")+"\n" +
                        "primaryCpichPower "+utranCell.get("primaryCpichPower")+"\n" +
                        "primarySchPower "+utranCell.get("primarySchPower")+"\n" +
                        "primaryScramblingCode "+utranCell.get("primaryScramblingCode")+"\n" +
                        "primaryTpsCell "+utranCell.get("primaryTpsCell")+"\n" +
                        "pwrAdm "+utranCell.get("pwrAdm")+"\n" +
                        "pwrHyst "+utranCell.get("pwrHyst")+"\n" +
                        "pwrLoadThresholdDlSpeech amr12200="+((Document)utranCell.get("pwrLoadThresholdDlSpeech")).get("amr12200")
                        +",amr7950="+((Document)utranCell.get("pwrLoadThresholdDlSpeech")).get("amr7950")
                        +",amr5900="+((Document)utranCell.get("pwrLoadThresholdDlSpeech")).get("amr5900")+"\n" +
                        "pwrOffset "+utranCell.get("pwrOffset")+"\n" +
                        "qHyst1 "+utranCell.get("qHyst1")+"\n" +
                        "qHyst2 "+utranCell.get("qHyst2")+"\n" +
                        "qQualMin "+utranCell.get("qQualMin")+"\n" +
                        "qRxLevMin "+utranCell.get("qRxLevMin")+"\n" +
                        "qualMeasQuantity "+utranCell.get("qualMeasQuantity")+"\n" +
                        "rachOverloadProtect "+utranCell.get("rachOverloadProtect")+"\n" +
                        "rateSelectionPsInteractive channelType="+((Document)utranCell.get("rateSelectionPsInteractive")).get("channelType")
                        +",ulPrefRate="+((Document)utranCell.get("rateSelectionPsInteractive")).get("ulPrefRate")
                        +",dlPrefRate="+((Document)utranCell.get("rateSelectionPsInteractive")).get("dlPrefRate")+"\n" +
                        "redirectUarfcn "+utranCell.get("redirectUarfcn")+"\n" +
                        "releaseAseDl "+utranCell.get("releaseAseDl")+"\n" +
                        "releaseAseDlNg "+utranCell.get("releaseAseDlNg")+"\n" +
                        "releaseRedirect "+utranCell.get("releaseRedirect")+"\n" +
                        "releaseRedirectEutraTriggers csFallbackCsRelease="+
                        ((Document)utranCell.get("releaseRedirectEutraTriggers")).get("csFallbackCsRelease")
                        +",csFallbackDchToFach="+((Document)utranCell.get("releaseRedirectEutraTriggers")).get("csFallbackDchToFach")+
                        ",dchToFach="+((Document)utranCell.get("releaseRedirectEutraTriggers")).get("dchToFach")
                        +",fachToUra="+((Document)utranCell.get("releaseRedirectEutraTriggers")).get("fachToUra")
                        +",fastDormancy="+((Document)utranCell.get("releaseRedirectEutraTriggers")).get("fastDormancy")+
                        ",normalRelease="+((Document)utranCell.get("releaseRedirectEutraTriggers")).get("normalRelease")+"\n" +
                        "releaseRedirectHsIfls "+utranCell.get("releaseRedirectHsIfls")+"\n" +
                        "reportingRange1a "+utranCell.get("reportingRange1a")+"\n" +
                        "reportingRange1b "+utranCell.get("reportingRange1b")+"\n" +
                        "rlFailureT "+utranCell.get("rlFailureT")+"\n" +
                        "routingAreaRef "+utranCell.get("routingAreaRef")+"\n" +
                        "rrcLcEnabled "+utranCell.get("rrcLcEnabled")+"\n" +
                        "rwrEutraCc "+utranCell.get("rwrEutraCc")+"\n" +
                        "sHcsRat "+utranCell.get("sHcsRat")+"\n" +
                        "sInterSearch "+utranCell.get("sInterSearch")+"\n" +
                        "sIntraSearch "+utranCell.get("sIntraSearch")+"\n" +
                        "sRatSearch "+utranCell.get("sRatSearch")+"\n" +
                        "secondaryCpichPower "+utranCell.get("secondaryCpichPower")+"\n" +
                        "secondarySchPower "+utranCell.get("secondarySchPower")+"\n" +
                        "servDiffRrcAdmHighPrioProfile "+utranCell.get("servDiffRrcAdmHighPrioProfile")+"\n" +
                        "serviceAreaRef "+utranCell.get("serviceAreaRef")+"\n" +
                        "serviceRestrictions csVideoCalls="+((Document)utranCell.get("serviceRestrictions")).get("csVideoCalls")+"\n" +
                        "sf128Adm "+utranCell.get("sf128Adm")+"\n" +
                        "sf16Adm "+utranCell.get("sf16Adm")+"\n" +
                        "sf16AdmUl "+utranCell.get("sf16AdmUl")+"\n" +
                        "sf16gAdm "+utranCell.get("sf16gAdm")+"\n" +
                        "sf32Adm "+utranCell.get("sf32Adm")+"\n" +
                        "sf4AdmUl "+utranCell.get("sf4AdmUl")+"\n" +
                        "sf64AdmUl "+utranCell.get("sf64AdmUl")+"\n" +
                        "sf8Adm "+utranCell.get("sf8Adm")+"\n" +
                        "sf8AdmUl "+utranCell.get("sf8AdmUl")+"\n" +
                        "sf8gAdmUl "+utranCell.get("sf8gAdmUl")+"\n" +
                        "sib1PlmnScopeValueTag "+utranCell.get("sib1PlmnScopeValueTag")+"\n" +
                        "spare "+utranCell.get("spare")+"\n" +
                        "spareA "+utranCell.get("spareA")+"\n" +
                        "srbAdmExempt "+utranCell.get("srbAdmExempt")+"\n" +
                        "standAloneSrbSelector "+utranCell.get("standAloneSrbSelector")+"\n" +
                        "tCell "+utranCell.get("tCell")+"\n" +
                        "timeToTrigger1a "+utranCell.get("timeToTrigger1a")+"\n" +
                        "timeToTrigger1b "+utranCell.get("timeToTrigger1b")+"\n" +
                        "tmCongAction "+utranCell.get("tmCongAction")+"\n" +
                        "tmCongActionNg "+utranCell.get("tmCongActionNg")+"\n" +
                        "tmInitialG "+utranCell.get("tmInitialG")+"\n" +
                        "tpsCellThresholds tpsCellThreshEnabled="+((Document)utranCell.get("tpsCellThresholds")).get("tpsCellThreshEnabled")
                        +",tpsLockThreshold="+((Document)utranCell.get("tpsCellThresholds")).get("tpsLockThreshold")+
                            ",tpsUnlockThreshold="+((Document)utranCell.get("tpsCellThresholds")).get("tpsUnlockThreshold")+"\n" +
                        "transmissionScheme "+utranCell.get("transmissionScheme")+"\n" +
                        "treSelection "+utranCell.get("treSelection")+"\n" +
                        "uarfcnDl "+utranCell.get("uarfcnDl")+"\n" +
                        "uarfcnUl "+utranCell.get("uarfcnUl")+"\n" +
                        "updateLocator "+utranCell.get("updateLocator")+"\n" +
                        "uraRef RncFunction=1,Ura=65535 \n"+//((Document)utranCell.get("uraRef")).get("uraRef")+"\n" +
                        "usedFreqThresh2dEcno "+utranCell.get("usedFreqThresh2dEcno")+"\n" +
                        "usedFreqThresh2dRscp "+utranCell.get("usedFreqThresh2dRscp")+"\n" +
                        "userLabel "+utranCell.get("userLabel")+"\n" +
                        "utranCellPosition \n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "";
                    //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateIubLink(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> iubLinks = MongoDB.getIubLinkCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document iubLink : iubLinks){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += "crn RncFunction=1,IubLink="+iubLink.get("IubLinkId")+"\n" +
                    "administrativeState 0\n" +
                    "atmUserPlaneTermSubrackRef Subrack=MS\n" +
                    "controlPlaneTransportOption atm="+((Document)iubLink.get("controlPlaneTransportOption")).get("atm")
                   +",ipv4="+((Document)iubLink.get("controlPlaneTransportOption")).get("ipv4")+"\n" +
                    "dlHwAdm "+iubLink.get("dlHwAdm")+"\n" +
                    "l2EstReqRetryTimeNbapC "+iubLink.get("l2EstReqRetryTimeNbapC")+"\n" +
                    "l2EstReqRetryTimeNbapD "+iubLink.get("l2EstReqRetryTimeNbapD")+"\n" +
                    "linkType "+iubLink.get("linkType")+"\n" +
                    "poolRedundancy "+iubLink.get("poolRedundancy")+"\n" +
                    "rbsId "+iubLink.get("rbsId")+"\n" +
                    "remoteCpIpAddress1 "+iubLink.get("remoteCpIpAddress1")+"\n" +
                    "remoteCpIpAddress2 "+iubLink.get("remoteCpIpAddress2")+"\n" +
                    "remoteSctpPortNbapC "+iubLink.get("remoteSctpPortNbapC")+"\n" +
                    "remoteSctpPortNbapD "+iubLink.get("remoteSctpPortNbapD")+"\n" +
                    "rncModuleAllocWeight "+iubLink.get("rncModuleAllocWeight")+"\n" +
                    "rncModulePreferredRef "+iubLink.get("rncModulePreferredRef")+"\n" +
                    "softCongThreshGbrBwDl "+iubLink.get("softCongThreshGbrBwDl")+"\n" +
                    "softCongThreshGbrBwUl "+iubLink.get("softCongThreshGbrBwUl")+"\n" +
                    "spare "+iubLink.get("spare")+"\n" +
                    "spareA "+iubLink.get("spareA")+"\n" +
                    "ulHwAdm "+iubLink.get("ulHwAdm")+"\n" +
                    "userLabel "+iubLink.get("userLabel")+"\n" +
                    "userPlaneGbrAdmBandwidthDl "+iubLink.get("userPlaneGbrAdmBandwidthDl")+"\n" +
                    "userPlaneGbrAdmBandwidthUl "+iubLink.get("userPlaneGbrAdmBandwidthUl")+"\n" +
                    "userPlaneGbrAdmEnabled "+iubLink.get("userPlaneGbrAdmEnabled")+"\n" +
                    "userPlaneGbrAdmMarginDl "+iubLink.get("userPlaneGbrAdmMarginDl")+"\n" +
                    "userPlaneGbrAdmMarginUl "+iubLink.get("userPlaneGbrAdmMarginUl")+"\n" +
                    "userPlaneIpResourceRef "+iubLink.get("userPlaneIpResourceRef")+"\n" +
                    "userPlaneTransportOption atm="+((Document)iubLink.get("userPlaneTransportOption")).get("atm")+
                    ",ipv4="+((Document)iubLink.get("userPlaneTransportOption")).get("ipv4")+"\n" +
                    "end\n" +
                        "\n" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateSac(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> sacs = MongoDB.getSacCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document sac : sacs){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += "crn RncFunction=1,"+sac.getString("_id").replaceAll(sourceRNC+"_", "")+"\n" +
                        "sac "+sac.getString("sac")+"\n" +
                        "userLabel "+sac.getString("userLabel")+"\n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateExternalGsmCells(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> externalGsmCells = MongoDB.getExternalGsmCellCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document externalGsmCell : externalGsmCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script +=   "crn RncFunction=1,ExternalGsmNetwork=1,ExternalGsmCell="+externalGsmCell.getString("_id").replaceAll(sourceRNC+"_", "")+"\n" +
                        "bandIndicator "+externalGsmCell.getString("bandIndicator")+"\n" +
                        "bcc "+externalGsmCell.getString("bcc")+"\n" +
                        "bcchFrequency "+externalGsmCell.getString("bcchFrequency")+"\n" +
                        "cellIdentity "+externalGsmCell.getString("cellIdentity")+"\n" +
                        "individualOffset "+externalGsmCell.getString("individualOffset")+"\n" +
                        "lac "+externalGsmCell.getString("lac")+"\n" +
                        "maxTxPowerUl "+externalGsmCell.getString("maxTxPowerUl")+"\n" +
                        "ncc "+externalGsmCell.getString("ncc")+"\n" +
                        "qRxLevMin "+externalGsmCell.getString("qRxLevMin")+"\n" +
                        "userLabel "+externalGsmCell.getString("userLabel")+"\n" +
                        "end" +
                        "\n" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateFachRachPch(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document uranCell : utranCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
            // PCH
            script +=   "crn RncFunction=1,UtranCell="+((Document)uranCell.get("pch")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",Pch="+((Document)uranCell.get("pch")).get("PchId")+"\n" +
                        "administrativeState 0\n" +
                        "pchPower "+((Document)uranCell.get("pch")).get("pchPower")+"\n" +
                        "pichPower "+((Document)uranCell.get("pch")).get("pichPower")+"\n" +
                        "sccpchOffset "+((Document)uranCell.get("pch")).get("sccpchOffset")+"\n" +
                        "userLabel Pch "+((Document)uranCell.get("pch")).get("PchId")+"\n" +
                        "end\n" +
                        "";
            // Rach
            script +=   "crn RncFunction=1,UtranCell="+((Document)uranCell.get("rach")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",Rach="+((Document)uranCell.get("rach")).get("RachId")+"\n" +
                        "administrativeState 0\n" +
                        "aichPower "+((Document)uranCell.get("rach")).get("aichPower")+"\n" +
                        "aichTransmissionTiming "+((Document)uranCell.get("rach")).get("aichTransmissionTiming")+"\n" +
                        "constantValueCprach "+((Document)uranCell.get("rach")).get("constantValueCprach")+"\n" +
                        "increasedRachCoverageEnabled "+((Document)uranCell.get("rach")).get("increasedRachCoverageEnabled")+"\n" +
                        "maxPreambleCycle "+((Document)uranCell.get("rach")).get("maxPreambleCycle")+"\n" +
                        "nb01Max "+((Document)uranCell.get("rach")).get("nb01Max")+"\n" +
                        "nb01Min "+((Document)uranCell.get("rach")).get("nb01Min")+"\n" +
                        "powerOffsetP0 "+((Document)uranCell.get("rach")).get("powerOffsetP0")+"\n" +
                        "powerOffsetPpm "+((Document)uranCell.get("rach")).get("powerOffsetPpm")+"\n" +
                        "preambleRetransMax "+((Document)uranCell.get("rach")).get("preambleRetransMax")+"\n" +
                        "preambleSignatures "+((Document)uranCell.get("rach")).get("preambleSignatures")+"\n" +
                        "scramblingCodeWordNo "+((Document)uranCell.get("rach")).get("scramblingCodeWordNo")+"\n" +
                        "spreadingFactor "+((Document)uranCell.get("rach")).get("spreadingFactor")+"\n" +
                        "subChannelNo "+((Document)uranCell.get("rach")).get("subChannelNo")+"\n" +
                        "userLabel Rach "+((Document)uranCell.get("rach")).get("RachId")+"\n" +
                        "end\n" +
                        "";
            // Fach
            script +=   "crn RncFunction=1,UtranCell="+((Document)uranCell.get("fach")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",Fach="+((Document)uranCell.get("fach")).get("FachId")+"\n" +
                        "administrativeState 0\n" +
                        "maxFach1Power "+((Document)uranCell.get("fach")).get("maxFach1Power")+"\n" +
                        "maxFach2Power "+((Document)uranCell.get("fach")).get("maxFach2Power")+"\n" +
                        "pOffset1Fach "+((Document)uranCell.get("fach")).get("pOffset1Fach")+"\n" +
                        "pOffset3Fach "+((Document)uranCell.get("fach")).get("pOffset3Fach")+"\n" +
                        "sccpchOffset "+((Document)uranCell.get("fach")).get("sccpchOffset")+"\n" +
                        "userLabel Fach "+((Document)uranCell.get("fach")).get("FachId")+"\n" +
                        "end" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateHsdsch(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document uranCell : utranCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script +=   "crn RncFunction=1,UtranCell="+((Document)uranCell.get("Hsdsch")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",Hsdsch="+((Document)uranCell.get("Hsdsch")).get("HsdschId")+"\n" +
                        "administrativeState 0\n" +
                        "codeThresholdPdu656 "+((Document)uranCell.get("Hsdsch")).get("codeThresholdPdu656")+"\n" +
                        "cqiFeedbackCycle "+((Document)uranCell.get("Hsdsch")).get("cqiFeedbackCycle")+"\n" +
                        "deltaAck1 "+((Document)uranCell.get("Hsdsch")).get("deltaAck1")+"\n" +
                        "deltaAck2 "+((Document)uranCell.get("Hsdsch")).get("deltaAck2")+"\n" +
                        "deltaCqi1 "+((Document)uranCell.get("Hsdsch")).get("deltaCqi1")+"\n" +
                        "deltaCqi2 "+((Document)uranCell.get("Hsdsch")).get("deltaCqi2")+"\n" +
                        "deltaNack1 "+((Document)uranCell.get("Hsdsch")).get("deltaNack1")+"\n" +
                        "deltaNack2 "+((Document)uranCell.get("Hsdsch")).get("deltaNack2")+"\n" +
                        "hsMeasurementPowerOffset "+((Document)uranCell.get("Hsdsch")).get("hsMeasurementPowerOffset")+"\n" +
                        "initialAckNackRepetitionFactor "+((Document)uranCell.get("Hsdsch")).get("initialAckNackRepetitionFactor")+"\n" +
                        "initialCqiRepetitionFactor "+((Document)uranCell.get("Hsdsch")).get("initialCqiRepetitionFactor")+"\n" +
                        "numHsPdschCodes "+((Document)uranCell.get("Hsdsch")).get("numHsPdschCodes")+"\n" +
                        "numHsScchCodes "+((Document)uranCell.get("Hsdsch")).get("numHsScchCodes")+"\n" +
                        "userLabel hsdsch "+((Document)uranCell.get("Hsdsch")).get("HsdschId")+"\n" +
                        "end" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateMultiCarrierAndEUL(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document uranCell : utranCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += 
                    "ld RncFunction=1,UtranCell="+((Document)uranCell.get("multiCarrier")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",Hsdsch=1,Eul=1,MultiCarrier=1" +
                    "\n" +
                    "crn RncFunction=1,UtranCell="+((Document)uranCell.get("eul")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",Hsdsch=1,Eul=1 " +
                    "administrativeState 0 " +
                    "eulDchBalancingEnabled "+((Document)uranCell.get("eul")).get("eulDchBalancingEnabled")+" \n" +
                    "eulDchBalancingLoad "+((Document)uranCell.get("eul")).get("eulDchBalancingLoad")+" \n" +
                    "eulDchBalancingOverload "+((Document)uranCell.get("eul")).get("eulDchBalancingOverload")+" \n" +
                    "eulDchBalancingReportPeriod "+((Document)uranCell.get("eul")).get("eulDchBalancingReportPeriod")+" \n" +
                    "eulDchBalancingSuspendDownSw "+((Document)uranCell.get("eul")).get("eulDchBalancingSuspendDownSw")+" \n" +
                    "eulDchBalancingTimerNg "+((Document)uranCell.get("eul")).get("eulDchBalancingTimerNg")+" \n" +
                    "eulLoadTriggeredSoftCong "+((Document)uranCell.get("eul")).get("eulLoadTriggeredSoftCong")+" \n" +
                    "eulMaxTargetRtwp "+((Document)uranCell.get("eul")).get("eulMaxTargetRtwp")+" \n" +
                    "numEagchCodes "+((Document)uranCell.get("eul")).get("numEagchCodes")+" \n" +
                    "numEhichErgchCodes "+((Document)uranCell.get("eul")).get("numEhichErgchCodes")+" \n" +
                    "pathlossThresholdEulTti2 "+((Document)uranCell.get("eul")).get("pathlossThresholdEulTti2")+" \n" +
                    "releaseAseUlNg "+((Document)uranCell.get("eul")).get("releaseAseUlNg")+" \n" +
                    "threshEulTti2Ecno "+((Document)uranCell.get("eul")).get("threshEulTti2Ecno")+" \n" +
                    "userLabel "+((Document)uranCell.get("eul")).get("userLabel")+"\n" +
                    "end";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateNodeSynchAndEdch(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> iubLinks = MongoDB.getIubLinkCollection().find(
                and(Filters.regex("reservedBy", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        String iubLinkId="";
        String nodeSynch="";
        String iubEdch="";
        int count = 0;
        for(Document iubLink : iubLinks){
            //<editor-fold defaultstate="collapsed" desc="comment">
            iubLinkId = ((Document)iubLink.get("nodeSynch")).get("iubLinkId").toString().replaceAll(sourceRNC+"_", "");
            nodeSynch = ((Document)iubLink.get("nodeSynch")).get("NodeSynchId")+"";
            script += 
                    "ld RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+" \n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ fixedWindowSizeInit "+((Document)iubLink.get("nodeSynch")).get("fixedWindowSizeInit")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ fixedWindowSizeSup "+((Document)iubLink.get("nodeSynch")).get("fixedWindowSizeSup")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ maxAllowedIubRtt "+((Document)iubLink.get("nodeSynch")).get("maxAllowedIubRtt")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ phaseDiffThreshold "+((Document)iubLink.get("nodeSynch")).get("phaseDiffThreshold")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ sampleIntervalInit "+((Document)iubLink.get("nodeSynch")).get("sampleIntervalInit")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ sampleIntervalSup "+((Document)iubLink.get("nodeSynch")).get("sampleIntervalSup")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ slidingWindowSize "+((Document)iubLink.get("nodeSynch")).get("slidingWindowSize")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ transportDelayMeasDiscRatio "+((Document)iubLink.get("nodeSynch")).get("transportDelayMeasDiscRatio")+"\n" +
                    "lset RncFunction=1,IubLink="+iubLinkId+",NodeSynch="+nodeSynch+"$ userLabel "+((Document)iubLink.get("nodeSynch")).get("userLabel")+"\n\n";
            iubLinkId = ((Document)iubLink.get("iubEdch")).get("iubLinkId").toString().replaceAll(sourceRNC+"_", "");
            iubEdch = ((Document)iubLink.get("iubEdch")).get("IubEdchId")+"";
            script +=   "crn RncFunction=1,IubLink="+iubLinkId+",IubEdch="+iubEdch+"\n" +
                        "edchDataFrameDelayThreshold "+((Document)iubLink.get("iubEdch")).get("edchDataFrameDelayThreshold")+"\n" +
                        "userLabel "+((Document)iubLink.get("iubEdch")).get("userLabel")+"\n" +
                        "end";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generatePowerAndDirectedRetry(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document uranCell : utranCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += "lset RncFunction=1,UtranCell="+((Document)uranCell.get("UtranCellId"))+"$ pwrAdm "+((Document)uranCell.get("pwrAdm"))+"";
            script += "lset RncFunction=1,UtranCell="+((Document)uranCell.get("UtranCellId"))+"$ interRate "+((Document)uranCell.get("interRate"))+"";
            script += "lset RncFunction=1,UtranCell="+((Document)uranCell.get("UtranCellId"))+"$ maxPwrMax "+((Document)uranCell.get("maxPwrMax"))+"\n\n";
            script += "lset UtranCell="+((Document)uranCell.get("UtranCellId"))+" directedRetryTarget"+((Document)uranCell.get("directedRetryTarget"))+"";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateEutranFreqRelation(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranCells = MongoDB.getUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document uranCell : utranCells){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script +=   "crn RncFunction=1,UtranCell="+((Document)uranCell.get("EutranFreqRelation")).get("utranCellId").toString().replaceAll(sourceRNC+"_", "")+",EutranFreqRelation="+((Document)uranCell.get("EutranFreqRelation")).get("EutranFreqRelation")+"\n" +
                        "barredCnOperatorRef "+((Document)uranCell.get("EutranFreqRelation")).get("barredCnOperatorRef")+"\n" +
                        "blacklistedCell "+((Document)uranCell.get("EutranFreqRelation")).get("blacklistedCell")+"\n" +
                        "cellReselectionPriority "+((Document)uranCell.get("EutranFreqRelation")).get("cellReselectionPriority")+"\n" +
                        "eutranFrequencyRef EutraNetwork="+((Document)((Document)uranCell.get("EutranFreqRelation")).get("eutranFrequencyRef")).get("EutraNetwork")+
                                ",EutranFrequency="+((Document)((Document)uranCell.get("EutranFreqRelation")).get("eutranFrequencyRef")).get("EutranFrequency")+"\n" +
                        "qQualMin "+((Document)uranCell.get("EutranFreqRelation")).get("qQualMin")+"\n" +
                        "qRxLevMin "+((Document)uranCell.get("EutranFreqRelation")).get("qRxLevMin")+"\n" +
                        "redirectionOrder "+((Document)uranCell.get("EutranFreqRelation")).get("redirectionOrder")+"\n" +
                        "thresh2dRwr "+((Document)uranCell.get("EutranFreqRelation")).get("thresh2dRwr")+"\n" +
                        "threshHigh "+((Document)uranCell.get("EutranFreqRelation")).get("threshHigh")+"\n" +
                        "threshHigh2 "+((Document)uranCell.get("EutranFreqRelation")).get("threshHigh2")+"\n" +
                        "threshLow "+((Document)uranCell.get("EutranFreqRelation")).get("threshLow")+"\n" +
                        "threshLow2 "+((Document)uranCell.get("EutranFreqRelation")).get("threshLow2")+"\n" +
                        "userLabel "+((Document)uranCell.get("EutranFreqRelation")).get("userLabel")+"\n" +
                        "end";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateGsmRelation(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> gsmRelations = MongoDB.getGsmRelationCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document gsmRelation : gsmRelations){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script +=   "crn RncFunction=1,"+gsmRelation.get("_id").toString().replaceAll(sourceRNC+"_", "")+"\n" +
                        "externalGsmCellRef "+gsmRelation.get("externalGsmCellRef")+"\n" +
                        "mobilityRelationType "+gsmRelation.get("mobilityRelationType")+"\n" +
                        "qOffset1sn "+gsmRelation.get("qOffset1sn")+"\n" +
                        "selectionPriority "+gsmRelation.get("selectionPriority")+"\n" +
                        "end" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateCoverageRelation(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> coverageRelations = MongoDB.getCoverageRelationCollection().find(
                and(Filters.regex("_id", ".*UtranCell=.*"+siteName+".*"),Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document coverageRelation : coverageRelations){
            //<editor-fold defaultstate="collapsed" desc="comment">
            script +=   "crn RncFunction=1,"+coverageRelation.getString("_id").replaceAll(sourceRNC+"_", "")+"\n" +
                        "coverageIndicator "+coverageRelation.getString("coverageIndicator")+"\n" +
                        "hsIflsDownswitch "+coverageRelation.getString("hsIflsDownswitch")+"\n" +
                        "hsPathlossThreshold "+coverageRelation.getString("hsPathlossThreshold")+"\n" +
                        "relationCapability dchLoadSharing="+((Document)coverageRelation.get("relationCapability")).get("dchLoadSharing")+
                        ",hsCellSelection="+((Document)coverageRelation.get("relationCapability")).get("hsCellSelection")+
                        ",hsLoadSharing="+((Document)coverageRelation.get("relationCapability")).get("hsLoadSharing")
                        +",powerSave="+((Document)coverageRelation.get("relationCapability")).get("powerSave")+"\n" +
                        "utranCellRef "+coverageRelation.getString("utranCellRef")+"\n" +
                        "end" +
                        "\n" +
                        "";
            //</editor-fold>
            count++;
        }
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    // Relation Methods Network
    public static String generateDeletionOnAllNetwork(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> externalUtranCells = MongoDB.getExternalUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),
                   Filters.not(Filters.eq("RNC", sourceRNC))));
        String script = "";
        int count = 0;
        for(Document externalUtranCell : externalUtranCells){
            count++;
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += "## Execute on RNC: "+externalUtranCell.get("RNC")+"\n";
            script += "lrdel "+externalUtranCell.get("_id").toString().replaceAll(externalUtranCell.get("RNC")+"_", "")+"\n";
            //</editor-fold>
        }
        
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateCreationOnAllNetwork_ExternalCells(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> externalUtranCells = MongoDB.getExternalUtranCellCollection().find(
                and(Filters.regex("_id", ".*"+siteName+".*"),
                   Filters.not(Filters.eq("RNC", sourceRNC))));
        String script = "";
        int count = 0;
        for(Document externalUtranCell : externalUtranCells){
            count++;
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += "## Execute on RNC: "+externalUtranCell.get("RNC")+"\n";
            script +=   "crn RncFunction=1,IurLink="+targetRNC+"_IurLink,ExternalUtranCell="+externalUtranCell.get("ExternalUtranCellId")+"\n" +
                        "agpsEnabled "+externalUtranCell.get("agpsEnabled")+"\n" +
                        "cId "+externalUtranCell.get("cId")+"\n" +
                        "cellCapability hsdschSupport="+((Document)externalUtranCell.get("cellCapability")).get("hsdschSupport")+
                        ",edchSupport="+((Document)externalUtranCell.get("cellCapability")).get("edchSupport")+
                        ",edchTti2Support="+((Document)externalUtranCell.get("cellCapability")).get("edchTti2Support")+
                        ",enhancedL2Support="+((Document)externalUtranCell.get("cellCapability")).get("enhancedL2Support")+
                        ",fdpchSupport="+((Document)externalUtranCell.get("cellCapability")).get("fdpchSupport")+
                        ",cpcSupport="+((Document)externalUtranCell.get("cellCapability")).get("cpcSupport")+
                        ",qam64MimoSupport="+((Document)externalUtranCell.get("cellCapability")).get("qam64MimoSupport")+"\n" +
                        "hsAqmCongCtrlSpiSupport "+externalUtranCell.get("hsAqmCongCtrlSpiSupport")+"\n" +
                        "hsAqmCongCtrlSupport "+externalUtranCell.get("hsAqmCongCtrlSupport")+"\n" +
                        "individualOffset "+externalUtranCell.get("individualOffset")+"\n" +
                        "lac "+externalUtranCell.get("lac")+"\n" +
                        "maxTxPowerUl "+externalUtranCell.get("maxTxPowerUl")+"\n" +
                        "primaryCpichPower "+externalUtranCell.get("primaryCpichPower")+"\n" +
                        "primaryScramblingCode "+externalUtranCell.get("primaryScramblingCode")+"\n" +
                        "qQualMin "+externalUtranCell.get("qQualMin")+"\n" +
                        "qRxLevMin "+externalUtranCell.get("qRxLevMin")+"\n" +
                        "rac "+externalUtranCell.get("rac")+"\n" +
                        "reportingRange1a "+externalUtranCell.get("reportingRange1a")+"\n" +
                        "reportingRange1b "+externalUtranCell.get("reportingRange1b")+"\n" +
                        "timeToTrigger1a "+externalUtranCell.get("timeToTrigger1a")+"\n" +
                        "timeToTrigger1b "+externalUtranCell.get("timeToTrigger1b")+"\n" +
                        "transmissionScheme "+externalUtranCell.get("transmissionScheme")+"\n" +
                        "uarfcnDl "+externalUtranCell.get("uarfcnDl")+"\n" +
                        "uarfcnUl "+externalUtranCell.get("uarfcnUl")+"\n" +
                        "userLabel "+externalUtranCell.get("userLabel")+"-1\n" +
                        "end\n";
            //</editor-fold>
        }
        
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateCreationOnAllNetwork_Relations(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranRelations = MongoDB.getUtranRelationCollection().find(
                and(Filters.regex("utranCellRef", ".*"+siteName+".*"),
                   Filters.not(Filters.eq("RNC", sourceRNC))));
        String script = "";
        int count = 0;
        for(Document utranRelation : utranRelations){
            count++;
            //<editor-fold defaultstate="collapsed" desc="comment">
            script += "## Execute on RNC: "+utranRelation.get("RNC")+"\n";
            script +=   "crn RncFunction=1,"+utranRelation.get("UtranRelationId")+"\n" +
                        "hcsSib11Config hcsPrio="+((Document)utranRelation.get("hcsSib11Config")).get("hcsPrio")+
                         ",qHcs="+((Document)utranRelation.get("hcsSib11Config")).get("qHcs")+
                        ",penaltyTime="+((Document)utranRelation.get("hcsSib11Config")).get("penaltyTime")+
                        ",temporaryOffset1="+((Document)utranRelation.get("hcsSib11Config")).get("temporaryOffset1")+
                        ",temporaryOffset2="+((Document)utranRelation.get("hcsSib11Config")).get("temporaryOffset2")+"\n" +
                        "loadSharingCandidate  "+utranRelation.get("loadSharingCandidate")+"\n" +
                        "mobilityRelationType  "+utranRelation.get("mobilityRelationType")+"\n" +
                        "qOffset1sn "+utranRelation.get("qOffset1sn")+"\n" +
                        "qOffset2sn "+utranRelation.get("qOffset2sn")+"\n" +
                        "selectionPriority "+utranRelation.get("selectionPriority")+"\n" +
                        "utranCellRef "+utranRelation.get("utranCellRef").toString().replaceAll(sourceRNC, targetRNC)+"\n" +
                        "end\n\n";
            //</editor-fold>
        }
        
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
     // Relations Method on Target
    public static String generateCreationOnTarget_ExternalCells(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> externalUtranCells = MongoDB.getExternalUtranCellCollection().find(
                and(Filters.regex("reservedBy", ".*UtranCell="+siteName+".*"),
                   Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        for(Document externalUtranCell : externalUtranCells){
            if(!externalUtranCell.get("_id").toString().contains(targetRNC)){
            //<editor-fold defaultstate="collapsed" desc="comment">
            count++;
            script +=   "crn RncFunction=1,"+externalUtranCell.get("_id").toString().replaceAll(externalUtranCell.get("RNC")+"_", "")+"\n" +
                        "agpsEnabled "+externalUtranCell.get("agpsEnabled")+"\n" +
                        "cId "+externalUtranCell.get("cId")+"\n" +
                        "cellCapability hsdschSupport="+((Document)externalUtranCell.get("cellCapability")).get("hsdschSupport")+
                        ",edchSupport="+((Document)externalUtranCell.get("cellCapability")).get("edchSupport")+
                        ",edchTti2Support="+((Document)externalUtranCell.get("cellCapability")).get("edchTti2Support")+
                        ",enhancedL2Support="+((Document)externalUtranCell.get("cellCapability")).get("enhancedL2Support")+
                        ",fdpchSupport="+((Document)externalUtranCell.get("cellCapability")).get("fdpchSupport")+
                        ",cpcSupport="+((Document)externalUtranCell.get("cellCapability")).get("cpcSupport")+
                        ",qam64MimoSupport="+((Document)externalUtranCell.get("cellCapability")).get("qam64MimoSupport")+"\n" +
                        "hsAqmCongCtrlSpiSupport "+externalUtranCell.get("hsAqmCongCtrlSpiSupport")+"\n" +
                        "hsAqmCongCtrlSupport "+externalUtranCell.get("hsAqmCongCtrlSupport")+"\n" +
                        "individualOffset "+externalUtranCell.get("individualOffset")+"\n" +
                        "lac "+externalUtranCell.get("lac")+"\n" +
                        "maxTxPowerUl "+externalUtranCell.get("maxTxPowerUl")+"\n" +
                        "primaryCpichPower "+externalUtranCell.get("primaryCpichPower")+"\n" +
                        "primaryScramblingCode "+externalUtranCell.get("primaryScramblingCode")+"\n" +
                        "qQualMin "+externalUtranCell.get("qQualMin")+"\n" +
                        "qRxLevMin "+externalUtranCell.get("qRxLevMin")+"\n" +
                        "rac "+externalUtranCell.get("rac")+"\n" +
                        "reportingRange1a "+externalUtranCell.get("reportingRange1a")+"\n" +
                        "reportingRange1b "+externalUtranCell.get("reportingRange1b")+"\n" +
                        "timeToTrigger1a "+externalUtranCell.get("timeToTrigger1a")+"\n" +
                        "timeToTrigger1b "+externalUtranCell.get("timeToTrigger1b")+"\n" +
                        "transmissionScheme "+externalUtranCell.get("transmissionScheme")+"\n" +
                        "uarfcnDl "+externalUtranCell.get("uarfcnDl")+"\n" +
                        "uarfcnUl "+externalUtranCell.get("uarfcnUl")+"\n" +
                        "userLabel "+externalUtranCell.get("userLabel")+"-1\n" +
                        "end\n\n";
            //</editor-fold>
            }
        
        }
        
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateCreationOnTarget_Relations_external(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranRelations = MongoDB.getUtranRelationCollection().find(
                and(Filters.regex("_id", ".*UtranCell="+siteName+".*"),
                   Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        script += "## Execute on RNC: "+targetRNC+"\n";
        for(Document utranRelation : utranRelations){
            if(utranRelation.get("utranCellRef").toString().contains("ExternalUtranCell=")){
            //<editor-fold defaultstate="collapsed" desc="comment">
            count++;
            script +=   "crn RncFunction=1,"+utranRelation.get("UtranRelationId")+"\n" +
                        "hcsSib11Config hcsPrio="+((Document)utranRelation.get("hcsSib11Config")).get("hcsPrio")+
                         ",qHcs="+((Document)utranRelation.get("hcsSib11Config")).get("qHcs")+
                        ",penaltyTime="+((Document)utranRelation.get("hcsSib11Config")).get("penaltyTime")+
                        ",temporaryOffset1="+((Document)utranRelation.get("hcsSib11Config")).get("temporaryOffset1")+
                        ",temporaryOffset2="+((Document)utranRelation.get("hcsSib11Config")).get("temporaryOffset2")+"\n" +
                        "loadSharingCandidate  "+utranRelation.get("loadSharingCandidate")+"\n" +
                        "mobilityRelationType  "+utranRelation.get("mobilityRelationType")+"\n" +
                        "qOffset1sn "+utranRelation.get("qOffset1sn")+"\n" +
                        "qOffset2sn "+utranRelation.get("qOffset2sn")+"\n" +
                        "selectionPriority "+utranRelation.get("selectionPriority")+"\n" +
                        "utranCellRef "+utranRelation.get("utranCellRef")+"\n" +
                        "end\n\n";
            //</editor-fold>
            }
        }
        
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    public static String generateCreationOnTarget_RelationsAndExternalCells_Internal(String siteName,String sourceRNC,String targetRNC){
        FindIterable<Document> utranRelations = MongoDB.getUtranRelationCollection().find(
                and(Filters.regex("_id", ".*UtranCell="+siteName+".*"),
                   Filters.eq("RNC", sourceRNC)));
        String script = "";
        int count = 0;
        Document internalCell = null;
        String newRNC = null;
        script += "## Execute on RNC: "+targetRNC+"\n";
        for(Document utranRelation : utranRelations){
            if(!utranRelation.get("utranCellRef").toString().contains("ExternalUtranCell=")){
            // look for the RNC that contain this cell 
            internalCell = 
                    MongoDB.getUtranCellCollection().find(
                            and(Filters.regex("_id", ".*"+utranRelation.getString("utranCellRef").replaceAll("UtranCell=", "")+".*"),
                                Filters.eq("RNC", sourceRNC))).first();
            if(internalCell!=null){
            // look for its new RNC
            newRNC = DR_Plan.getTargetRNC(internalCell.getString("UtranCellId"));
            if(newRNC==null){
                newRNC = sourceRNC;
            }
            if(newRNC!=null){
            // generate external cell script from the internal cell on the new RNC
            count++;
            //<editor-fold defaultstate="collapsed" desc="Transform Internal to External">
            script +=   "crn RncFunction=1,IurLink="+newRNC+"_IurLink,ExternalUtranCell="+internalCell.get("_id").toString().replaceAll(internalCell.get("RNC")+"_", "")+"\n" +
                        "agpsEnabled "+internalCell.get("agpsEnabled")+"\n" +
                        "cId "+internalCell.get("cId")+"\n" +
                        "cellCapability hsdschSupport=0, edchSupport=0 ,edchTti2Support=0 ,enhancedL2Support="+((Document)internalCell.get("Hsdsch")).get("enhancedL2Support")+
                        ",fdpchSupport="+internalCell.get("fdpchSupport")+
                        ",cpcSupport="+internalCell.get("cpcSupport")+
                        ",qam64MimoSupport="+((Document)internalCell.get("Hsdsch")).get("qam64MimoSupport")+"\n" +
                        "hsAqmCongCtrlSpiSupport "+((Document)internalCell.get("Hsdsch")).get("hsAqmCongCtrlSpiSupport")+"\n" +
                        "hsAqmCongCtrlSupport "+((Document)internalCell.get("Hsdsch")).get("hsAqmCongCtrlSupport")+"\n" +
                        "individualOffset "+internalCell.get("individualOffset")+"\n" +
                        "lac "+internalCell.get("locationAreaRef").toString().replaceAll("LocationArea=", "")+"\n" +
                        "maxTxPowerUl "+internalCell.get("maxTxPowerUl")+"\n" +
                        "primaryCpichPower "+internalCell.get("primaryCpichPower")+"\n" +
                        "primaryScramblingCode "+internalCell.get("primaryScramblingCode")+"\n" +
                        "qQualMin "+internalCell.get("qQualMin")+"\n" +
                        "qRxLevMin "+internalCell.get("qRxLevMin")+"\n";
                if(internalCell.get("routingAreaRef")!=null){
                    script +="rac "+internalCell.get("routingAreaRef").toString().split(",")[1].replaceAll("RoutingArea=", "")+"\n";
                }
             script += "reportingRange1a "+internalCell.get("reportingRange1a")+"\n" +
                        "reportingRange1b "+internalCell.get("reportingRange1b")+"\n" +
                        "timeToTrigger1a "+internalCell.get("timeToTrigger1a")+"\n" +
                        "timeToTrigger1b "+internalCell.get("timeToTrigger1b")+"\n" +
                        "transmissionScheme "+internalCell.get("transmissionScheme")+"\n" +
                        "uarfcnDl "+internalCell.get("uarfcnDl")+"\n" +
                        "uarfcnUl "+internalCell.get("uarfcnUl")+"\n" +
                        "userLabel "+internalCell.get("userLabel")+"-1\n" +
                        "end\n\n";
            //</editor-fold>
            // generate relation based on the new IuR
            //<editor-fold defaultstate="collapsed" desc="Relation Creation">
            script +=   "crn RncFunction=1,"+utranRelation.get("UtranRelationId")+"\n" +
                        "hcsSib11Config hcsPrio="+((Document)utranRelation.get("hcsSib11Config")).get("hcsPrio")+
                         ",qHcs="+((Document)utranRelation.get("hcsSib11Config")).get("qHcs")+
                        ",penaltyTime="+((Document)utranRelation.get("hcsSib11Config")).get("penaltyTime")+
                        ",temporaryOffset1="+((Document)utranRelation.get("hcsSib11Config")).get("temporaryOffset1")+
                        ",temporaryOffset2="+((Document)utranRelation.get("hcsSib11Config")).get("temporaryOffset2")+"\n" +
                        "loadSharingCandidate  "+utranRelation.get("loadSharingCandidate")+"\n" +
                        "mobilityRelationType  "+utranRelation.get("mobilityRelationType")+"\n" +
                        "qOffset1sn "+utranRelation.get("qOffset1sn")+"\n" +
                        "qOffset2sn "+utranRelation.get("qOffset2sn")+"\n" +
                        "selectionPriority "+utranRelation.get("selectionPriority")+"\n" +
                        "utranCellRef IurLink="+newRNC+"_IURLINK,External"+utranRelation.get("utranCellRef")+"\n" +
                        "end\n\n";
            //</editor-fold>
            }
            }
            
            }
        }
        
        script = "!!!!      Total MOs: "+count+"      !!!!\n"+script;
        return script;
    }
    
    
    public static void main(String[] args) {
        AppConf.configureApp("C:\\Documents\\DR_3G\\DR3G.conf");
        AppConf.configureDR();
        MongoDB.initializeDB();
        
//        System.out.println(generateScriptForSite("UCAI3092","KRX12","VRX07"));
//        System.out.println(generateCreationOnTarget_RelationsAndExternalCells_Internal("UCAI3092","KRX12","VRX07"));
//        System.out.println(generateCreationOnAllNetwork_ExternalCells("UCAI3092","KRX12","VRX07"));
    }
}
