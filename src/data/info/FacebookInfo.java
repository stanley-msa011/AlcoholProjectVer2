package data.info;

public class FacebookInfo {

	public long ts = 0;
	public int pageWeek = 0;
	public int pageLevel = 0;
	public String text = null;
	public boolean uploadSuccess = false;
	public int sendGroup = 0;
	
	public final static int FRIEND = 0;
	public final static int SELF = 1;
	
	public FacebookInfo(long ts,int pageWeek, int pageLevel, String text, boolean uploadSuccess,int sendGroup){
		this.ts = ts;
		this.pageWeek = pageWeek;
		this.pageLevel = pageLevel;
		this.text = text;
		if (this.text == null)
			this.text = "NULL";
		if (this.text.length()>=500)
			this.text = this.text.substring(0,499);
		this.uploadSuccess = uploadSuccess;
		this.sendGroup = sendGroup;
	}
	
}
