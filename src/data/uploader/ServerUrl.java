package data.uploader;

public class ServerUrl {

	private static final String SERVER_URL_TEST = "https://140.112.30.165/drunk_detection_ubicomp/drunk_detect_upload_3.php";
	private static final String DEVELOP_SERVER_URL_TEST = "https://140.112.30.165/develop/drunk_detection/drunk_detect_upload_3.php";
	
	public static String SERVER_URL_TEST(boolean develop){
		if (develop)
			return DEVELOP_SERVER_URL_TEST;
		else
			return SERVER_URL_TEST;
	} 
	
	private static final String SERVER_URL_EMOTION = "https://140.112.30.165/drunk_detection_ubicomp/emotionDIY_upload_2.php";
	private static final String DEVELOPER_SERVER_URL_EMOTION = "https://140.112.30.165/develop/drunk_detection/emotionDIY_upload_2.php";
	public static String SERVER_URL_EMOTION(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_EMOTION;
		else
			return SERVER_URL_EMOTION ;
	} 
	
	private static final String SERVER_URL_EMOTION_MANAGE = "https://140.112.30.165/drunk_detection_ubicomp/emotion_manage_upload_2.php";
	private static final String DEVELOPER_SERVER_URL_EMOTION_MANAGE = "https://140.112.30.165/develop/drunk_detection/emotion_manage_upload_2.php";
	public static String SERVER_URL_EMOTION_MANAGE(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_EMOTION_MANAGE;
		else
			return SERVER_URL_EMOTION_MANAGE ;
	} 
	
	private static final String SERVER_URL_QUESTIONNAIRE = "https://140.112.30.165/drunk_detection_ubicomp/questionnaire_upload_2.php";
	private static final String DEVELOPER_SERVER_URL_QUESTIONNAIRE = "https://140.112.30.165/develop/drunk_detection/questionnaire_upload_2.php";
	public static String SERVER_URL_QUESTIONNAIRE (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_QUESTIONNAIRE;
		else
			return SERVER_URL_QUESTIONNAIRE ;
	} 
	
	private static final String SERVER_URL_AUDIO = "https://140.112.30.165/drunk_detection_ubicomp/audio_upload_3.php";
	private static final String DEVELOPER_SERVER_URL_AUDIO = "https://140.112.30.165/develop/drunk_detection/audio_upload_3.php";
	public static String SERVER_URL_AUDIO (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_AUDIO;
		else
			return SERVER_URL_AUDIO ;
	} 
	
	private static final String SERVER_URL_RANK = "https://140.112.30.165/drunk_detection_ubicomp/userStates3.php";
	private static final String DEVELOPER_SERVER_URL_RANK = "https://140.112.30.165/develop/drunk_detection/userStates3.php";
	public static String SERVER_URL_RANK (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_RANK;
		else
			return SERVER_URL_RANK ;
	} 
	
	private static final String SERVER_URL_RANK_TODAY = "https://140.112.30.165/drunk_detection_ubicomp/userStatesAverage.php";
	private static final String DEVELOPER_SERVER_URL_RANK_TODAY = "https://140.112.30.165/develop/drunk_detection/userStatesAverage.php";
	public static String SERVER_URL_RANK_TODAY  (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_RANK_TODAY ;
		else
			return SERVER_URL_RANK_TODAY  ;
	} 
	private static final String SERVER_URL_CLICKLOG = "https://140.112.30.165/drunk_detection_ubicomp/clicklog_upload.php";
	private static final String DEVELOPER_SERVER_URL_CLICKLOG = "https://140.112.30.165/develop/drunk_detection/clicklog_upload.php";
	public static String SERVER_URL_CLICKLOG  (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_CLICKLOG ;
		else
			return SERVER_URL_CLICKLOG ;
	}
	
	private static final String SERVER_URL_USED = "https://140.112.30.165/drunk_detection_ubicomp/used_ts_upload.php";
	private static final String DEVELOPER_SERVER_URL_USED = "https://140.112.30.165/develop/drunk_detection/used_ts_upload.php";
	public static String SERVER_URL_USED (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_USED ;
		else
			return SERVER_URL_USED ;
	}

	private static final String SERVER_URL_REGULAR_CHECK = "https://140.112.30.165/drunk_detection_ubicomp/regular_check_3.php";
	private static final String DEVELOPER_SERVER_URL_REGULAR_CHECK = "https://140.112.30.165/develop/drunk_detection/regular_check_3.php";
	
	public static String SERVER_URL_REGULAR_CHECK (boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_REGULAR_CHECK;
		else
			return SERVER_URL_REGULAR_CHECK  ;
	}
	
	private static final String SERVER_URL_USAGE = "https://140.112.30.165/drunk_detection_ubicomp/storytelling_usage_3.php";
	private static final String DEVELOPER_SERVER_URL_USAGE = "https://140.112.30.165/develop/drunk_detection/storytelling_usage_3.php";
	
	public static String SERVER_URL_USAGE(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_USAGE;
		else
			return SERVER_URL_USAGE ;
	}
	
	private static final String SERVER_URL_FLING = "https://140.112.30.165/drunk_detection_ubicomp/storytelling_fling_upload.php";
	private static final String DEVELOPER_SERVER_URL_FLING = "https://140.112.30.165/develop/drunk_detection/storytelling_fling_upload.php";
	
	public static String SERVER_URL_FLING(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_FLING;
		else
			return SERVER_URL_FLING ;
	}
	
	private static final String SERVER_URL_GCM = "https://140.112.30.165/drunk_detection_ubicomp/GCM/register.php";
	private static final String DEVELOPER_SERVER_URL_GCM = "https://140.112.30.165/develop/drunk_detection/GCM/register.php";
	
	public static String SERVER_URL_GCM(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_GCM;
		else
			return SERVER_URL_GCM;
	}
	
	private static final String SERVER_URL_GCM_READ = "https://140.112.30.165/drunk_detection_ubicomp/GCM/read.php";
	private static final String DEVELOPER_SERVER_URL_GCM_READ = "https://140.112.30.165/develop/drunk_detection/GCM/read.php";
	
	public static String SERVER_URL_GCM_READ(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_GCM_READ;
		else
			return SERVER_URL_GCM_READ;
	}
	
	private static final String SERVER_URL_FACEBOOK = "https://140.112.30.165/drunk_detection_ubicomp/facebook.php";
	private static final String DEVELOPER_SERVER_URL_FACEBOOK  = "https://140.112.30.165/develop/drunk_detection/facebook.php";
	
	public static String SERVER_URL_FACEBOOK(boolean develop){
		if (develop)
			return DEVELOPER_SERVER_URL_FACEBOOK;
		else
			return SERVER_URL_FACEBOOK;
	}
}
