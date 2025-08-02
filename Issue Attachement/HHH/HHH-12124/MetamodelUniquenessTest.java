package hibernate.test;

import static org.junit.Assert.assertTrue;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class MetamodelUniquenessTest
{
    private static EntityManagerFactory emf;

    @BeforeClass
    public static void init()
    {
        emf = Persistence.createEntityManagerFactory("HHH-12124");
    }

    @AfterClass
    public static void finish()
    {
        if(emf != null)
        {
            emf.close();
        }
    }

    @Test
    public void managedTypesSelfConsistenceByEquivalenceTest()
    {
        // gather ManagedTypes directly from metamodel -> using equivalence (with "equals")
        Set<ManagedType<?>> managedTypes = emf.getMetamodel().getManagedTypes();

        // group ManagedTypes by javaType
        Map<Class<?>, List<ManagedType<?>>> groupingMap = managedTypes.stream()
            .collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<ManagedType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple ManagedTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate ManagedTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("multiple meta-classes found!", invalidMap.isEmpty());
    }

    @Test
    public void managedTypesFromAttributesConsistenceByEquivalenceTest()
    {
        // gather ManagedTypes directly from metamodel -> using equivalence (with "equals")
        Set<ManagedType<?>> managedTypes = new HashSet<>(emf.getMetamodel().getManagedTypes());

        // gather ManagedTypes indirectly, taking them from attributes
        emf.getMetamodel().getManagedTypes()
            .stream()
            .flatMap(x -> x.getDeclaredAttributes().stream())   // get ALL Attributes
            .flatMap(x ->                                       // extract Types from each Attribute 
            {
                if(x instanceof SingularAttribute)
                {
                    SingularAttribute<?, ?> s = (SingularAttribute<?, ?>) x;
                    return Stream.of(s.getDeclaringType(), s.getType());
                }

                if(x instanceof MapAttribute)
                {
                    MapAttribute<?, ?, ?> m = (MapAttribute<?, ?, ?>) x;
                    return Stream.of(m.getDeclaringType(), m.getKeyType(), m.getElementType());
                }

                PluralAttribute<?, ?, ?> p = ((PluralAttribute<?, ?, ?>) x);
                return Stream.of(p.getDeclaringType(), p.getElementType());
            })
            .filter(x -> x instanceof ManagedType)  // retain only ManagedTypes (discard BasicTypes)
            .distinct()                             // distinct using equivalence 
            .map(ManagedType.class::cast)           // safe-cast to ManagedType 
            .forEach(managedTypes::add);            // add them to the global set

        // group ManagedTypes by javaType
        Map<Class<?>, List<ManagedType<?>>> groupingMap = managedTypes.stream().collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<ManagedType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple ManagedTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate ManagedTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("the same javaType is represented by multiple non-equivalent ManagedTypes", invalidMap.isEmpty());
    }

    @Test
    public void managedTypesFromAttributesConsistenceByIdentityTest()
    {
        // gathering ManagedTypes directly from metamodel -> using identity (with "==")
        Set<ManagedType<?>> managedTypes = Collections.newSetFromMap(new IdentityHashMap<>());
        managedTypes.addAll(emf.getMetamodel().getManagedTypes());

        // gather ManagedTypes indirectly, taking them from attributes
        emf.getMetamodel().getManagedTypes()
            .stream()
            .flatMap(x -> x.getDeclaredAttributes().stream())   // get ALL Attributes
            .flatMap(x ->                                       // extract Types from each Attribute 
            {
                if(x instanceof SingularAttribute)
                {
                    SingularAttribute<?, ?> s = (SingularAttribute<?, ?>) x;
                    return Stream.of(s.getDeclaringType(), s.getType());
                }

                if(x instanceof MapAttribute)
                {
                    MapAttribute<?, ?, ?> m = (MapAttribute<?, ?, ?>) x;
                    return Stream.of(m.getDeclaringType(), m.getKeyType(), m.getElementType());
                }

                PluralAttribute<?, ?, ?> p = ((PluralAttribute<?, ?, ?>) x);
                return Stream.of(p.getDeclaringType(), p.getElementType());
            })
            .filter(x -> x instanceof ManagedType)  // retain only ManagedTypes (discard BasicTypes)
            .distinct()                             // distinct using equivalence 
            .map(ManagedType.class::cast)           // safe-cast to ManagedType 
            .forEach(managedTypes::add);            // add them to the global set

        // group ManagedTypes by javaType
        Map<Class<?>, List<ManagedType<?>>> groupingMap = managedTypes.stream().collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<ManagedType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple ManagedTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate ManagedTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("the same javaType is represented by multiple ManagedTypes", invalidMap.isEmpty());
    }


    @Test
    public void entityTypesFromAttributesConsistenceByEquivalenceTest()
    {
        // gather EntityType directly from metamodel -> using equivalence (with "equals")
        Set<EntityType<?>> entityTypes = new HashSet<>(emf.getMetamodel().getEntities());

        // gather ManagedTypes indirectly, taking them from attributes
        emf.getMetamodel().getManagedTypes()
            .stream()
            .flatMap(x -> x.getDeclaredAttributes().stream())   // get ALL Attributes
            .flatMap(x ->                                       // extract Types from each Attribute 
            {
                if(x instanceof SingularAttribute)
                {
                    SingularAttribute<?, ?> s = (SingularAttribute<?, ?>) x;
                    return Stream.of(s.getDeclaringType(), s.getType());
                }

                if(x instanceof MapAttribute)
                {
                    MapAttribute<?, ?, ?> m = (MapAttribute<?, ?, ?>) x;
                    return Stream.of(m.getDeclaringType(), m.getKeyType(), m.getElementType());
                }

                PluralAttribute<?, ?, ?> p = ((PluralAttribute<?, ?, ?>) x);
                return Stream.of(p.getDeclaringType(), p.getElementType());
            })
            .filter(x -> x instanceof EntityType)   // retain only EntityType (discard others)
            .distinct()                             // distinct using equivalence 
            .map(EntityType.class::cast)            // safe-cast to EntityType 
            .forEach(entityTypes::add);             // add them to the global set

        // group EntityTypes by javaType
        Map<Class<?>, List<EntityType<?>>> groupingMap = entityTypes.stream().collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<EntityType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple EntityTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate EntityTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("the same javaType is represented by multiple non-equivalent EntityTypes", invalidMap.isEmpty());
    }

    @Test
    public void entityTypesFromAttributesConsistenceByIdentityTest()
    {
        // gathering EntityTypes directly from metamodel -> using identity (with "==")
        Set<EntityType<?>> entityTypes = Collections.newSetFromMap(new IdentityHashMap<>());
        entityTypes.addAll(emf.getMetamodel().getEntities());

        // gather ManagedTypes indirectly, taking them from attributes
        emf.getMetamodel().getManagedTypes()
            .stream()
            .flatMap(x -> x.getDeclaredAttributes().stream())   // get ALL Attributes
            .flatMap(x ->                                       // extract Types from each Attribute 
            {
                if(x instanceof SingularAttribute)
                {
                    SingularAttribute<?, ?> s = (SingularAttribute<?, ?>) x;
                    return Stream.of(s.getDeclaringType(), s.getType());
                }

                if(x instanceof MapAttribute)
                {
                    MapAttribute<?, ?, ?> m = (MapAttribute<?, ?, ?>) x;
                    return Stream.of(m.getDeclaringType(), m.getKeyType(), m.getElementType());
                }

                PluralAttribute<?, ?, ?> p = ((PluralAttribute<?, ?, ?>) x);
                return Stream.of(p.getDeclaringType(), p.getElementType());
            })
            .filter(x -> x instanceof EntityType)   // retain only EntityType (discard others)
            .distinct()                             // distinct using equivalence 
            .map(EntityType.class::cast)            // safe-cast to EntityType 
            .forEach(entityTypes::add);             // add them to the global set

        // group EntityTypes by javaType
        Map<Class<?>, List<EntityType<?>>> groupingMap = entityTypes.stream().collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<EntityType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple EntityTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate EntityTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("the same javaType is represented by multiple EntityTypes", invalidMap.isEmpty());
    }

    @Test
    public void embeddableTypesFromAttributesConsistenceByEquivalenceTest()
    {
        // gather EmbeddableType directly from metamodel -> using equivalence (with "equals")
        Set<EmbeddableType<?>> embeddableTypes = new HashSet<>(emf.getMetamodel().getEmbeddables());

        // gather ManagedTypes indirectly, taking them from attributes
        emf.getMetamodel().getManagedTypes()
            .stream()
            .flatMap(x -> x.getDeclaredAttributes().stream())   // get ALL Attributes
            .flatMap(x ->                                       // extract Types from each Attribute 
            {
                if(x instanceof SingularAttribute)
                {
                    SingularAttribute<?, ?> s = (SingularAttribute<?, ?>) x;
                    return Stream.of(s.getDeclaringType(), s.getType());
                }

                if(x instanceof MapAttribute)
                {
                    MapAttribute<?, ?, ?> m = (MapAttribute<?, ?, ?>) x;
                    return Stream.of(m.getDeclaringType(), m.getKeyType(), m.getElementType());
                }

                PluralAttribute<?, ?, ?> p = ((PluralAttribute<?, ?, ?>) x);
                return Stream.of(p.getDeclaringType(), p.getElementType());
            })
            .filter(x -> x instanceof EmbeddableType)   // retain only EmbeddableType (discard others)
            .distinct()                                 // distinct using equivalence 
            .map(EmbeddableType.class::cast)            // safe-cast to EmbeddableType 
            .forEach(embeddableTypes::add);             // add them to the global set

        // group EmbeddableTypes by javaType
        Map<Class<?>, List<EmbeddableType<?>>> groupingMap = embeddableTypes.stream().collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<EmbeddableType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple EmbeddableTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate EmbeddableTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("the same javaType is represented by multiple non-equivalent EmbeddableTypes", invalidMap.isEmpty());
    }

    @Test
    public void embeddableTypesFromAttributesConsistenceByIdentityTest()
    {
        // gathering EmbeddableTypes directly from metamodel -> using identity (with "==")
        Set<EmbeddableType<?>> embeddableTypes = Collections.newSetFromMap(new IdentityHashMap<>());
        embeddableTypes.addAll(emf.getMetamodel().getEmbeddables());

        // gather ManagedTypes indirectly, taking them from attributes
        emf.getMetamodel().getManagedTypes()
            .stream()
            .flatMap(x -> x.getDeclaredAttributes().stream())   // get ALL Attributes
            .flatMap(x ->                                       // extract Types from each Attribute 
            {
                if(x instanceof SingularAttribute)
                {
                    SingularAttribute<?, ?> s = (SingularAttribute<?, ?>) x;
                    return Stream.of(s.getDeclaringType(), s.getType());
                }

                if(x instanceof MapAttribute)
                {
                    MapAttribute<?, ?, ?> m = (MapAttribute<?, ?, ?>) x;
                    return Stream.of(m.getDeclaringType(), m.getKeyType(), m.getElementType());
                }

                PluralAttribute<?, ?, ?> p = ((PluralAttribute<?, ?, ?>) x);
                return Stream.of(p.getDeclaringType(), p.getElementType());
            })
            .filter(x -> x instanceof EmbeddableType)   // retain only EmbeddableType (discard others)
            .distinct()                                 // distinct using equivalence 
            .map(EmbeddableType.class::cast)            // safe-cast to EmbeddableType 
            .forEach(embeddableTypes::add);             // add them to the global set

        // group EmbeddableTypes by javaType
        Map<Class<?>, List<EmbeddableType<?>>> groupingMap = embeddableTypes.stream().collect(Collectors.groupingBy(ManagedType::getJavaType));

        // instance the map to be populated by invalid entries
        Map<Class<?>, List<EmbeddableType<?>>> invalidMap = new HashMap<>();

        // filter groupingMap, retaining entries that have multiple EmbeddableTypes per javaType
        groupingMap.entrySet()
            .stream()
            .filter(x -> x.getValue().size() != 1)
            .forEach(x -> invalidMap.put(x.getKey(), x.getValue()));

        // debug print to err entries that contains duplicate EmbeddableTypes
        invalidMap.forEach((k, v) -> System.err.printf("%s -> %d\r\n", k, v.size()));

        // fail test if there's a duplicate
        assertTrue("the same javaType is represented by multiple EmbeddableTypes", invalidMap.isEmpty());
    }
}
