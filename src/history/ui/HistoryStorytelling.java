package history.ui;

import data.history.AccumulatedHistoryState;
import ubicomp.drunk_detection.activities.R;

public class HistoryStorytelling {

	public static final int[][] PAGE= 
		{
			{	R.drawable.history_page001,R.drawable.history_page002,R.drawable.history_page003,
				R.drawable.history_page004,R.drawable.history_page005,R.drawable.history_page006,
				R.drawable.history_page007,R.drawable.history_page008,R.drawable.history_page009,
				R.drawable.history_page010,R.drawable.history_page011,R.drawable.history_page012,
				R.drawable.history_page013,R.drawable.history_page014,R.drawable.history_page015,
				}, // 1
			{	R.drawable.history_page016,R.drawable.history_page017,R.drawable.history_page018,
					R.drawable.history_page019,R.drawable.history_page020,R.drawable.history_page021,
					R.drawable.history_page022,R.drawable.history_page023,R.drawable.history_page024,
					R.drawable.history_page025,R.drawable.history_page026,R.drawable.history_page027,
					R.drawable.history_page028,R.drawable.history_page029,R.drawable.history_page030,
				}, // 2
			{	R.drawable.history_page031,R.drawable.history_page032,R.drawable.history_page033,
					R.drawable.history_page034,R.drawable.history_page035,R.drawable.history_page036,
					R.drawable.history_page037,R.drawable.history_page038,R.drawable.history_page039,
					R.drawable.history_page040,R.drawable.history_page041,R.drawable.history_page042,
					R.drawable.history_page043,R.drawable.history_page044,R.drawable.history_page045,
				}, // 3			
			{	R.drawable.history_page046,R.drawable.history_page047,R.drawable.history_page048,
					R.drawable.history_page049,R.drawable.history_page050,R.drawable.history_page051,
					R.drawable.history_page052,R.drawable.history_page053,R.drawable.history_page054,
					R.drawable.history_page055,R.drawable.history_page056,R.drawable.history_page057,
					R.drawable.history_page058,R.drawable.history_page059,R.drawable.history_page060,
				}, // 4
			{	R.drawable.history_page061,R.drawable.history_page062,R.drawable.history_page063,
					R.drawable.history_page064,R.drawable.history_page065,R.drawable.history_page066,
					R.drawable.history_page067,R.drawable.history_page068,R.drawable.history_page069,
					R.drawable.history_page070,R.drawable.history_page071,R.drawable.history_page072,
					R.drawable.history_page073,R.drawable.history_page074,R.drawable.history_page075,
				}, // 5
			{	R.drawable.history_page076,R.drawable.history_page077,R.drawable.history_page078,
					R.drawable.history_page079,R.drawable.history_page080,R.drawable.history_page081,
					R.drawable.history_page082,R.drawable.history_page083,R.drawable.history_page084,
					R.drawable.history_page085,R.drawable.history_page086,R.drawable.history_page087,
					R.drawable.history_page088,R.drawable.history_page089,R.drawable.history_page090,
				}, // 6
			{	R.drawable.history_page091,R.drawable.history_page092,R.drawable.history_page093,
					R.drawable.history_page094,R.drawable.history_page095,R.drawable.history_page096,
					R.drawable.history_page097,R.drawable.history_page098,R.drawable.history_page099,
					R.drawable.history_page100,R.drawable.history_page101,R.drawable.history_page102,
					R.drawable.history_page103,R.drawable.history_page104,R.drawable.history_page105,
				}, // 7			
			{	R.drawable.history_page106,R.drawable.history_page106,R.drawable.history_page106,
					R.drawable.history_page106,R.drawable.history_page106,R.drawable.history_page106,
					R.drawable.history_page106,R.drawable.history_page106,R.drawable.history_page106,
					R.drawable.history_page106,R.drawable.history_page106,R.drawable.history_page106,
					R.drawable.history_page106,R.drawable.history_page106,R.drawable.history_page106,
				}, // 8
			{	R.drawable.history_page121,R.drawable.history_page121,R.drawable.history_page121,
					R.drawable.history_page121,R.drawable.history_page121,R.drawable.history_page121,
					R.drawable.history_page121,R.drawable.history_page121,R.drawable.history_page121,
					R.drawable.history_page121,R.drawable.history_page121,R.drawable.history_page121,
					R.drawable.history_page121,R.drawable.history_page121,R.drawable.history_page121,
				}, // 9
			{	R.drawable.history_page136,R.drawable.history_page136,R.drawable.history_page136,
					R.drawable.history_page136,R.drawable.history_page136,R.drawable.history_page136,
					R.drawable.history_page136,R.drawable.history_page136,R.drawable.history_page136,
					R.drawable.history_page136,R.drawable.history_page136,R.drawable.history_page136,
					R.drawable.history_page136,R.drawable.history_page136,R.drawable.history_page136,
				}, // A
			{	R.drawable.history_page151,R.drawable.history_page151,R.drawable.history_page151,
					R.drawable.history_page151,R.drawable.history_page151,R.drawable.history_page151,
					R.drawable.history_page151,R.drawable.history_page151,R.drawable.history_page151,
					R.drawable.history_page151,R.drawable.history_page151,R.drawable.history_page151,
					R.drawable.history_page151,R.drawable.history_page151,R.drawable.history_page151,
				}, // B			
			{	R.drawable.history_page166,R.drawable.history_page166,R.drawable.history_page166,
					R.drawable.history_page166,R.drawable.history_page166,R.drawable.history_page166,
					R.drawable.history_page166,R.drawable.history_page166,R.drawable.history_page166,
					R.drawable.history_page166,R.drawable.history_page166,R.drawable.history_page166,
					R.drawable.history_page166,R.drawable.history_page166,R.drawable.history_page166,
				}, // C
		};
	
	
	public static final int MAX_PAGE = 11;
	public static final int MAX_SCORE = 14;
	
	public static final int[] PAGE_FULL= 
		{
		R.drawable.history_page001, // 1
		R.drawable.history_page016, // 2
		R.drawable.history_page031, // 3
		R.drawable.history_page046, // 4
		R.drawable.history_page061, // 5
		R.drawable.history_page076, // 6
		R.drawable.history_page091, // 7
		R.drawable.history_page106, // 8
		R.drawable.history_page121, // 9
		R.drawable.history_page136, // 10
		R.drawable.history_page151, // 11
		R.drawable.history_page166  // 12
		 };
	
	public static int getPage(int score, int week){
		if (week < 0)
			week = 0;
		else if (week > MAX_PAGE)
			week = MAX_PAGE;
		if (score <0)
			score = 0;
		else if (score > MAX_SCORE)
			score = MAX_SCORE;
		return PAGE[week][score];
	}

/*
	public static boolean isChangePage(AccumulatedHistoryState prev,AccumulatedHistoryState cur){
		if (prev == null || cur == null)
			return false;
		return prev.week < cur.week;
	}
	*/
	
	public static int[] getAnimationBgs(AccumulatedHistoryState[] states){
		if (states == null)
			return null;
		int [] pages = new int[states.length];
		for (int i=0;i<pages.length;++i)
			pages[i] = getPage(states[i].getScore(),states[i].week);
		
		return pages;
	}
	
}
