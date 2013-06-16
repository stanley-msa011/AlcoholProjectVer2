package ubicomp.drunk_detection.activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TextView;

public class AboutActivity extends Activity {

	TextView aboutText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		aboutText = (TextView) this.findViewById(R.id.about_content);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("此應用程式由\n" +
				"國立台灣大學、\n" +
				"中央研究院、\n" +
				"臺北市立聯合醫院\n" +
				"合作開發。\n" +
				"目的是為了協助使用者戒酒。\n" +
				"此應用程式需搭配專用酒測器方可使用。");
		
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			sb.append("\n目前版本：");
			sb.append(versionName);
		} catch (NameNotFoundException e) {
		}
		aboutText.setText(sb.toString());
		
	}

}
