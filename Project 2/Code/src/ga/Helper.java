package ga;

import java.io.IOException;
import java.util.Random;

import model.Chromosome;
import model.MDVRProblem;

public class Helper {

	public static double euclidean_dist(double x1, double y1, double x2, double y2) {
		double y_diff = Math.abs(y2 - y1);
		double x_diff = Math.abs(x2 - x1);
		
		return Math.sqrt((y_diff*y_diff) + (x_diff*x_diff));
	}
	
	 
	 public static int getRandomIntNotEqual(int first, int max, Random random) {
		 int second;
		 if(first == 0) {
				second = random.nextInt(max-1)+1;
			} else if(first == max-1) {
				second = random.nextInt(max-1);
			} else {
				second = (random.nextInt() % 2 == 0) ? random.nextInt(first) : random.nextInt(max-(first+1))+(first+1);
			}
			
		 return second;
	 }
	 
	 public static int compare(Chromosome o1, Chromosome o2, MDVRProblem problem) {
		 double otherFitness = o1.getFitness(problem);
		 double fitness = o2.getFitness(problem);
		
		 if(fitness < otherFitness) {
				return 1;
			} else if(fitness == otherFitness) {
				return 0;
			}
			return -1;
	 }
	 
	 public static void plotWithPython(String filename, String testing, String scriptLocation) {
		 try {
			Runtime.getRuntime().exec("python " + scriptLocation  + "main.py " + testing + " " + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
