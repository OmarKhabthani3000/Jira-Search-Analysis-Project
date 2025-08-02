/*
 ******************************************************************************
 * (c) Copyright 2002-2007 Workscape, Inc. All rights reserved. Information
 * in this publication is subject to change without notice. No part of this
 * publication may be reproduced in any form without prior written permission
 * of Workscape Inc. Workscape is a registered trademark of Workscape Inc.
 * Other products mentioned in this publication may be registered trademarks,
 * trademarks, or service marks of their respective manufacturers, companies,
 * or organizations.
 ******************************************************************************
 */
package org.hibernate.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.action.EntityInsertAction;
import org.hibernate.type.Type;

/**
 * Sorts the insert actions using more hashes.
 * 
 * @author Workscape, Inc.
 * 
 */
public class NewInsertActionSorter {

	private List insertions;
	// the mapping of entity names to their latest batch numbers.
	private Map latestBatches = new HashMap();
	private Map entityBatchNumber;
	
	// the map of batch numbers to EntityInsertAction lists
	private Map actionBatches = new HashMap();

	public NewInsertActionSorter(List insertions) {
		this.insertions = insertions;
		//optimize the hash size to eliminate a rehash.
		entityBatchNumber = new HashMap(insertions.size()+1, 1.0f);
	}

	/**
	 * Sort the insert actions.
	 */
	public void sort() {
		
		// the list of entity names that indicate the batch number
		for (Iterator actionItr = insertions.iterator(); actionItr.hasNext();) {
			EntityInsertAction action = (EntityInsertAction) actionItr.next();
			// remove the current element from insertions. It will be added back later.
			String entityName = action.getEntityName();

			// the entity associated with the current action.
			Object currentEntity = action.getInstance();

			Integer batchNumber;
			if (latestBatches.containsKey(entityName)) {
				// There is already an existing batch for this type of entity.
				// Check to see if the latest batch is acceptable.
				batchNumber = findBatchNumber(action, entityName);
			} else {
				// add an entry for this type of entity.
				// we can be assured that all referenced entities have already
				// been processed,
				// so specify that this entity is with the latest batch.
				// doing the batch number before adding the name to the list is
				// a faster way to get an accurate number.
				
				batchNumber = new Integer(actionBatches.size());
				latestBatches.put(entityName, batchNumber);
			}
			entityBatchNumber.put(currentEntity, batchNumber);
			addToBatch(batchNumber, action);
		}
		insertions.clear();
		
		// now rebuild the insertions list. There is a batch for each entry in the name list.
		for(int i = 0; i < actionBatches.size(); i++) {
			List batch = (List) actionBatches.get(new Integer(i));
			for(Iterator batchItr = batch.iterator(); batchItr.hasNext();) {
				EntityInsertAction action = (EntityInsertAction) batchItr.next();
				insertions.add(action);
			}
		}
	}

	/**
	 * Finds an acceptable batch for this entity to be a member.
	 */
	private Integer findBatchNumber(EntityInsertAction action,
			String entityName) {
		// loop through all the associated entities and make sure they have been
		// processed before the latest
		// batch associated with this entity type.

		// the current batch number is the latest batch for this entity type.
		Integer latestBatchNumberForType = (Integer) latestBatches.get(entityName);
		
		// loop through all the associations of the current entity and make sure that they are processed
		// before the current batch number
		Object[] propertyValues = action.getState();
		Type[] propertyTypes = action.getPersister().getClassMetadata()
		.getPropertyTypes();
		
		for(int i = 0; i < propertyValues.length; i++) {
			Object value = propertyValues[i];
			Type type = propertyTypes[i];
			if(type.isEntityType() && value != null) {
				// find the batch number associated with the current association, if any.
				Integer associationBatchNumber = (Integer) entityBatchNumber.get(value);
				if(associationBatchNumber != null && associationBatchNumber.compareTo(latestBatchNumberForType) > 0) {
					// create a new batch for this type. The batch number is the number of current batches.
					latestBatchNumberForType = new Integer(actionBatches.size());
					latestBatches.put(entityName, latestBatchNumberForType);
					// since this entity will now be processed in the latest possible batch,
					// we can be assured that it will come after all other associations,
					// there's not need to continue checking.
					break;
				}
			}
		}
		return latestBatchNumberForType;
	}
	
	private void addToBatch(Integer batchNumber, EntityInsertAction action) {
		List actions = (List) actionBatches.get(batchNumber);
		
		if (actions == null) {
			actions = new LinkedList();
			actionBatches.put(batchNumber, actions);
		}
		actions.add(action);
	}

}
