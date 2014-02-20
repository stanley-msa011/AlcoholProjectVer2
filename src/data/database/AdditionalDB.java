package data.database;

import java.util.Calendar;

import data.info.FacebookInfo;
import data.info.GCMInfo;
import data.info.StorytellingFling;

import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdditionalDB {

	private SQLiteOpenHelper dbHelper = null;
	private SQLiteDatabase db = null;

	private static final long TIME_LIMIT = AlarmManager.INTERVAL_DAY;

	public AdditionalDB(Context context) {
		dbHelper = new DBHelper(context);
	}

	public boolean insertStorytellingFling(int page) {
		boolean isAcc = false;
		Calendar cal = Calendar.getInstance();
		long ts = cal.getTimeInMillis();

		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		sql = "SELECT * FROM StorytellingFling WHERE isClear = 0 ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);

		long temp_acc = 0;
		if (cursor.moveToFirst())
			temp_acc = cursor.getInt(2);
		cursor.close();

		sql = "SELECT * FROM StorytellingFling WHERE isClear = 0 AND acc=" + temp_acc + " ORDER BY id ASC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		long prev_ts = 0;
		if (cursor.moveToFirst())
			prev_ts = cursor.getLong(1);
		cursor.close();

		sql = "SELECT * FROM StorytellingFling ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		int acc = 0, used = 0;
		if (cursor.moveToFirst()) {
			acc = cursor.getInt(2);
			used = cursor.getInt(3);
		}
		cursor.close();

		if (ts - prev_ts > TIME_LIMIT) {
			++acc;
			isAcc = true;
		}

		sql = "INSERT INTO StorytellingFling (ts,acc,used,page) VALUES (" + ts + "," + acc + "," + used + "," + page
				+ ")";
		db.execSQL(sql);
		db.close();

		return isAcc;
	}

	public StorytellingFling getLatestStorytellingFling() {
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		sql = "SELECT * FROM StorytellingFling ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		long ts = 0;
		int acc = 0, used = 0, isClear = 0, page = -1;
		if (cursor.moveToFirst()) {
			ts = cursor.getLong(1);
			acc = cursor.getInt(2);
			used = cursor.getInt(3);
			isClear = cursor.getInt(4);
			page = cursor.getInt(5);
		}
		cursor.close();
		return new StorytellingFling(ts, acc, used, isClear, page);
	}

	public StorytellingFling[] getNotUploadedStorytellingFling() {
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();
		sql = "SELECT * FROM StorytellingFling WHERE upload=0";
		cursor = db.rawQuery(sql, null);
		int len = cursor.getCount();
		if (len == 0) {
			cursor.close();
			db.close();
			return null;
		}

		StorytellingFling[] flings = new StorytellingFling[len];
		long ts = 0;
		int acc = 0, used = 0, isClear = 0, page = -1;
		for (int i = 0; i < len; ++i) {
			cursor.moveToPosition(i);
			ts = cursor.getLong(1);
			acc = cursor.getInt(2);
			used = cursor.getInt(3);
			isClear = cursor.getInt(4);
			page = cursor.getInt(5);
			flings[i] = new StorytellingFling(ts, acc, used, isClear, page);
		}
		cursor.close();
		db.close();
		return flings;
	}

	public void setStorytellingFlingUploaded(long ts) {
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE StorytellingFling SET upload = 1 WHERE ts = " + ts;
		db.execSQL(sql);
		db.close();
	}

	public void restoreData(StorytellingFling sf) {
		db = dbHelper.getWritableDatabase();
		String sql;

		if (sf != null) {
			sql = "INSERT INTO StorytellingFling (ts,acc,used,isClear,page,upload) VALUES (" + sf.ts + "," + sf.acc
					+ "," + sf.used + "," + sf.isClear + "," + sf.page + "," + 1 + ")";
			db.execSQL(sql);
		}
	}

	public void cleanAcc(long ts) {
		String sql;
		Cursor cursor;
		db = dbHelper.getWritableDatabase();

		sql = "SELECT * FROM StorytellingFling ORDER BY id DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		int acc = 0;
		if (cursor.moveToFirst()) {
			acc = cursor.getInt(2);
		}
		cursor.close();

		sql = "INSERT INTO StorytellingFling (ts,acc,used,page,isClear) VALUES (" + ts + "," + acc + "," + acc + ","
				+ (-1) + "," + 1 + ")";
		db.execSQL(sql);
		db.close();
	}

	public void insertGCM(String message) {
		String sql;
		db = dbHelper.getWritableDatabase();
		long ts = System.currentTimeMillis();
		sql = "INSERT INTO GCMRead (ts,message) VALUES (" + ts + ",'" + message + "')";
		db.execSQL(sql);
		db.close();
	}

	public GCMInfo[] getNotUploadedGCM() {
		String sql;
		Cursor cursor;
		db = dbHelper.getReadableDatabase();
		sql = "SELECT * FROM GCMRead WHERE upload=0";
		cursor = db.rawQuery(sql, null);
		int len = cursor.getCount();
		if (len == 0) {
			cursor.close();
			db.close();
			return null;
		}

		GCMInfo[] ginfo = new GCMInfo[len];
		for (int i = 0; i < len; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(1);
			String msg = cursor.getString(2);
			ginfo[i] = new GCMInfo(ts, msg);
		}
		cursor.close();
		db.close();
		return ginfo;
	}

	public void setGCMUploaded(long ts) {
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE GCMRead SET upload = 1 WHERE ts = " + ts;
		db.execSQL(sql);
		db.close();
	}

	public void insertFacebook(FacebookInfo info) {
		String sql;
		db = dbHelper.getWritableDatabase();
		long ts = info.ts;
		int pageWeek = info.pageWeek;
		int pageLevel = info.pageLevel;
		int uploadSuccessInt = info.uploadSuccess ? 1 : 0;
		int sendGroup = info.sendGroup;
		sql = "INSERT INTO Facebook (ts,pageWeek,pageLevel,text,uploadSuccess,sendGroup) VALUES (" + ts + ","
				+ pageWeek + "," + pageLevel + ",'" + info.text + "'," + uploadSuccessInt + "," + sendGroup + ")";
		db.execSQL(sql);
		db.close();
	}

	public FacebookInfo[] getNotUploadedFacebook() {
		String sql;
		Cursor cursor;
		db = dbHelper.getReadableDatabase();
		sql = "SELECT * FROM Facebook WHERE upload=0";
		cursor = db.rawQuery(sql, null);
		int len = cursor.getCount();
		if (len == 0) {
			cursor.close();
			db.close();
			return null;
		}

		FacebookInfo[] finfo = new FacebookInfo[len];
		for (int i = 0; i < len; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(1);
			int week = cursor.getInt(2);
			int level = cursor.getInt(3);
			String text = cursor.getString(4);
			int uploadSuccess = cursor.getInt(5);
			int sendGroup = cursor.getInt(7);
			finfo[i] = new FacebookInfo(ts, week, level, text, uploadSuccess == 1, sendGroup);
		}
		cursor.close();
		db.close();
		return finfo;
	}

	public void setFacebookUploaded(long ts) {
		db = dbHelper.getWritableDatabase();
		String sql = "UPDATE Facebook SET upload = 1 WHERE ts = " + ts;
		db.execSQL(sql);
		db.close();
	}

}
