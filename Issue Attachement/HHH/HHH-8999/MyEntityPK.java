package entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Grzegorz
 * @version $Revision: 2014 $ $Date: 2012-08-02 16:31:38 +0200 (czw) $
 */
@Embeddable
public class MyEntityPK implements Serializable {
  
  @Column(name = "CARD_HASH", nullable = false)
  private byte[] keyByte;

  @Column(name = "ENQUEUED", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date keyTimestamp;

  public MyEntityPK() {}

  public byte[] getKeyByte() {
    return keyByte;
  }

  public void setKeyByte(byte[] keyByte) {
    this.keyByte = keyByte;
  }

  public Date getKeyTimestamp() {
    return keyTimestamp;
  }

  public void setKeyTimestamp(Date keyTimestamp) {
    this.keyTimestamp = keyTimestamp;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final MyEntityPK other = (MyEntityPK) obj;
    if ((this.keyByte == null) ? (other.keyByte != null) : !Arrays.equals(this.keyByte, other.keyByte))
      return false;
    if (this.keyTimestamp != other.keyTimestamp && (this.keyTimestamp == null || !this.keyTimestamp.equals(other.keyTimestamp)))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (this.keyByte != null ? Arrays.hashCode(this.keyByte) : 0);
    hash = 17 * hash + (this.keyTimestamp != null ? keyTimestamp.hashCode() : 0);
    return hash;
  }
}
