package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import acoModel.Ant;
import acoModel.Graph;
import model.JSSPInstance;
import model.Operation;
import model.Schedule;
import utils.Parameters;

public class ACO extends Algorithm{
	
	private ArrayList<Operation> bestIter;
	private ArrayList<Operation> bestStart;
	private ArrayList<Operation> bestRestart;
	private HashMap<Operation, Integer> bestIterOpToIndex;
	private HashMap<Operation, Integer> bestStartOpToIndex;
	private HashMap<Operation, Integer> bestRestartOpToIndex;
	private Graph graph;
	private boolean bsUpdate;
	private double cf;
	private int bestMSIter;
	private int bestMSStart;
	private int bestMSRestart;

	public ACO(JSSPInstance instance) {
		super((int) Math.floor(Math.max(10.0, (instance.getNJobs()*instance.getNMachines())/10.0)), instance);
		this.graph =  new Graph(instance);
		this.bsUpdate = false;
		this.cf = 0.0;
		this.bestMSRestart = Integer.MAX_VALUE;
		this.bestMSStart = Integer.MAX_VALUE;
		
		for(int i = 0; i < this.popSize; i++) {
			this.individuals.add(new Ant(instance, graph));
		}
		
	}

	public Schedule run() {
		System.out.println("\n*** STARTING ACO ***");
		
		this.graph.initPheromone(0.5);
		
		for(int iteration = 1; iteration < Parameters.ACO_ITERATIONS; iteration++) {
			
			this.bestMSIter = Integer.MAX_VALUE;
			int bestIndex = 0;
			
			for(int i = 0; i < this.popSize; i++) {
				Ant ant = (Ant) individuals.get(i);
				ant.createPath();
				ant.tabuSearch(Parameters.ANT_NORMAL_SEARCH_ITER);
				if(ant.getMakespan() < bestMSIter) {
					bestIndex = i;
					bestMSIter = ant.getMakespan();
				}
			}
			
			
			Ant best = (Ant) individuals.get(bestIndex);
			best.tabuSearch(Parameters.ANT_ELITE_SEARCH_ITER);
			this.bestIter = new ArrayList<>(best.getPath());
			this.bestIterOpToIndex = new HashMap<>(best.getOpToIndex());
			
			
			updateBest((Ant) individuals.get(bestIndex));
			if(bsUpdate) {
				graph.updatePheromone(bestStartOpToIndex);
			} else {
				graph.updatePheromone(bestRestartOpToIndex);
			}
			
			cf = graph.updateCf();
			
			if(cf > .99) {
				if(bsUpdate) {
					graph.initPheromone(0.5);
					bestMSRestart = Integer.MAX_VALUE;
					bsUpdate = false;
				} else {
					bsUpdate = true;
				}
			}
			
			System.out.println("Iteration number: " + iteration + ". Best makespan: " + this.bestMSStart);
		}
		
		
		return new Schedule(pathToOperations(bestStart), instance);
	}
	
	private void updateBest(Ant ant) {
		ArrayList<Operation> path = new ArrayList<>(ant.getPath());
		HashMap<Operation, Integer> opToIndex = new HashMap<>(ant.getOpToIndex());
		int makespan = ant.getMakespan();
		if(makespan < bestMSRestart) {
			this.bestRestart = path;
			this.bestRestartOpToIndex = opToIndex;
			this.bestMSRestart = makespan;
		}
		
		if(makespan < bestMSStart) {
			this.bestStart = path;
			this.bestStartOpToIndex = opToIndex;
			this.bestMSStart = makespan;
		}
		
		
	}
	
	public int[] pathToOperations(ArrayList<Operation> path) {
		int size = this.instance.getNJobs() * this.instance.getNMachines();
		int[] operations = new int[size];
		
		for(int i = 0; i < size; i++) {
			operations[i] = path.get(i).getJobNo();
		}
		
		return operations;
	}

}
