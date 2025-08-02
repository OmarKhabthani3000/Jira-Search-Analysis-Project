package org.baeldung.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
// @Table(schema = "ADS")
public class ServerHost implements Serializable {

	@Id
	@Column(nullable = false)
	private String serverHostName;

	public ServerHost() {
		super();
	}

	public ServerHost(final String serverHostName) {
		super();
		this.serverHostName = serverHostName;
	}

	public String getServerHostName() {
		return serverHostName.trim();
	}

	public void setServerHostName(final String serverHostName) {
		this.serverHostName = serverHostName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverHostName == null) ? 0 : serverHostName.trim()
						.hashCode());
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
		final ServerHost other = (ServerHost) obj;
		if (serverHostName == null) {
			if (other.serverHostName != null) {
				return false;
			}
		} else if (!serverHostName.trim().equals(other.serverHostName.trim())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ServerHost [serverHostName=")
		.append(serverHostName.trim())
		.append("]");
		return builder.toString();
	}

}
