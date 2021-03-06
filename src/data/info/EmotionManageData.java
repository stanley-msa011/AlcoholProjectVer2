package data.info;

public class EmotionManageData  implements SelfHelpCounter{
	public long ts;
	public int emotion;
	public int type;
	public String reason;
	public int[] acc = {0,0,0};
	public int[] used = {0,0,0};
	public EmotionManageData(long ts,int emotion, int type, String reason, int[] acc, int[] used){
		this.ts = ts;
		this.emotion = emotion;
		this.type = type;
		this.reason = reason;
		if (acc!=null && acc.length == 3)
			this.acc = acc.clone();
		if (used !=null && used.length == 3)
			this.used = used.clone();
	}
	
	@Override
	public String toString(){
		String str= ts+"@"+emotion+"|"+type+"="+reason+" in ";
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		sb.append('(');
		for (int i=0;i<acc.length;++i){
			sb.append(acc[i]);
			sb.append(',');
		}
		sb.append(") (");
		for (int i=0;i<used.length;++i){
			sb.append(used[i]);
			sb.append(',');
		}
		sb.append(')');
		return sb.toString();
	}
	
	public int getSelfHelpCounter(){
		int score = (acc[0]+acc[1]+acc[2]) - (used[0] +used[1]+used[2]);
		return score;
	}
}
