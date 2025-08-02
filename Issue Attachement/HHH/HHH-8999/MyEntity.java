package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Grzegorz
 * @version $Revision: 2014 $ $Date: 2012-08-02 16:31:38 +0200 (czw) $
 */
@Entity
@Table(name = "EMV_SCRIPT")
public class MyEntity implements Serializable {

  @EmbeddedId
  private MyEntityPK id;
  
  @Column(name = "P2", nullable = false)
  private byte[] value;
  
  public MyEntity() {
    id = new MyEntityPK();
  }

  public MyEntityPK getId() {
    return id;
  }

  public void setId(MyEntityPK id) {
    this.id = id;
  }

  public byte[] getKeyByte() {
    return id.getKeyByte();
  }

  public void setKeyByte(byte[] keyByte) {
    id.setKeyByte(keyByte);
  }

  public Date getKeyTimestamp() {
    return id.getKeyTimestamp();
  }

  public void setKeyTimestamp(Date enqueued) {
    id.setKeyTimestamp(enqueued);
  }

  public byte[] getValue() {
    return value;
  }

  public void setVal(byte[] value) {
    this.value = value;
  }
}
