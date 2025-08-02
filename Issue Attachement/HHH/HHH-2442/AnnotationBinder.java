//$Id: AnnotationBinder.java,v 1.169 2006/01/13 01:58:40 epbernard Exp $
package org.hibernate.cfg;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.persistence.DiscriminatorValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.Where;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.cfg.annotations.PropertyBinder;
import org.hibernate.cfg.annotations.QueryBinder;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.Versioning;
import org.hibernate.id.MultipleHiLoPerTableGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdGenerator;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.TypeFactory;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;
import org.hibernate.validator.ClassValidator;

/**
 * JSR 175 annotation binder
 * Will read the annotation from classes, apply the
 * principles of the EJB3 spec and produces the Hibernate
 * configuration-time metamodel (the classes in the <tt>mapping</tt>
 * package)
 *
 * @author Emmanuel Bernard
 */
public final class AnnotationBinder {
	public static final String ANNOTATION_STRING_DEFAULT = "";

	/*
	 * Some design description
	 * I tried to remove any link to annotation except from the 2 first level of
	 * method call.
	 * It'll enable to:
	 *   - facilitate annotation overriding
	 *   - mutualize one day xml and annotation binder (probably a dream though)
	 *   - split this huge class in smaller mapping oriented classes
	 *
	 * bindSomething usually create the mapping container and is accessed by one of the 2 first level method
	 * makeSomething usually create the mapping container and is accessed by bindSomething[else]
	 * fillSomething take the container into parameter and fill it.
	 *
	 *
	 */
	private AnnotationBinder() {
	}

	private static final Log log = LogFactory.getLog( AnnotationBinder.class );

	public static void bindPackage(String packageName, ExtendedMappings mappings) {
		Package pckg = null;
		try {
			pckg = ReflectHelper.classForName( packageName + ".package-info" ).getPackage();
		}
		catch (ClassNotFoundException cnf) {
			log.warn( "Package not found or wo package-info.java: " + packageName );
			return;
		}
		if ( pckg.isAnnotationPresent( SequenceGenerator.class ) ) {
			SequenceGenerator ann = pckg.getAnnotation( SequenceGenerator.class );
			IdGenerator idGen = buildIdGenerator( ann, mappings );
			mappings.addGenerator( idGen );
			log.debug( "Add sequence generator with name: " + idGen.getName() );
		}
		if ( pckg.isAnnotationPresent( TableGenerator.class ) ) {
			TableGenerator ann = pckg.getAnnotation( TableGenerator.class );
			IdGenerator idGen = buildIdGenerator( ann, mappings );
			mappings.addGenerator( idGen );

		}
		if ( pckg.isAnnotationPresent( GenericGenerator.class ) ) {
			GenericGenerator ann = pckg.getAnnotation( GenericGenerator.class );
			IdGenerator idGen = buildIdGenerator( ann, mappings );
			mappings.addGenerator( idGen );
		}
		bindQueries( pckg, mappings );
		bindFilterDefs( pckg, mappings );
		bindTypeDefs( pckg, mappings );
	}

	private static void bindQueries(AnnotatedElement annotatedElement, ExtendedMappings mappings) {
		{
			SqlResultSetMapping ann = annotatedElement.getAnnotation( SqlResultSetMapping.class );
			QueryBinder.bindSqlResultsetMapping( ann, mappings );
		}
		{
			NamedQuery ann = annotatedElement.getAnnotation( NamedQuery.class );
			QueryBinder.bindQuery( ann, mappings );
		}
		{
			org.hibernate.annotations.NamedQuery ann = annotatedElement.getAnnotation(
					org.hibernate.annotations.NamedQuery.class
			);
			QueryBinder.bindQuery( ann, mappings );
		}
		{
			NamedQueries ann = annotatedElement.getAnnotation( NamedQueries.class );
			QueryBinder.bindQueries( ann, mappings );
		}
		{
			org.hibernate.annotations.NamedQueries ann = annotatedElement.getAnnotation(
					org.hibernate.annotations.NamedQueries.class
			);
			QueryBinder.bindQueries( ann, mappings );
		}
		{
			NamedNativeQuery ann = annotatedElement.getAnnotation( NamedNativeQuery.class );
			QueryBinder.bindNativeQuery( ann, mappings );
		}
		{
			org.hibernate.annotations.NamedNativeQuery ann = annotatedElement.getAnnotation(
					org.hibernate.annotations.NamedNativeQuery.class
			);
			QueryBinder.bindNativeQuery( ann, mappings );
		}
		{
			NamedNativeQueries ann = annotatedElement.getAnnotation( NamedNativeQueries.class );
			QueryBinder.bindNativeQueries( ann, mappings );
		}
		{
			org.hibernate.annotations.NamedNativeQueries ann = annotatedElement.getAnnotation(
					org.hibernate.annotations.NamedNativeQueries.class
			);
			QueryBinder.bindNativeQueries( ann, mappings );
		}
	}

	private static IdGenerator buildIdGenerator(java.lang.annotation.Annotation ann, Mappings mappings) {
		IdGenerator idGen = new IdGenerator();
		if ( mappings.getSchemaName() != null ) {
			idGen.addParam( PersistentIdentifierGenerator.SCHEMA, mappings.getSchemaName() );
		}
		if ( mappings.getCatalogName() != null ) {
			idGen.addParam( PersistentIdentifierGenerator.CATALOG, mappings.getCatalogName() );
		}
		if ( ann == null ) {
			idGen = null;
		}
		else if ( ann instanceof TableGenerator ) {
			TableGenerator tabGen = (TableGenerator) ann;
			idGen.setName( tabGen.name() );
			idGen.setIdentifierGeneratorStrategy( MultipleHiLoPerTableGenerator.class.getName() );

			if ( !isDefault( tabGen.table() ) ) {
				idGen.addParam( MultipleHiLoPerTableGenerator.ID_TABLE, tabGen.table() );
			}
			if ( ! isDefault( tabGen.catalog() ) ) {
				idGen.addParam( MultipleHiLoPerTableGenerator.CATALOG, tabGen.catalog() );
			}
			if ( ! isDefault( tabGen.schema() ) ) {
				idGen.addParam( MultipleHiLoPerTableGenerator.SCHEMA, tabGen.schema() );
			}
			//FIXME implements uniqueconstrains

			if ( ! isDefault( tabGen.pkColumnName() ) ) {
				idGen.addParam( MultipleHiLoPerTableGenerator.PK_COLUMN_NAME, tabGen.pkColumnName() );
			}
			if ( ! isDefault( tabGen.valueColumnName() ) ) {
				idGen.addParam( MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME, tabGen.valueColumnName() );
			}
			if ( !isDefault( tabGen.pkColumnValue() ) ) {
				idGen.addParam( MultipleHiLoPerTableGenerator.PK_VALUE_NAME, tabGen.pkColumnValue() );
			}
			idGen.addParam( TableHiLoGenerator.MAX_LO, String.valueOf( tabGen.allocationSize() ) );
			log.debug( "Add table generator with name: " + idGen.getName() );
		}
		else if ( ann instanceof SequenceGenerator ) {
			SequenceGenerator seqGen = (SequenceGenerator) ann;
			idGen.setName( seqGen.name() );
			idGen.setIdentifierGeneratorStrategy( "sequence" );

			if ( ! isDefault( seqGen.sequenceName() ) ) {
				idGen.addParam( org.hibernate.id.SequenceGenerator.SEQUENCE, seqGen.sequenceName() );
			}
			//FIXME: work on initialValue() and allocationSize() through SequenceGenerator.PARAMETERS
			if ( seqGen.initialValue() != 0 || seqGen.allocationSize() != 50 ) {
				log.warn(
						"Hibernate does not support SequenceGenerator.initialValue() nor SequenceGenerator.allocationSize()"
				);
			}
			log.debug( "Add sequence generator with name: " + idGen.getName() );
		}
		else if ( ann instanceof GenericGenerator ) {
			GenericGenerator genGen = (GenericGenerator) ann;
			idGen.setName( genGen.name() );
			idGen.setIdentifierGeneratorStrategy( genGen.strategy() );
			Parameter[] params = genGen.parameters();
			for ( Parameter parameter : params ) {
				idGen.addParam( parameter.name(), parameter.value() );
			}
			log.debug( "Add generic generator with name: " + idGen.getName() );
		}
		else {
			throw new AssertionFailure( "Unknown Generator annotation: " + ann );
		}
		return idGen;
	}

	/**
	 * Bind a class having JSR175 annotations
	 * The subclasses <b>have to</b> be binded after its mother class
	 */
	public static void bindClass(
			Class clazzToProcess, Map<Class, InheritanceState> inheritanceStatePerClass, ExtendedMappings mappings
	) throws MappingException {
		//TODO: be more strict with secondarytable allowance (not for ids, not for secondary table join columns etc)
		InheritanceState inheritanceState = inheritanceStatePerClass.get( clazzToProcess );
		AnnotatedClassType classType = mappings.getClassType( clazzToProcess );
		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) //will be processed by their subentities
				|| AnnotatedClassType.NONE.equals( classType ) //to be ignored
				|| AnnotatedClassType.EMBEDDABLE.equals( classType ) //allow embeddable element declaration
				) {
			return;
		}
		if ( ! classType.equals( AnnotatedClassType.ENTITY ) ) {
			//TODO make this test accurate by removing the none elements artifically added
			throw new AnnotationException(
					"Annotated class should have a @javax.persistence.Entity, @javax.persistence.Embeddable or @javax.persistence.EmbeddedSuperclass annotation: " + clazzToProcess
							.getName()
			);
		}
		AnnotatedElement annotatedClass = clazzToProcess;
		if ( log.isInfoEnabled() ) log.info("Binding entity from annotated class: " + clazzToProcess.getName() );
		InheritanceState superEntityState =
				InheritanceState.getSuperEntityInheritanceState( clazzToProcess, inheritanceStatePerClass);
		PersistentClass superEntity = superEntityState != null ? mappings.getClass( superEntityState.clazz.getName() ) : null;
		if ( superEntity == null ) {
			//check if superclass is not a potential persistent class
			if ( inheritanceState.hasParents ) {
				throw new AssertionFailure(
						"Subclass has to be binded after it's mother class: "
								+ superEntityState.clazz.getName()
				);
			}
		}
		bindQueries( annotatedClass, mappings );
		bindFilterDefs( annotatedClass, mappings );
		bindTypeDefs( annotatedClass, mappings );

		String schema = "";
		String table = ""; //might be no @Table annotation on the annotated class
		String catalog = "";
		String discrimValue = null;
		List<String[]> uniqueConstraints = new ArrayList<String[]>();
		Ejb3DiscriminatorColumn discriminatorColumn = null;
		Ejb3JoinColumn[] inheritanceJoinedColumns = null;

		if ( annotatedClass.isAnnotationPresent( javax.persistence.Table.class ) ) {
			javax.persistence.Table tabAnn = annotatedClass.getAnnotation( javax.persistence.Table.class );
			table = tabAnn.name();
			schema = tabAnn.schema();
			catalog = tabAnn.catalog();
			uniqueConstraints = TableBinder.buildUniqueConstraints( tabAnn.uniqueConstraints() );
		}
		final boolean hasJoinedColumns = inheritanceState.hasParents
				&& InheritanceType.JOINED.equals( inheritanceState.type );
		if ( hasJoinedColumns ) {
			PrimaryKeyJoinColumns jcsAnn = annotatedClass.getAnnotation( PrimaryKeyJoinColumns.class );
			boolean explicitInheritanceJoinedColumns = jcsAnn != null && jcsAnn.value().length != 0;
			if ( explicitInheritanceJoinedColumns ) {
				int nbrOfInhJoinedColumns = jcsAnn.value().length;
				PrimaryKeyJoinColumn jcAnn;
				inheritanceJoinedColumns = new Ejb3JoinColumn[nbrOfInhJoinedColumns];
				for ( int colIndex = 0; colIndex < nbrOfInhJoinedColumns ; colIndex++ ) {
					jcAnn = jcsAnn.value()[colIndex];
					inheritanceJoinedColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
							jcAnn, superEntity.getIdentifier(),
							(Map<String, Join>) null, (PropertyHolder) null, mappings
					);
				}
			}
			else {
				PrimaryKeyJoinColumn jcAnn = annotatedClass.getAnnotation( PrimaryKeyJoinColumn.class );
				inheritanceJoinedColumns = new Ejb3JoinColumn[1];
				inheritanceJoinedColumns[0] = Ejb3JoinColumn.buildJoinColumn(
						jcAnn, superEntity.getIdentifier(),
						(Map<String, Join>) null, (PropertyHolder) null, mappings
				);
			}
			log.debug( "Subclass joined column(s) created" );
		}
		else {
			if ( annotatedClass.isAnnotationPresent( javax.persistence.PrimaryKeyJoinColumns.class )
					|| annotatedClass.isAnnotationPresent( javax.persistence.PrimaryKeyJoinColumn.class ) ) {
				log.warn( "Root entity should not hold an PrimaryKeyJoinColum(s), will be ignored" );
			}
		}

		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.type ) ) {
			javax.persistence.Inheritance inhAnn = annotatedClass.getAnnotation( javax.persistence.Inheritance.class );
			javax.persistence.DiscriminatorColumn discAnn = annotatedClass.getAnnotation(
					javax.persistence.DiscriminatorColumn.class
			);
			DiscriminatorType discriminatorType = discAnn != null ? discAnn.discriminatorType() : DiscriminatorType.STRING;

			org.hibernate.annotations.DiscriminatorFormula discFormulaAnn = annotatedClass.getAnnotation(
					org.hibernate.annotations.DiscriminatorFormula.class
			);
			if ( ! inheritanceState.hasParents ) {
				discriminatorColumn = Ejb3DiscriminatorColumn.buildDiscriminatorColumn(
						discriminatorType, discAnn, discFormulaAnn, mappings
				);
			}
			if ( discAnn != null && inheritanceState.hasParents ) {
				log.warn(
						"Discriminator column has to be defined in the root entity, it will be ignored in subclass: "
								+ clazzToProcess.getName()
				);
			}
			discrimValue = annotatedClass.isAnnotationPresent( DiscriminatorValue.class ) ?
					annotatedClass.getAnnotation( DiscriminatorValue.class ).value() :
					null;
		}

		//we now know what kind of persistent entity it is
		PersistentClass persistentClass;
		//create persistent class
		if ( ! inheritanceState.hasParents ) {
			persistentClass = new RootClass();
		}
		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.type ) ) {
			persistentClass = new Subclass( superEntity );
		}
		else if ( InheritanceType.JOINED.equals( inheritanceState.type ) ) {
			persistentClass = new JoinedSubclass( superEntity );
		}
		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.type ) ) {
			persistentClass = new UnionSubclass( superEntity );
		}
		else {
			throw new AssertionFailure( "Unknown inheritance type: " + inheritanceState.type );
		}
		Proxy proxyAnn = annotatedClass.getAnnotation( Proxy.class );
		BatchSize sizeAnn = annotatedClass.getAnnotation( BatchSize.class );
		Where whereAnn = annotatedClass.getAnnotation( Where.class );
		Entity entityAnn = annotatedClass.getAnnotation( Entity.class );
		org.hibernate.annotations.Entity hibEntityAnn = annotatedClass.getAnnotation(
				org.hibernate.annotations.Entity.class
		);
		org.hibernate.annotations.Cache cacheAnn = annotatedClass.getAnnotation(
				org.hibernate.annotations.Cache.class
		);
		EntityBinder entityBinder = new EntityBinder(
				entityAnn, hibEntityAnn, clazzToProcess, persistentClass, mappings
		);
		entityBinder.setDiscriminatorValue( discrimValue );
		entityBinder.setBatchSize( sizeAnn );
		entityBinder.setProxy( proxyAnn );
		entityBinder.setWhere( whereAnn );
		entityBinder.setCache( cacheAnn );
		entityBinder.setInheritanceState( inheritanceState );
		Filter filterAnn = annotatedClass.getAnnotation( Filter.class );
		if ( filterAnn != null ) {
			entityBinder.addFilter( filterAnn.name(), filterAnn.condition() );
		}
		Filters filtersAnn = annotatedClass.getAnnotation( Filters.class );
		if ( filtersAnn != null ) {
			for ( Filter filter : filtersAnn.value() ) {
				entityBinder.addFilter( filter.name(), filter.condition() );
			}
		}
		entityBinder.bindEntity();

		if ( inheritanceState.hasTable() ) {
			Check checkAnn = annotatedClass.getAnnotation( Check.class );
			String constraints = checkAnn == null ? null : checkAnn.constraints();
			entityBinder.bindTable(
					schema, catalog, table, uniqueConstraints,
					constraints, inheritanceState.hasDenormalizedTable() ? superEntity.getTable() : null
			);
		}
		Map<String, Column[]> columnOverride = PropertyHolderBuilder.buildColumnOverride(
				annotatedClass,
				persistentClass.getClassName()
		);
		PropertyHolder propertyHolder = PropertyHolderBuilder.buildPropertyHolder(
				persistentClass,
				columnOverride,
				entityBinder.getSecondaryTables()
		);

		javax.persistence.SecondaryTable secTabAnn = annotatedClass.getAnnotation(
				javax.persistence.SecondaryTable.class
		);
		javax.persistence.SecondaryTables secTabsAnn = annotatedClass.getAnnotation(
				javax.persistence.SecondaryTables.class
		);
		PrimaryKeyJoinColumn joinColAnn = annotatedClass.getAnnotation( PrimaryKeyJoinColumn.class );
		PrimaryKeyJoinColumns joinColsAnn = annotatedClass.getAnnotation( PrimaryKeyJoinColumns.class );
		entityBinder.firstLevelSecondaryTablesBinding( secTabAnn, secTabsAnn, joinColAnn, joinColsAnn );

		OnDelete onDeleteAnn = annotatedClass.getAnnotation( OnDelete.class );
		boolean onDeleteAppropriate = false;
		if ( InheritanceType.JOINED.equals( inheritanceState.type ) && inheritanceState.hasParents ) {
			onDeleteAppropriate = true;
			JoinedSubclass jsc = (JoinedSubclass) persistentClass;
			if ( persistentClass.getEntityPersisterClass() == null ) {
				persistentClass.getRootClass().setEntityPersisterClass( JoinedSubclassEntityPersister.class );
			}
			SimpleValue key = new DependantValue( jsc.getTable(), jsc.getIdentifier() );
			jsc.setKey( key );
			if ( onDeleteAnn != null ) {
				key.setCascadeDeleteEnabled( OnDeleteAction.CASCADE.equals( onDeleteAnn.action() ) );
			}
			else {
				key.setCascadeDeleteEnabled( false );
			}
			TableBinder.bindFk( jsc.getSuperclass(), jsc, inheritanceJoinedColumns, key, false, mappings );
			jsc.createPrimaryKey();
			jsc.createForeignKey();

		}
		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.type ) ) {
			if ( inheritanceState.hasParents ) {
				if ( persistentClass.getEntityPersisterClass() == null ) {
					persistentClass.getRootClass().setEntityPersisterClass( SingleTableEntityPersister.class );
				}
			}
			else {
				if ( inheritanceState.hasSons || ! discriminatorColumn.isImplicit() ) {
					//need a discriminator column
					bindDiscriminatorToPersistentClass(
							(RootClass) persistentClass,
							discriminatorColumn,
							entityBinder.getSecondaryTables(),
							propertyHolder
					);
				}
			}
		}
		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.type ) ) {
			if ( inheritanceState.hasParents ) {
				if ( persistentClass.getEntityPersisterClass() == null ) {
					persistentClass.getRootClass().setEntityPersisterClass( UnionSubclassEntityPersister.class );
				}
			}
		}
		if ( onDeleteAnn != null && ! onDeleteAppropriate ) {
			log.warn(
					"Inapropriate use of @OnDelete on entity, annotation ignored: " + propertyHolder.getEntityName()
			);
		}

		//try to find class level generators
		HashMap<String, IdGenerator> classGenerators = buildLocalGenerators( annotatedClass, mappings );

		// check properties
		List<PropertyAnnotatedElement> elements =
			getElementsToProcess( clazzToProcess, inheritanceStatePerClass, propertyHolder, entityBinder );
		if ( elements == null ) {
			throw new AnnotationException( "No identifier specified for entity: " + propertyHolder.getEntityName() );
		}
		final boolean subclassAndSingleTableStrategy = inheritanceState.type == InheritanceType.SINGLE_TABLE
				&& inheritanceState.hasParents;
		//process idclass if any
		Set<String> idProperties = new HashSet<String>();
		IdClass idClass = null;
		if (! inheritanceState.hasParents) {
			//look for idClass
			Class current = inheritanceState.clazz;
			InheritanceState state = inheritanceState;
			do {
				current = state.clazz;
				if ( current.isAnnotationPresent( IdClass.class) ) {
					idClass = (IdClass) current.getAnnotation( IdClass.class );
					break;
				}
				state = InheritanceState.getSuperclassInheritanceState( current, inheritanceStatePerClass );
			} while (state != null);
		}
		if ( idClass != null ) {
			Class compositeClass = idClass.value();
			boolean isComponent = true;
			boolean propertyAnnotated = entityBinder.isPropertyAnnotated( compositeClass );
			String propertyAccessor = entityBinder.getPropertyAccessor( compositeClass );
			String generatorType = "assigned";
			String generator = ANNOTATION_STRING_DEFAULT;
			PropertyInferredData inferredData = new PropertyInferredData(
					entityBinder.getPropertyAccessor(), "id", compositeClass
			);
			HashMap<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
			bindId(
					generatorType,
					generator,
					inferredData,
					null,
					propertyHolder,
					localGenerators,
					isComponent,
					columnOverride,
					propertyAnnotated,
					propertyAccessor, entityBinder,
					null,
					true,
					mappings
			);
			inferredData = new PropertyInferredData(
					propertyAccessor, "_identifierMapper", compositeClass
			);
			Component mapper = fillComponent(
					propertyHolder,
					inferredData,
					propertyAnnotated,
					propertyAccessor, false,
					entityBinder,
					true,
					columnOverride,
					mappings, true
			);
			persistentClass.setIdentifierMapper( mapper );
			Property property = new Property();
			property.setName( "_identifierMapper" );
			property.setNodeName( "id" );
			property.setUpdateable( false );
			property.setInsertable( false );
			property.setValue( mapper );
			property.setPropertyAccessorName( "embedded" );
			persistentClass.addProperty( property );
			entityBinder.setIgnoreIdAnnotations( true );

			Iterator properties = mapper.getPropertyIterator();
			while ( properties.hasNext() ) {
				idProperties.add( ( (Property) properties.next() ).getName() );
			}
		}
		Set<String> missingIdProperties = new HashSet<String>( idProperties );
		for ( PropertyAnnotatedElement propertyAnnotatedElement : elements ) {
			String propertyName = propertyAnnotatedElement.inferredData.getPropertyName();
			if ( ! idProperties.contains( propertyName ) ) {
				processElementAnnotations(
						propertyHolder,
						subclassAndSingleTableStrategy ? Nullability.FORCED_NULL : Nullability.NO_CONSTRAINT,
						propertyAnnotatedElement.element,
						propertyAnnotatedElement.inferredData, classGenerators, entityBinder,
						false, false, entityBinder.getPropertyAccessor(), mappings
				);
			}
			else {
				missingIdProperties.remove( propertyName );
			}
		}

		if ( missingIdProperties.size() != 0 ) {
			StringBuilder missings = new StringBuilder();
			for ( String property : missingIdProperties ) {
				missings.append( property ).append( ", " );
			}
			throw new AnnotationException(
					"Unable to find properties ("
							+ missings.substring( 0, missings.length() - 2 )
							+ ") in entity annotated with @IdClass:" + persistentClass.getEntityName()
			);
		}

		if ( ! inheritanceState.hasParents ) {
			( (RootClass) persistentClass ).createPrimaryKey();
		}
		else {
			superEntity.addSubclass( (Subclass) persistentClass );
		}

		mappings.addClass( persistentClass );
		entityBinder.finalSecondaryTableBinding( propertyHolder );

		//add indexes
		entityBinder.addIndexes( annotatedClass.getAnnotation( org.hibernate.annotations.Table.class ) );
		entityBinder.addIndexes( annotatedClass.getAnnotation( org.hibernate.annotations.Tables.class ) );

		//integrate the validate framework
		new ClassValidator( clazzToProcess ).apply( persistentClass );
	}

	/**
	 * Get the annotated elements
	 * Guess the annotated element from @Id or @EmbeddedId presence
	 * Change EntityBinder by side effect
	 */
	private static List<PropertyAnnotatedElement> getElementsToProcess(
			Class clazzToProcess, Map<Class, InheritanceState> inheritanceStatePerClass,
			PropertyHolder propertyHolder, EntityBinder entityBinder
	) {
		InheritanceState inheritanceState = inheritanceStatePerClass.get(clazzToProcess);
		List<Class> classesToProcess = orderClassesToBeProcessed(
				clazzToProcess, inheritanceStatePerClass, inheritanceState
		);
		List<PropertyAnnotatedElement> elements = new ArrayList<PropertyAnnotatedElement>();
		int deep = classesToProcess.size();
		boolean hasIdentifier = false;

		assert ! inheritanceState.isEmbeddableSuperclass;
		Boolean isExplicitPropertyAnnotated = null;
		String explicitAccessType = null;
		if ( inheritanceState.hasParents ) {
			InheritanceState superEntityState =
					InheritanceState.getSuperEntityInheritanceState( clazzToProcess, inheritanceStatePerClass );
			isExplicitPropertyAnnotated = superEntityState != null ? superEntityState.isPropertyAnnotated : null;
			explicitAccessType = superEntityState != null ? superEntityState.accessType : null;
		}
		else {
			AccessType access = (AccessType) clazzToProcess.getAnnotation( AccessType.class );
			explicitAccessType = access != null ? access.value() : null;
			if ( "property".equals( explicitAccessType ) ) {
				isExplicitPropertyAnnotated = Boolean.TRUE;
			}
			else if ( "field".equals( explicitAccessType ) ) {
				isExplicitPropertyAnnotated = Boolean.FALSE;
			}
		}
		Boolean isPropertyAnnotated = isExplicitPropertyAnnotated == null ?
				isPropertyAnnotated = Boolean.TRUE :  //default to property and fallback if needed
				isExplicitPropertyAnnotated;
		String accessType = explicitAccessType != null ? explicitAccessType : "property";

		for ( int index = 0; index < deep ; index++ ) {
			Class clazz = classesToProcess.get( index );
			InheritanceState state = inheritanceStatePerClass.get( clazz );

			boolean currentHasIdentifier = addElementsOfAClass( elements, propertyHolder, isPropertyAnnotated,
					accessType, clazz, state.rootEntity );
			hasIdentifier = hasIdentifier || currentHasIdentifier;
		}

		if (!hasIdentifier && !inheritanceState.hasParents) {
			if (isExplicitPropertyAnnotated != null) return null; //explicit but no @Id
			isPropertyAnnotated = !isPropertyAnnotated;
			accessType = "field";
			elements.clear();
			for ( int index = 0; index < deep ; index++ ) {
				Class clazz = classesToProcess.get( index );
				InheritanceState state = inheritanceStatePerClass.get( clazz );
				boolean currentHasIdentifier = addElementsOfAClass( elements, propertyHolder, isPropertyAnnotated,
						accessType, clazz, state.rootEntity );
				hasIdentifier = hasIdentifier || currentHasIdentifier;
			}
		}
		//TODO set the access type here?
		entityBinder.setPropertyAnnotated( isPropertyAnnotated );
		entityBinder.setPropertyAccessor( accessType );
		inheritanceState.isPropertyAnnotated = isPropertyAnnotated;
		inheritanceState.accessType = accessType;
		return hasIdentifier || inheritanceState.hasParents ? elements : null;
	}

	private static List<Class> orderClassesToBeProcessed(
			Class annotatedClass, Map<Class, InheritanceState> inheritanceStatePerClass,
			InheritanceState inheritanceState
	) {
		//ordered to allow proper messages on properties subclassing
		List<Class> classesToProcess = new ArrayList<Class>();
		Class currentClassInHierarchy = annotatedClass;
		InheritanceState superclassState;
		do {
			classesToProcess.add( 0, currentClassInHierarchy );
			Class superClass = currentClassInHierarchy;
			do {
				superClass = superClass.getSuperclass();
				superclassState = inheritanceStatePerClass.get( superClass );
			}
			while (superClass != Object.class && superclassState == null);

			if (superclassState != null && superclassState.isEmbeddableSuperclass) {
				superclassState.rootEntity = annotatedClass;
			}
			currentClassInHierarchy = superClass;
		}
		while ( superclassState != null && superclassState.isEmbeddableSuperclass );

		return classesToProcess;
	}

	private static void bindFilterDefs(AnnotatedElement annotatedElement, ExtendedMappings mappings) {
		FilterDef defAnn = annotatedElement.getAnnotation( FilterDef.class );
		FilterDefs defsAnn = annotatedElement.getAnnotation( FilterDefs.class );
		if ( defAnn != null ) {
			bindFilterDef( defAnn, mappings );
		}
		if ( defsAnn != null ) {
			for ( FilterDef def : defsAnn.value() ) {
				bindFilterDef( def, mappings );
			}
		}
	}

	private static void bindFilterDef(FilterDef defAnn, ExtendedMappings mappings) {
		Map<String, org.hibernate.type.Type> params = new HashMap<String, org.hibernate.type.Type>();
		for ( ParamDef param : defAnn.parameters() ) {
			params.put( param.name(), TypeFactory.heuristicType( param.type() ) );
		}
		FilterDefinition def = new FilterDefinition( defAnn.name(), defAnn.defaultCondition(), params );
		if (log.isInfoEnabled() ) log.info( "Binding filter definition: " + def.getFilterName() );
		mappings.addFilterDefinition( def );
	}

	private static void bindTypeDefs(AnnotatedElement annotatedElement, ExtendedMappings mappings) {
		TypeDef defAnn = annotatedElement.getAnnotation( TypeDef.class );
		TypeDefs defsAnn = annotatedElement.getAnnotation( TypeDefs.class );
		if ( defAnn != null ) {
			bindTypeDef( defAnn, mappings );
		}
		if ( defsAnn != null ) {
			for ( TypeDef def : defsAnn.value() ) {
				bindTypeDef( def, mappings );
			}
		}
	}

	private static void bindTypeDef(TypeDef defAnn, ExtendedMappings mappings) {
		Properties params = new Properties();
		for ( Parameter param : defAnn.parameters() ) {
			params.setProperty( param.name(), param.value() );
		}
		if (log.isInfoEnabled() ) log.info( "Binding type definition: " + defAnn.name() );
		mappings.addTypeDef( defAnn.name(), defAnn.typeClass().getName(), params );
	}

	private static void bindDiscriminatorToPersistentClass(
			RootClass rootClass,
			Ejb3DiscriminatorColumn discriminatorColumn, Map<String, Join> secondaryTables,
			PropertyHolder propertyHolder
	) {
		if ( rootClass.getDiscriminator() == null ) {
			if ( discriminatorColumn == null ) {
				throw new AssertionFailure( "discriminator column should have been built" );
			}
			discriminatorColumn.setJoins( secondaryTables );
			discriminatorColumn.setPropertyHolder( propertyHolder );
			SimpleValue discrim = new SimpleValue( rootClass.getTable() );
			rootClass.setDiscriminator( discrim );
			discriminatorColumn.linkWithValue( discrim );
			discrim.setTypeName( discriminatorColumn.getDiscriminatorTypeName() );
			rootClass.setPolymorphic( true );
			log.debug( "Setting discriminator for entity " + rootClass.getEntityName() );
		}
	}

	/**
	 * Add elements of a class
	 */
	private static boolean addElementsOfAClass(
			List<PropertyAnnotatedElement> elements, PropertyHolder propertyHolder, boolean isPropertyAnnotated,
			String propertyAccessor, final Class annotatedClass,
			final Class rootEntity
	) {
		boolean hasIdentifier = false;
		AccessType access = (AccessType) annotatedClass.getAnnotation( AccessType.class );
		String localPropertyAccessor = access != null ? access.value() : null;
		boolean localPropertyAnnotated;
		if ( "property".equals( localPropertyAccessor ) ) {
			localPropertyAnnotated = true;
		}
		else if ("field".equals( localPropertyAccessor ) ) {
			localPropertyAnnotated = false;
		}
		else if (localPropertyAccessor == null) {
			localPropertyAccessor = propertyAccessor;
			localPropertyAnnotated = isPropertyAnnotated;
		}
		else {
			localPropertyAnnotated = isPropertyAnnotated;
		}

		if ( localPropertyAnnotated ) {
			log.debug( "Processing " + propertyHolder.getEntityName() + " per property annotation" );
			Method[] methods = annotatedClass.getDeclaredMethods();
			Arrays.sort(methods, new Comparator<Method>(){
			    public int compare(Method o1, Method o2)
				{
				    return o1.getName().compareTo(o2.getName());
				}
			});

			AnnotatedElement currentElt;
			int index = 0;
			while ( index < methods.length ) {
				currentElt = (AnnotatedElement) methods[index];
				final boolean currentHasIdentifier = addAnnotatedElement( currentElt, elements, rootEntity,
						localPropertyAccessor
				);
				hasIdentifier = hasIdentifier || currentHasIdentifier;
				index++;
			}
		}
		else {
			log.debug( "Processing " + propertyHolder.getEntityName() + " per field annotation" );
			Field[] fields = annotatedClass.getDeclaredFields();
			Arrays.sort(fields, new Comparator<Field>(){
			    public int compare(Field o1, Field o2)
				{
				    return o1.getName().compareTo(o2.getName());
				}
			});

			AnnotatedElement currentElt;
			int index = 0;
			while ( index < fields.length ) {
				currentElt = (AnnotatedElement) fields[index];
				final boolean currentHasIdentifier = addAnnotatedElement( currentElt, elements, rootEntity,
						localPropertyAccessor
				);
				hasIdentifier = hasIdentifier || currentHasIdentifier;
				index++;
			}
		}
		return hasIdentifier;
	}

	private static boolean addAnnotatedElement(
			AnnotatedElement elt, List<PropertyAnnotatedElement> annElts, Class rootEntity,
			String propertyAccessor
	) {
		boolean hasIdentifier = false;
		PropertyAnnotatedElement propertyAnnotatedElement = new PropertyAnnotatedElement( elt, rootEntity,
				propertyAccessor
		);
		if ( ! propertyAnnotatedElement.inferredData.skip() ) {
			/* 
			 * put element annotated by @Id in front
			 * since it has to be parsed before any assoctation by Hibernate 
			 */
			final AnnotatedElement element = propertyAnnotatedElement.element;
			if ( element.isAnnotationPresent( Id.class ) || element.isAnnotationPresent( EmbeddedId.class ) ) {
				annElts.add( 0, propertyAnnotatedElement );
				hasIdentifier = true;
			}
			else {
				annElts.add( propertyAnnotatedElement );
				hasIdentifier = false;
			}
		}
		return hasIdentifier;
	}

	/**
	 * Process annotation of a particular element
	 */
	private static void processElementAnnotations(
			PropertyHolder propertyHolder, Nullability nullability, AnnotatedElement annotatedElt,
			PropertyInferredData inferredData, HashMap<String, IdGenerator> classGenerators,
			EntityBinder entityBinder, boolean isIdentifierMapper,
			boolean isComponentEmbedded, String accessType, ExtendedMappings mappings
	)
			throws MappingException {
		Ejb3Column[] columns = null;
		Ejb3JoinColumn[] joinColumns = null;
		if ( log.isDebugEnabled() ) {
			log.debug(
					"Processing annotations of " + propertyHolder.getEntityName() + "." + inferredData.getPropertyName()
			);
		}

		//process @JoinColumn(s) before @Column(s) to handle collection of elements properly
		if ( annotatedElt.isAnnotationPresent( JoinColumn.class ) ) {
			JoinColumn ann = (JoinColumn) annotatedElt.getAnnotation( JoinColumn.class );
			joinColumns = new Ejb3JoinColumn[1];
			joinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
					ann,
					entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappings
			);
		}
		else if ( annotatedElt.isAnnotationPresent( JoinColumns.class ) ) {
			JoinColumns ann = annotatedElt.getAnnotation( JoinColumns.class );
			JoinColumn[] annColumns = ann.value();
			int length = annColumns.length;
			if ( length == 0 ) {
				throw new AnnotationException( "Cannot bind an empty @JoinColumns" );
			}
			joinColumns = new Ejb3JoinColumn[length];
			for ( int index = 0; index < length ; index++ ) {
				joinColumns[index] = Ejb3JoinColumn.buildJoinColumn(
						annColumns[index],
						entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappings
				);
			}
		}
		if ( annotatedElt.isAnnotationPresent( Column.class ) || annotatedElt.isAnnotationPresent( Formula.class ) ) {
			Column ann = (Column) annotatedElt.getAnnotation( Column.class );
			Formula formulaAnn = (Formula) annotatedElt.getAnnotation( Formula.class );
			columns = Ejb3Column.buildColumnFromAnnotation(
					new Column[]{ann}, formulaAnn, nullability, propertyHolder, inferredData,
					entityBinder.getSecondaryTables(), mappings
			);
		}
		else if ( annotatedElt.isAnnotationPresent( Columns.class ) ) {
			Columns anns = annotatedElt.getAnnotation( Columns.class );
			columns = Ejb3Column.buildColumnFromAnnotation(
					anns.columns(), null, nullability, propertyHolder, inferredData, entityBinder.getSecondaryTables(),
					mappings
			);
		}

		//set default values in needed
		if ( joinColumns == null &&
				( annotatedElt.isAnnotationPresent( ManyToOne.class )
						|| annotatedElt.isAnnotationPresent( OneToOne.class ) )
				) {
			joinColumns = new Ejb3JoinColumn[1];
			if ( annotatedElt.isAnnotationPresent( JoinTable.class ) ) {
				JoinTable assocTable = annotatedElt.getAnnotation( JoinTable.class );
				//entityBinder.firstLevelSecondaryTablesBinding(assocTable);
				throw new NotYetImplementedException(
						"association table on a single ended association is not yet supported"
				);
			}
			else {
				OneToOne oneToOneAnn = annotatedElt.getAnnotation( OneToOne.class );
				String mappedBy = oneToOneAnn != null ?
						oneToOneAnn.mappedBy() :
						null;
				joinColumns[0] = Ejb3JoinColumn.buildImplicitJoinColumn(
						mappedBy, entityBinder.getSecondaryTables(),
						propertyHolder, inferredData.getPropertyName(), mappings
				);
			}
		}
		else if ( joinColumns == null &&
				( annotatedElt.isAnnotationPresent( OneToMany.class )
				|| annotatedElt.isAnnotationPresent( CollectionOfElements.class ) ) ) {
			joinColumns = new Ejb3JoinColumn[1];
			OneToMany oneToMany = (OneToMany) annotatedElt.getAnnotation( OneToMany.class );
			String mappedBy = oneToMany != null ?
					oneToMany.mappedBy() :
					"";
			joinColumns[0] = Ejb3JoinColumn.buildImplicitJoinColumn(
					mappedBy, entityBinder.getSecondaryTables(),
					propertyHolder, inferredData.getPropertyName(), mappings
			);
		}
		if ( columns == null && ! annotatedElt.isAnnotationPresent( ManyToMany.class ) ) {
			//useful for collection of embedded elements
			columns = Ejb3Column.buildColumnFromAnnotation(
					null, null, nullability, propertyHolder, inferredData, entityBinder.getSecondaryTables(), mappings
			);
		}

		//init index
		Index index = annotatedElt.getAnnotation( Index.class );
		if ( index != null ) {
			for ( Ejb3Column column : columns ) {
				column.addIndex( index );
			}
		}

		if ( nullability == Nullability.FORCED_NOT_NULL ) {
			//force columns to not null
			for ( Ejb3Column col : columns ) {
				col.forceNotNull();
			}
		}

		final Class returnedClass = inferredData.getReturnedClassOrElement();
		if ( !entityBinder.isIgnoreIdAnnotations() &&
				( annotatedElt.isAnnotationPresent( Id.class )
					|| annotatedElt.isAnnotationPresent( EmbeddedId.class ) ) ) {
			if ( isIdentifierMapper ) {
				throw new AnnotationException(
						"@IdClass class should not have @Id nor @EmbeddedId proeperties"
				);
			}
			log.debug( inferredData.getPropertyName() + " is an id" );
			//clone classGenerator and override with local values
			HashMap<String, IdGenerator> localGenerators = (HashMap<String, IdGenerator>) classGenerators.clone();
			localGenerators.putAll( buildLocalGenerators( annotatedElt, mappings ) );

			//manage composite related metadata
			Embeddable embeddableAnn = (Embeddable) returnedClass.getAnnotation( Embeddable.class );
			Map<String, Column[]> columnOverride = PropertyHolderBuilder.buildColumnOverride(
					annotatedElt, propertyHolder.getPath() + '.' + inferredData.getPropertyName()
			);
			//guess if its a component and find id data access (property, field etc)
			final boolean isComponent = embeddableAnn != null || annotatedElt.isAnnotationPresent( EmbeddedId.class );
			boolean propertyAnnotated = entityBinder.isPropertyAnnotated( returnedClass );
			String propertyAccessor = entityBinder.getPropertyAccessor( returnedClass );
			//if ( isComponent && embeddableAnn != null && embeddableAnn.access() == AccessType.FIELD ) propertyAccess = false;

			GeneratedValue generatedValue = annotatedElt.getAnnotation( GeneratedValue.class );
			String generatorType = generatedValue != null ? generatorType( generatedValue.strategy() ) : "assigned";
			String generator = generatedValue != null ? generatedValue.generator() : ANNOTATION_STRING_DEFAULT;
			if (isComponent) generatorType = "assigned"; //a component must not have any generator
			Type typeAnn = annotatedElt.getAnnotation( Type.class );
			bindId(
					generatorType,
					generator,
					inferredData,
					columns,
					propertyHolder,
					localGenerators,
					isComponent,
					columnOverride,
					propertyAnnotated,
					propertyAccessor, entityBinder,
					typeAnn,
					false,
					mappings
			);
			if ( log.isDebugEnabled() ) {
				log.debug(
						"Bind " + ( isComponent ? "@EmbeddedId" : "@Id" ) + " on " + inferredData.getPropertyName()
				);
			}
		}
		else if ( annotatedElt.isAnnotationPresent( Version.class ) ) {
			if ( isIdentifierMapper ) {
				throw new AnnotationException(
						"@IdClass class should not have @Version property"
				);
			}
			if ( ! ( propertyHolder.getPersistentClass() instanceof RootClass ) ) {
				throw new AnnotationException(
						"Unable to define/override @Version on a subclass: "
								+ propertyHolder.getEntityName()
				);
			}
			log.debug( inferredData.getPropertyName() + " is a version property" );
			RootClass rootClass = (RootClass) propertyHolder.getPersistentClass();
			boolean lazy = false;
			PropertyBinder propBinder = new PropertyBinder();
			propBinder.setName( inferredData.getPropertyName() );
			propBinder.setReturnedClassName( inferredData.getReturnedClassName() );
			propBinder.setLazy( lazy );
			propBinder.setPropertyAccessorName( inferredData.getDefaultAccess() );
			propBinder.setColumns( columns );
			propBinder.setHolder( propertyHolder ); //PropertyHolderBuilder.buildPropertyHolder(rootClass)
			propBinder.setAnnotatedElement( annotatedElt );
			propBinder.setReturnedClass( inferredData.getReturnedClass() );

			propBinder.setMappings( mappings );
			Property prop = propBinder.bind();
			rootClass.setVersion( prop );
			SimpleValue simpleValue = (SimpleValue) prop.getValue();
			if ( !simpleValue.isTypeSpecified() ) simpleValue.setTypeName( "integer" );
			simpleValue.setNullValue( "undefined" );
			rootClass.setOptimisticLockMode( Versioning.OPTIMISTIC_LOCK_VERSION );
			log.debug(
					"Version name: " + rootClass.getVersion().getName() + ", unsavedValue: " + ( (SimpleValue) rootClass
							.getVersion()
							.getValue() ).getNullValue()
			);
		}
		else if ( annotatedElt.isAnnotationPresent( ManyToOne.class ) ) {
			ManyToOne ann = annotatedElt.getAnnotation( ManyToOne.class );
			Cascade hibernateCascade = annotatedElt.getAnnotation( Cascade.class );
			NotFound notFound = annotatedElt.getAnnotation( NotFound.class );
			boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
			bindManyToOne(
					getCascadeStrategy( ann.cascade(), hibernateCascade ),
					(Ejb3JoinColumn[]) joinColumns,
					ann.optional(),
					getFetchMode( ann.fetch() ),
					ignoreNotFound, inferredData.getPropertyName(),
					inferredData.getReturnedClassOrElementName(),
					ann.targetEntity(),
					inferredData.getDefaultAccess(),
					propertyHolder,
					false, isIdentifierMapper, mappings
			);
		}
		else if ( annotatedElt.isAnnotationPresent( OneToOne.class ) ) {
			OneToOne ann = annotatedElt.getAnnotation( OneToOne.class );
			boolean trueOneToOne = annotatedElt.isAnnotationPresent( PrimaryKeyJoinColumn.class );
			Cascade hibernateCascade = annotatedElt.getAnnotation( Cascade.class );
			NotFound notFound = annotatedElt.getAnnotation( NotFound.class );
			boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
			bindOneToOne(
					getCascadeStrategy( ann.cascade(), hibernateCascade ),
					(Ejb3JoinColumn[]) joinColumns,
					ann.optional(),
					getFetchMode( ann.fetch() ),
					ignoreNotFound, inferredData.getPropertyName(),
					inferredData.getReturnedClassOrElementName(),
					ann.targetEntity(),
					inferredData.getDefaultAccess(),
					propertyHolder,
					ann.mappedBy(), trueOneToOne, isIdentifierMapper, mappings
			);
		}
		else if ( annotatedElt.isAnnotationPresent( OneToMany.class )
				|| annotatedElt.isAnnotationPresent( ManyToMany.class )
				|| annotatedElt.isAnnotationPresent( CollectionOfElements.class ) ) {
			OneToMany oneToManyAnn = annotatedElt.getAnnotation( OneToMany.class );
			ManyToMany manyToManyAnn = annotatedElt.getAnnotation( ManyToMany.class );
			CollectionOfElements collectionOfElementsAnn = annotatedElt.getAnnotation( CollectionOfElements.class );
			org.hibernate.annotations.IndexColumn indexAnn = annotatedElt.getAnnotation(
					org.hibernate.annotations.IndexColumn.class
			);
			JoinTable assocTable = annotatedElt.getAnnotation( JoinTable.class );

			IndexColumn indexColumn = IndexColumn.buildColumnFromAnnotation(
					indexAnn, propertyHolder, inferredData, mappings
			);
			CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
					propertyHolder.getEntityName(),
					inferredData,
					! indexColumn.isImplicit()
			);
			collectionBinder.setIndexColumn( indexColumn );
			MapKey mapKeyAnn = annotatedElt.getAnnotation( MapKey.class );
			collectionBinder.setMapKey( mapKeyAnn );
			collectionBinder.setPropertyName( inferredData.getPropertyName() );
			BatchSize batchAnn = annotatedElt.getAnnotation( BatchSize.class );
			collectionBinder.setBatchSize( batchAnn );
			javax.persistence.OrderBy ejb3OrderByAnn = annotatedElt.getAnnotation( javax.persistence.OrderBy.class );
			OrderBy orderByAnn = annotatedElt.getAnnotation( OrderBy.class );
			collectionBinder.setEjb3OrderBy( ejb3OrderByAnn );
			collectionBinder.setSqlOrderBy( orderByAnn );
			Sort sortAnn = annotatedElt.getAnnotation( Sort.class );
			collectionBinder.setSort( sortAnn );
			Cache cachAnn = annotatedElt.getAnnotation( Cache.class );
			collectionBinder.setCache( cachAnn );
			Filter filterAnn = annotatedElt.getAnnotation( Filter.class );
			if ( filterAnn != null ) {
				collectionBinder.addFilter( filterAnn.name(), filterAnn.condition() );
			}
			Filters filtersAnn = annotatedElt.getAnnotation( Filters.class );
			if ( filtersAnn != null ) {
				for ( Filter filter : filtersAnn.value() ) {
					collectionBinder.addFilter( filter.name(), filter.condition() );
				}
			}
			collectionBinder.setPropertyHolder( propertyHolder );
			Where whereAnn = annotatedElt.getAnnotation( Where.class );
			collectionBinder.setWhere( whereAnn );
			Cascade hibernateCascade = annotatedElt.getAnnotation( Cascade.class );
			NotFound notFound = annotatedElt.getAnnotation( NotFound.class );
			boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
			collectionBinder.setIgnoreNotFound(ignoreNotFound);
			collectionBinder.setCollectionType( inferredData.getCollectionType() );
			collectionBinder.setMappings( mappings );
			collectionBinder.setPropertyAccessorName( inferredData.getDefaultAccess() );

			Ejb3Column[] elementColumns = null;
			if ( annotatedElt.isAnnotationPresent( Column.class ) || annotatedElt.isAnnotationPresent(
					Formula.class
			) ) {
				Column ann = (Column) annotatedElt.getAnnotation( Column.class );
				Formula formulaAnn = (Formula) annotatedElt.getAnnotation( Formula.class );
				elementColumns = Ejb3Column.buildColumnFromAnnotation(
						new Column[]{ann},
						formulaAnn,
						nullability,
						propertyHolder,
						inferredData,
						entityBinder.getSecondaryTables(),
						mappings
				);
			}
			else if ( annotatedElt.isAnnotationPresent( Columns.class ) ) {
				Columns anns = annotatedElt.getAnnotation( Columns.class );
				elementColumns = Ejb3Column.buildColumnFromAnnotation(
						anns.columns(), null, nullability, propertyHolder, inferredData,
						entityBinder.getSecondaryTables(), mappings
				);
			}

			//potential element
			collectionBinder.setEmbedded( annotatedElt.isAnnotationPresent( Embedded.class ) );
			collectionBinder.setElementColumns( elementColumns );
			collectionBinder.setAnnotatedElement( annotatedElt );

			if ( oneToManyAnn != null && manyToManyAnn != null ) {
				throw new AnnotationException(
						"@OneToMany and @ManyToMany on the same property is not allowed: "
								+ propertyHolder.getEntityName() + "." + inferredData.getPropertyName()
				);
			}
			String mappedBy = null;
			if ( oneToManyAnn != null ) {
				for ( Ejb3JoinColumn column : joinColumns ) {
					if ( column.isSecondary() ) {
						throw new NotYetImplementedException( "Collections having FK in secondary table" );
					}
				}
				collectionBinder.setFkJoinColumns( joinColumns );
				mappedBy = oneToManyAnn.mappedBy();
				collectionBinder.setTargetEntity( oneToManyAnn.targetEntity() );
				collectionBinder.setFetchType( oneToManyAnn.fetch() );
				collectionBinder.setCascadeStrategy( getCascadeStrategy( oneToManyAnn.cascade(), hibernateCascade ) );
				collectionBinder.setOneToMany( true );
			}
			else if ( collectionOfElementsAnn != null ) {
				for ( Ejb3JoinColumn column : joinColumns ) {
					if ( column.isSecondary() ) {
						throw new NotYetImplementedException( "Collections having FK in secondary table" );
					}
				}
				collectionBinder.setFkJoinColumns( joinColumns );
				mappedBy = "";
				collectionBinder.setTargetEntity( collectionOfElementsAnn.targetElement() );
				collectionBinder.setFetchType( collectionOfElementsAnn.fetch() );
				//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
				collectionBinder.setOneToMany( true );
			}
			else if ( manyToManyAnn != null ) {
				mappedBy = manyToManyAnn.mappedBy();
				collectionBinder.setTargetEntity( manyToManyAnn.targetEntity() );
				collectionBinder.setFetchType( manyToManyAnn.fetch() );
				collectionBinder.setCascadeStrategy( getCascadeStrategy( manyToManyAnn.cascade(), hibernateCascade ) );
				collectionBinder.setOneToMany( false );
			}
			collectionBinder.setMappedBy( mappedBy );
			bindJoinedTableAssociation(
					assocTable, mappings, entityBinder, collectionBinder, propertyHolder, inferredData, mappedBy
			);

			OnDelete onDeleteAnn = annotatedElt.getAnnotation( OnDelete.class );
			boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
			collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
			if ( isIdentifierMapper ) {
				collectionBinder.setInsertable( false );
				collectionBinder.setUpdatable( false );
			}
			collectionBinder.bind();
		}
		else {
			//define whether the type is a component or not
			boolean isComponent = false;
			Embeddable embeddableAnn = (Embeddable) returnedClass.getAnnotation( Embeddable.class );
			Embedded embeddedAnn = (Embedded) annotatedElt.getAnnotation( Embedded.class );
			isComponent = embeddedAnn != null || embeddableAnn != null;

			if ( isComponent ) {
				//process component object
				//boolean propertyAccess = true;
				//if ( embeddableAnn != null && embeddableAnn.access() == AccessType.FIELD ) propertyAccess = false;
				boolean propertyAnnotated = entityBinder.isPropertyAnnotated( annotatedElt );
				String propertyAccessor = entityBinder.getPropertyAccessor( annotatedElt );
				Map<String, Column[]> columnOverride = PropertyHolderBuilder.buildColumnOverride(
						annotatedElt,
						StringHelper.qualify( propertyHolder.getPath(), inferredData.getPropertyName() )
				);
				bindComponent(
						inferredData, propertyHolder, propertyAnnotated, propertyAccessor, entityBinder, isIdentifierMapper, columnOverride,
						mappings, isComponentEmbedded
				);
			}
			else {
				//provide the basic property mapping
				boolean optional = true;
				boolean lazy = false;
				if ( annotatedElt.isAnnotationPresent( Basic.class ) ) {
					Basic ann = (Basic) annotatedElt.getAnnotation( Basic.class );
					optional = ann.optional();
					lazy = ann.fetch() == FetchType.LAZY;
				}
				//implicit type will check basic types and Serializable classes
				if ( !optional && nullability != Nullability.FORCED_NULL ) {
					//force columns to not null
					for ( Ejb3Column col : columns ) {
						col.forceNotNull();
					}
				}

				PropertyBinder propBinder = new PropertyBinder();
				propBinder.setName( inferredData.getPropertyName() );
				propBinder.setReturnedClassName( inferredData.getReturnedClassName() );
				propBinder.setLazy( lazy );
				propBinder.setPropertyAccessorName( inferredData.getDefaultAccess() );
				propBinder.setColumns( columns );
				propBinder.setHolder( propertyHolder );
				propBinder.setAnnotatedElement( annotatedElt );
				propBinder.setReturnedClass( inferredData.getReturnedClass() );
				propBinder.setMappings( mappings );
				if ( isIdentifierMapper ) {
					propBinder.setInsertable( false );
					propBinder.setUpdatable( false );
				}
				propBinder.bind();
			}
		}
	}

	//TODO move that to collection binder?
	private static void bindJoinedTableAssociation(
			JoinTable joinTableAnn, ExtendedMappings mappings, EntityBinder entityBinder,
			CollectionBinder collectionBinder, PropertyHolder propertyHolder, PropertyInferredData inferredData,
			String mappedBy
	) {
		TableBinder associationTableBinder = new TableBinder();
		JoinColumn[] annJoins;
		JoinColumn[] annInverseJoins;
		if ( joinTableAnn != null ) {
			collectionBinder.setExplicitAssociationTable( true );
			if ( ! isDefault( joinTableAnn.schema() ) ) associationTableBinder.setSchema( joinTableAnn.schema() );
			if ( ! isDefault( joinTableAnn.catalog() ) ) associationTableBinder.setCatalog( joinTableAnn.catalog() );
			if ( ! isDefault( joinTableAnn.name() ) ) associationTableBinder.setName( joinTableAnn.name() );
			associationTableBinder.setUniqueConstraints( joinTableAnn.uniqueConstraints() );

			//set check constaint in the second pass

			JoinColumn[] joins = joinTableAnn.joinColumns();

			if ( joins.length == 0 ) {
				annJoins = null;
			}
			else {
				annJoins = joins;
			}

			JoinColumn[] inverseJoins = joinTableAnn.inverseJoinColumns();

			if ( inverseJoins.length == 0 ) {
				annInverseJoins = null;
			}
			else {
				annInverseJoins = inverseJoins;
			}
		}
		else {
			annJoins = null;
			annInverseJoins = null;
		}
		Ejb3JoinColumn[] joinColumns = buildJoinTableJoinColumns(
				annJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappedBy,
				mappings
		);
		Ejb3JoinColumn[] inverseJoinColumns = buildJoinTableJoinColumns(
				annInverseJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(),
				mappedBy, mappings
		);
		associationTableBinder.setMappings( mappings );
		collectionBinder.setTableBinder( associationTableBinder );
		collectionBinder.setJoinColumns( joinColumns );
		collectionBinder.setInverseJoinColumns( inverseJoinColumns );
	}

	private static Ejb3JoinColumn[] buildJoinTableJoinColumns(
			JoinColumn[] annJoins, Map<String, Join> secondaryTables,
			PropertyHolder propertyHolder, String propertyName, String mappedBy, ExtendedMappings mappings
	) {
		Ejb3JoinColumn[] joinColumns;
		if ( annJoins == null ) {
			Ejb3JoinColumn currentJoinColumn = new Ejb3JoinColumn();
			currentJoinColumn.setImplicit( true );
			currentJoinColumn.setNullable( false ); //I break the spec, but it's for good
			currentJoinColumn.setPropertyHolder( propertyHolder );
			currentJoinColumn.setJoins( secondaryTables );
			currentJoinColumn.setMappings( mappings );
			currentJoinColumn.setPropertyName(
					BinderHelper.getRelativePath( propertyHolder, propertyName )
			);
			currentJoinColumn.setMappedBy( mappedBy );
			currentJoinColumn.bind();

			joinColumns = new Ejb3JoinColumn[]{
					currentJoinColumn

			};
		}
		else {
			joinColumns = new Ejb3JoinColumn[annJoins.length];
			JoinColumn annJoin;
			int length = annJoins.length;
			for ( int index = 0; index < length ; index++ ) {
				annJoin = annJoins[index];
				Ejb3JoinColumn currentJoinColumn = new Ejb3JoinColumn();
				currentJoinColumn.setImplicit( true );
				currentJoinColumn.setPropertyHolder( propertyHolder );
				currentJoinColumn.setJoins( secondaryTables );
				currentJoinColumn.setMappings( mappings );
				currentJoinColumn.setPropertyName( BinderHelper.getRelativePath( propertyHolder, propertyName ) );
				currentJoinColumn.setMappedBy( mappedBy );
				currentJoinColumn.setJoinAnnotation( annJoin, propertyName );
				currentJoinColumn.setNullable( false ); //I break the spec, but it's for good
				//done after the annotation to override it
				currentJoinColumn.bind();
				joinColumns[index] = currentJoinColumn;
			}
		}
		return joinColumns;
	}

	private static void bindComponent(
			PropertyInferredData inferredData,
			PropertyHolder propertyHolder,
			boolean propertyAnnotated,
			String propertyAccessor, EntityBinder entityBinder,
			boolean isIdentifierMapper, Map<String, Column[]> columnOverride,
			ExtendedMappings mappings, boolean isComponentEmbedded
	) {
		Component comp = fillComponent(
				propertyHolder, inferredData, propertyAnnotated, propertyAccessor, true, entityBinder, isIdentifierMapper, columnOverride,
				mappings, isComponentEmbedded
		);

		PropertyBinder binder = new PropertyBinder();
		binder.setName( inferredData.getPropertyName() );
		binder.setValue( comp );
		binder.setPropertyAccessorName( inferredData.getDefaultAccess() );
		Property prop = binder.make();
		propertyHolder.addProperty( prop );
	}

	public static Component fillComponent(
			PropertyHolder propertyHolder, PropertyInferredData inferredData,
			boolean propertyAnnotated, String propertyAccessor, boolean isNullable,
			EntityBinder entityBinder,
			boolean isIdentifierMapper, Map<String, Column[]> columnOverride, ExtendedMappings mappings,
			boolean isComponentEmbedded
	) {
		Component comp = new Component( propertyHolder.getPersistentClass() );
		comp.setEmbedded( isComponentEmbedded );
		//yuk
		comp.setTable( propertyHolder.getTable() );
		if ( !isIdentifierMapper ) {
			comp.setComponentClassName( inferredData.getReturnedClassOrElementName() );
		}
		else {
			comp.setComponentClassName( comp.getOwner().getClassName() );
		}
		String subpath = StringHelper.qualify( propertyHolder.getPath(), inferredData.getPropertyName() );
		log.debug( "Binding component with path: " + subpath );
		Map<String, Column[]> localColumnOverride = propertyHolder.mergeOverridenColumns( columnOverride );
		PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder( comp, subpath, localColumnOverride );
		List<PropertyAnnotatedElement> classElements = new ArrayList<PropertyAnnotatedElement>();
		Class returnedClassOrElement = inferredData.getReturnedClassOrElement();
		addElementsOfAClass(
				classElements,
				subHolder,
				propertyAnnotated,
				propertyAccessor, returnedClassOrElement,
				null
		);
		//add elements of the embeddable superclass
		Class superClass = inferredData.getReturnedClass().getSuperclass();
		while ( superClass != null && superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
			//FIXME: proper support of typevariables incl var resolved at upper levels
			addElementsOfAClass(
					classElements,
					subHolder,
					entityBinder.isPropertyAnnotated( superClass ),
					propertyAccessor, superClass,
					returnedClassOrElement
			);
			superClass = superClass.getSuperclass();
		}
		for ( PropertyAnnotatedElement propertyAnnotatedElement : classElements ) {
			processElementAnnotations(
					subHolder, isNullable ? Nullability.NO_CONSTRAINT : Nullability.FORCED_NOT_NULL,
					propertyAnnotatedElement.element, propertyAnnotatedElement.inferredData,
					new HashMap<String, IdGenerator>(), entityBinder, isIdentifierMapper, isComponentEmbedded,
					propertyAccessor, mappings
			);
		}
		return comp;
	}

	private static void bindId(
			String generatorType, String generatorName,
			PropertyInferredData inferredData, Ejb3Column[] columns, PropertyHolder propertyHolder,
			Map<String, IdGenerator> localGenerators,
			boolean isComposite, Map<String, Column[]> columnOverride, boolean isPropertyAnnotated,
			String propertyAccessor, EntityBinder entityBinder, Type typeAnn, boolean isEmbedded,
			ExtendedMappings mappings
	) {
		/*
		 * Fill simple value and property since and Id is a property
		 */
		PersistentClass persistentClass = propertyHolder.getPersistentClass();
		if ( ! ( persistentClass instanceof RootClass ) ) {
			throw new AnnotationException(
					"Unable to define/override @Id(s) on a subclass: "
							+ propertyHolder.getEntityName()
			);
		}
		RootClass rootClass = (RootClass) persistentClass;
		String persistentClassName = rootClass == null ? null : rootClass.getClassName();
		SimpleValue id;
		if ( isComposite ) {
			id = fillComponent(
					propertyHolder, inferredData, isPropertyAnnotated, propertyAccessor, false, entityBinder, false, columnOverride, mappings,
					false
			);
			( (Component) id ).setKey( true );
			if ( ! id.getColumnIterator().hasNext() ) {
				throw new AnnotationException( ( (Component) id ).getComponentClassName() + " has not persistent id property" );
			}
		}
		else {
			for ( Ejb3Column column : columns ) {
				column.forceNotNull(); //this is an id
			}
			SimpleValueBinder value = new SimpleValueBinder();
			value.setPropertyName( inferredData.getPropertyName() );
			value.setReturnedClassName( inferredData.getReturnedClassName() );
			value.setColumns( columns );
			value.setPersistentClassName( persistentClassName );
			value.setMappings( mappings );
			value.setExplicitType( typeAnn );
			id = value.make();
		}
		rootClass.setIdentifier( id );
		Table table = id.getTable();
		table.setIdentifierValue( id );
		//generator settings
		id.setIdentifierGeneratorStrategy( generatorType );
		Properties params = new Properties();
		//always settable
		params.setProperty(
				PersistentIdentifierGenerator.TABLE, table.getName()
		);

		Iterator idColumnIterator = id.getColumnIterator();
		params.setProperty(
				PersistentIdentifierGenerator.PK,
				( (org.hibernate.mapping.Column) idColumnIterator.next() ).getName()
		);
		if ( ! isDefault( generatorName ) ) {
			//we have a named generator
			IdGenerator gen = mappings.getGenerator( generatorName, localGenerators );
			if ( gen == null ) {
				throw new AnnotationException( "Unknown Id.generator: " + generatorName );
			}
			//This is quite vague in the spec but a generator could override the generate choice
			String identifierGeneratorStrategy = gen.getIdentifierGeneratorStrategy();
			//yuk! this is a hack not to override 'AUTO' even if generator is set
			final boolean avoidOverriding =
					identifierGeneratorStrategy.equals( "identity" )
					|| identifierGeneratorStrategy.equals( "sequence" )
					|| identifierGeneratorStrategy.equals( MultipleHiLoPerTableGenerator.class.getName() );
			if ( ! avoidOverriding ) {
				id.setIdentifierGeneratorStrategy( identifierGeneratorStrategy );
			}
			//checkIfMatchingGenerator(gen, generatorType, generatorName);
			Iterator genParams = gen.getParams().entrySet().iterator();
			while ( genParams.hasNext() ) {
				Map.Entry elt = (Map.Entry) genParams.next();
				params.setProperty( (String) elt.getKey(), (String) elt.getValue() );
			}
		}
		if ( generatorType == "assigned" ) id.setNullValue( "undefined" );
		id.setIdentifierGeneratorProperties( params );
		if ( isEmbedded ) {
			rootClass.setEmbeddedIdentifier( inferredData.getReturnedClass() == null );
		}
		else {
			PropertyBinder binder = new PropertyBinder();
			binder.setName( inferredData.getPropertyName() );
			binder.setValue( id );
			binder.setPropertyAccessorName( inferredData.getDefaultAccess() );
			Property prop = binder.make();
			rootClass.setIdentifierProperty( prop );
		}
	}

	private static void bindManyToOne(
			String cascadeStrategy, Ejb3JoinColumn[] columns, boolean optional, FetchMode fetchMode,
			boolean ignoreNotFound, String propertyName,
			String returnedClassName, Class targetEntity, String propertyAccessorName, PropertyHolder propertyHolder,
			boolean unique, boolean isIdentifierMapper, ExtendedMappings mappings
	) {
		//All FK columns should be in the same table
		org.hibernate.mapping.ManyToOne value = new org.hibernate.mapping.ManyToOne( columns[0].getTable() );
		if ( isDefault( targetEntity ) ) {
			value.setReferencedEntityName( returnedClassName );
		}
		else {
			value.setReferencedEntityName( targetEntity.getName() );
		}
		value.setFetchMode( fetchMode );
		value.setIgnoreNotFound( ignoreNotFound );
		value.setLazy( fetchMode != FetchMode.JOIN );
		if ( !optional ) {
			for ( Ejb3JoinColumn column : columns ) {
				column.setNullable( false );
			}
		}
		value.setTypeName( returnedClassName );
		value.setTypeUsingReflection( propertyHolder.getClassName(), propertyName );

		//value.createForeignKey();
		String path = propertyHolder.getPath() + "." + propertyName;
		mappings.addSecondPass( new FkSecondPass( value, columns, unique, path, mappings ) );

		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + propertyName );
		PropertyBinder binder = new PropertyBinder();
		binder.setName( propertyName );
		binder.setValue( value );
		//binder.setCascade(cascadeStrategy);
		if ( isIdentifierMapper ) {
			binder.setInsertable( false );
			binder.setInsertable( false );
		}
		else {
			binder.setInsertable( columns[0].isInsertable() );
			binder.setUpdatable( columns[0].isUpdatable() );
		}
		binder.setPropertyAccessorName( propertyAccessorName );
		binder.setCascade( cascadeStrategy );
		Property prop = binder.make();
		//composite FK columns are in the same table so its OK
		propertyHolder.addProperty( prop, columns );
	}

	private static void bindOneToOne(
			String cascadeStrategy,
			Ejb3JoinColumn[] columns,
			boolean optional,
			FetchMode fetchMode,
			boolean ignoreNotFound, String propertyName,
			String returnedClassName,
			Class targetEntity,
			String propertyAccessorName,
			PropertyHolder propertyHolder,
			String mappedBy,
			boolean trueOneToOne,
			boolean isIdentifierMapper, ExtendedMappings mappings
	) {
		//column.getTable() => persistentClass.getTable()
		log.debug( "Fetching " + propertyName + " with " + fetchMode );
		boolean mapToPK = true;
		if ( ! trueOneToOne ) {
			//try to find a hidden true one to one (FK == PK columns)
			Iterator idColumns = propertyHolder.getIdentifier().getColumnIterator();
			List<String> idColumnNames = new ArrayList<String>();
			org.hibernate.mapping.Column currentColumn;
			while ( idColumns.hasNext() ) {
				currentColumn = (org.hibernate.mapping.Column) idColumns.next();
				idColumnNames.add( currentColumn.getName() );
			}
			for ( Ejb3JoinColumn col : columns ) {
				if ( ! idColumnNames.contains( col.getMappingColumn().getName() ) ) {
					mapToPK = false;
					break;
				}
			}
		}
		if ( trueOneToOne || mapToPK || ! isDefault( mappedBy ) ) {
			//is a true one-to-one
			//FIXME referencedColumnName ignored => ordering may fail.
			org.hibernate.mapping.OneToOne value = new org.hibernate.mapping.OneToOne(
					propertyHolder.getTable(), propertyHolder.getPersistentClass()
			);
			value.setPropertyName( propertyName );
			if ( isDefault( targetEntity ) ) {
				value.setReferencedEntityName( returnedClassName );
			}
			else {
				value.setReferencedEntityName( targetEntity.getName() );
			}
			value.setFetchMode( fetchMode );
			value.setLazy( fetchMode != FetchMode.JOIN );

			if ( !optional ) value.setConstrained( true );
			value.setForeignKeyType(
					value.isConstrained() ?
							ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
							ForeignKeyDirection.FOREIGN_KEY_TO_PARENT
			);

			if ( ! isDefault( mappedBy) ) {
				mappings.addSecondPass( new ToOneMappedBySecondPass(
						mappedBy,
						value,
						propertyHolder.getEntityName(),
						propertyName,
						mappings)
				);
			}
//			if ( ! isDefault( mappedBy ) ) value.setReferencedPropertyName( mappedBy );
//
//			String propertyRef = value.getReferencedPropertyName();
//			if ( propertyRef != null ) {
//				mappings.addUniquePropertyReference(
//						value.getReferencedEntityName(),
//						propertyRef
//				);
//			}
			//value.createForeignKey();
			PropertyBinder binder = new PropertyBinder();
			binder.setName( propertyName );
			binder.setValue( value );
			binder.setCascade( cascadeStrategy );
			binder.setPropertyAccessorName( propertyAccessorName );
			Property prop = binder.make();
			prop.setCascade( cascadeStrategy );
			//no column associated since its a one to one
			propertyHolder.addProperty( prop );
		}
		else {
			//has a FK on the table
			bindManyToOne(
					cascadeStrategy, columns, optional, fetchMode, ignoreNotFound, propertyName, returnedClassName,
					targetEntity,
					propertyAccessorName, propertyHolder, true, isIdentifierMapper, mappings
			);
		}
	}

	private static String generatorType(GenerationType generatorEnum) {
		switch ( generatorEnum ) {
			case IDENTITY:
				return "identity";
			case AUTO:
				return "native";
			case TABLE:
				return MultipleHiLoPerTableGenerator.class.getName();
			case SEQUENCE:
				return "sequence";
		}
		throw new AssertionFailure( "Unknown GeneratorType: " + generatorEnum );
	}

	private static EnumSet<CascadeType> convertToHibernateCascadeType(javax.persistence.CascadeType[] ejbCascades) {
		EnumSet<CascadeType> hibernateCascadeSet = EnumSet.noneOf( CascadeType.class );
		if ( ejbCascades != null && ejbCascades.length > 0 ) {
			for ( javax.persistence.CascadeType cascade : ejbCascades ) {
				switch ( cascade ) {
					case ALL:
						hibernateCascadeSet.add( CascadeType.ALL );
						break;
					case PERSIST:
						hibernateCascadeSet.add( CascadeType.PERSIST );
						break;
					case MERGE:
						hibernateCascadeSet.add( CascadeType.MERGE );
						break;
					case REMOVE:
						hibernateCascadeSet.add( CascadeType.REMOVE );
						break;
					case REFRESH:
						hibernateCascadeSet.add( CascadeType.REFRESH );
						break;
				}
			}
		}

		return hibernateCascadeSet;
	}

	private static String getCascadeStrategy(
			javax.persistence.CascadeType[] ejbCascades, Cascade hibernateCascadeAnnotation
	) {
		EnumSet<CascadeType> hibernateCascadeSet = convertToHibernateCascadeType( ejbCascades );
		CascadeType[] hibernateCascades = hibernateCascadeAnnotation == null ? null : hibernateCascadeAnnotation.value();

		if ( hibernateCascades != null && hibernateCascades.length > 0 ) {
			for ( CascadeType cascadeType : hibernateCascades ) {
				hibernateCascadeSet.add( cascadeType );
			}
		}

		StringBuilder cascade = new StringBuilder();
		Iterator<CascadeType> cascadeType = hibernateCascadeSet.iterator();
		while ( cascadeType.hasNext() ) {
			switch ( cascadeType.next() ) {
				case ALL:
					cascade.append( "," ).append( "all" );
					break;
				case SAVE_UPDATE:
					cascade.append( "," ).append( "save-update" );
					break;
				case PERSIST:
					cascade.append( "," ).append( "persist" );
					break;
				case MERGE:
					cascade.append( "," ).append( "merge" );
					break;
				case LOCK:
					cascade.append( "," ).append( "lock" );
					break;
				case REFRESH:
					cascade.append( "," ).append( "refresh" );
					break;
				case REPLICATE:
					cascade.append( "," ).append( "replicate" );
					break;
				case EVICT:
					cascade.append( "," ).append( "evict" );
					break;
				case DELETE:
					cascade.append( "," ).append( "delete" );
					break;
				case DELETE_ORPHAN:
					cascade.append( "," ).append( "delete-orphan" );
					break;
				case REMOVE:
					cascade.append( "," ).append( "delete" );
					break;
			}
		}
		return cascade.length() > 0 ? cascade.substring( 1 ) : "none";
	}

	private static FetchMode getFetchMode(FetchType fetch) {
		if ( fetch == FetchType.EAGER ) {
			return FetchMode.JOIN;
		}
		else {
			return FetchMode.SELECT;
		}
	}

	private static HashMap<String, IdGenerator> buildLocalGenerators(AnnotatedElement annElt, Mappings mappings) {
		HashMap<String, IdGenerator> generators = new HashMap<String, IdGenerator>();
		TableGenerator tabGen = annElt.getAnnotation( TableGenerator.class );
		SequenceGenerator seqGen = annElt.getAnnotation( SequenceGenerator.class );
		GenericGenerator genGen = annElt.getAnnotation( GenericGenerator.class );
		if ( tabGen != null ) {
			IdGenerator idGen = buildIdGenerator( tabGen, mappings );
			generators.put( idGen.getName(), idGen );
		}
		if ( seqGen != null ) {
			IdGenerator idGen = buildIdGenerator( seqGen, mappings );
			generators.put( idGen.getName(), idGen );
		}
		if ( genGen != null ) {
			IdGenerator idGen = buildIdGenerator( genGen, mappings );
			generators.put( idGen.getName(), idGen );
		}
		return generators;
	}

	public static boolean isDefault(String annotationString) {
		return ANNOTATION_STRING_DEFAULT.equals( annotationString );
	}

	public static boolean isDefault(Class clazz) {
		return void.class.equals( clazz );
	}

	public static Map<Class, InheritanceState> buildInheritanceStates(List<Class> orderedClasses) {
		Map<Class, InheritanceState> inheritanceStatePerClass = new HashMap<Class, InheritanceState>(
				orderedClasses.size()
		);
		for ( Class clazz : orderedClasses ) {
			InheritanceState superclassState = InheritanceState.getSuperclassInheritanceState( clazz, inheritanceStatePerClass );
			InheritanceState state = new InheritanceState(clazz);
			if ( superclassState != null ) {
				//the classes are ordered thus preventing an NPE
				//FIXME if an entity has subclasses annotated @MappedSperclass wo sub @Entity this is wrong
				superclassState.hasSons = true;
				InheritanceState superEntityState = InheritanceState.getSuperEntityInheritanceState( clazz, inheritanceStatePerClass );
				state.hasParents = superEntityState != null;
				final boolean nonDefault = state.type != null && ! InheritanceType.SINGLE_TABLE.equals( state.type );
				if ( superclassState.type != null ) {
					final boolean mixingStrategy = state.type != null && ! state.type.equals( superclassState.type );
					if ( nonDefault && mixingStrategy ) {
						log.warn(
								"Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: " + clazz
										.getName()
						);
					}
					state.type = superclassState.type;
				}
			}
			inheritanceStatePerClass.put( clazz, state );
		}
		return inheritanceStatePerClass;
	}

	private static class PropertyAnnotatedElement {
		public PropertyAnnotatedElement(AnnotatedElement elt, Class rootEntity, String propertyAccessor) {
			element = elt;
			inferredData = new PropertyInferredData( element, rootEntity, propertyAccessor );
		}

		public AnnotatedElement element;
		public PropertyInferredData inferredData;
	}
}
