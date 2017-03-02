package utils;
import java.util.ArrayList;
import java.util.Arrays;

import ga.Parameters;
import model.MDVRProblem;
import utils.FileHandler;

public class InputHandler {
	
	public MDVRProblem createMDVRProblem(String filename) {
		String path = Parameters.INPUT_FOLDER + filename;
		ArrayList<String> content = FileHandler.getFileContent(path);
		ArrayList<String> firstLine = new ArrayList<String>(Arrays.asList(content.get(0).split("[\\s]+")));
		
		int noOfCustomers = Integer.parseInt(firstLine.get(1));
		int noOfDepots = Integer.parseInt(firstLine.get(2));
		int noOfTrucks = Integer.parseInt(firstLine.get(0));
		
		ArrayList<ArrayList<Double>> customers = new ArrayList<>();
		ArrayList<ArrayList<Double>> depots = new ArrayList<>();
		
		for(int depot = 1; depot < noOfDepots+1; depot++) {
			ArrayList<String> currLineOne = new ArrayList<String>(Arrays.asList(content.get(depot).split("[\\s]+")));
			ArrayList<String> currLineTwo = new ArrayList<String>(Arrays.asList(content.get(depot+noOfCustomers+noOfDepots).split("[\\s]+")));
			
			if(currLineOne.get(0).length() == 0) {
				currLineOne.remove(0);
			}
			
			if(currLineTwo.get(0).length() == 0) {
				currLineTwo.remove(0);
			}
			double x = Double.parseDouble(currLineTwo.get(1));
			double y = Double.parseDouble(currLineTwo.get(2));
			double maxLoad = Double.parseDouble(currLineOne.get(1));
			double maxDuration = Double.parseDouble(currLineOne.get(0));

			ArrayList<Double> currentDepot = new ArrayList<Double>();
			currentDepot.add((double) (depot-1));
			currentDepot.addAll(Arrays.asList(x, y, maxLoad, maxDuration));
			depots.add(currentDepot);
		}
		
		for(int customer = 1; customer < noOfCustomers+1; customer++) {
			int currIndex = customer + noOfDepots;
			ArrayList<String> currLine = new ArrayList<String>(Arrays.asList(content.get(currIndex).split("[\\s]+")));
			
			if(currLine.get(0).length() == 0) {
				currLine.remove(0);
			}

			double x = Double.parseDouble(currLine.get(1));
			double y = Double.parseDouble(currLine.get(2));
			double serviceDuration = Double.parseDouble(currLine.get(3));
			double demand = Double.parseDouble(currLine.get(4));
			
			ArrayList<Double> currCustomer = new ArrayList<>();
			currCustomer.add((double) (customer-1));
			currCustomer.addAll(Arrays.asList(x, y, demand, serviceDuration));
			customers.add(currCustomer);
		}
		
		return new MDVRProblem(customers, depots, noOfTrucks, noOfCustomers, noOfDepots);
	}
	
	public String getOptimalValue(String folder, String filename) {
		String path = folder + filename;
		
		return FileHandler.getFileContent(path).get(0);
	}
}
