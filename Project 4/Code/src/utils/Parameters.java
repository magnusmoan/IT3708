package utils;

public class Parameters {

	// General parameters
	public final static String ALGORITHM = "aco";
	public final static String FILE_NAME = "7";
	public final static String INPUT_FILE = FILE_NAME + ".txt";
	public final static String INPUT_FOLDER = "../Test Data/";
	public final static String INPUT_PATH = INPUT_FOLDER + INPUT_FILE;
	public final static String OUTPUT_FILE = ALGORITHM + "_" + FILE_NAME + ".res";
	public final static String OUTPUT_FOLDER = "../Results/txt/";
	public final static String OUTPUT_PATH = OUTPUT_FOLDER + OUTPUT_FILE;
	public final static String PYTHON_PATH = "../Plotter/plotter.py";
	public final static String PYTHON_EXE = "/usr/local/bin/python3";
	public final static String OPTIMAL_VALUES_PATH = "../Results/optimal_values";
	public final static String STATISTICS_PATH = "../Results/statistics";
	public final static boolean PLOT = true;
	public final static boolean WRITE_STAT = true;
	public final static boolean WRITE_FILE = true;
	
	// PSO parameters
	public final static double C1 = 2.0;		// Self learning factor
	public final static double C2 = 2.0;		// Social learning factor
	public final static double INITIAL_INERTIAL_WEIGHT = 1.4;
	public final static double FINAL_INERTIAL_WEIGHT = 0.4;
	public final static int PSO_ITERATIONS = 500;
	public final static double LOCAL_SEARCH_PROB = .01;
	public final static int N_PARTICLES = 30;
	public final static double MIN_TEMP = 0.01;
	public final static double BETA = 0.999;
	public final static double PSO_MOVE_ONE = 0.4;
	public final static double PSO_SWAP_TWO = 0.8;
	public final static double PSO_INVERT = 0.9;
	
	// BA parameters
	public final static int BA_ITERATIONS = 500;
	public final static int N_BEES = 250;
	public final static int N_ELITE_BEES = 30;
	public final static int N_ACC_BEES = 60;
	public final static int MOVES_ELITE = 200;
	public final static int MOVES_ACC = 10;
	public final static double BA_MOVE_ONE = 0.4;
	public final static double BA_SWAP_TWO = 0.8;
	public final static double BA_INVERT = 0.9;
	
	// ACO parameters
	public final static int ACO_ITERATIONS = 5000;
	public final static double ANT_BETA = 10.0;
	public final static double PHEROMONE_MIN = 0.001;
	public final static double PHEROMONE_MAX = 0.999;
	public final static double EVOP_RATE = 0.1;
	public final static double ANT_ND = .2;
	public final static double ANT_GT = .6;
	public final static double ANT_MOVE_ONE = 0.4;
	public final static double ANT_SWAP_TWO = 0.8;
	public final static double ANT_INVERT = 0.9;
	public final static int ANT_ELITE_SEARCH_ITER = 200;
	public final static int ANT_NORMAL_SEARCH_ITER = 10;
}

