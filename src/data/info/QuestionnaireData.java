package data.info;

public class QuestionnaireData {

	public long ts;
	public int type;
	public String seq;
	public int[] acc = {0, 0, 0,
										0, 0, 0,
										0, 0, 0,
										0, 0, 0};
	public int[] used = {	0, 0, 0,
											0, 0, 0,
											0, 0, 0,
											0, 0, 0};
	public QuestionnaireData(long ts,int type,String seq,int[] acc,int[] used){
		this.ts = ts;
		this.type = type;
		this.seq = seq;
		if (acc !=null && acc.length ==12)
			this.acc = acc.clone();
		if (used !=null && used.length ==12)
			this.used = used.clone();
	}
	
	@Override
	public String toString(){
		String str= ts+"@"+type+"/"+seq+" in ";
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
		int score = 0;
		for (int i=0;i<12;++i)
			score += (acc[i] - used[i]);
		return score;
	}
}
