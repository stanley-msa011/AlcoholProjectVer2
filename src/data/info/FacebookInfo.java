package data.info;

public class FacebookInfo {

	public long ts = 0;
	public int pageWeek = 0;
	public int pageLevel = 0;
	public String text = null;
	public boolean uploadSuccess = false;
	
	public FacebookInfo(long ts,int pageWeek, int pageLevel, String text, boolean uploadSuccess){
		this.ts = ts;
		this.pageWeek = pageWeek;
		this.pageLevel = pageLevel;
		this.text = text;
		if (this.text == null)
			this.text = "NULL";
		if (this.text.length()>=500)
			this.text = this.text.substring(0,499);
		this.uploadSuccess = uploadSuccess;
	}
	
}
