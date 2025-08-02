package com.linovation.trainapp.database.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * The persistent class for the ROUTE database table.
 * 
 */
@Entity
@NamedQuery(name = "Route.findAll", query = "SELECT r FROM Route r")
public class Route extends BasicEntity
{
	private static final long serialVersionUID = 4326869346959218702L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ROUTE_ID")
	private Long routeId;

	@Column(name = "ROUTE_CREATOR_ID")
	private Long routeCreatorId;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "GUID")
	private String guid;

	@Column(name = "ROUTE_NAME")
	private String routeName;

	// bi-directional one-to-one association to FTPServer
	@OneToOne(mappedBy = "route", cascade = { CascadeType.ALL })
	private FTPServer ftpServer;

	// bi-directional many-to-one association to Media
	@OneToMany(mappedBy = "route", cascade = { CascadeType.ALL })
	private Set<Media> routeMedia;

	// bi-directional many-to-many association to Station
	@ManyToMany(mappedBy = "routes", cascade = { CascadeType.ALL })
	private Set<Station> stations;

	public Route()
	{
		routeMedia = new HashSet<Media>();
		stations = new HashSet<Station>();
	}

	public Long getRouteId()
	{
		return this.routeId;
	}

	public void setRouteId(Long routeId)
	{
		this.routeId = routeId;
	}
	
	public Long getRouteCreatorId()
	{
		return routeCreatorId;
	}

	public void setRouteCreatorId(Long routeCreatorId)
	{
		this.routeCreatorId = routeCreatorId;
	}
	
	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getGuid()
	{
		return this.guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public String getRouteName()
	{
		return this.routeName;
	}

	public void setRouteName(String routeName)
	{
		this.routeName = routeName;
	}

	public FTPServer getFtpServer()
	{
		return this.ftpServer;
	}

	public void setFtpServer(FTPServer ftpServer)
	{
		this.ftpServer = ftpServer;
		ftpServer.setRoute(this);
	}

	public Set<Media> getRouteMedia()
	{
		return this.routeMedia;
	}

	public void setRouteMedia(Set<Media> routeMedia)
	{
		this.routeMedia = routeMedia;
	}

	public Media addRouteMedia(Media routeMedia)
	{
		getRouteMedia().add(routeMedia);
		routeMedia.setRoute(this);

		return routeMedia;
	}

	public Media removeRouteMedia(Media routeMedia)
	{
		getRouteMedia().remove(routeMedia);
		routeMedia.setRoute(null);

		return routeMedia;
	}

	public Set<Station> getStations()
	{
		return this.stations;
	}

	public void setStations(Set<Station> stations)
	{
		this.stations = stations;
	}
	
	public void addStation(Station station)
	{
		this.stations.add(station);
		station.getRoutes().add(this);
	}
	
	public void removeStation(Station station)
	{
		this.stations.remove(station);
		station.getRoutes().remove(this);
	}
}