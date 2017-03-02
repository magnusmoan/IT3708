package project1.model;

import project1.MainApp;
import project1.model.BoardItem;
import project1.model.MoveDirection;
import java.util.Random;


public class Board {
	
	public BoardItem[][] board;
	
	private int agent_x;
	private int agent_y;
	private SightDirection sight_direction;
	private MoveDirection[] sensors;
	
	
	public Board() {
		this.board = new BoardItem[MainApp.BOARD_SIZE][MainApp.BOARD_SIZE];
		Random generator = new Random();
		double first_number;
		double second_number;
		
		for(int x = 0; x < MainApp.BOARD_SIZE; x++) {
			for(int y = 0; y < MainApp.BOARD_SIZE; y++) {
				first_number = generator.nextDouble();
				
				if(first_number < MainApp.FOOD_PROB) {
					this.add_item(x, y, BoardItem.FOOD);
				} else {
					second_number = generator.nextDouble();
					if(second_number < MainApp.POISON_PROB) {
						this.add_item(x, y, BoardItem.POISON);
					} else {
						this.add_item(x, y, BoardItem.EMPTY);
					}
				}
			}
		}
		
		this.agent_x = generator.nextInt(MainApp.BOARD_SIZE);
		this.agent_y = generator.nextInt(MainApp.BOARD_SIZE);

		this.sight_direction = SightDirection.UP;
		this.sensors = new MoveDirection[3];
		this.sensors[MainApp.LEFT_INDEX] = MoveDirection.LEFT;
		this.sensors[MainApp.FORWARD_INDEX] = MoveDirection.FORWARD;
		this.sensors[MainApp.RIGHT_INDEX] = MoveDirection.RIGHT;
		
		board[this.agent_x][this.agent_y] = BoardItem.AGENT;
	}
	
	public void remove_item(int x, int y) {
		this.board[x][y] = BoardItem.EMPTY;
	}
	
	public void add_item(int x, int y, BoardItem item) {
		this.board[x][y] = item;
	}
	
	public BoardItem get_item_from_direction(MoveDirection direction) {
		int[] new_x_y = get_new_x_y(direction);
		if(is_wall(new_x_y[0], new_x_y[1])) {
			return BoardItem.WALL;
		}
		return this.get_item(new_x_y[0], new_x_y[1]);
	}
	
	public boolean is_wall(MoveDirection direction) {
		int[] new_x_y = get_new_x_y(direction);
		int x = new_x_y[0];
		int y = new_x_y[1];
		if(x == -1 || y == -1 || x == MainApp.BOARD_SIZE || y == MainApp.BOARD_SIZE) {
			return true;
		}
		return false;
	}
	
	public boolean is_wall(int x, int y) {
		if(x == -1 || y == -1 || x == MainApp.BOARD_SIZE || y == MainApp.BOARD_SIZE) {
			return true;
		}
		return false;
	}
	
	public int[] get_new_x_y(MoveDirection direction, int x, int y) {
		int[] x_y = new int[2];
		x_y[0] = x;
		x_y[1] = y;
		
		switch(this.sight_direction) {
		case UP:
			switch(direction) {
				case FORWARD: x_y[1]--; break;
				case RIGHT: x_y[0]++; break;
				case LEFT: x_y[0]--; break;
				default: break;
			} break;
		
		case DOWN:
			switch(direction) {
				case FORWARD: x_y[1]++; break;
				case RIGHT: x_y[0]--; break;
				case LEFT: x_y[0]++; break;
				default: break;
			} break;
			
		case LEFT:
			switch(direction) {
				case FORWARD: x_y[0]--; break;
				case RIGHT: x_y[1]--; break;
				case LEFT: x_y[1]++; break;
				default: break;
			} break;
			
		case RIGHT:
			switch(direction) {
				case FORWARD: x_y[0]++; break;
				case RIGHT: x_y[1]++; break;
				case LEFT: x_y[1]--; break;
				default: break;
			} break;
			
		default: break;
		}
	return x_y;
	}
	
	public int[] get_new_x_y(MoveDirection direction) {
		return get_new_x_y(direction, this.agent_x, this.agent_y);
	}
	
	public BoardItem get_item(int x, int y) {
		return this.board[x][y];
	}
	
	public void move_agent(MoveDirection direction) {
		remove_item(this.agent_x, this.agent_y);
		
		switch(this.sight_direction) {
		case UP:
			switch(direction) {
				case FORWARD: this.agent_y--; break;
				case RIGHT: this.agent_x++; this.sight_direction = SightDirection.RIGHT; break;
				case LEFT: this.agent_x--; this.sight_direction = SightDirection.LEFT; break;
				case BACKWARD: this.agent_y++; break;
				default: break;
			} break;
		
		case DOWN:
			switch(direction) {
				case FORWARD: this.agent_y++; break;
				case RIGHT: this.agent_x--; this.sight_direction = SightDirection.LEFT; break;
				case LEFT: this.agent_x++; this.sight_direction = SightDirection.RIGHT; break;
				case BACKWARD: this.agent_y--; break;
				default: break;
			} break;
			
		case LEFT:
			switch(direction) {
				case FORWARD: this.agent_x--; break;
				case RIGHT: this.agent_y--; this.sight_direction = SightDirection.UP; break;
				case LEFT: this.agent_y++; this.sight_direction = SightDirection.DOWN; break;
				case BACKWARD: this.agent_x++; break;
				default: break;
			} break;
			
		case RIGHT:
			switch(direction) {
				case FORWARD: this.agent_x++; break;
				case RIGHT: this.agent_y++; this.sight_direction = SightDirection.DOWN; break;
				case LEFT: this.agent_y--; this.sight_direction = SightDirection.UP; break;
				case BACKWARD: this.agent_x--; break;
				default: break;
			} break;
			
		default: break;
		}
		
		add_item(this.agent_x, this.agent_y, BoardItem.AGENT);
	}
	
	public BoardItem[][] get_board() {
		return this.board;
	}
	
	public int get_agent_x() {
		return this.agent_x;
	}
	
	public int get_agent_y() {
		return this.agent_y;
	}
	
	public MoveDirection[] get_sensors(){
		return this.sensors;
	}
	
	public SightDirection get_sight_direction() {
		return this.sight_direction;
	}
	
	public void set_sight_direction(SightDirection sight_direction) {
		this.sight_direction = sight_direction;
	}
	


}
