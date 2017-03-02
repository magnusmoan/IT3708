package project1;

import project1.model.Board;
import project1.model.BoardItem;
import project1.model.MoveDirection;
import project1.view.BoardGUI;
import project1.view.RootLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import project1.Procedure;

import javafx.application.Platform;

public class Game {

	private Board board;
	private BoardGUI gui;
	private int score;
	private int total_score;
	private int number_of_rounds; 
	private boolean is_gui_enabled;
	private int animation_speed;
	private RootLayout root_layout;
	private Agent agent;
	private ArrayList<Integer> total_scores_list;
	private ArrayList<Double> average_score_list;

	private BoardItem current_board_item;
	private MoveDirection current_direction;
	
	private final static int NUMBER_OF_GAMES = 100;
	private final static int NUMBER_OF_MOVES = 50;
	
	
	public Game(Board board, BoardGUI gui, RootLayout root_layout) {
		this.board = board;
		this.gui = gui;
		this.root_layout = root_layout;
	}
	
	public void play(String agent_type) {
		this.score = 0;
		this.total_score = 0;
		this.number_of_rounds = this.root_layout.get_rounds();
		this.is_gui_enabled = this.root_layout.gui_enabled();
		this.animation_speed = this.root_layout.get_animation_speed();
		this.total_scores_list = new ArrayList<>();
		this.average_score_list = new ArrayList<>();
		
		if(agent_type.equals(MainApp.BASELINE_AGENT_STRING)) {
			this.agent = new BaselineAgent();
		} else if(agent_type.equals(MainApp.SUPERVISED_AGENT_STRING)
				|| agent_type.equals(MainApp.REINFORCEMENT_V1_AGENT_STRING)
				|| agent_type.equals(MainApp.REINFORCEMENT_V2_AGENT_STRING)) {
			this.agent = new NeuralAgent(this.root_layout.get_neural_agent_type());
		} else {
			this.agent = new BaselineAgent();
		}
		
		if(this.is_gui_enabled) {
			play_one_game_with_gui();
			change_board();
			update_gui();
		}
		
		for(int round_number = 1; round_number < this.number_of_rounds+1; round_number++) {
			System.out.println("Starting round number " + round_number);
			for(int game_number = 0; game_number < NUMBER_OF_GAMES; game_number++ ) {
				play_one_game_without_gui();
				this.total_score += this.score;
				change_board();
			}
			this.total_scores_list.add(this.total_score);
			double average_score = ((double) this.total_score) / NUMBER_OF_GAMES;
			this.average_score_list.add(average_score);
			System.out.println("Average score in round number " + round_number + ": " + average_score + "\n");
			this.total_score = 0;
		}
		
		if(this.is_gui_enabled) {
			update_gui();
			play_one_game_with_gui();
			this.total_score = 0;
		}
		
		for(int i : this.total_scores_list) {
			this.total_score += i;
		}
		double average_score = ((double) this.total_score) / (NUMBER_OF_GAMES * this.number_of_rounds);
		System.out.println("Average score for entire game: " + average_score);
		
		String agent_type_string;
		if(BaselineAgent.class.isInstance(this.agent)) { 
			agent_type_string = "Baseline"; 
		} else {
			agent_type_string = ((NeuralAgent) this.agent).get_neural_agent_type();
		}
		// Uncomment to save average scores to file. File path is set in the Helpers class.
		// Helpers.save_to_file(this.average_score_list, agent_type_string);
		
	}
	
	public void play_one_game_with_gui() {
		for(int i = 0; i < NUMBER_OF_MOVES; i++) {
			final int old_x, old_y, new_x, new_y;
			old_x = this.board.get_agent_x();
			old_y = this.board.get_agent_y();
			perform_logic();
			this.change_score(this.current_board_item);
			
			if(this.current_board_item == BoardItem.WALL) {
				break;
			}
			
			this.board.move_agent(this.current_direction);
			new_x = this.board.get_agent_x();
			new_y = this.board.get_agent_y();
			
			move_agent_in_gui(old_x, old_y, new_x, new_y);
		}
	}
	
	public void play_one_game_without_gui() {
		for(int i = 0; i < NUMBER_OF_MOVES; i++) {
			perform_logic();
			this.change_score(this.current_board_item);
			if(this.current_board_item == BoardItem.WALL) {
				break;
			}
			this.board.move_agent(this.current_direction);
		}
	}
	
	private void perform_logic() {
		this.current_direction = this.agent.choose_direction(board);
		this.current_board_item = this.board.get_item_from_direction(this.current_direction);
	}

	
	public void move_agent_in_gui(int old_x, int old_y, int new_x, int new_y) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				root_layout.update_score_text(score);
				gui.move_agent(old_x, old_y, new_x, new_y, board.get_sight_direction());
			}
		});
		
		try {
			animation_speed = this.root_layout.get_animation_speed();
			TimeUnit.MILLISECONDS.sleep(animation_speed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	public void change_board() {
		this.score = 0;
		this.board = new Board();
	}
	
	public void update_gui() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.new_board_gui(board);
			}
		});
	}

	
	private void change_score(BoardItem item) {
		this.score += Helpers.get_item_value(item);
	}
}
