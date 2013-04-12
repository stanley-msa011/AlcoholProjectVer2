package history;

public class GameHistory {

	public int level;
	public static final int MIN_LEVEL = 0;
	public static final int MAX_LEVEL = 16;
	
	public GameHistory(int level){
		if (level > MAX_LEVEL)
			level = MAX_LEVEL;
		if (level < MIN_LEVEL)
			level = MIN_LEVEL;
		
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
