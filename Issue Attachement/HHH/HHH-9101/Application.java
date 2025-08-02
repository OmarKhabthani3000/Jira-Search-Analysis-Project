package org.baeldung.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
// @Table(schema = "ADS")
public class Application implements Serializable {

	@Id
	@Column(nullable = false)
	private String applicationId;

	@Column(nullable = false)
	private String applicationAjpSecret;

	@ManyToOne(targetEntity = ApplicationType.class, optional = false)
	@JoinColumn(nullable = false, name = "applicationTypeName")
	private ApplicationType applicationType;

	public Application() {
		super();
	}

	public Application(final String applicationId,
			final String applicationAjpSecret,
			final ApplicationType applicationType) {
		super();
		this.applicationId = applicationId;
		this.applicationAjpSecret = applicationAjpSecret;
		this.applicationType = applicationType;
	}

	public String getApplicationId() {
		return applicationId.trim();
	}

	public void setApplicationId(final String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationAjpSecret() {
		return applicationAjpSecret.trim();
	}

	public void setApplicationAjpSecret(final String applicationAjpSecret) {
		this.applicationAjpSecret = applicationAjpSecret;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(final ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((applicationAjpSecret == null) ? 0 : applicationAjpSecret
						.trim()
						.hashCode());
		result = prime * result
				+ ((applicationId == null) ? 0 : applicationId.trim()
						.hashCode());
		result = prime * result
				+ ((applicationType == null) ? 0 : applicationType.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Application other = (Application) obj;
		if (applicationAjpSecret == null) {
			if (other.applicationAjpSecret != null) {
				return false;
			}
		} else if (!applicationAjpSecret.trim().equals(
				other.applicationAjpSecret.trim())) {
			return false;
		}
		if (applicationId == null) {
			if (other.applicationId != null) {
				return false;
			}
		} else if (!applicationId.trim().equals(other.applicationId.trim())) {
			return false;
		}
		if (applicationType == null) {
			if (other.applicationType != null) {
				return false;
			}
		} else if (!applicationType.equals(other.applicationType)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Application [applicationId=")
		.append(applicationId.trim()).append(", applicationAjpSecret=")
		.append(applicationAjpSecret.trim())
		.append(", applicationType=").append(applicationType)
		.append("]");
		return builder.toString();
	}

}
