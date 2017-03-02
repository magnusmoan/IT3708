package project1.view;

import project1.MainApp;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import project1.model.Board;
import project1.model.BoardItem;
import project1.model.MoveDirection;
import project1.model.SightDirection;

public class BoardGUI extends GridPane{

	private int width;
	private int height;
	private int no_rows;
	private int no_cols;
	private double r_width;
	private double r_height;
	
	private ImageView agent_view;
	
	private Node[][] children;

	public BoardGUI() {
		this.width = MainApp.GAME_WIDTH;
		this.height = MainApp.GAME_HEIGHT;
		this.no_rows = MainApp.BOARD_SIZE+2;
		this.no_cols = MainApp.BOARD_SIZE+2;
		this.r_width = this.width / this.no_cols;
		this.r_height = this.height / this.no_rows;
		this.children = new Node[MainApp.BOARD_SIZE][MainApp.BOARD_SIZE];
	}
	
	public void skeleton_board() {
		this.setMaxSize(this.width, this.height);
		this.setGridLinesVisible(true);

		for(int i = 0; i < MainApp.BOARD_SIZE+2; i++) {
			add_wall(i, 0);
		}
		
		for(int i = 1; i < MainApp.BOARD_SIZE+2; i++) {
			add_wall(0, i);
		}
		
		for(int i = 1; i < MainApp.BOARD_SIZE+2; i++) {
			add_wall(MainApp.BOARD_SIZE+1, i);
		}
		
		for(int i = 1; i < MainApp.BOARD_SIZE+1; i++) {
			add_wall(i, MainApp.BOARD_SIZE+1);
		}
	}
	
	private void add_wall(int x, int y) {
		Image img = new Image("wall.png");
		ImageView wall = new ImageView();
		wall.setFitWidth(this.r_width);
		wall.setFitHeight(this.r_height);
		wall.setImage(img);
		this.add(wall, x, y);
	}
	
	public void init_board(Board board) {
		
		for(int x = 0; x < MainApp.BOARD_SIZE; x++) {
			for(int y = 0; y < MainApp.BOARD_SIZE; y++) {
				BoardItem item = board.get_item(x, y);
				Image img;
				switch(item) {
					case FOOD:
						img = new Image("food.png");
						break;
					case POISON:
						img = new Image("poison.png");
						break;
					case EMPTY:
						img = new Image("empty.png");
						break;
					case AGENT:
						img = new Image("bomberman_up.png");
						break;
					default:
						img = new Image("empty.png");
				}
				ImageView pic = new ImageView();
				pic.setFitWidth(this.r_width-4);
				pic.setFitHeight(this.r_height-4);
				pic.setImage(img);
				this.add(pic, x+1, y+1);
				GridPane.setHalignment(pic, HPos.CENTER);
				GridPane.setValignment(pic, VPos.CENTER);
				if(item == BoardItem.AGENT) {
					this.agent_view = pic;
				}
				this.children[x][y] = pic;
				
			}
		}
	}
	
	public void move_agent(int old_x, int old_y, int new_x, int new_y, SightDirection sight_direction) {
		Image img;
		switch(sight_direction) {
			case LEFT:
				img = new Image("bomberman_left.png");
				break;
			case RIGHT:
				img = new Image("bomberman_right.png");
				break;
			case UP:
				img = new Image("bomberman_up.png");
				break;
			case DOWN:
				img = new Image("bomberman_down.png");
				break;
			default:
				img = null;
		}
		
		agent_view.setImage(img);
		
		ObservableList<Node> gui_children = this.getChildren();
		
		gui_children.remove(this.agent_view);
		children[new_x][new_y].setOpacity(0.2);
		this.agent_view.setOpacity(1);
		this.add(this.agent_view, new_x+1, new_y+1);
		children[new_x][new_y] = this.agent_view;
	}
	
	public void new_board_gui(Board board) {
		Node lines = this.getChildren().get(0);
		this.getChildren().clear();
		this.getChildren().add(lines);
		this.skeleton_board();
		this.init_board(board);
	}	
}
