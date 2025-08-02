/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wissenstein.calendarpersistence;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author olupandin
 */
public class EventTest {

    @Test
    public void fetchedCalendarValueShouldTakeIntoAccountTimeZoneIfPresent() {

        // Let's assume the application creating events runs in Helsinki
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));

        Event event = new Event();
        // event.startDate is clear and has time zone UTC

        Calendar startDate =
                new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        startDate.clear();
        startDate.set(2012, Calendar.AUGUST, 4, 10, 45);
        event.setStartDate(startDate);
        // event.startDate is 2012-08-04 10:45 UTC

        // Let's save the event
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(
                "calendar-persistence-experiments-pu");
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {
            entityManager.persist(event);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }
        // "2012-08-04 10:45" should be kept in database
        // but is "2012-08-14 13:45" instead

        Long eventId = event.getId();
        long savedStartDateInMillis = event.getStartDate().getTimeInMillis();
        // eventStartDateInMillis represents 2012-08-04 10:45 UTC

        // Let's assume the application reading events runs in Madrid
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));

        // Let's fetch the event
        entityManager = emf.createEntityManager();
        tx = entityManager.getTransaction();
        tx.begin();
        try {
            event = entityManager.find(Event.class, eventId);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }

        long fetchedStartDateInMillis = event.getStartDate().getTimeInMillis();
        // fetchedStartDateInMillis should represent "2012-08-04 10:45 UTC"
        // but represents "2012-08-04 13:45 CEST instead"

        assertEquals(savedStartDateInMillis, fetchedStartDateInMillis);
    }
}
