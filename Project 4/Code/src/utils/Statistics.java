package utils;

import java.util.ArrayList;
import java.util.HashMap;


public class Statistics {

	public void writeStatistics(FileHandler fh) {
		String[] fileNames = fh.getFileNames(Parameters.OUTPUT_FOLDER);
		ArrayList<String> statistics = new ArrayList<String>();
		
		HashMap<Integer, String[]> aco = new HashMap<>();
		HashMap<Integer, String[]> pso = new HashMap<>();
		HashMap<Integer, String[]> ba = new HashMap<>();
		
		int maxProblemNo = 0;
		
		for(int i = 0; i < fileNames.length; i++) {
			String file = fileNames[i];
			int problemNo = Integer.parseInt(file.replaceAll("[\\D]", ""));
			int makespan = Integer.parseInt(fh.getMakespanFromFile(file));
			String[] optimalValues = fh.getOptimalValues(problemNo-1);
			double optimal = Double.parseDouble(optimalValues[1]);
			double acceptable = Double.parseDouble(optimalValues[0]);
			
			Double p1 = ((makespan/optimal) - 1.0) * 100;
			Double p2 = ((makespan/acceptable) - 1.0) * 100;
			
			if(p2 < 0.0) { p2 = 0.0; }
			
			String p1f = String.format("%.2f", p1) + "%";
			String p2f = String.format("%.2f", p2) + "%";
			
			String[] info = file.split("_");
			String[] percents = new String[] {p1f, p2f};
			
			if(info[0].equals("aco")) {
				aco.put(problemNo, percents);
			} else if(info[0].equals("ba")) {
				ba.put(problemNo, percents);
			} else if(info[0].equals("pso")) {
				pso.put(problemNo, percents);
			}
			
			if(problemNo > maxProblemNo) {
				maxProblemNo = problemNo;
			}
			
		}
		String h1 =    "*** PERCENT AWAY FROM OPTIMUM ***\n";
		statistics.add(h1);
		String firstLine = "         ACO       PSO       BA\n";
		statistics.add(firstLine);
		addToStatistics(0, maxProblemNo, aco, pso, ba, statistics);
		String h2 = "\n*** PERCENT AWAY FROM ACCEPTABLE ***\n";
		statistics.add(h2);
		statistics.add(firstLine);
		addToStatistics(1, maxProblemNo, aco, pso, ba, statistics);
		
		fh.writeFile(statistics, Parameters.STATISTICS_PATH);

	}
	
	private String appendToString(String d, String s) {
		s += String.format("%1$10s", d);
		return s;
	}
	
	private void addToStatistics(int n, int maxProblemNo, HashMap<Integer, String[]> aco, HashMap<Integer, String[]> pso, HashMap<Integer, String[]> ba, ArrayList<String> statistics) {
		for(int i = 1; i < maxProblemNo+1; i++) {
			String currLine = "" + i + "  ";
			if(aco.containsKey(i)) {
				currLine = appendToString(aco.get(i)[n], currLine);
			} else {
				currLine = appendToString("", currLine);
			}
			
			if(pso.containsKey(i)) {
				currLine = appendToString(pso.get(i)[n], currLine);
			} else {
				currLine = appendToString("", currLine);
			}
			
			if(ba.containsKey(i)) {
				currLine = appendToString(ba.get(i)[n], currLine);
			} else {
				currLine = appendToString("", currLine);
			}
			
			currLine += "\n";
			statistics.add(currLine);
		}
	}
	
}
