package data.info;

public class AccAudioData extends AudioData implements SelfHelpCounter{

	public int[] acc = {0,0,0};
	public int[] used ={0,0,0}; 
	
	public AccAudioData(int year, int month, int date, long ts, String filename, int[] acc, int[] used) {
		super(year, month, date, ts, filename);
		if (acc!=null && acc.length == 3)
			this.acc = acc.clone();
		if (used != null && used.length == 3)
			this.used = used.clone();
	}

	
	@Override
	public String toString(){
		return year+"/"+month+"/"+date+"-"+ts+"("+acc[0]+","+acc[1]+","+acc[2]+","+used[0]+","+used[1]+","+used[2]+")";
	}
	
	public int getSelfHelpCounter(){
		return acc[0]+acc[1]+acc[2]-used[0]-used[1]-used[2];
	}
}
