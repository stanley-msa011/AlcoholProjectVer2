package data.history;


public class AccumulatedHistoryState extends HistoryState {

	public int[] acc_test = {0,0,0};
	public int[] acc_pass = {0,0,0};
	public int[] total_acc_test = {0,0,0};
	public int[] total_acc_pass = {0,0,0};
	
	public int MAX_SCORE = 15;
	
	public AccumulatedHistoryState(int week,int[] acc_test,int[] acc_pass, int[] t_acc_test, int[] t_acc_pass) {
		super(week);
		if (acc_test != null && acc_test.length == 3)
			this.acc_test = acc_test.clone();
		if (acc_pass != null && acc_pass.length == 3)
			this.acc_pass = acc_pass.clone();
		if (t_acc_test != null && t_acc_test.length == 3)
			this.total_acc_test = t_acc_test.clone();
		if (t_acc_pass != null && t_acc_pass.length == 3)
			this.total_acc_pass = t_acc_pass.clone();
	}

	public void changeAcc(boolean pass, int week, int timeblock){
		if (this.week != week){
			acc_test = new int[3];
			acc_pass = new int[3];
		}
		this.week = week;
		acc_test[timeblock]+=1;
		total_acc_test[timeblock] +=1;
		if (pass){
			acc_pass[timeblock] += 1;
			total_acc_pass[timeblock] +=1;
		}
	}
	
	public int getScore(){
		int score = (acc_test[0] + acc_test[2] + acc_pass[0] + acc_pass[2]) /2;
		return score;
	}
	
	public int getSelfHelpCounter(){
		int score = (total_acc_test[0]+total_acc_test[1]+total_acc_test[2]) + (total_acc_pass[0]+total_acc_pass[1]+total_acc_pass[2]);
		return score;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<acc_test.length;++i){
			sb.append(acc_test[i]);
			sb.append("\t");
		}
		for (int i=0;i<acc_pass.length;++i){
			sb.append(acc_pass[i]);
			sb.append("\t");
		}
		for (int i=0;i<total_acc_test.length;++i){
			sb.append(total_acc_test[i]);
			sb.append("\t");
		}
		for (int i=0;i<total_acc_pass.length;++i){
			sb.append(total_acc_pass[i]);
			sb.append("\t");
		}
		return sb.toString();
	}
}
