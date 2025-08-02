import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.engine.query.ParameterParser;

/*
 * http://opensource2.atlassian.com/projects/hibernate/browse/HHH-1423
 * ParameterParser bug - ordinal parameter mismatch
 *
 * Copyright (c) 2006 WEB.DE GmbH, Karlsruhe. All rights reserved.
 *
 */
public class HHH1423Test extends TestCase {

    MyRecognizer recognizer;

    protected void setUp() throws Exception {
        recognizer = new MyRecognizer();
    }

    public class MyRecognizer implements ParameterParser.Recognizer {
        List outParamPositsions = new ArrayList();
        List ordinalParamPositsions = new ArrayList();
        List namedParamPositsions = new ArrayList();
        List ejb3ParamPositsions = new ArrayList();

        public void outParameter(int arg0) {
            outParamPositsions.add(arg0);
        }

        public void ordinalParameter(int arg0) {
            ordinalParamPositsions.add(arg0);
        }

        public void namedParameter(String arg0, int arg1) {
            namedParamPositsions.add(arg1);
        }

        public void ejb3PositionalParameter(String arg0, int arg1) {
            ejb3ParamPositsions.add(arg1);
        }

        public void other(char arg0) {
            // nothing
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("outParamPositions: ");
            sb.append(outParamPositsions.toString());
            sb.append(", ordinalParamPositions: ");
            sb.append(ordinalParamPositsions.toString());
            sb.append(", namedParamPositions: ");
            sb.append(namedParamPositsions.toString());
            sb.append(", ejb3ParamPositions: ");
            sb.append(ejb3ParamPositsions.toString());
            return sb.toString();
        }

    }

    public void testSqlStringWithFakedCall() throws Exception {
        String sqlString = "from domain.Order o where o.status = ? and o.orderRecallDate < ? ";
        ParameterParser.parse(sqlString, recognizer);
        System.out.println(recognizer.toString());
        assertEquals(0, recognizer.outParamPositsions.size());
        assertEquals(2, recognizer.ordinalParamPositsions.size());
    }

    public void testSqlStringWithCall() throws Exception {
        String sqlString = " ? = call myFunction() ";
        ParameterParser.parse(sqlString, recognizer);
        System.out.println(recognizer.toString());
        assertEquals(1, recognizer.outParamPositsions.size());
        assertEquals(1, ( (Integer) recognizer.outParamPositsions.get(0)).intValue());
    }

}
