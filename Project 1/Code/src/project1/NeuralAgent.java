package project1;

import java.util.HashMap;
import java.util.function.BiFunction;

import project1.model.Board;
import project1.model.BoardItem;
import project1.model.MoveDirection;
import project1.model.Network;
import project1.model.NeuralAgentType;
import project1.model.SightDirection;

public class NeuralAgent implements Agent{
	
	public Network neural_network;
	private BaselineAgent trainer;
	private NeuralAgentType neural_agent_type;
	
	private final static double LEARNING_RATE = 0.007;
	private BiFunction<Board, Integer, Double> delta_function;
	private boolean train;
	private int sight_length;
	
	private final static int[] INDEXES = new int[3];
	
	public NeuralAgent(NeuralAgentType neural_agent_type) {
		this.trainer = new BaselineAgent();
		this.train = true;
		this.neural_agent_type = neural_agent_type;
		INDEXES[0] = MainApp.LEFT_INDEX;
		INDEXES[1] = MainApp.FORWARD_INDEX;
		INDEXES[2] = MainApp.RIGHT_INDEX;
		
		switch(neural_agent_type) {
			case SUPERVISED: {
				delta_function = (Board board, Integer output_index) -> {
					return delta_supervised(board, output_index);
				};
				this.neural_network = new Network(12, 3);
				this.sight_length = 1;
				break;
			}
			
			case REINFORCEMENTv1: {
				delta_function = (Board board, Integer output_index) -> {
					return delta_reinforcement(board, output_index);
				};
				this.neural_network = new Network(12, 3);
				this.sight_length = 1;
				break;
			}
			case REINFORCEMENTv2: {
				delta_function = (Board board, Integer output_index) -> {
					return delta_reinforcement(board, output_index);
				};
				this.neural_network = new Network(36, 3);
				this.sight_length = 3;
				break;
			}
			default:
				break;
		}
	}

	
	@Override
	public MoveDirection choose_direction(Board board) {
		set_inputs(get_sensor_info(board));
		update_outputs();
		MoveDirection direction = Helpers.index_to_direction(this.neural_network.get_best_output_index());
		if(train) {
			this.update_weights(board);
		}
		
		return direction;
	}
	
	private void update_weights(Board board) {
		int[] curr_inputs = this.neural_network.get_inputs();
		
		for(int output = 0; output < neural_network.get_no_outputs(); output++) {
			double delta = this.delta_function.apply(board, output);
			for(int input = 0; input < neural_network.get_no_inputs(); input++){
				double weight_change = LEARNING_RATE * delta * curr_inputs[input];
				int index = input + (neural_network.get_no_inputs()*output);
				this.neural_network.update_weight(index, weight_change);
			}
		}
	}
	
	public void set_done_training() {
		this.train = false;
	}
	
	private double delta_supervised(Board board, int output_index) {
		// Get the current outputs
		double[] curr_outputs = this.neural_network.get_outputs();
		double[] exp_outputs = new double[3];
		
		// Get the index (0,1 or 2) corresponding to the current move direction 
		// (left, forward, right) chosen by baseline agent
		int correct_choice = Helpers.direction_to_index(this.trainer.choose_direction(board));
		double exp_output_sum = 0.0;
		
		// Calculate exp(output) for each output and also the sum of these values
		for(int i = 0; i < neural_network.get_no_outputs(); i++) {
			exp_outputs[i] = Math.exp(curr_outputs[i]);
			exp_output_sum += exp_outputs[i];
		}
		
		// If the output we are looking at is the same as the output chosen by 
		// the baseline agent we set the variable chosen to 1
		int chosen = 0;
		if(correct_choice == output_index) { chosen = 1; }
		
		// Calculate equation 3
		return - (exp_outputs[output_index] / exp_output_sum) + chosen;
	}
	
	private double delta_reinforcement(Board board, Integer output_index) {
		 
		// Chosen is the index of the output chosen
		MoveDirection a = Helpers.index_to_direction(neural_network.get_best_output_index());
		int chosen = Helpers.direction_to_index(a);
		
		if(output_index == chosen) {
			
			// Find Q(s,a) by getting the output value of the chosen direction
			double Q = this.neural_network.get_outputs()[chosen];
			
			// r is found by getting the value of the item in the field moved to
			double r = Helpers.get_item_value(board.get_item_from_direction(a));
			
			// Q_new = Q(s',a')
			double Q_new = get_best_output_from_direction(board, a);
			
			// Equation 5
			return r + 0.9*Q_new - Q;
		} else {
			return 0.0;
		}
	}
	
	private double get_best_output_from_direction(Board board, MoveDirection direction) {

		// Saving current state
		SightDirection curr_sight_dir = board.get_sight_direction();
		BoardItem curr_item = board.get_item_from_direction(direction);
		int[] new_x_y = board.get_new_x_y(direction);
		if(curr_item.equals(BoardItem.WALL)) {
			return 0.0;
		}
		
		// Moving agent
		board.move_agent(direction);
		set_inputs(get_sensor_info(board));
		update_outputs();
		double Q_new = this.neural_network.get_best_output_value();
		
		// Restoring state
		board.move_agent(MoveDirection.BACKWARD);
		board.set_sight_direction(curr_sight_dir);
		board.add_item(new_x_y[0], new_x_y[1], curr_item);
		set_inputs(get_sensor_info(board));
		update_outputs();
		return Q_new;
	}
	
	private HashMap<MoveDirection, BoardItem[]> get_sensor_info(Board board) {
		HashMap<MoveDirection, BoardItem[]> sensor_info = new HashMap<MoveDirection, BoardItem[]>();
		int[] new_x_y;
		for(MoveDirection sensor : board.get_sensors()) {
			new_x_y = board.get_new_x_y(sensor);
			BoardItem[] items = new BoardItem[sight_length];
			for(int step = 0; step < sight_length; step++) {
				BoardItem curr_item;
				if(board.is_wall(new_x_y[0], new_x_y[1])) {
					items[step] = BoardItem.WALL;
					for(int i = step+1; i < sight_length; i++) {
						items[i] = null;
					}
					sensor_info.put(sensor, items);
					break;
				}
				curr_item = board.get_item(new_x_y[0], new_x_y[1]);
				items[step] = curr_item;
				new_x_y = board.get_new_x_y(MoveDirection.FORWARD, new_x_y[0], new_x_y[1]);
			}
			sensor_info.put(sensor, items);
		}
		
		return sensor_info;
		
	}
	
	public String get_neural_agent_type() {
		switch(this.neural_agent_type) {
			case REINFORCEMENTv1: return "Reinforcementv1";
			case REINFORCEMENTv2: return "Reinforcementv2";
			case SUPERVISED: return "Supervised";
			default: return "";
		}
	}
	
	private void set_inputs(HashMap<MoveDirection, BoardItem[]> sensor_info) {
		int index;
		int[] inputs = new int[neural_network.get_no_inputs()];
		for(MoveDirection direction : sensor_info.keySet()) {
			index = Helpers.direction_to_index(direction);
			BoardItem[] items = sensor_info.get(direction);
			for(int i = 0; i < items.length; i++) {
				int input_index = (index * sight_length*4) + (i * 4);
				BoardItem curr_item = items[i];
				if(curr_item == null) {
					continue;
				}
				switch(curr_item) {
					case EMPTY: inputs[input_index] = 1; continue;
					case WALL: inputs[input_index + 1] = 1; continue;
					case FOOD: inputs[input_index + 2] = 1; continue;
					case POISON: inputs[input_index + 3] = 1; continue;
					default: continue;
				}
			}
		}
		neural_network.set_inputs(inputs);
	}
	
	public void update_outputs() {
		double[] outputs = new double[neural_network.get_no_outputs()];
		int[] inputs = neural_network.get_inputs();
		int no_of_inputs = neural_network.get_no_inputs();
		for(int direction_index : INDEXES) {
			double output = 0;
			for(int i = 0; i < no_of_inputs; i++) {
				output += inputs[i]*neural_network.get_weight(i+no_of_inputs*direction_index);
			}
			outputs[direction_index] = output;
		}
		neural_network.set_outputs(outputs);
	}
}
