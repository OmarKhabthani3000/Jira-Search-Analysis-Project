package tests;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="PLGE_CTRSD")
public class CountrySubdivisionPdoWithEmbeddedId implements Serializable {

	@Embeddable
	public static class PrimaryKey implements Serializable {

		// COLUMN: CTRSD_CD
		/**
		 * The "ISO Code" value.
		 */
		private String code;

		// COLUMN: CTR_ISO_CD
		/**
		 * The "Country" value.
		 */
		private String countryIsoCode;

		// COLUMN: CTRSD_CD
		/**
		 * Gets the "ISO Code" value.
		 */
// columnDo.getDomain(): {<class:Domain>;id=ALPHANUMERICUPPER$;sqlDataTypeName=VARCHAR;desc=Alphabetic/Numeric - upper ([A9]*);length=null;scale=-1;typeDef=0}
		@Column(name="CTRSD_CD", nullable=false)
		public String getCode() {
			return this.code;
		}

		// COLUMN: CTRSD_CD
		/**
		 * Sets the "ISO Code" value.
		 */
		public void setCode(String code) {
			this.code = code;
		}

		// COLUMN: CTR_ISO_CD
		/**
		 * Gets the "Country" value.
		 */
// columnDo.getDomain(): {<class:Domain>;id=ALPHABETICUPPER$;sqlDataTypeName=VARCHAR;desc=Alphabetic - upper ([A]*);length=null;scale=-1;typeDef=0}
		@Column(name="CTR_ISO_CD", nullable=false)
		public String getCountryIsoCode() {
			return this.countryIsoCode;
		}

		// COLUMN: CTR_ISO_CD
		/**
		 * Sets the "Country" value.
		 */
		public void setCountryIsoCode(String countryIsoCode) {
			this.countryIsoCode = countryIsoCode;
		}

		public int hashCode() {
			return 1; // This is the minimal hashCode implementation we can generate
		}

		public boolean equals(Object o) {
			if (o instanceof PrimaryKey) {
				PrimaryKey other = (PrimaryKey) o;
				return (
					true // This is just because of the generation algorithm
					&&
					(
						(this.getCode() == null && other.getCode() == null) ||
						(this.getCode() != null && other.getCode() != null &&
						 this.getCode().equals(other.getCode()))
					)
					&&
					(
						(this.getCountryIsoCode() == null && other.getCountryIsoCode() == null) ||
						(this.getCountryIsoCode() != null && other.getCountryIsoCode() != null &&
						 this.getCountryIsoCode().equals(other.getCountryIsoCode()))
					)
				);
			}
			return false;
		}

	}
	
	private PrimaryKey pk = null;

	@EmbeddedId
	public PrimaryKey getPk() {
		return this.pk;
	}

	public void setPk(PrimaryKey aPk) {
		this.pk = aPk;
	}

}
