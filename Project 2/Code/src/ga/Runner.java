package ga;

import java.util.ArrayList;

import model.MDVRProblem;
import utils.FileHandler;
import utils.InputHandler;
import utils.OutputHandler;

public class Runner {
	
	public static void main(String[] args) {
		
		InputHandler ih = new InputHandler();
		FileHandler fh = new FileHandler();
		OutputHandler oh = new OutputHandler();
		
		if(Parameters.RUN_SINGLE_FILE) {
			System.out.println("Running " + Parameters.FILE_NAME);
			MDVRProblem problem = ih.createMDVRProblem(Parameters.FILE_NAME);
			GA ga = new GA(problem);
			ArrayList<String> solution = ga.run();
			if(solution == null) {
				System.out.println("Didn't find any feasible solutions");
			} else {
				oh.writeFile(solution, Parameters.OUTPUT_FOLDER + Parameters.FILE_NAME + ".res");
				Helper.plotWithPython(Parameters.FILE_NAME, "f", Parameters.SCRIPT_FOLDER);
				System.out.println();
			}
			return;
		}
		
		String[] inputFiles = fh.getFileNames(Parameters.INPUT_FOLDER);
		
		for(int i = 0; i < inputFiles.length; i++) {
			System.out.println("Running " + inputFiles[i]);
			MDVRProblem problem = ih.createMDVRProblem(inputFiles[i]);
			GA ga = new GA(problem);
			ArrayList<String> solution = ga.run();
			if(solution == null) {
				System.out.println("Didn't find any feasible solutions");
				i--;
			} else {
				oh.writeFile(solution, Parameters.OUTPUT_FOLDER + inputFiles[i] + ".res");
				Helper.plotWithPython(inputFiles[i], "f", Parameters.SCRIPT_FOLDER);
				System.out.println();
			}
		}
		
		String[] solutionFiles = fh.getFileNames(Parameters.OUTPUT_FOLDER);
		ArrayList<String> statistics = new ArrayList<>();
		double total = 0.0;
		
		for(int i = 0; i < solutionFiles.length; i++) {
			double optimal = Double.parseDouble(ih.getOptimalValue(Parameters.SOLUTION_FOLDER, solutionFiles[i]));
			double result = Double.parseDouble(ih.getOptimalValue(Parameters.OUTPUT_FOLDER, solutionFiles[i]));
			double percent = ((result/optimal) - 1.0)*100;
			total += percent;
			String currLine = solutionFiles[i] + ": " + String.format("%.2f", percent) + "%\n";
			statistics.add(currLine);
		}
		
		String avg = String.format("%.2f", (total/((double) solutionFiles.length)));
		statistics.add("\nAverage: " + avg + "%");
		
		oh.writeStatisticsFile(statistics);
		
		
		
	}
}
