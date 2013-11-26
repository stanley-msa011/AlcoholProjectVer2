package data.info;


public class AccumulatedHistoryState extends HistoryState  implements SelfHelpCounter {

	private int[] empty_array = {0,0,0};
	
	public int[] acc_test_week = {0,0,0};
	public int[] acc_pass_week = {0,0,0};
	public int[] acc_test_total = {0,0,0};
	public int[] acc_pass_total = {0,0,0};
	
	private final static int MAX_SCORE = 15;
	
	public AccumulatedHistoryState(int week,int[] acc_test_week,int[] acc_pass_week, int[] acc_test_total, int[] acc_pass_total) {
		super(week);
		if (acc_test_week != null && acc_test_week.length == 3)
			this.acc_test_week = acc_test_week.clone();
		if (acc_pass_week != null && acc_pass_week.length == 3)
			this.acc_pass_week = acc_pass_week.clone();
		if (acc_test_total != null && acc_test_total.length == 3)
			this.acc_test_total = acc_test_total.clone();
		if (acc_pass_total != null && acc_pass_total.length == 3)
			this.acc_pass_total = acc_pass_total.clone();
	}

	public void changeAcc(boolean pass, int week, int timeblock){
		if (this.week != week){
			acc_test_week = empty_array.clone();
			acc_pass_week = empty_array.clone();
		}
		this.week = week;
		++acc_test_week[timeblock];
		++acc_test_total[timeblock];
		if (pass){
			++acc_pass_week[timeblock];
			++acc_pass_total[timeblock];
		}
	}
	
	public int getScore(){
		int score = (acc_test_week[0] + acc_test_week[2] + acc_pass_week[0] + acc_pass_week[2]) /2;
		return score;
	}
	
	public int getSelfHelpCounter(){
		int score = (acc_test_total[0]+acc_test_total[1]+acc_test_total[2]) + (acc_pass_total[0]+acc_pass_total[1]+acc_pass_total[2]);
		return score;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<acc_test_week.length;++i){
			sb.append(acc_test_week[i]);
			sb.append("\t");
		}
		for (int i=0;i<acc_pass_week.length;++i){
			sb.append(acc_pass_week[i]);
			sb.append("\t");
		}
		for (int i=0;i<acc_test_total.length;++i){
			sb.append(acc_test_total[i]);
			sb.append("\t");
		}
		for (int i=0;i<acc_pass_total.length;++i){
			sb.append(acc_pass_total[i]);
			sb.append("\t");
		}
		return sb.toString();
	}
	
	public float getProgress(){
		float progress =  (float)getScore()*100F/MAX_SCORE;
		if (progress > 100.f)
			return 100.f;
		return progress;
	}
}
