package org.hibernate.bugs;

import org.hibernate.internal.util.ReflectHelper;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

/**
 * Test case for https://hibernate.atlassian.net/browse/HHH-11498.
 *
 * @author Russ Tennant (russ@venturetech.net)
 */
public class HHH11498 {

    @Test
    public void hhh11498Test() {
        Method trashed = ReflectHelper.findGetterMethod(ContentElement.class, "trashed");
        assertNotNull(trashed);
    }
}

interface Trashable {
    boolean isTrashed();
    boolean setTrashed(boolean flag);
}

interface PageElement extends Trashable {

}

interface ContentElement extends PageElement {

}