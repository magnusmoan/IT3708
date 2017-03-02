package ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import model.Chromosome;
import model.Customer;
import model.Depot;
import model.MDVRProblem;
import model.Truck;
import utils.DeepCopy;

public class GA {

	private MDVRProblem problem;
	private final Random random;
	
	public static double penaltyFactorDemand = Parameters.DEMAND_PENALTY;
	public static double penaltyFactorDuration = Parameters.DURATION_PENALTY;
	
	public GA(MDVRProblem problem) {
		this.problem = problem;
		this.random = new Random();
	}
	
	public ArrayList<String> run() {
		
		ArrayList<Chromosome> population = initPopulation();
		ArrayList<Chromosome> parents = new ArrayList<Chromosome>();
		Chromosome bestFeasible = null;
		boolean feasible = false;
		 
		for(int gen = 0; gen < Parameters.NO_OF_GENERATIONS; gen++) {
			selectParents(parents, population);
			if(parents.get(0).isFeasible(problem)) {
				bestFeasible = parents.get(0);
				feasible = true;
			}
			System.out.println("Generation: " + gen + ". Best fitness: " + parents.get(0).getLength(problem)
					+ ". Feasible: " + feasible);
			generateNextPopulation(parents, population);
		}
		
		Chromosome candidate = findBestFeasibleIndividual(population);
		if(bestFeasible == null) {
			if(candidate == null) {
				return null;
			} else {
				return generateSolution(bestFeasible);
			}
		} else {
			if(candidate == null) {
				return null;
			} else {
				if(candidate.getFitness(problem) < bestFeasible.getFitness(problem)) {
					return generateSolution(candidate);
				} else {
					return generateSolution(bestFeasible);
				}
			}
		}
	}
	
	
	private ArrayList<Chromosome> initPopulation() {
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		for(int i = 0; i < Parameters.POP_SIZE; i++) {
			population.add(new Chromosome(problem));
		}
		return population;
	}
	
	private ArrayList<Chromosome> selectParents(ArrayList<Chromosome> parents, ArrayList<Chromosome> population) {
		Collections.sort(population, (c1, c2) -> Helper.compare(c1, c2, problem));
		
		for(int i = 0; i < Parameters.NO_OF_ELITE; i++) {
			parents.add(population.get(0));
			population.remove(0);
		}
		
		
		for(int i = population.size(); i > 1; i-=2) {
			int firstIndex = random.nextInt(i);
			Chromosome firstIndividual = population.get(firstIndex);
			population.remove(firstIndex);
			
			int secondIndex = random.nextInt(i-1);
			Chromosome secondIndividual = population.get(secondIndex);
			population.remove(secondIndex);	
			
			if(random.nextDouble() > Parameters.SELECT_BEST) {
				if(Helper.compare(firstIndividual, secondIndividual, problem) == 1) {
					parents.add(firstIndividual);
				} else {
					parents.add(firstIndividual);
				}
			} else {
				parents.add(firstIndividual);
				parents.add(secondIndividual);
			}
		}
		population.clear();
		return parents;
	}
	
	private ArrayList<Chromosome> generateNextPopulation(ArrayList<Chromosome> parents, ArrayList<Chromosome> population) {
		int numberOfParents = parents.size();
		
		for(int i = 0; i < Parameters.NO_OF_ELITE; i++) {
			population.add(parents.get(i));
		}
		
		while(population.size() < Parameters.POP_SIZE) {
			if(random.nextDouble() <= Parameters.CROSSOVER_RATE) {
				Chromosome first = (Chromosome) DeepCopy.copy((parents.get(random.nextInt(numberOfParents))));
				Chromosome second = (Chromosome) DeepCopy.copy((parents.get(random.nextInt(numberOfParents))));
				crossover(first, second);
				if(random.nextDouble() <= Parameters.MUTATION_RATE) {
					first.mutate(problem);
				}
				
				if(random.nextDouble() <= Parameters.MUTATION_RATE) {
					second.mutate(problem);
				}
			
				population.add(first); population.add(second);
			} else {
				population.add((Chromosome) DeepCopy.copy(parents.get(random.nextInt(numberOfParents))));
				population.add((Chromosome) DeepCopy.copy(parents.get(random.nextInt(numberOfParents))));
			}
			
		}
		parents.clear();
		return population;
	}
	
	@SuppressWarnings("unchecked")
	private void crossover(Chromosome first, Chromosome second) {
		
		int depotId = random.nextInt(problem.getNumberOfDepots());
		int truckId1 = random.nextInt(problem.getNumberOfTrucks());
		int truckId2 = random.nextInt(problem.getNumberOfTrucks());
		
		Depot depot1 = first.getDepots().get(depotId);
		Depot depot2 = second.getDepots().get(depotId);
		
		ArrayList<Customer> customers1 = (ArrayList<Customer>) depot1.getTrucks().get(truckId1).getCustomers().clone();
		ArrayList<Customer> customers2 = (ArrayList<Customer>) depot2.getTrucks().get(truckId2).getCustomers().clone();
		
		first.removeCustomers(customers2);
		second.removeCustomers(customers1);
		
		depot1.insertCustomersBestTrucks(problem, customers2);
		depot2.insertCustomersBestTrucks(problem, customers1);
	}
	
	private Chromosome findBestFeasibleIndividual(ArrayList<Chromosome> population) {
		Collections.sort(population, (c1, c2) -> Helper.compare(c1, c2, problem));
		
		for(Chromosome individual : population) {
			if(individual.isFeasible(problem)) {
				return individual;
			}
		}
	
		return null;
	}
	
	private ArrayList<String> generateSolution(Chromosome individual) {
		ArrayList<String> solution = new ArrayList<>();
		
		String fitness = String.format("%.2f", individual.getLength(problem));
		
		for(Depot depot : individual.getDepots()) {
			ArrayList<Truck> trucks = depot.getTrucks();
			ArrayList<Truck> emptyTrucks = new ArrayList<Truck>();
			for(int i = 0; i < problem.getNumberOfTrucks(); i++) {
				Truck truck = trucks.get(i);
				if(truck.getCustomers().size() == 0) {
					emptyTrucks.add(truck);
				}
			}
			trucks.removeAll(emptyTrucks);
			depot.setNumberOfTrucks(depot.getNumberOfTrucks()-emptyTrucks.size());
		}
			
		
		for(Depot depot : individual.getDepots()) {
			ArrayList<Truck> trucks = depot.getTrucks();
			for(int i = 0; i < depot.getNumberOfTrucks(); i++) {
				String truckNo = String.format("%-4s", (i+1));
				int depotId = depot.getId()+1;
				String depotIdString = String.format("%-4s", depotId);
				Truck truck = trucks.get(i);
				if(truck.getCustomers().size() == 0) {
					continue;
				}
				String routeLength = String.format("%-8s", String.format("%.2f", truck.getLength(problem)));
				String carriedLoad = String.format("%-5s", String.format("%.0f", truck.getDemand()));
				String routeString = "0 ";
				
				for(Customer customer : truck.getCustomers()) {
					routeString += (customer.getId()+1) + " ";
				}
				routeString += "0";
				String solutionString = depotIdString + truckNo + routeLength + carriedLoad + routeString;
				
				solution.add(solutionString);	
			}
					
		}
		
		Collections.sort(solution);
		solution.add(0, fitness);
		
		return solution;
	}
	
}
