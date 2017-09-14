package model;

import utils.Parameters;

public class Particle extends Individual{

	private double[] velocity;
	private double v_max;

	public Particle(JSSPInstance instance) {
		super(instance);
		this.velocity = new double[size];
		this.v_max = size*0.1;
		
		for (int i = 0; i < size; i++) {
			this.velocity[i] = random.nextDouble(v_max*2) - v_max;
		}

	}
	
	private void updateVelocity(int i, double omega, double[] globalBest) {
		double t1 = omega * velocity[i];
		double t2 = Parameters.C1 * random.nextDouble() * (bestPosition[i] - position[i]);
		double t3 = Parameters.C2 * random.nextDouble() * (globalBest[i] - position[i]);
		this.velocity[i] = t1 + t2 + t3;
		if(this.velocity[i] > this.v_max) {
			this.velocity[i] = this.v_max;
		} else if(this.velocity[i] < -this.v_max) {
			this.velocity[i] = -this.v_max;
		}
	}
	
	public void updatePosition(double omega, double[] globalBest) {
		for(int i = 0; i < size; i++) {
			updateVelocity(i, omega, globalBest);
			position[i] = position[i] + velocity[i];
			if(position[i] < 0) {
				position[i] = 0;
			} else if(position[i] > size) {
				position[i] = size;
			}
		}
		this.changed = true;
	}
	
	public void localSearchSA(int globalBest) {
		double temperature = getMakespan() - globalBest;
		while(temperature > Parameters.MIN_TEMP) {
			double[] candidatePos;
			double r = random.nextDouble();
			if(r < Parameters.PSO_MOVE_ONE) {
				candidatePos = moveOneOperation();
			} else if (r < Parameters.PSO_SWAP_TWO) {
				candidatePos = do2Swap();
			} else if (r < Parameters.PSO_INVERT) {
				candidatePos = doInversion();
			} else {
				candidatePos = doLongDistanceSwap();
			}
			Schedule candidateSch = new Schedule(candidatePos, instance);
			int candMakespan = candidateSch.getMakespan();
			
			if(candMakespan < makespan) {
				this.position = candidatePos;
				this.makespan = candMakespan;
				changed = true;
				
			} else {
				if(random.nextDouble() < Math.min(1,  Math.exp(-(candMakespan-makespan)/temperature))) {
					this.position = candidatePos;
					this.makespan = candMakespan;
					changed = true;
				};
			}
			temperature *= Parameters.BETA;
		}
	}
	

	@Override
	public String toString() {
		String s = "Position: ";
		for (double d : position) {
			s += d + " ";
		}
		s += "\nVelocity: ";
		for (double d : velocity) {
			s += d + " ";
		}
		
		s += "\nMakespan: " + this.makespan + "\n";

		return s;
	}

}
