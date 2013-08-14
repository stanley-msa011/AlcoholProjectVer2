package debug.clicklog;

public class ClickLogId {
	/*Format = [pageId][itemId][appendix][action]
	 * 						0				0	 		0000				0
	 *pageId
	 *		0	TAB
	 *		1	TUTORIAL	PAGE
	 *		2	TEST PAGE
	 *		3	STATISTIC PAGE
	 *		4	STORYTELLING	PAGE
	 *		5	EMOTION DIY PAGE
	 *		6 	MENU
	 * action
	 * 	0	click
	 * 	1	scroll
	 * 	2	touch
	 * 	3	fling
	 *		4	back_button
	 *		5	edit action
	*/
	
	public final static String LOG_MSG_ID = "CLICK_MSG";
	
	public final static long TAB_TEST 																			=	  000000;
	public final static long TAB_STATISTIC 																=	  100000;
	public final static long TAB_STORYTELLING 													=	  200000;
	
	public final static long TUTORIAL_CLICK					 										=	1000000;
	public final static long TUTORIAL_REPLAY 														=	1100000;
	public final static long TUTORIAL_END		 														=	1200000;
	
	public final static long TEST_TUTORIAL_BUTTON											=	2000000;
	public final static long TEST_START_BUTTON													=	2100000;
	public final static long TEST_RESTART_BUTTON												=	2200000;
	
	public final static long TEST_QUESTION_CANCEL											=	2300000;
	public final static long TEST_QUESTION_SEND												=	2400000;
	public final static long TEST_QUESTION_SEND_DATA									=	2400010;
	
	public final static long STATISTIC_TODAY_VIEW											=	3000001;
	public final static long STATISTIC_WEEKLY_VIEW											=	3100001;
	public final static long STATISTIC_MONTHLY_VIEW									=	3200001;
	public final static long STATISTIC_ANALYSIS_TOUCH									=	3300002;
	public final static long STATISTIC_QUESTION_BUTTON								=	3400000;
	
	public final static long STATISTIC_QUESTION_CANCEL								=	3500000;
	public final static long STATISTIC_QUESTION_SELECT								=	3600000;
	public final static long STATISTIC_QUESTION_NEXT									=	3700000;
	
	public final static long STORYTELLING_FLING_UP										=	4000003;
	public final static long STORYTELLING_FLING_DOWN								=	4000013;
	
	public final static long STORYTELLING_CHART_TYPE0								=	4100002;
	public final static long STORYTELLING_CHART_TYPE1								=	4100012;
	public final static long STORYTELLING_CHART_TYPE2								=	4100022;
	public final static long STORYTELLING_CHART_TYPE3								=	4100032;
	
	public final static long STORYTELLING_CHART_TOUCH							=	4200000;
	public final static long STORYTELLING_CHART_BUTTON							=	4300000;
	//[appendix] = [month][date]
	
	public final static long STORYTELLING_RECORD_CANCEL						=	4400000;
	public final static long STORYTELLING_RECORD_RECORD						=	4500000;
	public final static long STORYTELLING_RECORD_CANCEL_RECORD	=	4500010;
	public final static long STORYTELLING_RECORD_PLAY								=	4600000;
	public final static long STORYTELLING_RECORD_CANCEL_PLAY			=	4600010;
	
	public final static long STORYTELLING_SHARE_OK										=	4700000;
	public final static long STORYTELLING_SHARE_CANCEL							=	4800000;
	
	public final static long EMOTIONDIY_RETURN_BUTTON							=	5000004;
	public final static long EMOTIONDIY_SELECTION										=	5100000;
	public final static long EMOTIONDIY_CALL													=	5200000;
	public final static long EMOTIONDIY_CANCEL_CALL									=	5300000;
	public final static long EMOTIONDIY_OPEN_CALL_BOX							=	5100010;
	public final static long EMOTIONDIY_OPEN_PLAY_BOX							=	5100020;
	public final static long EMOTIONDIY_PAUSE_AUDIO									=	5400000;
	public final static long EMOTIONDIY_PLAY_AUDIO									=	5400010;
	public final static long EMOTIONDIY_CANCEL_AUDIO								= 5500000;
	
	public final static long EMOTIONMANAGE_RETURN_BUTTON				=	6000004;
	public final static long EMOTIONMANAGE_SELECTION							=	6100000;
	public final static long EMOTIONMANAGE_EDIT											=	6200005;
	
	public final static long MENU_EMOTIONDIY													=	7000000;
	public final static long MENU_EMOTIONMANAGE										=	7100000;
	public final static long MENU_ABOUT																=	7200000;
}
