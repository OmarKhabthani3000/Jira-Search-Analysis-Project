package com.pragya.usc.activation.business;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pragya.usc.activation.Activation;

/**
 * 
 * @author fsuguimoto
 * @hibernate.joined-subclass table="activationsms"
 * @hibernate.joined-subclass-key column="fk_id_activation"
 * @hibernate.cache usage="read-write"
 */
public class ActivationSMS extends Activation {
	
	private Map keywords;

	public ActivationSMS() {
		keywords = new TreeMap();
	}

	public ActivationSMS(Activation act) {
		keywords = new TreeMap();
		this.setAccessType(act.getAccessType());
		this.setAni(act.getAni());
		this.setChildPortal(act.getChildPortal());
		this.setDnis(act.getDnis());
		this.setEnable(act.isEnable());
		this.setEndDate(act.getEndDate());
		this.setId(act.getId());
		this.setPortal(act.getPortal());
		this.setPromptPath(act.getPromptPath());
		this.setServiceId(this.getServiceId());
		this.setStartDate(this.getStartDate());
		this.setTemplateId(this.getTemplateId());
		this.setVoiceMenu(this.getVoiceMenu());
		this.setVxmlPath(this.getVxmlPath());
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @hibernate.map inverse="false" sort="natural" cascade="all-delete-orphan" lazy="false" table="activationsms_keyword" 
	 * @hibernate.map-key type="string" formula="keyword"
 	 * @hibernate.key column="fk_id_activation" 
	 * @hibernate.element type="string" column="keyword"
	 * @hibernate.cache usage="read-write"
	 */
	public Map getKeywords() {
		return keywords;
	}
	public void setKeywords(Map keywords) {
		this.keywords = keywords;
	} 
}
