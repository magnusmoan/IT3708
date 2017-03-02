package project1.view;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import project1.Helpers;
import project1.MainApp;
import project1.model.NeuralAgentType;

public class RootLayout extends BorderPane{

	private final static String START_STRING = "Start";
	private final static String CONTINUE_STRING = "Continue";
	private final static String RESTART_STRING = "Restart";
	private final static String SLIDER_STRING = "Animation Speed (sec.)";
	private final static String GUI_CHOICE_STRING = "Enable GUI?";
	private final static String ROUNDS_STRING = "Rounds";
	private final static String AGENT_TYPE_STRING = "Agent type";
	private final static String ILLEGAL_ANIMATION_FREQ_STRING = "Illegal animation frequency. Set to 0.";
	private final static String ILLEGAL_ROUNDS_STRING = "Illegal number of games inserted. Set to 1";
	private final static double BUTTON_HEIGHT = MainApp.GAME_HEIGHT/17.0;
	private final static double BUTTON_WIDTH = MainApp.GAME_WIDTH/5.0;
	private final static double BOTTOM_ROW_HEIGHT = (MainApp.HEIGHT - MainApp.GAME_HEIGHT)/2.0;
	private final static double TEXT_FIELD_WIDTH = 50.0;
	
	
	private HBox top_row;
	
	private HBox hbox1; // ALL SETTINGS, CONTAINS vbox2, vbox3, vbox4
	private VBox vbox1; // MAIN BOTTOM BOX
	private VBox vbox2; // BUTTONS BOX
	private VBox vbox3; // GUI CHOICE BOX
	private VBox vbox4; // ROUNDS AND AGENT CHOICE BOX
	private VBox vbox5; // SLIDER BOX
	
	
	private CheckBox choose_gui_box;
	
	private ChoiceBox<String> agent_box;
	private HBox agent_type_box;
	private Label agent_type;
	
	private HBox rounds_box;
	private TextField rounds;
	private Label rounds_text;
	
	private Slider animation_speed_slider;
	private Label slider_text;
	
	private Label score_text;
	private VBox score_box;
	
	private Button start_button;
	private Button continue_button;
	
	private boolean first;
	private boolean continue_game;
	
	public RootLayout(MainApp mainApp) {
		this.continue_game = false;
		init_settings_box(mainApp, this);
		this.init_animation_speed_slider();
		this.init_score_text();
		
		top_row = new HBox();
		top_row.getChildren().add(this.score_box);
		top_row.setAlignment(Pos.CENTER);
		this.setTop(top_row);
		
		this.vbox1 = new VBox(15);
		this.vbox1.getChildren().addAll(this.hbox1, this.vbox5);
		double padding = (MainApp.WIDTH - MainApp.GAME_WIDTH)/2.0;
		this.vbox1.setPadding(new Insets(0,padding,0,padding));
		this.vbox1.setMinWidth(MainApp.GAME_WIDTH);
		this.setBottom(vbox1);
		this.setMinSize(MainApp.WIDTH, MainApp.HEIGHT);
	
	}
	
	private void init_settings_box(MainApp mainApp, RootLayout rootLayout) {
		this.hbox1 = new HBox(MainApp.GAME_WIDTH / 14.0);
		this.hbox1.setMinWidth(MainApp.GAME_WIDTH);
		init_button_box(mainApp, rootLayout);
		init_choose_gui_box();
		init_choice_box();
		this.hbox1.getChildren().addAll(this.vbox2, this.vbox4, this.vbox3);
		
		
	}
	
	private void init_button_box(MainApp mainApp, RootLayout rootLayout) {
		this.vbox2 = new VBox(10);
		init_start_button(mainApp, rootLayout);
		init_continue_button();
		
		this.vbox2.getChildren().addAll(this.start_button, this.continue_button);
		this.vbox2.setAlignment(Pos.CENTER);
	}
	
	private void init_start_button(MainApp mainApp, RootLayout rootLayout) {
		this.start_button = new Button();
		this.start_button.setText(START_STRING);
		this.start_button.setMinWidth(BUTTON_WIDTH);
		this.start_button.setMinHeight(BUTTON_HEIGHT);
		Button button = this.start_button;
		this.first = true;
		
		this.start_button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(rootLayout.first) {
					mainApp.startGame();
					rootLayout.first = false;
					button.setText(RESTART_STRING);
				} else {
					mainApp.restartGame();
				}
			}
		});
	}
	
	private void init_continue_button() {
		this.continue_button = new Button();
		this.continue_button.setText(CONTINUE_STRING);
		this.continue_button.setMinWidth(BUTTON_WIDTH);
		this.continue_button.setMinHeight(BUTTON_HEIGHT);
		this.continue_button.setDisable(true);
		
		this.continue_button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
			}
		});
	}
	
	private void init_choose_gui_box() {
		this.vbox3 = new VBox();
		this.choose_gui_box = new CheckBox(GUI_CHOICE_STRING);
		this.vbox3.getChildren().add(this.choose_gui_box);
		this.vbox3.setAlignment(Pos.CENTER);
	}
	
	private void init_choice_box() {
		this.vbox4 = new VBox(10);
		init_rounds_box();
		init_agent_type_box();
		this.vbox4.getChildren().addAll(this.agent_box, this.rounds_box);
		this.vbox4.setAlignment(Pos.CENTER_RIGHT);
	}
	
	private void init_rounds_box() {
		this.rounds_box = new HBox(BUTTON_WIDTH/10.0);
		this.rounds_text = new Label(ROUNDS_STRING);
		this.rounds_text.setMaxWidth(5*BUTTON_WIDTH/10.0);
		this.rounds_text.setMinHeight(BUTTON_HEIGHT);
	
		this.rounds = new TextField();
		this.rounds.setMaxWidth(BUTTON_WIDTH);
		this.rounds_box.getChildren().addAll(this.rounds_text, this.rounds);
	}
	
	private void init_agent_type_box() {
		this.agent_box = new ChoiceBox<String>(FXCollections.observableArrayList(
				MainApp.SUPERVISED_AGENT_STRING, 
				MainApp.REINFORCEMENT_V1_AGENT_STRING,
				MainApp.REINFORCEMENT_V2_AGENT_STRING,
				MainApp.BASELINE_AGENT_STRING));
		this.agent_box.getSelectionModel().selectFirst();
		this.agent_box.setMinWidth(BUTTON_WIDTH+60);
		this.agent_box.setMinHeight(BUTTON_HEIGHT);
		this.agent_box.setMaxWidth(BUTTON_WIDTH+60);
		this.agent_box.setMaxHeight(BUTTON_HEIGHT);
	}
	
	
	
	private void init_animation_speed_slider() {
		this.vbox5 = new VBox();
		this.slider_text = new Label(SLIDER_STRING);
		this.animation_speed_slider = new Slider(0, 2.01, 1.0);
		this.animation_speed_slider.setShowTickLabels(true);
		this.animation_speed_slider.setShowTickMarks(true);
		this.animation_speed_slider.setMajorTickUnit(0.2f);
		this.animation_speed_slider.setMinWidth(MainApp.GAME_WIDTH);
		this.vbox5.getChildren().addAll(this.slider_text, this.animation_speed_slider);
		this.vbox5.setAlignment(Pos.CENTER);
	}
	
	private void init_score_text() {
		this.score_box = new VBox();
		double padding = (MainApp.HEIGHT - MainApp.GAME_WIDTH)/8.0;
		this.score_box.setPadding(new Insets(padding,0,0,0));
		this.score_text = new Label("Score: 0");
		this.score_box.getChildren().add(score_text);
	}
	
	public int get_animation_speed() {
		return (int) (this.animation_speed_slider.getValue()*1000);
	}
	
	public String get_agent_type() {
		return (String) this.agent_box.getValue();
	}
	
	public void update_score_text(int score) {
		this.score_text.setText("Score: " + score);
	}
	
	public boolean gui_enabled() {
		return this.choose_gui_box.selectedProperty().get();

	}
	
	public int get_rounds() {
		String value = this.rounds.getText();
		if(Helpers.isInteger(value)) {
			return Integer.parseInt(value);
		} else {
			System.out.println(ILLEGAL_ROUNDS_STRING);
			return 1;
		}
	}
	
	public NeuralAgentType get_neural_agent_type() {
		switch(get_agent_type()) {
			case MainApp.SUPERVISED_AGENT_STRING: { return NeuralAgentType.SUPERVISED; }
			case MainApp.REINFORCEMENT_V1_AGENT_STRING: { return NeuralAgentType.REINFORCEMENTv1; }
			case MainApp.REINFORCEMENT_V2_AGENT_STRING: { return NeuralAgentType.REINFORCEMENTv2; }
			default: { return null; }
		}
	}
	
	public void enable_continue_button() {
		//this.continue_button.setDisable(false);
	}
	
	private void disable_continue_button() {
		this.continue_button.setDisable(true);
	}
	
	public boolean is_continue_pressed() {
		return this.continue_game;
	}
	
}
