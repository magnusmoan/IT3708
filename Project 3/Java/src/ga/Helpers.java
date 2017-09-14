package ga;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Helpers {
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public boolean isSimilar(int[] a1, int[] a2) {
		 return Arrays.equals(a1, a2);
	 }
	
	public static void removeSimilar(ArrayList<int[]> arr) {
		boolean removed = true;
		while(removed) {
			for(int i = 0; i < arr.size(); i++) {
				removed = removeIfSimilar(arr.get(i), arr, i);
				if(removed) { break; }
			}
		}
	}
	
	private static boolean removeIfSimilar(int[] a, ArrayList<int[]> arr, int i) {
		for(int j = 0; j < arr.size(); j++) {
			if(j==i) {continue; }
			if(Arrays.equals(a, arr.get(j))) {
				arr.remove(j);
				return true;
			}
		}
		return false;
	}
	
	public static int mergeWithMostSimilar(HashMap<Integer, ArrayList<Integer>> segments, int i, int[] nodeToSeg, HashMap<Integer, int[]> avgRGB, 
			ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes) {
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
		updateSingleAvg(mostSim, segments, avgRGB, nodes);
		ArrayList<Integer> removed = segments.remove(i);
		for(int j : removed) {
			nodeToSeg[j] = mostSim;
		}
		avgRGB.remove(i);
		return mostSim;
	}
	
	public static void updateSingleAvg(int i, HashMap<Integer, ArrayList<Integer>> segments, HashMap<Integer, int[]> avgRGB, 
			ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes) {
		avgRGB.put(i, calcAvg(segments.get(i), nodes));
	}
	
	public static int[] calcAvg(ArrayList<Integer> segment, ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes) {
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
	
	public static int findMostSimilarSegment(HashMap<Integer, int[]> avgRGB, int i, ArrayList<Integer> segs) {
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
	
	public static int diff(int[] seg1, int[] seg2) {
		return Math.abs(seg1[0]-seg2[0]) + Math.abs(seg1[1]-seg2[1]) + Math.abs(seg1[2]-seg2[2]);
	}
	
	public static void setEdgesInSegment(int[] chromosome, ArrayList<Integer> segment, int[] nodeToSeg, int currSeg,
			ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes) {
		
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
	
	public static HashMap<Integer, int[]> calcAvgRGB(HashMap<Integer, ArrayList<Integer>> segments, ArrayList<NodeInfo<int[], ArrayList<Edge>>> nodes) {
		HashMap<Integer, int[]> avgRGB = new HashMap<>();
		for(int i : segments.keySet()) {
			avgRGB.put(i, calcAvg(segments.get(i), nodes));
		}
		
		return avgRGB;
	}
	
	 public static void plotWithPython() {
		 String s1; String s2; String s3;
		 if(Config.OVERALL_DIV) {s1 = "1";} else {s1 = "0";}
		 if(Config.EDGE) {s2 = "1";} else {s2 = "0";}
		 if(Config.CONN) {s3 = "1";} else {s3 = "0";}
		 try {
			 
			Runtime.getRuntime().exec("/usr/local/bin/python3 ../Python/main.py " + Config.PARETO_FRONT + " " + s1 + " " + s2 + " " + s3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
