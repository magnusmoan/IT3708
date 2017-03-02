package utils;

import java.util.ArrayList;

import ga.Parameters;

public class OutputHandler {
	
	public void writeFile(ArrayList<String> output, String filename) {
		ArrayList<String> outputWithLineBreak = new ArrayList<>();
		for(int i = 0; i < output.size(); i++){
			outputWithLineBreak.add(i, output.get(i).concat("\n"));
		}
		outputWithLineBreak.add(" ");
		
		FileHandler.writeFile(outputWithLineBreak, filename);
	}
	
	public void writeStatisticsFile(ArrayList<String> output) {
		String path = Parameters.DATA_FOLDER + Parameters.STATISTICS_FILE;
		FileHandler.writeFile(output, path);
	}
}
