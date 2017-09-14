package acoModel;

import java.io.Serializable;
import java.util.HashMap;

import model.JSSPInstance;
import model.Job;
import model.Operation;
import utils.Parameters;

public class Graph implements Serializable{

	private static final long serialVersionUID = 2L;
	private HashMap<Operation, HashMap<Operation, Double>> pheromones;
	private final JSSPInstance instance;
	private final double evopRate;
	private final double pherMin;
	private final double pherMax;
	private final int size;
	private final int nJobs;
	private final int nMachines;
	private final double cfFactor;
	
	public Graph(JSSPInstance instance) {
		this.instance = instance;
		this.pheromones = new HashMap<>();
		this.evopRate = Parameters.EVOP_RATE;
		this.pherMax = Parameters.PHEROMONE_MAX;
		this.pherMin = Parameters.PHEROMONE_MIN;
		this.nJobs = instance.getNJobs();
		this.nMachines = instance.getNMachines();
		this.size = this.nJobs*this.nMachines;
		this.cfFactor = this.size * (this.pherMax - this.pherMin);
		
		for(int jobNo = 0; jobNo < this.nJobs; jobNo++) {
			Job currJob = instance.getJob(jobNo);
			
			for(int opNo = 0; opNo < this.nMachines; opNo++) {
				Operation currOp = currJob.getOperation(opNo);
				HashMap<Operation, Double> currPheromones = new HashMap<>();
				
				for(int i = 0; i < this.nJobs; i++) {
					Job secondJob = instance.getJob(i);
					if(secondJob == currJob) {
						if(opNo != this.nMachines-1) { 
							currPheromones.put(secondJob.getOperation(opNo+1), 0.0);
						}
						
					} else {
						for(int j = 0; j < this.nMachines; j++) {
							Operation secondOp = secondJob.getOperation(j);
							currPheromones.put(secondOp, 0.0);
						}
					}
				}
				pheromones.put(currOp, currPheromones);
			}
		}
	}
	
	public double updateCf() {
		double sum = 0.0;
		
		for(Operation o1 : this.pheromones.keySet()) {
			HashMap<Operation, Double> currMap = this.pheromones.get(o1);
			
			for(Operation o2 : currMap.keySet()) {
				double pheromone = currMap.get(o2);
				sum += Math.max(this.pherMax - pheromone, pheromone - this.pherMin);
			}
		}
		
		return 2 * ((sum / this.cfFactor) - .5);
	}
	
	public double getPheromone(Operation o1, Operation o2) {
		try {
			return pheromones.get(o1).get(o2);
		} catch (NullPointerException e) {
			System.out.println("FOO!");
			return 0.0;
		}
	}
	
	public void initPheromone(double value) {
		for(int jobNo = 0; jobNo < this.nJobs; jobNo++) {
			Job currJob = instance.getJob(jobNo);
			
			for(int opNo = 0; opNo < this.nMachines; opNo++) {
				Operation currOp = currJob.getOperation(opNo);
				HashMap<Operation, Double> currPheromones = pheromones.get(currOp);
				
				for(int i = 0; i < this.nJobs; i++) {
					Job secondJob = instance.getJob(i);
					if(secondJob == currJob) {
						if(opNo != this.nMachines-1) { 
							currPheromones.put(secondJob.getOperation(opNo+1), value);
						}
						break;
					} 
					for(int j = 0; j < this.nMachines; j++) {
						Operation secondOp = secondJob.getOperation(j);
						currPheromones.put(secondOp, value);
					}
				}
				pheromones.put(currOp, currPheromones);
			}
		}
	}
	
	public void updatePheromone(HashMap<Operation, Integer> opToIndex) {
		for(Operation o1 : this.pheromones.keySet()) {
			int i1 = opToIndex.get(o1);
			HashMap<Operation, Double> currMap = this.pheromones.get(o1);
			
			for(Operation o2 : currMap.keySet()) {
				int i2 = opToIndex.get(o2);
				double currPher = currMap.get(o2);
				double bigger = (i1 < i2) ? 1 : 0;
				currMap.put(o2, fmmas(currPher + this.evopRate*(bigger - currPher)));
			}
		}
	}
	
	private double fmmas(double d) {
		if(d < this.pherMin) {
			return this.pherMin;
		} else if(d > this.pherMax) {
			return this.pherMax;
		} else {
			return d;
		}
	}
	
	
	@Override
	public String toString() {
		String s = "";
		
		
		
		return s;
	}
}
