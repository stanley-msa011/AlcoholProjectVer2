package data.info;

public class UsedDetection  implements SelfHelpCounter{
	public int[] test = {0,0,0};
	public int[] pass = {0,0,0};
	
	public UsedDetection(int[] test, int[] pass){
		if (test != null && test.length == 3)
			this.test = test.clone();
		if (pass != null && pass.length == 3)
			this.pass = pass.clone();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<test.length;++i){
			sb.append(test[i]);
			sb.append("\t");
		}
		for (int i=0;i<pass.length;++i){
			sb.append(pass[i]);
			sb.append("\t");
		}
		return sb.toString();
	}
	
	public int getSelfHelpCounter(){
		int score = test[0] + test[1] + test[2] +pass[0] + pass[1] + pass[2]; 
		return score;
	}
}
