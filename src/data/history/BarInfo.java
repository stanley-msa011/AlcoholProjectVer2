package data.history;

import history.ui.DateValue;

public class BarInfo{
	public float emotion,desire,brac;
	public int week;
	public boolean hasData;
	public DateValue dv;
	
	public BarInfo (float emotion, float desire, float brac, int week,boolean hasData,DateValue dv){
		this.emotion = emotion;
		this.desire = desire;
		this.brac = brac;
		this.week = week;
		this.hasData = hasData;
		this.dv = dv;
	}
}