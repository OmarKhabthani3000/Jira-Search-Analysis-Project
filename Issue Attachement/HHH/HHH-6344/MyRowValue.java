package intest;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class MyRowValue implements Serializable {
    int a;
    int b;
    public MyRowValue() {
    }
    public MyRowValue(int a, int b) {
        this.a = a;
        this.b = b;
    }
    public int getA() { return a; }
    public void setA(int a) { this.a = a; }
    public int getB() { return b; }
    public void setB(int b) { this.b = b; }
}
