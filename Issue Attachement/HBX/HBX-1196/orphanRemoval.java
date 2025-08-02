 public String generateCollectionAnnotation(Property property,
	    Configuration cfg) {
	StringBuffer annotation = new StringBuffer();
	Value value = property.getValue();
	if (value != null && value instanceof Collection) {
	    Collection collection = (Collection) value;
	    if (collection.isOneToMany()) {
		String mappedBy = null;
		annotation.append("    @")
			.append(importType("javax.persistence.OneToMany"))
			.append("(cascade={").append(getCascadeType(property))
			.append("}").append(getOrphanRemoval(property))
			.append(", fetch=").append(getFetchType(property));
		if (collection.isInverse()) {
		    annotation.append(", mappedBy=\"");
		    mappedBy = getOneToManyMappedBy(cfg, collection);
		    annotation.append(mappedBy).append("\"");
		}
		annotation.append(")");
		if (mappedBy == null)
		    annotation.append("\n").append(
			    generateJoinColumnsAnnotation(property));
	    } else {
		// TODO do the @OneToMany @JoinTable
		// TODO composite element
		String mappedBy = null;
		annotation.append("    @")
			.append(importType("javax.persistence.ManyToMany"))
			.append("(cascade={").append(getCascadeType(property))
			.append("}").append(getOrphanRemoval(property)).append(", fetch=")
			.append(getFetchType(property));
		if (collection.isInverse()) {
		    annotation.append(", mappedBy=\"");
		    mappedBy = getManyToManyMappedBy(cfg, collection);
		    annotation.append(mappedBy).append("\"");
		}
		annotation.append(")");
		if (mappedBy == null) {
		    annotation.append("\n    @");
		    annotation
			    .append(importType("javax.persistence.JoinTable"))
			    .append("(name=\"");
		    Table table = collection.getCollectionTable();

		    annotation.append(table.getName());
		    annotation.append("\"");
		    if (StringHelper.isNotEmpty(table.getSchema())) {
			annotation.append(", schema=\"")
				.append(table.getSchema()).append("\"");
		    }
		    if (StringHelper.isNotEmpty(table.getCatalog())) {
			annotation.append(", catalog=\"")
				.append(table.getCatalog()).append("\"");
		    }
		    String uniqueConstraint = generateAnnTableUniqueConstraint(table);
		    if (uniqueConstraint.length() > 0) {
			annotation.append(", uniqueConstraints={")
				.append(uniqueConstraint).append("}");
		    }
		    annotation.append(", joinColumns = { ");
		    buildArrayOfJoinColumnAnnotation(collection.getKey()
			    .getColumnIterator(), annotation,
			    property.isInsertable(), property.isUpdateable());
		    annotation.append(" }");
		    annotation.append(", inverseJoinColumns = { ");
		    buildArrayOfJoinColumnAnnotation(collection.getElement()
			    .getColumnIterator(), annotation,
			    property.isInsertable(), property.isUpdateable());
		    annotation.append(" }");
		    annotation.append(")");
		}

	    }
	    String hibernateCascade = getHibernateCascadeTypeAnnotation(property);
	    if (hibernateCascade.length() > 0)
		annotation.append("\n    ").append(hibernateCascade);
	}
	return annotation.toString();
    }

    private Object getOrphanRemoval(Property property) {
	boolean orphanRemovalActivated = property.getCascade().contains(
		"DELETE");
	return orphanRemovalActivated ? ", orphanRemoval=true" : "";
    }