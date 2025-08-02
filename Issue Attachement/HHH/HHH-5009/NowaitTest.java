/*
 * Copyright 2001-2005 Fizteh-Center Lab., MIPT, Russia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 15.03.2010
 */
package ru.arptek.arpsite.data.jpa.single;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.TransactionManager;

import ru.arptek.arpsite.data.jpa.EntityManagerHolder;
import ru.arptek.arpsite.data.jpa.JPAConstants;
import ru.arptek.arpsite.db.AbstractDerbyTest;
import ru.arptek.common.DateUtils;

public class NowaitTest extends AbstractDerbyTest {
    public void testNowaitLock() throws Exception {
        EntityManagerHolder entityManagerHolder = componentManager
                .lookup(EntityManagerHolder.class);
        entityManagerHolder.updateTable("test", "entities");

        TransactionManager transactionManager = (TransactionManager) componentManager
                .lookup(TransactionManager.class.getName());
        // for debugging
        transactionManager.setTransactionTimeout(DateUtils.SECOND_PER_HOUR);

        transactionManager.begin();
        try {
            EntityManager entityManager = entityManagerHolder
                    .getEntityManager("test");

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(JPAConstants.HINT_LOCK_TIMEOUT, Integer.valueOf(0));
            entityManager.find(Entity.class, Integer.valueOf(1),
                    LockModeType.PESSIMISTIC_WRITE, properties);
        } finally {
            transactionManager.commit();
        }
    }
}
