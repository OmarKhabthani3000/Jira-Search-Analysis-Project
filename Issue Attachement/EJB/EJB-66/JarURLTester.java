package org.hibernate.ejb.packaging;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JarURLTester {
	public   static void main(String[] args) {
		List<String> urlStrings = new ArrayList<String>();
		urlStrings.add("jar:http://www.myopenproject.org/code/model.par!/META-INF/some/other/resource.xml");
		urlStrings.add("jar:http://www.myopenproject.org/code/model.par!/META-INF/persistence.xml");
		urlStrings.add("jar:file:///home/src/code/model.par!/META-INF/some/other/resource.xml");
		urlStrings.add("jar:file:///home/src/code/model.par!/META-INF/persistence.xml");
		urlStrings.add("file:///home/src/code/model.par");  // this is wrong, use "jar:file:...."
		urlStrings.add("file:///home/myopenproject/bin/model");
		urlStrings.add("file:home/myopenproject/bin/model/META-INF/persistence.xml");
		
		for(String s: urlStrings) {
			System.out.printf("\n");
			URL nurl;
			try {
				nurl = new URL(s);
				System.out.printf("input URL: %s\n", nurl);
			} catch(Exception e) {
				System.out.printf("failed new URL: %s\n", e.getMessage());
				continue;
			}
			try {
				System.out.printf("beta3: URL: %s\n", JarVisitor.getJarURLFromURLEntry(nurl, "META-INF/persistence.xml"));
			} catch(Exception e) {
				System.out.printf("JarVisitor.getJarURLFromURLEntry fail: %s, e: %s\n", nurl, e.getMessage());
			}
			
			try {
				System.out.printf("new: URL: %s\n", JarVisitor.getRootURL(nurl, "META-INF/persistence.xml"));
			}catch(Exception e) {
				System.out.printf("JarVisitor.getRootURL fail: %s, e: %s\n", nurl, e.getMessage());
			}			
		}
		
	}
	
}
