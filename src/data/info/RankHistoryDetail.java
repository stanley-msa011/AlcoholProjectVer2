package data.info;

public class RankHistoryDetail extends RankHistory {

	public int test =0 ,ques = 0,story = 0;
	
	public RankHistoryDetail(int score, String uid,int test, int ques, int story) {
		super(score, uid);
		this.test = test;
		this.ques = ques;
		this.story = story;
	}

}
