package pl.comit.orm;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.comit.orm.dao.Dao;
import pl.comit.orm.model.Note;

public final class Application {

	private static final String CFG_FILE = "applicationContext.xml";

	public static void main(String[] args) {
		test(new ClassPathXmlApplicationContext(CFG_FILE).getBean(Dao.class));
	}

	public static void test(Dao dao) {
		dao.assureCreatedTaskAndNote(1, 1);
		dao.removeNote(1, 1);
		Note note = dao.find(1);
		if (note != null) {
			System.err.println("Found note: " + note);
		}
	}
}
