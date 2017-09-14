package model;

import java.util.ArrayList;
import java.util.Arrays;

import utils.Pair;

public class Schedule {

	private int nJobs;
	private int nMachines;
	private int[] operations;
	private int[] nextAvailableMachineTime;
	private int[] nextAvailableJobTime;
	private int[] nextActivity;
	private int[][] information;
	private int makespan;
	
	private void init(JSSPInstance instance) {
		this.nJobs = instance.getNJobs();
		this.nMachines = instance.getNMachines();
		this.information = new int[nJobs*nMachines][4];
	}
	
	public Schedule(int[] operations, JSSPInstance instance) {
		init(instance);
		this.operations = operations;
		createSchedule(instance);
	}
	
	public Schedule(double[] position, JSSPInstance instance) {
		this.nJobs = instance.getNJobs();
		this.nMachines = instance.getNMachines();
		this.information = new int[nJobs*nMachines][4];
		int size = position.length;
		this.operations = new int[size];
		int[] rank = new int[size];
		
		Pair[] pairs = new Pair[size];
		for(int i = 0; i < size; i++) {
			pairs[i] = new Pair(position[i], i);
		}
		Arrays.sort(pairs, (p1, p2) -> (int) Math.signum(p1.value - p2.value));
		
		int currRank = 0;
		for(Pair pair : pairs) {
			rank[pair.index] = currRank++;
		}
		
		for (int i = 0; i < size; i++) {
			operations[i] = rank[i] % nJobs;
		}
		
		createSchedule(instance);
	}
	
	private void createSchedule(JSSPInstance instance) {
		this.nextActivity =  new int[nJobs];
		this.nextAvailableJobTime = new int[nJobs];
		this.nextAvailableMachineTime = new int[nMachines];
		Arrays.fill(this.nextAvailableJobTime, 0);
		Arrays.fill(this.nextAvailableMachineTime, 0);
		this.makespan = 0;
		
		for(int i = 0; i < nJobs*nMachines; i++) {
			int jobNo = 0;
			jobNo = this.operations[i];
			int currActivityNo = nextActivity[jobNo];
			int machineNo = instance.getActivity(jobNo, currActivityNo);
			int startTime = Math.max(nextAvailableJobTime[jobNo], nextAvailableMachineTime[machineNo]);
			int endTime = instance.getProcessingTime(jobNo, currActivityNo) + startTime;
			information[i] = new int[] {machineNo, jobNo, startTime, endTime};
			
			nextActivity[jobNo] = currActivityNo + 1;
			nextAvailableJobTime[jobNo] = endTime;
			nextAvailableMachineTime[machineNo] = endTime;
			
			if(endTime > makespan) {
				makespan = endTime;
			}
		}
			
	}
	
	public int getMakespan() {
		return makespan;
	}
	
	public ArrayList<String> getResultRepresentation() {
		ArrayList<String> result = new ArrayList<>();
		result.add(this.toString());
		return result;
	}
	
	public int[][] getInformation() {
		return this.information;
	}
	
	
	
	@Override
	public String toString() {
		String s = nJobs + " " + makespan + "\n";
		for(int i = 0; i < information.length; i++) {
			s += information[i][0] + " " + information[i][1] + " " + information[i][2] + " " + information[i][3] + "\n";
		}
		return s.substring(0, s.length()-1);
	}
}
