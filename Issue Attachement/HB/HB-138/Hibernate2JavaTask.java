package org.apache.tools.ant.taskdefs.optional.hibernate;

// * User: GBegley
// * Date: Jun 21, 2003
// * Time: 1:42:49 AM

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import org.apache.log4j.Logger;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * todo add javadoc for Hibernate2JavaTask
 */
public class Hibernate2JavaTask extends Task {

   private static final String TARGET_CLASS = "net.sf.hibernate.tool.hbm2java";

   private Path compileClasspath;
   private File destDir;

   public Hibernate2JavaTask() {
   }

   /**
    * Set the classpath to be used for this compilation.
    *
    * @param classpath an Ant Path object containing the compilation classpath.
    */
   public void setClasspath(Path classpath) {
       if (compileClasspath == null) {
           compileClasspath = classpath;
       } else {
           compileClasspath.append(classpath);
       }
   }

   /** Gets the classpath to be used for this compilation. */
   public Path getClasspath() {
       return compileClasspath;
   }

   /**
    * Adds a path to the classpath.
    */
   public Path createClasspath() {
       if (compileClasspath == null) {
           compileClasspath = new Path(getProject());
       }
       return compileClasspath.createPath();
   }

   /**
    * Adds a reference to a classpath defined elsewhere.
    */
   public void setClasspathRef(Reference r) {
       createClasspath().setRefid(r);
   }




   private Vector filesets = new java.util.Vector();

   /**
    * Sets the binary output directory.
    *
    * @param binDirectory directory
    */
   public void setDestDir(File binDirectory) {
      this.destDir = binDirectory;
   }

   /**
    * Adds a set of files to translate.
    */
   public void addFileset(FileSet set) {
      filesets.addElement(set);
   }

   private List getTargetFiles( ) {
      List l = new java.util.ArrayList();
      // deal with the filesets
      for (int i = 0; i < filesets.size(); i++) {
         FileSet fs = (FileSet) filesets.elementAt(i);
         File parent = fs.getDir( getProject() );
         DirectoryScanner ds = fs.getDirectoryScanner(getProject());
         String[] files = ds.getIncludedFiles();
         for (int j = 0; j < files.length; j++) {
            int dot = files[j].indexOf(".hbm.xml");
            if (dot<=0) continue;
            String targetName = files[j].substring(0,dot)+".java";
            File srcFile = new File( parent, files[j] );
            File targetFile = new File( destDir, targetName );
            if ( !targetFile.exists() ) {
               log( "Adding "+files[j]+" as "+
                  targetFile.getAbsolutePath()+
                  " does not exist", Project.MSG_VERBOSE );
               l.add( srcFile );
            } else if ( srcFile.lastModified() > targetFile.lastModified() ) {
               log( "Adding "+files[j]+" as "+
                  targetFile.getAbsolutePath()+
                  " is out of date.", Project.MSG_VERBOSE );
               l.add( srcFile );
            } else {
               log( "Skipping "+files[j]+" as "+
                  targetFile.getAbsolutePath()+
                  " is up to date.", Project.MSG_VERBOSE );
            }
         }
      }
      return l;
   }


   public void execute(  ) throws BuildException {
      if (!(destDir.exists()&&destDir.isDirectory()))
         throw new BuildException(destDir.getAbsolutePath() +
            " is not a valid directory.");

      List fileList = getTargetFiles();
      if (fileList.size()==0) return;
      log("Processing "+fileList.size()+" files.");
      try {
         log("Building hibernate objects");
         for (int i=0; i<fileList.size(); i++ ) {
            processFile( destDir, (File)fileList.get(i) );
         }
      } catch (Throwable t) {
         StringWriter sw = new StringWriter();
         t.printStackTrace(new PrintWriter(sw));
         throw new BuildException( "Caused by:\n"+sw.toString() );
      }
   }

   private void processFile( File destDir, File target ) {
      String [] args = new String[2];
      args[0] = "--output="+destDir.getAbsolutePath();
      args[1] = target.getAbsolutePath();
      log("Processing "+target.getAbsolutePath(), Project.MSG_VERBOSE );
      try {
         net.sf.hibernate.tool.hbm2java.CodeGenerator.main( args );
      } catch (Throwable t) {
         StringWriter sw = new StringWriter();
         t.printStackTrace(new PrintWriter(sw));
         throw new BuildException( "Caused by:\n"+sw.toString() );
      }

   }

}
