package utils;

@SuppressWarnings("hiding")
public class Tuple<Integer, Double> {
	private final int x; 
	private final double y; 
	  
	public Tuple(int x, double y) { 
	    this.x = x; 
	    this.y = y; 
	  } 
	
	public int getIndex() {
		return x;
	}
	
	public double getValue() {
		return y;
	}
}
