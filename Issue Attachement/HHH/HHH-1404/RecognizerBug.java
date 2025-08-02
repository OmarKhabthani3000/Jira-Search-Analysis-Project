package test;

import org.hibernate.engine.query.ParamLocationRecognizer;
import org.hibernate.engine.query.ParameterParser;

public class RecognizerBug {

	public static void main(String[] args) {
		String sqlString= "from domain.Order o where o.status in (?, ?) and o.paid = ? and o.stockAgreed = ? and o.reminderDate is not null and o.orderRecallDate < ? ";

		ParamLocationRecognizer recognizer = new ParamLocationRecognizer();
		ParameterParser.parse( sqlString, recognizer );

		int count = recognizer.getOrdinalParameterLocationList().size();
		
		if ( count != 5) {
			System.out.println("Bug! There is 5 parameters in query, not " + count);
		} else {
			System.out.println("OK!");
		}
	}

}

