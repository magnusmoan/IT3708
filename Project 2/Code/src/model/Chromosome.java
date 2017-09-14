package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ga.Helper;
import ga.Parameters;

public class Chromosome implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Depot> depots;
	private boolean changed;
	private double fitness;
	private int noOfDepots;
	private int noOfCustomers;
	private final Random random;
	
	
	public Chromosome(MDVRProblem problem) {
		this.noOfDepots = problem.getNumberOfDepots();
		this.noOfCustomers = problem.getNumberOfCustomers();
		this.random = new Random();
		initDepots(problem);
		this.changed = true;
		
	}
	
	private void initDepots(MDVRProblem problem) {
		HashMap<Integer, ArrayList<Customer>> customers = new HashMap<Integer, ArrayList<Customer>>();
		initCustomers(problem, customers);
		
		depots = new ArrayList<Depot>();
		for(int i = 0; i < noOfDepots; i++) {
			ArrayList<Double> depotInfo = problem.getDepot(i);
			int id = (int) (double) depotInfo.get(0);
			double maxLoad = depotInfo.get(3);
			double maxDuration = depotInfo.get(4);
			depots.add(new Depot(id, maxLoad, maxDuration, problem, customers.get(i), this));
		}
	}
	
	private void initCustomers(MDVRProblem problem, HashMap<Integer, ArrayList<Customer>> customers) {
		for(int i = 0; i < noOfCustomers; i++) {
			ArrayList<Double> customerInfo = problem.getCustomer(i);
			int id = (int) (double) customerInfo.get(0);
			double demand = customerInfo.get(3);
			double duration = customerInfo.get(4);
			int closestDepotId = problem.getClosestDepot(id);
			
			if(!customers.containsKey(closestDepotId)) {
				customers.put(closestDepotId, new ArrayList<Customer>());
			}
			
			customers.get(closestDepotId).add(new Customer(id, demand, duration));
			
		}
		
	}
	
	public ArrayList<Depot> getDepots() {
		return depots;
	}
	
	public double getFitness(MDVRProblem problem) {
		if(changed) {
			fitness = depots.stream().mapToDouble(d -> d.getFitness(problem)).sum();
			changed = false;
		}
		return fitness;
	}
	
	public void setChanged() {
		changed = true;
	}
	
	public void removeCustomers(ArrayList<Customer> customers) {
		for(Customer customer : customers) {
			for(Depot depot : depots) {
				if(depot.removeCustomer(customer)) {
					break;
				}
			}
		}
	}
	
	public void mutate(MDVRProblem problem) {
		if(random.nextDouble() < Parameters.MUTATE_INTERNAL_OR_EXTERNAL) {
			moveCustomerBetweenDepots(problem);
		} else {
			depots.get(random.nextInt(noOfDepots)).mutate();
		}
	}
	
	private void moveCustomerBetweenDepots(MDVRProblem problem) {
		return;
		/*int depotId = random.nextInt(noOfDepots);
		Depot oldDepot = depots.get(depotId);
		Customer customer = oldDepot.removeRandomCustomer();
		
		if(customer == null) {
			return;
		}
		
		
		Depot newDepot = depots.get(Helper.getRandomIntNotEqual(depotId, noOfDepots, random));
		newDepot.insertCustomerBestTruck(problem, customer);*/
	}
		
	
	public boolean isFeasible(MDVRProblem problem) {
		double penalty = 0.0;
		
		for(Depot depot : depots) {
			for(Truck truck : depot.getTrucks()) {
				truck.getLength(problem);
				penalty += truck.getPenalty();
			}
		}
		
		return penalty == 0.0;
	}
	
	public double getLength(MDVRProblem problem) {
		return depots.stream().mapToDouble(d -> d.getLength(problem)).sum();
	}
}
