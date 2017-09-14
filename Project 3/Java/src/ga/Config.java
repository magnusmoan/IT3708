package ga;

import java.awt.Color;

public class Config {

	public final static int POP_SIZE = 30;
	public final static Color EDGE_COLOR = new Color(255, 147, 163);
	public final static Color BLACK = new Color(0,0,0);
	public final static Color OFFWHITE = new Color(214, 207, 199);
	public final static int ARCHIEVE_SIZE = 5;
	public final static int NO_OF_GEN = 30;
	public final static double MUTATION_RATE = 0.2;
	public final static double TOURNAMENT_RATE = 0.8;
	public final static boolean OVERALL_DIV = true;
	public final static boolean EDGE = true;
	public final static boolean CONN = false;
	public final static String FOLDER = "9";
	public final static String PARETO_FRONT = "pareto_" + FOLDER + "foo.txt";
	public final static String FILENAME = "test image.jpg";
	public final static String RESULT_FOLDER = "../Images/" + FOLDER + "/Results/";
}
	