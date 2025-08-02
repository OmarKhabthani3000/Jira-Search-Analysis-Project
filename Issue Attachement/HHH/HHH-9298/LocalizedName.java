package core.model.test;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
public class LocalizedName {

	@Basic
	private String localeTag;

	@Basic
	private String value;

	public LocalizedName(String localeTag, String value) {
		this.localeTag = localeTag;
		this.value = value;
	}

	protected LocalizedName() {		
	}

	public String getLocaleTag() {
		return localeTag;
	}

	public String getValue() {
		return value;
	}
	
}
