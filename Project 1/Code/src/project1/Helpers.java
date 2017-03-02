package project1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import project1.model.BoardItem;
import project1.model.MoveDirection;

public class Helpers {

	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static void save_to_file(ArrayList<Double> score_list, String agent_type) {
		PrintWriter out;
		try {
			out = new PrintWriter("/Users/Moan/Documents/NTNU/IT3708 - Bio-inspirert Kunstig Intelligens/Project 1/Results/" + agent_type + "-results");
			out.println(agent_type);
			for(double score : score_list) {
				out.println(score);
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static MoveDirection index_to_direction(int index) {
		switch(index) {
			case MainApp.LEFT_INDEX: return MoveDirection.LEFT;
			case MainApp.FORWARD_INDEX: return MoveDirection.FORWARD;
			case MainApp.RIGHT_INDEX: return MoveDirection.RIGHT;
			default: return MoveDirection.LEFT;
		}
	}
	
	public static int direction_to_index(MoveDirection direction) {
		switch(direction) {
			case LEFT: return MainApp.LEFT_INDEX;
			case FORWARD: return MainApp.FORWARD_INDEX;
			case RIGHT: return MainApp.RIGHT_INDEX;
			default: return MainApp.LEFT_INDEX;
		}
	}
	
	public static int get_item_value(BoardItem item) {
		switch(item) {
		case FOOD:
			return 1;
		case POISON:
			return -4;
		case EMPTY:
			return 0;
		case WALL:
			return -100;
		default:
			return 0;
		}
	}
}
