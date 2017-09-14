package algorithms;

import java.util.ArrayList;

import model.Individual;
import model.JSSPInstance;
import model.Schedule;

public abstract class Algorithm {

	protected final JSSPInstance instance;
	protected ArrayList<Individual> individuals;
	protected final int popSize;
	protected int bestMakespan;
	protected double[] bestIndividual;
	protected final int individualSize;
	
	public Algorithm(int n, JSSPInstance instance) {
		this.popSize = n;
		this.instance = instance;
		this.individuals = new ArrayList<Individual>(popSize);
		this.bestMakespan = Integer.MAX_VALUE;
		this.individualSize = this.instance.getNJobs()*this.instance.getNMachines();
		this.bestIndividual = new double[this.individualSize];
	}
	protected void updateBest() {
		for(int i = 0; i < popSize; i++) {
			int candidate = individuals.get(i).getBestMakespan();
			if(candidate < bestMakespan) {
				bestMakespan = candidate;
				System.arraycopy(individuals.get(i).getPosition(), 0, bestIndividual, 0, individualSize);
			}
		}
	}
	
	protected double[] getBestPosition() {
		return this.bestIndividual;
	}
	
	public abstract Schedule run();
	
}
