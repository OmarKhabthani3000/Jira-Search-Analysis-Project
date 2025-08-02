package com.kodak.intersystem.data.color;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity()
@Table(name="\"Library\"",  schema="\"ColorRepository\"")
public class Library implements Serializable
{
	private static final long serialVersionUID = 8971081990800080567L;

	private UUID id;
	private String name;
	private String attributes;
	private Set<Color> colors;
	
	public Library() {}
	public Library(String name, String attributes) { this.name = name; this.attributes = attributes; }

    @Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	public UUID getId() {return id;}
	@SuppressWarnings("unused") // only used by Hibernate
	private void setId(UUID id) {this.id = id;}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getAttributes() { return attributes; }
	public void setAttributes(String attributes) { this.attributes = attributes; }
    
	public void addColor(Color color)
	{
		if (colors == null)
		{
			colors = new HashSet<Color>();
		}
		colors.add(color);
	}

    @ManyToMany(
        targetEntity = Color.class,
        mappedBy = "libraries",			// Color.linraries
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    public Set<Color> getColors() { return colors; }
    public void setColors(Set<Color> colors) { this.colors = colors; }
}            