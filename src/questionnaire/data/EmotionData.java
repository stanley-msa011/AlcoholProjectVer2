package questionnaire.data;

public class EmotionData {

	public long ts;
	public int selection;
	public String call;
	public EmotionData (long ts, int selection){
		this.ts = ts;
		this.selection = selection;
		this.call = null;
	}
	public EmotionData(long ts, int selection, String call){
		this.ts = ts;
		this.selection = selection;
		this.call = call;
	}
}
