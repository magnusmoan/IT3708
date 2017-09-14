package algorithms;

import model.JSSPInstance;
import model.Schedule;
import utils.FileHandler;
import utils.Parameters;
import utils.PythonHandler;
import utils.Statistics;

public class Runner {

	public static void main(String[] argv) {
		
		FileHandler fh = new FileHandler();
		JSSPInstance instance = new JSSPInstance(fh.getFileContent(Parameters.INPUT_PATH));
		System.out.println(instance);
		
		Algorithm algorithm;
		switch(Parameters.ALGORITHM) {
			case "pso": algorithm = new PSO(instance); break;
			case "ba":  algorithm = new BA(instance); break;
			case "aco": algorithm = new ACO(instance); break;
			default: algorithm = new PSO(instance);
		}
		
		Schedule schedule = algorithm.run();
		
		if(Parameters.WRITE_FILE) { fh.writeFile(schedule.getResultRepresentation(), Parameters.OUTPUT_PATH); }
		if(Parameters.WRITE_STAT) { (new Statistics()).writeStatistics(fh); }
		if(Parameters.PLOT) { (new PythonHandler()).plotWithPython(); }
	}
	
}
