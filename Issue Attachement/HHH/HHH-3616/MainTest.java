package com;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import com.entity.Car;
import com.entity.Travel;

public class MainTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
	Configuration cfg = new Configuration().configure();
	SessionFactory sessionFactory = cfg.buildSessionFactory();
	Session session = sessionFactory.openSession();
	session.beginTransaction();

	// Find Car
	Criteria critCar = session.createCriteria(Car.class);
	critCar.add(Restrictions.eq("idCar", new Long(1)));
	Car car = (Car) critCar.uniqueResult();

	// unlink travel
	Criteria critTravel = session.createCriteria(Travel.class);
	Criteria crit = critTravel.createAlias("car", "car");
	crit.add(Restrictions.eq("car", car));
	List lst = crit.list();
	for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
	    Travel travel = (Travel) iterator.next();
	    travel.setCar(null);
	    session.saveOrUpdate(travel);
	}

	// Here ... Probleman
	// delete car
	StringBuilder sb = new StringBuilder("DELETE FROM ");
	sb.append(car.getClass().getCanonicalName());
	sb.append(" AS DEL WHERE DEL.id");
	sb.append(car.getClass().getSimpleName());
	sb.append(" = ");
	sb.append(car.getIdCar());

	Query query = session.createQuery(sb.toString());
	int row = query.executeUpdate();

	session.getTransaction().commit();
    }
}
