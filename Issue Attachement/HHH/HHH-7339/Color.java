package com.kodak.intersystem.data.color;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity()
@Table(name="\"Color\"", schema="\"ColorRepository\"")
public class Color implements Serializable
{
	private static final long serialVersionUID = 6474762197663866110L;

	private UUID id;
	private String name;
	private String attributes;
	private Set<Library> libraries;
    
    public Color() {}
    public Color(String name, String attributes) { this.name = name; this.attributes = attributes; }

    @Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	public UUID getId() {return id;}
	@SuppressWarnings("unused") // only used by Hibernate
	private void setId(UUID id) {this.id = id;}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getAttributes() { return attributes; }
	public void setAttributes(String attributes) { this.attributes = attributes; }
	
	public void addLibrary(Library library)
	{
		if (libraries == null)
		{
			libraries = new HashSet<Library>();
		}
		libraries.add(library);
	}


	@ManyToMany(
	    targetEntity=Library.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name="\"Color_Library\"",  schema="\"ColorRepository\"",
        joinColumns=@JoinColumn(name="colorID", updatable=false, nullable=false),
        inverseJoinColumns=@JoinColumn(name="libraryID", updatable=false, nullable=false)
    )

    public Set<Library> getLibraries() { return libraries; }
    public void setLibraries(Set<Library> libraries) { this.libraries = libraries; }
}           