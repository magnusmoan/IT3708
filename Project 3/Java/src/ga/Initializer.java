package ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Initializer {

	private ArrayList<int[]> initialPopulation;
	private ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes;
	private HashMap<Integer, ArrayList<Integer>> firstSegment;
	private int pixels;
	private final int kMeansSeg = 8;
	private final int[] noOfSegments = new int[] {10, 40, 7, 60, 35, 10, 13, 19, 12, 21, 25, 30, 17, 40, 45, 50, 55, 60, 65, 70,
			40, 45, 20, 30, 32, 17, 18, 19, 20, 21, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70};
	
	public Initializer(ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes) {
		this.nodes = nodes;
		this.pixels = nodes.size();
		this.initialPopulation = new ArrayList<>();
	}
	
	public void initialize() {
		System.out.println("Initializing...");
		for(int i = 0; i < Config.POP_SIZE; i++) {
			System.out.println("Initializing individual " + (1+i));
			int nOs = noOfSegments[i];
			HashMap<Integer, ArrayList<Integer>> segments = kmeans(nOs);
			if(i == 0) {
				firstSegment = segments;
			}
			initialPopulation.add(getChromosomeFromSegments(segments));
		}
	}
	
	public HashMap<Integer, ArrayList<Integer>> getFirst() {
		return firstSegment;
	}
	
	public ArrayList<int[]> getInitialPop() {
		return initialPopulation;
	}
	
	public int[] getChromosomeFromSegments(HashMap<Integer, ArrayList<Integer>> segments) {
		int[] chromosome = new int[pixels];
		for(int i = 0; i < chromosome.length; i++) {
			chromosome[i] = -1;
		}
		int[] nodeToSeg = getNodeSegment(segments);
		for(int i : segments.keySet()) {
			setEdgesInSegment(chromosome, segments.get(i), nodeToSeg, i);
		}
		for(int i = 0; i < chromosome.length; i++) {
			if(chromosome[i] == -1) { 
				chromosome[i] = i; 
			}
		}
		
		return chromosome;
	}
	
	private void setEdgesInSegment(int[] chromosome, ArrayList<Integer> segment, int[] nodeToSeg, int currSeg) {
		ArrayList<Integer> unchecked = new ArrayList<>();
		HashMap<Integer, Boolean> seen =  new HashMap<>();
		ArrayList<int[]> edges = new ArrayList<>();
		for(int i : segment) {
			seen.put(i, false);
		}
		
		unchecked.add(segment.get(0));
		seen.put(segment.get(0), true);
		
		while(!unchecked.isEmpty()) {
			int curr = unchecked.remove(0);
			for(Edge e : nodes.get(curr).getNeighbors()) {
				int end = e.getEnd();
				if(nodeToSeg[end] == currSeg && !seen.get(end)) {
					edges.add(new int[] {curr, end});
					seen.put(end, true);
					unchecked.add(end);
				}
			}
		}
		
		for(int[] edge : edges) {
			if(chromosome[edge[0]] == -1) {
				chromosome[edge[0]] = edge[1];
			}
			else if(chromosome[edge[1]] == -1) {
				chromosome[edge[1]] = edge[0];
			}
		}	
	}
	
	
	
	public HashMap<Integer, ArrayList<Integer>> kmeans(int noOfSegments) {
		HashMap<Integer, ArrayList<Integer>> segments = new HashMap<>();
		HashMap<Integer, int[]> avg;
		initKmeans(segments);
		for(int i = 0; i < 100; i++) {
			avg = calcAvgRGB(segments);
			putInBestSegment(segments, avg);
			
		}
		
		int[] nodeToSeg = getNodeSegment(segments);
		HashMap<Integer, ArrayList<Integer>> segmentsSplit = getSpatialSegments(segments, nodeToSeg);
		avg = calcAvgRGB(segmentsSplit);
		nodeToSeg = getNodeSegment(segmentsSplit);
		mergeSmallSegments(segmentsSplit, nodeToSeg, avg, noOfSegments);
		return segmentsSplit;
	}
	
	private void mergeSmallSegments(HashMap<Integer, ArrayList<Integer>> segments, int[] nodeToSeg, HashMap<Integer, int[]> avgRGB, int noOfSegments) {
		while(segments.size() > noOfSegments) {
			int smallestSegment = getSmallestSegment(segments);
			mergeWithMostSimilar(segments, smallestSegment, nodeToSeg, avgRGB); 
		}
	}
	
	private int getSmallestSegment(HashMap<Integer, ArrayList<Integer>> segments) {
		int index = 0;
		int size = pixels;
		for(int i : segments.keySet()) {
			int s = segments.get(i).size();
			if(s < size) {
				index = i;
				size = s;
			}
		}
		return index;
	}
	
	private void mergeWithMostSimilar(HashMap<Integer, ArrayList<Integer>> segments, int i, int[] nodeToSeg, HashMap<Integer, int[]> avgRGB) {
		ArrayList<Integer> neighbors = new ArrayList<>();
		ArrayList<Integer> seg = segments.get(i);
		for(int node : seg) {
			for(Edge e: nodes.get(node).getNeighbors()) {
				int otherSeg = nodeToSeg[e.getEnd()];
				if(otherSeg != i && !neighbors.contains(otherSeg)) {
					neighbors.add(otherSeg);
				}
			}
		}

		int mostSim = findMostSimilarSegment(avgRGB, i, neighbors);
		segments.get(mostSim).addAll(segments.get(i));
		updateSingleAvg(mostSim, segments, avgRGB);
		ArrayList<Integer> removed = segments.remove(i);
		for(int j : removed) {
			nodeToSeg[j] = mostSim;
		}
		avgRGB.remove(i);
	}
	
	private void updateSingleAvg(int i, HashMap<Integer, ArrayList<Integer>> segments, HashMap<Integer, int[]> avgRGB) {
		avgRGB.put(i, calcAvg(segments.get(i)));
	}
	
	private int findMostSimilarSegment(HashMap<Integer, int[]> avgRGB, int i, ArrayList<Integer> segs) {
		int mostSim = segs.get(0);
		int[] currRGB = avgRGB.get(i);
		int val = diff(currRGB, avgRGB.get(mostSim));
		for(int j : segs) {
			int cand = diff(currRGB, avgRGB.get(j));
			if(cand < val) {
				val = cand;
				mostSim = j;
			}
		}
		return mostSim;
	}
	
	private int diff(int[] seg1, int[] seg2) {
		return Math.abs(seg1[0]-seg2[0]) + Math.abs(seg1[1]-seg2[1]) + Math.abs(seg1[2]-seg2[2]);
	}
	
	private void putInBestSegment(HashMap<Integer, ArrayList<Integer>> segments, HashMap<Integer, int[]> avg) {
		for(int i = 0; i < kMeansSeg; i++) {
			segments.put(i, new ArrayList<>());
		}
		for(int i = 0; i < pixels; i++) {
			segments.get(findClosestSegment(i, avg)).add(i);
		}
	}
	
	private int findClosestSegment(int node, HashMap<Integer, int[]> avg) {
		int best = 0;
		int val = sumDiff(node, avg.get(0));
		for(int i : avg.keySet()) {
			int cand = sumDiff(node, avg.get(i));
			if(cand < val) {
				best = i;
				val = cand;
			}
		}
		return best;
	}
	
	private int sumDiff(int node, int[] rgb) {
		int[] i = nodes.get(node).getInfo();
		return Math.abs(i[1] - rgb[0]) + Math.abs(i[2] - rgb[1]) + Math.abs(i[3] - rgb[2]);
	}
	
	private void initKmeans(HashMap<Integer, ArrayList<Integer>> segments) {
		Random r = new Random();
		for(int i = 0; i < kMeansSeg; i++) {
			segments.put(i, new ArrayList<>());
		}
		for(int i = 0; i < pixels; i++) {
			segments.get(r.nextInt(kMeansSeg)).add(i);
		}
	}
	
	public HashMap<Integer, int[]> calcAvgRGB(HashMap<Integer, ArrayList<Integer>> segments) {
		HashMap<Integer, int[]> avgRGB = new HashMap<>();
		for(int i : segments.keySet()) {
			avgRGB.put(i, calcAvg(segments.get(i)));
		}
		
		return avgRGB;
	}
	
	private int[] calcAvg(ArrayList<Integer> segment) {
		double r = 0;
		double g = 0;
		double b = 0;
		for(int j : segment) {
			r += nodes.get(j).getInfo()[1];
			g += nodes.get(j).getInfo()[2];
			b += nodes.get(j).getInfo()[3];
		}
		double s = (double) segment.size();
		r = r/s;
		g = g/s;
		b = b/s;
		return new int[] {(int) Math.floor(r), (int) Math.floor(g), (int) Math.floor(b)};
	}
	
	public int[] getNodeSegment(HashMap<Integer, ArrayList<Integer>> segments) {
		int[] nToS = new int[pixels];
		for(int i : segments.keySet()) {
			ArrayList<Integer> currSeg = segments.get(i);
			for(int j : currSeg) {
				nToS[j] = i;
			}
		}
		
		return nToS;
	}
	
	public HashMap<Integer, ArrayList<Integer>> getSpatialSegments(HashMap<Integer, ArrayList<Integer>> colorSeg, int[] nodeToSeg) {
		HashMap<Integer, ArrayList<Integer>> spSeg = new HashMap<>();
		int segNo = 0;
		for(int j : colorSeg.keySet()) {
			ArrayList<Integer> seg = colorSeg.get(j);
			HashMap<Integer, Boolean> seen = new HashMap<>();
			for(int i : seg) {
				seen.put(i, false);
			}
			for(int i : seg) {
				if(!seen.get(i)) {
					spSeg.put(segNo, getAllNodesInSameSegment(i, seg, seen, nodeToSeg, j));
					segNo++;
				}
			}
		}
		
		return spSeg;
	}
	
	private ArrayList<Integer> getAllNodesInSameSegment(int i, ArrayList<Integer> seg, HashMap<Integer, Boolean> seen, int[] nodeToSeg, int currSeg) {
		ArrayList<Integer> redSeg = new ArrayList<>();
		ArrayList<Integer> unchecked = new ArrayList<>();
		redSeg.add(i);
		seen.put(i, true);
		unchecked.add(i);
		
		while(!unchecked.isEmpty()) {
			int curr = unchecked.remove(0);
			for(Edge e : nodes.get(curr).getNeighbors()) {
				int end = e.getEnd();
				if(nodeToSeg[end] == currSeg && !seen.get(end)) {
					redSeg.add(end);
					unchecked.add(end);
					seen.put(end, true);
				}
			}
		}
		
		return redSeg;
		
	}
}
