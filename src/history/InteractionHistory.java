package history;

public class InteractionHistory extends GameHistory {

	public String uid;
	
	public InteractionHistory(int level,String uid) {
		super(level);
		this.uid = uid;
	}

}
