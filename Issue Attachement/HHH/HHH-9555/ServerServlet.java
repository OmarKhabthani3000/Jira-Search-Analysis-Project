package com.manh.hibernate.issue;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
//import org.hibernate.service.ServiceRegistry;

import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.manh.df.base.impl.ConfigurationScopeElement;
//import org.hibernate.Query;
//import com.manh.df.base.impl.ConfigurationParameterImpl;

@WebServlet("/ServerServlet")
public class ServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ServerServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			Configuration configuration = new Configuration();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.configure().getProperties()).buildServiceRegistry();
			SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			final Session session = sessionFactory.getCurrentSession();
			final PrintWriter writer = response.getWriter();
			Transaction tx = session.beginTransaction();
            session.doWork(new Work() {
				public void execute(Connection connection) throws SQLException {
					Iterator<ConfigurationScopeElement> it = session.getNamedQuery("suite.foundation.base.ConfigurationScope.getByCode").setCacheable(true).setParameter("code", "suite.default").iterate();
					try{
						writer.print("<h2>ConfigurationScopeElement ID = " + it.next().getId() + "</h2></br> </br>");
					}catch(Exception e){
						e.printStackTrace();
					}

					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery("select count(*) from t_config_parm");
					while(rs.next()){
						writer.print("<h2> Count of t_config_parm = " + rs.getString(1) + " </h2>");
					}
					
					rs.close();
					stmt.close();
				}
			});
            tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
