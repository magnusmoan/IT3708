package ga;

import java.util.ArrayList;

public class NodeInfo<L, R> {
	private final int[] info; 
	private ArrayList<Edge> neighbors; 
	  
	public NodeInfo(int[] info, ArrayList<Edge> neighbors) { 
	    this.info = info; 
	    this.neighbors = neighbors; 
	  } 
	
	public int[] getInfo() {
		return info;
	}
	
	public ArrayList<Edge> getNeighbors() {
		return neighbors;
	}
	
	public void setNeighbors(ArrayList<Edge> neighbors) {
		this.neighbors = neighbors;
	}
}
