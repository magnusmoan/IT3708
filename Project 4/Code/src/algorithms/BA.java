package algorithms;

import java.util.Collections;

import model.Bee;
import model.JSSPInstance;
import model.Schedule;
import utils.Parameters;

public class BA extends Algorithm{
	
	private final int nElite;
	private final int nAcc;
	private final int eliteMoves;
	private final int accMoves;

	public BA(JSSPInstance instance) {
		super(Parameters.N_BEES, instance);
		
		this.nElite = Parameters.N_ELITE_BEES;
		this.nAcc = Parameters.N_ACC_BEES;
		this.eliteMoves = Parameters.MOVES_ELITE;
		this.accMoves = Parameters.MOVES_ACC;
		
		for(int i = 0; i < popSize; i++) {
			individuals.add(new Bee(this.instance));
		}
		
		updateBest();
		System.out.println("Best initial makespan: " + this.bestMakespan);
	}

	public Schedule run() {
		System.out.println("\n*** STARTING BA ***");
		
		for(int iteration = 1; iteration < Parameters.BA_ITERATIONS; iteration++) {
			Collections.sort(this.individuals, (b1, b2) -> b1.getMakespan() - b2.getMakespan());
			
			for(int en = 0; en < nElite; en++) {
				((Bee) this.individuals.get(en)).LocalSearchTabu(this.eliteMoves);
			}
			
			for (int an = nElite; an < nElite+nAcc; an++) {
				((Bee) this.individuals.get(an)).LocalSearchTabu(this.accMoves);
			}
			
			for (int sn = nElite+nAcc; sn < this.popSize; sn++) {
				((Bee) this.individuals.get(sn)).scout();
			}
			
			updateBest();
			System.out.println("Iteration number: " + iteration + ". Best makespan: " + this.bestMakespan);
		}
			
		
		return new Schedule(getBestPosition(), this.instance);
	}

}
