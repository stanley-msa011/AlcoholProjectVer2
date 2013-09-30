package ubicomp.drunk_detection.check;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DefaultCheck {

	public static boolean check(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String uid = sp.getString("uid","sober_default_test");
		if (uid.equals("sober_default_test"))
			return true;
		return false;
	}
	
}
