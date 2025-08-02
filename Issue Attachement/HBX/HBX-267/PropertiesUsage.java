
public class PropertiesUsage {

	public void personName(){
		Person person = new Person();
		person.setName( "name" );
		person.setContact( true );
		person.setCompany( new Company() );
	}
}
