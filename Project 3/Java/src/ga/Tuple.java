package ga;

public class Tuple<L, R> implements Comparable<Tuple<Integer, Double>>{
	private final Integer x; 
	private final double y; 
	  
	public Tuple(Integer x, double y) { 
	    this.x = x; 
	    this.y = y; 
	}
	
	public Integer getIndex() {
		return x;
	}
	
	public double getValue() {
		return y;
	}

	@Override
	public int compareTo(Tuple<Integer, Double> t) {
		double otherY = t.getValue();
		if(y < otherY) {
			return -1;
		} else if(y > otherY) {
			return 1;
		} 
		return 0;
	}
}
