package history;

public class GameHistory {

	public int level;
	private static final int MIN_LEVEL = 0;
	private static final int MAX_LEVEL = 16;
	
	public GameHistory(int level){
		this.level = level;
	}
	
	public void changeLevel(int change){
		level += change;
		if (level>MAX_LEVEL){
			level = MAX_LEVEL;
		}
		if (level < MIN_LEVEL){
			level = MIN_LEVEL;
		}
	}
}
