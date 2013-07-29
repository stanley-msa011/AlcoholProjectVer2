package data.info;

public class EmotionData {

	public long ts;
	public int selection;
	public String call;
	public int[] acc = {0,0,0};
	public int[] used = {0,0,0};
	public EmotionData(long ts,int selection, String call, int[] acc, int[] used){
		this.ts = ts;
		this.selection = selection;
		this.call = call;
		if (acc !=null && acc.length == 3)
			this.acc = acc.clone();
		if (used !=null && used.length == 3)
			this.used = used.clone();
	}
	
	@Override
	public String toString(){
		String str= ts+"@"+selection+"/"+call+" in ";
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
