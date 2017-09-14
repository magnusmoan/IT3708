package model;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Individual {

	protected int bestMakespan;
	protected int makespan;
	protected double[] position;
	protected double[] bestPosition;
	protected Schedule schedule;
	protected JSSPInstance instance;
	protected int size;
	protected boolean changed;
	protected ThreadLocalRandom random;
	
	public Individual(JSSPInstance instance) {
		this.instance = instance;
		this.size = instance.getNJobs() * instance.getNMachines();
		changed = true;
		this.bestMakespan = Integer.MAX_VALUE;
		this.makespan = Integer.MAX_VALUE;
		this.position = new double[size];
		this.bestPosition = new double[size];
		this.random = ThreadLocalRandom.current();
		setRandomPosition();
	}
	
	public int getMakespan() {
		if (changed) {
			calculateMakespan();
		}
		return this.makespan;
	}
	
	private void calculateMakespan() {
		this.schedule = new Schedule(position, instance);
		this.makespan = schedule.getMakespan();
		this.changed = false;
	}
	
	protected void updateBestMakespan() {
		this.bestMakespan = makespan;
	}
	
	protected void setRandomPosition() {
		for(int i = 0; i < this.size; i++) {
			this.position[i] = this.random.nextDouble(this.size);
		}
	}
	
	public void updateBest() {
		if(getMakespan() < this.bestMakespan) {
			this.bestMakespan = this.makespan;
			System.arraycopy(position, 0, bestPosition, 0, size);
		}
	}
	
	public int getBestMakespan() {
		updateBest();
		return this.bestMakespan;
	}
	
	public double[] getBestPosition() {
		return this.bestPosition;
	}
	
	public double[] getPosition() {
		return this.position;
	};
	
	protected double[] moveOneOperation() {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		while(r1 == r2) {
			r2 = random.nextInt(size);
		}
		
		double[] newPosition = new double[size];
		System.arraycopy(position, 0, newPosition, 0, size);
		
		double chosen = newPosition[r1];
		
		if(r1 > r2) {
			for(int i = r1; i > r2; i--) {
				newPosition[i] = newPosition[i-1];
			}
		} else {
			for(int i = r1; i < r2; i++) {
				newPosition[i] = newPosition[i+1];
			}
		}
		
		newPosition[r2] = chosen;
		changed = true;
	
		return newPosition;
	}
	
	protected double[] do2Swap() {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		return swap(r1, r2);
		
	}
	
	private double[] swap(int r1, int r2) {
		double[] newPosition = new double[size];
		System.arraycopy(position, 0, newPosition, 0, size);
		double f1 = newPosition[r1];
		double f2 = newPosition[r2];
		newPosition[r1] = f2;
		newPosition[r2] = f1;
		changed = true;
		
		return newPosition;
	}
	
	protected double[] doInversion() {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		while(r1 == r2) {
			r2 = random.nextInt(size);
		}
		
		if(r2 < r1) {
			int intermediate = r2;
			r2 = r1;
			r1 = intermediate;
		}
		
		if(r2 - r1 == 1) {
			return swap(r1, r2);
		} else {
			double[] newPosition = new double[size];
			System.arraycopy(position, 0, newPosition, 0, size);
			
			for(int i = 0; i < (r2-r1)-1; i++) {
				double intermediate = newPosition[r1+i];
				newPosition[r1+i] = newPosition[r2-i];
				newPosition[r2-i] = intermediate;
			}
			
			return newPosition;
		}
	}
	
	protected double[] doLongDistanceSwap() {
		int r1 = random.nextInt(size);
		int r2 = random.nextInt(size);
		
		while(r1 == r2) {
			r2 = random.nextInt(size);
		}
		
		if(r2 < r1) {
			int intermediate = r2;
			r2 = r1;
			r1 = intermediate;
		}
		
		double[] newPosition = new double[size];
		double[] front = new double[size-r2];
		double[] middle = new double[r2-r1];
		double[] back = new double[r1];
		
		for(int i = 0; i < r1; i++) {
			back[i] = position[i];
		}
		
		for(int i = 0; i < size-r2; i++) {
			front[i] = position[r2+i];
		}
		
		for(int i = 0; i < r2-r1; i++) {
			middle[i] = position[r1+i];
		}
		
		for(int i = 0; i < front.length; i++) {
			newPosition[i] = front[i];
		}
		
		for(int i = 0; i < middle.length; i++) {
			newPosition[front.length+i] = middle[i];
		}
		
		for(int i = 0; i < back.length; i++) {
			newPosition[front.length+middle.length+i] = back[i];
		}
		
		
		return newPosition;
	}
}
