package history;

public class BracGameHistory extends GameHistory {

	public long timestamp;
	public float brac;
	public int emotion, desire;
	
	static final private int MAX_EMOTION = 5, MAX_DESIRE = 10;
	static final private int MIN_EMOTION = 0, MIN_DESIRE = 0;
	
	public BracGameHistory(int level,long timestamp, float brac, int emotion, int desire) {
		super(level);
		this.timestamp = timestamp;
		this.brac = brac;
    	if (emotion > MAX_EMOTION)
    		emotion = MAX_EMOTION;
    	else if (emotion < MIN_EMOTION)
    		emotion = MIN_EMOTION;
    	this.emotion = emotion;
    	
    	if (desire > MAX_DESIRE)
    		desire = MAX_DESIRE;
    	else if (desire < MIN_DESIRE)
    		desire = MIN_DESIRE;
    	this.desire = desire;
		
	}

}
