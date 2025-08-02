package com.flylite.ticketing.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Entity;
import org.hibernate.annotations.Table;

@Entity
@Table(appliesTo = "Flight_Master")
public class FlightMaster {

	private Long flightId;
	private String flightName;
	private String description;
	private int capacity;

	@Id @GeneratedValue
	@Column(name="FLIGHT_ID")
	public Long getFlightId() {
		return flightId;
	}
	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	
	@Column(name="FLIGHT_NAME")
	public String getFlightName() {
		return flightName;
	}
	public void setFlightName(String flightName) {
		this.flightName = flightName;
	}
	
	@Column(name="DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="CAPACITY")
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}
