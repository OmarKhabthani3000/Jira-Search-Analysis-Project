/**
 * $Id$
 *
 * Public Domain code
 */
package org.hibernate.eclipse.console;

import java.io.File;
import java.util.*;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.hibernate.tool.instrument.InstrumentTask;

/**
 * @author juozas
 *
 */
public class Enhancer extends IncrementalProjectBuilder {

    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
            throws CoreException {
        
        
        IJavaProject jproject = JavaCore.create(getProject());
        IResource res = getProject().findMember(jproject.getOutputLocation());
        if(res != null){
            res.accept( new IResourceVisitor(){
                
                public boolean visit(IResource resource) throws CoreException {
                    
                    if( resource instanceof IFile ){
                        IFile file = (IFile)resource;
                        process(file.getFullPath().toFile());
                    }
                    return true;
                }
                
                
            });
        }
        
        return null;
    }

   private void process(final File file){

        InstrumentTask task = new InstrumentTask(){
            
             protected Collection getFiles() {
               return Collections.singleton(file);       
            
             }  
           }; 
         
           task.execute();
    }
    
}

/**
* $Log$
*/