package com.unicast.campaign.impl;
// Generated Sep 19, 2006 10:24:53 PM by Hibernate Tools 3.1.0 beta3

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.hibernate.CacheMode;
import com.unicast.campaign.interf.*;

// http://www.experts-exchange.com/Programming/Programming_Languages/Java/Q_21743817.html

/*
 String Q_GET_ALL_AD_MGMT_INFO = 
        	"select vw.* from FUSE_RP.AD_MGMT_VW vw, fuse_ui.status_reference sr, fuse_ui.status s, fuse_ui.ads_placement p "+
        	"where 1=1 "+
        	"and vw.placement_id = p.placement_id "+
        	"and p.status_reference_id = sr.status_reference_id "+
        	"and sr.status_id = s.status_id "+
        	TOKEN_WHERE_CLAUSE +
            " order by lower(vw.campaign_name), lower(vw.publisher_name), lower(vw.placement_name) ";

*/


public class AdMgmtInfo  implements IAdMgmtInfo {
      
	// Fields    

 	private IAdMgmtVw adMgmtVw;
	private IStatusReference statusReference;
	private IStatus status;
	private IAdsPlacement adsPlacement;
	
	////////////////
	
    private Integer adId;
    private Long creativeId;
    private String creativeName;
    private Integer placementId;
    private String placementName;
    private short campaignId;
    private String campaignName;
    private Date campaignStartDate;
    private Date campaignEndDate;
    private String campaignPo;
    private short publisherId;
    private String publisherName;
    private short advertiserId;
    private String advertiserName;
    private short agencyId;
    private String agencyName;
    private String billablePartyBusType;
    private String vwptSalesManager;
    private String vwptSalesLocation;
    private BigDecimal vwptCsrId;
    private String vwptCsrName;
    private Long placementCap;
    private Date placementStartDate;
    private Date placementEndDate;
    private String creativeProductGroup;
    private String creativeFormat;
    private String creativeTechnologyType;
    private BigDecimal creativeFileSize;
    private BigDecimal creativeCpm;
    private String billablePartyContactName;
    private String billablePartyAddress;
    private String billablePartyPhone;
    private String billablePartyFax;
    private String billablePartyEmail;
    private Long adPlannedImps;
    private Boolean impressionToCount;
    private BigDecimal creativeFullPlayLength;
    private String billingName;
    private short videoPlayTime;
  
    // Constructors
 	 
 	/** default constructor */
     public AdMgmtInfo() {
    
     }
    
     /** minimal constructor */
     public AdMgmtInfo(IAdMgmtVw adMgmtVw) {
    	 
    	 this.adMgmtVw = adMgmtVw;
    	 
     }
     
     /** full constructor */
     public AdMgmtInfo(IAdMgmtVw adMgmtVw, IStatusReference statusReference, IStatus status, IAdsPlacement adsPlacement) {
         
    	 this.adMgmtVw = adMgmtVw;
    	 this.statusReference = statusReference;
         this.status = status;
         this.adsPlacement = adsPlacement;
        
     }
     
     /** full constructor */
     public AdMgmtInfo(Integer adId, Long creativeId, String creativeName, Integer placementId, String placementName, short campaignId, String campaignName, Date campaignStartDate, Date campaignEndDate, String campaignPo, short publisherId, String publisherName, short advertiserId, String advertiserName, short agencyId, String agencyName, String billablePartyBusType, String vwptSalesManager, String vwptSalesLocation, BigDecimal vwptCsrId, String vwptCsrName, Long placementCap, Date placementStartDate, Date placementEndDate, String creativeProductGroup, String creativeFormat, String creativeTechnologyType, BigDecimal creativeFileSize, BigDecimal creativeCpm, String billablePartyContactName, String billablePartyAddress, String billablePartyPhone, String billablePartyFax, String billablePartyEmail, Long adPlannedImps, Boolean impressionToCount, BigDecimal creativeFullPlayLength, String billingName, short videoPlayTime) {
         
    	 this.adId = adId;
         this.creativeId = creativeId;
         this.creativeName = creativeName;
         this.placementId = placementId;
         this.placementName = placementName;
         this.campaignId = campaignId;
         this.campaignName = campaignName;
         this.campaignStartDate = campaignStartDate;
         this.campaignEndDate = campaignEndDate;
         this.campaignPo = campaignPo;
         this.publisherId = publisherId;
         this.publisherName = publisherName;
         this.advertiserId = advertiserId;
         this.advertiserName = advertiserName;
         this.agencyId = agencyId;
         this.agencyName = agencyName;
         this.billablePartyBusType = billablePartyBusType;
         this.vwptSalesManager = vwptSalesManager;
         this.vwptSalesLocation = vwptSalesLocation;
         this.vwptCsrId = vwptCsrId;
         this.vwptCsrName = vwptCsrName;
         this.placementCap = placementCap;
         this.placementStartDate = placementStartDate;
         this.placementEndDate = placementEndDate;
         this.creativeProductGroup = creativeProductGroup;
         this.creativeFormat = creativeFormat;
         this.creativeTechnologyType = creativeTechnologyType;
         this.creativeFileSize = creativeFileSize;
         this.creativeCpm = creativeCpm;
         this.billablePartyContactName = billablePartyContactName;
         this.billablePartyAddress = billablePartyAddress;
         this.billablePartyPhone = billablePartyPhone;
         this.billablePartyFax = billablePartyFax;
         this.billablePartyEmail = billablePartyEmail;
         this.adPlannedImps = adPlannedImps;
         this.impressionToCount = impressionToCount;
         this.creativeFullPlayLength = creativeFullPlayLength;
         this.billingName = billingName;
         this.videoPlayTime = videoPlayTime;
     }

     public List<IAdMgmtInfo> executeQuery(EntityManager entityManager, String where, String order) {
     	
    	 String queryString = buildHQLQueryString(where, order);
         
         Query query = entityManager.createQuery(queryString);
        
         query.setHint("org.hibernate.cacheable", new Boolean(false));
         query.setHint("org.hibernate.cacheMode", CacheMode.IGNORE);
         query.setHint("org.hibernate.fetchSize", new Integer(100));
           
         
         List<IAdMgmtInfo> adMgmtInfoList = (List<IAdMgmtInfo>) query.getResultList();
         
         return adMgmtInfoList;
         
     }
     
     private String buildHQLQueryString(String where, String order) {
     	
         StringBuffer queryString = new StringBuffer();
   
       //  queryString.append("select new com.unicast.campaign.impl.AdMgmtInfo(new com.unicast.campaign.impl.AdMgmtVw(admgmtvw), new com.unicast.campaign.impl.StatusReference(statusreference), new com.unicast.campaign.impl.Status(status), new com.unicast.campaign.impl.AdsPlacement(adsplacement)) ");
         
       // TO INVESTIGATE FILE DEFECT REPORT W/ HIBERNATE  
         
         queryString.append("select new com.unicast.campaign.impl.AdMgmtInfo(admgmtvw) ");
         queryString.append("from com.unicast.campaign.impl.AdMgmtVw as admgmtvw");
         
      /*   
         queryString.append("select new com.unicast.campaign.impl.AdMgmtInfo(admgmtvw.adId," + 
					" admgmtvw.creativeId," +
					" admgmtvw.creativeName," +
					" admgmtvw.placementId," +
					" admgmtvw.placementName," + 
					" admgmtvw.campaignId," +
					" admgmtvw.campaignName," +
					" admgmtvw.campaignStartDate, " +
					" admgmtvw.campaignEndDate, " +
					" admgmtvw.campaignPo, " +
					" admgmtvw.publisherId, " +
					" admgmtvw.publisherName, " +
					" admgmtvw.advertiserId,"+
					" admgmtvw.advertiserName, " +
					" admgmtvw.agencyId, " + 
					" admgmtvw.agencyName, " + 
					" admgmtvw.billablePartyBusType," + 
					" admgmtvw.vwptSalesManager," + 
					" admgmtvw.vwptSalesLocation," +
					" admgmtvw.vwptCsrId," +
					" admgmtvw.vwptCsrName," +
					" admgmtvw.placementCap," +
					" admgmtvw.placementStartDate," +
					" admgmtvw.placementEndDate," +
					" admgmtvw.creativeProductGroup," +
					" admgmtvw.creativeFormat," +
					" admgmtvw.creativeTechnologyType," +
					" admgmtvw.creativeFileSize," +
					" admgmtvw.creativeCpm," +
					" admgmtvw.billablePartyContactName," +
					" admgmtvw.billablePartyAddress," +
					" admgmtvw.billablePartyPhone," +
					" admgmtvw.billablePartyFax," +
					" admgmtvw.billablePartyEmail, " +
					" admgmtvw.adPlannedImps," +
					" admgmtvw.impressionToCount," +
					" admgmtvw.creativeFullPlayLength," +
					" admgmtvw.billingName," +
					" admgmtvw.videoPlayTime) ");
		 */
     //    queryString.append("from com.unicast.campaign.impl.AdMgmtVw as admgmtvw, com.unicast.campaign.impl.StatusReference as statusreference, com.unicast.campaign.impl.Status as status, com.unicast.campaign.impl.AdsPlacement as adsplacement ");
     //    queryString.append("where 1=1 ");
     //    queryString.append("and admgmtvw.placementId = adsplacement.placementId ");
     //    queryString.append("and adsplacement.statusReference.statusReferenceId = statusreference.statusReferenceId ");
     //    queryString.append("and statusreference.status.statusId = status.statusId ");        
         
     //    if (where!=null && where != "") {
     //   	 queryString.append(where);
     //    }
         
     //    if (order != null && order != "") {
     //   	 queryString.append(" order by ");
     //   	 queryString.append(order);
     //    }         
         return queryString.toString();
         
     }

	public IAdMgmtVw getAdMgmtVw() {
		return adMgmtVw;
	}

	public void setAdMgmtVw(IAdMgmtVw adMgmtVw) {
		this.adMgmtVw = adMgmtVw;
	}

	public IAdsPlacement getAdsPlacement() {
		return adsPlacement;
	}

	public void setAdsPlacement(IAdsPlacement adsPlacement) {
		this.adsPlacement = adsPlacement;
	}

	public IStatus getStatus() {
		return status;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

	public IStatusReference getStatusReference() {
		return statusReference;
	}

	public void setStatusReference(IStatusReference statusReference) {
		this.statusReference = statusReference;
	}

	public Integer getAdId() {
		return adId==null?new Integer(0):adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public Long getAdPlannedImps() {
		return adPlannedImps==null?new Long(0):adPlannedImps;
	}

	public void setAdPlannedImps(Long adPlannedImps) {
		this.adPlannedImps = adPlannedImps;
	}

	public short getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(short advertiserId) {
		this.advertiserId = advertiserId;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public void setAdvertiserName(String advertiserName) {
		this.advertiserName = advertiserName;
	}

	public short getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(short agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getBillablePartyAddress() {
		return billablePartyAddress;
	}

	public void setBillablePartyAddress(String billablePartyAddress) {
		this.billablePartyAddress = billablePartyAddress;
	}

	public String getBillablePartyBusType() {
		return billablePartyBusType;
	}

	public void setBillablePartyBusType(String billablePartyBusType) {
		this.billablePartyBusType = billablePartyBusType;
	}

	public String getBillablePartyContactName() {
		return billablePartyContactName;
	}

	public void setBillablePartyContactName(String billablePartyContactName) {
		this.billablePartyContactName = billablePartyContactName;
	}

	public String getBillablePartyEmail() {
		return billablePartyEmail;
	}

	public void setBillablePartyEmail(String billablePartyEmail) {
		this.billablePartyEmail = billablePartyEmail;
	}

	public String getBillablePartyFax() {
		return billablePartyFax;
	}

	public void setBillablePartyFax(String billablePartyFax) {
		this.billablePartyFax = billablePartyFax;
	}

	public String getBillablePartyPhone() {
		return billablePartyPhone;
	}

	public void setBillablePartyPhone(String billablePartyPhone) {
		this.billablePartyPhone = billablePartyPhone;
	}

	public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public Date getCampaignEndDate() {
		return campaignEndDate;
	}

	public void setCampaignEndDate(Date campaignEndDate) {
		this.campaignEndDate = campaignEndDate;
	}

	public short getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(short campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getCampaignPo() {
		return campaignPo;
	}

	public void setCampaignPo(String campaignPo) {
		this.campaignPo = campaignPo;
	}

	public Date getCampaignStartDate() {
		return campaignStartDate;
	}

	public void setCampaignStartDate(Date campaignStartDate) {
		this.campaignStartDate = campaignStartDate;
	}

	public BigDecimal getCreativeCpm() {
		return creativeCpm==null?new BigDecimal(0):creativeCpm;
	}

	public void setCreativeCpm(BigDecimal creativeCpm) {
		this.creativeCpm = creativeCpm;
	}

	public BigDecimal getCreativeFileSize() {
		return creativeFileSize==null?new BigDecimal(0):creativeFileSize;
	}

	public void setCreativeFileSize(BigDecimal creativeFileSize) {
		this.creativeFileSize = creativeFileSize;
	}

	public String getCreativeFormat() {
		return creativeFormat;
	}

	public void setCreativeFormat(String creativeFormat) {
		this.creativeFormat = creativeFormat;
	}

	public BigDecimal getCreativeFullPlayLength() {
		return creativeFullPlayLength==null?new BigDecimal(0):creativeFullPlayLength;
	}

	public void setCreativeFullPlayLength(BigDecimal creativeFullPlayLength) {
		this.creativeFullPlayLength = creativeFullPlayLength;
	}

	public Long getCreativeId() {
		return creativeId==null?new Long(0):creativeId;
	}

	public void setCreativeId(Long creativeId) {
		this.creativeId = creativeId;
	}

	public String getCreativeName() {
		return creativeName;
	}

	public void setCreativeName(String creativeName) {
		this.creativeName = creativeName;
	}

	public String getCreativeProductGroup() {
		return creativeProductGroup;
	}

	public void setCreativeProductGroup(String creativeProductGroup) {
		this.creativeProductGroup = creativeProductGroup;
	}

	public String getCreativeTechnologyType() {
		return creativeTechnologyType;
	}

	public void setCreativeTechnologyType(String creativeTechnologyType) {
		this.creativeTechnologyType = creativeTechnologyType;
	}

	public Boolean getImpressionToCount() {
		return impressionToCount==null?new Boolean(false):impressionToCount;
	}

	public void setImpressionToCount(Boolean impressionToCount) {
		this.impressionToCount = impressionToCount;
	}

	public Long getPlacementCap() {
		return placementCap;
	}

	public void setPlacementCap(Long placementCap) {
		this.placementCap = placementCap;
	}

	public Date getPlacementEndDate() {
		return placementEndDate;
	}

	public void setPlacementEndDate(Date placementEndDate) {
		this.placementEndDate = placementEndDate;
	}

	public Integer getPlacementId() {
		return placementId==null?new Integer(0):placementId;
	}

	public void setPlacementId(Integer placementId) {
		this.placementId = placementId;
	}

	public String getPlacementName() {
		return placementName;
	}

	public void setPlacementName(String placementName) {
		this.placementName = placementName;
	}

	public Date getPlacementStartDate() {
		return placementStartDate;
	}

	public void setPlacementStartDate(Date placementStartDate) {
		this.placementStartDate = placementStartDate;
	}

	public short getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(short publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public short getVideoPlayTime() {
		return videoPlayTime;
	}

	public void setVideoPlayTime(short videoPlayTime) {
		this.videoPlayTime = videoPlayTime;
	}

	public BigDecimal getVwptCsrId() {
		return vwptCsrId==null?new BigDecimal(0):vwptCsrId;
	}

	public void setVwptCsrId(BigDecimal vwptCsrId) {
		this.vwptCsrId = vwptCsrId;
	}

	public String getVwptCsrName() {
		return vwptCsrName;
	}

	public void setVwptCsrName(String vwptCsrName) {
		this.vwptCsrName = vwptCsrName;
	}

	public String getVwptSalesLocation() {
		return vwptSalesLocation;
	}

	public void setVwptSalesLocation(String vwptSalesLocation) {
		this.vwptSalesLocation = vwptSalesLocation;
	}

	public String getVwptSalesManager() {
		return vwptSalesManager;
	}

	public void setVwptSalesManager(String vwptSalesManager) {
		this.vwptSalesManager = vwptSalesManager;
	}

	
}
