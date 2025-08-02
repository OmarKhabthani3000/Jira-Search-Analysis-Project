import java.math.BigDecimal;

public class MyEntity {
    private BigDecimal cost;
    private long id;
    public MyEntity() {}
   public final BigDecimal getCost() {
        return cost;
    }
     public final long getId() {
        return id;
    }
    public final void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    public final void setId(long id) {
        this.id = id;
    }
}