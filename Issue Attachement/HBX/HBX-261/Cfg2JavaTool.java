/*
 * Created on 02-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.StringHelper;

/**
 * Helper methods for javacode generation.
 * 
 * TODO: this class contains alot of helper methods. Consider wrapping PersistentClass and Component instead.
 * @author max
 *
 */
public class Cfg2JavaTool {

    private static final Log log = LogFactory.getLog(Cfg2JavaTool.class);
	
	public String stripPackage(String type, PersistentClass clazz) {
		String packageName = getPackageName(clazz);
		return stripPackage(type, packageName);
	}

	public String stripPackage(String type, Component clazz) {
		String packageName = getPackageName(clazz);
		return stripPackage(type, packageName);
	}
	
	private String stripPackage(String type, String packageName) {
		String pkg = StringHelper.qualifier(type);
		if ( pkg.equals( packageName ) || pkg.equals("java.lang") || pkg.equals("java.util") ) {
			return StringHelper.unqualify(type);
		}
		else {
			return type;
		}
	}
    
	/** 
	 * Returns "package packagename;" where packagename is either the declared packagename,
	 * or the one provide via meta attribute "generated-class".
	 * 
	 * Returns "// default package" if no package declarition available.
	 *  
	 * @param cm
	 * @return
	 */
	public String getPackageDeclaration(PersistentClass clazz) {
		String pkgName = getPackageName(clazz);
		return getPackageDeclaration(pkgName);	
	}
	
	/**
	 * @param pkgName
	 * @return
	 */
	private String getPackageDeclaration(String pkgName) {
		if (pkgName!=null && pkgName.trim().length()!=0 ) {
			return "package " + pkgName + ";";
		   } else {        
		   	return "// default package";
		  }
	}

	public String getPackageDeclaration(Component clazz) {
		String pkgName = getPackageName(clazz);
		return getPackageDeclaration(pkgName);	
	  }
	
	/**
	 * Return packagename for a PersistentClass.
	 * 
	 * @param classMapping
	 * @return
	 */
	public String getPackageName(PersistentClass classMapping) {
		String generatedClass = getMetaAsString(classMapping, "generated-class").trim();
		if(StringHelper.isEmpty(generatedClass)) {
			generatedClass = classMapping.getClassName();
		}
		return StringHelper.qualifier(generatedClass);
	}

	public String getPackageName(Component classMapping) {
		String generatedClass = getMetaAsString(classMapping, "generated-class").trim();
		if(StringHelper.isEmpty(generatedClass)) {
			generatedClass = classMapping.getComponentClassName();
		}
		return StringHelper.qualifier(classMapping.getComponentClassName());
	}
	
	/**
	 * Returns all meta items as one large string.
	 * 
	 * @param string
	 * @return String
	 */
	public String getMetaAsString(MetaAttributable pc, String attribute) {
		MetaAttribute c = pc.getMetaAttribute(attribute);

		return MetaAttributeHelper.getMetaAsString(c);
	}
	
	public boolean hasMetaAttribute(MetaAttributable pc, String attribute) {
		return pc.getMetaAttribute(attribute)!=null;		
	}
		
	public String getMetaAsString(MetaAttributable pc, String attribute, String seperator) {
		return MetaAttributeHelper.getMetaAsString(pc.getMetaAttribute(attribute), seperator);
	}
		
    public boolean getMetaAsBool(MetaAttributable property, String attribute) {
        return getMetaAsBool(property, attribute, false);
    }

    public boolean getMetaAsBool(MetaAttributable pc, String attribute, boolean defaultValue) {
        return MetaAttributeHelper.getMetaAsBool(pc.getMetaAttribute(attribute), defaultValue);
    }
	
	
	

	public String getFieldJavaDoc(Property property, int indent) {
		MetaAttribute c = property.getMetaAttribute("field-description");
		if(c==null) {
			return toJavaDoc("",indent);
		} else {
			return toJavaDoc(getMetaAsString(property, "field-description"),indent);	
		}
	}
	
	// todo: getClassJavaDoc
	public String getClassJavaDoc(MetaAttributable classMapping, String fallback, int indent) {
		MetaAttribute c = classMapping.getMetaAttribute("class-description");
		if(c==null) {
			return toJavaDoc(fallback,indent);
		} else {
			return toJavaDoc(getMetaAsString(classMapping, "class-description"),indent);	
		}
	}
	
	/**
	 * Convert string into something that can be rendered nicely into a javadoc
	 * comment.
	 * Prefix each line with a star ('*').
	 * @param string
	 */
	public String toJavaDoc(String string, int indent) {
	    StringBuffer result = new StringBuffer();
	    
	    if(string!=null) {
	        String[] lines = StringUtils.split(string, "\n\r\f");
	        for (int i = 0; i < lines.length; i++) {
	            String docline = " * " + lines[i];
	            if(i<lines.length-1) docline += "\n";
	            result.append(StringUtils.leftPad(docline, docline.length() + indent));
	        }
	    }
	    
	    return result.toString();
	}

	public String getClassModifiers(MetaAttributable pc) {
		String classModifiers = null;
		
		// Get scope (backwards compatibility)
		if(pc.getMetaAttribute("scope-class")!=null) {
			classModifiers = getMetaAsString(pc,"scope-class").trim();
		}
		
		// Get modifiers
		if(pc.getMetaAttribute("class-modifier")!=null) {
			classModifiers = getMetaAsString(pc,"class-modifier").trim();
		}
		return classModifiers==null?"public":classModifiers;
	}

	/**
	 * 
	 * @param pc
	 * @return "class" or "interface" dependent on wether meta attribute "interface" is set.  
	 */
	public String getDeclarationType(MetaAttributable pc) {
		boolean isInterface = isInterface(pc);
		if(isInterface) {
			return "interface";
		} else {
			return "class";
		}
	}
		
	/**
	 * @param pc
	 * @return
	 */
	public boolean isInterface(MetaAttributable pc) {
		return getMetaAsBool(pc, "interface");
	}

	/**
	 * 
	 * @param pc
	 * @return unqualified classname for this class (can be changed by meta attribute "generated-class")
	 */
	public String getDeclarationName(PersistentClass pc) {
		return StringHelper.unqualify(getQualifiedDeclarationName(pc));
	}
	
	public String getDeclarationName(Component pc) {
		return StringHelper.unqualify(getQualifiedDeclarationName(pc));
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public String getQualifiedDeclarationName(PersistentClass pc) {
		String generatedName = getMetaAsString(pc, "generated-class");
		if(generatedName==null || generatedName.trim().length()==0) {
			generatedName = pc.getClassName();
		} 
		//TODO: handle user wanting to change package
		return generatedName;
	}
	
	/**
	 * @param component
	 * @return
	 */
	public String getQualifiedDeclarationName(Component component) {
		String generatedName = getMetaAsString(component, "generated-class");
		if(generatedName==null || generatedName.trim().length()==0) {
			generatedName = component.getComponentClassName();
		} 
		
		generatedName  = generatedName.replace('$', '.');
		String qualifier = StringHelper.qualifier(component.getComponentClassName());
		if("".equals(qualifier)) {
			return qualifier + "." + generatedName;
		} else {
			return generatedName;
		}	
	}
	
	public String getExtendsDeclaration(PersistentClass pc) {
		String extendz = getExtends(pc);
		if(extendz==null || extendz.trim().length()==0) {
			return "";
		} else {
			return "extends " + extendz;
		}
	}

	public String getExtendsDeclaration(Component pc) {
		String extendz = getExtends(pc);
		if(extendz==null || extendz.trim().length()==0) {
			return "";
		} else {
			return "extends " + extendz;
		}
	}

	public String getImplementsDeclaration(PersistentClass pc) {
		String implementz = getImplements(pc);
		if(implementz==null || implementz.trim().length()==0) {
			return "";
		} else {
			return "implements " + implementz;
		}
	}
	
	public String getImplementsDeclaration(Component pc) {
		String implementz = getImplements(pc);
		if(implementz==null || implementz.trim().length()==0) {
			return "";
		} else {
			return "implements " + implementz;
		}
	}
	
	/**
	 * 
	 * @param pc
	 * @return whatever the class (or interface) extends (null if it does not extend anything)
	 */
	public String getExtends(PersistentClass pc) {
		String extendz = "";
		
		if(isInterface(pc)) {
			if(pc.getSuperclass()!= null) {
				extendz = pc.getSuperclass().getClassName();					
			}
			if(pc.getMetaAttribute("extends")!=null) {
				if(!"".equals(extendz)) {
					extendz += ",";
				}
				extendz += getMetaAsString(pc, "extends",",");
			}
		} else if(pc.getSuperclass()!=null) {
			if(isInterface(pc.getSuperclass())) {
				// class cannot extend it's superclass because the superclass is marked as an interface
			} else {
				extendz = pc.getSuperclass().getClassName();
			}
		} else if (pc.getMetaAttribute("extends")!=null) {
			extendz = getMetaAsString(pc, "extends",",");
		}
		
		return "".equals(extendz)?null:extendz;
	}

	public String getExtends(Component pc) {
		String extendz = "";
		
		if(isInterface(pc)) {
			if(pc.getMetaAttribute("extends")!=null) {
				if(!"".equals(extendz)) {
					extendz += ",";
				}
				extendz += getMetaAsString(pc, "extends",",");
			}
		} else if (pc.getMetaAttribute("extends")!=null) {
			extendz = getMetaAsString(pc, "extends",",");
		}
		
		return "".equals(extendz)?null:extendz;
	}

	/**
	 * @param sub
	 * @return
	 */
	public String getImplements(PersistentClass pc) {
		List interfaces = new ArrayList();
		
		//			implement proxy, but NOT if the proxy is the class it self!
		if (pc.getProxyInterfaceName()!=null &&	( !pc.getProxyInterfaceName().equals( pc.getClassName() ) )) {
			interfaces.add(pc.getProxyInterfaceName());
		}
		
		if(!isInterface(pc)) {
			if(pc.getSuperclass()!=null && isInterface(pc.getSuperclass())) {
				interfaces.add(pc.getSuperclass().getClassName());
			}
			if (pc.getMetaAttribute("implements")!=null) {
				interfaces.addAll(pc.getMetaAttribute("implements").getValues());
			}
			interfaces.add(Serializable.class.getName()); // TODO: is this "nice" ? shouldn't it be a user choice ?
		} else { 
			// interfaces can't implement suff
		} 
		
		
		if(interfaces.size()>0) {
			StringBuffer sbuf = new StringBuffer();
			for (Iterator iter = interfaces.iterator(); iter.hasNext();) {				
				//sbuf.append(JavaTool.shortenType(iter.next().toString(), pc.getImports()));
				sbuf.append(iter.next());
				if(iter.hasNext()) sbuf.append(",");
			} 
			return sbuf.toString();
		} else {
			return null;
		}

	}

	public String getImplements(Component pc) {
		List interfaces = new ArrayList();
		
		//	implement proxy, but NOT if the proxy is the class it self!
		if(!isInterface(pc)) {
			if (pc.getMetaAttribute("implements")!=null) {
				interfaces.addAll(pc.getMetaAttribute("implements").getValues());
			}
			interfaces.add(Serializable.class.getName()); // TODO: is this "nice" ? shouldn't it be a user choice ?
		} else { 
			// interfaces can't implement suff
		} 
		
		
		if(interfaces.size()>0) {
			StringBuffer sbuf = new StringBuffer();
			for (Iterator iter = interfaces.iterator(); iter.hasNext();) {				
				//sbuf.append(JavaTool.shortenType(iter.next().toString(), pc.getImports()));
				sbuf.append(iter.next());
				if(iter.hasNext()) sbuf.append(",");
			} 
			return sbuf.toString();
		} else {
			return null;
		}

	}

	private String toName(Class c) {
		
		if(c.isArray()) {
			Class a = c.getComponentType();
			
			return a.getName() + "[]";
		} else {
			return c.getName();
		}
	}

	/**
	 * Method that tries to get the typename for a property WITHOUT reflection.
	 * TODO: rename to getJavaTypeName
	 * @param p
	 * @return
	 */
	public String getTypeName(Property p) {
		return getTypeName(p, false);
	}

	/**
	 * Returns the typename for a property, using generics if this is a Set type and useGenerics is set to true.
	 */
	public String getTypeName (Property p, boolean useGenerics)
	{
		String overrideType = getMetaAsString(p, "property-type");
		if(!StringHelper.isEmpty(overrideType)) {
			return overrideType;
		} else {
			String rawType = getRawTypeName(p, useGenerics);
			
			rawType = rawType.replace('$', '.');
			
			return rawType;
		}
	}
	
	/**
	 * @param p
	 * @return
	 */
	private String getRawTypeName(Property p, boolean useGenerics) {
		Value v = null;
    	Type t = null;
    	try {
    		v = p.getValue();
    		
    		if(v instanceof Array) { // array has a string rep.inside. 
    			Array a = (Array)v;
    			if(a.isPrimitiveArray()) {
    				return toName(v.getType().getReturnedClass());
    			} else {
    				return ((Array)v).getElementClassName() + "[]";
    			}
    		}
    		
    		if(v instanceof Component) { // same for component.
    			return ((Component)v).getComponentClassName();
    		}
    		
    		if(v instanceof ToOne) {
    			return ((ToOne)v).getReferencedEntityName();
    		}
    		
        	if (useGenerics)
        	{
    			if (v instanceof Collection)
    			{
    				Collection collection = (Collection) v;
    				OneToMany oneToMany = (OneToMany) collection.getElement();
    				String entityType = oneToMany.getAssociatedClass().getClassName();
    				
    				return toName(p.getType().getReturnedClass()) + "<" + entityType + ">";
    			}
        	}
        	
			t = p.getType();
			 
//			use native types when the value is not nullable
			if (v.isSimpleValue() && (!v.isNullable()) && (t instanceof PrimitiveType)) {
				//start of patch
				String typeName = ((SimpleValue) v).getTypeName();
				if (typeName.equals("int") || 
					typeName.equals("long") ||
					typeName.equals("double")
						)
					return toName(((PrimitiveType)t).getPrimitiveClass());
				else
					return toName(t.getReturnedClass());
				//end of patch
			}
    		
    		return toName(t.getReturnedClass());
    	} catch(Exception e) {
            String msg = "Could not resolve type without exception for " + p + " Value: "+ v + " Type: " + t;
            if(v!=null && v instanceof SimpleValue) {
                String typename = ((SimpleValue)v).getTypeName();
                log.warn(msg + ". Falling back to typename: " + typename );
                return typename;
            } else {
                throw new ExporterException(msg);
            }
    	}
	}

	/**
	 * 
	 * @param Iterator on Property elements.
	 * @return "String name, int number, ..." for a property list, usable for method declarations.
	 * 
	 * TODO: handle this in a template ?
	 */
	public String asParameterList(Iterator fields) {
		StringBuffer buf = new StringBuffer();
		while(fields.hasNext()) {
			Property field = (Property) fields.next();
			buf.append(getTypeName(field) + " " + field.getName());
			if(fields.hasNext()) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}
	
	/**
	 * @param Iterator on Property elements.
	 * @return "name, number, ..." for a property list, usable for method calls.
	 * 
	 * TODO: handle this in a template ?
	 */
	public String asArgumentList(Iterator fields) {
		StringBuffer buf = new StringBuffer();
		while(fields.hasNext()) {
			Property field = (Property) fields.next();
			buf.append(field.getName());
			if(fields.hasNext()) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}

	/**
	 * @return
	 * 
	 */
	public String asParameterList(List fields) {
		return asParameterList(fields.iterator());
	}

	public String asArgumentList(List fields) {
		return asArgumentList(fields.iterator());
	}
	
	/**
	 * @param pc
	 * @return
	 */
	public List getPropertyClosureForFullConstructor(PersistentClass pc) {
		List result = getPropertyClosureForSuperclassFullConstructor(pc);
		result.addAll(getPropertiesForFullConstructor(pc));
		return result;
	}

	/**
	 * @param pc
	 * @return
	 */
	public List getPropertiesForFullConstructor(PersistentClass pc) {
		List result = new ArrayList();
		
		// only include identifier for the root class.
		if(pc.getSuperclass()==null && pc.hasIdentifierProperty()) result.add(pc.getIdentifierProperty());
		
		for(Iterator myFields = pc.getPropertyIterator(); myFields.hasNext();) {
			Property field = (Property) myFields.next();
			// TODO: if(!field.isGenerated())) {
				result.add(field);
			//}
		}       
		
		return result;
		
	}

	/**
	 * @param pc
	 * @return
	 */
	public List getPropertyClosureForSuperclassFullConstructor(PersistentClass pc) {
		List result = new ArrayList();
		if(pc.getSuperclass()!=null) {
			// The correct sequence is vital here, as the subclass should be
			// able to invoke the fullconstructor based on the sequence returned
			// by this method!
			result.addAll(getPropertyClosureForSuperclassFullConstructor(pc.getSuperclass()));
			result.addAll(getPropertiesForFullConstructor(pc.getSuperclass()));
		}
		
		return result;
	}

	/**
	 * 
	 * @param pc
	 * @return list containing all properties (including id and version if applicable)
	 */
	public Iterator getAllPropertiesIterator(PersistentClass pc) {
		List properties = new ArrayList();
		List iterators = new ArrayList();
		if (pc.getSuperclass()==null) {
			if(pc.hasIdentifierProperty()) {
				properties.add(pc.getIdentifierProperty());
			} else if (pc.hasEmbeddedIdentifier()) {
				Component embeddedComponent = (Component) pc.getIdentifier();
				iterators.add(embeddedComponent.getPropertyIterator()); 				
			}
			/*if(pc.isVersioned()) { // version is already in property set
				properties.add(pc.getVersion());
			}*/
		}
		
		iterators.add(properties.iterator());
		iterators.add(pc.getPropertyIterator());
		Iterator[] it = (Iterator[]) iterators.toArray(new Iterator[iterators.size()]);
		return new JoinedIterator(it);
	}
	
	/**
	 * 
	 * @param pc
	 * @return list containing all properties (including id and version if applicable)
	 */
	public Iterator getAllPropertiesIterator(Component pc) {
		return pc.getPropertyIterator();
	}
	
	public String getFieldModifiers(Property property) {
		return getModifiers(property, "scope-field", "private");
	}
	
	public String getPropertyGetModifiers(Property property) {
		return getModifiers(property, "scope-get", "public");
	}
	
	public String getPropertySetModifiers(Property property) {
		return getModifiers(property, "scope-set", "public");
	}
	
	/**
	 * @param property
	 * @param defaultModifiers
	 * @param modifiername TODO
	 * @return
	 */
	private String getModifiers(Property property, String modifiername, String defaultModifiers) {
		MetaAttribute override =property.getMetaAttribute(modifiername);
		if(override!=null) {
			return MetaAttributeHelper.getMetaAsString(override);
		} else {
			return defaultModifiers;
		}
	}

	/**
	 * Method getGetterSignature.
	 * @return String
	 */
	public String getGetterSignature(Property p) {
		String prefix = getTypeName(p).equals("boolean") ? "is" : "get";
		return prefix + beanCapitalize(p.getName());
	}

	/**
	 * 
	 * @param p
	 * @return  foo -> Foo, FOo -> FOo
	 */
	public String getPropertyName(Property p) {
		return beanCapitalize(p.getName());
	}
	
	/**
     * foo -> Foo
     * FOo -> FOo
     * 
     * @param name2
     * @return
     */
    private String beanCapitalize(String fieldname) {
            if (fieldname == null || fieldname.length() == 0) {
                return fieldname;
            }
            
            if (fieldname.length() > 1 && Character.isUpperCase(fieldname.charAt(1)) &&
                    Character.isUpperCase(fieldname.charAt(0))){
                return fieldname;
            }
            char chars[] = fieldname.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);      
    }


    public boolean isComponent(Property property) {
        Value value = property.getValue();
        if(value != null && value instanceof Component) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isComponent(Component c) {
        return true;
    }
    
    public boolean isComponent(PersistentClass c) {
        return false;
    }

    public boolean needsEqualsHashCode(Component c) {
        Iterator iter = getAllPropertiesIterator(c);
        return needsEqualsHashCode(iter);
    }

    /**
     * @param iter
     * @return
     */
    private boolean needsEqualsHashCode(Iterator iter) {
        while (iter.hasNext()) {
            Property element = (Property) iter.next();
            if(getMetaAsBool(element,"use-in-equals")) {
                return true;
            }
        }
        return false;
    }
    
    public boolean needsEqualsHashCode(PersistentClass c) {
        Iterator iter = getAllPropertiesIterator(c);
        return needsEqualsHashCode(iter);
    }
    
    public String generateEquals(PersistentClass c, String thisName, String otherName) {
        Iterator allPropertiesIterator = getAllPropertiesIterator(c);
        return generateEquals(thisName, otherName, allPropertiesIterator);
    }

    public String generateEquals(Component c, String thisName, String otherName) {
        Iterator allPropertiesIterator = getAllPropertiesIterator(c);
        return generateEquals(thisName, otherName, allPropertiesIterator);
    }
    
    public String generateHashCode(Property property, String result, String thisName) {
        StringBuffer buf = new StringBuffer();
        if(getMetaAsBool(property, "use-in-equals")) {
            String hashCode = "";
            String cast = "";
            if(!PRIMITIVES.contains(getTypeName(property))) {
				hashCode +=  ".hashCode()";
            } else {
                cast = "(int) ";
            }
            buf.append(result)
            	.append(" = 37 * ")
            	.append(result)
            	.append(" + ");
			if ( property.isOptional() ) {
				buf.append("( ")
					.append( getGetterSignature(property) )
					.append("() == null ? 0 : ");
			}
            buf.append(cast)
            	.append(thisName)
            	.append(".")
            	.append( getGetterSignature(property) )
            	.append("()")
            	.append(hashCode);
			if ( property.isOptional() ) buf.append(" )");
        }
        return buf.toString();        
    }
    
    /**
     * @param thisName
     * @param otherName
     * @param allPropertiesIterator
     * @return 
     */
    private String generateEquals(String thisName, String otherName, Iterator allPropertiesIterator) {
        StringBuffer buf = new StringBuffer();
        while (allPropertiesIterator.hasNext()) {
            Property property = (Property) allPropertiesIterator.next();
            if(getMetaAsBool(property, "use-in-equals")) {
                if(buf.length()>0) buf.append("\n && ");
                buf.append(generateEquals(getTypeName(property), thisName + "." + getGetterSignature(property) + "()", otherName + "." + getGetterSignature(property) + "()"));
            }
        }
        
        if(buf.length()==0) {
            return "false"; 
        } else {
            return buf.toString();
        }
    }

    static final Set PRIMITIVES = new HashSet();
    static {
        PRIMITIVES.add("byte");
        PRIMITIVES.add("short");
        PRIMITIVES.add("int");
        PRIMITIVES.add("long");     
        PRIMITIVES.add("float");
        PRIMITIVES.add("double");
        PRIMITIVES.add("char");
        PRIMITIVES.add("boolean");
    }
    
    /**
     * @param string
     * @param string2
     * @return
     */
    private String generateEquals(String typeName, String lh, String rh) {
        if(PRIMITIVES.contains(typeName)) {
            return "(" + lh + "==" + rh + ")";  
        } else {
            return "(" + lh + "==" + rh + ") || ( " + lh + "!=null && " + rh + "!=null && " + lh + ".equals(" + rh + ") )";    
        }
    }
    
    public String getExtraCode(PersistentClass clazz) {
		return getMetaAsString(clazz, "class-code", "\n");
    }
    
	public String getExtraCode(Component clazz) {
		return getMetaAsString(clazz, "class-code", "\n");
    }
	
	public void addExtraImports(PersistentClass pc, Context ctx) {
		MetaAttribute metaAttribute = pc.getMetaAttribute("extra-import");
		if(metaAttribute!=null) {
			addImports(ctx, metaAttribute.getValues());
		} else {
			ctx.put("classimports", "");
		}
	}

	public void addExtraImports(Component pc, Context ctx) {
		MetaAttribute metaAttribute = pc.getMetaAttribute("extra-import");
		if(metaAttribute!=null) {
			addImports(ctx, metaAttribute.getValues());
		}
	}
	private void addImports(Context ctx, List imports) {
		StringBuffer buf = new StringBuffer();
		if (imports != null) {			
		    for (Iterator it = imports.iterator(); it.hasNext(); ) {
			    String cname = it.next().toString();
			    buf.append("import " + cname + ";\n");
		    }		    
	    }
		ctx.put("classimports", buf.toString());
	}
	
	
    
}
