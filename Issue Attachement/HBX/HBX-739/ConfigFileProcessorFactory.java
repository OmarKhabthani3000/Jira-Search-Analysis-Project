/*
 * Copyright (c) 2006, Carman Consulting, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.carmanconsulting.hibernate.apt;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.SimpleDeclarationVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * @author James Carman
 * @version 1.0
 */
public class ConfigFileProcessorFactory implements AnnotationProcessorFactory
{
    public ConfigFileProcessorFactory()
    {
        System.out.println( "I'm being instantiated!" );
    }

    public Collection<String> supportedOptions()
    {
        return Arrays.asList( "file" );
    }

    public Collection<String> supportedAnnotationTypes()
    {
        return Arrays.asList( "javax.persistence.Entity" );
    }

    public AnnotationProcessor getProcessorFor( Set<AnnotationTypeDeclaration> atds,
                                                AnnotationProcessorEnvironment env )
    {
        return new ConfigFileProcessor( env, atds );
    }

    private static class ConfigFileProcessor implements AnnotationProcessor
    {
        private final AnnotationProcessorEnvironment environment;
        private final Set<AnnotationTypeDeclaration> typeDeclarations;

        public ConfigFileProcessor( AnnotationProcessorEnvironment environment,
                                    Set<AnnotationTypeDeclaration> typeDeclarations )
        {
            this.environment = environment;
            this.typeDeclarations = typeDeclarations;
        }

        private File getDestinationFile()
        {
            for( String key : environment.getOptions().keySet() )
            {
                if( key.startsWith( "-Afile=" ) )
                {
                    final String fileName = key.substring( "-Afile=".length() );
                    return new File( fileName );
                }
            }
            return new File( "hibernate.cfg.xml" );
        }

        public void process()
        {
            try
            {
                final File file = getDestinationFile();
                if( file.getParentFile() != null && !file.getParentFile().exists() )
                {
                    file.getParentFile().mkdirs();
                }
                System.out.println( "Creating configuration file at " + file.getAbsolutePath() + "..." );
                final FileWriter fw = new FileWriter( file );
                final PrintWriter pw = new PrintWriter( fw );
                pw.println( "<!DOCTYPE hibernate-configuration PUBLIC" );
                pw.println( "    \"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"" );
                pw.println( "    \"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\n" );
                pw.println();
                pw.println( "<hibernate-configuration>" );
                pw.println( "  <session-factory>" );
                for( AnnotationTypeDeclaration typeDeclaration : typeDeclarations )
                {
                    for( Declaration declaration : environment.getDeclarationsAnnotatedWith( typeDeclaration ) )
                    {
                        declaration.accept( new EntityClassVisitor( pw ) );
                    }
                }
                pw.println( "  </session-factory>" );
                pw.println( "</hibernate-configuration>" );
                pw.close();
                fw.close();
            }
            catch( IOException e )
            {
                environment.getMessager().printError( "Unable to process @Entity annotations." );
            }
        }
    }

    private static class EntityClassVisitor extends SimpleDeclarationVisitor
    {
        private final PrintWriter pw;

        public EntityClassVisitor( PrintWriter pw )
        {
            this.pw = pw;
        }

        public void visitClassDeclaration( ClassDeclaration d )
        {
            System.out.println( "Adding mapping for class " + d.getQualifiedName() + "..." );
            pw.println( "    <mapping class=\"" + d.getQualifiedName() + "\"/>" );
        }
    }
}
