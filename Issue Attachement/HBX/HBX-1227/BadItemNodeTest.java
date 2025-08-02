package org.testing.hibernate;

import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BadItemNodeTest extends BaseJPA {

    private BadItemTree tree;
    private Date now;

    @Before
    public void setup() {
        now = new Date();

        tree = new BadItemTree(now);
        manager.persist(tree);
    }

    @Test
    public void testSimplePersist() {
        Item item = createItem("TEST-ITEM");
        BadItemNode treeItem = BadItemNode.create(tree, item);
        manager.persist(treeItem);
        flushAndClear();

        BadItemNode in = manager.find(BadItemNode.class, treeItem.getId());
        assertNotNull(in);
        assertEquals("TEST-ITEM", in.getName());
        assertNull(in.getParent());

        List<BadItemNode> children = in.getChildren();
        assertTrue(children.isEmpty());
    }

    @Test
    public void testParentHasChild() {
        Item parent = createItem("PARENT-ITEM");
        BadItemNode parentNode = BadItemNode.create(tree, parent);
        manager.persist(parentNode);

        Item child = createItem("CHILD-ITEM");
        BadItemNode childNode = BadItemNode.create(child, parentNode);
        manager.persist(childNode);

        flushAndClear();

        BadItemNode pn = manager.find(BadItemNode.class, parentNode.getId());
        assertNotNull(pn);
        assertEquals("PARENT-ITEM", pn.getName());

        List<BadItemNode> children = pn.getChildren();
        assertEquals(1, children.size());
        BadItemNode cn = children.get(0);
        assertNotNull(cn);
        assertEquals("CHILD-ITEM", cn.getName());
        assertEquals(parent, cn.getParent());
    }

    @Test
    public void testPersistWithChildren() {
        Item parent = createItem("PARENT-ITEM");
        BadItemNode parentNode = BadItemNode.create(tree, parent);
        manager.persist(parentNode);

        Item child1 = createItem("CHILD1-ITEM");
        BadItemNode childNode1 = BadItemNode.create(child1, parentNode);
        manager.persist(childNode1);

        Item child2 = createItem("CHILD2-ITEM");
        BadItemNode childNode2 = BadItemNode.create(child2, parentNode);
        manager.persist(childNode2);

        Item child3 = createItem("CHILD3-ITEM");
        BadItemNode childNode3 = BadItemNode.create(child3, parentNode);
        manager.persist(childNode3);

        flushAndClear();

        BadItemNode in = manager.find(BadItemNode.class, parentNode.getId());
        assertNotNull(in);

        List<BadItemNode> children = in.getChildren();
        assertNotNull(children);
        assertEquals(3, children.size());

        String[] names = {
            children.get(0).getName(),
            children.get(1).getName(),
            children.get(2).getName()
        };
        assertArrayEquals(new String[]{ "CHILD1-ITEM", "CHILD2-ITEM", "CHILD3-ITEM" }, names);
    }

    private Item createItem(String name) {
        Item i = new Item(name);
        manager.persist(i);
        return i;
    }

}
