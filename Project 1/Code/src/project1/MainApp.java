package project1;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project1.view.BoardGUI;
import project1.view.RootLayout;
import project1.model.Board;

public class MainApp extends Application {

	public final static int WIDTH = 700;
	public final static int HEIGHT = 700;
	public final static int GAME_WIDTH = 500;
	public final static int GAME_HEIGHT = 500;
	public final static int BOARD_SIZE = 10;
	public final static double FOOD_PROB = .5;
	public final static double POISON_PROB = .5;
	public final static String BASELINE_AGENT_STRING = "Baseline";
	public final static String SUPERVISED_AGENT_STRING = "Supervised learning";
	public final static String REINFORCEMENT_V1_AGENT_STRING = "Reinforcement learning v1";
	public final static String REINFORCEMENT_V2_AGENT_STRING = "Reinforcement learning v2";
	public final static int LEFT_INDEX = 1;
	public final static int FORWARD_INDEX = 0;
	public final static int RIGHT_INDEX = 2;
	
	public Board board;
	public BoardGUI boardGUI;
	public Game game;
	private Stage primaryStage;
	private RootLayout rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("IT3708: Project 1");
		
		initRootLayout();
	
		this.board = new Board();
		addBoardGUI();
		game = new Game(this.board, this.boardGUI, this.rootLayout);
		primaryStage.show();
		
	}
	
	public void initRootLayout() {
		this.rootLayout = new RootLayout(this);
		Scene scene = new Scene(rootLayout);
		this.primaryStage.setScene(scene);
	}
	
	public void addBoardGUI() {
		this.boardGUI = new BoardGUI();
		boardGUI.skeleton_board();
		boardGUI.init_board(this.board);
		rootLayout.setCenter(boardGUI);
	}
	
	public void startGame() {
		Game game = this.game;
		String agent_type = this.rootLayout.get_agent_type();
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				game.play(agent_type);
				return null;
			}
		};
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();	
	}
	
	public void restartGame() {
		this.game.change_board();
		this.game.update_gui();
		startGame();
	}

	public Stage primaryStage() {
		return this.primaryStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
