package ubicomp.drunk_detection.check;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LockCheck {
	public static boolean check(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		if (sp.getBoolean("system_lock", false)){
			Calendar c_1 = Calendar.getInstance();
			Calendar c = Calendar.getInstance();
			int lYear = sp.getInt("lockYear", c.get(Calendar.YEAR));
			int lMonth = sp.getInt("lockMonth", c.get(Calendar.MONTH));
			int lDay = sp.getInt("lockDate", c.get(Calendar.DATE));
			c_1.set(lYear, lMonth, lDay, 0, 0, 0);
			c_1.set(Calendar.MILLISECOND, 0);
			if (c_1.before(c))
				return true;
		}
			return false;
	}
	
}
