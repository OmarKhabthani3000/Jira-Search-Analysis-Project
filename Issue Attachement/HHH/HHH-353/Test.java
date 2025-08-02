import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;

public class Test {

	private String propA;
	private String propB;
	
	public Test(){
	}
	
	public String getPropA() {
		return propA;
	}
	public void setPropA(String propA) {
		this.propA = propA;
	}
	public String getPropB() {
		return propB;
	}
	public void setPropB(String propB) {
		this.propB = propB;
	}
	
	public static List getByCriteria(String propA){
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(Test.class); 
		criteria.add(Expression.eq("propA", propA));    
		return criteria.list(); 
	}
	
	public static List getByHQL(String propA){
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		
		Query query = session.getNamedQuery("Test.getByPropA"); 
		query.setString("propA", propA); 
		return query.list(); 
	}
}
