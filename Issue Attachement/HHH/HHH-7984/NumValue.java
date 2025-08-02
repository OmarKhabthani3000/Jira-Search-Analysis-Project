import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

@Entity
@Table(name="BOT_NUMVALUE")
@NamedNativeQueries({
    @NamedNativeQuery(name="NumValue.getSomeValues",
            query = "{ ? = call BOTEMPLATE.PKG_BOT_TESTS.f_test_ReturnNonEmptyCursor() }",
            resultClass=NumValue.class, hints={@QueryHint(name="org.hibernate.callable", value="true")})
})
public class NumValue {
    @Id
    @Column(name="BOT_NUM", nullable=false)
    private long num;
    @Column(name="BOT_VALUE")
    private String value;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
