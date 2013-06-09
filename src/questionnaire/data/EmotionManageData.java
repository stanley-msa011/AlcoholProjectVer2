package questionnaire.data;

public class EmotionManageData {
	public long ts;
	public int emotion;
	public int type;
	public String reason;
	
	public EmotionManageData(long ts, int emotion, int type, String reason){
		this.ts = ts;
		this.emotion = emotion;
		this.type = type;
		this.reason = reason;
	}
}
