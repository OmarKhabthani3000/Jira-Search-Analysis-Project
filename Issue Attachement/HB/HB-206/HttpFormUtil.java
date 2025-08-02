package net.sf.hibernate.tool;

import org.apache.commons.logging.Log;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Utility used for loading hibernate data objects automatically from either an HttpServletRequest
 * or a simple map of parameters. Right now this utility handles only the following types, although it
 * could be modified easily to handle more:
 *
 * Long, Integer, Character, String
 *
 * For this utility to work you must have constants at the top of your hibernate data objects that the
 * html form uses to name it's fields.  This should be automatic based on the other part of this patch
 * to the BasicRenderer class.
 *
 * User: Matt Hall (matt2k at users.sf.net)
 * Date: Jul 1, 2003
 * Time: 1:17:06 PM
 */
public class HttpFormUtil {

    public static String FIELD_IDENTIFIER = "FIELD_";

    public static void loadObject(HttpServletRequest request, Object objectToLoad) {
        loadObject(request.getParameterMap(), objectToLoad);
    }

    public static void loadObject(Map params, Object objectToLoad) {
        Class classToLoad = objectToLoad.getClass();

        Field[] fields = classToLoad.getFields();
        for (int i=0; i<fields.length; i++) {
            Field thisField = fields[i];
            if (thisField.getName().startsWith(FIELD_IDENTIFIER)) {
                String methodName = "";

                try {
                    // Find the method we'll use to set the value
                    String constantFieldVal = (String) classToLoad.getField(thisField.getName()).get(objectToLoad);

                    // Naming conventions for hibernate getters and setters make me do this stuff to get the
                    // capitilization right.
                    String realFieldName = constantFieldVal.substring(constantFieldVal.indexOf('_')+1);
                    methodName = "set"+constantFieldVal.substring(constantFieldVal.indexOf('_')+1, constantFieldVal.indexOf('_')+2).toUpperCase();
                    if (constantFieldVal.indexOf('_')+2 < constantFieldVal.length()) {
                        methodName += constantFieldVal.substring(constantFieldVal.indexOf('_')+2);
                    }
                    Field privateField = classToLoad.getDeclaredField(realFieldName);

                    Method setter = classToLoad.getDeclaredMethod(methodName, new Class[] {privateField.getType()});

                    Object objValue = params.get(constantFieldVal);
                    String reqValue = null;
                    if (objValue instanceof String[]) {
                        reqValue = ((String[]) objValue)[0];
                    } else {
                        reqValue = (String) objValue;
                    }

                    // Attempt to cast to correct type
                    if (reqValue != null) {
                        Object correctTypeVal = null;
                        if (privateField.getType() == Long.class) {
                            try {
                                correctTypeVal = new Long(reqValue);
                            } catch (NumberFormatException e) {
                                correctTypeVal = null;
                                // do squat
                            }
                        } else if (privateField.getType() == String.class) {
                            correctTypeVal = reqValue;
                        } else if (privateField.getType() == Integer.class) {
                            correctTypeVal = new Integer(reqValue);
                        } else if (privateField.getType() == Character.TYPE) {
                            correctTypeVal = new Character(reqValue.charAt(0));
                        }
                        if (correctTypeVal != null) {
                            setter.invoke(objectToLoad, new Object[] {correctTypeVal});
                        } else {
                        }
                    }
                } catch (NoSuchFieldException e) {

                } catch (NoSuchMethodException e) {

                } catch (Exception e) {

                }

            }
        }
    }
}
