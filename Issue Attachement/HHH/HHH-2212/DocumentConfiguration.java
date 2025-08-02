package de.mq.fourier.server.document;

import java.util.List;
import java.util.Set;

public interface DocumentConfiguration {
	public Long getId(); 
	public Set<DocumentConfiguration> getChilds();
	public String getName() ; 
	public List <Document> getDocuments()  ;

}
