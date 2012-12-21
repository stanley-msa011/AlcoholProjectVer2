package game.interaction;

import game.GameState;

public class InteractiveGameState extends GameState {

	public String PID;
	public InteractiveGameState(int state, int coin, String PID) {
		super(state, coin);
		this.PID = PID;
	}
	public String toString(){
		return "<"+PID+">";
	}
}
