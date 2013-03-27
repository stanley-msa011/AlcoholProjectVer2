package statisticPageView.analysis;

import ioio.examples.hello.R;
import android.content.Context;
import android.os.Build;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import statisticPageView.StatisticPageView;

public class AnalysisDrunkView extends StatisticPageView {

	private ImageView[] levelImageViews;
	
	
	public AnalysisDrunkView(Context context){
		super(context, R.layout.analysis_drunk_view);
		setting();
	}
	
	private void setting(){
		levelImageViews = new ImageView[5];
		
		levelImageViews[0] = (ImageView) view.findViewById(R.id.analysis_drunk_level_1);
		levelImageViews[1] = (ImageView) view.findViewById(R.id.analysis_drunk_level_2);
		levelImageViews[2] = (ImageView) view.findViewById(R.id.analysis_drunk_level_3);
		levelImageViews[3] = (ImageView) view.findViewById(R.id.analysis_drunk_level_4);
		levelImageViews[4] = (ImageView) view.findViewById(R.id.analysis_drunk_level_5);
		
		//dummy setting
		if (Build.VERSION.SDK_INT<11){
			AlphaAnimation alpha_1 = new AlphaAnimation(1F, 1F);
			AlphaAnimation alpha_2 = new AlphaAnimation(0.2F, 0.2F);
			alpha_1.setDuration(0); 
			alpha_2.setDuration(0); 
			alpha_1.setFillAfter(true);
			alpha_2.setFillAfter(true);
			
			levelImageViews[0].setAnimation(alpha_1);
			levelImageViews[1].setAnimation(alpha_1);
			levelImageViews[2].setAnimation(alpha_1);
			levelImageViews[3].setAnimation(alpha_2);
			levelImageViews[4].setAnimation(alpha_2);
		}
		else{
			levelImageViews[0].setAlpha(1.f);
			levelImageViews[1].setAlpha(1.f);
			levelImageViews[2].setAlpha(1.f);
			levelImageViews[3].setAlpha(0.2f);
			levelImageViews[4].setAlpha(0.2f);
		}
	}
}
