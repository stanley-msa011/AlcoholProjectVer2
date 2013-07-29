package data.info;

public class AudioInfo {
	
	public int year,month,date;
	public long ts;
	public String filename;
	public AudioInfo(int year, int month, int date, long ts, String filename){
		this.year = year;
		this.month = month;
		this.date = date;
		this.ts = ts;
		this.filename = filename;
	}
	
	@Override
	public String toString(){
		return ts+" "+filename;
	}
}
