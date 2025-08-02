import junit.framework.TestCase;
import java.util.Map;
import java.util.HashMap;

public class HashMapTest extends TestCase {
    public void testClientUpdate(){
        Obj a = new Obj("p1");
        Map map = new HashMap();
        map.put(a, a);

        Obj a2 = (Obj) map.get(a);
        System.out.println("a2: "+ a2);
        a.setProp1("s12");

        Obj a3 = (Obj)map.get(a);
        System.out.println("a3: "+ a3);

    }
    public class Obj{
        private String prop1;

        public Obj(String prop1) {
            this.prop1 = prop1;
        }

        public String getProp1() {
            return prop1;
        }

        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Obj obj = (Obj) o;

            if (prop1 != null ? !prop1.equals(obj.prop1) : obj.prop1 != null) return false;

            return true;
        }

        public int hashCode() {
            return (prop1 != null ? prop1.hashCode() : 0);
        }

    }

}
