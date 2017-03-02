package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ga.Helper;
import ga.Parameters;
import utils.Tuple;

public class Depot implements Serializable{

	private static final long serialVersionUID = 2L;
	
	private double truckMaxLoad;
	private double maxServiceDuration;
	private double totalMaxLoad;
	private ArrayList<Truck> trucks;
	private boolean changed;
	private double fitness;
	private Chromosome chromosome;
	private int id;
	private int noOfTrucks;
	private final Random random;

	public Depot(int id, double truckMaxLoad, double maxServiceDuration, MDVRProblem problem, ArrayList<Customer> customers, Chromosome chromosome) {
		this.id = id;
		this.noOfTrucks = problem.getNumberOfTrucks();
		this.truckMaxLoad = truckMaxLoad;
		this.maxServiceDuration = maxServiceDuration;
		this.changed = true;
		this.chromosome = chromosome;
		this.random = new Random();
		initTrucks(problem);
		randomDistributeCustomers(customers);
	}
	
	public void setNumberOfTrucks(int noOfTrucks) {
		this.noOfTrucks = noOfTrucks;
	}
	
	public int getNumberOfTrucks() {
		return this.noOfTrucks;
	}
	
	private void initTrucks(MDVRProblem problem) {
		trucks = new ArrayList<Truck>();
		for(int i = 0; i < problem.getNumberOfTrucks(); i++) {
			Truck truck = new Truck(new ArrayList<>(), truckMaxLoad, maxServiceDuration, this, problem);
			trucks.add(truck);
		}
	}
	
	private void randomDistributeCustomers(ArrayList<Customer> customers) {
		Collections.shuffle(customers);
		int truckNo = 0;
		for(Customer customer : customers) {
			trucks.get(truckNo).appendCustomer(customer);
			truckNo++;
			if (truckNo == noOfTrucks) {
				truckNo = 0;
			}
		}
	}

	public double getMaxLoad() {
		return truckMaxLoad;
	}
	
	public double getTotalMaxLoad() {
		return totalMaxLoad;
	}

	public double getMaxServiceDuration() {
		return maxServiceDuration;
	}
	
	public int getId() {
		return id;
	}
	
	public ArrayList<Truck> getTrucks() {
		return trucks;
	}
	
	public double getFitness(MDVRProblem problem) {
		if(changed) {
			fitness = 0.0;
			for(Truck truck : trucks) {
				fitness += truck.getFitness(problem);
			}
			changed = false;
		}
		return fitness;
	}
	
	public void setChanged() {
		changed = true;
		chromosome.setChanged();
	}
	
	public boolean removeCustomer(Customer customer) {
		for(Truck truck : trucks) {
			if(truck.checkAndRemoveCustomer(customer)) {
				return true;
			}
		}
		return false;
	}
	
	public void insertCustomersBestTrucks(MDVRProblem problem, ArrayList<Customer> customers) {
		for(Customer customer : customers) {
			insertCustomerBestTruck(problem, customer);
		}
	}
	
	public void insertCustomerBestTruck(MDVRProblem problem, Customer customer) {
		int bestTruck = 0;
		Tuple<Integer, Double> bestPair = trucks.get(0).getBestIndexAndFitnessChange(problem, customer);
		for(int i = 1; i < noOfTrucks; i++) {
			Tuple<Integer, Double> currPair = trucks.get(i).getBestIndexAndFitnessChange(problem, customer);
			if(currPair.getValue() < bestPair.getValue()) {
				bestPair = currPair;
				bestTruck = i;
			}
		}
		trucks.get(bestTruck).addCustomer(bestPair.getIndex(), customer);
	}
	
	public void mutate() {
		if(random.nextDouble() < Parameters.MUTATE_INTERNAL_RATE) {
			moveBetweenTrucks();
		} else {
			trucks.get(random.nextInt(noOfTrucks)).moveInTruck();
		}
	}
	
	private void moveBetweenTrucks() {
		int t1 = random.nextInt(noOfTrucks);
		Customer customer = trucks.get(t1).removeRandomCustomer();
		if(customer == null) {
			return;
		}
		
		int t2 = Helper.getRandomIntNotEqual(t1, noOfTrucks, random);
		trucks.get(t2).appendCustomer(customer);
	}
	
	public Customer removeRandomCustomer() {
		return trucks.get(random.nextInt(noOfTrucks)).removeRandomCustomer();
	}
	
	public double getLength(MDVRProblem problem) {
		return trucks.stream().mapToDouble(t -> t.getLength(problem)).sum();
	}
}
