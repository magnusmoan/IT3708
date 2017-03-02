package project1;

import java.util.ArrayList;

import project1.model.Board;
import project1.model.BoardItem;
import project1.model.MoveDirection;

public class BaselineAgent implements Agent{
	
	
	public BaselineAgent() {
	}
	
	public MoveDirection choose_direction(Board board) {
		MoveDirection[] current_sensors = board.get_sensors();
		
		ArrayList<MoveDirection> poison_list = new ArrayList<MoveDirection>();
		ArrayList<MoveDirection> empty_list = new ArrayList<MoveDirection>();
		
		for(MoveDirection direction : current_sensors) {
			if(!board.is_wall(direction)) {
				BoardItem item = board.get_item_from_direction(direction);
				if(item == BoardItem.FOOD) {
					return direction;
				} else if(item == BoardItem.EMPTY) {
					empty_list.add(direction);
				} else {
					poison_list.add(direction);
				}
			} else {
			}
		}
		
		
		if(empty_list.size() > 0) {
			return empty_list.get(0);
		} 
		return poison_list.get(0);
	}
}
