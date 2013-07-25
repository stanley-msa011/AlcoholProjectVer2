package data.history;

import history.ui.DateValue;

public class BarInfo{
	public float emotion,desire,brac;
	public int week;
	public boolean hasData;
	public DateValue dv;
	
	public BarInfo (float emotion, float desire, float brac, int week,boolean hasData,DateValue dv){
		this.emotion = emotion;
		if (this.emotion < 0)
			this.emotion = 0;
		this.desire = desire;
		if (this.desire < 0)
			this.desire = 0;
		this.brac = brac;
		if (this.brac < 0.F)
			this.brac = 0.F;
		this.week = week;
		this.hasData = hasData;
		this.dv = dv;
	}
}