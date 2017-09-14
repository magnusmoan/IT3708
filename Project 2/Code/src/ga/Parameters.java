package ga;

public class Parameters {

	public final static int POP_SIZE = 500;
	public final static int NO_OF_GENERATIONS = 200;
	
	// Selection variables
	public final static int NO_OF_ELITE = (int) Math.floor(POP_SIZE*0.02);
	public final static double SELECT_BEST = .2;
	public final static double MUTATION_RATE = .1;
	
	// Generate next population variables
	public final static double CROSSOVER_RATE = .9;
	public final static double MUTATE_INTERNAL_OR_EXTERNAL = .9;
	public final static double MUTATE_INTERNAL_RATE = .5;
	
	// Fitness function variables
	public final static double DEMAND_PENALTY = 10;
	public final static double DURATION_PENALTY = 10;
	
	public final static boolean RUN_SINGLE_FILE = true;
	public final static String INPUT_FOLDER = "../Data/Data Files/";
	public final static String OUTPUT_FOLDER = "../Data/Results/";
	public final static String TEST_FOLDER = "../Data/Test Files/";
	public final static String SOLUTION_FOLDER = "../Data/Solution Files/";
	public final static String DATA_FOLDER = "../Data/";
	public final static String STATISTICS_FILE = "statistics";
	public final static String SCRIPT_FOLDER = "../Plotter/";
	public final static String FILE_NAME = "p27";
}
