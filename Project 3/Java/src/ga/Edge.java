package ga;

public class Edge implements Comparable<Edge>{

	private int start, end;
	private double weight;
	
	public Edge(int start, int end, double weight) {
		this.start = start;
		this.end = end;
		this.weight = weight;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}

	@Override
	public int compareTo(Edge e) {
		double oWeight = e.getWeight();
		if(weight < oWeight) {
			return -1;
		} else if(weight > oWeight) {
			return 1;
		} else {
			return 0;
		}
	}
}
