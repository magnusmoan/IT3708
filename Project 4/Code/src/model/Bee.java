package model;

import utils.Parameters;

public class Bee extends Individual{

	public Bee(JSSPInstance instance) {
		super(instance);
		setRandomPosition();
	}
	
	public void scout() {
		setRandomPosition();
	}
	
	public void LocalSearchTabu(int moves) {
		for(int move = 0; move < moves; move++) {
			double[] candidatePos;
			double r = random.nextDouble();
			if(r < Parameters.BA_MOVE_ONE) {
				candidatePos = moveOneOperation();
			} else if (r < Parameters.BA_SWAP_TWO){
				candidatePos = do2Swap();
			} else if (r < Parameters.BA_INVERT){
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
			}
		}
	}
}
