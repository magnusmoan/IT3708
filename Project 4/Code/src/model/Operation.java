package model;

public class Operation {

	private final int jobNo;
	private final int machineNo;
	private final int time;
	
	public Operation(int jobNo, int machineNo, int time) {
		this.jobNo = jobNo;
		this.machineNo = machineNo;
		this.time = time;
	}
	
	public int getJobNo() {
		return this.jobNo;
	}
	
	public int getMachineNo() {
		return this.machineNo;
	}
	
	public int getTime() {
		return this.time;
	}

}
