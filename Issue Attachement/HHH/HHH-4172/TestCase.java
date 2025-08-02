import java.util.List;

import org.hibernate.Query;

import br.com.getnet.prepago.framework.connection.Connection;


public class TestCase {
	public static void main(String[] args) throws Exception {
		// IT WORKS
		String hql = "from TblTest";
		Query query = Connection.getSession().createQuery(hql);
		List<TblTest> lst = query.list();
		
		// IT DOES NOT WORK
		// Exception in thread "main" org.hibernate.QueryException: , expected in SELECT
		hql = "select new MyPojo(t.name, sum(t.age) as soma) " +
				" from TblTest t group by t.name";
		query = Connection.getSession().createQuery(hql);
		List<MyPojo> lst2 = query.list();
	}
}

class MyPojo{
	private String name;
	private Long sumAge;
	
	public MyPojo( String name, Long sumAge ){
		this.name = name;
		this.sumAge = sumAge;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSumAge() {
		return sumAge;
	}
	public void setSumAge(Long sumAge) {
		this.sumAge = sumAge;
	}
}
