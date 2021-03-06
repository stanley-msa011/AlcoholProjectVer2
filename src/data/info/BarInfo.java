package data.info;

import test.data.BracDataHandler;


public class BarInfo{
	public float emotion = 0.F,desire=0.F,brac=0.F;
	public int week;
	public boolean hasData;
	public DateValue dv;
	
	public BarInfo (float emotion, float desire, float brac, int week,boolean hasData,DateValue dv){
		if (emotion > 0.F)
			this.emotion = emotion;
		if (desire > 0)
			this.desire = desire;
		if (brac < BracDataHandler.THRESHOLD)
			this.brac = 0;
		else
			this.brac = brac;
		
		this.week = week;
		this.hasData = hasData;
		this.dv = dv;
	}
}