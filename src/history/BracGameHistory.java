package history;

public class BracGameHistory extends GameHistory {

	public long timestamp;
	public float brac;
	
	public BracGameHistory(int level,long timestamp, float brac) {
		super(level);
		this.timestamp = timestamp;
		this.brac = brac;
	}

}
