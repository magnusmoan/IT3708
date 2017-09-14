package model;

import java.util.ArrayList;

import ga.Helper;

public class MDVRProblem {

	private int noOfCustomers;
	private int noOfDepots;
	private int noOfTrucks;
	private ArrayList<ArrayList<Double>> customers;
	private ArrayList<ArrayList<Double>> depots;
	
	private double[][] distances;
	
	public MDVRProblem(ArrayList<ArrayList<Double>> customers, ArrayList<ArrayList<Double>> depots, int noOfTrucks, int noOfCustomers, int noOfDepots) {
		this.customers = customers;
		this.depots = depots;
		this.noOfCustomers = noOfCustomers;
		this.noOfDepots = noOfDepots;
		this.noOfTrucks = noOfTrucks;
		
		distances = new double[noOfDepots + noOfCustomers][noOfDepots + noOfCustomers];
		calculateDistances();
		
	}
	
	public int getNumberOfCustomers() {
		return noOfCustomers;
	}

	public int getNumberOfDepots() {
		return noOfDepots;
	}
	
	public int getNumberOfTrucks() {
		return noOfTrucks;
	}

	public ArrayList<ArrayList<Double>> getCustomers() {
		return customers;
	}

	public ArrayList<ArrayList<Double>> getDepots() {
		return depots;
	}
	
	public ArrayList<Double> getCustomer(int customerId) {
		return customers.get(customerId);
	}
	
	public ArrayList<Double> getDepot(int depotId) {
		return depots.get(depotId);
	}
	
	private void calculateDistances() {
		for(int depotId1 = 0; depotId1 < noOfDepots; depotId1++) {
			
			ArrayList<Double> depot1 = depots.get(depotId1);
			double x1 = depot1.get(1);
			double y1 = depot1.get(2);
			
			for(int depotId2 = 0; depotId2 < noOfDepots; depotId2++) {
				
				ArrayList<Double> depot2 = depots.get(depotId2);
				double x2 = depot2.get(1);
				double y2 = depot2.get(2);
				
				distances[depotId1][depotId2] = Helper.euclidean_dist(x1, y1, x2, y2);
			}
			
			for(int customerId = 0; customerId < noOfCustomers; customerId++) {
				
				ArrayList<Double> customer = customers.get(customerId);
				double x2 = customer.get(1);
				double y2 = customer.get(2);
				
				distances[depotId1][customerId+noOfDepots] = Helper.euclidean_dist(x1, y1, x2, y2);
			}
		}
		
		for(int customerId1 = 0; customerId1 < noOfCustomers; customerId1++) {
			
			ArrayList<Double> customer1 = customers.get(customerId1);
			double x1 = customer1.get(1);
			double y1 = customer1.get(2);
			
			for(int depotId = 0; depotId < noOfDepots; depotId++) {
				
				ArrayList<Double> depot = depots.get(depotId);
				double x2 = depot.get(1);
				double y2 = depot.get(2);
				
				distances[customerId1+noOfDepots][depotId] = Helper.euclidean_dist(x1, y1, x2, y2);
			}
			
			for(int customerId2 = 0; customerId2 < noOfCustomers; customerId2++) {
				
				ArrayList<Double> customer2 = customers.get(customerId2);
				double x2 = customer2.get(1);
				double y2 = customer2.get(2);
				
				distances[customerId1+noOfDepots][customerId2+noOfDepots] = Helper.euclidean_dist(x1, y1, x2, y2);
			}
		}
		
	}
	
	public double getDistanceCC(int customerId1, int customerId2) {
		return distances[customerId1+noOfDepots][customerId2+noOfDepots];
	}
	
	public double getDistanceCD(int depotId, int customerId) {
		return distances[depotId][customerId+noOfDepots];
	}
	
	public int getClosestDepot(int customerId) {
		int closest = 0;
		double dist = Double.POSITIVE_INFINITY;
		
		for(int depotId = 0; depotId < noOfDepots; depotId++) {
			double curr_dist = getDistanceCD(depotId, customerId);
			if(curr_dist < dist) {
				closest = depotId;
				dist = curr_dist;
			}
		}
		return closest;
	}
	
	public void printer() {
		
		System.out.println("Number of customers: " + noOfCustomers);
		System.out.println("Number of depots: " + noOfDepots);
		System.out.println("Number of trucks: " + noOfTrucks);
		
		System.out.println();
		
		for(ArrayList<Double> depot : depots) {
			System.out.println("Depot no.: " + (((int) ((double) depot.get(0)))+1) + 
							   "  Location: (" + depot.get(1) + "," + depot.get(2) + ")" +
					           "  Max route duration: " + depot.get(4) + "  Max truck load: " + depot.get(3));
		}
		
		for(ArrayList<Double> customer : customers) {
			System.out.println("Customer no.: " + (((int) ((double) customer.get(0)))+1) + 
							   "  Location: (" + customer.get(1) + "," + 
							   customer.get(2) + ")  Demand: " + customer.get(3) + " Service Duration: " + customer.get(4));
		}
		
		System.out.println("\nDistances:");
		
		for(double[] element : distances) {
			for(double dist : element) {
				String output = String.format("%.2f", dist);
				System.out.printf("%5s ", output);
			}
			System.out.println();
		}
	}
	
}
