package history.ui;

import data.history.AccumulatedHistoryState;
import ubicomp.drunk_detection.activities.R;

public class HistoryStorytelling {

	public static final int[][] PAGE= 
		{
			{	R.drawable.history_page001,R.drawable.history_page001,R.drawable.history_page001,
				R.drawable.history_page001,R.drawable.history_page001,R.drawable.history_page001,
				R.drawable.history_page001,R.drawable.history_page001,R.drawable.history_page001,
				R.drawable.history_page001,R.drawable.history_page001,R.drawable.history_page001,
				R.drawable.history_page001,R.drawable.history_page001,R.drawable.history_page001,
				}, // 1
			{	R.drawable.history_page016,R.drawable.history_page017,R.drawable.history_page018,
					R.drawable.history_page019,R.drawable.history_page020,R.drawable.history_page021,
					R.drawable.history_page022,R.drawable.history_page023,R.drawable.history_page024,
					R.drawable.history_page025,R.drawable.history_page026,R.drawable.history_page027,
					R.drawable.history_page028,R.drawable.history_page029,R.drawable.history_page030,
				}, // 2
			{	R.drawable.history_page031,R.drawable.history_page031,R.drawable.history_page031,
					R.drawable.history_page031,R.drawable.history_page031,R.drawable.history_page031,
					R.drawable.history_page031,R.drawable.history_page031,R.drawable.history_page031,
					R.drawable.history_page031,R.drawable.history_page031,R.drawable.history_page031,
					R.drawable.history_page031,R.drawable.history_page031,R.drawable.history_page031,
				}, // 3			
			{	R.drawable.history_page046,R.drawable.history_page046,R.drawable.history_page046,
					R.drawable.history_page046,R.drawable.history_page046,R.drawable.history_page046,
					R.drawable.history_page046,R.drawable.history_page046,R.drawable.history_page046,
					R.drawable.history_page046,R.drawable.history_page046,R.drawable.history_page046,
					R.drawable.history_page046,R.drawable.history_page046,R.drawable.history_page046,
				}, // 4
			{	R.drawable.history_page061,R.drawable.history_page061,R.drawable.history_page061,
					R.drawable.history_page061,R.drawable.history_page061,R.drawable.history_page061,
					R.drawable.history_page061,R.drawable.history_page061,R.drawable.history_page061,
					R.drawable.history_page061,R.drawable.history_page061,R.drawable.history_page061,
					R.drawable.history_page061,R.drawable.history_page061,R.drawable.history_page061,
				}, // 5
			{	R.drawable.history_page076,R.drawable.history_page076,R.drawable.history_page076,
					R.drawable.history_page076,R.drawable.history_page076,R.drawable.history_page076,
					R.drawable.history_page076,R.drawable.history_page076,R.drawable.history_page076,
					R.drawable.history_page076,R.drawable.history_page076,R.drawable.history_page076,
					R.drawable.history_page076,R.drawable.history_page076,R.drawable.history_page076,
				}, // 6
			{	R.drawable.history_page091,R.drawable.history_page091,R.drawable.history_page091,
					R.drawable.history_page091,R.drawable.history_page091,R.drawable.history_page091,
					R.drawable.history_page091,R.drawable.history_page091,R.drawable.history_page091,
					R.drawable.history_page091,R.drawable.history_page091,R.drawable.history_page091,
					R.drawable.history_page091,R.drawable.history_page091,R.drawable.history_page091,
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
