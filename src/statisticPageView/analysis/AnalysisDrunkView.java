package statisticPageView.analysis;

import ioio.examples.hello.R;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
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
		levelImageViews[0].setAlpha(1.f);
		levelImageViews[1].setAlpha(1.f);
		levelImageViews[2].setAlpha(1.f);
		levelImageViews[3].setAlpha(0.2f);
		levelImageViews[4].setAlpha(0.2f);
	}
}
