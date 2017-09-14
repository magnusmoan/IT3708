package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JSSPInstance{

	private HashMap<Integer, Job> jobs;
	private int nJobs;
	private int nMachines;
	
	public JSSPInstance(ArrayList<String> input) {
		
		this.jobs = new HashMap<>();
		
		String firstLineString = input.get(0).replaceAll("\\s+", " ");
		ArrayList<String> firstLine = new ArrayList<String>(Arrays.asList(firstLineString.split("[\\s]+")));
		
		if(firstLine.get(0).length() == 0) {
			firstLine.remove(0);
		}
		
		this.nJobs = Integer.parseInt(firstLine.get(0));
		this.nMachines = Integer.parseInt(firstLine.get(1));
		
		for(int line = 1; line < this.nJobs+1; line++) {
			String currS = input.get(line).replaceAll("\\s+", " ");
			ArrayList<String> currLine = new ArrayList<String>(Arrays.asList(currS.split("[\\s]+")));
			
			if(currLine.get(0).length() == 0) {
				currLine.remove(0);
			}
			
			ArrayList<Integer> currInfo = new ArrayList<Integer>();
			
			for(String s : currLine) {
				currInfo.add(Integer.valueOf(s));
			}
			
			int[] machines = new int[nMachines];
			int[] times = new int[nMachines];
			
			for(int i = 0; i < 2*nMachines; i += 2) {
				machines[i/2] = currInfo.get(i);
				times[i/2] = currInfo.get(i+1);
			}
			
			int jobNumber = line - 1;
			Job job = new Job(line-1, machines, times, nMachines);
			
			jobs.put(jobNumber, job);			
		}
	}
	
	public int getNJobs() {
		return nJobs;
	}
	
	public int getNMachines() {
		return nMachines;
	}
	
	public int getActivity(int jobNo, int activityNo) {
		return jobs.get(jobNo).getMachineNo(activityNo);
	}
	
	public int getProcessingTime(int jobNo, int activityNo) {
		return jobs.get(jobNo).getProcessingTime(activityNo);
	}
	
	public Job getJob(int jobNo) {
		return jobs.get(jobNo);
	}
	
	public Operation getOperation(int jobNo, int machineNo) {
		ArrayList<Operation> operations = jobs.get(jobNo).getOperations();
		for(Operation o : operations) {
			if(o.getMachineNo() == machineNo) {
				return o;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		String s = "JSSP Instance:\n";
		for(int jobNo = 0; jobNo < nJobs; jobNo++) {
			s += "Job " + jobNo + ": ";
			Job currJob = jobs.get(jobNo);
			for(int machineNo = 0; machineNo < nMachines; machineNo++) {
				s += "M" + currJob.getMachineNo(machineNo) + "(" + currJob.getProcessingTime(machineNo) + ") - ";
			}
			s = s.substring(0, s.length()-3);
			s += "\n";
		}
		return s;
	}
}
