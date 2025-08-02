
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;

public class Main {

	public static void main(String[] args) {
		System.out.println("PoC of Hibernate bug when doing wrapping");
		System.out.println("Seyma Nur Soydemir 2016");
		BooleanTypeDescriptor x = new BooleanTypeDescriptor();
		System.out.println("Wrap(\"Y\") = " + x.wrap("Y", null)); // Testing with Y (True)
		System.out.println("Wrap(\"N\") = " + x.wrap("N", null)); // Testing with N (False)
		System.out.println("Wrap(\"k\") = " + x.wrap("k", null)); // Testing with random value (returns False)
		System.out.println("Wrap(null) = " + x.wrap(null, null)); // Testing with null (returns null)
		System.out.println("Wrap(\"\") = " + x.wrap("", null)); // Testing with empty string causes exception
		
	}

}
