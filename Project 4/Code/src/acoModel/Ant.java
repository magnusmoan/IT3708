package acoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import model.Individual;
import model.JSSPInstance;
import model.Job;
import model.Operation;
import model.Schedule;
import utils.Parameters;

public class Ant extends Individual{

	private ArrayList<Operation> path;
	private int[] nextActivityIndex;
	private int[] nextMachineTime;
	private int[] nextJobTime;
	private HashMap<Operation, Integer> opToIndex;
	private HashSet<Operation> remainingOperations;
	private ArrayList<Operation> allOperations;
	private int nJob;
	private int nMachine;
	private Graph graph;
	
	public Ant(JSSPInstance instance, Graph graph) {
		super(instance);
		this.nJob = this.instance.getNJobs();
		this.nMachine = this.instance.getNMachines();
		this.nextActivityIndex =  new int[this.nJob];
		this.nextJobTime = new int[this.nJob];
		this.nextMachineTime = new int[this.nMachine];
		this.graph = graph;
		this.opToIndex = new HashMap<>();
		this.remainingOperations = new HashSet<>();
		this.allOperations = new ArrayList<>();
		
		for(int i = 0; i < nJob; i++) {
			Job job = instance.getJob(i);
			
			for(int j = 0; j < nMachine; j++) {
				allOperations.add(job.getOperation(j));
			}
		}
	}
	
	public void createPath() {
		this.makespan = 0;
		this.path = new ArrayList<Operation>();
		this.remainingOperations = new HashSet<>(allOperations);
		Arrays.fill(this.nextActivityIndex, 0);
		Arrays.fill(this.nextJobTime, 0);
		Arrays.fill(this.nextMachineTime, 0);
		ArrayList<Operation> actualChoices;
		ArrayList<Operation> possibleChoices = new ArrayList<>();
		
		
		for(int i = 0; i < this.nJob; i++) {
			possibleChoices.add(this.instance.getJob(i).getOperation(0));
		}
		
		// Choose restrict method
		double r = random.nextDouble();
		int chosen;
		if(r < Parameters.ANT_GT) {
			chosen = 0;
		} else if(r < Parameters.ANT_ND) {
			chosen = 1;
		} else {
			chosen = 2;
		}
		
		int indexOfFirst = random.nextInt(this.nJob);
		Operation operation = instance.getJob(indexOfFirst).getOperation(0);
		this.path.add(operation);
		
		updateTimesAndNext(operation);
		updatePossibleChoices(operation, possibleChoices);
		this.opToIndex.put(operation, 0);
		
		
		for(int i = 0; i < this.size-1; i++) {
			if(chosen == 0) {
				actualChoices = RestrictActiveSchedule(possibleChoices);
			} else if(chosen == 1) {
				actualChoices = RestrictNoDelay(possibleChoices);
			} else {
				actualChoices = possibleChoices;
			}
			
			operation = choose(actualChoices);
			path.add(operation);
			updateTimesAndNext(operation);
			updatePossibleChoices(operation, possibleChoices);
			this.opToIndex.put(operation, i);
		}
	}
	
	private void updateTimesAndNext(Operation operation) {
		this.nextActivityIndex[operation.getJobNo()]++;
		int time = operation.getTime();
		int startTime = Math.max(this.nextMachineTime[operation.getMachineNo()], this.nextJobTime[operation.getJobNo()]);
		int endTime = startTime + time;
		this.nextMachineTime[operation.getMachineNo()] = endTime;
		this.nextJobTime[operation.getJobNo()] = endTime;
		
		if(endTime > this.makespan) {
			this.makespan = endTime;
		}
	}
	
	private void updatePossibleChoices(Operation operation, ArrayList<Operation> possibleChoices) {
		possibleChoices.remove(operation);
		remainingOperations.remove(operation);
		int jobNo = operation.getJobNo();
		if(this.nextActivityIndex[jobNo] == this.nMachine) {
			return;
		}
		
		possibleChoices.add(this.instance.getJob(jobNo).getOperation(this.nextActivityIndex[jobNo]));
	}
	
	private ArrayList<Operation> RestrictNoDelay(ArrayList<Operation> possibleChoices) {
		ArrayList<Operation> choices =  new ArrayList<>();
		int minStartTime = Integer.MAX_VALUE;
		
		int[] startTimes = new int[possibleChoices.size()];
		
		for(int i = 0; i < possibleChoices.size(); i++) {
			Operation operation = possibleChoices.get(i);
			int currTime = Math.max(this.nextJobTime[operation.getJobNo()], this.nextMachineTime[operation.getMachineNo()]);
			if(currTime < minStartTime) {
				minStartTime = currTime;
			}
			startTimes[i] = currTime;
		}
		
		for(int i = 0; i < possibleChoices.size(); i++) {
			Operation operation = possibleChoices.get(i);
			if(startTimes[i] == minStartTime) {
				choices.add(operation);
			}
		}
		
		return choices;
	}
	
	private ArrayList<Operation> RestrictActiveSchedule(ArrayList<Operation> possibleChoices) {
		ArrayList<Operation> choices =  new ArrayList<>();
		ArrayList<Integer> machineNo = new ArrayList<>();
		int[] endTimes = new int[possibleChoices.size()];
		int[] startTimes = new int[possibleChoices.size()];
		int minEndTime = Integer.MAX_VALUE;
		
		for(int i = 0; i < possibleChoices.size(); i++) {
			Operation o = possibleChoices.get(i);
			int jobNo = o.getJobNo();
			int startTime = Math.max(this.nextJobTime[jobNo], this.nextMachineTime[o.getMachineNo()]);
			int endTime = startTime + instance.getProcessingTime(jobNo, this.nextActivityIndex[jobNo]);
			if(endTime < minEndTime) {
				minEndTime = endTime;
			}
			endTimes[i] = endTime;
			startTimes[i] = startTime;
		}
		
		for(int i = 0; i < possibleChoices.size(); i++) {
			Operation o = possibleChoices.get(i);
			if(endTimes[i] == minEndTime) {
				machineNo.add(o.getMachineNo());
			}
		}
		
		int chosenM = machineNo.get(random.nextInt(machineNo.size()));
		
		for(int i = 0; i < possibleChoices.size(); i++) {
			Operation o = possibleChoices.get(i);
			if(o.getMachineNo() == chosenM && startTimes[i] < minEndTime) {
				choices.add(o);
			}
		}
		return choices;
	}
	
	private Operation choose(ArrayList<Operation> choices) {
		double[] prob = new double[choices.size()];
		double[] heuristics = calcHeuristics(choices);
		double sum = 0.0;
		
		if(choices.size() == 1) {
			return choices.get(0);
		}
		
		
		for(int i = 0; i < choices.size(); i++) {
			Operation o1 = choices.get(i);
			int m1 = o1.getMachineNo();
			double currMin = Integer.MAX_VALUE;
			for(Operation o2 : remainingOperations) {
				int m2 = o2.getMachineNo();
				if(m1 == m2 && o1.getJobNo() != o2.getJobNo()) {
					double pheromone = this.graph.getPheromone(o1, o2);
					double currVal = pheromone * Math.pow(heuristics[i], Parameters.ANT_BETA);
					if(currVal < currMin) {
						currMin = currVal;
					}
				}
			}
			
			prob[i] = currMin;
			sum += currMin;
			
		}
		
		prob[0] /= sum;
		
		for(int i = 1; i < choices.size(); i++) {
			prob[i] = (prob[i]/sum) + prob[i-1];
		}
		
		for(int i = 0; i < prob.length; i++) {
			if(Double.isNaN(prob[i])) {
				return choices.get(i);
			}
		}
		
		double chosenProb = random.nextDouble();
		
		for(int i = 0; i < choices.size(); i++) {
			if(chosenProb < prob[i]) {
				return choices.get(i);
			}
		}
		
		return null;
	}
	
	private double[] calcHeuristics(ArrayList<Operation> choices) {
		double[] earliestStart = new double[choices.size()];
		double[] heuristics = new double[choices.size()];
		double sum = 0.0;
		
		for(int i = 0; i < choices.size(); i++) {
			Operation operation = choices.get(i);
			earliestStart[i] = 1.0/(Math.max(this.nextJobTime[operation.getJobNo()], this.nextMachineTime[operation.getMachineNo()])+1.0);
			sum += earliestStart[i];
		}
		
		for(int i = 0; i < choices.size(); i++) {
			heuristics[i] = earliestStart[i]/sum;
		}
		
		return heuristics;
	}
	
	public int[] toOperations() {
		int[] operations = new int[this.size];
		
		for(int i = 0; i < this.size; i++) {
			operations[i] = this.path.get(i).getJobNo();
		}
		
		return operations;
	}
	
	public HashMap<Operation, Integer> getOpToIndex() {
		return this.opToIndex;
	}
	
	@Override
	public int getMakespan() {
		return this.makespan;
	}
	
	public ArrayList<Operation> getPath() {
		return this.path;
	}
	
	public void tabuSearch(int iterations) {
		int[] operations = toOperations();
		this.schedule = new Schedule(operations, instance);
		
		for(int move = 0; move < iterations; move++) {
			int[] candidatePos;
			double r = random.nextDouble();
			if(r < Parameters.ANT_MOVE_ONE) {
				candidatePos = moveSingleOperation(operations);
			} else if (r < Parameters.ANT_SWAP_TWO){
				candidatePos = twoSwap(operations);
			} else if (r < Parameters.ANT_INVERT){
				candidatePos = inversion(operations);
			} else {
				candidatePos = longDistanceSwap(operations);
			}
			Schedule candidateSch = new Schedule(candidatePos, instance);
			int candMakespan = candidateSch.getMakespan();
			
			if(candMakespan < makespan) {
				operations = candidatePos;
				this.makespan = candMakespan;
				this.schedule = candidateSch;
				changed = true;	
			}
		}
		toPath();
	}
	
	private void toPath() {
		int[][] info = this.schedule.getInformation();
		this.path.clear();
		
		for(int i = 0; i < this.size; i++) {
			Operation o = instance.getOperation(info[i][1], info[i][0]);
			this.path.add(o);
			this.opToIndex.put(o, i);
		}
	}
	
	private int[] moveSingleOperation(int[] position) {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		while(r1 == r2) {
			r2 = random.nextInt(size);
		}
		
		int[] newPosition = new int[size];
		System.arraycopy(position, 0, newPosition, 0, size);
		
		int chosen = newPosition[r1];
		
		if(r1 > r2) {
			for(int i = r1; i > r2; i--) {
				newPosition[i] = newPosition[i-1];
			}
		} else {
			for(int i = r1; i < r2; i++) {
				newPosition[i] = newPosition[i+1];
			}
		}
		
		newPosition[r2] = chosen;
		changed = true;
	
		return newPosition;
	}
	
	private int[] twoSwap(int[] position) {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		return this.swap(position, r1, r2);
		
	}
	
	private int[] swap(int[] position, int r1, int r2) {
		int[] newPosition = new int[size];
		System.arraycopy(position, 0, newPosition, 0, size);
		int f1 = newPosition[r1];
		int f2 = newPosition[r2];
		newPosition[r1] = f2;
		newPosition[r2] = f1;
		changed = true;
		
		return newPosition;
	}
	
	private int[] inversion(int[] position) {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		while(r1 == r2) {
			r2 = random.nextInt(size);
		}
		
		if(r2 < r1) {
			int intermediate = r2;
			r2 = r1;
			r1 = intermediate;
		}
		
		if(r2 - r1 == 1) {
			return swap(position, r1, r2);
		} else {
			int[] newPosition = new int[size];
			System.arraycopy(position, 0, newPosition, 0, size);
			
			for(int i = 0; i < (r2-r1)-1; i++) {
				int intermediate = newPosition[r1+i];
				newPosition[r1+i] = newPosition[r2-i];
				newPosition[r2-i] = intermediate;
			}
			
			return newPosition;
		}
	}
	
	private int[] longDistanceSwap(int[] position) {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		
		while(r1 == r2) {
			r2 = random.nextInt(size);
		}
		
		if(r2 < r1) {
			int intermediate = r2;
			r2 = r1;
			r1 = intermediate;
		}
		
		int[] newPosition = new int[size];
		int[] front = new int[size-r2];
		int[] middle = new int[r2-r1];
		int[] back = new int[r1];
		
		for(int i = 0; i < r1; i++) {
			back[i] = position[i];
		}
		
		for(int i = 0; i < size-r2; i++) {
			front[i] = position[r2+i];
		}
		
		for(int i = 0; i < r2-r1; i++) {
			middle[i] = position[r1+i];
		}
		
		for(int i = 0; i < front.length; i++) {
			newPosition[i] = front[i];
		}
		
		for(int i = 0; i < middle.length; i++) {
			newPosition[front.length+i] = middle[i];
		}
		
		for(int i = 0; i < back.length; i++) {
			newPosition[front.length+middle.length+i] = back[i];
		}
		
		
		return newPosition;
	}
}
