package history.ui;

import android.graphics.Point;
import history.GameHistory;
import ubicomp.drunk_detection.activities.R;

public class HistoryStorytelling {

	public static final int[] PAGE= 
		{
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03,
		R.drawable.history_page01,R.drawable.history_page02,R.drawable.history_page03
		};
	
	public static int[] PAGE_FULL_NUM = {
		2,5,8,11,14,17,20,23,26,29
	};
	
	public static int[] PAGE_START_NUM = {
		0,3,6,9,12,15,18,21,24,27
	};
	
	public static final int MAX_PAGE = 10;
	
	public static final int MIN_LEVEL = GameHistory.MIN_LEVEL;
	public static final int MAX_LEVEL = GameHistory.MAX_LEVEL;
	
	public static final int[] PAGE_FULL= 
		{
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03,
		R.drawable.history_page03
		 };
	
	public static int getPage(int level){
		if (level <0 || level >=PAGE.length)
			return 0;
		else
			return PAGE[level];
	}

	public static boolean isChangePage(int level){
		for (int i=0;i<PAGE_START_NUM.length;++i){
			if (level == PAGE_START_NUM[i])
				return true;
		}
		return false;
	}
	
	
	public static int getPageNum(int level){
		for (int i=0;i<PAGE_FULL_NUM.length;++i){
			if (level <= PAGE_FULL_NUM[i])
				return (i+1);
		}
		return PAGE_FULL_NUM.length;
	}
	
	public static int[] getAnimationBgs(int level){
		int[] pages = PAGE_FULL.clone();
		int pageNum = getPageNum(level)-1;
		pages[pageNum] = getPage(level);
		return pages;
	}
	
	public static int getNextPageLevel(int level){
		for (int i=0; i<PAGE_FULL_NUM.length; ++i){
			if (level < PAGE_FULL_NUM[i])
				return PAGE_FULL_NUM[i];
		}
		return MAX_LEVEL;
	}
	
	public static int getPrevPageLevel(int level){
		for (int i=PAGE_FULL_NUM.length-1; i>=0; --i){
			if (level > PAGE_FULL_NUM[i])
				return PAGE_FULL_NUM[i];
		}
		return PAGE_FULL_NUM[0];
	}

	public static boolean isInRange(int curLevel, int targetLevel){
		int curPage = getPageNum(curLevel);
		int targetPage = getPageNum(targetLevel);
		return curPage == targetPage;
	}
	
}
