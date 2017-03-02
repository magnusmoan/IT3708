package project1;

import project1.model.Board;
import project1.model.MoveDirection;

public interface Agent {
	
	MoveDirection choose_direction(Board board);
}
