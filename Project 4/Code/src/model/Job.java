package model;

import java.util.ArrayList;

public class Job{
	
	private final ArrayList<Operation> operations;
	private final int jobNo;
	
	public Job(int jobNo, int[] machines, int[] times, int n) {
		this.operations = new ArrayList<>();
		this.jobNo = jobNo;
		
		for(int i = 0; i < n; i++) {
			this.operations.add(new Operation(jobNo, machines[i], times[i]));
		}
	}
	
	public int getMachineNo(int n) {
		return this.operations.get(n).getMachineNo();
	}
	
	public int getProcessingTime(int n) {
		return this.operations.get(n).getTime();
	}
	
	public int getJobNo() {
		return this.jobNo;
	}
	
	public Operation getOperation(int n) {
		return this.operations.get(n);
	}
	
	public ArrayList<Operation> getOperations() {
		return this.operations;
	}
}
