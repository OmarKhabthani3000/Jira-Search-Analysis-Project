/*
 * CarBean.java
 *
 * Created on December 22, 2006, 11:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hqb.session;

import hqb.model.Car;
import hqb.model.CarLot;
import hqb.model.Carz;
import hqb.model.Lotz;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author vjenks
 */
@Stateless
public class CarBean implements CarLocal
{
  @PersistenceContext
  private EntityManager em;
  
  /** Creates a new instance of CarBean */
  public CarBean()
  {
  }
  
  public Car getCarById(Integer id)
  {
    return this.em.find(Car.class, id);
  }
  
  public CarLot getLotById(Integer id)
  {
    return this.em.find(CarLot.class, id);
  }
  
  public List<Car> getAllCars()
  {
    return this.em.createQuery("select c from Car c left join fetch c.carLot").getResultList();
  }
  
  public List<Carz> getAllCarz()
  {
  	return this.em.createQuery("select c from Carz c left join fetch c.lot").getResultList();
  }
  
  public List<CarLot> getAllLots()
  {
    return this.em.createQuery("select cl from CarLot cl").getResultList();
  }
  
  public List<Lotz> getAllLotz()
  {
  	return this.em.createQuery("select distinct l from Lotz l left join fetch l.cars").getResultList();
  }
  
  public List<CarLot> getAllLotsAndCars()
  {
    return this.em.createQuery("select distinct cl from CarLot cl left join fetch cl.cars").getResultList();
  }
  
  public void persist(Car car)
  {
    this.em.persist(car);
  }  

  public void persist(CarLot lot)
  {
    this.em.persist(lot);
  }  
}
