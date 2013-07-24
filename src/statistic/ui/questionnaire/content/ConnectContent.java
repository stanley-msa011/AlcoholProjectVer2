package statistic.ui.questionnaire.content;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import statistic.ui.QuestionMsgBox;
import statistic.ui.questionnaire.listener.CallCheckOnClickListener;
import statistic.ui.questionnaire.listener.SelectedListener;
import ubicomp.drunk_detection.activities.R;

public class ConnectContent extends QuestionnaireContent {

	String[] dummyNames = {
			"dummy1",
			"dummy2",
			"dummy3"
			};
	String[] dummyPhones = {
			"0212345678",
			"0312345678",
			"0412345678"
			};
	
	private int type;
	public static final int TYPE_FAMILY = 2, TYPE_SOCIAL = 3;
	
	public ConnectContent(QuestionMsgBox msgBox,int type) {
		super(msgBox);
		this.type = type;
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.add(","+type+",5");
		setHelp(R.string.call_to);
		if (type == TYPE_FAMILY){
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(msgBox.getContext());
			String connectN0,connectN1,connectN2;
			String connectP0,connectP1,connectP2;
			connectN0 = sp.getString("connect_n0", "");
			connectN1 = sp.getString("connect_n1", "");
			connectN2 = sp.getString("connect_n2", "");
			connectP0 = sp.getString("connect_p0", "");
			connectP1 = sp.getString("connect_p1", "");
			connectP2 = sp.getString("connect_p2", "");
			
			setSelectItem(connectN0,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,connectN0,connectP0),R.string.next));
			setSelectItem(connectN1,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,connectN1,connectP1),R.string.next));
			setSelectItem(connectN2,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,connectN2,connectP2),R.string.next));
		}else if(type == TYPE_SOCIAL){
			SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(msgBox.getContext());
			int connectS0,connectS1,connectS2;
			connectS0 = sp.getInt("connect_s0", 0);
			connectS1 = sp.getInt("connect_s1", 1);
			connectS2 = sp.getInt("connect_s2", 2);
			String n0 = ConnectSocialInfo.NAME[connectS0];
			String n1 = ConnectSocialInfo.NAME[connectS1];
			String n2 = ConnectSocialInfo.NAME[connectS2];
			String p0 = ConnectSocialInfo.PHONE[connectS0];
			String p1 = ConnectSocialInfo.PHONE[connectS1];
			String p2 = ConnectSocialInfo.PHONE[connectS2];
			setSelectItem(n0,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,n0,p0),R.string.next));
			setSelectItem(n1,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,n1,p1),R.string.next));
			setSelectItem(n2,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,n2,p2),R.string.next));
		}else{
			for (int i=0;i<dummyNames.length;++i)
				setSelectItem(dummyNames[i]+":"+dummyPhones[i],new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,dummyNames[i],dummyPhones[i]),R.string.next));
		}
		msgBox.showQuestionnaireLayout(true);
	}

	@Override
	public void onPop() {
		contentSeq.remove(contentSeq.size()-1);
		seq.remove(seq.size()-1);
	}

}
