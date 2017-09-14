package ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;



public class SPEA {

	private ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes;
	private int pixels;
	private Random random;
	private int densityFactor;
	private int totalSize;
	private final int edge;
	private final int overall_dev;
	private final int conn;
	
	public SPEA(ImgConverter converter) {
		nodes = converter.getNodes();
		pixels = converter.getNumberOfPixels();
		random = new Random();
		totalSize = Config.ARCHIEVE_SIZE + Config.POP_SIZE;
		densityFactor = (int) Math.floor(Math.sqrt(totalSize));
		if(Config.OVERALL_DIV) { overall_dev = 1; } else { overall_dev = 0; }
		if(Config.EDGE) {edge = 1; } else { edge  = 0; }
		if(Config.CONN) {conn = 1; } else { conn = 0; }
		
	}
	
	public ArrayList<int[]> run() {
		
		Initializer init = new Initializer(nodes);
		init.initialize();
		System.out.println();
		System.out.println("Initialization done. Starting SPEA...");
		ArrayList<int[]> population = init.getInitialPop();
		
		ArrayList<int[]> archive = new ArrayList<int[]>();
		ArrayList<int[]> parents;
		ArrayList<Integer> archiveCandidates;
		ArrayList<Tuple<Integer, Double>> fitness = new ArrayList<>();
		double[][] objectives;

		for(int round = 1; round < Config.NO_OF_GEN+1; round++) {
			System.out.println("Generation number: " + round);
			ArrayList<int[]> all = new ArrayList<int[]>();
			all.addAll(population); all.addAll(archive);
			objectives = calculateObjectives(all);
			fitness = calculateFitnessAll(all, objectives);
			archiveCandidates = findArchiveCandidates(fitness);
			if(archiveCandidates.size() > Config.ARCHIEVE_SIZE) { shrinkArchive(archiveCandidates, objectives); }
			else if(archiveCandidates.size() < Config.ARCHIEVE_SIZE) { growArchive(archiveCandidates, fitness, population); }
			archive = fillArchive(archiveCandidates, all);
			parents = selectParents(all, fitness);
			population = crossover(parents);
			for(int[] c : population) {
				if(random.nextDouble() < Config.MUTATION_RATE) {
					mutate(c);
				}
			}
		}
		return getUniqueSolutions(population);
	}
	
	private ArrayList<int[]> getUniqueSolutions(ArrayList<int[]> archive) {
		double[][] obj = calculateObjectives(archive);
		ArrayList<Integer> nonDominated = findArchiveCandidates(calculateFitnessAll(archive, obj));
		ArrayList<int[]> solutions = fillArchive(nonDominated, archive);
		Helpers.removeSimilar(solutions);
		return solutions;
	}
	
	private void shrinkArchive(ArrayList<Integer> archiveCandidates, double[][] objectives) {
		while(archiveCandidates.size() > Config.ARCHIEVE_SIZE) {
			ArrayList<Tuple<Integer, Double>> densities =  new ArrayList<>();
			for(int candidate : archiveCandidates) {
				densities.add(new Tuple<Integer, Double>(candidate, calcDensity(objectives, archiveCandidates, candidate)));
			}
			Collections.sort(densities);
			archiveCandidates.remove(densities.get(0).getIndex());
		}
	}
	
	private void growArchive(ArrayList<Integer> archiveCandidates, ArrayList<Tuple<Integer, Double>> fitness, ArrayList<int[]> population) {
		int i = archiveCandidates.size();
		while(archiveCandidates.size() < Config.ARCHIEVE_SIZE) {
			archiveCandidates.add(fitness.get(i).getIndex());
			i++;
		}
	}
	
	private ArrayList<Integer> findArchiveCandidates(ArrayList<Tuple<Integer, Double>> fitness) {
		ArrayList<Integer> archiveCandidates = new ArrayList<>();
		Collections.sort(fitness);
		for(Tuple<Integer, Double> f : fitness) {
			if(f.getValue() < 1) {
				archiveCandidates.add(f.getIndex());
			}
		}
		return archiveCandidates;
	}
	
	private ArrayList<int[]> fillArchive(ArrayList<Integer> candidates, ArrayList<int[]> population) {
		ArrayList<int[]> archive = new ArrayList<>();
		for(int candidate : candidates) {
			archive.add(population.get(candidate));
		}
		return archive;
	}
	
	public double[][] calculateObjectives(ArrayList<int[]> population) {
		double[][] objectives = new double[population.size()][3];
		for(int i = 0; i < population.size(); i++) {
			int[] segmentation = getSegmentation(population.get(i));
			objectives[i] = calcObjective(segmentation);
		}
		
		return objectives;
	}
	
	private ArrayList<Tuple<Integer, Double>> calculateFitnessAll(ArrayList<int[]> population, double[][] objectives) {
		ArrayList<Tuple<Integer, Double>> fitness = new ArrayList<>();
		HashMap<Integer, ArrayList<Integer>> dominates = new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> dominatedBy = new HashMap<>();
		
		findDominates(dominates, dominatedBy, objectives);
		
		for(int i = 0; i < objectives.length; i++) {
			fitness.add(new Tuple<Integer, Double>(i,calcFitnessOne(dominates, dominatedBy, objectives, i)));
		}
		
		return fitness;
	}
	
	private void findDominates(HashMap<Integer, ArrayList<Integer>> dominates, HashMap<Integer, ArrayList<Integer>> dominatedBy, double[][] objectives) {
		for(int i = 0; i < objectives.length; i++) {
			ArrayList<Integer> currDominates = new ArrayList<>();
			ArrayList<Integer> currDominatedBy = new ArrayList<>();
			for(int j = 0; j < objectives.length; j++) {
				if(j == i) { continue; }
				if(doesDominate(objectives[i], objectives[j])) {
					currDominates.add(j);
				}
				if(doesDominate(objectives[j], objectives[i])) {
					currDominatedBy.add(j);
				}
			}
			dominates.put(i, currDominates);
			dominatedBy.put(i, currDominatedBy);
		}
	}
	
	private boolean doesDominate(double[] o1, double[] o2) {
		return (!Config.OVERALL_DIV || (o1[0] < o2[0])) && 
			   (!Config.EDGE || (o1[1] < o2[1])) && 
			   (!Config.CONN || (o1[2] < o2[2])); 
	}
	
	private double calcFitnessOne(HashMap<Integer, ArrayList<Integer>> dominates, HashMap<Integer, ArrayList<Integer>> dominatedBy, double[][] objectives, int cNo) {
		double rawFitness = calcRawFitness(dominatedBy, dominates, objectives, cNo);
		double density = calcDensity(objectives, cNo);
		return rawFitness + density;
	}
	
	private int calcRawFitness(HashMap<Integer, ArrayList<Integer>> dominatedBy, HashMap<Integer, ArrayList<Integer>> dominates, double[][] objectives, int cNo) {
		int rawFitness = 0;
		for(int i : dominatedBy.get(cNo)) {
			rawFitness += dominates.get(i).size();
		}
		
		return rawFitness;
	}
	
	private double calcDensity(double[][] objectives, int chromosomeNo) {
		ArrayList<Double> dist = new ArrayList<Double>();
		for(int i = 0; i < objectives.length; i++) {
			dist.add(objectiveDistance(objectives, chromosomeNo, i));
		}
		Collections.sort(dist);
		
		return 1.0/(dist.get(densityFactor) + 2);
	}
	
	public int[] getSegmentation(int[] chromosome){
		int[] segment = new int[chromosome.length];
		Arrays.fill(segment, -1);
		int segmentIn = 0;
		for (int i = 0; i < chromosome.length; i++) {
			ArrayList<Integer> previous = new ArrayList<Integer>();
			int cluster = 1;
			if (segment[i] == -1){
				previous.add(i);
				segment[i] = segmentIn;
				int x = chromosome[i];
				cluster += 1;
				while(segment[x] == -1){
					segment[x] = segmentIn;
					previous.add(x);
					x = chromosome[x];
					cluster += 1;		
				}if(segment[x] != segmentIn){
					int newSegment = segment[x];
					cluster -= 1;
					while(cluster >= 1){
						segment[previous.get(cluster-1)] = newSegment;
						cluster -= 1;
					}
				}else{
					segmentIn += 1;
				}
			}
		}
		return segment;
	}
	
	private ArrayList<int[]> selectParents(ArrayList<int[]> population, ArrayList<Tuple<Integer, Double>> fitness) {
		ArrayList<int[]> parents = new ArrayList<>();
		while(parents.size() < Config.POP_SIZE) {
			int r1 = random.nextInt(population.size());
			int r2 = getRandomIntNotEqual(r1, population.size());
			Tuple<Integer, Double> first = fitness.get(r1);
			Tuple<Integer, Double> second = fitness.get(r2);
			if(random.nextDouble() < Config.TOURNAMENT_RATE) {
				if(first.getValue() < second.getValue()) {
					parents.add(Arrays.copyOf(population.get(first.getIndex()), pixels));
				} else {
					parents.add(Arrays.copyOf(population.get(second.getIndex()), pixels));
				}
			} else {
				parents.add(Arrays.copyOf(population.get(first.getIndex()), pixels));
				parents.add(Arrays.copyOf(population.get(second.getIndex()), pixels));
			}
		}
		return parents;
	}
	
	private HashMap<Integer, ArrayList<Integer>> getSegments(int[] segmentations) {
		HashMap<Integer, ArrayList<Integer>> segments = new HashMap<>();
		for(int i = 0; i < segmentations.length; i++) {
			int curr = segmentations[i];
			if(!segments.containsKey(curr)) {
				segments.put(curr, new ArrayList<>());
			}
			segments.get(curr).add(i);
		}
		return segments;
	}
	
	
	private ArrayList<int[]> crossover(ArrayList<int[]> parents) {
		ArrayList<int[]> population = new ArrayList<>();
		for(int i = 0; i < Config.POP_SIZE; i++) {
			int r1 = random.nextInt(parents.size());
			int r2 = getRandomIntNotEqual(r1, parents.size());
			int r3 = random.nextInt(pixels);
			int[] p1 = parents.get(r1);
			int[] p2 = parents.get(r2);
			int[] chromosome = new int[pixels];
			System.arraycopy(p1, 0, chromosome, 0, r3);
			System.arraycopy(p2, r2, chromosome, r2, pixels-r2);
			population.add(chromosome);
		}
		
		return population;
	}
	
	private void mutate(int[] chromosome) {
		int[] segmentation = getSegmentation(chromosome);
		HashMap<Integer, ArrayList<Integer>> segments = getSegments(segmentation);
		int r = random.nextInt(segments.size());
		HashMap<Integer, int[]> avgRGB = Helpers.calcAvgRGB(segments, nodes);
		int mostSim = Helpers.mergeWithMostSimilar(segments, r, segmentation, avgRGB, nodes);
		Helpers.setEdgesInSegment(chromosome, segments.get(mostSim), segmentation, mostSim, nodes);
	}
	
	private double[] calcObjective(int[] nodeToSeg) {
		double[] answer = new double[3];
		double edgeValue = 0.0;
		double connValue = 0.0;
		double deviation = 0.0;
		HashMap<Integer, ArrayList<Integer>> segToNode =  new HashMap<>();
		
		for(int node = 0; node < pixels; node++) {
			int currSeg = nodeToSeg[node];
			int count = 0;
			for(Edge edge : nodes.get(node).getNeighbors()) {
				if(nodeToSeg[edge.getEnd()] != currSeg) {
					edgeValue += edge.getWeight();
					count += 1;
				}
			}
			for(int i = 1; i <= count; i++) {
				connValue += 1.0/i;
			}
			if(segToNode.containsKey(currSeg)) {
				segToNode.get(currSeg).add(node);
			} else {
				ArrayList<Integer> seg = new ArrayList<>();
				seg.add(node);
				segToNode.put(currSeg, seg);
			}
		}
		
		for(int seg : segToNode.keySet()) {
			ArrayList<Integer> currSeg = segToNode.get(seg);
			double[] center = getCentroid(currSeg);
			for(int node : currSeg) {
				int[] currInfo = nodes.get(node).getInfo();
				deviation += calcDeviation(center[0], currInfo[1], center[1], currInfo[2], center[2], currInfo[3]);
			}
		}
		answer[0] = deviation;
		answer[1] = -edgeValue;
		answer[2] = connValue;
		return answer;
	}
	
	private double[] getCentroid(ArrayList<Integer> seg) {
		int r = 0;
		int g = 0;
		int b = 0;
		double total = (double) seg.size();
		
		for(int node : seg) {
			int[] nodeInfo = nodes.get(node).getInfo();
			r += nodeInfo[1];
			g += nodeInfo[2];
			b += nodeInfo[3];
		}
		
		double result[] = new double[3];
		result[0] = r/total;
		result[1] = g/total;
		result[2] = b/total;
		return result;
	}
	
	private double calcDeviation(double r1, int r2, double g1, int g2, double b1, int b2) {
		return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2));
	}	
	
	private double objectiveDistance(double[][] objectives, int i, int j) {
		double[] obj1 = objectives[i];
		double[] obj2 = objectives[j];
		return Math.sqrt(overall_dev*Math.pow(obj1[0]-obj2[0], 2) + 
				         edge*Math.pow(obj1[1]-obj2[1], 2) + 
				         conn*Math.pow(obj1[2]-obj2[2], 2));
	}
	
	
	private double calcDensity(double[][] objectives, ArrayList<Integer> nonDominated, int chromosomeNo) {
		ArrayList<Double> dist = new ArrayList<Double>();
		for(int i : nonDominated) {
			dist.add(objectiveDistance(objectives, chromosomeNo, i));
		}
		Collections.sort(dist);
		
		int k = (int) Math.floor(Math.sqrt(nonDominated.size()));
		return 1.0/(dist.get(k) + 2);
	}

	
	 private int getRandomIntNotEqual(int first, int max) {
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
}
