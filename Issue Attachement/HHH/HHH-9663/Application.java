package pl.comit.orm;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.comit.orm.dao.Dao;
import pl.comit.orm.model.FileContent;

public final class Application {

	private static final String CFG_FILE = "applicationContext.xml";

	public static void main(String[] args) {
		test(new ClassPathXmlApplicationContext(CFG_FILE).getBean(Dao.class));
	}

	public static void test(Dao dao) {
		dao.assureCreatedTaskAndNote(1, 2);
		dao.removeContent(1);
		FileContent content = dao.find(2);
		if (content != null) {
			System.err.println("Content found: " + content);
		}
	}
}
