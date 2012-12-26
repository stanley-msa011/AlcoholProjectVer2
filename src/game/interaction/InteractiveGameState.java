package game.interaction;

import game.GameState;

public class InteractiveGameState extends GameState {

	public String PID;
	public String name;
	public InteractiveGameState(int state, int coin, String PID,String name) {
		super(state, coin);
		this.PID = PID;
		this.name = name;
	}
	public String toString(){
		return "<"+PID+" "+name+">";
	}
}
