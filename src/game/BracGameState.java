package game;

public class BracGameState extends GameState {
	
	public BracGameState(int stage, int coin, long date, float brac) {
		super(stage, coin);
		this.date = date;
		this.brac = brac;
	}
	public long date;
	public float brac;

}
