package algorithms;

import java.util.concurrent.ThreadLocalRandom;

import model.Individual;
import model.JSSPInstance;
import model.Particle;
import model.Schedule;
import utils.Parameters;

public class PSO extends Algorithm{

	private double omega;
	private double omegaDiff;
	
	public PSO(JSSPInstance instance) {
		super(Parameters.N_PARTICLES, instance);
		this.omega = Parameters.INITIAL_INERTIAL_WEIGHT;
		this.omegaDiff = (Parameters.INITIAL_INERTIAL_WEIGHT - Parameters.FINAL_INERTIAL_WEIGHT) / Parameters.PSO_ITERATIONS;
		
		for(int i = 0; i < popSize; i++) {
			individuals.add(new Particle(this.instance));
		}
		
		updateBest();
		System.out.println("Best initial makespan: " + this.bestMakespan);
	}
	
	public Schedule run() {
		System.out.println("\n*** STARTING PSO ***");
		for(int iteration = 1; iteration < Parameters.PSO_ITERATIONS; iteration++) {
			
			for(Individual p : individuals) {
				if(ThreadLocalRandom.current().nextDouble() < Parameters.LOCAL_SEARCH_PROB) {
					((Particle) p).localSearchSA(this.bestMakespan);
				}
				p.updateBest();
			}
			
			updateBest();
			updateOmega();
			
			for(Individual p : individuals) {
				((Particle) p).updatePosition(omega, bestIndividual);
			}
			
			System.out.println("Iteration number: " + iteration + ". Best makespan: " + this.bestMakespan);
		}
		
		return new Schedule(getBestPosition(), this.instance);
	}
	
	
	private void updateOmega() {
		this.omega -= omegaDiff;
	}
}
