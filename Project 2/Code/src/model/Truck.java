package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ga.GA;
import ga.Helper;
import utils.Tuple;

public class Truck implements Serializable{

	private static final long serialVersionUID = 3L;
	
	private ArrayList<Customer> customers;
	private final double maxCapacity;
	private final double maxDuration;
	private double currDemand;
	private double currDuration;
	private double length;
	private final Depot depot;
	private boolean changed;
	private final Random random; 
	
	public Truck(ArrayList<Customer> customers, double maxCapacity, double maxDuration, Depot depot, MDVRProblem problem) {
		this.customers = customers;
		this.maxCapacity = maxCapacity;
		if(maxDuration == 0.0) {
			this.maxDuration = Double.POSITIVE_INFINITY;
		} else {
			this.maxDuration = maxDuration;
		}
		
		this.depot = depot;
		this.changed = true;
		random = new Random();
	}
	
	public boolean capacityBroken() {
		return currDemand > maxCapacity;
	}
	
	public boolean durationBroken() {
		return currDuration > maxDuration;
	}
	
	public boolean checkAndRemoveCustomer(Customer customer) {
		int id = customer.getId();
		for(Customer currCustomer : customers) {
			if(currCustomer.getId() == id) {
				removeDurationDemand(customer);
				setChanged();
				return customers.remove(currCustomer);
			}
		}
		return false;
	}
	
	public double getDemand() {
		return currDemand;
	}
	
	public double getDuration() {
		return currDuration;
	}
	
	public void removeCustomer(Customer customer) {
		customers.remove(customer);
		removeDurationDemand(customer);
		setChanged();
	}
	
	public Customer removeCustomerIndex(int index) {
		Customer customer = customers.remove(index);
		removeDurationDemand(customer);
		setChanged();
		return customer;
	}
	
	public void appendCustomer(Customer customer) {
		customers.add(customer);
		addDurationDemand(customer);
		setChanged();
	}
	
	
	public Tuple<Integer, Double> getBestIndexAndFitnessChange(MDVRProblem problem, Customer customer) {
		int bestIndex = 0;
		double bestFitness;
		int noOfCustomers = customers.size();
		int depotId = depot.getId();
		int customerId = customer.getId();
		
		if(noOfCustomers == 0) {
			bestFitness = 2*problem.getDistanceCD(depotId, customerId);
		} else {
			bestFitness = problem.getDistanceCD(depotId, customerId);
			bestFitness += problem.getDistanceCC(customerId, customers.get(0).getId());
			bestFitness -= problem.getDistanceCD(depotId, customers.get(0).getId());
			
			if(noOfCustomers > 1) {
				for(int index = 1; index < noOfCustomers; index++) {
					double currFitness = problem.getDistanceCC(customerId, customers.get(index-1).getId());
					currFitness += problem.getDistanceCC(customerId, customers.get(index).getId());
					currFitness -= problem.getDistanceCC(customers.get(index).getId(), customers.get(index-1).getId());
					if(currFitness < bestFitness) {
						bestFitness = currFitness;
						bestIndex = index;
					}
				}
			}
		} 
		
		bestFitness += penaltyWithNewCustomer(customer, bestFitness);
		return new Tuple<Integer, Double>(bestIndex, bestFitness);
	}
	
	private double penaltyWithNewCustomer(Customer customer, double extraLength) {
		double customerDemand = customer.getDemand();
		double customerDuration = customer.getServiceDuration();
		double penalty = 0.0;
		penalty += GA.penaltyFactorDemand * Math.max(0, (currDemand + customerDemand) - maxCapacity);
		penalty += GA.penaltyFactorDuration * Math.max(0, (currDuration + customerDuration + length + extraLength) - maxDuration);
		return penalty;
	}
	
	public void addCustomer(int index, Customer customer) {
		customers.add(index, customer);
		addDurationDemand(customer);
		setChanged();
	}
	
	public void recalculateLength(MDVRProblem problem) {
		length = 0.0;
		if(customers.size() == 0) {
			return;
		}
		

		length += problem.getDistanceCD(depot.getId(), customers.get(0).getId());
		length += problem.getDistanceCD(depot.getId(), customers.get(customers.size()-1).getId());
		
		if(customers.size() == 1) {
			return;
		}
		
		for(int i = 0; i < customers.size()-1; i++) {
			double dist = problem.getDistanceCC(customers.get(i).getId(), customers.get(i+1).getId());
			length += dist;
		}
	}
	
	public double getLength(MDVRProblem problem) {
		if(changed) {
			recalculateLength(problem);
			changed = false;
		}
		return length;
	}
	
	private void addDemand(Customer customer) {
		currDemand += customer.getDemand();
	}
	
	private void addDuration(Customer customer) {
		currDuration += customer.getServiceDuration();
	}
	
	private void removeDemand(Customer customer) {
		currDemand -= customer.getDemand();
	}
	
	private void removeDuration(Customer customer) {
		currDuration -= customer.getServiceDuration();
	}
	
	public void addDurationDemand(Customer customer) {
		addDemand(customer);
		addDuration(customer);
	}
	
	public void removeDurationDemand(Customer customer) {
		removeDemand(customer);
		removeDuration(customer);
	}
	
	public ArrayList<Customer> getCustomers() {
		return customers;
	}
	
	public double getPenalty() {
		double penalty = 0.0;
		penalty += GA.penaltyFactorDemand*Math.max(0, currDemand - maxCapacity);
		penalty += GA.penaltyFactorDuration*Math.max(0, (currDuration + length) - maxDuration);
		return penalty;
	}
	
	public double getFitness(MDVRProblem problem) {
		return getLength(problem) + getPenalty();
	}
	
	public void setChanged() {
		changed = true;
		depot.setChanged();
	}
	
	public void moveInTruck() {
		int noOfCustomers = customers.size();
		
		if(noOfCustomers == 0 || noOfCustomers == 1) {
			return;
		}
		
		int i1 = random.nextInt(customers.size());
		int i2 = Helper.getRandomIntNotEqual(i1, noOfCustomers, random);

		Collections.swap(customers, i1, i2);
	}
	
	public Customer removeRandomCustomer() {
		if(customers.size() == 0) {
			return null;
		}
		return removeCustomerIndex(random.nextInt(customers.size()));
	}
}
