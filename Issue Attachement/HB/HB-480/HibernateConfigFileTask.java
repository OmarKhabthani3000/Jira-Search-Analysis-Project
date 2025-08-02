package no.nr.ant;

/**
 * HibernateConfigFileTask is a Ant task for creating the hibernate
 * configuration file (usually named hibernate.cgf.xml). This can be usefull if you use middlegen to generate
 * mapping files and then hbm2java to generate java source files (based on the mapping files). With this task there is
 * no need to manually edit the hibernate configuration file.
 *
 * The DTD is located at:
 * http://hibernate.sourceforge.net/hibernate-configuration-2.0.dtd
 *
 * Note 1: this class has not been tested. Use at your own risk. There are support for jcs-class-cache and
 * jcs-collection-cache but I have never tested them.
 *
 * Note 2: Only the "resource" attribute will be used when writing out mappings (no support for file or jar).
 *
 * Example of usage:
 * &lt;target name="hibernate-config"&gt;
 *        &lt;taskdef name="hibconfig" classname="no.nr.ant.HibernateConfigFileTask" classpath="yourclasspath" /&gt;
 *        &lt;hibconfig dest="hibernate.cfg.xml"&gt;
 *           &lt;sfproperty name="connection.datasource" value="java:comp/env/jdbc/hibernate"/&gt;
 *           &lt;sfproperty name="show_sql" value="false"/&gt;
 *           &lt;sfproperty name="dialect" value="net.sf.hibernate.dialect.SybaseDialect"/&gt;
 *           &lt;mapping dir="mapping_folder"/&gt;
 *        &lt;/hibconfig&gt;
 *   &lt;/target&gt;
 *
 *
 * @author Per Thomas Jahr, perja at nr.no, 18.11.2003
 * @version $Id$
 */

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.util.Vector;

public class HibernateConfigFileTask extends Task {
    // the folder for the hibernate mapping files (fileset)
    private Vector mappingFilesets = new Vector();

    // the config file that will be created by this task
    private File dest;

    // session factory properties
    private Vector properties = new Vector();

    // name of the session factory
    private String factoryName;

    // jcs-class-cache settings
    private Vector jcsClassSettings = new Vector();

    // jcs-collections-cache settings
    private Vector jcsCollectionSettings = new Vector();

    private void checkParams() throws BuildException {
        if (dest == null) {
            throw new BuildException("You must specify a destination file for the hibernate configuration file.");
        } else if (mappingFilesets.size() == 0) {
            // we must have one or more mapping elements
            throw new BuildException("There must be at least one mapping element.");
        } else if (properties.size() > 0) {
            // all properties must have a name and a value
            for (int i = 0; i < properties.size(); i++) {
                Parameter parameter = (Parameter) properties.elementAt(i);
                if ((parameter.getName() == null) || (parameter.getValue() == null)) {
                    throw new BuildException("A property must have a name and a value.");
                }
            }
        } else if (jcsClassSettings.size() > 0) {
            // the classname (class in the DTD) setting is required
            for (int i = 0; i < jcsClassSettings.size(); i++) {
                JcsClassCache jcc = (JcsClassCache) jcsClassSettings.elementAt(i);
                if (jcc.getClassname() == null) {
                    throw new BuildException("The jcsclasscache must have the classname attribute.");
                }
            }
        } else if (jcsCollectionSettings.size() > 0) {
            // the collection attribute is required
            for (int i = 0; i < jcsCollectionSettings.size(); i++) {
                JcsCollectionCache jcc = (JcsCollectionCache) jcsCollectionSettings.elementAt(i);
                if (jcc.getCollection() == null) {
                    throw new BuildException("The jcscollectioncache must have the collection attribute.");
                }
            }
        }
    }

    public void execute() throws BuildException {
        checkParams();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("hibernate-configuration");
            doc.appendChild(root);
            Element sessionRoot = doc.createElement("session-factory");
            if (factoryName != null) {
                sessionRoot.setAttribute("name", factoryName);
            }
            root.appendChild(sessionRoot);

            // add session factory properties
            for (int i = 0; i < properties.size(); i++) {
                Parameter parameter = (Parameter) properties.elementAt(i);
                Element element = doc.createElement("property");
                element.setAttribute("name", parameter.getName());
                Text textNode = doc.createTextNode(parameter.getValue());
                element.appendChild(textNode);
                sessionRoot.appendChild(element);
            }

            // add mappings
            for (int i = 0; i < mappingFilesets.size(); i++) {
                FileSet fileSet = (FileSet) mappingFilesets.elementAt(i);
                String files[] = fileSet.getDirectoryScanner(this.getProject()).getIncludedFiles();
                for (int j = 0; j < files.length; j++) {
                    String file = files[j];
                    Element mapping = doc.createElement("mapping");
                    mapping.setAttribute("resource", file.replace('\\', '/'));
                    sessionRoot.appendChild(mapping);
                }
            }

            // add jcs-class-cache settings
            for (int i = 0; i < jcsClassSettings.size(); i++) {
                JcsClassCache jcc = (JcsClassCache) jcsClassSettings.elementAt(i);
                Element element = doc.createElement("jcs-class-cache");
                element.setAttribute("class", jcc.getClassname());
                element.setAttribute("region", jcc.getRegion());
                element.setAttribute("usage", jcc.getUsage());
                sessionRoot.appendChild(element);
            }

            // add jcs-collections-cache settings
            for (int i = 0; i < jcsCollectionSettings.size(); i++) {
                JcsCollectionCache jcc = (JcsCollectionCache) jcsCollectionSettings.elementAt(i);
                Element element = doc.createElement("jcs-collection-cache");
                element.setAttribute("collection", jcc.getCollection());
                element.setAttribute("region", jcc.getRegion());
                element.setAttribute("usage", jcc.getUsage());
                sessionRoot.appendChild(element);
            }

            // write dom to file
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Configuration DTD//EN");
            aTransformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-configuration-2.0.dtd");
            aTransformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            Source src = new DOMSource(doc);
            Result result = new StreamResult(dest);
            aTransformer.transform(src, result);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError factoryConfigurationError) {
            factoryConfigurationError.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public void addSFProperty(Parameter param) {
        properties.add(param);
    }

    public void addMapping(FileSet fileset) {
        mappingFilesets.add(fileset);
    }

    public void addJcsClassCache(JcsClassCache jcc) {
        jcsClassSettings.add(jcc);
    }

    public void addJcsCollectionCache(JcsCollectionCache jcc) {
        jcsCollectionSettings.add(jcc);
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public static class Usage extends EnumeratedAttribute {
        public String[] getValues() {
            return new String[]{"read-only", "read-write", "nonstrict-read-write"};
        }
    }
}
