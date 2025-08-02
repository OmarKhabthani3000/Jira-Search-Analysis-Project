package com.tetsuwantech.hakase.database.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.tetsuwantech.core.database.GenericEntity;
import com.tetsuwantech.core.database.authentication.SecureEntity;
import com.tetsuwantech.core.database.authentication.Subject;
import com.tetsuwantech.hakase.database.SequenceableEntity;
import com.tetsuwantech.hakase.database.config.Config;

@Entity
@Indexed(index = "project")
@Table(name = "project")
public class Project extends GenericEntity implements SecureEntity, SequenceableEntity {

	private static final long serialVersionUID = 1L;
	
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(length = LONG_LENGTH)
	private String title;
	
	private Long sequence;
	
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Lob
	@Type(type="org.hibernate.type.TextType")
	private String description;

	@IndexedEmbedded(includePaths = { "id" })
	@ManyToOne
	@JoinColumn(name = "config_id")
	private Config config;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the sequence
	 */
	@Override
	public Long getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	@Override
	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the config
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}
	
	@Override
	public Subject getSubject() {
		return getConfig().getSubject();
	}	
}

