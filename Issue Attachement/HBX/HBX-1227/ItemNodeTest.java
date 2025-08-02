package org.testing.hibernate;

import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ItemNodeTest extends BaseJPA {

    private ItemTree tree;
    private Date now;

    @Before
    public void setup() {
        now = new Date();

        tree = new ItemTree(now);
        manager.persist(tree);
    }

    @Test
    public void testSimplePersist() {
        Item item = createItem("TEST-ITEM");
        ItemNode treeItem = ItemNode.create(tree, item);
        manager.persist(treeItem);
        flushAndClear();

        ItemNode in = manager.find(ItemNode.class, treeItem.getId());
        assertNotNull(in);
        assertEquals("TEST-ITEM", in.getName());
        assertNull(in.getParentId());

        List<ItemNode> children = in.getChildren();
        assertTrue(children.isEmpty());
    }

    @Test
    public void testParentHasChild() {
        Item parent = createItem("PARENT-ITEM");
        ItemNode parentNode = ItemNode.create(tree, parent);
        manager.persist(parentNode);

        Item child = createItem("CHILD-ITEM");
        ItemNode childNode = ItemNode.create(child, parentNode);
        manager.persist(childNode);

        flushAndClear();

        ItemNode pn = manager.find(ItemNode.class, parentNode.getId());
        assertNotNull(pn);
        assertEquals("PARENT-ITEM", pn.getName());

        List<ItemNode> children = pn.getChildren();
        assertEquals(1, children.size());
        ItemNode cn = children.get(0);
        assertNotNull(cn);
        assertEquals("CHILD-ITEM", cn.getName());
        assertEquals(Long.valueOf(parent.getId()), cn.getParentId());
    }

    @Test
    public void testPersistWithChildren() {
        Item parent = createItem("PARENT-ITEM");
        ItemNode parentNode = ItemNode.create(tree, parent);
        manager.persist(parentNode);

        Item child1 = createItem("CHILD1-ITEM");
        ItemNode childNode1 = ItemNode.create(child1, parentNode);
        manager.persist(childNode1);

        Item child2 = createItem("CHILD2-ITEM");
        ItemNode childNode2 = ItemNode.create(child2, parentNode);
        manager.persist(childNode2);

        Item child3 = createItem("CHILD3-ITEM");
        ItemNode childNode3 = ItemNode.create(child3, parentNode);
        manager.persist(childNode3);

        flushAndClear();

        ItemNode in = manager.find(ItemNode.class, parentNode.getId());
        assertNotNull(in);

        List<ItemNode> children = in.getChildren();
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
