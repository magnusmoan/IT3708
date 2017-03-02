package model;

import java.io.Serializable;

public class Customer implements Serializable{
	
	private static final long serialVersionUID = 4L;
	
	private double demand;
	private double serviceDuration;
	private int id;

	public Customer(int id, double demand, double serviceDuration) {
		this.id = id;
		this.demand = demand;
		this.serviceDuration = serviceDuration;
	} 
	
	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}
	
	public int getId() {
		return id;
	}
	
	public double getServiceDuration() {
		return serviceDuration;
	}

}
