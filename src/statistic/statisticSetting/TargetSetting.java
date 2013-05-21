package statistic.statisticSetting;

public class TargetSetting {

	private static final int[] targetValue = {50000,700000,30000};
	private static final String[] targetName = {"機車","汽車","筆電"};
	
	public static int getTargetValue(int targetId){
		if (targetId-1<0||targetId-1>=targetValue.length)
			return targetId;
		return targetValue[targetId-1];
	}
	
	public static String getTargetName(int targetId){
		if (targetId-1<0||targetId-1>=targetName.length)
			return "自訂目標";
		return targetName[targetId-1];
	}
}
