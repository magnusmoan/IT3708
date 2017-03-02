package project1.model;

import java.util.Random;

public class Network {
	
	private int[] inputs;
	private double[] weights;
	private double[] outputs;
	
	private int no_inputs;
	private int no_outputs;
	private int no_weights;
	
	public Network(int no_inputs, int no_outputs) {
		this.no_inputs = no_inputs;
		this.no_outputs = no_outputs;
		this.no_weights = no_inputs * no_outputs;
		this.inputs = new int[no_inputs];
		this.weights = new double[no_weights];
		this.outputs = new double[no_outputs];
		
		Random random = new Random();
		for(int i = 0; i < no_weights; i++) {
			this.weights[i] = random.nextDouble()/1000.0;
		}
	}
	
	public int get_best_output_index() {
		double best = this.outputs[0];
		int best_index = 0;
		for(int i = 1; i < no_outputs; i++) {
			double candidate = this.outputs[i];
			if(candidate > best) { 
				best = candidate;
				best_index = i;
			}
		}
		return best_index;
	}
	
	public double get_best_output_value() {
		return this.outputs[get_best_output_index()];
	}
	
	public int get_no_outputs() {
		return this.no_outputs;
	}
	
	public double[] get_outputs() {
		return this.outputs;
	}
	
	public void set_outputs(double[] outputs) {
		this.outputs = outputs;
	}
	
	public int get_no_inputs() {
		return this.no_inputs;
	}
	
	public int[] get_inputs() {
		return this.inputs;
	}
	
	public void set_inputs(int[] inputs) {
		this.inputs = inputs;
	}
	
	public int get_no_weights() {
		return this.no_weights;
	}
	
	public double get_weight(int index) {
		return this.weights[index];
	}
	
	public double[] get_weights() {
		return this.weights;
	}
	
	public void update_weight(int weight_index, double weight_change) {
		this.weights[weight_index] += weight_change;
	}
	
}
