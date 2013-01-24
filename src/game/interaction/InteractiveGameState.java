package game.interaction;

import game.GameState;

public class InteractiveGameState extends GameState {

	public String PID;
	public String name;
	public InteractiveGameState(int stage, int coin, String PID,String name) {
		super(stage, coin);
		this.PID = PID;
		if (name == null)
			name = "no name";
		this.name = name;
	}
	public String toString(){
		return "<"+PID+" "+name+">";
	}
}
