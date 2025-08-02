package com.shutterfly.commercial.model;

import java.io.Serializable;
import java.util.Date;

public class CmPaperType implements Serializable{
	private static final long serialVersionUID = -8950320494321123460L;
	
	private Integer m_ID;
	private String m_paperType;
	private String m_weight;
	private String m_category;
	private String m_description;
	private String m_active;
	private Date m_insertTime;
	
	public CmPaperType(){}
	
	public CmPaperType(String mailerID) {
		super();
	}

	public Integer getID() {
		return m_ID;
	}
	public void setID(Integer iD) {
		m_ID = iD;
	}

	public String getPaperType() {
		return m_paperType;
	}

	public void setPaperType(String paperType) {
		m_paperType = paperType;
	}

	public String getWeight() {
		return m_weight;
	}

	public void setWeight(String weight) {
		m_weight = weight;
	}

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		m_description = description;
	}

	public String getActive() {
		return m_active;
	}

	public void setActive(String active) {
		m_active = active;
	}
	
	public String toString(){
		return new StringBuffer().append(m_paperType).toString();
	}

	public Date getInsertTime() {
		return m_insertTime;
	}

	public void setInsertTime(Date insertTime) {
		m_insertTime = insertTime;
	}

	public String getCategory() {
		return m_category;
	}

	public void setCategory(String category) {
		m_category = category;
	}
	
	public boolean isActive(){
		return getActive().equalsIgnoreCase("y");
	}
}
