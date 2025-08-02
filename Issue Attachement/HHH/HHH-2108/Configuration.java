package org.hibernate.test.configuration;

/**
 * @author Courtney Arnold
 */
public class Configuration {
	private String id;
	Configuration() {}
	public Configuration(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
