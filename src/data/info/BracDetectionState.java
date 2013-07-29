package data.info;

public class BracDetectionState extends HistoryState {

	public long timestamp;
	public float brac;
	public int emotion, desire;
	
	static final public int MAX_EMOTION = 5, MAX_DESIRE = 10;
	static final public int MIN_EMOTION = -1, MIN_DESIRE = -1;
	
	public BracDetectionState(int week, long timestamp, float brac, int emotion, int desire) {
		super(week);
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
