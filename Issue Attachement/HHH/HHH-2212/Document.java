package de.mq.fourier.server.document;

public interface Document {
	 public String getType();
	 public String getId();
	 public DocumentConfiguration getConfiguration();
	 public String getDocument();
	 public void setDocument(String document);
    public void save() ;
}
