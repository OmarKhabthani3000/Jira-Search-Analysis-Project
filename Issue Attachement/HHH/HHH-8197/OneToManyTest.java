package com.cg.oneToMany;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OneToManyTest {

    private SessionFactory sessionFactory;

    public OneToManyTest() {
    }

    @Before
    public void setUp() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @After
    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void storeAndFindTask() {
        //given
        Session session = sessionFactory.openSession();

        session.beginTransaction();
        Map<String, Object> tsk1 = new HashMap<String, Object>();
        tsk1.put("id", "TASK_A");
        tsk1.put("name", "Task A");
        session.save("_testCatalog_Task", tsk1);
        session.getTransaction().commit();
        session.close();

        //when
        session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("select distinct root from _testCatalog_Task root");
        query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List result = query.list();
        session.getTransaction().commit();
        session.close();

        //then
        assertThat(result.size(), is(equalTo(1)));
        Map<String, Object> persistedTask = (Map<String, Object>) result.get(0);
        //the relation 'toBlo' should not be initialized in this case
        assertThat(Hibernate.isInitialized(persistedTask.get("subtasks")), is(false));
    }

    @Test
    public void storeAndFindTaskWithSubtasks() {
        //given
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Map<String, Object> tsk1 = new HashMap<String, Object>();
        tsk1.put("id", "TASK_A");
        tsk1.put("name", "Task A");
        session.save("_testCatalog_Task", tsk1);

        Map<String, Object> subtaskOne = new HashMap<String, Object>();
        subtaskOne.put("id", "SUB_A");
        subtaskOne.put("ownerId", "TASK_A");
        session.save("_testCatalog_Subtask", subtaskOne);

        Map<String, Object> subtaskTwo = new HashMap<String, Object>();
        subtaskTwo.put("id", "SUB_B");
        subtaskTwo.put("ownerId", "TASK_A");
        session.save("_testCatalog_Subtask", subtaskTwo);

        session.getTransaction().commit();
        session.close();

        //when
        session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("select distinct root from _testCatalog_Task root inner join fetch root.subtasks");
        query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List result = query.list();
        session.getTransaction().commit();
        session.close();

        //then
        assertThat(result.size(), is(equalTo(1)));
        Map<String, Object> persistedTask = (Map<String, Object>) result.get(0);
        //the relation 'toBlo' should not be initialized in this case
        assertThat(Hibernate.isInitialized(persistedTask.get("subtasks")), is(true));
        assertThat(((Set) persistedTask.get("subtasks")).size(), is(equalTo(2)));
    }

    @Test
    public void storeAndFindTaskWithSubtasksAndAssignee() {
        //given
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Map<String, Object> tsk1 = new HashMap<String, Object>();
        tsk1.put("id", "TASK_A");
        tsk1.put("name", "Task A");
        session.save("_testCatalog_Task", tsk1);

        Map<String, Object> assignee = new HashMap<String, Object>();
        assignee.put("id", "ASS_A");
        assignee.put("name", "Assignee A");
        session.save("_testCatalog_Assignee", assignee);

        Map<String, Object> subtaskOne = new HashMap<String, Object>();
        subtaskOne.put("id", "SUB_A");
        subtaskOne.put("ownerId", "TASK_A");
        subtaskOne.put("assigneeId", "ASS_A");
        session.save("_testCatalog_Subtask", subtaskOne);

        Map<String, Object> subtaskTwo = new HashMap<String, Object>();
        subtaskTwo.put("id", "SUB_B");
        subtaskTwo.put("ownerId", "TASK_A");
        session.save("_testCatalog_Subtask", subtaskTwo);

        session.getTransaction().commit();
        session.close();

        //when
        session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("select distinct root from _testCatalog_Task root inner join fetch root.subtasks subs left join fetch subs.assignee");
        query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List result = query.list();
        session.getTransaction().commit();
        session.close();

        //then
        assertThat(result.size(), is(equalTo(1)));
        Map<String, Object> persistedTask = (Map<String, Object>) result.get(0);
        //the relation 'toBlo' should not be initialized in this case

        assertThat(Hibernate.isInitialized(persistedTask.get("subtasks")), is(true));
        Set<Map<String, Object>> subtasks = ((Set) persistedTask.get("subtasks"));
        assertThat(subtasks.size(), is(equalTo(2)));
        boolean traversedAssigneeCheck = false;
        for (Map<String, Object> sub : subtasks) {
            assertThat(Hibernate.isInitialized(sub.get("assignee")), is(true));
            if (sub.get("id").equals("SUB_A")) {
                Map<String, Object> fetchedAss = (Map<String, Object>) sub.get("assignee");
                assertThat(fetchedAss, is(notNullValue()));
                traversedAssigneeCheck = true;
            }

        }
        assertThat(traversedAssigneeCheck, is(true));
    }

    @Test
    public void storeAndFindTaskWithSubtasksAndActivities() {
        //given
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Map<String, Object> tsk1 = new HashMap<String, Object>();
        tsk1.put("id", "TASK_A");
        tsk1.put("name", "Task A");
        session.save("_testCatalog_Task", tsk1);

        Map<String, Object> subtaskOne = new HashMap<String, Object>();
        subtaskOne.put("id", "SUB_A");
        subtaskOne.put("ownerId", "TASK_A");
        session.save("_testCatalog_Subtask", subtaskOne);

        Map<String, Object> subtaskTwo = new HashMap<String, Object>();
        subtaskTwo.put("id", "SUB_B");
        subtaskTwo.put("ownerId", "TASK_A");
        session.save("_testCatalog_Subtask", subtaskTwo);

        Map<String, Object> activity = new HashMap<String, Object>();
        activity.put("id", "ACT_A");
        activity.put("subtaskId", "SUB_A");
        activity.put("label", "Activity A");
        session.save("_testCatalog_Activity", activity);

        Map<String, Object> activityTwo = new HashMap<String, Object>();
        activityTwo.put("id", "ACT_B");
        activityTwo.put("subtaskId", "SUB_A");
        activityTwo.put("label", "Activity B");
        session.save("_testCatalog_Activity", activityTwo);

        session.getTransaction().commit();
        session.close();

        //when
        session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("select distinct root from _testCatalog_Task root inner join fetch root.subtasks subs left join fetch subs.activities");
        query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List result = query.list();
        session.getTransaction().commit();
        session.close();

        //then
        assertThat(result.size(), is(equalTo(1)));
        Map<String, Object> persistedTask = (Map<String, Object>) result.get(0);
        //the relation 'toBlo' should not be initialized in this case

        assertThat(Hibernate.isInitialized(persistedTask.get("subtasks")), is(true));
        Set<Map<String, Object>> subtasks = ((Set) persistedTask.get("subtasks"));
        assertThat(subtasks.size(), is(equalTo(2)));
        boolean traversedActivitiesCheck = false;
        for (Map<String, Object> sub : subtasks) {
            assertThat(Hibernate.isInitialized(sub.get("activities")), is(true));
            if (sub.get("id").equals("SUB_A")) {
                Set<Map<String, Object>> activities = (Set<Map<String, Object>>) sub.get("activities");
                assertThat(activities.size(), is(equalTo(2)));
                traversedActivitiesCheck = true;
            }

        }
        assertThat(traversedActivitiesCheck, is(true));
    }


}
