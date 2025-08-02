package org.baeldung.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
// @Table(schema = "ADS")
@IdClass(BindingId.class)
public class Binding implements Serializable {

	@Id
	@Column(nullable = false)
	private int bindingCloneIndex;

	@Id
	@ManyToOne(targetEntity = Application.class)
	@JoinColumn(name = "applicationId", nullable = true, insertable = false, updatable = false)
	private Application application;

	@Id
	@ManyToOne(targetEntity = ServerHost.class)
	@JoinColumn(name = "serverHostName", nullable = true, insertable = false, updatable = false)
	private ServerHost serverHost;

	@Column(nullable = true)
	private Timestamp bindingReservedTimestamp;

	@OneToOne(targetEntity = IPAddress.class)
	@JoinColumn(name = "ipAddressAddress", nullable = true, insertable = false, updatable = false)
	private IPAddress ipAddress;

	public Binding() {
		super();
	}

	public Binding(final int bindingCloneIndex, final Application application,
			final ServerHost serverHost,
			final Timestamp bindingReservedTimestamp, final IPAddress ipAddress) {
		super();
		this.bindingCloneIndex = bindingCloneIndex;
		this.application = application;
		this.serverHost = serverHost;
		this.bindingReservedTimestamp = bindingReservedTimestamp;
		this.ipAddress = ipAddress;
	}

	public int getBindingCloneIndex() {
		return bindingCloneIndex;
	}

	public void setBindingCloneIndex(final int bindingCloneIndex) {
		this.bindingCloneIndex = bindingCloneIndex;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(final Application application) {
		this.application = application;
	}

	public ServerHost getServerHost() {
		return serverHost;
	}

	public void setServerHost(final ServerHost serverHost) {
		this.serverHost = serverHost;
	}

	public Timestamp getBindingReservedTimestamp() {
		return bindingReservedTimestamp;
	}

	public void setBindingReservedTimestamp(
			final Timestamp bindingReservedTimestamp) {
		this.bindingReservedTimestamp = bindingReservedTimestamp;
	}

	public IPAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final IPAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((application == null) ? 0 : application.hashCode());
		result = prime * result + bindingCloneIndex;
		result = prime
				* result
				+ ((bindingReservedTimestamp == null) ? 0
						: bindingReservedTimestamp.hashCode());
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result
				+ ((serverHost == null) ? 0 : serverHost.hashCode());
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
		final Binding other = (Binding) obj;
		if (application == null) {
			if (other.application != null) {
				return false;
			}
		} else if (!application.equals(other.application)) {
			return false;
		}
		if (bindingCloneIndex != other.bindingCloneIndex) {
			return false;
		}
		if (bindingReservedTimestamp == null) {
			if (other.bindingReservedTimestamp != null) {
				return false;
			}
		} else if (!bindingReservedTimestamp
				.equals(other.bindingReservedTimestamp)) {
			return false;
		}
		if (ipAddress == null) {
			if (other.ipAddress != null) {
				return false;
			}
		} else if (!ipAddress.equals(other.ipAddress)) {
			return false;
		}
		if (serverHost == null) {
			if (other.serverHost != null) {
				return false;
			}
		} else if (!serverHost.equals(other.serverHost)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Binding [bindingCloneIndex=").append(bindingCloneIndex)
		.append(", application=").append(application)
		.append(", serverHost=").append(serverHost)
		.append(", bindingReservedTimestamp=")
		.append(bindingReservedTimestamp).append(", ipAddress=")
		.append(ipAddress).append("]");
		return builder.toString();
	}

}
