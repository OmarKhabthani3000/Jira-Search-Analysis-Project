package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Component;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;


public class GenericExporter extends AbstractExporter {

	String templateName;
	private String filePattern;
	
	/**
	 * Normal constructor
	 * @param cfg the hibernate configuration
	 * @param outputdir 
	 */
	public GenericExporter(Configuration cfg, File outputdir) {
		super(cfg,outputdir);
	}

	/**
	 * Default constructor
	 *
	 */
	public GenericExporter() {
	}
	
	/**
	 * 
	 * @return the filename of the template to be used,
	 * relative to one the template paths
	 * @see org.hibernate.tool.hbm2x.AbstractExporter.getTemplatePaths()
	 */
	public String getTemplateName() {
		return templateName;
	}
	
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
		
	/**
	 * Launches the file generation. If "{class-name}" appears in filePattern,
	 * a file is produced for each hibernate class mapping.
	 */
	protected void doStart() {
				
		if(filePattern==null) throw new ExporterException("File pattern not set on GenericExporter");
		if(templateName==null) throw new ExporterException("Template pattern not set on GenericExporter");
		
		if(filePattern.indexOf("{class-name}")>=0) {				
			exportClasses();
		} else {
			TemplateProducer producer = new TemplateProducer(getTemplateHelper(),getArtifactCollector());
			producer.produce(new HashMap(), getTemplateName(), new File(getOutputDirectory(),filePattern), templateName);
		}
	}

	private void exportClasses() {
		Map components = new HashMap();
		
		Iterator iterator = getCfg2JavaTool().getPOJOIterator(getConfiguration().getClassMappings());
		Map additionalContext = new HashMap();
		while ( iterator.hasNext() ) {					
			POJOClass element = (POJOClass) iterator.next();
			ConfigurationNavigator.collectComponents(components, element);						
			exportPersistentClass( additionalContext, element );
		}
				
		iterator = components.values().iterator();
		while ( iterator.hasNext() ) {					
			Component component = (Component) iterator.next();
			ComponentPOJOClass element = new ComponentPOJOClass(component,getCfg2JavaTool());
			exportComponent( additionalContext, element );
		}
				        
	}

	protected void exportComponent(Map additionalContext, POJOClass element) {
		exportPOJO(additionalContext, element);		
	}

	protected void exportPersistentClass(Map additionalContext, POJOClass element) {
		exportPOJO(additionalContext, element);		
	}

	protected void exportPOJO(Map additionalContext, POJOClass element) {
		TemplateProducer producer = new TemplateProducer(getTemplateHelper(),getArtifactCollector());					
		additionalContext.put("pojo", element);
		additionalContext.put("clazz", element.getDecoratedObject());
		String filename = resolveFilename( element );
		if(filename.endsWith(".java") && filename.indexOf('$')>=0) {
			log.warn("Filename for " + getClassNameForFile( element ) + " contains a $. Innerclass generation is not supported.");
		}
		producer.produce(additionalContext, getTemplateName(), new File(getOutputDirectory(),filename), templateName);
	}

	protected String resolveFilename(POJOClass element) {
		String filename = StringHelper.replace(filePattern, "{class-name}", getClassNameForFile( element )); 
		String packageLocation = StringHelper.replace(getPackageNameForFile( element ),".", "/");
		if(StringHelper.isEmpty(packageLocation)) {
			packageLocation = "."; // done to ensure default package classes doesn't end up in the root of the filesystem when outputdir=""
		}
		filename = StringHelper.replace(filename, "{package-name}", packageLocation);
		return filename;
	}

	protected String getPackageNameForFile(POJOClass element) {
		return element.getPackageName(); 
	}

	protected String getClassNameForFile(POJOClass element) {
		return element.getDeclarationName();
	}

	/**
	 * Sets the filename to use for generated file(s). <br/> 
	 * - If "{class-name}" appears in filePattern,
	 *   a file is produced for each hibernate class mapping.<br/>   
	 * - If {package-name} appears, it is replaced with
	 *   the unqualified name of the active class <br/>
	 *    
	 * @param filePattern the filename string
	 */
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
		// TODO: better definition of the "{package-name}" replacement  
	}

	/**
	 * @return the filename pattern used for generated file(s)
	 * @see getFilePattern()
	 */
	public String getFilePattern() {
		return filePattern;
	}

}
/* Latest Changes (Michelle Baert)
 * - suppressed exporterClass property, not used elsewhere in project
 *   (apart from HibernateExt\tools\src\test\org\hibernate\tool\hbm2x\GenericExporterTest.java)
 * - suppressed start() method now useless. Superclass's method will be called instead.
 * - added getter for filePattern, needed by custom subclasses.
 * - added some javadoc: need to be clear for subclasses writers  
 */ 
